package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.margem.ListaHistoricoComposicaoMargemQuery;

public class ListaHistoricoComposicaoMargemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaHistoricoComposicaoMargemQuery query = new ListaHistoricoComposicaoMargemQuery();
        query.setCriterios(criterios);

        query.strRseCodigo = "123";

        executarConsulta(query);
    }
}

