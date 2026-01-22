package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ObtemMaxDiaCalendarioQuery;

public class ObtemMaxDiaCalendarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemMaxDiaCalendarioQuery query = new ObtemMaxDiaCalendarioQuery();

        executarConsulta(query);
    }
}

