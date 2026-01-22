package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ObtemTotalValorMargemRetidaQuery;

public class ObtemTotalValorMargemRetidaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalValorMargemRetidaQuery query = new ObtemTotalValorMargemRetidaQuery();
        query.rseCodigo = "123";
        query.marCodigo = 1;

        executarConsulta(query);
    }
}

