package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaServicosSemPrazoConvenioCsaQuery;

public class ListaServicosSemPrazoConvenioCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        final String csaCodigo = "267";

        final ListaServicosSemPrazoConvenioCsaQuery query = new ListaServicosSemPrazoConvenioCsaQuery();
        query.csaCodigo = csaCodigo;

        executarConsulta(query);
    }
}

