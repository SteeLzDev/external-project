package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.extrato.ListaExtratoMargemVlrFolhaPercentualQuery;

public class ListaExtratoMargemVlrFolhaPercentualQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String rseCodigo = "123";
        java.util.Date ultPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        ListaExtratoMargemVlrFolhaPercentualQuery query = new ListaExtratoMargemVlrFolhaPercentualQuery(rseCodigo, ultPeriodo);

        executarConsulta(query);
    }
}

