package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.auditoria.ListaLogAuditoriaQuery;

public class ListaLogAuditoriaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaLogAuditoriaQuery query = new ListaLogAuditoriaQuery();
        query.setCriterios(criterios);

        query.count = false;
        query.codigoEntidade = "123";
        query.tipoEntidade = "ORG";
        query.naoAuditado = true;
        query.criterios = new CustomTransferObject();

        executarConsulta(query);
    }
}

