package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.persistence.query.notificacao.ListaNotificacaoEmailQuery;

public class ListaNotificacaoEmailQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        com.zetra.econsig.dto.TransferObject criterio = new CustomTransferObject();

        ListaNotificacaoEmailQuery query = new ListaNotificacaoEmailQuery(criterio);

        executarConsulta(query);
    }
}

