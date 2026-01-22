package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPorRseSadNseQuery;

public class ListaConsignacaoPorRseSadNseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoPorRseSadNseQuery query = new ListaConsignacaoPorRseSadNseQuery();
        query.rseCodigo = "123";
        query.sadCodigos = java.util.List.of("1", "2");
        query.nseCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

