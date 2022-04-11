package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.Town;
import softuni.exam.models.dto.TownRootDto;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

@Service
public class TownServiceImpl implements TownService {

    private static final String TOWN_FILE_PATH = "src/main/resources/files/json/towns.json";
    private final TownRepository townRepository;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final ModelMapper modelMapper;

    public TownServiceImpl(TownRepository townRepository,
                           ValidationUtil validationUtil, Gson gson, ModelMapper modelMapper) {
        this.townRepository = townRepository;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(Path.of(TOWN_FILE_PATH));
    }

    @Override
    public String importTowns() throws IOException {

        StringBuilder stringBuilder = new StringBuilder();

        Arrays.stream(gson.fromJson(readTownsFileContent(), TownRootDto[].class))
                .filter(townRootDto -> {

                    boolean isValid = validationUtil.isValid(townRootDto);

                    stringBuilder.append(isValid ? String.format("Successfully imported Town %s - %d",
                                    townRootDto.getName(), townRootDto.getPopulation()) : "Invalid Town")
                            .append(System.lineSeparator());


                    return isValid;
                }).map(townRootDto -> modelMapper.map(townRootDto, Town.class))
                .forEach(townRepository::save);


        return stringBuilder.toString();
    }

    @Override
    public Town findTownByName(String name) {
        return this.townRepository.findTownByName(name);
    }
}