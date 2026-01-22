package com.zetra.econsig.unittest.query;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.persistence.query.servidor.ListaServidorCadastroQuery;
import com.zetra.econsig.values.Columns;

public class ListaServidorCadastroQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        TransferObject excecao = new CustomTransferObject();
        excecao.setAttribute(Columns.SER_CODIGO, "111122");

        ListaServidorCadastroQuery query = new ListaServidorCadastroQuery();
        query.count = false;
        query.excecoes = List.of(excecao);
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

