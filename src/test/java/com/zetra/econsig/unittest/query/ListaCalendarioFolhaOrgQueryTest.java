package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalendarioFolhaOrgQuery;

public class ListaCalendarioFolhaOrgQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalendarioFolhaOrgQuery query = new ListaCalendarioFolhaOrgQuery();
        query.cfoPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.anoPeriodo = 1;
        query.orgCodigo = "751F8080808080808080808080809780";
        query.cfoDataFimMaiorQue = "2023-01-01 01:01:01";
        query.cfoPeriodoMaiorQueIgual = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.count = false;

        executarConsulta(query);
    }
}

