package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoCsaQuery;

public class ObtemTotalConsignacaoCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalConsignacaoCsaQuery query = new ObtemTotalConsignacaoCsaQuery();
        query.rseCodigo = "123";
        query.sadCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}
