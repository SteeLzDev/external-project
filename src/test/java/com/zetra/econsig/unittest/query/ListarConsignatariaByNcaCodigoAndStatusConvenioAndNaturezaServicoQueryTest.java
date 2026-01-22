package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.beneficios.ListarConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServicoQuery;

public class ListarConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServicoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListarConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServicoQuery query = new ListarConsignatariaByNcaCodigoAndStatusConvenioAndNaturezaServicoQuery();
        query.ncaCodigo = "1";
        query.scvCodigo = "123";
        query.nseCodigo = "123";

        executarConsulta(query);
    }
}

