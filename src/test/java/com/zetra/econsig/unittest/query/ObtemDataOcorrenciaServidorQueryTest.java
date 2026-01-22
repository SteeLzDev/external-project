package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servidor.ObtemDataOcorrenciaServidorQuery;

public class ObtemDataOcorrenciaServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemDataOcorrenciaServidorQuery query = new ObtemDataOcorrenciaServidorQuery();
        query.serCodigo = "123";
        query.tocCodigo = "123";
        query.ordenar = true;

        executarConsulta(query);
    }
}

