package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaTipoNaturezaEditavelServicoQuery;

public class ListaTipoNaturezaEditavelServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoNaturezaEditavelServicoQuery query = new ListaTipoNaturezaEditavelServicoQuery();
        query.nseCodigo = "123";

        executarConsulta(query);
    }
}

