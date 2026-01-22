package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.relatorio.RelatorioBeneficioConsolidadoDIRFQuery;

public class RelatorioBeneficioConsolidadoDIRFQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        RelatorioBeneficioConsolidadoDIRFQuery query = new RelatorioBeneficioConsolidadoDIRFQuery();
        query.setCriterios(criterios);

        query.periodo = "2023-01-01";
        query.lstOrgaos = java.util.List.of("1", "2");
        query.lstCsas = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

