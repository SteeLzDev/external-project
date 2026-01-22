package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioDecisaoJudicialQuery;

public class RelatorioDecisaoJudicialQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioDecisaoJudicialQuery query = new RelatorioDecisaoJudicialQuery();
        query.serCpf = "123";
        query.rseMatricula = "123";
        query.dataIni = "2023-01-01 00:00:00";
        query.dataFim = "2023-01-01 23:59:59";
        query.orgCodigos = java.util.List.of("751F8080808080808080808080809780", "1001808080808080808080808080017B");
        query.estCodigo = "751F8080808080808080808080809680";
        query.csaCodigo = "267";
        query.corCodigo = "EF128080808080808080808080809980";
        query.svcCodigos = java.util.List.of("1", "2");
        query.sadCodigos = java.util.List.of("1", "2");
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

