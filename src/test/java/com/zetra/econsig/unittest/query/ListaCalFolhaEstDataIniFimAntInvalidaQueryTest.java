package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.calendario.ListaCalFolhaEstDataIniFimAntInvalidaQuery;

public class ListaCalFolhaEstDataIniFimAntInvalidaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCalFolhaEstDataIniFimAntInvalidaQuery query = new ListaCalFolhaEstDataIniFimAntInvalidaQuery();
        query.estCodigo = "751F8080808080808080808080809680";

        executarConsulta(query);
    }
}

