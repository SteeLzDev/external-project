package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.senha.ListaUsuarioSerSenhaAutExpiradaQuery;

public class ListaUsuarioSerSenhaAutExpiradaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuarioSerSenhaAutExpiradaQuery query = new ListaUsuarioSerSenhaAutExpiradaQuery();

        executarConsulta(query);
    }
}

