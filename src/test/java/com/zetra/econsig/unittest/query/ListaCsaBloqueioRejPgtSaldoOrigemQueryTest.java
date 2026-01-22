package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.compra.ListaCsaBloqueioRejPgtSaldoOrigemQuery;

public class ListaCsaBloqueioRejPgtSaldoOrigemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaCsaBloqueioRejPgtSaldoOrigemQuery query = new ListaCsaBloqueioRejPgtSaldoOrigemQuery();
        query.setCriterios(criterios);

        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

