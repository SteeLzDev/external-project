package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaTaxaJurosAcimaLimiteQuery;

public class ListaTaxaJurosAcimaLimiteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTaxaJurosAcimaLimiteQuery query = new ListaTaxaJurosAcimaLimiteQuery();
        query.csaCodigo = "267";
        query.csaCodigoLimiteTaxa = "123";
        query.svcCodigo = "050E8080808080808080808080808280";

        executarConsulta(query);
    }
}

