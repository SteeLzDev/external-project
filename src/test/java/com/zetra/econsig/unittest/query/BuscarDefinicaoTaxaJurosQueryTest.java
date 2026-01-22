package com.zetra.econsig.unittest.query;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.definicaotaxajuros.BuscarDefinicaoTaxaJurosQuery;

public class BuscarDefinicaoTaxaJurosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        BuscarDefinicaoTaxaJurosQuery query = new BuscarDefinicaoTaxaJurosQuery();
        query.idade = 1;
        query.tempServico = 1;
        query.salario = BigDecimal.ONE;
        query.margem = BigDecimal.ONE;
        query.valorTotal = BigDecimal.ONE;
        query.valorContrato = BigDecimal.ONE;
        query.prazo = 1;
        query.funCodigos = java.util.List.of("1", "2");
        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.offset = 1;

        executarConsulta(query);
    }
}

