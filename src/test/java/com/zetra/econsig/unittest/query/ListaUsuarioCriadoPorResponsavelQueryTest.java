package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.usuario.ListaUsuarioCriadoPorResponsavelQuery;

public class ListaUsuarioCriadoPorResponsavelQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuarioCriadoPorResponsavelQuery query = new ListaUsuarioCriadoPorResponsavelQuery();
        query.responsaveis = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

