package com.schwab.urlshortener;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

/**
 * This class is the main entry point for running Cucumber tests with JUnit 5.
 * It tells the JUnit Platform where to find the feature files and the step definition code ("glue").
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.schwab.urlshortener")
public class CucumberTestRunner {
}
