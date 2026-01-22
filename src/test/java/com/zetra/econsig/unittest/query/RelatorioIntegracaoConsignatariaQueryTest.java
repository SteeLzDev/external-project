package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioIntegracaoConsignatariaQuery;

public class RelatorioIntegracaoConsignatariaQueryTest extends AbstractQueryTest {

    @Disabled("A query não é executada no relatório")
    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("CAMPOS", List.of("CSE_NOME"));

        RelatorioIntegracaoConsignatariaQuery query = new RelatorioIntegracaoConsignatariaQuery();
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}

