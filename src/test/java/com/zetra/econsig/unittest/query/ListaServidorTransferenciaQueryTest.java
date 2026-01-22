package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ListaServidorTransferenciaQuery;

public class ListaServidorTransferenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServidorTransferenciaQuery query = new ListaServidorTransferenciaQuery();
        query.estIdentificador = "123";
        query.orgIdentificador = "123";
        query.rseMatricula = "123";
        query.orgCnpj = "123";
        query.serCPF = "123";
        query.ativo = true;
        query.orgIdentificadorSemMascara = true;

        executarConsulta(query);
    }
}

