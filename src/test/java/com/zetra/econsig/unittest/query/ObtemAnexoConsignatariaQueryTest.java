package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ObtemAnexoConsignatariaQuery;

public class ObtemAnexoConsignatariaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemAnexoConsignatariaQuery query = new ObtemAnexoConsignatariaQuery();
        query.csaCodigo = "267";
        query.nomeArquivo = "123";

        executarConsulta(query);
    }
}

