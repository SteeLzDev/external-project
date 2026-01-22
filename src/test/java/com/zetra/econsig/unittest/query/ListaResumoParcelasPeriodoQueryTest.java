package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.parcela.ListaResumoParcelasPeriodoQuery;

public class ListaResumoParcelasPeriodoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaResumoParcelasPeriodoQuery query = new ListaResumoParcelasPeriodoQuery();
        query.setCriterios(criterios);

        query.periodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

