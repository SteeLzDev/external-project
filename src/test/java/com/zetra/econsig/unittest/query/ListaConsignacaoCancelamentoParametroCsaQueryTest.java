package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoCancelamentoParametroCsaQuery;

public class ListaConsignacaoCancelamentoParametroCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoCancelamentoParametroCsaQuery query = new ListaConsignacaoCancelamentoParametroCsaQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.sadCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

