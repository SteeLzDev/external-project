package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.texto.ListaTextoSistemaQuery;

public class ListaTextoSistemaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTextoSistemaQuery query = new ListaTextoSistemaQuery();
        query.count = false;
        query.texChave = "123";
        query.texTexto = "123";
        query.texDataAlteracao = "<ISNULL>";

        executarConsulta(query);
    }
}

