package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.retorno.ListaHistoricoConclusaoRetornoQuery;

public class ListaHistoricoConclusaoRetornoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        int qtdeMesesPesquisa = 1;

        ListaHistoricoConclusaoRetornoQuery query = new ListaHistoricoConclusaoRetornoQuery(qtdeMesesPesquisa);
        query.count = false;
        query.orgCodigo = "751F8080808080808080808080809780";
        query.periodo = "2023-01-01";
        query.qtdeMesesPesquisa = 1;

        executarConsulta(query);
    }
}

