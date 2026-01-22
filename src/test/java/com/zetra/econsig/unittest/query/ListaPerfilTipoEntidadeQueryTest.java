package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.perfil.ListaPerfilTipoEntidadeQuery;

public class ListaPerfilTipoEntidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaPerfilTipoEntidadeQuery query = new ListaPerfilTipoEntidadeQuery();
        query.tipoEntidade = "ORG";
        query.codigoEntidade = "123";
        query.pceAtivo = 1;
        query.pcaAtivo = 1;
        query.porAtivo = 1;
        query.pcoAtivo = 1;
        query.psuAtivo = 1;
        query.perDescricao = "123";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

