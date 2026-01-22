package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ObtemBlocoQuery;

public class ObtemBlocoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        java.lang.Integer bprCodigo = 1;

        ObtemBlocoQuery query = new ObtemBlocoQuery(bprCodigo);

        executarConsulta(query);
    }
}

