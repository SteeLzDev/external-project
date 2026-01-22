package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarTabelaCalculoSubsidioQuery;

public class ListarTabelaCalculoSubsidioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarTabelaCalculoSubsidioQuery query = new ListarTabelaCalculoSubsidioQuery();
        query.tipoEntidade = "ORG";
        query.entCodigos = java.util.List.of("1", "2");
        query.simulacao = true;
        query.orgCodigo = "751F8080808080808080808080809780";
        query.benCodigo = "123";

        executarConsulta(query);
    }
}

