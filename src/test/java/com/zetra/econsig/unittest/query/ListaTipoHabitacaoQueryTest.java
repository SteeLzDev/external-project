package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaTipoHabitacaoQuery;

public class ListaTipoHabitacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoHabitacaoQuery query = new ListaTipoHabitacaoQuery();
        query.thaCodigo = "123";

        executarConsulta(query);
    }
}

