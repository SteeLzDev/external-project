package com.zetra.econsig.web.controller.renegociacao;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.dto.web.ColunaListaConsignacao;
import com.zetra.econsig.dto.web.RenegociarConsignacaoModel;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.consignacao.ReservaMargemHelper;
import com.zetra.econsig.helper.criptografia.JCryptOld;
import com.zetra.econsig.helper.margem.ExibeMargem;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamCsa;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.financiamentodivida.FinanciamentoDividaController;
import com.zetra.econsig.service.indice.IndiceController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.AcaoTipoDadoAdicionalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.InformacaoSerCompraEnum;
import com.zetra.econsig.values.MotivoAdeNaoRenegociavelEnum;
import com.zetra.econsig.values.StatusPropostaEnum;
import com.zetra.econsig.values.VisibilidadeTipoDadoAdicionalEnum;
import com.zetra.econsig.web.controller.consignacao.AbstractIncluirConsignacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: RenegociarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso RenegociarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/renegociarConsignacao" })
public class RenegociarConsignacaoWebController extends AbstractIncluirConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RenegociarConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private FinanciamentoDividaController financiamentoDividaController;

    @Autowired
    private IndiceController indiceController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Remove validação de senha realizada anteriormente
        session.removeAttribute("senhaServidorRenegOK");
        session.removeAttribute("senhaServidorOK");
        session.removeAttribute("serAutorizacao");

        if (responsavel.isCseSupOrg()) {
            carregarListaConsignataria(request, session, model, responsavel);
        }

        return super.iniciar(request, response, session, model);
    }

    @Override
    protected void carregarListaServico(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        try {
            final List<TransferObject> lstConvenio = convenioController.lstCnvEntidade(responsavel.getCodigoEntidade(), responsavel.getTipoEntidade(), "renegociar", responsavel);
            final List<TransferObject> lstServico = TextHelper.groupConcat(lstConvenio, new String[]{Columns.SVC_DESCRICAO,Columns.SVC_CODIGO}, new String[]{Columns.CNV_COD_VERBA}, ",", true, true);
            model.addAttribute("lstServico", lstServico);
        } catch (final ConvenioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @RequestMapping(params = { "acao=renegociarConsignacao" })
    protected String renegociarConsignacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        final boolean compra = (model.asMap().get("comprar") != null) ? (Boolean) model.asMap().get("comprar") : false;

        // Se é compra e tem etapa de aprovação de saldo, então e-mail do servidor é obrigatório (ALTERAÇÃO SUSPENSA: CRIAR PARÂMETRO EM VERSÃO FUTURA)
        final boolean exigeEmailServidor = false; // (compra && ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel));

        // Verifica se o sistema está configurado para trabalhar com o CET.
        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
        //Verifica se permite a escolha de periodicidade da folha diferente da que está configurada no sistema
        final boolean permiteEscolherPeriodicidade = ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODICIDADE_FOLHA, CodedValues.TPC_SIM, responsavel);

        // Verifica os contratos selecionados para compra/renegociação
        List<String> adesReneg = null;
        if (request.getParameterValues("chkADE") != null) {
            adesReneg = Arrays.asList(request.getParameterValues("chkADE"));
        } else if (request.getParameter("ADE_CODIGO") != null) {
            adesReneg = new ArrayList<>();
            adesReneg.add(request.getParameter("ADE_CODIGO"));
        }

        if ((adesReneg == null) || adesReneg.isEmpty()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // No caso da compra de contratos e se nao for uma csa recupera o codigo da csa informado
        // csaCodigo; csa_identificador; csa_nome_abrev
        String csaCodigo = !"".equals(JspHelper.verificaVarQryStr(request, "CSA_CODIGO")) ? JspHelper.verificaVarQryStr(request, "CSA_CODIGO").split(";")[0] : "";
        if (responsavel.isCsa()) {
            csaCodigo = responsavel.getCodigoEntidade();
        } else if (responsavel.isCor()) {
            csaCodigo = responsavel.getCodigoEntidadePai();
        }

        // Serviço selecionado para a compra de contratos
        String svcCodigo                = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        TransferObject autdes           = null;
        final List<TransferObject> autdesList = new ArrayList<>();
        try {
            // validaPermissao = !compra -> pois na compra de contrato, o servidor entrou a senha para listar contratos de terceiros
            final List<TransferObject> resultado = pesquisarConsignacaoController.buscaAutorizacao(adesReneg, !compra, responsavel);
            final Iterator<TransferObject> it    = resultado.iterator();
            while (it.hasNext()) {
                autdes = it.next();
                autdes = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) autdes, null, responsavel);
                autdesList.add(autdes);
            }
            autdes = autdesList.get(0);
            if (TextHelper.isNull(svcCodigo)) {
                svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
            }
            if (TextHelper.isNull(csaCodigo) && !responsavel.isCsaCor()) {
                csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
            }
        } catch (final AutorizacaoControllerException ex) {
            final ParamSession paramSession = ParamSession.getParamSession(session);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";
        }

        if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_RENE_CONTRATO, responsavel.getUsuCodigo(), svcCodigo)
                && !AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_COMP_CONTRATO, responsavel.getUsuCodigo(), svcCodigo)) {
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Se só tem uma consignação sendo renegociada, exibe os dados à frente dos campos de entrada
        final boolean exibeVlrAtual         = (autdesList.size() == 1);
        final int limiteIndice              = ((ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel) != null) && !"".equals(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel))) ? Integer.parseInt(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITE_MAX_INDICE, responsavel).toString()) : 99;
        final String orgCodigo              = autdes.getAttribute(Columns.ORG_CODIGO).toString();
        final String rseCodigo              = autdes.getAttribute(Columns.RSE_CODIGO).toString();
        // Parametro que indica se a senha do servidor foi informada e validada
        final boolean senhaServidorOK       = ((session.getAttribute("senhaServidorRenegOK") != null) && session.getAttribute("senhaServidorRenegOK").equals(rseCodigo));
        // Parametro que indica se é processo de financiamento de dívida
        boolean financiamentoDivida   = false;
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)) {
                // Verifica se existe relacionamento de financiamento de dívida entre o serviço do novo contrato "svcCodigo",
                // e o serviço do contrato a ser renegociado/comprado "svcOrigem".
                final String svcOrigem    = autdes.getAttribute(Columns.SVC_CODIGO).toString();
                final List<TransferObject> servicos = parametroController.getRelacionamentoSvc(CodedValues.TNT_FINANCIAMENTO_DIVIDA, svcOrigem, svcCodigo, responsavel);
                financiamentoDivida = ((servicos != null) && !servicos.isEmpty());
            }

            final CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);

            final String serDataNasc = servidor.getAttribute(Columns.SER_DATA_NASC) != null ? DateHelper.reformat(servidor.getAttribute(Columns.SER_DATA_NASC).toString(), "yyyy-MM-dd", LocaleHelper.getDatePattern()) : "";
            final String numBanco    = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_BANCO_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_BANCO_SAL).toString()) : "");
            final String numAgencia  = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString()) : "");
            final String numConta    = servidor.getAttribute(Columns.RSE_CONTA_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_CONTA_SAL).toString()) : "";
            String numConta1   = "";
            String numConta2   = "";

            if (numConta.length() > 0) {
                numConta1 = numConta.substring(0, numConta.length() / 2);
                numConta2 = numConta.substring(numConta.length() / 2, numConta.length());
            } else {
                numConta1 = numConta2 = numConta;
            }

            numConta1 = JCryptOld.crypt("IB", numConta1);
            numConta2 = JCryptOld.crypt("IB", numConta2);

            //Conta salário alternativa
            final String numBancoAlt   = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_BANCO_SAL_2) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_BANCO_SAL_2).toString()) : "");
            final String numAgenciaAlt = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2).toString()) : "");
            final String numContaAlt   = servidor.getAttribute(Columns.RSE_CONTA_SAL_2) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_CONTA_SAL_2).toString()) : "";
            String numContaAlt1  = "";
            String numContaAlt2  = "";

            if (numContaAlt.length() > 0) {
                numContaAlt1 = numContaAlt.substring(0, numContaAlt.length()/2);
                numContaAlt2 = numContaAlt.substring(numContaAlt.length()/2, numContaAlt.length());
            } else {
                numContaAlt1 = numContaAlt2 = numContaAlt;
            }
            numContaAlt1 = JCryptOld.crypt("IB", numContaAlt1);
            numContaAlt2 = JCryptOld.crypt("IB", numContaAlt2);

            final boolean rseTemInfBancaria = ((!TextHelper.isNull(servidor.getAttribute(Columns.RSE_BANCO_SAL)) &&
                    !TextHelper.isNull(servidor.getAttribute(Columns.RSE_AGENCIA_SAL)) &&
                    !TextHelper.isNull(servidor.getAttribute(Columns.RSE_CONTA_SAL))) ||
                    (!TextHelper.isNull(servidor.getAttribute(Columns.RSE_BANCO_SAL_2)) &&
                            !TextHelper.isNull(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2)) &&
                            !TextHelper.isNull(servidor.getAttribute(Columns.RSE_CONTA_SAL_2))));

            CustomTransferObject convenio = null;
            try {
                convenio = convenioController.getParamCnv(csaCodigo, orgCodigo, svcCodigo, responsavel);
                if (convenio == null) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.convenio.inexistente.ser", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final String cnvCodigo = convenio.getAttribute(Columns.CNV_CODIGO).toString();

            // Parâmetros de serviço
            final ParamSvcTO paramSvcCse              = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            final boolean permiteCadVlrTac            = paramSvcCse.isTpsCadValorTac();
            final boolean permiteCadVlrIof            = paramSvcCse.isTpsCadValorIof();
            final boolean permiteCadVlrLiqLib         = paramSvcCse.isTpsCadValorLiquidoLiberado();
            final boolean permiteCadVlrMensVinc       = paramSvcCse.isTpsCadValorMensalidadeVinc();
            final boolean permiteVlrLiqTxJuros        = paramSvcCse.isTpsVlrLiqTaxaJuros();
            final boolean boolTpsSegPrestamista       = paramSvcCse.isTpsExigeSeguroPrestamista();
            final boolean validarDataNasc             = paramSvcCse.isTpsValidarDataNascimentoNaReserva();
            boolean serSenhaObrigatoria         = parametroController.senhaServidorObrigatoriaReserva(rseCodigo, svcCodigo, csaCodigo, responsavel);
            boolean serInfBancariaObrigatoria   = paramSvcCse.isTpsInfBancariaObrigatoria();
            boolean validarInfBancaria          = paramSvcCse.isTpsValidarInfBancariaNaReserva();
            final boolean valorMaxIgualSomaContratos  = (compra ? paramSvcCse.isTpsVlrMaxCompraIgualSomaContratos() : paramSvcCse.isTpsVlrMaxRenegIgualSomaContratos());
            final boolean prazoMaxIgualMaiorContratos = (compra ? paramSvcCse.isTpsPrzMaxCompraIgualMaiorContratos() : paramSvcCse.isTpsPrzMaxRenegIgualMaiorContratos());
            final String mascaraAdeIdentificador      = paramSvcCse.getTpsMascaraIdentificadorAde();

            if (compra) {
                final InformacaoSerCompraEnum exigeInfCompra = parametroController.senhaServidorObrigatoriaCompra(svcCodigo, rseCodigo, responsavel);
                if (InformacaoSerCompraEnum.CONTA_BANCARIA.equals(exigeInfCompra)) {
                    serInfBancariaObrigatoria = true;
                    validarInfBancaria = true;
                } else if (InformacaoSerCompraEnum.SENHA.equals(exigeInfCompra) && !senhaServidorOK && !financiamentoDivida) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.senha", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                } else if (serInfBancariaObrigatoria && InformacaoSerCompraEnum.NADA.equals(exigeInfCompra)) {
                    serInfBancariaObrigatoria = false;
                }
            }

            // Busca parâmetro de Controle de Valor máximo de desconto
            final boolean controlaSaldoDevedor         = paramSvcCse.isTpsControlaSaldo();
            final boolean possuiControleVlrMaxDesconto = controlaSaldoDevedor && paramSvcCse.isTpsControlaVlrMaxDesconto();
            BigDecimal vlrMaxParcelaSaldoDevedor = null;
            if (possuiControleVlrMaxDesconto) {
                vlrMaxParcelaSaldoDevedor = autorizacaoController.calcularValorDescontoParcela(rseCodigo, svcCodigo, null);
            }

            final Short adeIncMargem   = paramSvcCse.getTpsIncideMargem(); // Incide na margem 1, 2 ou 3
            final String tipoVlr       = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
            final String labelTipoVlr  = ParamSvcTO.getDescricaoTpsTipoVlr(tipoVlr);
            String adeVlrPadrao  = paramSvcCse.getTpsAdeVlr() != null ? paramSvcCse.getTpsAdeVlr() : ""; // Valor da prestação fixo para o serviço
            final boolean alteraAdeVlr = paramSvcCse.isTpsAlteraAdeVlr(); // Habilita ou nao campo de valor da reserva, campo ja vem preenchido
            final int maxPrazo         = ((paramSvcCse.getTpsMaxPrazoRenegociacao() != null) && !"".equals(paramSvcCse.getTpsMaxPrazoRenegociacao())) ?
                                    Integer.parseInt(paramSvcCse.getTpsMaxPrazoRenegociacao()) : ((paramSvcCse.getTpsMaxPrazo() != null) &&
                                    !"".equals(paramSvcCse.getTpsMaxPrazo())) ? Integer.parseInt(paramSvcCse.getTpsMaxPrazo()) : -1;
            final String vlrLimite     = ((paramSvcCse.getTpsVlrLimiteAdeSemMargem() != null) && !"".equals(paramSvcCse.getTpsVlrLimiteAdeSemMargem())) ? NumberHelper.reformat(paramSvcCse.getTpsVlrLimiteAdeSemMargem(), "en", NumberHelper.getLang()) : "0";
            final int carenciaMinCse   = ((paramSvcCse.getTpsCarenciaMinima() != null) && !"".equals(paramSvcCse.getTpsCarenciaMinima())) ? Integer.parseInt(paramSvcCse.getTpsCarenciaMinima()) : 0;
            final int carenciaMaxCse   = ((paramSvcCse.getTpsCarenciaMaxima() != null) && !"".equals(paramSvcCse.getTpsCarenciaMaxima())) ? Integer.parseInt(paramSvcCse.getTpsCarenciaMaxima()) : 99;
            final int numAdeHistLiqAntecipadas = ((paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas() != null) && !"".equals(paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas())) ? Integer.parseInt(paramSvcCse.getTpsNumAdeHistLiquidacoesAntecipadas()) : 0;

            // Parâmetros de convênio
            final int carenciaMinima        = ((convenio.getAttribute("CARENCIA_MINIMA") != null) && !"".equals(convenio.getAttribute("CARENCIA_MINIMA"))) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MINIMA").toString()) : 0;
            final int carenciaMaxima        = ((convenio.getAttribute("CARENCIA_MAXIMA") != null) && !"".equals(convenio.getAttribute("CARENCIA_MAXIMA"))) ? Integer.parseInt(convenio.getAttribute("CARENCIA_MAXIMA").toString()) : 99;
            final String indPadCsa          = ((convenio.getAttribute("VLR_INDICE") != null) && !"".equals(convenio.getAttribute("VLR_INDICE"))) ? convenio.getAttribute("VLR_INDICE").toString() : "";
            boolean vlrIndiceDisabled = false;
            final boolean permitePrazoMaiorContSer = ((convenio.getAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO") != null) && "S".equals(convenio.getAttribute("PERMITE_PRAZO_MAIOR_RSE_PRAZO")));


            // Define os valores de carência mínimo e máximo
            final int[] carenciaPermitida  = ReservaMargemHelper.getCarenciaPermitida(carenciaMinima, carenciaMaxima, carenciaMinCse, carenciaMaxCse);
            final int carenciaMinPermitida = carenciaPermitida[0];
            final int carenciaMaxPermitida = carenciaPermitida[1];

            // Parâmetro de identificador ADE obrigatório
            final boolean identificadorAdeObrigatorio = (!TextHelper.isNull(convenio.getAttribute("IDENTIFICADOR_ADE_OBRIGATORIO")) ? "S".equals(convenio.getAttribute("IDENTIFICADOR_ADE_OBRIGATORIO")) : paramSvcCse.isTpsIdentificadorAdeObrigatorio());

            // Verifica o valor total renegociado e se existe parcela em processamento
            // em algum dos contratos renegociados para informação ao usuário
            String mensagem                = "";
            BigDecimal valorTotal          = new BigDecimal("0.00");
            Integer maiorPrazoRestante     = null;
            boolean parcelaEmProcessamento = false;

            for (final TransferObject ctoAde : autdesList) {
                parcelaEmProcessamento |= (ctoAde.getAttribute(Columns.PRD_ADE_CODIGO) != null);

                if (AutorizacaoHelper.valorMargemDisponivelRenegociacao(adeIncMargem, (Short) ctoAde.getAttribute(Columns.ADE_INC_MARGEM), responsavel)) {
                    valorTotal = valorTotal.add(AutorizacaoHelper.restringirValorDisponivelRenegociacao((BigDecimal) ctoAde.getAttribute(Columns.ADE_VLR), svcCodigo, compra, responsavel));
                }

                if (ctoAde.getAttribute(Columns.ADE_PRAZO) != null) {
                    final int prazoRestAdeReneg = ((Integer) ctoAde.getAttribute(Columns.ADE_PRAZO)) - (ctoAde.getAttribute(Columns.ADE_PRD_PAGAS) != null ? (Integer) ctoAde.getAttribute(Columns.ADE_PRD_PAGAS) : 0);
                    if ((maiorPrazoRestante == null) || (maiorPrazoRestante < prazoRestAdeReneg)) {
                        maiorPrazoRestante = prazoRestAdeReneg;
                    }
                }
            }

            if (parcelaEmProcessamento) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.consignacao.possui.prd.processamento.folha", responsavel) + "\n";
                JspHelper.addMsgSession(session, CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.consignacao.possui.prd.processamento.folha", responsavel));
            }

            if (responsavel.isCsaCor() && !TextHelper.isNull(paramSvcCse.getTpsMsgExibirInclusaoAlteracaoAdeCsa())) {
                // Se é consignatária ou correspondente que está reservando margem, então exibe a mensagem do parâmetro
                JspHelper.addMsgSession(session, CodedValues.MSG_ALERT, paramSvcCse.getTpsMsgExibirInclusaoAlteracaoAdeCsa());
            }

            // Verifica se pode mostrar margem
            MargemDisponivel margemDisponivel = null;
            try {
                margemDisponivel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, adeIncMargem, adesReneg, responsavel);
            } catch (final ViewHelperException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final boolean podeMostrarMargem = margemDisponivel.getExibeMargem().isExibeValor();

            final List<MargemTO> margensIncidentes       = parametroController.lstMargensIncidentes(null, csaCodigo, orgCodigo, null, null, responsavel);
            boolean exibeAlgumaMargem                    = false;
            for (final MargemTO margem : margensIncidentes) {
                exibeAlgumaMargem |= new ExibeMargem(margem, responsavel).isExibeValor();
            }
            final BigDecimal margemRestOld = margemDisponivel.getMargemRestante();

            // Margem restante atualizada
            BigDecimal margemRestNew = (!adeIncMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) ? margemRestOld.add(valorTotal) : margemRestOld;
            if (!margemDisponivel.getExibeMargem().isSemRestricao() && (margemRestNew.signum() == -1)) {
                margemRestNew = new BigDecimal("0.00");
            }

            // Se tipo valor igual a margem total, coloca no campo de adeVlr o
            // valor da margem disponível para o serviço
            if (CodedValues.TIPO_VLR_TOTAL_MARGEM.equals(tipoVlr)) {
                adeVlrPadrao = margemRestNew.toString();
            }

            // Se existe simulação de consignação, então adiciona combo para seleção
            // de prazos para a autorização, se houverem prazos cadastrados
            final Set<Integer> prazosPossiveisMensal    = new TreeSet<>();
            Set<Integer> prazosPossiveisPeriodicidadeFolha = new TreeSet<>();

            final boolean temSimulacaoConsignacao = ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel);
            final boolean validaPrazoRenegociacao = ((paramSvcCse.getTpsMaxPrazoRenegociacao() != null) && !"".equals(paramSvcCse.getTpsMaxPrazoRenegociacao()));
            if (temSimulacaoConsignacao || paramSvcCse.isTpsValidarTaxaJuros()) {
                // Seleciona prazos ativos para simulação.
                try {
                    final int dia = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                    final List<TransferObject> prazos = simulacaoController.getPrazoCoeficiente(svcCodigo, csaCodigo, orgCodigo, dia, validaPrazoRenegociacao, responsavel);
                    if ((prazos != null) && !prazos.isEmpty()) {
                        prazos.forEach(p -> prazosPossiveisMensal.add(Integer.valueOf(p.getAttribute(Columns.PRZ_VLR).toString())));
                        if (!PeriodoHelper.folhaMensal(responsavel)) {
                            prazosPossiveisPeriodicidadeFolha = PeriodoHelper.converterListaPrazoMensalEmPeriodicidade(prazos, responsavel);
                        }
                    }
                } catch (final Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }

            // Verifica se sistema permite cadastro de índice para o serviço
            final boolean permiteCadIndice        = (ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_CAD_INDICE, responsavel).toString());
            // Índice cadastrado automaticamente
            final boolean indiceSomenteAutomatico = ParamSist.paramEquals(CodedValues.TPC_INDICE_SOMENTE_AUTOMATICO, CodedValues.TPC_SIM, responsavel);
            // Verifica se sistema o cadastro de índice é numérico ou alfanumérico
            final boolean indiceNumerico          = (ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_NUMERICO, responsavel).toString());
            final String mascaraIndice            = (indiceNumerico ? "#D" : "#A") + String.valueOf(limiteIndice).length();

            // Busca status se mensagem de margem comprometida esta ativa
            final boolean mensagemMargemComprometida = (ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_MSG_MARGEM_COMPROMET, responsavel) != null) && "S".equals(ParamSist.getInstance().getParam(CodedValues.TPC_PERMITE_MSG_MARGEM_COMPROMET, responsavel).toString());

            // Parametro que mostra a composição da margem do servidor na reserva.
            final boolean boolTpcPmtCompMargem = (ParamSist.getInstance().getParam(CodedValues.TPC_MOSTRA_COMPOSICAO_MARGEM, responsavel) != null) && CodedValues.TPC_SIM.equals(ParamSist.getInstance().getParam(CodedValues.TPC_MOSTRA_COMPOSICAO_MARGEM, responsavel).toString());

            //Parâmetro para exibição de variação de margem
            final boolean possuiVariacaoMargem = (responsavel.isCseSupOrg() && ParamSist.getBoolParamSist(CodedValues.TPC_MOSTRA_VARIACAO_MARGEM_CSE_ORG, responsavel))
                    || (responsavel.isCsaCor() && ParamSist.getBoolParamSist(CodedValues.TPC_MOSTRA_VARIACAO_MARGEM_CSA_COR, responsavel));

            // Parametro de sistema que indica qual e o indice padrao para contratos
            final String indicePadrao = (ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_PADRAO, responsavel) != null) ? ParamSist.getInstance().getParam(CodedValues.TPC_INDICE_PADRAO, responsavel).toString() : null;

            // Parametro de sistema que exige ou não a senha para visualizar margem
            final boolean boolTpcExigeSenha = parametroController.senhaServidorObrigatoriaConsultaMargem(rseCodigo, responsavel);

            final boolean permiteVlrNegativo = parametroController.permiteContratoValorNegativo(csaCodigo, svcCodigo, responsavel);

            // Parametro de sistema que indica se permite compra na margem 3 casada negativa
            final boolean permiteRenegociarComprarMargem3NegativaCasada = adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3) && ParamSist.getBoolParamSist(CodedValues.TPC_RENEGOCIACAO_COMPRA_MARGEM_3_NEG_CASADA, responsavel);

            // Se permite anexo na inclusão e neste serviço é obrigatório, ou é compra e o anexo de documentação adicional é obrigatório,
            // verifica se o usuário possui permissão de anexar arquivos
            if ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) && parametroController.isObrigatorioAnexoInclusao(svcCodigo, responsavel)) ||
                    (compra && ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OBRIGATORIO, responsavel))) {
                // Caso não tenha permissão de anexar arquivos, envia mensagem de erro
                if (!responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.permissao.anexo.reserva", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            boolean exigeModalidadeOperacao = false;
            boolean exigeMatriculaSerCsa    = false;

            if (responsavel.isCsaCor()) {
                final String tpaModalidadeOperacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO, responsavel);
                exigeModalidadeOperacao      = (!TextHelper.isNull(tpaModalidadeOperacao) && "S".equals(tpaModalidadeOperacao));

                final String tpaMatriculaSerCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO, responsavel);
                exigeMatriculaSerCsa      = (!TextHelper.isNull(tpaMatriculaSerCsa) && "S".equals(tpaMatriculaSerCsa));
            }

            // Busca as propostas de pagamento
            List<TransferObject> propostas = null;
            try {
                if (financiamentoDivida) {
                    // Se é financiamento de dívida, busca as propostas ofertadas pela CSA.
                    propostas = financiamentoDividaController.lstPropostaPagamentoDivida(autdes.getAttribute(Columns.ADE_CODIGO).toString(), responsavel.getCsaCodigo(), null, responsavel);
                    // Se não existem propostas, ou foi passado mais de um contrato, então retorna erro
                    if ((propostas == null) || propostas.isEmpty()) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.proposta.pagamento.nao.encontrada", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    } else if (autdesList.size() > 1) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                    // Se a senha é obrigatória para inclusão, verifica se existe proposta aprovada,
                    // e caso exista, não exige senha, já que a aprovação é realizada pelo servidor
                    if (serSenhaObrigatoria) {
                        for (final TransferObject proposta : propostas) {
                            final StatusPropostaEnum status = StatusPropostaEnum.recuperaStatusProposta(proposta.getAttribute(Columns.STP_CODIGO).toString());
                            if (status.equals(StatusPropostaEnum.APROVADA)) {
                                serSenhaObrigatoria = false;
                                break;
                            }
                        }
                    }
                }
            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

            final String fileName            = JspHelper.getPhoto(servidor.getAttribute(Columns.SER_CPF).toString(), servidor.getAttribute(Columns.RSE_CODIGO).toString(), responsavel);
            final List<TransferObject> cnvList = convenioController.lstConvenios(null, csaCodigo, svcCodigo, orgCodigo, true, responsavel);
            final CustomTransferObject cnvTO = (CustomTransferObject) cnvList.get(0);
            final String csaNomeAbrev        = (String) cnvTO.getAttribute(Columns.CSA_NOME_ABREV);
            String csaNome = ((String) cnvTO.getAttribute(Columns.CSA_IDENTIFICADOR)) + " - " +
                                                     ((!TextHelper.isNull(csaNomeAbrev)) ? csaNomeAbrev : (String) cnvTO.getAttribute(Columns.CSA_NOME));

            if (csaNome.length() > 50) {
                csaNome = csaNome.substring(0, 47) + "...";
            }

            final boolean sistExibeHistLiqAntecipadas = ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_HISTORICO_LIQUIDACOES_ANTECIPADAS, responsavel);

            boolean geraCombo = false;
            String vlrIndice = null;
            List<TransferObject> indices = null;
            if (permiteCadIndice && !indiceSomenteAutomatico) {
                CustomTransferObject c0 = null;

                final CustomTransferObject criterio = new CustomTransferObject();

                criterio.setAttribute(Columns.IND_SVC_CODIGO, svcCodigo);
                criterio.setAttribute(Columns.IND_CSA_CODIGO, csaCodigo);

                // Verifica a existencia de registros de indice ja cadastrados pela csa ou cse
                indices = indiceController.selectIndices(-1, -1, criterio, responsavel);

                if ((indices == null) || (indices.size() == 0)) {
                    // Se não existir nenhum indice cadastrado, então utilizar parâmetro de convenio 41 OU
                    // parâmetro de sistema 79 (que é sobreposto pelo 41)
                    if (!TextHelper.isNull(indPadCsa)) {
                        vlrIndice         = indPadCsa;
                        // Se existir um valor padrão para o parâmetro então o campo estará desabilitado
                        vlrIndiceDisabled = true;
                    } else {
                        vlrIndice = indicePadrao;
                    }
                } else // Se existir um registro apenas, exibir este registro no campo de indice
                if (indices.size() == 1) {
                    c0                = (CustomTransferObject) indices.get(0);
                    vlrIndice         = c0.getAttribute(Columns.IND_CODIGO).toString();
                    vlrIndiceDisabled = true;
                } else {
                    // Se existir mais de um, exibir um combo de seleção com as possibilidades existentes
                    geraCombo = true;
                }
            }

            final List<TransferObject> tdaList = autorizacaoController.lstTipoDadoAdicional(AcaoTipoDadoAdicionalEnum.ALTERA, VisibilidadeTipoDadoAdicionalEnum.WEB, svcCodigo, csaCodigo, responsavel);

            for (final TransferObject tda: tdaList) {
                String valorOriginal = "";
                if (exibeVlrAtual){
                   valorOriginal = autorizacaoController.getValorDadoAutDesconto((String)autdes.getAttribute(Columns.ADE_CODIGO), (String) tda.getAttribute(Columns.TDA_CODIGO), responsavel);
                }
                tda.setAttribute("VALOR_ORIGINAL", valorOriginal);
            }

            final RenegociarConsignacaoModel renegociarModel = new RenegociarConsignacaoModel();

            // Seleciona os vínculos que não podem reservar margem para este csa e svc
            autorizacaoController.verificaBloqueioVinculoCnvAlertaSessao(session, csaCodigo, svcCodigo, (String) servidor.getAttribute(Columns.RSE_VRS_CODIGO), responsavel);

            if (responsavel.isCsaCor()) {
                int maxPrazoRenegociacaoPeriodo = 0;
                int minPrazoRenegociacaoPeriodo = 0;

                final List<String> tpsCodigo = new ArrayList<>();
                tpsCodigo.add(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO);
                tpsCodigo.add(CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO);

                final List<TransferObject> paramSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigo, false, responsavel);
                for (final TransferObject param : paramSvcCsa) {
                    if ((param != null) && (param.getAttribute(Columns.TPS_CODIGO) != null) && (param.getAttribute(Columns.PSC_VLR) != null)) {

                        if (CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_NO_PERIODO.equals(param.getAttribute(Columns.TPS_CODIGO))) {
                            maxPrazoRenegociacaoPeriodo = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? Integer.parseInt((String)param.getAttribute(Columns.PSC_VLR)) : 0;
                        } else if (CodedValues.TPS_MIN_PRAZO_INI_DESCONTO_RENEGOCIACAO.equals(param.getAttribute(Columns.TPS_CODIGO))) {
                            minPrazoRenegociacaoPeriodo = (!param.getAttribute(Columns.PSC_VLR).toString().isEmpty()) ? Integer.parseInt((String)param.getAttribute(Columns.PSC_VLR)) : 0;
                        }
                    }
                }

                // Calcula data início padrão do contrato
                Integer adeCarencia = 0;
                final String periodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);
                Date anoMesIniNovaAde = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, adeCarencia, periodicidade, responsavel);

                // Calcula o período padrão usado na ocorrência de liquidação
                Date ocaPeriodoRenegociacao = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);

                // Define se exibe na interface as datas da renegociação
                boolean podeMostrarDatasRenegociacao = false;

                if ((maxPrazoRenegociacaoPeriodo > 0) && (minPrazoRenegociacaoPeriodo > 0)) {
                    // Se tem regra específica para datas na renegociação recupera o período em que deve
                    // ser criada a ocorrência de liquidação do contrato renegociado e a data de inclusão da nova
                    final Timestamp agora  = new Timestamp(Calendar.getInstance().getTimeInMillis());

                    // Calcula Final do Contrato Anterior
                    ocaPeriodoRenegociacao = (Date) AutorizacaoHelper.recuperarPeriodoOcorrenciaLiquidacao(orgCodigo, csaCodigo, svcCodigo, agora, anoMesIniNovaAde, responsavel);

                    // Calcula a carência mínima do novo contrato de acordo com os parâmetros de serviço
                    final Integer adeCarenciaNova = AutorizacaoHelper.calcularCarenciaContratoDestinoRenegociacao(orgCodigo, csaCodigo, svcCodigo, adeCarencia, ocaPeriodoRenegociacao, responsavel);
                    if (adeCarenciaNova.intValue() > adeCarencia.intValue()) {
                        adeCarencia = adeCarenciaNova;
                        adeCarencia = parametroController.calcularAdeCarenciaDiaCorteCsa(adeCarencia, csaCodigo, orgCodigo, responsavel);
                        anoMesIniNovaAde = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, adeCarencia, periodicidade, responsavel);
                    }

                    podeMostrarDatasRenegociacao = true;

                } else {
                    // Se não regra específica, verifica se há carência obrigatória com base no corte da CSA
                    adeCarencia = parametroController.calcularAdeCarenciaDiaCorteCsa(adeCarencia, csaCodigo, orgCodigo, responsavel);
                    if (adeCarencia > 0) {
                        // Calcula a nova data inicial com base na carência obrigatória
                        anoMesIniNovaAde = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, adeCarencia, periodicidade, responsavel);
                        podeMostrarDatasRenegociacao = true;
                    }
                }

                // DESENV-18331: Precisamos verificar se existe data na margem do servidor caso seja extra para mudar a data inicio de acordo com essa data.
                final Date dataInicioMargemExtra = autorizacaoController.calcularDataIniFimMargemExtra(rseCodigo, anoMesIniNovaAde, adeIncMargem, true, false, responsavel);

                if ((dataInicioMargemExtra != null) && (dataInicioMargemExtra.compareTo(anoMesIniNovaAde) > 0)) {
                    anoMesIniNovaAde = dataInicioMargemExtra;

                    // Define mensagem de alerta sobre a data inicial alterada
                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.info.calcular.data.inicio.fim.margem.extra.rse.data.margem.maior.web", responsavel));
                }

                renegociarModel.setAnoMesIniNovaAde(anoMesIniNovaAde);
                renegociarModel.setOcaPeriodoRenegociacao(ocaPeriodoRenegociacao);
                renegociarModel.setPodeMostrarDatasRenegociacao(podeMostrarDatasRenegociacao);
                renegociarModel.setPadraoAlterarDataEncerramento(CodedValues.TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_ALTERA_DATA_ENCERRAMENTO_RENEGOCIACAO_PADRAO, responsavel)));
            }

            renegociarModel.setSerInfBancariaObrigatoria(serInfBancariaObrigatoria);
            renegociarModel.setFileName(fileName);
            renegociarModel.setCsaNome(csaNome);
            renegociarModel.setCsaNomeAbrev(csaNomeAbrev);
            renegociarModel.setServico((cnvTO.getAttribute(Columns.CNV_COD_VERBA) != null ? cnvTO.getAttribute(Columns.CNV_COD_VERBA) :
                                       cnvTO.getAttribute(Columns.SVC_IDENTIFICADOR)) + " - " + cnvTO.getAttribute(Columns.SVC_DESCRICAO));
            renegociarModel.setDescricaoTipoVlrMargem(margemDisponivel.getTipoVlr());
            renegociarModel.setVlrMargemRestNew(margemRestNew);
            renegociarModel.setRotuloPeriodicidadePrazo(" (" + ApplicationResourcesHelper.getMessage("rotulo.meses", responsavel) + ")");
            renegociarModel.setAnexoInclusaoContratosObrigatorio(parametroController.isObrigatorioAnexoInclusao(svcCodigo, responsavel));
            renegociarModel.setPropostas(propostas);
            renegociarModel.setCsaIdentificador((String) cnvTO.getAttribute(Columns.CSA_IDENTIFICADOR));
            renegociarModel.setCnvCodVerba((String) cnvTO.getAttribute(Columns.CNV_COD_VERBA));
            renegociarModel.setSvcDescricao((String) cnvTO.getAttribute(Columns.SVC_DESCRICAO));
            renegociarModel.setSvcIdentifcador((String) cnvTO.getAttribute(Columns.SVC_DESCRICAO));
            renegociarModel.setRseCodigo(rseCodigo);
            renegociarModel.setDescricaoTipoVlrMargem(ParamSvcTO.getDescricaoTpsTipoVlr(margemDisponivel.getTipoVlr()));
            renegociarModel.setLabelTipoVlr(labelTipoVlr);
            renegociarModel.setAdeVlrPadrao(adeVlrPadrao);
            renegociarModel.setAlteraAdeVlr(alteraAdeVlr);
            renegociarModel.setPermiteEscolherPeriodicidade(permiteEscolherPeriodicidade);
            renegociarModel.setPrazosPossiveisMensal(prazosPossiveisMensal);
            renegociarModel.setExibeVlrAtual(exibeVlrAtual);
            renegociarModel.setMaxPrazo(maxPrazo);
            renegociarModel.setAutdes(autdes);
            renegociarModel.setCarenciaMinPermitida(carenciaMinPermitida);
            renegociarModel.setCarenciaMaxPermitida(carenciaMaxPermitida);
            renegociarModel.setPermiteCadVlrIof(permiteCadVlrIof);
            renegociarModel.setPermiteCadVlrTac(permiteCadVlrTac);
            renegociarModel.setExibeVlrAtual(exibeVlrAtual);
            renegociarModel.setPermiteCadVlrLiqLib(permiteCadVlrLiqLib);
            renegociarModel.setPermiteCadVlrMensVinc(permiteCadVlrMensVinc);
            renegociarModel.setSeguroPrestamista(boolTpsSegPrestamista);
            renegociarModel.setPermiteVlrLiqTxJuros(permiteVlrLiqTxJuros);
            renegociarModel.setTemCET(temCET);
            renegociarModel.setGeraComboIndice(geraCombo);
            renegociarModel.setPermiteCadIndice(permiteCadIndice);
            renegociarModel.setIndiceSomenteAutomatico(indiceSomenteAutomatico);
            renegociarModel.setVlrIndice(vlrIndice);
            renegociarModel.setVlrIndiceDisabled(vlrIndiceDisabled);
            renegociarModel.setMascaraIndice(mascaraIndice);
            renegociarModel.setIndices(indices);
            renegociarModel.setIdentificadorAdeObrigatorio(identificadorAdeObrigatorio);
            renegociarModel.setMascaraAdeIdentificador(mascaraAdeIdentificador);
            renegociarModel.setTdaList(tdaList);
            renegociarModel.setMascaraLogin((String) ParamSist.getInstance().getParam(CodedValues.TPC_MASCARA_LOGIN_EXTERNO_SERVIDOR, responsavel));
            renegociarModel.setSerSenhaObrigatoria(serSenhaObrigatoria);
            renegociarModel.setSvcCodigo(svcCodigo);
            renegociarModel.setAnexoObrigatorio(parametroController.isObrigatorioAnexoInclusao(svcCodigo, responsavel));
            renegociarModel.setPodeMostrarMargem(podeMostrarMargem);
            renegociarModel.setPmtCompMargem(boolTpcPmtCompMargem);
            renegociarModel.setExigeSenhaServidor(parametroController.senhaServidorObrigatoriaConsultaMargem(rseCodigo, responsavel));
            renegociarModel.setPossuiVariacaoMargem(possuiVariacaoMargem);
            renegociarModel.setExibeAlgumaMargem(exibeAlgumaMargem);
            renegociarModel.setSistExibeHistLiqAntecipadas(sistExibeHistLiqAntecipadas);
            renegociarModel.setNumAdeHistLiqAntecipadas(numAdeHistLiqAntecipadas);
            renegociarModel.setAdesReneg(adesReneg);
            renegociarModel.setCnvCodigo(cnvCodigo);
            renegociarModel.setCsaCodigo(csaCodigo);
            renegociarModel.setOrgCodigo(orgCodigo);
            renegociarModel.setVlrLimite(vlrLimite);
            renegociarModel.setSvcCodigo(svcCodigo);
            renegociarModel.setPossuiControleVlrMaxDesconto(possuiControleVlrMaxDesconto);
            renegociarModel.setVlrMaxParcelaSaldoDevedor(vlrMaxParcelaSaldoDevedor);
            renegociarModel.setPermitePrazoMaiorContSer(permitePrazoMaiorContSer);
            renegociarModel.setIdentificadorAdeObrigatorio(identificadorAdeObrigatorio);
            renegociarModel.setprazosPossiveisPeriodicidadeFolha(prazosPossiveisPeriodicidadeFolha);
            renegociarModel.setSerNome((String) servidor.getAttribute(Columns.SER_NOME));
            renegociarModel.setSerDataNasc(serDataNasc);
            renegociarModel.setNumBanco(numBanco);
            renegociarModel.setNumAgencia(numAgencia);
            renegociarModel.setNumConta(numConta);
            renegociarModel.setNumConta1(numConta1);
            renegociarModel.setNumConta2(numConta2);
            renegociarModel.setNumBancoAlt(numBancoAlt);
            renegociarModel.setNumAgenciaAlt(numAgenciaAlt);
            renegociarModel.setNumContaAlt(numContaAlt);
            renegociarModel.setNumContaAlt1(numContaAlt1);
            renegociarModel.setNumContaAlt2(numContaAlt2);
            renegociarModel.setValidarDataNasc(validarDataNasc);
            renegociarModel.setValidarInfBancaria(validarInfBancaria);
            renegociarModel.setRseTemInfBancaria(rseTemInfBancaria);
            renegociarModel.setValorMaxIgualSomaContratos(valorMaxIgualSomaContratos);
            renegociarModel.setPrazoMaxIgualMaiorContratos(prazoMaxIgualMaiorContratos);
            renegociarModel.setMensagem(mensagem);
            renegociarModel.setMensagemMargemComprometida(mensagemMargemComprometida);
            renegociarModel.setExigeSenhaServidor(boolTpcExigeSenha);
            renegociarModel.setPermiteVlrNegativo(permiteVlrNegativo);
            renegociarModel.setPermiteRenegociarComprarMargem3NegativaCasada(permiteRenegociarComprarMargem3NegativaCasada);
            renegociarModel.setExigeModalidadeOperacao(exigeModalidadeOperacao);
            renegociarModel.setExigeMatriculaSerCsa(exigeMatriculaSerCsa);
            renegociarModel.setExigeEmailServidor(exigeEmailServidor);
            renegociarModel.setValorTotal(valorTotal);
            renegociarModel.setAutdesList(autdesList);
            renegociarModel.setCompra(compra);

            model.addAttribute("renegociarModel", renegociarModel);
            model.addAttribute("qtdeMinAnexos", paramSvcCse.getTpsQuantidadeMinimaInclusaoContratos());

            return viewRedirect("jsp/renegociarConsignacao/renegociarConsignacao", request, session, model, responsavel);
        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected String executarFuncaoAposDuplicidade(HttpServletRequest request, HttpServletResponse response,
    		HttpSession session, Model model) {
    	final String rseCodigo = request.getParameter("RSE_CODIGO");
    	return incluirReserva(rseCodigo, request, response, session, model);
    }

    @Override
    protected boolean validarSenhaServidor(String rseCodigo, boolean consomeSenha, HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        boolean senhaServidorOK = false;
        // Verifica se a senha foi digitada e validada corretamente
        if (!TextHelper.isNull(rseCodigo)) {
            // Se o servidor informou senha, então será validada
            final boolean informouSenhaServidor = !TextHelper.isNull(session.getAttribute("serAutorizacao")) || !TextHelper.isNull(request.getParameter("serAutorizacao"));
            final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");

            try {
                if (informouSenhaServidor) {
                    SenhaHelper.validarSenha(request, rseCodigo, svcCodigo, false, false, consomeSenha, responsavel);
                    senhaServidorOK = true;
                }
            } catch (final ViewHelperException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return false;
            }
        }

        if (senhaServidorOK) {
            session.setAttribute("senhaServidorRenegOK", rseCodigo);
        } else if ((session.getAttribute("senhaServidorRenegOK") != null)
                && session.getAttribute("senhaServidorRenegOK").equals(rseCodigo)) {
            senhaServidorOK = true;
        } else {
            session.removeAttribute("senhaServidorRenegOK");
        }

        return true;
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.renegociar.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/renegociarConsignacao");
        model.addAttribute("imageHeader", "i-operacional");

        try {
            final boolean geraSenhaAutOtp = ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel);

            // Se é validação de digital, realiza a validação após selecionar o servidor, então não deve pedir senha
            if ((!geraSenhaAutOtp && !ParamSist.paramEquals(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, CodedValues.TPC_SIM, responsavel)) && parametroController.senhaServidorObrigatoriaConsultaMargem(null, responsavel)) {
                model.addAttribute("exibirCampoSenha", Boolean.TRUE);
            }
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        final List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_DEFERIDA);
        sadCodigos.add(CodedValues.SAD_EMANDAMENTO);
        sadCodigos.add(CodedValues.SAD_ESTOQUE);
        sadCodigos.add(CodedValues.SAD_ESTOQUE_MENSAL);
        if(ParamSist.getBoolParamSist(CodedValues.TPC_SUSPENDER_CONTRATO_PARCELA_REJEITADA_RETORNO, responsavel)) {
            sadCodigos.add(CodedValues.SAD_SUSPENSA);
        }
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link                    = "../v3/renegociarConsignacao?acao=renegociarConsignacao";
        String descricao               = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar.abreviado", responsavel);
        String descricaoCompleta       = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        String msgAlternativa          = "";
        String msgConfirmacao          = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.renegociar", responsavel);
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("RENE_CONTRATO", CodedValues.FUN_RENE_CONTRATO, descricao, descricaoCompleta,"renegociar_contrato.gif", "btnRenegociarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, "chkADE"));

        // Adiciona o editar consignação
        link                    = "../v3/renegociarConsignacao?acao=detalharConsignacao";
        descricao               = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta       = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa          = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao          = "";
        msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta,"editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));

        return acoes;
    }

    @Override
    protected List<ColunaListaConsignacao> definirColunasListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final List<ColunaListaConsignacao> colunas = super.definirColunasListaConsignacao(request, responsavel);

        if (ParamSist.paramEquals(CodedValues.TPC_LISTAR_MOTIVO_ADES_NAO_RENEGOCIAVEIS, CodedValues.TPC_SIM, responsavel)) {
            colunas.add(new ColunaListaConsignacao(MotivoAdeNaoRenegociavelEnum.CHAVE_MOTIVO_INDISPONIBILIDADE, ApplicationResourcesHelper.getMessage("mensagem.indisponibilidade.renegociacao.titulo", responsavel), ColunaListaConsignacao.TipoValor.TEXTO, true));
        }

        return colunas;
    }

    @Override
    protected List<TransferObject> formatarValoresListaConsignacao(List<TransferObject> lstConsignacao, List<ColunaListaConsignacao> colunas, HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {

        if (ParamSist.paramEquals(CodedValues.TPC_LISTAR_MOTIVO_ADES_NAO_RENEGOCIAVEIS, CodedValues.TPC_SIM, responsavel)) {
            boolean temAdeDisponivel = false;
            for (final TransferObject ade : lstConsignacao) {
                if (ade.getAttribute(MotivoAdeNaoRenegociavelEnum.CHAVE_MOTIVO_INDISPONIBILIDADE) == null) {
                    temAdeDisponivel = true;
                    break;
                }
            }
            request.setAttribute("temAdeDisponivel", temAdeDisponivel);
        }

        return super.formatarValoresListaConsignacao(lstConsignacao, colunas, request, session, responsavel);
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "renegociar");

        final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
        final String csaCodigo = (responsavel.isCsaCor()) ? responsavel.getCsaCodigo() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

        criterio.setAttribute(Columns.SVC_CODIGO, svcCodigo);
        criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);

        return criterio;
    }

    @Override
    protected void carregarInformacoesAcessorias(String rseCodigo, String adeNumero, List<TransferObject> lstConsignacao, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws AutorizacaoControllerException {
        // Verifica se as consignações pertencem ao mesmo registro servidor
        if (request.getAttribute("resultadoMultiplosServidores") != null) {
            throw new AutorizacaoControllerException("mensagem.erro.multiplo.servidor.nao.permitido", responsavel);
        }

        try {
            // Carrega parâmetro com a quantidade máxima de consignações permitidas para renegociação
            final String svcCodigo = JspHelper.verificaVarQryStr(request, "SVC_CODIGO");
            if (!TextHelper.isNull(svcCodigo)) {
                final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                if (!TextHelper.isNull(paramSvcCse.getTpsQtdeMaxAdeRenegociacao())) {
                    try {
                        // Valida, caso o parâmetro não seja numérico
                        final String qtdMaxRneg = String.valueOf(Integer.parseInt(paramSvcCse.getTpsQtdeMaxAdeRenegociacao()));
                        model.addAttribute("qtdMaxSelecaoMultipla", qtdMaxRneg);
                        model.addAttribute("msgErroQtdMaxSelecaoMultiplaSuperada", ApplicationResourcesHelper.getMessage("mensagem.erro.renegociar.consignacao.limite.operacao", responsavel, qtdMaxRneg));
                    } catch (final NumberFormatException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    protected String continuarOperacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        boolean exigeSenhaConsultaMargem = false;
        try {
            exigeSenhaConsultaMargem = parametroController.senhaServidorObrigatoriaConsultaMargem(null, responsavel);
        } catch (final ZetraException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!responsavel.isSer() &&
                ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel) &&
                exigeSenhaConsultaMargem) {

                // Se utiliza otp como senha de autorizção e exige senha para consulta de margem, redireciona para validação de digital do servidor
                return validarOtp(rseCodigo, request, response, session, model, true);
        } else if (!responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel) && exigeSenhaConsultaMargem) {
            // Se exige validação de digital do servidor e exige senha para consulta de margem, redireciona para validação de digital do servidor
            return validarDigital(rseCodigo, request, response, session, model);
        } else {
            return pesquisarConsignacao(rseCodigo, adeNumero, request, response, session, model);
        }
    }

    @Override
    protected String getFunCodigo() {
        return CodedValues.FUN_RENE_CONTRATO;
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        boolean exigeSenhaConsultaMargem = false;
        try {
            exigeSenhaConsultaMargem = parametroController.senhaServidorObrigatoriaConsultaMargem(null, responsavel);
        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        if (!responsavel.isSer() &&
                ParamSist.paramEquals(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, CodedValues.TPC_SIM, responsavel) &&
                ParamSist.paramEquals(CodedValues.TPC_GERA_OTP_SENHA_AUTORIZACAO, CodedValues.TPC_SIM, responsavel) &&
                exigeSenhaConsultaMargem) {
            // Se exige validação de digital do servidor e exige senha para consulta de margem, redireciona para validação de digital do servidor
            return "validarOtp";
        } else if (!responsavel.isSer() && ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel) && exigeSenhaConsultaMargem) {
            // Se exige validação de digital do servidor e exige senha para consulta de margem, redireciona para validação de digital do servidor
            return "validarDigital";
        } else {
            return "pesquisarConsignacao";
        }
    }

    @Override
    protected String validarServicoOperacao(String svcCodigo, String rseCodigo, Map<String, String> parametrosPlano, HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(svcCodigo)) {
            svcCodigo = request.getParameter("SVC_CODIGO");
        }
        if (TextHelper.isNull(svcCodigo)) {
            throw new ViewHelperException("mensagem.erro.servico.nao.informado", responsavel);
        }
        return svcCodigo;
    }

    @Override
    @RequestMapping(params = { "acao=listarHistLiquidacoesAntecipadas" })
    public String listarHistLiquidacoesAntecipadas(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return super.listarHistLiquidacoesAntecipadas(request, response, session, model);
    }
}
