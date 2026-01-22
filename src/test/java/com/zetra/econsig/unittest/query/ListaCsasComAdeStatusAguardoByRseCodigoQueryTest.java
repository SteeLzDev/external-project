package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsasComAdeStatusAguardoByRseCodigoQuery;

public class ListaCsasComAdeStatusAguardoByRseCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCsasComAdeStatusAguardoByRseCodigoQuery query = new ListaCsasComAdeStatusAguardoByRseCodigoQuery();
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}
