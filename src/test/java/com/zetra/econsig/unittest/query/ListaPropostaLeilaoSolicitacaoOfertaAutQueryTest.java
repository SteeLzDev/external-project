package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.ListaPropostaLeilaoSolicitacaoOfertaAutQuery;

public class ListaPropostaLeilaoSolicitacaoOfertaAutQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPropostaLeilaoSolicitacaoOfertaAutQuery query = new ListaPropostaLeilaoSolicitacaoOfertaAutQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";

        executarConsulta(query);
    }
}

