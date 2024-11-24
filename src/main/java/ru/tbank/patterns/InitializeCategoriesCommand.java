package ru.tbank.patterns;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import ru.tbank.client.CategoryApiClient;
import ru.tbank.db_repository.CategoryRepository;
import ru.tbank.entities.Category;
import java.time.LocalDateTime;

@Component
@Slf4j
public class InitializeCategoriesCommand implements Command {

    private final CategoryApiClient categoryApiClient;
    private final CategoryRepository categoryRepository;

    @Autowired
    public InitializeCategoriesCommand(CategoryApiClient categoryApiClient, CategoryRepository categoryRepository) {
        this.categoryApiClient = categoryApiClient;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public void execute() {
        log.info("Инициализация списка категорий");
        try {
            Category[] categories = this.categoryApiClient.initializeData();
            for (Category category : categories) {
                if (categoryRepository.existsBySlug(category.getSlug())) {
                    log.warn("Category already exists");
                } else {
                    category.setNaviDate(LocalDateTime.now());
                    String currentUser = "Initializer";
                    category.setNaviUser(currentUser);
                    this.categoryRepository.save(category);
                }
            }
        } catch (RestClientException ex) {
            log.error("Ошибка загрузки списка категорий");
        }
    }
}
