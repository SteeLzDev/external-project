package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalValorConsignacaoQuery;

public class ObtemTotalValorConsignacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalValorConsignacaoQuery query = new ObtemTotalValorConsignacaoQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.adeIncMargem = 1;
        query.sadCodigos = java.util.List.of("1", "2");
        query.adeCodigosExceto = java.util.List.of("1", "2");
        query.tratamentoEspecialMargem = true;

        executarConsulta(query);
    }
}

