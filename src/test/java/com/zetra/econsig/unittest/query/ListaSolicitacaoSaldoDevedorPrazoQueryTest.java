package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.saldodevedor.ListaSolicitacaoSaldoDevedorPrazoQuery;

public class ListaSolicitacaoSaldoDevedorPrazoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        String tisCodigo = "123";

        ListaSolicitacaoSaldoDevedorPrazoQuery query = new ListaSolicitacaoSaldoDevedorPrazoQuery(adeCodigo, tisCodigo);

        executarConsulta(query);
    }
}

