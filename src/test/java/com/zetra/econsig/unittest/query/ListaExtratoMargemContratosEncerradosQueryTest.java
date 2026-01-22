package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemContratosEncerradosQuery;

public class ListaExtratoMargemContratosEncerradosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigo = "123";
        java.util.Date ultPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        java.util.Date dataFimUltPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        ListaExtratoMargemContratosEncerradosQuery query = new ListaExtratoMargemContratosEncerradosQuery(rseCodigo, ultPeriodo, dataFimUltPeriodo);

        executarConsulta(query);
    }
}

