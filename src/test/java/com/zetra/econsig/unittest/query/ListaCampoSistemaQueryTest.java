package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.texto.ListaCampoSistemaQuery;

public class ListaCampoSistemaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaCampoSistemaQuery query = new ListaCampoSistemaQuery();
        query.casChave = "ser.confirmacaoDadosServidor_cidade";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.somenteCamposEditaveis = true;

        executarConsulta(query);
    }
}

