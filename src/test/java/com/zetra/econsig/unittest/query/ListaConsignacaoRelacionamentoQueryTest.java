package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRelacionamentoQuery;

public class ListaConsignacaoRelacionamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoRelacionamentoQuery query = new ListaConsignacaoRelacionamentoQuery();
        query.adeCodigoList = java.util.List.of("1", "2");
        query.adeCodigoOrigem = "123";
        query.adeCodigoDestino = "123";
        query.csaCodigoOrigem = "123";
        query.csaCodigoDestino = "123";
        query.tntCodigo = "123";
        query.stcCodigo = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

