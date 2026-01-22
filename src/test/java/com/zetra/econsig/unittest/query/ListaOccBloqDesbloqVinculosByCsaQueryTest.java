package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.ocorrenciaconsignataria.ListaOccBloqDesbloqVinculosByCsaQuery;

public class ListaOccBloqDesbloqVinculosByCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOccBloqDesbloqVinculosByCsaQuery query = new ListaOccBloqDesbloqVinculosByCsaQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}