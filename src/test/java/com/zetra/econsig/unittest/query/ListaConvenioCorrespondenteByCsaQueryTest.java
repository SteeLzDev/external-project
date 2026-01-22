package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConvenioCorrespondenteByCsaQuery;

public class ListaConvenioCorrespondenteByCsaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConvenioCorrespondenteByCsaQuery query = new ListaConvenioCorrespondenteByCsaQuery();
        query.csaCodigo = "267";
        query.filtraPorCnvCodVerbaRef = true;
        query.filtraPorCnvCodVerbaFerias = true;

        executarConsulta(query);
    }
}

