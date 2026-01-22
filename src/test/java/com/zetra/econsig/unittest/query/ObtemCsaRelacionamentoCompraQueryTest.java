package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.compra.ObtemCsaRelacionamentoCompraQuery;

public class ObtemCsaRelacionamentoCompraQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemCsaRelacionamentoCompraQuery query = new ObtemCsaRelacionamentoCompraQuery();
        query.adeCodigoOrigem = "123";
        query.adeCodigoDestino = "123";
        query.stcCodigo = "123";

        executarConsulta(query);
    }
}

