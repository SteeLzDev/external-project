package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ObtemBloqueioNseRegistroServidorQuery;

public class ObtemBloqueioNseRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemBloqueioNseRegistroServidorQuery query = new ObtemBloqueioNseRegistroServidorQuery();
        query.rseCodigo = "123";
        query.nseCodigo = "123";

        executarConsulta(query);
    }
}

