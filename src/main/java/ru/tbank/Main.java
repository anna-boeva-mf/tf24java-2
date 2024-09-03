package ru.tbank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        logger.info("Application started");

        Location location = Location.fromJsonFile("/city.json");
        if (location != null) {
            logger.info("Location object created: {}", location);
            location.saveToXMLFile("location.xml");
        } else {
            logger.warn("Location object creation failed");
        }

        logger.info("Application finished");
    }
}