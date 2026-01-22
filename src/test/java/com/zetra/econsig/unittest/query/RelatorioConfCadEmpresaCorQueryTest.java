package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioConfCadEmpresaCorQuery;

public class RelatorioConfCadEmpresaCorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioConfCadEmpresaCorQuery query = new RelatorioConfCadEmpresaCorQuery();
        query.csaCodigo = "267";
        query.ecoCodigo = "123";

        executarConsulta(query);
    }
}

