package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaUsuarioNotificacaoInatividadeQuery;

public class ListaUsuarioNotificacaoInatividadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuarioNotificacaoInatividadeQuery query = new ListaUsuarioNotificacaoInatividadeQuery();
        query.dataLimiteBloqueio = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

