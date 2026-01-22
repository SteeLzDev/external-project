package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioConsignatariasQuery;

public class RelatorioConsignatariasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioConsignatariasQuery query = new RelatorioConsignatariasQuery();
        query.nseCodigos = java.util.List.of("1", "2");
        query.csaAtivo = null;
        query.possuiAdeAtiva = true;
        query.permiteIncluirAde = true;

        executarConsulta(query);
    }
}

