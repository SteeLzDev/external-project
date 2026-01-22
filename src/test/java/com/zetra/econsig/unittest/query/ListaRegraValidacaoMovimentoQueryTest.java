package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.movimento.ListaRegraValidacaoMovimentoQuery;

public class ListaRegraValidacaoMovimentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRegraValidacaoMovimentoQuery query = new ListaRegraValidacaoMovimentoQuery();
        query.rvmAtivo = true;

        executarConsulta(query);
    }
}

