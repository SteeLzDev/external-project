package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ListaHistoricoMediaMargemQuery;

public class ListaHistoricoMediaMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaHistoricoMediaMargemQuery query = new ListaHistoricoMediaMargemQuery();
        query.estCodigos = java.util.List.of("1", "2");
        query.orgCodigos = java.util.List.of("1", "2");
        query.hpmPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

