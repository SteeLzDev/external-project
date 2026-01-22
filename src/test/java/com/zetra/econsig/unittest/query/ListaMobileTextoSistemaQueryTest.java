package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.texto.ListaMobileTextoSistemaQuery;

public class ListaMobileTextoSistemaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String texChave = "123";
        java.util.Date texDataAlteracao = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        ListaMobileTextoSistemaQuery query = new ListaMobileTextoSistemaQuery(texChave, texDataAlteracao);

        executarConsulta(query);
    }
}

