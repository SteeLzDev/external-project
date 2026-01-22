package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRelacionamentoByOrigemQuery;

public class ListaConsignacaoRelacionamentoByOrigemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final ListaConsignacaoRelacionamentoByOrigemQuery query = new ListaConsignacaoRelacionamentoByOrigemQuery();
        query.adeCodigoOrigem = "123";
        query.tntCodigo = "123";
        query.sadCodigoDestino = "10";

        executarConsulta(query);
    }
}

