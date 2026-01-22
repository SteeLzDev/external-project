package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.CustomTransferObject;

import com.zetra.econsig.persistence.query.beneficios.contrato.ListaContratosBeneficiosRelacionamentoMigracaoOrigemQuery;

public class ListaContratosBeneficiosRelacionamentoMigracaoOrigemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        TransferObject criterios = new CustomTransferObject();

        ListaContratosBeneficiosRelacionamentoMigracaoOrigemQuery query = new ListaContratosBeneficiosRelacionamentoMigracaoOrigemQuery();
        query.setCriterios(criterios);

        query.cbeCodigo = "123";

        executarConsulta(query);
    }
}

