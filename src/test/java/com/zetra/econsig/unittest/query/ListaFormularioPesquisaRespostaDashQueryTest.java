package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.formulariopesquisa.ListaFormularioPesquisaRespostaDashQuery;

public class ListaFormularioPesquisaRespostaDashQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListaFormularioPesquisaRespostaDashQuery query = new ListaFormularioPesquisaRespostaDashQuery();
        query.fpeCodigo = "1";
        executarConsulta(query);
    }
}