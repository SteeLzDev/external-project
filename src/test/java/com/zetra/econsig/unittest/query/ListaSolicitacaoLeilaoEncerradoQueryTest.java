package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.ListaSolicitacaoLeilaoEncerradoQuery;

public class ListaSolicitacaoLeilaoEncerradoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaSolicitacaoLeilaoEncerradoQuery query = new ListaSolicitacaoLeilaoEncerradoQuery();

        executarConsulta(query);
    }
}

