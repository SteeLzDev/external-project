package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.relatorio.RelatorioContratoLiquidadoPosCorteQuery;
import com.zetra.econsig.values.Columns;

public class RelatorioContratoLiquidadoPosCorteQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();
        criterios.setAttribute(Columns.SVC_CODIGO, List.of("050E8080808080808080808080808280", "1"));
        criterios.setAttribute(Columns.ORG_CODIGO, List.of("751F8080808080808080808080809780", "1001808080808080808080808080017B"));
        criterios.setAttribute(Columns.CSA_CODIGO, "267");
        criterios.setAttribute("PERIODO", "2023-01-01");
        criterios.setAttribute("ORDEM", Boolean.TRUE);

        RelatorioContratoLiquidadoPosCorteQuery query = new RelatorioContratoLiquidadoPosCorteQuery();
        query.setCriterios(criterios);

        executarConsulta(query);
    }



}
