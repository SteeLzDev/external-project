package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.servidor.ListaOcorrenciaSerUnionRseQuery;

public class ListaOcorrenciaSerUnionRseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaOcorrenciaSerUnionRseQuery query = new ListaOcorrenciaSerUnionRseQuery();
        query.setCriterios(criterios);

        query.count = false;
        query.serCodigo = "123";
        query.tocCodigo = "123";
        query.tocCodigoRse = "123";
        query.rseCodigo = "123";

        executarConsulta(query);
    }
}

