package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoSerByAdeCodigoQuery;

public class ListaConsignacaoSerByAdeCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        final String adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";

        final ListaConsignacaoSerByAdeCodigoQuery query = new ListaConsignacaoSerByAdeCodigoQuery();
        query.adeCodigo = adeCodigo;

        executarConsulta(query);
    }
}
