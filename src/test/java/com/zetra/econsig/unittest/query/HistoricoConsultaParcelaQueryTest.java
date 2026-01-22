package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.historico.HistoricoConsultaParcelaQuery;

public class HistoricoConsultaParcelaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        HistoricoConsultaParcelaQuery query = new HistoricoConsultaParcelaQuery();
        query.setCriterios(criterios);

        query.csaCodigo = "267";
        query.spdCodigos = java.util.List.of("1", "2");
        query.prdDataDesconto = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

