package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaDadosServidoresQuery;

public class RelatorioSinteticoGerencialCsaDadosServidoresQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioSinteticoGerencialCsaDadosServidoresQuery query = new RelatorioSinteticoGerencialCsaDadosServidoresQuery();
        executarConsulta(query);
    }
}