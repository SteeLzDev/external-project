package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ObtemBlocosProcessamentoAgrupadosQuery;

public class ObtemBlocosProcessamentoAgrupadosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemBlocosProcessamentoAgrupadosQuery query = new ObtemBlocosProcessamentoAgrupadosQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";
        query.sbpCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

