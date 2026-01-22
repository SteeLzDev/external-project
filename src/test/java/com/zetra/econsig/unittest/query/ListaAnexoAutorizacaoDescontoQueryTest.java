package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.anexo.ListaAnexoAutorizacaoDescontoQuery;

public class ListaAnexoAutorizacaoDescontoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaAnexoAutorizacaoDescontoQuery query = new ListaAnexoAutorizacaoDescontoQuery();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.aadNome = "123";
        query.tarCodigo = "123";
        query.tarCodigos = java.util.List.of("1", "2");
        query.aadAtivo = 1;
        query.count = false;
        query.arquivado = true;

        executarConsulta(query);
    }
}

