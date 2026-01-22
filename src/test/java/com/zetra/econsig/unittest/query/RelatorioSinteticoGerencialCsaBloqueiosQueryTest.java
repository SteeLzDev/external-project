package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaBloqueiosQuery;

public class RelatorioSinteticoGerencialCsaBloqueiosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioSinteticoGerencialCsaBloqueiosQuery query = new RelatorioSinteticoGerencialCsaBloqueiosQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

