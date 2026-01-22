package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.comunicacao.ObtemResponsavelComunicacaoQuery;

public class ObtemResponsavelComunicacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemResponsavelComunicacaoQuery query = new ObtemResponsavelComunicacaoQuery();
        query.cmnCodigo = "123";

        executarConsulta(query);
    }
}

