package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.historico.HistoricoConsignacaoRelacionamentoQuery;

public class HistoricoConsignacaoRelacionamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        HistoricoConsignacaoRelacionamentoQuery query = new HistoricoConsignacaoRelacionamentoQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.adeCodigoOrigem = "123";
        query.adeCodigoDestino = "123";
        query.arquivado = true;
        query.intermediario = true;

        executarConsulta(query);
    }
}

