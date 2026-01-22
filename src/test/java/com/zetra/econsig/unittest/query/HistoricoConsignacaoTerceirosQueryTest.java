package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.historico.HistoricoConsignacaoTerceirosQuery;

public class HistoricoConsignacaoTerceirosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        HistoricoConsignacaoTerceirosQuery query = new HistoricoConsignacaoTerceirosQuery();
        query.adeCodigoOrigem = "123";
        query.adeCodigoDestino = "123";

        executarConsulta(query);
    }
}

