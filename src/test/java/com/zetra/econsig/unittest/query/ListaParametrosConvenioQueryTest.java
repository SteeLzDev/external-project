package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.convenio.ListaParametrosConvenioQuery;

public class ListaParametrosConvenioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaParametrosConvenioQuery query = new ListaParametrosConvenioQuery();
        query.setCriterios(criterios);

        query.cnvCodigo = "751F808080808080808080809090Z85";
        query.csaCodigo = "267";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.cnvAtivo = true;
        query.svcAtivo = true;

        executarConsulta(query);
    }
}

