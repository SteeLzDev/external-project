package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ContaContratosNaoPossuemRelacionamentoVerbaRescisoriaByRseQuery;

public class ContaContratosNaoPossuemRelacionamentoVerbaRescisoriaByRseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ContaContratosNaoPossuemRelacionamentoVerbaRescisoriaByRseQuery query = new ContaContratosNaoPossuemRelacionamentoVerbaRescisoriaByRseQuery();
        query.rseCodigo = "123";
        
        executarConsulta(query);
    }
}

