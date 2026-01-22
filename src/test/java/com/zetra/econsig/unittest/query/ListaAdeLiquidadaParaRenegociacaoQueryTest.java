package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.lote.ListaAdeLiquidadaParaRenegociacaoQuery;

public class ListaAdeLiquidadaParaRenegociacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAdeLiquidadaParaRenegociacaoQuery query = new ListaAdeLiquidadaParaRenegociacaoQuery();
        query.rseCodigo = "123";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.ocaData = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.fixaServico = true;

        executarConsulta(query);
    }
}

