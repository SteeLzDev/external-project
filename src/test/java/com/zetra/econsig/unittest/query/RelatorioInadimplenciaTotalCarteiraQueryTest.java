package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioInadimplenciaTotalCarteiraQuery;

public class RelatorioInadimplenciaTotalCarteiraQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioInadimplenciaTotalCarteiraQuery query = new RelatorioInadimplenciaTotalCarteiraQuery();
        query.prdDtDesconto = "2023-01-01";
        query.csaCodigo = "267";
        query.csaProjetoInadimplencia = "123";
        query.spdCodigos = java.util.List.of("1", "2");
        query.naturezaServico = "123";
        query.sadCodigos = java.util.List.of("1", "2");
        query.count = false;
        query.srsCodigos = java.util.List.of("1", "2");
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

