package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetQuery;

public class RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetQuery query = new RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

