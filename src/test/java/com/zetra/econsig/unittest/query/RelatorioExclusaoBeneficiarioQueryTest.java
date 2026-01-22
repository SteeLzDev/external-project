package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioExclusaoBeneficiarioQuery;

public class RelatorioExclusaoBeneficiarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioExclusaoBeneficiarioQuery query = new RelatorioExclusaoBeneficiarioQuery();
        query.dataIni = "2023-01-01 00:00:00";
        query.dataFim = "2023-01-01 23:59:59";
        query.operadora = "123";
        query.beneficio = "123";
        query.motivoOperacao = "123";
        query.formatoRelatorio = "123";

        executarConsulta(query);
    }
}

