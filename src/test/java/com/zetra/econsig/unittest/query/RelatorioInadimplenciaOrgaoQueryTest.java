package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioInadimplenciaOrgaoQuery;

public class RelatorioInadimplenciaOrgaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioInadimplenciaOrgaoQuery query = new RelatorioInadimplenciaOrgaoQuery();
        query.prdDtDesconto = "2023-01-01";
        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.csaProjetoInadimplencia = "123";
        query.naturezaServico = "123";
        query.spdCodigos = java.util.List.of("1", "2");
        // query.notOrgCodigo = java.util.List.of("1", "2");
        query.top = false;
        query.count = false;
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

