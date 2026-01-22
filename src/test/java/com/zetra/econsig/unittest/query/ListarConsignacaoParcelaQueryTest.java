package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListarConsignacaoParcelaQuery;

public class ListarConsignacaoParcelaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarConsignacaoParcelaQuery query = new ListarConsignacaoParcelaQuery();
        query.rseCodigo = "123";
        query.sadCodigos = java.util.List.of("1", "2");
        query.svcCodigos = java.util.List.of("1", "2");
        query.csaCodigos = java.util.List.of("1", "2");
        query.adeNumeros = java.util.List.of(1l, 2l);

        executarConsulta(query);
    }
}

