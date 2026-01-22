package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConsignatariaMesmaVerbaQuery;

public class ListaConsignatariaMesmaVerbaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignatariaMesmaVerbaQuery query = new ListaConsignatariaMesmaVerbaQuery();
        query.csaCodigo = "267";
        query.cnvCodVerba = "123";

        executarConsulta(query);
    }
}

