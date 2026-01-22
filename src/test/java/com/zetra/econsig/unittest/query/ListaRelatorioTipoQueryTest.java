package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.ListaRelatorioTipoQuery;

public class ListaRelatorioTipoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRelatorioTipoQuery query = new ListaRelatorioTipoQuery();
        query.relCodigo = "123";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

