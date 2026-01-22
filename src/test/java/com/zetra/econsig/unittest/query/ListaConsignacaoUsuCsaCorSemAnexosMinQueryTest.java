package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoUsuCsaCorSemAnexosMinQuery;

public class ListaConsignacaoUsuCsaCorSemAnexosMinQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoUsuCsaCorSemAnexosMinQuery query = new ListaConsignacaoUsuCsaCorSemAnexosMinQuery();
        query.csaCodigo = "267";
        query.dataIniVerificacao = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        executarConsulta(query);
    }
}

