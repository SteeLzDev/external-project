package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasEntreQuery;

public class ObtemTotalParcelasEntreQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalParcelasEntreQuery query = new ObtemTotalParcelasEntreQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.periodoIni = "2023-01-01";
        query.periodoFim = "2023-01-01";
        query.relatorio = true;

        executarConsulta(query);
    }
}

