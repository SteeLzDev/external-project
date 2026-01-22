package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.senha.ListaOcorrenciaUsuSerSenhaAutorizacaoViaTotemQuery;

public class ListaOcorrenciaUsuSerSenhaAutorizacaoViaTotemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaUsuSerSenhaAutorizacaoViaTotemQuery query = new ListaOcorrenciaUsuSerSenhaAutorizacaoViaTotemQuery();
        query.usuCodigo = "123";

        executarConsulta(query);
    }
}

