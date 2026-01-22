package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoLimiteQuery;

public class ListaConsignacaoLimiteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoLimiteQuery query = new ListaConsignacaoLimiteQuery();
        query.rseCodigo = "123";
        query.cnvCodigo = "751F808080808080808080809090Z85";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.nseCodigo = "123";
        query.csaCodigo = "267";
        query.adeCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

