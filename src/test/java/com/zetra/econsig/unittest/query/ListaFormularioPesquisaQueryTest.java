package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.formulariopesquisa.ListaFormularioPesquisaQuery;

public class ListaFormularioPesquisaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListaFormularioPesquisaQuery query = new ListaFormularioPesquisaQuery();
        query.fpeNome = "Test";
        executarConsulta(query);
    }

    @Test
    public void test_02() throws com.zetra.econsig.exception.ZetraException {
        ListaFormularioPesquisaQuery query = new ListaFormularioPesquisaQuery();
        query.fpeNome = "Test";
        query.count = true;
        executarConsulta(query);
    }
}

