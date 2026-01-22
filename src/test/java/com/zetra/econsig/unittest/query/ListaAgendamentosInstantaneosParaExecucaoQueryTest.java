package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.agendamento.ListaAgendamentosInstantaneosParaExecucaoQuery;

public class ListaAgendamentosInstantaneosParaExecucaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaAgendamentosInstantaneosParaExecucaoQuery query = new ListaAgendamentosInstantaneosParaExecucaoQuery();
        query.setCriterios(criterios);


        executarConsulta(query);
    }
}

