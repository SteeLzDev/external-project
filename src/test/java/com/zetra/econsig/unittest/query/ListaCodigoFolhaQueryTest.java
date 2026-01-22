package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignante.ListaCodigoFolhaQuery;

public class ListaCodigoFolhaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCodigoFolhaQuery query = new ListaCodigoFolhaQuery();

        executarConsulta(query);
    }
}

