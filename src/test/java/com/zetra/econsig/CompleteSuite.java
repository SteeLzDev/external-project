package com.zetra.econsig;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import com.zetra.econsig.enomina.ENominaSuite;

@Suite
@SelectClasses({
    ENominaSuite.class,
    EconsigSuite.class
})
public class CompleteSuite {
}
