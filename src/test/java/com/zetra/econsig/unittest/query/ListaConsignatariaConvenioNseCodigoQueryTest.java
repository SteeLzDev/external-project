package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignataria.ListaConsignatariaConvenioNseCodigoQuery;

public class ListaConsignatariaConvenioNseCodigoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignatariaConvenioNseCodigoQuery query = new ListaConsignatariaConvenioNseCodigoQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.nseCodigo = "123";

        executarConsulta(query);
    }
}

