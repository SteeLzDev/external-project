package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.proposta.ListaPropostaPagamentoDividaExpiradaQuery;

public class ListaPropostaPagamentoDividaExpiradaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPropostaPagamentoDividaExpiradaQuery query = new ListaPropostaPagamentoDividaExpiradaQuery();

        executarConsulta(query);
    }
}

