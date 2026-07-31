package com.schwab.urlshortener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.ContextConfiguration;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = UrlShortenerService.class)
@EnableRetry
public class UrlShortenerServiceTest {

    @Mock
    private UrlMappingRepository urlMappingRepository;
    
    @Mock
    private UrlValidator urlValidator; // Use the interface instead of the old class

    @InjectMocks
    private UrlShortenerService urlShortenerService;

    @Test
    public void testShortenUrl() {
        String longUrl = "https://www.example.com";
        UrlMapping mapping = new UrlMapping();
        mapping.setId(1L);
        mapping.setLongUrl(longUrl);

        when(urlValidator.isUrlSafe(longUrl)).thenReturn(true); // Use the new mock
        when(urlMappingRepository.findByLongUrl(longUrl)).thenReturn(Optional.empty());
        when(urlMappingRepository.saveAndFlush(any(UrlMapping.class))).thenReturn(mapping);

        String shortUrl = urlShortenerService.shortenUrl(longUrl);

        verify(urlMappingRepository).setShortUrlById(any(String.class), any(Long.class));
        assertEquals(Base62.encode(1L), shortUrl);
    }

    @Test
    public void testGetLongUrl() {
        String longUrl = "https://www.example.com";
        String shortUrl = "b";
        UrlMapping urlMapping = new UrlMapping(shortUrl, longUrl);

        when(urlMappingRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(urlMapping));

        String result = urlShortenerService.getLongUrl(shortUrl);

        assertEquals(longUrl, result);
        verify(urlMappingRepository).save(urlMapping);
    }

    @Test
    public void testShortenUrl_DatabaseDown_ShouldRetryAndFail() {
        String longUrl = "https://www.resilience-test.com";

        when(urlValidator.isUrlSafe(longUrl)).thenReturn(true); // Use the new mock
        when(urlMappingRepository.findByLongUrl(longUrl))
            .thenThrow(new DataAccessResourceFailureException("Cannot connect to database"));

        assertThrows(ServiceUnavailableException.class, () -> {
            urlShortenerService.shortenUrl(longUrl);
        });

        verify(urlMappingRepository, times(3)).findByLongUrl(longUrl);
    }

    @Test
    public void testGetLongUrl_NotFound_ShouldFailFastAndNotRetry() {
        String shortUrl = "non-existent";

        // Simulate a "not found" scenario
        when(urlMappingRepository.findByShortUrl(shortUrl)).thenReturn(Optional.empty());

        // Verify that the service throws the correct exception immediately
        assertThrows(ShortUrlNotFoundException.class, () -> {
            urlShortenerService.getLongUrl(shortUrl);
        });

        // Verify that the repository method was only called ONCE (no retries)
        verify(urlMappingRepository, times(1)).findByShortUrl(shortUrl);
    }
}
