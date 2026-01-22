package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.compra.ListaCsaComOcorrenciaPendenciaCompraQuery;

public class ListaCsaComOcorrenciaPendenciaCompraQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCsaComOcorrenciaPendenciaCompraQuery query = new ListaCsaComOcorrenciaPendenciaCompraQuery();

        executarConsulta(query);
    }
}

