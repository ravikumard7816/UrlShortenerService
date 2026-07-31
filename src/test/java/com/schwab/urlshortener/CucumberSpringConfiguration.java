package com.schwab.urlshortener;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

/**
 * This class is the single source of truth for configuring the Cucumber test context.
 * It loads the Spring Boot application and creates all necessary mock beans.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK) // Use MOCK environment for MockMvc
@AutoConfigureMockMvc
public class CucumberSpringConfiguration {

    // Define all mock beans required for the tests here.
    // This ensures they are created and available in the Spring context
    // before any step definition classes are instantiated.
    @MockBean
    protected UrlMappingRepository urlMappingRepository;

    @MockBean
    protected UrlValidator urlValidator;
}
