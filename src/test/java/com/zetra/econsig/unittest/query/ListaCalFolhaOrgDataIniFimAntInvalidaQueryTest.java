package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaOrgDataIniFimAntInvalidaQuery;

public class ListaCalFolhaOrgDataIniFimAntInvalidaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalFolhaOrgDataIniFimAntInvalidaQuery query = new ListaCalFolhaOrgDataIniFimAntInvalidaQuery();
        query.orgCodigo = "751F8080808080808080808080809780";

        executarConsulta(query);
    }
}

