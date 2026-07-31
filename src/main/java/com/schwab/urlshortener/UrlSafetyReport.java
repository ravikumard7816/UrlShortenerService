package com.schwab.urlshortener;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UrlSafetyReport {

    private final boolean safe;

    @JsonCreator
    public UrlSafetyReport(@JsonProperty("safe") boolean safe) {
        this.safe = safe;
    }

    public boolean isSafe() {
        return safe;
    }
}
