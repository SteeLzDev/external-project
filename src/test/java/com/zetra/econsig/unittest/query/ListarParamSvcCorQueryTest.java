package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListarParamSvcCorQuery;

public class ListarParamSvcCorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarParamSvcCorQuery query = new ListarParamSvcCorQuery();
        query.svcCodigos = java.util.List.of("1", "2");
        query.corCodigos = java.util.List.of("1", "2");
        query.tpsCodigos = java.util.List.of("1", "2");
        query.ativo = true;
        query.dataIniVigIndiferente = true;
        query.psoVlr = "123";

        executarConsulta(query);
    }
}

