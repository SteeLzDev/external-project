package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaOrgAjustesQuery;

public class ListaCalFolhaOrgAjustesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalFolhaOrgAjustesQuery query = new ListaCalFolhaOrgAjustesQuery();
        query.orgCodigo = "751F8080808080808080808080809780";
        query.dataLimite = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

