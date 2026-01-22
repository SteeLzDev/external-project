package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoModuloCompraQuery;

public class ListaServicoModuloCompraQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoModuloCompraQuery query = new ListaServicoModuloCompraQuery();
        query.orgCodigo = "751F8080808080808080808080809780";
        query.csaCodigo = "267";
        query.nseCodigo = "123";

        executarConsulta(query);
    }
}

