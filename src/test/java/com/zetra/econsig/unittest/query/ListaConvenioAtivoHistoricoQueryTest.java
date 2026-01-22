package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.convenio.ListaConvenioAtivoHistoricoQuery;

public class ListaConvenioAtivoHistoricoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConvenioAtivoHistoricoQuery query = new ListaConvenioAtivoHistoricoQuery();
        query.nseCodigo = "123";
        query.orgCodigo = "751F8080808080808080808080809780";

        executarConsulta(query);
    }
}

