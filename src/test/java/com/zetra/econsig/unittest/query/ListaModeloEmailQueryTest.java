package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.mensagem.ListaModeloEmailQuery;

public class ListaModeloEmailQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaModeloEmailQuery query = new ListaModeloEmailQuery();
        query.memCodigo = "org.enviarEmailUploadArquivoCsa";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

