package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.proposta.ListaSolicitacaoPropostaExpiradaQuery;

public class ListaSolicitacaoPropostaExpiradaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaSolicitacaoPropostaExpiradaQuery query = new ListaSolicitacaoPropostaExpiradaQuery();

        executarConsulta(query);
    }
}

