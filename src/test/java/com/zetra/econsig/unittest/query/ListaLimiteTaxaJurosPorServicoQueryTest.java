package com.zetra.econsig.unittest.query;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.juros.ListaLimiteTaxaJurosPorServicoQuery;

public class ListaLimiteTaxaJurosPorServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaLimiteTaxaJurosPorServicoQuery query = new ListaLimiteTaxaJurosPorServicoQuery();
        query.taxaJuros = BigDecimal.ONE;
        query.svcCodigos = java.util.List.of("1", "2");
        query.faixaPrazoInicial = 1;
        query.faixaPrazoFinal = 1;

        executarConsulta(query);
    }
}

