package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.compra.ListaCsaBloqueioRejPgtSaldoDestinoQuery;

public class ListaCsaBloqueioRejPgtSaldoDestinoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaCsaBloqueioRejPgtSaldoDestinoQuery query = new ListaCsaBloqueioRejPgtSaldoDestinoQuery();
        query.setCriterios(criterios);

        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

