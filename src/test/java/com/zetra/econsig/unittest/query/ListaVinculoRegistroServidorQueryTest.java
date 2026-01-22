package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaVinculoRegistroServidorQuery;

public class ListaVinculoRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaVinculoRegistroServidorQuery query = new ListaVinculoRegistroServidorQuery();
        query.ativo = true;
        query.vrsIdentificador = "123";

        executarConsulta(query);
    }
}

