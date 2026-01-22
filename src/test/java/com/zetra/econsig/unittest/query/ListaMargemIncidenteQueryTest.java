package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.margem.ListaMargemIncidenteQuery;
import com.zetra.econsig.values.CodedValues;

public class ListaMargemIncidenteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaMargemIncidenteQuery query = new ListaMargemIncidenteQuery();
        query.setCriterios(criterios);

        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.rseCodigo = "123";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.estCodigo = "751F8080808080808080808080809680";
        query.marCodigo = CodedValues.INCIDE_MARGEM_SIM;

        executarConsulta(query);
    }
}

