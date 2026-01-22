package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.parametro.ListaParamOrgaoEditavelQuery;

public class ListaParamOrgaoEditavelQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        String orgCodigo = "751F8080808080808080808080809780";
        com.zetra.econsig.helper.seguranca.AcessoSistema responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        ListaParamOrgaoEditavelQuery query = new ListaParamOrgaoEditavelQuery(orgCodigo, responsavel);

        executarConsulta(query);
    }
}

