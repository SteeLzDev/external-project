package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ListaMargemComServicoAtivoQuery;

public class ListaMargemComServicoAtivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaMargemComServicoAtivoQuery query = new ListaMargemComServicoAtivoQuery();

        executarConsulta(query);
    }
}

