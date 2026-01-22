package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.lote.ListarLotesEmProcessamentoQuery;

public class ListarLotesEmProcessamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarLotesEmProcessamentoQuery query = new ListarLotesEmProcessamentoQuery();

        executarConsulta(query);
    }

}
