package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.perfil.ListaOcorrenciaPerfilQuery;

public class ListaOcorrenciaPerfilQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOcorrenciaPerfilQuery query = new ListaOcorrenciaPerfilQuery();
        query.count = false;
        query.perCodigo = "123";

        executarConsulta(query);
    }
}

