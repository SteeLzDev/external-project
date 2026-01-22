package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioTermoUsoPrivacidadeAdesaoAutorizadoQuery;

public class RelatorioTermoUsoPrivacidadeAdesaoAutorizadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioTermoUsoPrivacidadeAdesaoAutorizadoQuery query = new RelatorioTermoUsoPrivacidadeAdesaoAutorizadoQuery();
        query.tocCodigo = java.util.List.of("1", "2");
        query.aceiteWeb = true;
        query.aceiteMobile = true;
        query.aceiteTermo = true;
        query.aceitePrivacidade = true;
        query.aceiteTermoAdesaoAutorizado = true;
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

