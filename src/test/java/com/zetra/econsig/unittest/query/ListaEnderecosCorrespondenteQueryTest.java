package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaEnderecosCorrespondenteQuery;

public class ListaEnderecosCorrespondenteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaEnderecosCorrespondenteQuery query = new ListaEnderecosCorrespondenteQuery();
        query.corCodigo = "EF128080808080808080808080809980";
        query.csaCodigo = "267";
        query.count = false;

        executarConsulta(query);
    }
}

