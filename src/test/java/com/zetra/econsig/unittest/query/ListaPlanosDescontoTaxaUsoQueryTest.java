package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.sdp.plano.ListaPlanosDescontoTaxaUsoQuery;

public class ListaPlanosDescontoTaxaUsoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaPlanosDescontoTaxaUsoQuery query = new ListaPlanosDescontoTaxaUsoQuery();
        query.setCriterios(criterios);

        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

