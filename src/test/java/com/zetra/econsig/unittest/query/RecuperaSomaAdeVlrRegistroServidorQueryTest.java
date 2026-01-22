package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.RecuperaSomaAdeVlrRegistroServidorQuery;

public class RecuperaSomaAdeVlrRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigo = "123";
        java.util.List<java.lang.String> svcCodigos = java.util.List.of("1", "2");
        java.util.List<java.lang.String> sadCodigos = java.util.List.of("1", "2");

        RecuperaSomaAdeVlrRegistroServidorQuery query = new RecuperaSomaAdeVlrRegistroServidorQuery(rseCodigo, svcCodigos, sadCodigos);

        executarConsulta(query);
    }
}

