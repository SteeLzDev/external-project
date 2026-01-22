package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoGerencialCsaVolumePortabilidadeTipoQuery;

public class RelatorioSinteticoGerencialCsaVolumePortabilidadeTipoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final RelatorioSinteticoGerencialCsaVolumePortabilidadeTipoQuery query = new RelatorioSinteticoGerencialCsaVolumePortabilidadeTipoQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

