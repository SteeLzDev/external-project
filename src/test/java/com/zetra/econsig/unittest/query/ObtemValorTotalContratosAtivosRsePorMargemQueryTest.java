package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemValorTotalContratosAtivosRsePorMargemQuery;

public class ObtemValorTotalContratosAtivosRsePorMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

    	ObtemValorTotalContratosAtivosRsePorMargemQuery query = new ObtemValorTotalContratosAtivosRsePorMargemQuery();
        query.marCodigo = 1;
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}
