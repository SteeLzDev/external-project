package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaStatusConvenioCorrespondenteQuery;

public class ListaStatusConvenioCorrespondenteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaStatusConvenioCorrespondenteQuery query = new ListaStatusConvenioCorrespondenteQuery();
        query.corCodigo = "EF128080808080808080808080809980";

        executarConsulta(query);
    }
}

