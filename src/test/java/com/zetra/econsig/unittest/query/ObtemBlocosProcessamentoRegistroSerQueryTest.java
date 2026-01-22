package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ObtemBlocosProcessamentoRegistroSerQuery;

public class ObtemBlocosProcessamentoRegistroSerQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        java.lang.Integer bprCodigo = 1;

        ObtemBlocosProcessamentoRegistroSerQuery query = new ObtemBlocosProcessamentoRegistroSerQuery(bprCodigo);

        executarConsulta(query);
    }
}

