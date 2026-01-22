package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.FuncoesPerfilRestricaoAcessoQuery;

public class FuncoesPerfilRestricaoAcessoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        FuncoesPerfilRestricaoAcessoQuery query = new FuncoesPerfilRestricaoAcessoQuery();
        query.perCodigo = "123";
        query.papel = "1";

        executarConsulta(query);
    }
}

