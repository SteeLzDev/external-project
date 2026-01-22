package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.usuario.ListaUsuariosEntidadeQuery;

public class ListaUsuariosEntidadeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuariosEntidadeQuery query = new ListaUsuariosEntidadeQuery();
        query.count = false;
        query.tipo = AcessoSistema.ENTIDADE_CSE;
        query.entCodigo = "1";
        query.perCodigo = "123";
        query.perDescricao = "123";
        query.stuCodigo = null;
        query.usuCodigo = "123";
        query.usuLogin = "123";
        query.usuNome = "123";
        query.usuEmail = "123";
        query.ocultaUsuVisivel = true;

        executarConsulta(query);
    }
}

