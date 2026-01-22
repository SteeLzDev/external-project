package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoReaberturaQuery;

public class ListaConsignacaoReaberturaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigo = "123";
        java.util.Date periodoAtual = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        ListaConsignacaoReaberturaQuery query = new ListaConsignacaoReaberturaQuery(rseCodigo, periodoAtual);

        executarConsulta(query);
    }
}

