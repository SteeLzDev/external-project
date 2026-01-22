package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.correspondente.ListaEmpresaCorrespondenteQuery;

public class ListaEmpresaCorrespondenteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaEmpresaCorrespondenteQuery query = new ListaEmpresaCorrespondenteQuery();
        query.count = false;
        query.csaCodigo = "267";
        query.ecoAtivo = 1;
        query.ecoIdentificador = "123";
        query.ecoNome = "123";
        query.ecoCodigo = "123";
        query.ecoCnpj = "123";

        executarConsulta(query);
    }
}

