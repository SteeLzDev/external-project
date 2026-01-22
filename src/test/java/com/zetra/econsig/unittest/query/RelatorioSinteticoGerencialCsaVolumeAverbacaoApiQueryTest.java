package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolumeAverbacaoApiQuery;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.web.ApplicationContextProvider;

public class RelatorioSinteticoGerencialCsaVolumeAverbacaoApiQueryTest extends AbstractQueryTest {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioSinteticoGerencialCsaVolumeAverbacaoApiQueryTest.class);

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
        try {
            relatorioController.criarPivotAux();
        } catch (final RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

        final RelatorioSinteticoGerencialCsaVolumeAverbacaoApiQuery query = new RelatorioSinteticoGerencialCsaVolumeAverbacaoApiQuery();
        query.csaCodigo = "267";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

