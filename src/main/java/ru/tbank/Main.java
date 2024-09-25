package ru.tbank;

import ru.tbank.clients.CategoryApiClient;
import ru.tbank.clients.LocationApiClient;
import ru.tbank.entities.Category;
import ru.tbank.entities.Location;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        LocationApiClient apiClient = new LocationApiClient();

        // Получить все города
        String allLocations = apiClient.getAllLocations();
        System.out.println("All locations: " + allLocations);

        // Получить город по ID
        int locationId = 1;
        String location = apiClient.getLocationById(locationId);
        System.out.println("Location with ID " + locationId + ": " + location);

        // Создать новый город
        Location newLocation = new Location("ryb", "Рыбинск");
        apiClient.createLocation(newLocation);

        // Обновить город
        Location updLocation = new Location("yar", "Ярославль");
        apiClient.updateLocation(1, updLocation);

        allLocations = apiClient.getAllLocations();
        System.out.println("All locations: " + allLocations);

        // Удалить город
        apiClient.deleteLocation(1);
        System.out.println("Location with ID " + 1 + " deleted.");

        allLocations = apiClient.getAllLocations();
        System.out.println("All locations: " + allLocations);

        //////////////

        CategoryApiClient apiClientCat = new CategoryApiClient();

        // Получить все категории
        String allCategories = apiClientCat.getAllCategories();
        System.out.println("All categories: " + allCategories);

        // Получить категорию по ID
        int categoryId = 1;
        String category = apiClientCat.getCategoryById(categoryId);
        System.out.println("Category with ID " + categoryId + ": " + category);

        // Создать новую категорию
        Category newCategory = new Category("animal-cafes", "Кафе с животными");
        apiClientCat.createCategory(newCategory);

        // Обновить категорию
        Category updCategory = new Category("stables", "Конюшни");
        apiClientCat.updateCategory(1, updCategory);

        allCategories = apiClientCat.getAllCategories();
        System.out.println("All categories: " + allCategories);

        // Удалить категорию
        apiClientCat.deleteCategory(1);
        System.out.println("Category with ID " + 1 + " deleted.");

        allCategories = apiClientCat.getAllCategories();
        System.out.println("All categories: " + allCategories);
    }
}
