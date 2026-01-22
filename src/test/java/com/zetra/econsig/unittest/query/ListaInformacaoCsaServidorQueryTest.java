package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaInformacaoCsaServidorQuery;

public class ListaInformacaoCsaServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaInformacaoCsaServidorQuery query = new ListaInformacaoCsaServidorQuery();
        query.csaCodigo = "267";
        query.serCodigo = "123";

        executarConsulta(query);
    }
}

