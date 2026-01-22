package com.zetra.econsig;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    com.zetra.econsig.bdd.BDDSuite.class,
    com.zetra.econsig.tdd.TDDSuite.class
})
public class EconsigSuite {
}
