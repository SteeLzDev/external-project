package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.sdp.despesaindividual.ListaDespesaTaxaUsoAtualizacaoQuery;

public class ListaDespesaTaxaUsoAtualizacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaDespesaTaxaUsoAtualizacaoQuery query = new ListaDespesaTaxaUsoAtualizacaoQuery();
        query.posCodigo = "123";
        query.prmCodigo = "123";
        query.rseCodigo = "123";
        query.echCondominio = true;
        query.echCodigo = "123";

        executarConsulta(query);
    }
}

