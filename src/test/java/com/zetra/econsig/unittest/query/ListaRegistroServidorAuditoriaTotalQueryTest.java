package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorAuditoriaTotalQuery;

public class ListaRegistroServidorAuditoriaTotalQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRegistroServidorAuditoriaTotalQuery query = new ListaRegistroServidorAuditoriaTotalQuery();
        query.recuperaRseExcluido = true;

        executarConsulta(query);
    }
}

