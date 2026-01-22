package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.agendamento.ListaAgendamentosParaExecucaoQuery;

public class ListaAgendamentosParaExecucaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaAgendamentosParaExecucaoQuery query = new ListaAgendamentosParaExecucaoQuery();
        query.setCriterios(criterios);


        executarConsulta(query);
    }
}

