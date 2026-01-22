package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasTransfSemRetornoQuery;

public class ObtemTotalParcelasTransfSemRetornoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalParcelasTransfSemRetornoQuery query = new ObtemTotalParcelasTransfSemRetornoQuery();
        query.adeCodigos = java.util.List.of("1", "2");
        query.agrupaPorOrgao = true;
        query.periodo = "2023-01-01";

        executarConsulta(query);
    }
}

