package com.zetra.econsig.unittest.query;

import org.junit.jupiter.api.Test;

import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

public class ListaConsignacaoQueryTest extends AbstractQueryTest {

    @Test
    public void test_01() throws com.zetra.econsig.exception.ZetraException {
        com.zetra.econsig.helper.seguranca.AcessoSistema responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        ListaConsignacaoQuery query = new ListaConsignacaoQuery(responsavel);
        query.tipo = "CSE";
        query.codigo = "1";
        query.tipoOperacao = "consultar";
        query.rseCodigo = "123";
        query.adeCodigo = "731A8D1EAZ564668A4Z0004423D9A1BD";
        query.adeNumero = java.util.List.of(1l, 2l);
        query.adeIdentificador = java.util.List.of("1", "2");
        query.serCpf = "123";
        query.rseMatricula = "123";
        query.tpsCodigo = "123";
        query.tgcCodigo = "123";
        query.csaCodigo = "267";
        query.corCodigo = "EF128080808080808080808080809980";
        query.estCodigo = "751F8080808080808080808080809680";
        query.orgCodigo = "751F8080808080808080808080809780";
        query.tmoCodigo = "123";
        query.tgsCodigo = "123";
        query.svcCodigo = "050E8080808080808080808080808280";
        query.cnvCodVerba = "123";
        query.adeAnoMesIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.tipoOcorrenciaPeriodo = "2023-01-01";
        query.infSaldoDevedor = "2";
        query.diasSolicitacaoSaldo = 1;
        query.diasSolicitacaoSaldoPagaAnexo = 1;
        query.periodoIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.adeIntFolha = 1;
        query.adeIncMargem = 1;
        query.adeIndice = "123";
        query.srsCodigo = java.util.List.of(CodedValues.SRS_ATIVO, CodedValues.SRS_BLOQUEADO);
        query.sadCodigos = java.util.List.of(CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMANDAMENTO);
        query.nseCodigos = java.util.List.of(CodedValues.NSE_EMPRESTIMO);
        query.marCodigos = java.util.List.of(CodedValues.INCIDE_MARGEM_SIM, CodedValues.INCIDE_MARGEM_SIM_2);
        query.svcCodigos = java.util.List.of("1", "2");
        query.csaCodigos = java.util.List.of("1", "2");
        query.ocaDataIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.ocaDataFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.decCodigo = "123";
        query.prmCodigo = "123";
        query.planoExclusaoAutomatica = true;
        query.transferencia = true;
        query.dataConciliacao = null;
        query.temAnexoPendenteValidacao = true;
        query.count = false;
        query.adePropria = true;
        query.retornaSomenteAdeCodigo = true;
        query.arquivado = true;
        query.tipoOrdenacao = "123";
        query.ordenacao = "ORD01;ASC";
        query.validarInexistenciaConfLiquidacao = true;
        query.validarInexistenciaConfLiquidacaoRelacionamento = true;
        query.usaModuloBeneficio = true;
        query.tntCodigos = java.util.List.of("1", "2");
        query.existeTipoOcorrencias = java.util.List.of("1", "2");
        //query.listaTipoSolicitacaoSaldo = java.util.List.of("1", "2");
        query.operacaoSOAPEditarSaldoDevedor = true;

        executarConsulta(query);
    }


    @Test
    public void test_02() throws com.zetra.econsig.exception.ZetraException {
        com.zetra.econsig.helper.seguranca.AcessoSistema responsavel = com.zetra.econsig.helper.seguranca.AcessoSistema.recuperaAcessoSistemaByLogin("cse", getLoopbackAddress(), null);

        ListaConsignacaoQuery query = new ListaConsignacaoQuery(responsavel);
        query.tipo = "CSE";
        query.codigo = "1";
        query.tipoOperacao = "consultar";
        query.csaCodigo = "267";
        query.infSaldoDevedor = null;
        query.periodoIni = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.periodoFim = com.zetra.econsig.helper.texto.DateHelper.getSystemDate();
        query.srsCodigo = java.util.List.of(CodedValues.SRS_ATIVO, CodedValues.SRS_BLOQUEADO);
        query.sadCodigos = java.util.List.of(CodedValues.SAD_DEFERIDA, CodedValues.SAD_EMANDAMENTO);
        query.nseCodigos = java.util.List.of(CodedValues.NSE_EMPRESTIMO);
        query.marCodigos = java.util.List.of(CodedValues.INCIDE_MARGEM_SIM, CodedValues.INCIDE_MARGEM_SIM_2);
        query.tipoOrdenacao = "123";
        query.ordenacao = "ORD01;ASC";
        query.listaTipoSolicitacaoSaldo = java.util.List.of(TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR.getCodigo(), TipoSolicitacaoEnum.SOLICITACAO_SALDO_DEVEDOR_LIQUIDACAO.getCodigo());

        executarConsulta(query);
    }
}

