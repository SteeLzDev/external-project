package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.agendamento.ListaAgendamentosQuery;

public class ListaAgendamentosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        java.util.List<java.lang.String> agdCodigos = java.util.List.of("1", "2");
        java.util.List<java.lang.String> sagCodigos = java.util.List.of("1", "2");
        java.util.List<java.lang.String> tagCodigos = java.util.List.of("1", "2");
        String classe = "123";
        String tipoEntidade = "ORG";
        String codigoEntidade = "123";
        String usuCodigo = "123";
        String relCodigo = "123";

        ListaAgendamentosQuery query = new ListaAgendamentosQuery(agdCodigos, sagCodigos, tagCodigos, classe, tipoEntidade, codigoEntidade, usuCodigo, relCodigo);
        query.count = false;

        executarConsulta(query);
    }
}

