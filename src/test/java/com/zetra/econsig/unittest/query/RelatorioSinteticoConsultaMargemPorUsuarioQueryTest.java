package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoConsultaMargemPorUsuarioQuery;

public class RelatorioSinteticoConsultaMargemPorUsuarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioSinteticoConsultaMargemPorUsuarioQuery query = new RelatorioSinteticoConsultaMargemPorUsuarioQuery();
        query.csaCodigo = "267";
        query.corCodigos = java.util.List.of("1", "2");
        query.dataIni = "2023-01-01 00:00:00";
        query.dataFim = "2023-01-01 23:59:59";

        executarConsulta(query);
    }
}

