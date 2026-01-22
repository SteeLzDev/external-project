package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.FuncoesPersonalizadasRestricaoAcessoQuery;

public class FuncoesPersonalizadasRestricaoAcessoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        FuncoesPersonalizadasRestricaoAcessoQuery query = new FuncoesPersonalizadasRestricaoAcessoQuery();
        query.usuCodigo = "123";
        query.entidade = "123";
        query.papel = "1";
        query.tipo = "CSE";

        executarConsulta(query);
    }
}

