package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaCodVerbaConvenioInativoQuery;

public class ListaCodVerbaConvenioInativoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCodVerbaConvenioInativoQuery query = new ListaCodVerbaConvenioInativoQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

