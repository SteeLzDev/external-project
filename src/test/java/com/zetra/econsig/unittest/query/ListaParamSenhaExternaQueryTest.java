package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.senhaexterna.ListaParamSenhaExternaQuery;

public class ListaParamSenhaExternaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamSenhaExternaQuery query = new ListaParamSenhaExternaQuery();

        executarConsulta(query);
    }
}

