package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioConfCadVerbaQuery;

public class RelatorioConfCadVerbaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioConfCadVerbaQuery query = new RelatorioConfCadVerbaQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.csaCodigo = "267";
        query.orgCodigos = java.util.List.of("1", "2");
        query.svcCodigos = java.util.List.of("1", "2");
        query.scvCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

