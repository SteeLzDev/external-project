package com.zetra.econsig.unittest.query;

import com.zetra.econsig.persistence.query.parametro.ListaAdeRefinanciamentoQuery;
import org.junit.jupiter.api.Test;

public class ListaAdeRefinanciamentoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAdeRefinanciamentoQuery query = new ListaAdeRefinanciamentoQuery();
        query.csaCodigo = "123";

        executarConsulta(query);
    }



}
