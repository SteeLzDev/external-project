package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoPrdRejeitadaQuery;

public class ListaConsignacaoPrdRejeitadaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoPrdRejeitadaQuery query = new ListaConsignacaoPrdRejeitadaQuery();
        query.rseCodigo = "123";
        query.nseCodigo = "123";
        query.csaCodigo = "267";
        query.adeCodigosRenegociacao = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

