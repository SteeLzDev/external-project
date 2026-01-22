package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoSemParcelaQuery;

public class ListaConsignacaoSemParcelaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoSemParcelaQuery query = new ListaConsignacaoSemParcelaQuery();
        query.adeNumero = java.util.List.of(1l, 2l);
        query.rseCodigo = "123";
        query.count = false;
        query.orgCodigo = "751F8080808080808080808080809780";
        query.csaCodigo = "267";
        query.corCodigo = "EF128080808080808080808080809980";

        executarConsulta(query);
    }
}

