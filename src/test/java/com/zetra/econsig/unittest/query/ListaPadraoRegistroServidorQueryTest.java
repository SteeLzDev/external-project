package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaPadraoRegistroServidorQuery;

public class ListaPadraoRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPadraoRegistroServidorQuery query = new ListaPadraoRegistroServidorQuery();
        query.prsCodigo = "123";
        query.prsIdentificador = "123";

        executarConsulta(query);
    }
}

