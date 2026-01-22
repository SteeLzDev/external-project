package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.sdp.despesacomum.ListaDespesaComumQuery;

public class ListaDespesaComumQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaDespesaComumQuery query = new ListaDespesaComumQuery();
        query.echCodigo = "123";
        query.plaCodigo = "123";
        query.decCodigo = "123";
        query.count = false;

        executarConsulta(query);
    }
}

