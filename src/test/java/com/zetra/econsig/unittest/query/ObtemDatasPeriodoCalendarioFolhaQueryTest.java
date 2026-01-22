package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.calendario.ObtemDatasPeriodoCalendarioFolhaQuery;

public class ObtemDatasPeriodoCalendarioFolhaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ObtemDatasPeriodoCalendarioFolhaQuery query = new ObtemDatasPeriodoCalendarioFolhaQuery();
        query.setCriterios(criterios);

        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigos = java.util.List.of("1", "2");
        query.periodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

