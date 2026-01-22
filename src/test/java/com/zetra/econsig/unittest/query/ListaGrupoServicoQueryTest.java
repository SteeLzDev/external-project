package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaGrupoServicoQuery;

public class ListaGrupoServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaGrupoServicoQuery query = new ListaGrupoServicoQuery();
        query.orderById = true;

        executarConsulta(query);
    }
}

