package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarBeneficiariosRemocaoSubsidioQuery;

public class ListarBeneficiariosRemocaoSubsidioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarBeneficiariosRemocaoSubsidioQuery query = new ListarBeneficiariosRemocaoSubsidioQuery();
        query.dataCalculoSubsidio = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.tipoEntidade = "ORG";
        query.entCodigos = java.util.List.of("1", "2");

        executarConsulta(query);
    }
}

