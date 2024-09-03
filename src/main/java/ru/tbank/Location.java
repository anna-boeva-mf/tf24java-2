package ru.tbank;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.IOException;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private String slug;
    private Coords coords;

    private static final Logger logger = LoggerFactory.getLogger(Location.class);

    public static Location fromJsonFile(String filePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream inputStream = Location.class.getResourceAsStream(filePath)) {
            if (inputStream == null) {
                logger.error("File not found: {}", filePath);
                return null;
            }
            logger.info("Reading JSON file: {}", filePath);
            return objectMapper.readValue(inputStream, Location.class);
        } catch (IOException e) {
            logger.error("Error reading JSON file", e);
            return null;
        }
    }

    public String toXML() {
        logger.debug("Converting object to XML format");
        return String.format(
                "<Location><Slug>%s</Slug><Coords><Lat>%.6f</Lat><Lon>%.6f</Lon></Coords></Location>",
                slug, coords.getLat(), coords.getLon()
        );
    }

    public void saveToXMLFile(String filePath) {
        try {
            logger.info("Saving XML to file: {}", filePath);
            String xmlContent = toXML();
            java.nio.file.Files.writeString(java.nio.file.Path.of(filePath), xmlContent);
            logger.info("XML saved successfully");
        } catch (IOException e) {
            logger.error("Error saving XML to file", e);
        }
    }
}