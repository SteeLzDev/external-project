package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.mensagem.PesquisaMensagemQuery;

public class PesquisaMensagemQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        PesquisaMensagemQuery query = new PesquisaMensagemQuery();
        query.count = false;
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.menExigeLeitura = "123";
        query.menDataMinima = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.naoConfirmadas = true;
        query.qtdeMesExpirarMsg = 1;

        executarConsulta(query);
    }
}

