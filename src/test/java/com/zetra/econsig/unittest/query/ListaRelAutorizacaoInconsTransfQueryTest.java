package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaRelAutorizacaoInconsTransfQuery;

public class ListaRelAutorizacaoInconsTransfQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRelAutorizacaoInconsTransfQuery query = new ListaRelAutorizacaoInconsTransfQuery();
        query.origem = true;
        query.rseCodigoAnt = "123";
        query.rseCodigoNov = "123";

        executarConsulta(query);
    }
}

