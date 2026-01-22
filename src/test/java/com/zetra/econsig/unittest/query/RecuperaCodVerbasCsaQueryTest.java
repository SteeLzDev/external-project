package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.RecuperaCodVerbasCsaQuery;

public class RecuperaCodVerbasCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RecuperaCodVerbasCsaQuery query = new RecuperaCodVerbasCsaQuery();
        query.csaCodigo = "267";
        query.incluiCnvBloqueados = true;

        executarConsulta(query);
    }
}

