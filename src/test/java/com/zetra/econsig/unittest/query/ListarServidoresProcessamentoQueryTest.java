package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ListarServidoresProcessamentoQuery;

public class ListarServidoresProcessamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarServidoresProcessamentoQuery query = new ListarServidoresProcessamentoQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";

        executarConsulta(query);
    }
}

