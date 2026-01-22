package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.FindEmailUsuarioRepeatQuery;

public class FindEmailUsuarioRepeatQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        FindEmailUsuarioRepeatQuery query = new FindEmailUsuarioRepeatQuery();
        query.emailUsuario = "123";

        executarConsulta(query);
    }
}

