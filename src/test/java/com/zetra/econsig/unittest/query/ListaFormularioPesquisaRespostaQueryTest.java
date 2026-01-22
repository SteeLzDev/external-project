package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.formulariopesquisa.ListaFormularioPesquisaRespostaQuery;

public class ListaFormularioPesquisaRespostaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListaFormularioPesquisaRespostaQuery query = new ListaFormularioPesquisaRespostaQuery();
        query.fpeCodigo = "1";
        executarConsulta(query);
    }

    @Test
    public void test_02() throws com.zetra.econsig.exception.ZetraException {
        ListaFormularioPesquisaRespostaQuery query = new ListaFormularioPesquisaRespostaQuery();
        query.fpeCodigo = "1";
        query.count = true;
        executarConsulta(query);
    }
}