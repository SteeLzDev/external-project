package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.exception.RelatorioControllerException;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaInadimplenciaUltMovFinQuery;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.web.ApplicationContextProvider;

public class RelatorioSinteticoGerencialCsaInadimplenciaUltMovFinQueryTest extends AbstractQueryTest {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RelatorioSinteticoGerencialCsaInadimplenciaUltMovFinQueryTest.class);

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
        try {
            relatorioController.criarPivotAux();
        } catch (final RelatorioControllerException e) {
            LOG.error("ERROR: " + e.getMessage());
        }

        final RelatorioSinteticoGerencialCsaInadimplenciaUltMovFinQuery query = new RelatorioSinteticoGerencialCsaInadimplenciaUltMovFinQuery();
        query.csaCodigo = "267";
        query.periodo = "2023-01-01";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

