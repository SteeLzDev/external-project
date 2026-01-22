package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.parcela.ObtemRetornoNaoDesfeitoQuery;

public class ObtemRetornoNaoDesfeitoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();
        java.util.List<java.lang.String> orgCodigos = java.util.List.of("1", "2");
        java.util.List<java.lang.String> estCodigos = java.util.List.of("1", "2");

        ObtemRetornoNaoDesfeitoQuery query = new ObtemRetornoNaoDesfeitoQuery(orgCodigos, estCodigos);
        query.setCriterios(criterios);


        executarConsulta(query);
    }
}

