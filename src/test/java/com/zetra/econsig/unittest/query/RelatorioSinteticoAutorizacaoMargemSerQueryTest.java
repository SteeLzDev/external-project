package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioSinteticoAutorizacaoMargemSerQuery;
import com.zetra.econsig.values.Columns;

public class RelatorioSinteticoAutorizacaoMargemSerQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        final TransferObject criterios = new CustomTransferObject();
        criterios.setAttribute(Columns.CSA_CODIGO, List.of("267", "C88E808080808080808080808888F01A"));

        final RelatorioSinteticoAutorizacaoMargemSerQuery query = new RelatorioSinteticoAutorizacaoMargemSerQuery();
        query.setCriterios(criterios);

        executarConsulta(query);
    }
}


