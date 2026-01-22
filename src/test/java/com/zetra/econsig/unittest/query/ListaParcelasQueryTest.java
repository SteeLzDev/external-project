package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ListaParcelasQuery;

public class ListaParcelasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParcelasQuery query = new ListaParcelasQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.spdCodigos = java.util.List.of("1", "2");
        query.prdNumero = 1;
        query.prdDataDesconto = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.ordenaDataDescontoDesc = true;

        executarConsulta(query);
    }
}

