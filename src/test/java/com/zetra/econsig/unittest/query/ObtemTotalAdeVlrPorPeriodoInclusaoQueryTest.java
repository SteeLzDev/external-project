package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalAdeVlrPorPeriodoInclusaoQuery;

public class ObtemTotalAdeVlrPorPeriodoInclusaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalAdeVlrPorPeriodoInclusaoQuery query = new ObtemTotalAdeVlrPorPeriodoInclusaoQuery();
        query.rseCodigo = "123";
        query.periodoIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.adeIncMargem = 1;
        query.adeCodigosExclusao = java.util.List.of("1", "2");
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.verificaAdeAnoMesIni = true;

        executarConsulta(query);
    }
}

