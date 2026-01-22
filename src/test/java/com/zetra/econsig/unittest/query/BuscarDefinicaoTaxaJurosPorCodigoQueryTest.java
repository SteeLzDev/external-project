package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.definicaotaxajuros.BuscarDefinicaoTaxaJurosPorCodigoQuery;

public class BuscarDefinicaoTaxaJurosPorCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        BuscarDefinicaoTaxaJurosPorCodigoQuery query = new BuscarDefinicaoTaxaJurosPorCodigoQuery();
        query.dtjCodigo = "123";

        executarConsulta(query);
    }
}

