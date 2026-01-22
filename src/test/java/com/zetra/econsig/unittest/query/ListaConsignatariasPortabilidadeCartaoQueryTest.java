package com.zetra.econsig.unittest.query;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignatariasPortabilidadeCartaoQuery;
import org.junit.jupiter.api.Test;

public class ListaConsignatariasPortabilidadeCartaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignatariasPortabilidadeCartaoQuery query = new ListaConsignatariasPortabilidadeCartaoQuery();
        query.csaCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";

        executarConsulta(query);
    }
}
