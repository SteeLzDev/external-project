package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.periodo.ObtemDatasUltimoPeriodoRetornoQuery;

public class ObtemDatasUltimoPeriodoRetornoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemDatasUltimoPeriodoRetornoQuery query = new ObtemDatasUltimoPeriodoRetornoQuery();
        query.orgCodigo = "751F8080808080808080808080809780";
        query.estCodigo = "751F8080808080808080808080809680";

        executarConsulta(query);
    }
}

