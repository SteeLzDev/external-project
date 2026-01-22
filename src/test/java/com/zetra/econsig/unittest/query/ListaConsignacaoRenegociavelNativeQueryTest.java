package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRenegociavelNativeQuery;

public class ListaConsignacaoRenegociavelNativeQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoRenegociavelNativeQuery query = new ListaConsignacaoRenegociavelNativeQuery();
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

    @Test
    public void tipoOperacaoComprarTest() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoRenegociavelNativeQuery query = new ListaConsignacaoRenegociavelNativeQuery();
        query.adeCodigos = java.util.List.of("1", "2");
        query.tipoOperacao = "comprar";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.ignoraParamRestTaxaMenor = true;
        query.fixaServico = true;
        query.adeSuspensas = true;

        executarConsulta(query);
    }

    @Test
    public void tipoOperacaoRenegociarTest() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoRenegociavelNativeQuery query = new ListaConsignacaoRenegociavelNativeQuery();
        query.adeCodigos = java.util.List.of("1", "2");
        query.tipoOperacao = "renegociar";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.ignoraParamRestTaxaMenor = true;
        query.fixaServico = true;
        query.adeSuspensas = true;

        executarConsulta(query);
    }

    @Test
    public void tipoOperacaoAlongarTest() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoRenegociavelNativeQuery query = new ListaConsignacaoRenegociavelNativeQuery();
        query.adeCodigos = java.util.List.of("1", "2");
        query.tipoOperacao = "alongar";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.ignoraParamRestTaxaMenor = true;
        query.fixaServico = true;
        query.adeSuspensas = true;

        executarConsulta(query);
    }

    @Test
    public void tipoOperacaoSolicitar_portabilidadeTest() throws com.zetra.econsig.exception.ZetraException {

        ListaConsignacaoRenegociavelNativeQuery query = new ListaConsignacaoRenegociavelNativeQuery();
        query.adeCodigos = java.util.List.of("1", "2");
        query.tipoOperacao = "solicitar_portabilidade";
        query.csaCodigo = "267";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);
        query.ignoraParamRestTaxaMenor = true;
        query.fixaServico = true;
        query.adeSuspensas = true;

        executarConsulta(query);
    }
}

