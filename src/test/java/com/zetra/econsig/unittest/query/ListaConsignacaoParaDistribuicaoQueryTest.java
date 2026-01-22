package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.distribuirconsignacao.ListaConsignacaoParaDistribuicaoQuery;

public class ListaConsignacaoParaDistribuicaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoParaDistribuicaoQuery query = new ListaConsignacaoParaDistribuicaoQuery();
        query.rseCodigo = "123";
        query.vcoCodigo = "123";

        executarConsulta(query);
    }
}

