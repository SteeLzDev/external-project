package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioPrdPagasPorCsaPeriodoQuery;
import com.zetra.econsig.values.Columns;

public class RelatorioPrdPagasPorCsaPeriodoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();
        criterios.setAttribute(Columns.EST_CODIGO, "751F8080808080808080808080809680");
        criterios.setAttribute(Columns.ORG_CODIGO, List.of("1001808080808080808080808080FD69", "1001808080808080808080808080FF44", "751F8080808080808080808080809780"));
        criterios.setAttribute(Columns.CSA_CODIGO, "267");
        criterios.setAttribute(Columns.CSA_ATIVO, Boolean.TRUE);

        RelatorioPrdPagasPorCsaPeriodoQuery query = new RelatorioPrdPagasPorCsaPeriodoQuery();
        query.setCriterios(criterios);

        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

