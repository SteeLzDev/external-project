package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.margem.ListaMargemRegistroServidoresQuery;

public class ListaMargemRegistroServidoresQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaMargemRegistroServidoresQuery query = new ListaMargemRegistroServidoresQuery();
        query.setCriterios(criterios);

        query.rseCodigo = java.util.List.of("1", "2");
        query.margensComSvcAtivo = true;

        executarConsulta(query);
    }
}

