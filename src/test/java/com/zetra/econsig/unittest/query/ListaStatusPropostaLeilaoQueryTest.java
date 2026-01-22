package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.ListaStatusPropostaLeilaoQuery;

public class ListaStatusPropostaLeilaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaStatusPropostaLeilaoQuery query = new ListaStatusPropostaLeilaoQuery();

        executarConsulta(query);
    }
}

