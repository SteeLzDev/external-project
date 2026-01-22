package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.leilao.ObtemAnaliseDeRiscoRegistroServidorQuery;

public class ObtemAnaliseDeRiscoRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemAnaliseDeRiscoRegistroServidorQuery query = new ObtemAnaliseDeRiscoRegistroServidorQuery();
        query.csaCodigo = "267";
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

