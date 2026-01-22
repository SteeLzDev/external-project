package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.servidor.ListaRegistrosServidoresLikeMatriculaQuery;

public class ListaRegistrosServidoresLikeMatriculaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaRegistrosServidoresLikeMatriculaQuery query = new ListaRegistrosServidoresLikeMatriculaQuery();
        query.setCriterios(criterios);

        query.rseMatricula = "123";

        executarConsulta(query);
    }
}

