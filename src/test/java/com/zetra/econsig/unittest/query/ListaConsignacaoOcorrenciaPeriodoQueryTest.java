package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoOcorrenciaPeriodoQuery;

public class ListaConsignacaoOcorrenciaPeriodoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoOcorrenciaPeriodoQuery query = new ListaConsignacaoOcorrenciaPeriodoQuery();
        query.dataIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.dataFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.csaCodigo = "267";
        query.corCodigo = "EF128080808080808080808080809980";
        query.tocCodigos = java.util.List.of("1", "2");
        query.count = false;
        query.periodoIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.sum = false;
        query.sadCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

