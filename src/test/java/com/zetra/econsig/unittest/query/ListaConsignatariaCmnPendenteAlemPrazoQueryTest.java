package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaCmnPendenteAlemPrazoQuery;

public class ListaConsignatariaCmnPendenteAlemPrazoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaConsignatariaCmnPendenteAlemPrazoQuery query = new ListaConsignatariaCmnPendenteAlemPrazoQuery();
        query.setCriterios(criterios);

        query.diasParaBloqueioCmnSer = 1;
        query.diasParaBloqueioCmnCseOrg = 1;
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}

