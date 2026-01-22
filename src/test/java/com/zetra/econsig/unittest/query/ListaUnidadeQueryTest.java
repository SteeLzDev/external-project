package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.orgao.ListaUnidadeQuery;

public class ListaUnidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUnidadeQuery query = new ListaUnidadeQuery();
        query.sboCodigo = "123";
        query.uniIdentificador = "123";

        executarConsulta(query);
    }
}

