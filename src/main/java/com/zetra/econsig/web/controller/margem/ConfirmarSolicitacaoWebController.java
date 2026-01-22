package com.zetra.econsig.web.controller.margem;

import java.math.BigDecimal;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.criptografia.JCryptOld;
import com.zetra.econsig.helper.criptografia.RSA;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.senha.SenhaHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.usuario.LoginHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.coeficiente.CoeficienteController;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.ConfirmarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractConsultarConsignacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ConfirmarSolicitacao</p>
 * <p>Description: Controlador Web para o caso de uso Confirmar Solicitacao.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 24240 $
 * $Date: 2018-05-15 10:45:30 -0300 (Ter, 15 mai 2018) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/confirmarSolicitacao" })
public class ConfirmarSolicitacaoWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfirmarSolicitacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ConfirmarConsignacaoController confirmarConsignacaoController;

    @Autowired
    private CoeficienteController coeficienteController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            ParamSession paramSession = ParamSession.getParamSession(session);
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String adeCodigo = request.getParameter("ADE_CODIGO");
            if (TextHelper.isNull(adeCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Verifica se o sistema está configurado para trabalhar com o CET.
            boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

            // Verifica parâmetro que indica se simula por taxas de juros ou coeficientes
            boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
            boolean simulacaoMetodoMexicano = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_MEXICANO, responsavel);
            boolean simulacaoMetodoBrasileiro = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);

            // Busca a autorização
            CustomTransferObject autdes = null;
            try {
                autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
            } catch (AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_CONF_SOLICITACAO, responsavel.getUsuCodigo(), autdes.getAttribute(Columns.SVC_CODIGO).toString())) {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca os parâmetros de serviço necessários
            boolean podeAlterarValorParcela = true;
            boolean exigeSenhaServidor = false;
            boolean serInfBancariaObrigatoria = false;
            boolean validarInfBancaria = false;
            boolean exigeCodAutSolicitacao = false;
            boolean exigeModalidadeOperacao = false;
            boolean exigeMatriculaSerCsa = false;
            boolean pulaInformacaoValorPrazo = false;
            try {
                String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
                ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                String tipoVlr = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
                // Se é tipo valor total da margem, não pode alterar o valor da parcela
                if (tipoVlr.equals(CodedValues.TIPO_VLR_TOTAL_MARGEM)) {
                    podeAlterarValorParcela = false;
                }
                exigeSenhaServidor = paramSvcCse.isTpsExigeSenhaConfirmacaoSolicitacao();
                serInfBancariaObrigatoria = paramSvcCse.isTpsInfBancariaObrigatoria();
                validarInfBancaria = paramSvcCse.isTpsValidarInfBancariaNaReserva();
                exigeCodAutSolicitacao = paramSvcCse.isTpsExigeCodAutorizacaoSolic();

                if (responsavel.isCsaCor()) {
                    String csaCodigo = (String) autdes.getAttribute(Columns.CSA_CODIGO);
                    String tpaModalidadeOperacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO, responsavel);
                    exigeModalidadeOperacao = (!TextHelper.isNull(tpaModalidadeOperacao) && tpaModalidadeOperacao.equals("S")) ? true : false;

                    String tpaMatriculaSerCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO, responsavel);
                    exigeMatriculaSerCsa = (!TextHelper.isNull(tpaMatriculaSerCsa) && tpaMatriculaSerCsa.equals("S")) ? true : false;

                    //busca valor do parametro de serviço 277;
                    CustomTransferObject naturezaSvc = servicoController.findNaturezaServico(svcCodigo, responsavel);
                    pulaInformacaoValorPrazo = (paramSvcCse.isTpsPulaInformacaoValorPrazoFluxoReserva() && naturezaSvc != null && !TextHelper.isNull(naturezaSvc.getAttribute(Columns.NSE_CODIGO)) && !naturezaSvc.getAttribute(Columns.NSE_CODIGO).toString().equals(CodedValues.NSE_EMPRESTIMO));

                    String rotuloMoeda = ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel);
                    model.addAttribute("rotuloMoeda", rotuloMoeda);
                }

            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Verifica se oculta TAC
            boolean ocultarCamposTac = ParamSist.getInstance().getParam(CodedValues.TPC_OCULTAR_CAMPOS_TAC, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_OCULTAR_CAMPOS_TAC, responsavel).toString().equals(CodedValues.TPC_SIM);

            // Busca as taxas utilizadas no contrato
            BigDecimal adeTac = null, adeOp = null;
            try {
                List<String> tpsCodigos = new ArrayList<>();
                tpsCodigos.add(CodedValues.TPS_TAC_FINANCIADA);
                tpsCodigos.add(CodedValues.TPS_OP_FINANCIADA);
                Map<String, String> taxas = autorizacaoController.getParamSvcADE(adeCodigo, tpsCodigos, responsavel);
                adeTac = taxas.get(CodedValues.TPS_TAC_FINANCIADA) == null ? new BigDecimal("0") : new BigDecimal(taxas.get(CodedValues.TPS_TAC_FINANCIADA).toString());
                adeOp = taxas.get(CodedValues.TPS_OP_FINANCIADA) == null ? new BigDecimal("0") : new BigDecimal(taxas.get(CodedValues.TPS_OP_FINANCIADA).toString());
            } catch (AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca o coeficiente através do CoeficienteDesconto
            CustomTransferObject cftdes = null;
            TransferObject cft = null;
            BigDecimal cftVlrAtual = null;
            try {
                cftdes = simulacaoController.findCdeByAdeCodigo(adeCodigo, responsavel);
                cft = coeficienteController.getCoeficiente(cftdes.getAttribute(Columns.CDE_CFT_CODIGO).toString(), responsavel);
                cftVlrAtual = new BigDecimal(cft.getAttribute(Columns.CFT_VLR).toString());
            } catch (Exception ex) {
                LOG.error(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
            }

            // Busca os dados do registro servidor
            CustomTransferObject servidor = null;
            String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
            try {
                servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            } catch (ServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Prepara os dados bancários do servidor para validação via javascript
            String numBanco = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_BANCO_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_BANCO_SAL).toString()) : "");
            String numAgencia = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString()) : "");
            String numConta = servidor.getAttribute(Columns.RSE_CONTA_SAL) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_CONTA_SAL).toString()) : "";

            String numConta1 = "";
            String numConta2 = "";

            if (numConta.length() > 0) {
                numConta1 = numConta.substring(0, numConta.length() / 2);
                numConta2 = numConta.substring(numConta.length() / 2, numConta.length());
            } else {
                numConta1 = numConta2 = numConta;
            }

            numConta1 = JCryptOld.crypt("IB", numConta1);
            numConta2 = JCryptOld.crypt("IB", numConta2);

            String numBancoAlt = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_BANCO_SAL_2) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_BANCO_SAL_2).toString()) : "");
            String numAgenciaAlt = JCryptOld.crypt("IB", servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2).toString()) : "");
            String numContaAlt = servidor.getAttribute(Columns.RSE_CONTA_SAL_2) != null ? TextHelper.formataParaComparacao(servidor.getAttribute(Columns.RSE_CONTA_SAL_2).toString()) : "";

            String numContaAlt1 = "";
            String numContaAlt2 = "";

            if (numContaAlt.length() > 0) {
                numContaAlt1 = numContaAlt.substring(0, numContaAlt.length() / 2);
                numContaAlt2 = numContaAlt.substring(numContaAlt.length() / 2, numContaAlt.length());
            } else {
                numContaAlt1 = numContaAlt2 = numContaAlt;
            }

            numContaAlt1 = JCryptOld.crypt("IB", numContaAlt1);
            numContaAlt2 = JCryptOld.crypt("IB", numContaAlt2);

            boolean rseTemInfBancaria = ((!TextHelper.isNull(servidor.getAttribute(Columns.RSE_BANCO_SAL)) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_AGENCIA_SAL)) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_CONTA_SAL))) || (!TextHelper.isNull(servidor.getAttribute(Columns.RSE_BANCO_SAL_2)) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2)) && !TextHelper.isNull(servidor.getAttribute(Columns.RSE_CONTA_SAL_2))));

            String linkAcao = "../v3/confirmarSolicitacao?acao=salvar";
            if (JspHelper.verificaVarQryStr(request, "TMO_CODIGO") != null) {
                String tmoCodigo = JspHelper.verificaVarQryStr(request, "TMO_CODIGO");
                String adeObs = JspHelper.verificaVarQryStr(request, "ADE_OBS");
                linkAcao = SynchronizerToken.updateTokenInURL(linkAcao + "&TMO_CODIGO=" + tmoCodigo + "&ADE_OBS=" + adeObs, request);
            }

            //Busca atributos quanto a exigencia de Tipo de motivo da operacao
            Object objMtvCancelamento = ParamSist.getInstance().getParam(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, responsavel);
            boolean exigeMotivo = (objMtvCancelamento != null && objMtvCancelamento.equals(CodedValues.TPC_SIM) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_CONF_SOLICITACAO, responsavel));

            model.addAttribute("responsavel", responsavel);
            model.addAttribute("adeTac", adeTac);
            model.addAttribute("adeOp", adeOp);
            model.addAttribute("cftVlrAtual", cftVlrAtual);
            model.addAttribute("temCET", temCET);
            model.addAttribute("exigeMotivo", exigeMotivo);
            model.addAttribute("ocultarCamposTac", ocultarCamposTac);
            model.addAttribute("rseTemInfBancaria", rseTemInfBancaria);
            model.addAttribute("exigeSenhaServidor", exigeSenhaServidor);
            model.addAttribute("validarInfBancaria", validarInfBancaria);
            model.addAttribute("exigeMatriculaSerCsa", exigeMatriculaSerCsa);
            model.addAttribute("simulacaoPorTaxaJuros", simulacaoPorTaxaJuros);
            model.addAttribute("exigeCodAutSolicitacao", exigeCodAutSolicitacao);
            model.addAttribute("exigeModalidadeOperacao", exigeModalidadeOperacao);
            model.addAttribute("pulaInformacaoValorPrazo", pulaInformacaoValorPrazo);
            model.addAttribute("podeAlterarValorParcela", podeAlterarValorParcela);
            model.addAttribute("simulacaoMetodoMexicano", simulacaoMetodoMexicano);
            model.addAttribute("simulacaoMetodoBrasileiro", simulacaoMetodoBrasileiro);
            model.addAttribute("serInfBancariaObrigatoria", serInfBancariaObrigatoria);
            model.addAttribute("paramSession", paramSession);
            model.addAttribute("cftdes", cftdes);
            model.addAttribute("cft", cft);
            model.addAttribute("autdes", autdes);
            model.addAttribute("numBanco", numBanco);
            model.addAttribute("numAgencia", numAgencia);
            model.addAttribute("numConta1", numConta1);
            model.addAttribute("numConta2", numConta2);
            model.addAttribute("numBancoAlt", numBancoAlt);
            model.addAttribute("numAgenciaAlt", numAgenciaAlt);
            model.addAttribute("numContaAlt1", numContaAlt1);
            model.addAttribute("numContaAlt2", numContaAlt2);
            model.addAttribute("linkAcao", linkAcao);
            model.addAttribute("rseCodigo", rseCodigo);
            model.addAttribute("adeCodigo", adeCodigo);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/confirmarSolicitacao/confirmarSolicitacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            ParamSession paramSession = ParamSession.getParamSession(session);
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String adeCodigo = request.getParameter("ADE_CODIGO");
            if (TextHelper.isNull(adeCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca a autorização
            CustomTransferObject autdes = null;
            try {
                autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
                autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
            } catch (AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_CONF_SOLICITACAO, responsavel.getUsuCodigo(), autdes.getAttribute(Columns.SVC_CODIGO).toString())) {
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
            String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();

            // Busca os parâmetros de serviço necessários
            boolean podeAlterarValorParcela = true;
            boolean exigeSenhaServidor = false;
            boolean comSerSenha = false;
            boolean serInfBancariaObrigatoria = false;
            boolean validarInfBancaria = false;
            boolean exigeCodAutSolicitacao = false;
            boolean pulaInformacaoValorPrazo = false;
            try {
                ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
                String tipoVlr   = paramSvcCse.getTpsTipoVlr(); // Tipo do valor: F (Fixo) / P (Percentual) / T (Total da Margem)
                // Se é tipo valor total da margem, não pode alterar o valor da parcela
                if (tipoVlr.equals(CodedValues.TIPO_VLR_TOTAL_MARGEM)) {
                    podeAlterarValorParcela = false;
                }
                exigeSenhaServidor = paramSvcCse.isTpsExigeSenhaConfirmacaoSolicitacao();
                serInfBancariaObrigatoria = paramSvcCse.isTpsInfBancariaObrigatoria();
                validarInfBancaria = paramSvcCse.isTpsValidarInfBancariaNaReserva();
                exigeCodAutSolicitacao = paramSvcCse.isTpsExigeCodAutorizacaoSolic();
                pulaInformacaoValorPrazo = paramSvcCse.isTpsPulaInformacaoValorPrazoFluxoReserva();

            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca as taxas utilizadas no contrato
            BigDecimal adeTac = null, adeOp = null;
            try {
                List<String> tpsCodigos = new ArrayList<>();
                tpsCodigos.add(CodedValues.TPS_TAC_FINANCIADA);
                tpsCodigos.add(CodedValues.TPS_OP_FINANCIADA);
                Map<String, String> taxas = autorizacaoController.getParamSvcADE(adeCodigo, tpsCodigos, responsavel);
                adeTac = taxas.get(CodedValues.TPS_TAC_FINANCIADA) == null ? new BigDecimal("0") : new BigDecimal(taxas.get(CodedValues.TPS_TAC_FINANCIADA).toString());
                adeOp  = taxas.get(CodedValues.TPS_OP_FINANCIADA) == null ? new BigDecimal("0") : new BigDecimal(taxas.get(CodedValues.TPS_OP_FINANCIADA).toString());
            } catch (AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca o coeficiente através do CoeficienteDesconto
            CustomTransferObject cftdes = null;
            TransferObject cft = null;
            BigDecimal cftVlrAtual = null;
            try {
                cftdes = simulacaoController.findCdeByAdeCodigo(adeCodigo, responsavel);
                cft = coeficienteController.getCoeficiente(cftdes.getAttribute(Columns.CDE_CFT_CODIGO).toString(), responsavel);
                cftVlrAtual = new BigDecimal(cft.getAttribute(Columns.CFT_VLR).toString());
            } catch (Exception ex) {
                LOG.error(ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage());
            }

            // Busca os dados do registro servidor
            CustomTransferObject servidor = null;
            String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
            try {
                servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            } catch (ServidorControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            //Desenv-11823
           //verifica se deve gravar os campos de valor e prazo digitados pelo usuário.
            try {
                CustomTransferObject naturezaSvc = servicoController.findNaturezaServico(svcCodigo, responsavel);
                pulaInformacaoValorPrazo = ( pulaInformacaoValorPrazo && naturezaSvc != null && !TextHelper.isNull(naturezaSvc.getAttribute(Columns.NSE_CODIGO)) && !naturezaSvc.getAttribute(Columns.NSE_CODIGO).toString().equals(CodedValues.NSE_EMPRESTIMO));
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Salva a confirmação da solicitação
            try {
                String senhaAberta = null;
                if (exigeSenhaServidor) {
                    boolean dispensaValidacaoDigital = false;
                    if (ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel)) {
                        ServidorTransferObject servidorTO = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);
                        dispensaValidacaoDigital = !TextHelper.isNull(servidorTO.getSerDispensaDigital()) && servidorTO.getSerDispensaDigital().equals(CodedValues.TPC_SIM);
                    }

                    boolean validaDigitais = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_VALIDACAO_DIGITAL_SERVIDOR, responsavel) && !dispensaValidacaoDigital;
                    boolean digitalServidorValidada = (session.getAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_VALIDA) != null && rseCodigo.equals(session.getAttribute(CodedNames.ATTR_SESSION_SER_DIGITAL_VALIDA).toString()));
                    if (validaDigitais && digitalServidorValidada) {
                        comSerSenha = true;
                    } else {
                        // Valida Senha de Autorização do Servidor
                        String senhaCriptografada = JspHelper.verificaVarQryStr(request, JspHelper.verificaVarQryStr(request, "cryptedPasswordFieldName"));
                        if (senhaCriptografada != null && !senhaCriptografada.equals("")) {
                            KeyPair keyPair = LoginHelper.getRSAKeyPair(request);
                            senhaAberta = RSA.decrypt(senhaCriptografada, keyPair.getPrivate());
                        }
                        if (senhaAberta != null && !senhaAberta.equals("")) {
                            try {
                                SenhaHelper.validarSenhaServidor(rseCodigo, senhaAberta, JspHelper.getRemoteAddr(request), request.getParameter("serLogin"), null, true, false, responsavel);
                                comSerSenha = true;
                            } catch (UsuarioControllerException ex) {
                                // Paraná: ao receber 'senha expirada' a CSA poderá ativar a senha.
                                if (ex.getMessageKey().indexOf("mensagem.erro.senha.expirada.certifique.ativacao") != -1) {
                                    session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.expirada.ativar", responsavel));
                                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                                    return "jsp/redirecionador/redirecionar";
                                } else {
                                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                                }
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                        } else if (!TextHelper.isNull(request.getParameter("tokenOAuth2"))) {
                            SenhaHelper.validarSenha(request, rseCodigo, svcCodigo, true, true, responsavel);
                        } else {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ser.senha", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                }

                // Dados enviados pelo usuário
                String numBanco = !JspHelper.verificaVarQryStr(request, "numBanco").equals("") ? JspHelper.verificaVarQryStr(request, "numBanco") : null;
                String numAgencia = !JspHelper.verificaVarQryStr(request, "numAgencia").equals("") ? JspHelper.verificaVarQryStr(request, "numAgencia") : null;
                String numConta = !JspHelper.verificaVarQryStr(request, "numConta").equals("") ? JspHelper.verificaVarQryStr(request, "numConta") : null;

                //inicia variáveis
                BigDecimal vlrParcela = null;
                Integer adePrazo = null;

                if (pulaInformacaoValorPrazo && responsavel.isCsaCor()) {
                    vlrParcela = new BigDecimal(JspHelper.verificaVarQryStr(request, "ADE_VLR"));
                    adePrazo = Integer.valueOf(JspHelper.verificaVarQryStr(request, "ADE_PRAZO"));


                }

                if (serInfBancariaObrigatoria && validarInfBancaria) {
                    // Dados bancários do servidor
                    String rseBancoSal   = servidor.getAttribute(Columns.RSE_BANCO_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_BANCO_SAL).toString(), "0", JspHelper.ESQ) : "";
                    String rseAgenciaSal = servidor.getAttribute(Columns.RSE_AGENCIA_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL).toString(), "0", JspHelper.ESQ) : "";
                    String rseContaSal   = servidor.getAttribute(Columns.RSE_CONTA_SAL) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_CONTA_SAL).toString(), "0", JspHelper.ESQ) : "";
                    String rseBancoSalAlt   = servidor.getAttribute(Columns.RSE_BANCO_SAL_2) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_BANCO_SAL_2).toString(), "0", JspHelper.ESQ) : "";
                    String rseAgenciaSalAlt = servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_AGENCIA_SAL_2).toString(), "0", JspHelper.ESQ) : "";
                    String rseContaSalAlt   = servidor.getAttribute(Columns.RSE_CONTA_SAL_2) != null ? JspHelper.removePadrao(servidor.getAttribute(Columns.RSE_CONTA_SAL_2).toString(), "0", JspHelper.ESQ) : "";

                    // Se as informações bancárias são obrigatórias e devem ser válidas,
                    // então valida as informações digitadas pelo usuário
                    if ((!TextHelper.formataParaComparacao(rseBancoSal).equals(TextHelper.formataParaComparacao(numBanco)) ||
                            !TextHelper.formataParaComparacao(rseAgenciaSal).equals(TextHelper.formataParaComparacao(numAgencia)) ||
                            !TextHelper.formataParaComparacao(rseContaSal).equals(TextHelper.formataParaComparacao(numConta))) &&
                            (!TextHelper.formataParaComparacao(rseBancoSalAlt).equals(TextHelper.formataParaComparacao(numBanco)) ||
                                    !TextHelper.formataParaComparacao(rseAgenciaSalAlt).equals(TextHelper.formataParaComparacao(numAgencia)) ||
                                    !TextHelper.formataParaComparacao(rseContaSalAlt).equals(TextHelper.formataParaComparacao(numConta)))) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informacaoBancariaIncorreta", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                String paramAlteraVlrLiberado = JspHelper.verificaVarQryStr(request, "ALTERA_VLR_LIBERADO");
                String adeIdentificador       = JspHelper.verificaVarQryStr(request, "ADE_IDENTIFICADOR");
                String cftVlr                 = JspHelper.verificaVarQryStr(request, "CFT_VLR");
                String codAutorizacao         = JspHelper.verificaVarQryStr(request, "codAutorizacao");
                String tdaModalidadeOp        = JspHelper.verificaVarQryStr(request, "tdaModalidadeOp");
                String tdaMatriculaCsa        = JspHelper.verificaVarQryStr(request, "tdaMatriculaCsa");

                if (exigeCodAutSolicitacao && TextHelper.isNull(codAutorizacao)) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.ade.codigo.autorizacao", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                String corCodigo = responsavel.isCor() ? responsavel.getCodigoEntidade() : null;

                // Motivo da operacao
                CustomTransferObject tmo = null;
                if (!TextHelper.isNull(request.getParameter("TMO_CODIGO"))) {
                    tmo = new CustomTransferObject();
                    tmo.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                    tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                    tmo.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                }

                if (cftVlr.equals("")) {
                    // Apenas confirma a Solicitação
                    confirmarConsignacaoController.confirmar(adeCodigo, vlrParcela, adeIdentificador, numBanco, numAgencia, numConta, corCodigo, adePrazo, senhaAberta, codAutorizacao, comSerSenha, tdaModalidadeOp, tdaMatriculaCsa, tmo, responsavel);
                } else {
                    // Se o novo valor é menor que o valor atual e é maior que zero, altera o coeficiente
                    // e o vlr liberado ou o valor da parcela
                    BigDecimal cftVlrNovo = new BigDecimal(NumberHelper.reformat(cftVlr, NumberHelper.getLang(), "en", 2, 8));
                    if (cftVlrNovo.compareTo(cftVlrAtual) == -1 && cftVlrNovo.doubleValue() > 0) {

                        vlrParcela  = new BigDecimal(autdes.getAttribute(Columns.ADE_VLR).toString());
                        BigDecimal vlrLiberado = new BigDecimal(cftdes.getAttribute(Columns.CDE_VLR_LIBERADO).toString());
                        String adePeriodicidade = (String) autdes.getAttribute(Columns.ADE_PERIODICIDADE);
                        int przVlr = Integer.parseInt(autdes.getAttribute(Columns.ADE_PRAZO).toString());
                        String orgCodigo = autdes.getAttribute(Columns.ORG_CODIGO).toString();
                        boolean alteraVlrLiberado = paramAlteraVlrLiberado.equals("S");

                        BigDecimal retorno = simulacaoController.alterarValorTaxaJuros(alteraVlrLiberado, vlrParcela, vlrLiberado, cftVlrNovo, adeTac, adeOp, przVlr, orgCodigo, svcCodigo, csaCodigo, adePeriodicidade, responsavel);

                        if (alteraVlrLiberado) {
                            vlrLiberado = retorno;
                        } else if (podeAlterarValorParcela) {
                            vlrParcela = retorno;
                        } else {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }

                        if (pulaInformacaoValorPrazo && responsavel.isCsaCor()) {
                            vlrParcela = new BigDecimal(JspHelper.verificaVarQryStr(request, "ADE_VLR"));
                            adePrazo = Integer.valueOf(JspHelper.verificaVarQryStr(request, "ADE_PRAZO"));
                            if (adePrazo.equals(null) && vlrParcela.equals(null) ) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.pula.etapa.obrigatorios", responsavel));
                                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                            }
                        }

                        // Confirma a solicitação
                        confirmarConsignacaoController.confirmar(adeCodigo, vlrParcela, adeIdentificador, numBanco, numAgencia, numConta, corCodigo, adePrazo, senhaAberta, codAutorizacao, comSerSenha, tdaModalidadeOp, tdaMatriculaCsa, tmo, responsavel);

                        if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                            autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO,
                                    (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA),
                                    (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                            session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
                            session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
                        }

                        // Grava novo coeficiente, passa o coeficiente com formato em NumberHelper.getLang()
                        cft.setAttribute(Columns.CFT_VLR, cftVlr);
                        cft.setAttribute(Columns.CFT_CODIGO, null);
                        String cft_codigo = coeficienteController.insertCoeficiente(cft, responsavel);

                        // Atualiza o coeficiente desconto
                        simulacaoController.updateCoeficienteDesconto(cftdes.getAttribute(Columns.CDE_CODIGO).toString(), cft_codigo, vlrLiberado, responsavel);

                    } else if (cftVlrNovo.compareTo(cftVlrAtual) == 0) {
                        // Se os coeficientes são iguais, apenas confirma a Solicitação
                        confirmarConsignacaoController.confirmar(adeCodigo, vlrParcela, adeIdentificador, numBanco, numAgencia, numConta, corCodigo, adePrazo, senhaAberta, codAutorizacao, comSerSenha, tdaModalidadeOp, tdaMatriculaCsa, tmo, responsavel);
                    } else {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.coeficiente.menor.anterior", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.solicitacao.confirmada", responsavel));
                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));

            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return "jsp/redirecionador/redirecionar";
    }

}
