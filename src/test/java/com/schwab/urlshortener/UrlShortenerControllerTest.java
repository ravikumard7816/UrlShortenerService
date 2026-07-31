package com.schwab.urlshortener;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UrlShortenerController.class)
public class UrlShortenerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UrlShortenerService urlShortenerService;

    // This test is no longer used with the current setup, but I'm keeping it for reference.
    // @MockBean
    // private UrlValidator urlValidator;

    @Test
    public void testShortenUrl() throws Exception {
        String longUrl = "https://www.example.com";
        String shortCode = "1a2b3c";
        String fullShortUrl = "http://localhost:8080/" + shortCode;

        // The service now returns just the short code
        when(urlShortenerService.shortenUrl(longUrl)).thenReturn(shortCode);

        mockMvc.perform(post("/shorten")
                .contentType(MediaType.TEXT_PLAIN) // Be explicit with content type
                .content(longUrl))
                .andExpect(status().isOk())
                // The controller now returns a JSON object
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.shortUrl").value(fullShortUrl));
    }

    @Test
    public void testRedirect() throws Exception {
        String longUrl = "https://www.example.com";
        String shortUrlKey = "1a2b3c";

        // The service now returns the longUrl directly
        when(urlShortenerService.getLongUrl(shortUrlKey)).thenReturn(longUrl);

        mockMvc.perform(get("/" + shortUrlKey))
                .andExpect(status().isFound())
                .andExpect(header().string("Location", longUrl));
    }

    @Test
    public void testRedirectNotFound() throws Exception {
        String shortUrlKey = "nonexistent";

        // The service now throws an exception when not found
        when(urlShortenerService.getLongUrl(shortUrlKey))
            .thenThrow(new ShortUrlNotFoundException(shortUrlKey));

        mockMvc.perform(get("/" + shortUrlKey))
                .andExpect(status().isNotFound());
    }
}
