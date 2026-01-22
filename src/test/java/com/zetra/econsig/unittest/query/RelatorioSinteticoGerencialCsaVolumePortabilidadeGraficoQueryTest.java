package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolumePortabilidadeGraficoQuery;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.web.ApplicationContextProvider;

public class RelatorioSinteticoGerencialCsaVolumePortabilidadeGraficoQueryTest extends AbstractQueryTest {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioSinteticoGerencialCsaVolumePortabilidadeGraficoQueryTest.class);

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
        try {
            relatorioController.criarPivotAux();
        } catch (final RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

        final RelatorioSinteticoGerencialCsaVolumePortabilidadeGraficoQuery query = new RelatorioSinteticoGerencialCsaVolumePortabilidadeGraficoQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

