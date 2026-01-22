package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioTransfereAdeQuery;
import com.zetra.econsig.values.CodedValues;

public class RelatorioTransfereAdeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("csaCodigoOrigem", "267");
        criterio.setAttribute("csaCodigoDestino", "267");
        criterio.setAttribute("svcCodigoOrigem", "050E8080808080808080808080808280");
        criterio.setAttribute("svcCodigoDestino", "1");
        criterio.setAttribute("orgCodigo", "751F8080808080808080808080809780");
        criterio.setAttribute("sadCodigo", CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO);
        criterio.setAttribute("periodoIni", "2023-01-01 00:00:00");
        criterio.setAttribute("periodoFim", "2023-01-01 23:59:59");
        criterio.setAttribute("adeNumero", List.of(1l, 2l));
        criterio.setAttribute("rseMatricula", "123");
        criterio.setAttribute("serCpf", "123");
        criterio.setAttribute("somenteConveniosAtivos", Boolean.TRUE);

        RelatorioTransfereAdeQuery query = new RelatorioTransfereAdeQuery();
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}


