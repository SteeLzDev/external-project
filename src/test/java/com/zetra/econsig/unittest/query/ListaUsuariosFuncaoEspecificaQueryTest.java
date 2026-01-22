package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.usuario.ListaUsuariosFuncaoEspecificaQuery;

public class ListaUsuariosFuncaoEspecificaQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuariosFuncaoEspecificaQuery query = new ListaUsuariosFuncaoEspecificaQuery();
        query.tipo = AcessoSistema.ENTIDADE_CSE;
        query.codigoEntidade = "1";
        query.perCodigo = null;
        query.stuCodigo = null;
        //query.usuCodigo = java.util.List.of("1", "2");
        query.funCodigo = "123";
        query.count = false;

        executarConsulta(query);
    }
}

