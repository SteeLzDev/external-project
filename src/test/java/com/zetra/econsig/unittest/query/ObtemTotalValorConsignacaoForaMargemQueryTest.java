package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorConsignacaoForaMargemQuery;

public class ObtemTotalValorConsignacaoForaMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalValorConsignacaoForaMargemQuery query = new ObtemTotalValorConsignacaoForaMargemQuery();
        query.rseCodigo = "123";
        query.sadCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

