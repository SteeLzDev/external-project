package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemContratosLiqPrzIndeterminadoQuery;

public class ListaExtratoMargemContratosLiqPrzIndeterminadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigo = "123";

        ListaExtratoMargemContratosLiqPrzIndeterminadoQuery query = new ListaExtratoMargemContratosLiqPrzIndeterminadoQuery(rseCodigo);

        executarConsulta(query);
    }
}

