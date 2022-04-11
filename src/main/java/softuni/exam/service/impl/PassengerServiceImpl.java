package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.Passenger;
import softuni.exam.models.dto.PassengerRootDto;
import softuni.exam.repository.PassengerRepository;
import softuni.exam.service.PassengerService;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;


@Service
public class PassengerServiceImpl implements PassengerService {

    private static final String PASSENGER_FILE_PATH = "src/main/resources/files/json/passengers.json";
    private final PassengerRepository passengerRepository;
    private final TownService townService;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final ModelMapper modelMapper;

    public PassengerServiceImpl(PassengerRepository passengerRepository,
                                TownService townService, ValidationUtil validationUtil, Gson gson,
                                ModelMapper modelMapper) {
        this.passengerRepository = passengerRepository;
        this.townService = townService;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.passengerRepository.count() > 0;
    }

    @Override
    public String readPassengersFileContent() throws IOException {
        return Files.readString(Path.of(PASSENGER_FILE_PATH));
    }

    @Override
    public String importPassengers() throws IOException {


        StringBuilder stringBuilder = new StringBuilder();

        Arrays.stream(gson.fromJson(readPassengersFileContent(), PassengerRootDto[].class))
                .filter(passengerRootDto -> {

                    boolean isValid = validationUtil.isValid(passengerRootDto);

                    stringBuilder.append(isValid ? String.format("Successfully imported Passenger %s - %s",
                                    passengerRootDto.getLastName(), passengerRootDto.getEmail()) : "Invalid Passenger")
                            .append(System.lineSeparator());

                    return isValid;
                }).map(passengerRootDto -> {

                    Passenger passenger = modelMapper.map(passengerRootDto, Passenger.class);
                    passenger.setTown(townService.findTownByName(passengerRootDto.getTown()));
                    return passenger;
                }).forEach(passengerRepository::save);


        return stringBuilder.toString();
    }

    @Override
    public String getPassengersOrderByTicketsCountDescendingThenByEmail() {

        StringBuilder stringBuilder = new StringBuilder();

        passengerRepository.findAllPassengersOrderByTicketCountDes().forEach(passenger -> {

            stringBuilder.append(String.format("Passenger %s  %s\n" +
                            "\tEmail - %s\n" +
                            "\tPhone - %s\n" +
                            "\tNumber of tickets - %d\n", passenger.getFirstName(), passenger.getLastName(),
                    passenger.getEmail(), passenger.getPhoneNumber(), passenger.getTickets().size())).
                    append(System.lineSeparator());

        });


        return stringBuilder.toString();
    }

    @Override
    public Passenger findByEmail(String email) {
        return this.passengerRepository.findByEmail(email);
    }
}