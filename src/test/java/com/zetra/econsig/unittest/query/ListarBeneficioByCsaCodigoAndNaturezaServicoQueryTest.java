package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarBeneficioByCsaCodigoAndNaturezaServicoQuery;

public class ListarBeneficioByCsaCodigoAndNaturezaServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarBeneficioByCsaCodigoAndNaturezaServicoQuery query = new ListarBeneficioByCsaCodigoAndNaturezaServicoQuery();
        query.csaCodigo = "267";
        query.corCodigo = "EF128080808080808080808080809980";
        query.nseCodigo = "123";

        executarConsulta(query);
    }
}

