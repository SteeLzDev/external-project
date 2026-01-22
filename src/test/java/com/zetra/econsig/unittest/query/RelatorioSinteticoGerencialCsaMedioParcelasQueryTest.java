package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaMedioParcelasQuery;

public class RelatorioSinteticoGerencialCsaMedioParcelasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioSinteticoGerencialCsaMedioParcelasQuery query = new RelatorioSinteticoGerencialCsaMedioParcelasQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

