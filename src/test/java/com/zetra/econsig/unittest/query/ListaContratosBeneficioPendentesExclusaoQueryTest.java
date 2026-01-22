package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.beneficios.contrato.ListaContratosBeneficioPendentesExclusaoQuery;

public class ListaContratosBeneficioPendentesExclusaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaContratosBeneficioPendentesExclusaoQuery query = new ListaContratosBeneficioPendentesExclusaoQuery();
        query.setCriterios(criterios);

        query.count = false;
        query.csaCodigo = "267";
        query.statusContrato = "123";

        executarConsulta(query);
    }
}

