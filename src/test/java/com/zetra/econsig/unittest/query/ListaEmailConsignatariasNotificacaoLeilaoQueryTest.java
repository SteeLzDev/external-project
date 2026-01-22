package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.ListaEmailConsignatariasNotificacaoLeilaoQuery;

public class ListaEmailConsignatariasNotificacaoLeilaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaEmailConsignatariasNotificacaoLeilaoQuery query = new ListaEmailConsignatariasNotificacaoLeilaoQuery();

        executarConsulta(query);
    }
}

