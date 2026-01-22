package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.margem.ListaMargemDisponivelCompulsorioQuery;

public class ListaMargemDisponivelCompulsorioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaMargemDisponivelCompulsorioQuery query = new ListaMargemDisponivelCompulsorioQuery();
        query.setCriterios(criterios);

        query.alteracao = true;
        query.controlaMargem = true;
        query.rseCodigo = "123";
        query.adeIncMargem = 1;
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";

        executarConsulta(query);
    }
}

