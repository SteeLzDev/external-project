package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.senha.ListaSenhaAutorizacaoServidorExpiradaQuery;

public class ListaSenhaAutorizacaoServidorExpiradaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaSenhaAutorizacaoServidorExpiradaQuery query = new ListaSenhaAutorizacaoServidorExpiradaQuery();

        executarConsulta(query);
    }
}

