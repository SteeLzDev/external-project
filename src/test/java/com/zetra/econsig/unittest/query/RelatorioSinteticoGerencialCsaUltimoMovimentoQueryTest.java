package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaUltimoMovimentoQuery;

public class RelatorioSinteticoGerencialCsaUltimoMovimentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioSinteticoGerencialCsaUltimoMovimentoQuery query = new RelatorioSinteticoGerencialCsaUltimoMovimentoQuery();
        query.csaCodigo ="267";
        query.AntesUltimoPeriodoProcessado="2024-01-01";
        query.ultimoPeriodoProcessado="2024-02-01";

        executarConsulta(query);
    }
}