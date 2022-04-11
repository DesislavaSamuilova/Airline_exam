package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.Plane;
import softuni.exam.models.dto.PlaneRootSeedDto;
import softuni.exam.repository.PlaneRepository;
import softuni.exam.service.PlaneService;
import softuni.exam.util.ValidationUtil;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class PlaneServiceImpl implements PlaneService {

    private static final String PLANE_FILE_PATH = "src/main/resources/files/xml/planes.xml";
    private final PlaneRepository planeRepository;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;

    public PlaneServiceImpl(PlaneRepository planeRepository, ModelMapper modelMapper,
                            XmlParser xmlParser, ValidationUtil validationUtil) {
        this.planeRepository = planeRepository;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
    }


    @Override
    public boolean areImported() {
        return planeRepository.count() > 0;
    }

    @Override
    public String readPlanesFileContent() throws IOException {
        return Files.readString(Path.of(PLANE_FILE_PATH));
    }

    @Override
    public String importPlanes() throws JAXBException, FileNotFoundException {

        StringBuilder stringBuilder = new StringBuilder();

        xmlParser.fromFile(PLANE_FILE_PATH, PlaneRootSeedDto.class).getPlanes().stream()
                .filter(planesDto -> {

                    boolean isValid = validationUtil.isValid(planesDto);

                    stringBuilder.append(isValid ? String.format("Successfully imported Plane %s",
                                    planesDto.getRegisterNumber()) : "Invalid Plane")
                            .append(System.lineSeparator());

                    return isValid;
                }).map(planesDto -> modelMapper.map(planesDto, Plane.class))
                .forEach(planeRepository::save);

        return stringBuilder.toString();
    }

    @Override
    public Plane findRegisterNumber(String registerNumber) {
        return this.planeRepository.findByRegisterNumber(registerNumber);
    }
}