package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.distribuirconsignacao.ListaVerbaConvenioParaDistribuicaoQuery;

public class ListaVerbaConvenioParaDistribuicaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaVerbaConvenioParaDistribuicaoQuery query = new ListaVerbaConvenioParaDistribuicaoQuery();
        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.svcCodigosDestino = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

