package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.orgao.ListaOrgaoQuery;

public class ListaOrgaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaOrgaoQuery query = new ListaOrgaoQuery();
        query.orgAtivo = null;
        query.orgIdentificador = "123";
        query.orgNome = "123";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.estIdentificador = "123";
        query.estNome = "123";
        query.estCodigo = "751F8080808080808080808080809680";
        query.orgEstCodigo = "123";
        query.csaCodigo = "267";
        query.count = false;

        executarConsulta(query);
    }
}

