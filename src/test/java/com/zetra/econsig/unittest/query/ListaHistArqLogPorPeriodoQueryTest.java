package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.log.ListaHistArqLogPorPeriodoQuery;

public class ListaHistArqLogPorPeriodoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaHistArqLogPorPeriodoQuery query = new ListaHistArqLogPorPeriodoQuery();
        query.setCriterios(criterios);

        query.dataIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.dataFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

