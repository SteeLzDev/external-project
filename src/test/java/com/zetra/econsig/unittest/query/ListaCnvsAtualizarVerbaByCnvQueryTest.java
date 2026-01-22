package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.convenio.ListaCnvsAtualizarVerbaByCnvQuery;

public class ListaCnvsAtualizarVerbaByCnvQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaCnvsAtualizarVerbaByCnvQuery query = new ListaCnvsAtualizarVerbaByCnvQuery();
        query.setCriterios(criterios);


        executarConsulta(query);
    }
}

