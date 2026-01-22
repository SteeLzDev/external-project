package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.relatorio.RelatorioPercentualRejeitoTotalQuery;

public class RelatorioPercentualRejeitoTotalQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();
        String periodo = "2023-01-01";
        java.util.List<java.lang.String> orgCodigos = java.util.List.of("1", "2");
        java.util.List<java.lang.String> estCodigos = java.util.List.of("1", "2");
        boolean integrada = true;
        boolean rejeitoDoPeriodo = true;

        RelatorioPercentualRejeitoTotalQuery query = new RelatorioPercentualRejeitoTotalQuery(periodo, orgCodigos, estCodigos, integrada, rejeitoDoPeriodo);
        query.setCriterios(criterios);


        executarConsulta(query);
    }
}

