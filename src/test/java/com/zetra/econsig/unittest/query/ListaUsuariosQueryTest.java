package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.usuario.ListaUsuariosQuery;
import com.zetra.econsig.values.CodedValues;

public class ListaUsuariosQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaUsuariosQuery query = new ListaUsuariosQuery();
        query.usuLogin = "123";
        query.usuNome = "123";
        query.usuCodigo = "123";
//        query.cseNome = "123";
//        query.orgNome = "123";
//        query.csaNome = "123";
//        query.csaNomeAbrev = "123";
//        query.corNome = "123";
//        query.usuCseUsuCodigo = "123";
//        query.usuOrgUsuCodigo = "123";
//        query.usuCsaUsuCodigo = "123";
//        query.usuCorUsuCodigo = "123";
        query.stuCodigo = CodedValues.STU_ATIVO;
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.ocultaUsuVisivel = true;
        query.count = false;
        query.entCodigo = "1";
        query.tipo = AcessoSistema.ENTIDADE_SUP;

        executarConsulta(query);
    }
}

