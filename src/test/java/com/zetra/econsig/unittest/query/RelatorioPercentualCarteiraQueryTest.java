package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.relatorio.RelatorioPercentualCarteiraQuery;
import com.zetra.econsig.values.CodedValues;

public class RelatorioPercentualCarteiraQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String dataIni = "2023-01-01 00:00:00";
        String dataFim = "2023-01-01 23:59:59";
        List<String> svcCodigos = List.of("050E8080808080808080808080808280", "1");
        List<String> orgCodigo = List.of("751F8080808080808080808080809780", "1");
        List<String> origensAdes = List.of(CodedValues.ORIGEM_ADE_NOVA);
        String campo = "QTDE";

        RelatorioPercentualCarteiraQuery query = new RelatorioPercentualCarteiraQuery(dataIni, dataFim, svcCodigos, orgCodigo, origensAdes, campo);
        executarConsulta(query);
    }
}


