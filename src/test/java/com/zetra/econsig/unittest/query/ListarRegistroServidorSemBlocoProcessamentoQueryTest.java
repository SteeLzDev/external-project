package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ListarRegistroServidorSemBlocoProcessamentoQuery;

public class ListarRegistroServidorSemBlocoProcessamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarRegistroServidorSemBlocoProcessamentoQuery query = new ListarRegistroServidorSemBlocoProcessamentoQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";

        executarConsulta(query);
    }
}

