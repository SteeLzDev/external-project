package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaOrgAgrupadoQuery;

public class ListaCalFolhaOrgAgrupadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalFolhaOrgAgrupadoQuery query = new ListaCalFolhaOrgAgrupadoQuery();
        query.orgCodigo = "751F8080808080808080808080809780";
        query.cfoPeriodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.dataLimite = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

