package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConvenioSvcQuery;

public class ListaConvenioSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConvenioSvcQuery query = new ListaConvenioSvcQuery();
        query.count = false;
        query.svcCodigo = "050E8080808080808080808080808280";
        query.cnvCodVerba = "123";

        executarConsulta(query);
    }
}

