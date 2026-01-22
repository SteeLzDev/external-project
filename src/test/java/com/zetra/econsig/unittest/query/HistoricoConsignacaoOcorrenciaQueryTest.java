package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.historico.HistoricoConsignacaoOcorrenciaQuery;

public class HistoricoConsignacaoOcorrenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        HistoricoConsignacaoOcorrenciaQuery query = new HistoricoConsignacaoOcorrenciaQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.tocCodigos = java.util.List.of("1", "2");
        query.mostraTodoHistorico = true;
        query.arquivado = true;
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

