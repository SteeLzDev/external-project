package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConvenioCorrespondenteIncluirQuery;

public class ListaConvenioCorrespondenteIncluirQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConvenioCorrespondenteIncluirQuery query = new ListaConvenioCorrespondenteIncluirQuery();
        query.corCodigo = "EF128080808080808080808080809980";

        executarConsulta(query);
    }
}

