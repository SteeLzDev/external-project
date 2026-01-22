package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRseNseQuery;

public class ListaConsignacaoRseNseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoRseNseQuery query = new ListaConsignacaoRseNseQuery();
        query.sadCodigos = java.util.List.of("1", "2");
        query.tocCodigos = java.util.List.of("1", "2");
        query.rseCodigo = "123";
        query.nseCodigo = "123";
        query.somenteValorReduzido = true;

        executarConsulta(query);
    }
}

