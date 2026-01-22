package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.ajuda.ListaFuncaoPapelAcessoRecursoQuery;

public class ListaFuncaoPapelAcessoRecursoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFuncaoPapelAcessoRecursoQuery query = new ListaFuncaoPapelAcessoRecursoQuery();
        query.acrCodigos = java.util.List.of("1", "2");
        query.acrRecurso = "123";
        query.funCodigos = java.util.List.of("1", "2");
        query.acrOperacao = "123";
        query.acrParametro = "123";

        executarConsulta(query);
    }
}

