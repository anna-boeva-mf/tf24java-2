package ru.tbank.patterns;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HistoryManager {
    private List<CategorySnapshot> categoryHistory = new ArrayList<>();
    private List<LocationSnapshot> locationHistory = new ArrayList<>();
    private List<EventSnapshot> eventHistory = new ArrayList<>();

    public void addCategorySnapshot(CategorySnapshot snapshot) {
        categoryHistory.add(snapshot);
    }

    public void addLocationSnapshot(LocationSnapshot snapshot) {
        locationHistory.add(snapshot);
    }

    public void addEventSnapshot(EventSnapshot snapshot) {
        eventHistory.add(snapshot);
    }

    public List<CategorySnapshot> getCategoryHistory() {
        return categoryHistory;
    }

    public List<LocationSnapshot> getLocationHistory() {
        return locationHistory;
    }

    public List<EventSnapshot> getEventHistory() {
        return eventHistory;
    }
}
