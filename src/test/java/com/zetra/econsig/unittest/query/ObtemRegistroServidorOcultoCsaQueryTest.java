package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.registroservidor.ObtemRegistroServidorOcultoCsaQuery;

public class ObtemRegistroServidorOcultoCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigo = "123";

        ObtemRegistroServidorOcultoCsaQuery query = new ObtemRegistroServidorOcultoCsaQuery(rseCodigo);

        executarConsulta(query);
    }
}
