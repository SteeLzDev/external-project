package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSaldoDevedorServidorQuery;

public class RelatorioSaldoDevedorServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioSaldoDevedorServidorQuery query = new RelatorioSaldoDevedorServidorQuery();

        executarConsulta(query);
    }
}

