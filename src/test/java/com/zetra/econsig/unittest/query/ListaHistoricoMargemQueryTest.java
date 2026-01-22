package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ListaHistoricoMargemQuery;

public class ListaHistoricoMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        com.zetra.econsig.helper.seguranca.AcessoSistema responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        ListaHistoricoMargemQuery query = new ListaHistoricoMargemQuery(responsavel);
        query.count = false;
        query.rseCodigo = "123";
        query.marCodigo = 1;
        query.adeNumero = 1l;
        query.hmrOperacao = "123";
        query.periodoIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

