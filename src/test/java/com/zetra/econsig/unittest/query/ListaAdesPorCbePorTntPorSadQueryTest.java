package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.contrato.ListaAdesPorCbePorTntPorSadQuery;

public class ListaAdesPorCbePorTntPorSadQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAdesPorCbePorTntPorSadQuery query = new ListaAdesPorCbePorTntPorSadQuery();
        query.cbeCodigo = "123";
        query.tntCodigo = java.util.List.of("1", "2");
        query.sadCodigo = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

