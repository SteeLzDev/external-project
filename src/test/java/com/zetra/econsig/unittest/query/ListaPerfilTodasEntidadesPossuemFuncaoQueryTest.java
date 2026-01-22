package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.funcao.ListaPerfilTodasEntidadesPossuemFuncaoQuery;

public class ListaPerfilTodasEntidadesPossuemFuncaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaPerfilTodasEntidadesPossuemFuncaoQuery query = new ListaPerfilTodasEntidadesPossuemFuncaoQuery();
        query.setCriterios(criterios);

        query.funCodigo = "123";

        executarConsulta(query);
    }
}

