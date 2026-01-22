package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConvenioCorrespondenteBloquearQuery;

public class ListaConvenioCorrespondenteBloquearQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConvenioCorrespondenteBloquearQuery query = new ListaConvenioCorrespondenteBloquearQuery();
        query.corCodigo = "EF128080808080808080808080809980";

        executarConsulta(query);
    }
}

