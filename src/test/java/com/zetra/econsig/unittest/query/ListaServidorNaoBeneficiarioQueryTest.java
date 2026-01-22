package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaServidorNaoBeneficiarioQuery;

public class ListaServidorNaoBeneficiarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServidorNaoBeneficiarioQuery query = new ListaServidorNaoBeneficiarioQuery();
        query.count = false;
        query.excecoes = null;

        executarConsulta(query);
    }
}

