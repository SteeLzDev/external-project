package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.funcao.ListaFuncoesPermitidasPapelQuery;

public class ListaFuncoesPermitidasPapelQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFuncoesPermitidasPapelQuery query = new ListaFuncoesPermitidasPapelQuery();
        query.papCodigoOrigem = "1";
        query.papCodigoDestino = "1";

        executarConsulta(query);
    }
}

