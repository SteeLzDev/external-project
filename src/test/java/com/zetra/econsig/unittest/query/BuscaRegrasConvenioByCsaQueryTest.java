package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.regraconvenio.BuscaRegrasConvenioByCsaQuery;

public class BuscaRegrasConvenioByCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

    	BuscaRegrasConvenioByCsaQuery query = new BuscaRegrasConvenioByCsaQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}
