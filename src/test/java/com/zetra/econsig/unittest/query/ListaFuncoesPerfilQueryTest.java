package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListaFuncoesPerfilQuery;

public class ListaFuncoesPerfilQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFuncoesPerfilQuery query = new ListaFuncoesPerfilQuery();
        query.perCodigo = "123";
        query.papCodigoOrigem = "1";
        query.papCodigoDestino = "1";

        executarConsulta(query);
    }
}

