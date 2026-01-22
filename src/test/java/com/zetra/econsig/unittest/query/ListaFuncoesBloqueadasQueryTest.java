package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListaFuncoesBloqueadasQuery;

public class ListaFuncoesBloqueadasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFuncoesBloqueadasQuery query = new ListaFuncoesBloqueadasQuery();
        query.usuCodigo = "123";
        query.tipoEntidade = "ORG";

        executarConsulta(query);
    }
}

