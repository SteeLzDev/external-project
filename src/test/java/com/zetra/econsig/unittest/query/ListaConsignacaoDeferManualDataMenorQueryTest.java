package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoDeferManualDataMenorQuery;

public class ListaConsignacaoDeferManualDataMenorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoDeferManualDataMenorQuery query = new ListaConsignacaoDeferManualDataMenorQuery();
        query.rseCodigo = "123";
        query.adeDataContrato = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

