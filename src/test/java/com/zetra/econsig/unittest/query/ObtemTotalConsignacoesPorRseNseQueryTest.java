package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ObtemTotalConsignacoesPorRseNseQuery;

public class ObtemTotalConsignacoesPorRseNseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemTotalConsignacoesPorRseNseQuery query = new ObtemTotalConsignacoesPorRseNseQuery();
        query.rseCodigo = "123";
        query.nseCodigo = "123";
        query.csaCodigo = "267";
        query.sadCodigos = java.util.List.of("1", "2");
        query.adeAnoMesIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

