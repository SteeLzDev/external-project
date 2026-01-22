package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConvenioCodVerbaFeriasQuery;

public class ListaConvenioCodVerbaFeriasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConvenioCodVerbaFeriasQuery query = new ListaConvenioCodVerbaFeriasQuery();

        executarConsulta(query);
    }
}

