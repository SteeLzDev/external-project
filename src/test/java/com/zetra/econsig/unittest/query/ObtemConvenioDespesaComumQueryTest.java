package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.sdp.despesacomum.ObtemConvenioDespesaComumQuery;

public class ObtemConvenioDespesaComumQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemConvenioDespesaComumQuery query = new ObtemConvenioDespesaComumQuery();
        query.decCodigo = "123";

        executarConsulta(query);
    }
}

