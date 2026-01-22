package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.proposta.VerificaAusenciaPropostasPgtDivQuery;

public class VerificaAusenciaPropostasPgtDivQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        VerificaAusenciaPropostasPgtDivQuery query = new VerificaAusenciaPropostasPgtDivQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";

        executarConsulta(query);
    }
}

