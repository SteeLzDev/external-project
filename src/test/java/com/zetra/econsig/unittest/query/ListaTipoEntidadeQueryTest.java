package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.admin.ListaTipoEntidadeQuery;

public class ListaTipoEntidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoEntidadeQuery query = new ListaTipoEntidadeQuery();
        query.tipoEntidadeCodigo = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

