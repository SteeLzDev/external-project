package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.margem.ListaMargemNaturezaServicoQuery;

public class ListaMargemNaturezaServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaMargemNaturezaServicoQuery query = new ListaMargemNaturezaServicoQuery();
        query.setCriterios(criterios);

        query.marCodigo = "123";

        executarConsulta(query);
    }
}

