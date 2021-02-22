package com.stonks.main;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class CompanyData {
    @JsonProperty("Meta Data")
    Map<String, String> metadata;

    @JsonProperty("Time Series (Daily)")
    Map<String, StockData> timeSeriesDataMap;
}
