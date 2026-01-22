package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolumeAverbacaoApiGraficoQuery;

public class RelatorioSinteticoGerencialCsaVolumeAverbacaoApiGraficoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioSinteticoGerencialCsaVolumeAverbacaoApiGraficoQuery query = new RelatorioSinteticoGerencialCsaVolumeAverbacaoApiGraficoQuery();
        query.csaCodigo = "267";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

