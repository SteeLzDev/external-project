package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaServicosParaCadastroTaxasQuery;

public class ListaServicosParaCadastroTaxasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicosParaCadastroTaxasQuery query = new ListaServicosParaCadastroTaxasQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

