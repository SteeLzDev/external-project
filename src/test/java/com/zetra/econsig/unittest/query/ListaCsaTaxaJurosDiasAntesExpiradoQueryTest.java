package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaCsaTaxaJurosDiasAntesExpiradoQuery;

public class ListaCsaTaxaJurosDiasAntesExpiradoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        java.lang.Integer diasParaExpiracao = 1;

        ListaCsaTaxaJurosDiasAntesExpiradoQuery query = new ListaCsaTaxaJurosDiasAntesExpiradoQuery(diasParaExpiracao);

        executarConsulta(query);
    }
}

