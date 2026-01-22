package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioEstatisticoProcessamentoPeriodosQuery;

public class RelatorioEstatisticoProcessamentoPeriodosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioEstatisticoProcessamentoPeriodosQuery query = new RelatorioEstatisticoProcessamentoPeriodosQuery();
        query.funCodigos = java.util.List.of("1", "2");
        query.tarCodigos = java.util.List.of("1", "2");
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

