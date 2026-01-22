package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.definicaotaxajuros.ListaServicosDefinicaoTaxaJurosQuery;

public class ListaServicosDefinicaoTaxaJurosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicosDefinicaoTaxaJurosQuery query = new ListaServicosDefinicaoTaxaJurosQuery();
        query.svcCodigo = "050E8080808080808080808080808280";
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

