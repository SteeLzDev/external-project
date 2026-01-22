package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioRegrasConvenioQuery;

public class RelatorioRegrasConvenioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioRegrasConvenioQuery query = new RelatorioRegrasConvenioQuery();

        executarConsulta(query);
    }
}

