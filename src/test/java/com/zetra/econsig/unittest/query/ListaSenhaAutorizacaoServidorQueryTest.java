package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.senha.ListaSenhaAutorizacaoServidorQuery;

public class ListaSenhaAutorizacaoServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaSenhaAutorizacaoServidorQuery query = new ListaSenhaAutorizacaoServidorQuery();
        query.usuCodigo = "123";
        query.sasSenha = "123";
        query.senhasValidas = true;
        query.count = false;

        executarConsulta(query);
    }
}

