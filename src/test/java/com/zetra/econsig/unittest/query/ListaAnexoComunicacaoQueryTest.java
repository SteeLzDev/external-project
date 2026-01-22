package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.anexo.ListaAnexoComunicacaoQuery;

public class ListaAnexoComunicacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAnexoComunicacaoQuery query = new ListaAnexoComunicacaoQuery();
        query.cmnCodigo = "123";
        query.acmNome = "123";
        query.count = false;

        executarConsulta(query);
    }
}

