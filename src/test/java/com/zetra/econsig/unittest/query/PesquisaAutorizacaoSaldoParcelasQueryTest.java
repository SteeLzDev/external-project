package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.PesquisaAutorizacaoSaldoParcelasQuery;

public class PesquisaAutorizacaoSaldoParcelasQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        PesquisaAutorizacaoSaldoParcelasQuery query = new PesquisaAutorizacaoSaldoParcelasQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.adeIndice = "123";
        query.svcCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

