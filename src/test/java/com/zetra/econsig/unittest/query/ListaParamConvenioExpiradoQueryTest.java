package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaParamConvenioExpiradoQuery;

public class ListaParamConvenioExpiradoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaParamConvenioExpiradoQuery query = new ListaParamConvenioExpiradoQuery();

        executarConsulta(query);
    }
}

