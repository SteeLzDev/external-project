package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ObtemMinDiaCalendarioQuery;

public class ObtemMinDiaCalendarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemMinDiaCalendarioQuery query = new ObtemMinDiaCalendarioQuery();

        executarConsulta(query);
    }
}

