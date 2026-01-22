package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioTermoAdesaoNaoAutorizadoQuery;

public class RelatorioTermoAdesaoNaoAutorizadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioTermoAdesaoNaoAutorizadoQuery query = new RelatorioTermoAdesaoNaoAutorizadoQuery();
        query.aceiteWeb = true;
        query.aceiteMobile = true;
        query.periodoIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.cse = true;
        query.org = true;
        query.csa = true;
        query.cor = true;
        query.ser = true;
        query.sup = true;
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

