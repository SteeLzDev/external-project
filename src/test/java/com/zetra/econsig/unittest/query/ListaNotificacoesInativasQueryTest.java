package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.notificacao.ListaNotificacoesInativasQuery;

public class ListaNotificacoesInativasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaNotificacoesInativasQuery query = new ListaNotificacoesInativasQuery();
        query.ndiCodigo = "123";
        query.tnoCodigo = "123";
        query.funCodigo = "123";
        query.usuCodigoDestinatario = "123";

        executarConsulta(query);
    }
}

