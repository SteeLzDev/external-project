package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioTaxasEfetivasContratosQuery;

public class RelatorioTaxasEfetivasContratosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioTaxasEfetivasContratosQuery query = new RelatorioTaxasEfetivasContratosQuery();
        query.periodo = "2023-01-01";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.csaCodigo = "267";
        query.svcCodigos = java.util.List.of("1", "2");
        query.sadCodigos = java.util.List.of("1", "2");
        query.prazosInformados = java.util.List.of(1, 2);
        query.prazoMultiploDoze = true;

        executarConsulta(query);
    }
}

