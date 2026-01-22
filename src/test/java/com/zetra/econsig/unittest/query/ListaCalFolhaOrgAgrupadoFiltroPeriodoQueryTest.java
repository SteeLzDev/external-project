package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaOrgAgrupadoFiltroPeriodoQuery;

public class ListaCalFolhaOrgAgrupadoFiltroPeriodoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalFolhaOrgAgrupadoFiltroPeriodoQuery query = new ListaCalFolhaOrgAgrupadoFiltroPeriodoQuery();
        query.orgCodigo = "751F8080808080808080808080809780";
        query.periodoInicio = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

