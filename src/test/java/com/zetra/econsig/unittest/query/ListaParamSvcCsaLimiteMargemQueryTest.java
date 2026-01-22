package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamSvcCsaLimiteMargemQuery;

public class ListaParamSvcCsaLimiteMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamSvcCsaLimiteMargemQuery query = new ListaParamSvcCsaLimiteMargemQuery();
        query.csaCodigo = "267";
        query.rseCodigo = "123";
        query.marCodigo = 1;

        executarConsulta(query);
    }
}

