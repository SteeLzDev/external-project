package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarBeneficioByCsaCodigoAndCorCodigosAndNaturezaServicoQuery;

public class ListarBeneficioByCsaCodigoAndCorCodigosAndNaturezaServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarBeneficioByCsaCodigoAndCorCodigosAndNaturezaServicoQuery query = new ListarBeneficioByCsaCodigoAndCorCodigosAndNaturezaServicoQuery();
        query.csaCodigo = "267";
        query.corCodigos = java.util.List.of("1", "2");
        query.nseCodigo = "123";

        executarConsulta(query);
    }
}

