package com.zetra.econsig.bdd;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/econsig")
@ConfigurationParameter(key = io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME, value = "com.zetra.econsig.bdd.steps")
//@ConfigurationParameter(key = io.cucumber.junit.platform.engine.Constants.FILTER_TAGS_PROPERTY_NAME, value = "@mytag")
public class BDDSuite {
}
