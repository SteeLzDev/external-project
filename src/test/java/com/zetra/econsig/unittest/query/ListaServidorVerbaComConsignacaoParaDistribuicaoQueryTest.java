package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.distribuirconsignacao.ListaServidorVerbaComConsignacaoParaDistribuicaoQuery;

public class ListaServidorVerbaComConsignacaoParaDistribuicaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServidorVerbaComConsignacaoParaDistribuicaoQuery query = new ListaServidorVerbaComConsignacaoParaDistribuicaoQuery();
        query.svcCodigoOrigem = "123";
        query.csaCodigos = java.util.List.of("1", "2");
        query.rseMatricula = "123";
        query.serCPF = "123";

        executarConsulta(query);
    }
}

