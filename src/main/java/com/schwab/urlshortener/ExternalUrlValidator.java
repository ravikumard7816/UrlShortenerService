package com.schwab.urlshortener;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("test") // Only active during tests
public class ExternalUrlValidator implements UrlValidator {

    private final RestTemplate restTemplate;
    private final String validationUrl;

    public ExternalUrlValidator(RestTemplate restTemplate, @Value("${external.validation.url}") String validationUrl) {
        this.restTemplate = restTemplate;
        this.validationUrl = validationUrl;
    }

    @Override
    public boolean isUrlSafe(String longUrl) {
        UrlSafetyReport report = restTemplate.postForObject(validationUrl + "/validate", longUrl, UrlSafetyReport.class);
        return report != null && report.isSafe();
    }
}
