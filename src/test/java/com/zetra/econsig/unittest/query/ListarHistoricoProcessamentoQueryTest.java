package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ListarHistoricoProcessamentoQuery;

public class ListarHistoricoProcessamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarHistoricoProcessamentoQuery query = new ListarHistoricoProcessamentoQuery();
        query.estCodigos = java.util.List.of("1", "2");
        query.orgCodigos = java.util.List.of("1", "2");
        query.hprPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.orderDesc = true;

        executarConsulta(query);
    }
}

