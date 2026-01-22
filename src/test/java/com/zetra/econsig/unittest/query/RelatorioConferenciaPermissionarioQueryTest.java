package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.relatorio.RelatorioConferenciaPermissionarioQuery;

public class RelatorioConferenciaPermissionarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        RelatorioConferenciaPermissionarioQuery query = new RelatorioConferenciaPermissionarioQuery();
        query.setCriterios(criterios);

        query.csaCodigo = "267";
        query.echCodigo = "123";
        query.posCodigo = "123";
        query.trsCodigo = "123";

        executarConsulta(query);
    }
}

