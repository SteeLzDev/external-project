package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.AlteracaoADEValorRetornoQuery;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.values.Columns;

public class AlteracaoADEValorRetornoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(ReportManager.PARAM_NAME_PERIODO, "2023-01-01");
        criterio.setAttribute(Columns.CNV_CSA_CODIGO, "267");
        criterio.setAttribute(Columns.CNV_ORG_CODIGO, List.of("751F8080808080808080808080809780", "1"));
        criterio.setAttribute(Columns.CNV_SVC_CODIGO, List.of("050E8080808080808080808080808280", "2"));

        AlteracaoADEValorRetornoQuery query = new AlteracaoADEValorRetornoQuery();
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}


