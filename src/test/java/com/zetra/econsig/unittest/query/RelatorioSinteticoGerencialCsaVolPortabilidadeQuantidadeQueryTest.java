package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolPortabilidadeQuantidadeQuery;

public class RelatorioSinteticoGerencialCsaVolPortabilidadeQuantidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioSinteticoGerencialCsaVolPortabilidadeQuantidadeQuery query = new RelatorioSinteticoGerencialCsaVolPortabilidadeQuantidadeQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

