package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.correspondente.ListaCorrespondentesQuery;

public class ListaCorrespondentesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCorrespondentesQuery query = new ListaCorrespondentesQuery();
        query.count = false;
        query.csaCodigo = "267";
        query.corAtivo = null;
        query.corIdentificador = "123";
        query.corNome = "123";
        query.corCodigo = "EF128080808080808080808080809980";
        query.ecoCodigo = "123";

        executarConsulta(query);
    }
}

