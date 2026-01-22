package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioTaxasEfetivasQuery;

public class RelatorioTaxasEfetivasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioTaxasEfetivasQuery query = new RelatorioTaxasEfetivasQuery();
        query.periodo = "2023-01-01";
        query.orgCodigos = java.util.List.of("1", "2");
        query.csaCodigo = "267";
        query.svcCodigos = java.util.List.of("1", "2");
        query.sadCodigos = java.util.List.of("1", "2");
        query.prazosInformados = java.util.List.of(1, 2);
        query.prazoMultiploDoze = true;

        executarConsulta(query);
    }
}

