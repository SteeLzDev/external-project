package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.mensagem.ListaConsignatariaMensagemQuery;

public class ListaConsignatariaMensagemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignatariaMensagemQuery query = new ListaConsignatariaMensagemQuery();
        query.csaIdentificador = "123";
        query.csaNome = "123";
        query.csaNomeAbrev = "123";
        query.csaCodigo = "267";
        query.menCodigo = "123";

        executarConsulta(query);
    }
}

