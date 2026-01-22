package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.folha.ListarCodigosCsaComConvenioAtivoQuery;

public class ListarCodigosCsaComConvenioAtivoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        ListarCodigosCsaComConvenioAtivoQuery query = new ListarCodigosCsaComConvenioAtivoQuery();
        query.tipoEntidade = "CSE";
        query.codigoEntidade = "1";

        executarConsulta(query);
    }

    @Test
    public void test_02() throws com.zetra.econsig.exception.ZetraException {
        ListarCodigosCsaComConvenioAtivoQuery query = new ListarCodigosCsaComConvenioAtivoQuery();
        query.tipoEntidade = "EST";
        query.codigoEntidade = "001";

        executarConsulta(query);
    }

    @Test
    public void test_03() throws com.zetra.econsig.exception.ZetraException {
        ListarCodigosCsaComConvenioAtivoQuery query = new ListarCodigosCsaComConvenioAtivoQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";

        executarConsulta(query);
    }
}

