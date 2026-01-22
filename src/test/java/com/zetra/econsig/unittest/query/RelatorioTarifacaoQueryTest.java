package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioTarifacaoQuery;

public class RelatorioTarifacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("PERIODO", "2023-01-01");
        criterio.setAttribute("CSA_CODIGO", "267");
        criterio.setAttribute("CNV_COD_VERBA", "123");
        criterio.setAttribute("ORG_CODIGO", List.of("751F8080808080808080808080809780"));
        criterio.setAttribute("SVC_CODIGO", List.of("050E8080808080808080808080808280", "1"));
        criterio.setAttribute("POR_MODALIDADE", Boolean.TRUE);
        criterio.setAttribute("TARIFACAO_POR_NATUREZA", Boolean.TRUE);

        RelatorioTarifacaoQuery query = new RelatorioTarifacaoQuery();
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}


