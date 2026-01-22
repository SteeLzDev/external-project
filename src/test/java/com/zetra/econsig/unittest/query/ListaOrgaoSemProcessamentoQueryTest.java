package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.retorno.ListaOrgaoSemProcessamentoQuery;

public class ListaOrgaoSemProcessamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOrgaoSemProcessamentoQuery query = new ListaOrgaoSemProcessamentoQuery();
        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

