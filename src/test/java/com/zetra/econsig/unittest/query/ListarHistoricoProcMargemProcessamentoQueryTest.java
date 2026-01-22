package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ListarHistoricoProcMargemProcessamentoQuery;

public class ListarHistoricoProcMargemProcessamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarHistoricoProcMargemProcessamentoQuery query = new ListarHistoricoProcMargemProcessamentoQuery();
        query.estCodigos = java.util.List.of("1", "2");
        query.orgCodigos = java.util.List.of("1", "2");
        query.cseCodigo = "1";
        query.hpmPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

