package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.coeficiente.ListaTaxasJurosComDataFimVigMaiorQuery;

public class ListaTaxasJurosComDataFimVigMaiorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaTaxasJurosComDataFimVigMaiorQuery query = new ListaTaxasJurosComDataFimVigMaiorQuery();
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.cftDataFimVig = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

