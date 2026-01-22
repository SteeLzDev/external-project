package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.ListaRelatorioQuery;

public class ListaRelatorioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRelatorioQuery query = new ListaRelatorioQuery();
        query.relCodigo = "123";
        query.relTitulo = "123";
        query.relAtivo = 1;
        query.relCustomizado = "123";

        executarConsulta(query);
    }
}

