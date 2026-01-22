package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServicoQuery;

public class ListarBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServicoQuery query = new ListarBeneficioByCsaCodigoAndNaturezaServicoAndRelacionamentoServicoQuery();
        query.csaCodigo = "267";
        query.nseCodigo = "123";
        query.benAtivo = true;

        executarConsulta(query);
    }
}

