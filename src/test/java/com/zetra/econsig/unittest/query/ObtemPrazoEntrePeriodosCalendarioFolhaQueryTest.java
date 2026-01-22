package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.calendario.ObtemPrazoEntrePeriodosCalendarioFolhaQuery;

public class ObtemPrazoEntrePeriodosCalendarioFolhaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ObtemPrazoEntrePeriodosCalendarioFolhaQuery query = new ObtemPrazoEntrePeriodosCalendarioFolhaQuery();
        query.setCriterios(criterios);

        query.orgCodigo = "751F8080808080808080808080809780";
        query.periodoInicial = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFinal = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

