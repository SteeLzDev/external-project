package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ObtemMaxMinVlrParamSvcCseNseQuery;

public class ObtemMaxMinVlrParamSvcCseNseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemMaxMinVlrParamSvcCseNseQuery query = new ObtemMaxMinVlrParamSvcCseNseQuery();
        query.tpsCodigos = java.util.List.of("1", "2");
        query.nseCodigo = "123";
        query.buscaMin = true;

        executarConsulta(query);
    }
}

