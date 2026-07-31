package com.schwab.urlshortener;

import org.springframework.dao.DataAccessException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UrlShortenerService {

    private final UrlMappingRepository urlMappingRepository;
    private final UrlValidator urlValidator; // Use the interface

    public UrlShortenerService(UrlMappingRepository urlMappingRepository, UrlValidator urlValidator) { // Depend on the interface
        this.urlMappingRepository = urlMappingRepository;
        this.urlValidator = urlValidator;
    }

    @Retryable(
        value = { DataAccessException.class }, 
        exclude = { InvalidUrlException.class }, 
        maxAttempts = 3, 
        backoff = @Backoff(delay = 1000))
    @Transactional
    public String shortenUrl(String longUrl) {
        if (!urlValidator.isUrlSafe(longUrl)) { // Use the injected validator
            throw new InvalidUrlException("The provided URL is considered unsafe.");
        }

        Optional<UrlMapping> existingMapping = urlMappingRepository.findByLongUrl(longUrl);
        if (existingMapping.isPresent()) {
            return existingMapping.get().getShortUrl();
        }

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setLongUrl(longUrl);
        
        urlMapping = urlMappingRepository.saveAndFlush(urlMapping);
        
        String shortUrl = Base62.encode(urlMapping.getId());
        
        urlMappingRepository.setShortUrlById(shortUrl, urlMapping.getId());
        
        return shortUrl;
    }

    @Retryable(
        value = { DataAccessException.class }, 
        exclude = { ShortUrlNotFoundException.class }, 
        maxAttempts = 3, 
        backoff = @Backoff(delay = 1000))
    @Transactional
    public String getLongUrl(String shortUrl) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new ShortUrlNotFoundException(shortUrl));
        urlMapping.setAccessCount(urlMapping.getAccessCount() + 1);
        urlMappingRepository.save(urlMapping);
        return urlMapping.getLongUrl();
    }

    @Retryable(
        value = { DataAccessException.class }, 
        exclude = { ShortUrlNotFoundException.class }, 
        maxAttempts = 3, 
        backoff = @Backoff(delay = 1000))
    public UrlAnalyticsResponse getAnalytics(String shortUrl) {
        UrlMapping urlMapping = urlMappingRepository.findByShortUrl(shortUrl)
                .orElseThrow(() -> new ShortUrlNotFoundException(shortUrl));
        return new UrlAnalyticsResponse("http://localhost:8080/" + urlMapping.getShortUrl(), urlMapping.getLongUrl(), urlMapping.getAccessCount());
    }

    @Recover
    public String recoverForString(DataAccessException e, String url) {
        throw new ServiceUnavailableException("Database is currently unavailable. Please try again later.", e);
    }
    
    @Recover
    public UrlAnalyticsResponse recoverForAnalytics(DataAccessException e, String shortUrl) {
        throw new ServiceUnavailableException("Database is currently unavailable. Please try again later.", e);
    }
}
