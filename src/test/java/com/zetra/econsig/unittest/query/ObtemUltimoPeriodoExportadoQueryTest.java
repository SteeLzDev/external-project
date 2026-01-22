package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.periodo.ObtemUltimoPeriodoExportadoQuery;

public class ObtemUltimoPeriodoExportadoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ObtemUltimoPeriodoExportadoQuery query = new ObtemUltimoPeriodoExportadoQuery();
        query.orgCodigos = java.util.List.of("1", "2");
        query.estCodigos = java.util.List.of("1", "2");
        query.temRetorno = true;
        query.dataIniFimDistintas = true;
        query.periodo = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();

        executarConsulta(query);
    }
}

