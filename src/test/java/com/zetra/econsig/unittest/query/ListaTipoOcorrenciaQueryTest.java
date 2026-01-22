package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaTipoOcorrenciaQuery;

public class ListaTipoOcorrenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTipoOcorrenciaQuery query = new ListaTipoOcorrenciaQuery();
        query.tocCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

