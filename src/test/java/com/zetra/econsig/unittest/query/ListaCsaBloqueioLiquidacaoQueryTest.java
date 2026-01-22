package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.compra.ListaCsaBloqueioLiquidacaoQuery;

public class ListaCsaBloqueioLiquidacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaCsaBloqueioLiquidacaoQuery query = new ListaCsaBloqueioLiquidacaoQuery();
        query.setCriterios(criterios);

        query.csaCodigo = "267";
        query.csaComBloqManual = true;

        executarConsulta(query);
    }
}

