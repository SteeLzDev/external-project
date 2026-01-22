package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoReclamacoesQuery;

public class RelatorioSinteticoReclamacoesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioSinteticoReclamacoesQuery query = new RelatorioSinteticoReclamacoesQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.dataInicio = "2023-01-01 00:00:00";
        query.dataFim = "2023-01-01 23:59:59";
        query.csaCodigo = "267";
        query.tmrCodigos = java.util.List.of("1", "2");
        query.fields = null;

        executarConsulta(query);
    }
}

