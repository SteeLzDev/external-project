package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaLimiteMargemCsaOrgByCsaCodigoQuery;

public class ListaLimiteMargemCsaOrgByCsaCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaLimiteMargemCsaOrgByCsaCodigoQuery query = new ListaLimiteMargemCsaOrgByCsaCodigoQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

