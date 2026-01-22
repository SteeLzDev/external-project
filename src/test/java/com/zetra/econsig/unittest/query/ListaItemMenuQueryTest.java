package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.menu.ListaItemMenuQuery;

public class ListaItemMenuQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaItemMenuQuery query = new ListaItemMenuQuery();
        query.mnuCodigo = "123";
        query.itmCodigo = "123";
        query.itmCodigoPai = "123";

        executarConsulta(query);
    }
}

