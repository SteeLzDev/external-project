package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaQuantidadeCargosBloqueadosQuery;

public class RelatorioSinteticoGerencialCsaQuantidadeCargosBloqueadosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioSinteticoGerencialCsaQuantidadeCargosBloqueadosQuery query = new RelatorioSinteticoGerencialCsaQuantidadeCargosBloqueadosQuery();
        query.csaCodigo = "267";
        query.defaultBloqueio = true;

        executarConsulta(query);

        query.csaCodigo = "267";
        executarConsulta(query);
    }
}

