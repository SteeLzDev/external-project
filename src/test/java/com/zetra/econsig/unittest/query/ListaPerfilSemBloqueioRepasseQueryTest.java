package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.perfil.ListaPerfilSemBloqueioRepasseQuery;

public class ListaPerfilSemBloqueioRepasseQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPerfilSemBloqueioRepasseQuery query = new ListaPerfilSemBloqueioRepasseQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";
        query.usuCodigoEdt = "123";
        query.papCodigoOrigem = "1";
        query.papCodigoDestino = "1";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

