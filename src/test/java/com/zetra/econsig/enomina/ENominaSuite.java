package com.zetra.econsig.enomina;

import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
    "com.zetra.econsig.enomina.soap.tests",
    "com.zetra.econsig.enomina.integrationtest",
})
/*
@SelectClasses({
    com.zetra.econsig.tdd.tests.AlterarSenhaTest.class
})
*/
public class ENominaSuite {
}
