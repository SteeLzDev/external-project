package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaCodigoVerbaByCsaQuery;

public class ListaCodigoVerbaByCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCodigoVerbaByCsaQuery query = new ListaCodigoVerbaByCsaQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

