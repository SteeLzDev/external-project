package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicosRenegociaveisServidorQuery;

public class ListaServicosRenegociaveisServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicosRenegociaveisServidorQuery query = new ListaServicosRenegociaveisServidorQuery();
        query.orgCodigo = "751F8080808080808080808080809780";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.nseCodigo = "123";
        query.ativos = true;
        query.cftDia = 1;

        executarConsulta(query);
    }
}

