package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.FuncoesPersonalizadasQuery;

public class FuncoesPersonalizadasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        FuncoesPersonalizadasQuery query = new FuncoesPersonalizadasQuery();
        query.usuCodigo = "123";
        query.entidade = "123";
        query.tipo = "CSE";
        query.funCodigo = "123";
        query.funDescricao = "123";

        executarConsulta(query);
    }
}

