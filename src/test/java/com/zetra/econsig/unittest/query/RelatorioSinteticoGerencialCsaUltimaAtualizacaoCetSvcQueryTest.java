package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetSvcQuery;

public class RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetSvcQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetSvcQuery query = new RelatorioSinteticoGerencialCsaUltimaAtualizacaoCetSvcQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

