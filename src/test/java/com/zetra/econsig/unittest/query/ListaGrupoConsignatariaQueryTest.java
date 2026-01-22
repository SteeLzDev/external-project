package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaGrupoConsignatariaQuery;

public class ListaGrupoConsignatariaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaGrupoConsignatariaQuery query = new ListaGrupoConsignatariaQuery();
        query.tgcCodigo = "123";

        executarConsulta(query);
    }
}

