package com.jamesanton;

public enum StatType {
    CONFIRMED("time_series_19-covid-Confirmed.csv"),
    DEATHS ("time_series_19-covid-Deaths.csv"),
    RECOVERED ("time_series_19-covid-Recovered.csv");

    private final String url;
    private static final String BASE_URL = "https://raw.githubusercontent.com/CSSEGISandData/" +
            "COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/";

    StatType(String fileName) {
        this.url = BASE_URL + fileName;
    }

    public String url() {
        return url;
    }
}
