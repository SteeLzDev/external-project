package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.servidor.ObtemConvenioOutroSvcTransfQuery;

public class ObtemConvenioOutroSvcTransfQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ObtemConvenioOutroSvcTransfQuery query = new ObtemConvenioOutroSvcTransfQuery();
        query.setCriterios(criterios);

        query.orgCodigo = "751F8080808080808080808080809780";
        query.rseCodigo = "123";
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.bloqTransfSerBloqCnvSvc = true;

        executarConsulta(query);
    }
}

