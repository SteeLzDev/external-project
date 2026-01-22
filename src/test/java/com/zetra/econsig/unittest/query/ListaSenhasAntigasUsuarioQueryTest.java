package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.senha.ListaSenhasAntigasUsuarioQuery;

public class ListaSenhasAntigasUsuarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaSenhasAntigasUsuarioQuery query = new ListaSenhasAntigasUsuarioQuery();
        query.usuCodigo = "123";

        executarConsulta(query);
    }
}

