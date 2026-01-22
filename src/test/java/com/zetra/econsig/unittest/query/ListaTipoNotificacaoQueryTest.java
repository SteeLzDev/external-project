package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.notificacao.ListaTipoNotificacaoQuery;

public class ListaTipoNotificacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoNotificacaoQuery query = new ListaTipoNotificacaoQuery();

        executarConsulta(query);
    }
}

