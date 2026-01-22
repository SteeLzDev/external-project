package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConvenioCorrespondenteDesbloquearQuery;

public class ListaConvenioCorrespondenteDesbloquearQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConvenioCorrespondenteDesbloquearQuery query = new ListaConvenioCorrespondenteDesbloquearQuery();
        query.corCodigo = "EF128080808080808080808080809980";

        executarConsulta(query);
    }
}

