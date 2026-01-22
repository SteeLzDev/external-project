package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPorRseCnvCodVerbaQuery;

public class ListaConsignacaoPorRseCnvCodVerbaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoPorRseCnvCodVerbaQuery query = new ListaConsignacaoPorRseCnvCodVerbaQuery();
        query.rseCodigo = "123";
        query.sadCodigos = java.util.List.of("1", "2");
        query.cnvCodVerba = java.util.List.of("1", "2");
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

