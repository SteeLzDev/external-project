package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConvenioCodVerbaRefQuery;

public class ListaConvenioCodVerbaRefQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConvenioCodVerbaRefQuery query = new ListaConvenioCodVerbaRefQuery();

        executarConsulta(query);
    }
}

