package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.senha.ObtemProtocoloSenhaAutorizacaoQuery;

public class ObtemProtocoloSenhaAutorizacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemProtocoloSenhaAutorizacaoQuery query = new ObtemProtocoloSenhaAutorizacaoQuery();
        query.psaCodigo = "123";

        executarConsulta(query);
    }
}

