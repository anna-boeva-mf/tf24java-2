package ru.tbank.client;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "client")
public class ClientProperties {

    private String categoriesUrl;
    private String locationsUrl;
    private String eventsUrl;

    public String getCategoriesUrl() {
        return categoriesUrl;
    }

    public void setCategoriesUrl(String categoriesUrl) {
        this.categoriesUrl = categoriesUrl;
    }

    public String getLocationsUrl() {
        return locationsUrl;
    }

    public void setLocationsUrl(String locationsUrl) {
        this.locationsUrl = locationsUrl;
    }

    public String getEventsUrl() {
        return eventsUrl;
    }

    public void setEventsUrl(String eventsUrl) {
        this.eventsUrl = eventsUrl;
    }
}
