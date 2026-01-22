package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioBloqueioServidorQuery;

public class RelatorioBloqueioServidorQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("DATA_INI", "2023-01-01 00:00:00");
        criterio.setAttribute("DATA_FIM", "2023-01-01 23:59:59");
        criterio.setAttribute("ORG_CODIGO", List.of("751F8080808080808080808080809680"));
        criterio.setAttribute("SVC_CODIGO", List.of("050E8080808080808080808080808280", "1"));
        criterio.setAttribute("CSA_CODIGO", "267");

        RelatorioBloqueioServidorQuery query = new RelatorioBloqueioServidorQuery();
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}


