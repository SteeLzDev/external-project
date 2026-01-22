package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioOcorrenciaRegistroServidorQuery;

public class RelatorioOcorrenciaRegistroServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioOcorrenciaRegistroServidorQuery query = new RelatorioOcorrenciaRegistroServidorQuery();
        query.dataIni = "2023-01-01 00:00:00";
        query.dataFim = "2023-01-01 23:59:59";
        query.orgCodigos = java.util.List.of("1", "2");
        query.serCpf = "123";
        query.estCodigo = "751F8080808080808080808080809680";
        query.rseMatricula = "123";
        query.opLogin = "123";
        query.tocCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

