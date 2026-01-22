package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaUsuarioSolicitacaoSuporteQuery;

public class ListaUsuarioSolicitacaoSuporteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuarioSolicitacaoSuporteQuery query = new ListaUsuarioSolicitacaoSuporteQuery();

        executarConsulta(query);
    }
}

