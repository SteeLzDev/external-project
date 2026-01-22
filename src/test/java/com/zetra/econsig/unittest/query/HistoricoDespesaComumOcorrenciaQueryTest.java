package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.historico.HistoricoDespesaComumOcorrenciaQuery;

public class HistoricoDespesaComumOcorrenciaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        HistoricoDespesaComumOcorrenciaQuery query = new HistoricoDespesaComumOcorrenciaQuery();
        query.decCodigo = "123";

        executarConsulta(query);
    }
}

