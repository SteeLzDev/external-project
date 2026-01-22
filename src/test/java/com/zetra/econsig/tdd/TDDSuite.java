package com.zetra.econsig.tdd;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
    "com.zetra.econsig.tdd.tests",
    "com.zetra.econsig.integrationtest",
    "com.zetra.econsig.unittest"
})
/*
@SelectClasses({
    com.zetra.econsig.tdd.tests.AlterarSenhaTest.class
})
*/
public class TDDSuite {
}
