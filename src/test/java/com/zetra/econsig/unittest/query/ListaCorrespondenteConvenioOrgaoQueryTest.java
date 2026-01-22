package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.convenio.ListaCorrespondenteConvenioOrgaoQuery;

public class ListaCorrespondenteConvenioOrgaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaCorrespondenteConvenioOrgaoQuery query = new ListaCorrespondenteConvenioOrgaoQuery();
        query.setCriterios(criterios);

        query.cnvCodigo = "751F808080808080808080809090Z85";

        executarConsulta(query);
    }
}

