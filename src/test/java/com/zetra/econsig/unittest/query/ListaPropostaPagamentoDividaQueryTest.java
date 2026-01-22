package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.proposta.ListaPropostaPagamentoDividaQuery;

public class ListaPropostaPagamentoDividaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPropostaPagamentoDividaQuery query = new ListaPropostaPagamentoDividaQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.csaCodigo = "267";
        query.stpCodigo = "123";
        query.arquivado = true;

        executarConsulta(query);
    }
}

