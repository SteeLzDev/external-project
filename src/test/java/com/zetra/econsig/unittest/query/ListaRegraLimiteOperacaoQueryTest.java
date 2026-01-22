package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.limiteoperacao.ListaRegraLimiteOperacaoQuery;

public class ListaRegraLimiteOperacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaRegraLimiteOperacaoQuery query = new ListaRegraLimiteOperacaoQuery();

        executarConsulta(query);
    }
}

