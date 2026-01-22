package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ObtemPapelUsuarioQuery;

public class ObtemPapelUsuarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemPapelUsuarioQuery query = new ObtemPapelUsuarioQuery();
        query.usuCodigo = "123";
        query.usuLogin = "123";
        query.usuChaveRecuperarSenha = "123";
        query.usuChaveValidacaoEmail = "123";

        executarConsulta(query);
    }
}

