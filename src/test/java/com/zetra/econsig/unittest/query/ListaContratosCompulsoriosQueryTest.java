package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ListaContratosCompulsoriosQuery;

public class ListaContratosCompulsoriosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaContratosCompulsoriosQuery query = new ListaContratosCompulsoriosQuery();
        query.somatorioValor = true;
        query.svcCodigo = "050E8080808080808080808080808280";
        query.rseCodigo = "123";
        query.svcPrioridade = "123";

        executarConsulta(query);
    }
}

