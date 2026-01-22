package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioComunicacoesQuery;

public class RelatorioComunicacoesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioComunicacoesQuery query = new RelatorioComunicacoesQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.count = false;
        query.soCmnPai = true;

        executarConsulta(query);
    }
}

