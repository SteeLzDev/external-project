package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoConciliacaoQuery;

public class ListaConsignacaoConciliacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoConciliacaoQuery query = new ListaConsignacaoConciliacaoQuery();
        query.orgIdentificador = "123";
        query.periodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.cpf = java.util.List.of("1", "2");
        query.adeNumero = java.util.List.of(1l, 2l);
        query.adeIdentificador = java.util.List.of("1", "2");
        query.statusPagamento = "123";

        executarConsulta(query);
    }
}

