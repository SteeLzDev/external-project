package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPorRseCnvQuery;

public class ListaConsignacaoPorRseCnvQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoPorRseCnvQuery query = new ListaConsignacaoPorRseCnvQuery();
        query.rseCodigo = "123";
        query.cnvCodigo = "751F808080808080808080809090Z85";
        query.sadCodigos = java.util.List.of("1", "2");
        query.adePeriodicidade = "123";

        executarConsulta(query);
    }
}

