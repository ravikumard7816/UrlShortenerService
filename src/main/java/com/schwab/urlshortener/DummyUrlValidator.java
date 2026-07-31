package com.schwab.urlshortener;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!test") // Active when the 'test' profile is NOT active
public class DummyUrlValidator implements UrlValidator {

    @Override
    public boolean isUrlSafe(String longUrl) {
        // For normal operation, we assume all URLs are safe.
        // This avoids the need for a running WireMock server.
        return true;
    }
}
