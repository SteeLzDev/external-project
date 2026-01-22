package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.compra.ObtemEmailDestinatarioMensagemCompraQuery;

public class ObtemEmailDestinatarioMensagemCompraQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ObtemEmailDestinatarioMensagemCompraQuery query = new ObtemEmailDestinatarioMensagemCompraQuery();
        query.setCriterios(criterios);

        query.adeCodigoOrigem = "123";
        query.csaCodigoRemetente = "123";

        executarConsulta(query);
    }
}

