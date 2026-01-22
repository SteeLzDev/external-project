package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioEventosTotemQuery;

public class RelatorioEventosTotemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioEventosTotemQuery query = new RelatorioEventosTotemQuery();

        executarConsulta(query);
    }
}

