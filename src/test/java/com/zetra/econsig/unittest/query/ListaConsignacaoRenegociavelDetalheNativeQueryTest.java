package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRenegociavelDetalheNativeQuery;

public class ListaConsignacaoRenegociavelDetalheNativeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoRenegociavelDetalheNativeQuery query = new ListaConsignacaoRenegociavelDetalheNativeQuery();
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.servidorPossuiAde = true;
        query.adePossuiSolicitacaoSaldoLiq = true;
        query.adeCodigos = java.util.List.of("1", "2");
        query.tipoOperacao = "123";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.ignoraParamRestTaxaMenor = true;
        query.fixaServico = true;
        query.adeSuspensas = true;

        executarConsulta(query);
    }
}

