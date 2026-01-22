package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.FuncoesPerfilQuery;

public class FuncoesPerfilQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        FuncoesPerfilQuery query = new FuncoesPerfilQuery();
        query.perCodigo = "123";
        query.usuCodigo = "123";
        query.funCodigo = "123";
        query.funDescricao = "123";

        executarConsulta(query);
    }
}

