package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioIntegracaoMapeamentoMultiploQuery;

public class RelatorioIntegracaoMapeamentoMultiploQueryTest extends AbstractQueryTest {

    @Disabled("A query não é executada no relatório")
    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("CAMPOS", List.of("CSE_NOME"));

        RelatorioIntegracaoMapeamentoMultiploQuery query = new RelatorioIntegracaoMapeamentoMultiploQuery();
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}

