package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamSvcCsaQuery;

public class ListaParamSvcCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamSvcCsaQuery query = new ListaParamSvcCsaQuery();
        query.svcCodigos = java.util.List.of("1", "2");
        query.csaCodigos = java.util.List.of("1", "2");
        query.tpsCodigos = java.util.List.of("1", "2");
        query.ativo = true;
        query.dataIniVigIndiferente = true;
        query.csaIdentificadorInterno = "123";
        query.pscVlr = "123";
        query.pscVlrDiferente = "123";
        query.pscVlrRefDiferente = "123";

        executarConsulta(query);
    }
}

