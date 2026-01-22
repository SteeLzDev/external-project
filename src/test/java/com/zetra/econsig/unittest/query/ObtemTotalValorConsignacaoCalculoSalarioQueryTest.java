package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorConsignacaoCalculoSalarioQuery;

public class ObtemTotalValorConsignacaoCalculoSalarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalValorConsignacaoCalculoSalarioQuery query = new ObtemTotalValorConsignacaoCalculoSalarioQuery();
        query.marCodigo = 1;
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}
