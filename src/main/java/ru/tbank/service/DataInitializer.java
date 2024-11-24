package ru.tbank.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.tbank.patterns.InitializeCategoriesCommand;
import ru.tbank.patterns.InitializeEventsCommand;
import ru.tbank.patterns.InitializeLocationsCommand;

@Component
@Slf4j
public class DataInitializer implements ApplicationRunner {

    private final InitializeLocationsCommand initializeLocationsCommand;
    private final InitializeCategoriesCommand initializeCategoriesCommand;
    private final InitializeEventsCommand initializeEventsCommand;

    @Autowired
    public DataInitializer(InitializeLocationsCommand initializeLocationsCommand,
                           InitializeCategoriesCommand initializeCategoriesCommand,
                           InitializeEventsCommand initializeEventsCommand) {
        this.initializeLocationsCommand = initializeLocationsCommand;
        this.initializeCategoriesCommand = initializeCategoriesCommand;
        this.initializeEventsCommand = initializeEventsCommand;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Инициализация данных.");

        initializeLocationsCommand.execute();
        initializeCategoriesCommand.execute();
        initializeEventsCommand.execute();

        log.info("Инициализация данных завершена.");
    }
}
