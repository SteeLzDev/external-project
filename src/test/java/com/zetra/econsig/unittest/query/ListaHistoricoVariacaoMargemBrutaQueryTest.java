package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.margem.ListaHistoricoVariacaoMargemBrutaQuery;

public class ListaHistoricoVariacaoMargemBrutaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        int qtdeMesesPesquisa = 1;

        ListaHistoricoVariacaoMargemBrutaQuery query = new ListaHistoricoVariacaoMargemBrutaQuery(qtdeMesesPesquisa);
        query.count = false;
        query.rseCodigo = "123";
        query.marCodigo = 1;
        query.qtdeMesesPesquisa = 1;

        executarConsulta(query);
    }
}

