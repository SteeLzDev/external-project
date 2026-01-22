package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.seguranca.ListarOperacoesLiberacaoMargemParaBloqueioQuery;

public class ListarOperacoesLiberacaoMargemParaBloqueioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarOperacoesLiberacaoMargemParaBloqueioQuery query = new ListarOperacoesLiberacaoMargemParaBloqueioQuery();
        query.usuCodigo = "123";
        query.csaCodigo = "267";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

