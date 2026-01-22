package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalContratosPorGrupoSvcQuery;

public class ObtemTotalContratosPorGrupoSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalContratosPorGrupoSvcQuery query = new ObtemTotalContratosPorGrupoSvcQuery();
        query.rseCodigo = "123";
        query.tgsCodigo = "123";
        query.csaCodigo = "267";
        query.adeCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

