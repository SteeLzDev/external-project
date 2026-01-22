package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaOrgaoConvenioAtivoQuery;

public class ListaOrgaoConvenioAtivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOrgaoConvenioAtivoQuery query = new ListaOrgaoConvenioAtivoQuery();
        query.csaCodigo = "267";
        query.corCodigo = "EF128080808080808080808080809980";

        executarConsulta(query);
    }
}

