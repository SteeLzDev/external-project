package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioReclamacoesQuery;

public class RelatorioReclamacoesQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("DATA_INI", "2023-01-01 00:00:00");
        criterio.setAttribute("DATA_FIM", "2023-01-01 23:59:59");

        RelatorioReclamacoesQuery query = new RelatorioReclamacoesQuery();
        query.setCriterios(criterio);

        executarConsulta(query);
    }
}

