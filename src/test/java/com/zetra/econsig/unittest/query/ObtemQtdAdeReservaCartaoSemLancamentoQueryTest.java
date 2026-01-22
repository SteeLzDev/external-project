package com.zetra.econsig.unittest.query;

import com.zetra.econsig.persistence.query.consignacao.ObtemQtdAdeReservaCartaoSemLancamentoQuery;
import org.junit.jupiter.api.Test;

public class ObtemQtdAdeReservaCartaoSemLancamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        final ObtemQtdAdeReservaCartaoSemLancamentoQuery query = new ObtemQtdAdeReservaCartaoSemLancamentoQuery();
        query.csaCodigo = "267";

        executarConsulta(query);
    }
}
