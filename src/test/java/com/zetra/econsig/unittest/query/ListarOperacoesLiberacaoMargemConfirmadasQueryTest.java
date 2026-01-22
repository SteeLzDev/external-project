package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.seguranca.ListarOperacoesLiberacaoMargemConfirmadasQuery;

public class ListarOperacoesLiberacaoMargemConfirmadasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarOperacoesLiberacaoMargemConfirmadasQuery query = new ListarOperacoesLiberacaoMargemConfirmadasQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

