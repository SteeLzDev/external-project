package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.mensagem.ListaEmailsEnvioMensagemQuery;

public class ListaEmailsEnvioMensagemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaEmailsEnvioMensagemQuery query = new ListaEmailsEnvioMensagemQuery();
        query.menCodigo = "123";
        query.papCodigo = "1";
        query.csaCodigos = java.util.List.of("1", "2");
        query.incluirBloqueadas = true;

        executarConsulta(query);
    }
}

