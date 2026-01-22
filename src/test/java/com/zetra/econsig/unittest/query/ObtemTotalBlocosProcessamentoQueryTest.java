package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ObtemTotalBlocosProcessamentoQuery;

public class ObtemTotalBlocosProcessamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalBlocosProcessamentoQuery query = new ObtemTotalBlocosProcessamentoQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";
        query.tbpCodigos = java.util.List.of("1", "2");
        query.sbpCodigos = java.util.List.of("1", "2");
        query.convenioMapeado = true;

        executarConsulta(query);
    }
}

