package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.notificacao.ListaNotificacaoQuery;

public class ListaNotificacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaNotificacaoQuery query = new ListaNotificacaoQuery();
        query.tnoCodigo = "123";
        query.funCodigo = "123";
        query.ndiAtivo = true;

        executarConsulta(query);
    }
}

