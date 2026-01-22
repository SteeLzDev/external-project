package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemContratosAlteradosQuery;

public class ListaExtratoMargemContratosAlteradosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigo = "123";
        java.util.Date dataFimUltPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        ListaExtratoMargemContratosAlteradosQuery query = new ListaExtratoMargemContratosAlteradosQuery(rseCodigo, dataFimUltPeriodo);

        executarConsulta(query);
    }
}

