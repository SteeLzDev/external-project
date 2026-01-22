package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.contracheque.ListaContrachequeQuery;

public class ListaContrachequeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaContrachequeQuery query = new ListaContrachequeQuery();
        query.rseCodigo = "123";
        query.ccqPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.obtemUltimo = true;
        query.ordemDesc = true;
        query.dataInicio = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.dataFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

