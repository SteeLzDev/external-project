package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioRegrasConvenioListaConveniosQuery;

public class RelatorioRegrasConvenioListaConveniosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        com.zetra.econsig.helper.seguranca.AcessoSistema responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        RelatorioRegrasConvenioListaConveniosQuery query = new RelatorioRegrasConvenioListaConveniosQuery(responsavel);

        executarConsulta(query);
    }
}

