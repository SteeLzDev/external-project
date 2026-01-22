package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarServidoresCalculoSubsidioQuery;

public class ListarServidoresCalculoSubsidioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarServidoresCalculoSubsidioQuery query = new ListarServidoresCalculoSubsidioQuery();
        query.tipoEntidade = "ORG";
        query.entCodigos = java.util.List.of("1", "2");
        query.srsCodigos = java.util.List.of("1", "2");
        query.servidoresForaFolha = true;

        executarConsulta(query);
    }
}

