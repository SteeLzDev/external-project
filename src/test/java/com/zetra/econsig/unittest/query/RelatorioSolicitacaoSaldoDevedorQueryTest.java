package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSolicitacaoSaldoDevedorQuery;

public class RelatorioSolicitacaoSaldoDevedorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioSolicitacaoSaldoDevedorQuery query = new RelatorioSolicitacaoSaldoDevedorQuery();
        query.tipoSolicitacao = "123";
        query.periodoIni = "2023-01-01";
        query.periodoFim = "2023-01-01";
        query.adeNumero = "123";
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.estCodigo = "751F8080808080808080808080809680";
        query.orgCodigos = java.util.List.of("1", "2");
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

