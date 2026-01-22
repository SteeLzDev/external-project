package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaServicosSimulacaoQuery;

public class ListaServicosSimulacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicosSimulacaoQuery query = new ListaServicosSimulacaoQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.dia = 1;
        query.usaDefinicaoTaxaJuros = true;
        query.corCodigo = "EF128080808080808080808080809980";

        executarConsulta(query);
    }
}

