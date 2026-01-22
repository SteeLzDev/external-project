package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaCodigoVerbaCsaQuery;

public class ListaCodigoVerbaCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCodigoVerbaCsaQuery query = new ListaCodigoVerbaCsaQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

