package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioComssionamentoAgenciamentoAnaliticoQuery;

public class RelatorioComssionamentoAgenciamentoAnaliticoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        RelatorioComssionamentoAgenciamentoAnaliticoQuery query = new RelatorioComssionamentoAgenciamentoAnaliticoQuery();
        query.periodo = "2023-01-01";
        query.csaCogido = "123";
        query.orgaos = java.util.List.of("1", "2");
        query.agenciamento = true;
        query.percentualAgenciamento = "123";
        query.nseCodigo = "123";
        query.benCodigo = "123";

        executarConsulta(query);
    }
}

