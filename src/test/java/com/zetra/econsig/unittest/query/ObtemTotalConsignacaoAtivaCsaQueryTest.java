package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacaoAtivaCsaQuery;

public class ObtemTotalConsignacaoAtivaCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalConsignacaoAtivaCsaQuery query = new ObtemTotalConsignacaoAtivaCsaQuery();
        query.csaCodigo = "267";
        query.adeIdentificador = "123";

        executarConsulta(query);
    }
}

