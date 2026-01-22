package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ObtemRegistroServidorQuery;

public class ObtemRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemRegistroServidorQuery query = new ObtemRegistroServidorQuery();
        query.estCodigo = "751F8080808080808080808080809680";
        query.estIdentificador = "123";
        query.estCnpj = "123";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.orgIdentificador = "123";
        query.orgCnpj = "123";
        query.rseMatricula = "123";
        query.serCpf = "123";

        executarConsulta(query);
    }
}

