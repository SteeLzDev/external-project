package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.parametro.ListaContratoLiberaOperacaoByDestinoQuery;

public class ListaContratoLiberaOperacaoByDestinoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaContratoLiberaOperacaoByDestinoQuery query = new ListaContratoLiberaOperacaoByDestinoQuery();
        query.setCriterios(criterios);

        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";

        executarConsulta(query);
    }
}

