package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.ListaPropostaLeilaoSolicitacaoQuery;

public class ListaPropostaLeilaoSolicitacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPropostaLeilaoSolicitacaoQuery query = new ListaPropostaLeilaoSolicitacaoQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.csaCodigo = "267";
        query.stpCodigo = "123";
        query.arquivado = true;

        executarConsulta(query);
    }
}

