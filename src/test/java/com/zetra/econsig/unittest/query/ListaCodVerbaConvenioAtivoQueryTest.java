package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaCodVerbaConvenioAtivoQuery;

public class ListaCodVerbaConvenioAtivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCodVerbaConvenioAtivoQuery query = new ListaCodVerbaConvenioAtivoQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

