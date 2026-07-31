package com.schwab.urlshortener;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UrlShortenerStepDefinitions extends CucumberSpringConfiguration {

    @Autowired
    private MockMvc mockMvc;

    // Autowire the mocks that were created in the configuration class.
    // They are guaranteed to be non-null here.
    @Autowired
    private UrlMappingRepository urlMappingRepository;

    @Autowired
    private UrlValidator urlValidator;

    private WireMockServer wireMockServer;
    private ResultActions lastResult;
    private String lastShortUrl;

    @Before
    public void setup() {
        // Reset mocks before each scenario to ensure test isolation
        Mockito.reset(urlMappingRepository, urlValidator);
        
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(8090));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8090);
    }

    @After
    public void teardown() {
        wireMockServer.stop();
    }

    @Given("the application is running")
    public void theApplicationIsRunning() {}

    @Given("the external URL safety service is running")
    public void theExternalURLSafetyServiceIsRunning() {}

    @Given("the safety service will report {string} as safe")
    public void theSafetyServiceWillReportAsSafe(String url) {
        when(urlValidator.isUrlSafe(url)).thenReturn(true);
    }

    @Given("the safety service will report {string} as unsafe")
    public void theSafetyServiceWillReportAsUnsafe(String url) {
        when(urlValidator.isUrlSafe(url)).thenReturn(false);
    }

    @Given("a user has already shortened {string} to a short URL")
    public void aUserHasAlreadyShortenedToAShortURL(String longUrl) throws Exception {
        when(urlValidator.isUrlSafe(longUrl)).thenReturn(true);
        when(urlMappingRepository.findByLongUrl(longUrl)).thenReturn(Optional.empty());
        UrlMapping mapping = new UrlMapping();
        mapping.setId(1L);
        mapping.setLongUrl(longUrl);
        when(urlMappingRepository.saveAndFlush(any(UrlMapping.class))).thenReturn(mapping);
        
        lastResult = mockMvc.perform(post("/shorten")
                .contentType(MediaType.TEXT_PLAIN)
                .content(longUrl));
        lastShortUrl = lastResult.andReturn().getResponse().getContentAsString();
    }

    @Given("the database is down")
    public void theDatabaseIsDown() {
        when(urlValidator.isUrlSafe(anyString())).thenReturn(true);
        when(urlMappingRepository.findByLongUrl(anyString()))
            .thenThrow(new DataAccessResourceFailureException("Database is down for resilience test"));
    }
    
    @Given("the database is running")
    public void theDatabaseIsRunning() {}

    @Given("the short URL {string} does not exist")
    public void theShortURLDoesNotExist(String shortUrl) {
        when(urlMappingRepository.findByShortUrl(shortUrl)).thenReturn(Optional.empty());
    }

    @When("a user sends a POST request to {string} with the body {string}")
    public void aUserSendsAPOSTRequestToWithTheBody(String path, String body) throws Exception {
        lastResult = mockMvc.perform(post(path)
                .contentType(MediaType.TEXT_PLAIN)
                .content(body));
    }
    
    @When("a user sends a GET request to {string}")
    public void aUserSendsAGETRequestTo(String path) throws Exception {
        lastResult = mockMvc.perform(get(path));
    }

    @When("the user sends a GET request to the short URL path")
    public void theUserSendsAGETRequestToTheShortURLPath() throws Exception {
        String shortUrlCode = lastShortUrl.replaceAll(".*\"shortUrl\":\"http://localhost:8080/(.*)\".*", "$1");
        
        UrlMapping mapping = new UrlMapping();
        mapping.setLongUrl("https://www.yahoo.com");
        when(urlMappingRepository.findByShortUrl(shortUrlCode)).thenReturn(Optional.of(mapping));

        lastResult = mockMvc.perform(get("/" + shortUrlCode));
    }

    @Then("the user receives a {int} OK response")
    public void theUserReceivesAOKResponse(int status) throws Exception {
        lastResult.andExpect(status().is(status));
    }
    
    @Then("the user receives a {int} Bad Request error")
    public void theUserReceivesABadRequestError(int status) throws Exception {
        lastResult.andExpect(status().is(status));
    }

    @Then("the user receives a {int} Service Unavailable error")
    public void theUserReceivesAServiceUnavailableError(int status) throws Exception {
        lastResult.andExpect(status().is(status));
    }
    
    @Then("the user receives a {int} Not Found error")
    public void theUserReceivesANotFoundError(int status) throws Exception {
        lastResult.andExpect(status().is(status));
    }

    @Then("the response contains a short URL")
    public void theResponseContainsAShortURL() throws Exception {
        lastResult.andExpect(jsonPath("$.shortUrl").exists());
    }

    @Then("the user is redirected to {string} with a {int} Found status")
    public void theUserIsRedirectedToWithAFoundStatus(String url, int status) throws Exception {
        lastResult.andExpect(status().is(status));
        lastResult.andExpect(header().string("Location", url));
    }

    @Then("the error message indicates the URL is unsafe")
    public void theErrorMessageIndicatesTheURLIsUnsafe() throws Exception {
        lastResult.andExpect(content().string(containsString("unsafe")));
    }

    @Then("the system has attempted the operation {int} times")
    public void theSystemHasAttemptedTheOperationTimes(int times) {
        verify(urlMappingRepository, times(times)).findByLongUrl(anyString());
    }
    
    @Then("the system has attempted the lookup only {int} time")
    public void theSystemHasAttemptedTheLookupOnlyTime(int times) {
        verify(urlMappingRepository, times(times)).findByShortUrl(anyString());
    }
}
