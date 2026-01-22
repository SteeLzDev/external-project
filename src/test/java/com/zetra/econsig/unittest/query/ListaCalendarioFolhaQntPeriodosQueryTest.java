package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.calendario.ListaCalendarioFolhaQntPeriodosQuery;

public class ListaCalendarioFolhaQntPeriodosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaCalendarioFolhaQntPeriodosQuery query = new ListaCalendarioFolhaQntPeriodosQuery();
        query.setCriterios(criterios);

        query.dataIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

