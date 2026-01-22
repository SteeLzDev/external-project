package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConvenioEntidadeQuery;

public class ListaConvenioEntidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConvenioEntidadeQuery query = new ListaConvenioEntidadeQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";
        query.operacao = "123";

        executarConsulta(query);
    }
}

