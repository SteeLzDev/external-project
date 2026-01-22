package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.sdp.permissionario.ListaHistoricoPermissionarioQuery;

public class ListaHistoricoPermissionarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaHistoricoPermissionarioQuery query = new ListaHistoricoPermissionarioQuery();
        query.setCriterios(criterios);

        query.prmCodigo = "123";
        query.count = false;

        executarConsulta(query);
    }
}

