package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRseSvcQuery;

public class ListaConsignacaoRseSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoRseSvcQuery query = new ListaConsignacaoRseSvcQuery();
        query.svcCodigo = "050E8080808080808080808080808280";
        query.rseCodigo = "123";
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

