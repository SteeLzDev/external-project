package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.historico.ListarHistoricoServidorQuery;

public class ListarHistoricoServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarHistoricoServidorQuery query = new ListarHistoricoServidorQuery();
        query.rseMatricula = "123";
        query.serCpf = "123";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.estCodigo = "751F8080808080808080808080809680";

        executarConsulta(query);
    }
}

