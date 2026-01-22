package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.ListaSolicitacaoLeilaoCanceladoParaBloqueioQuery;

public class ListaSolicitacaoLeilaoCanceladoParaBloqueioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaSolicitacaoLeilaoCanceladoParaBloqueioQuery query = new ListaSolicitacaoLeilaoCanceladoParaBloqueioQuery();
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

