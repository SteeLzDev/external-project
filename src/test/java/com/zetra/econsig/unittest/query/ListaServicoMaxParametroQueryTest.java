package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.servico.ListaServicoMaxParametroQuery;

public class ListaServicoMaxParametroQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaServicoMaxParametroQuery query = new ListaServicoMaxParametroQuery();
        query.tpsCodigo = "123";
        query.nseCodigo = "123";
        query.ativos = true;

        executarConsulta(query);
    }
}

