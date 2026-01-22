package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaRegistrosServidoresQuery;

public class ListaRegistrosServidoresQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String serCodigo = "123";
        String orgCodigo = "751F8080808080808080808080809780";
        String estCodigo = "751F8080808080808080808080809680";
        String rseMatricula = "123";

        ListaRegistrosServidoresQuery query = new ListaRegistrosServidoresQuery(serCodigo, orgCodigo, estCodigo, rseMatricula);

        executarConsulta(query);
    }
}

