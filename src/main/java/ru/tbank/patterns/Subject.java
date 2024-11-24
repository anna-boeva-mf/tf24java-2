package ru.tbank.patterns;

public interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers(String action, Object entity);
}
