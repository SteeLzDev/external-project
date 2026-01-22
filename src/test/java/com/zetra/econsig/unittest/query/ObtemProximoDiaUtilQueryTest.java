package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ObtemProximoDiaUtilQuery;

public class ObtemProximoDiaUtilQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        java.util.Date dataInicio = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        java.lang.Integer diasApos = 1;

        ObtemProximoDiaUtilQuery query = new ObtemProximoDiaUtilQuery(dataInicio, diasApos);

        executarConsulta(query);
    }
}

