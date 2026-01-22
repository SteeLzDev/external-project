package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioServicoOperacaoMesQuery;
import com.zetra.econsig.values.CodedValues;

public class RelatorioServicoOperacaoMesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("PERIODO", "2023-01-01");
        criterio.setAttribute("DATA_INI", "2023-01-01 00:00:00");
        criterio.setAttribute("DATA_FIM", "2023-01-01 23:59:59");
        criterio.setAttribute("SVC_CODIGO", List.of("050E8080808080808080808080808280", "1"));
        criterio.setAttribute("NSE_CODIGO", List.of(CodedValues.NSE_EMPRESTIMO, CodedValues.NSE_AUXILIO_FINANCEIRO));
        criterio.setAttribute("OPERACAO_POR_CSA", Boolean.TRUE);

        RelatorioServicoOperacaoMesQuery query = new RelatorioServicoOperacaoMesQuery();
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}


