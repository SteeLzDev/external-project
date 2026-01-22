package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPossuiRejeitoPgtSaldoQuery;

public class ListaConsignacaoPossuiRejeitoPgtSaldoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaConsignacaoPossuiRejeitoPgtSaldoQuery query = new ListaConsignacaoPossuiRejeitoPgtSaldoQuery();
        query.setCriterios(criterios);

        query.csaCodigo = "267";
        query.count = false;

        executarConsulta(query);
    }
}

