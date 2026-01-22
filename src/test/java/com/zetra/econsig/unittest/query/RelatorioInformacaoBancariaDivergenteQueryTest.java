package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.relatorio.RelatorioInformacaoBancariaDivergenteQuery;

public class RelatorioInformacaoBancariaDivergenteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        RelatorioInformacaoBancariaDivergenteQuery query = new RelatorioInformacaoBancariaDivergenteQuery();
        query.periodoIni = DateHelper.getSystemDatetime();
        query.periodoFim = DateHelper.getSystemDatetime();
        query.csaCodigo = "267";
        query.orgCodigos = List.of("751F8080808080808080808080809780", "1");
        query.svcCodigo = List.of("050E8080808080808080808080808280", "1");

        executarConsulta(query);
    }
}


