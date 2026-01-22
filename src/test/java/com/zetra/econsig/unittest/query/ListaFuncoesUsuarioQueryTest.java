package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.usuario.ListaFuncoesUsuarioQuery;

public class ListaFuncoesUsuarioQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaFuncoesUsuarioQuery query = new ListaFuncoesUsuarioQuery();
        query.tipo = AcessoSistema.ENTIDADE_CSE;
        query.usuCodigo = "123";
        query.papCodigoDestino = "1";
        query.papCodigoOrigem = "1";

        executarConsulta(query);
    }
}

