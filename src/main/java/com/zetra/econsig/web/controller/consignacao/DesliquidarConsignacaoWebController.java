package com.zetra.econsig.web.controller.consignacao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.GeraTelaSegundaSenhaHelper;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: DesliquidarConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso DesliquidarConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/desliquidarConsignacao" })
public class DesliquidarConsignacaoWebController extends AbstractEfetivarAcaoConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DesliquidarConsignacaoWebController.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private LiquidarConsignacaoController liquidarConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.desliquidar.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/desliquidarConsignacao");
        model.addAttribute("nomeCampo", "chkADE");
        model.addAttribute("imageHeader", "i-operacional");
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String urlDestino = "../v3/desliquidarConsignacao?acao=desliquidar";
        String funCodigo = CodedValues.FUN_DESLIQ_CONTRATO;

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return super.informarMotivoOperacao(funCodigo, urlDestino, null, request, response, session, model);
        }
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        List<String> sadCodigos = new ArrayList<>();
        sadCodigos.add(CodedValues.SAD_LIQUIDADA);
        return sadCodigos;
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        List<AcaoConsignacao> acoes = new ArrayList<>();

        // Adiciona opção para liquidar consignação
        String link = (responsavel.temPermissao(CodedValues.FUN_DESLIQUIDACAO_AVANCADA_CONTRATO) ? "../v3/desliquidarConsignacao?acao=confirmarDesliquidacao" : "../v3/desliquidarConsignacao?acao=efetivarAcao");
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.desliquidar.abreviado", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.selecionar", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.desliquidar.consignacao.clique.aqui", responsavel);
        String msgConfirmacao = (!responsavel.temPermissao(CodedValues.FUN_DESLIQUIDACAO_AVANCADA_CONTRATO) ? ApplicationResourcesHelper.getMessage("mensagem.confirmacao.desliquidacao", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.confirmacao.multiplo.desliquidacao", responsavel));
        String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("DESLIQ_CONTRATO", CodedValues.FUN_DESLIQ_CONTRATO, descricao, descricaoCompleta, "liquidar_contrato_des.gif", "btnDesliquidarConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, "chkADE"));

        // Adiciona o editar consignação
        link = "../v3/desliquidarConsignacao?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";
        msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null,null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "desliquidar");
        return criterio;
    }

    @RequestMapping(params = { "acao=confirmarDesliquidacao" })
    public String confirmarDesliquidacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            // Valida o token de sessao para evitar a chamada direta da operacao
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String adeCodigo = request.getParameter("ADE_CODIGO");
            String[] adeCodigos = request.getParameterValues("chkADE");

            List<String> adeCodigoList = new ArrayList<>();
            if (!TextHelper.isNull(adeCodigo)) {
                adeCodigoList.add(adeCodigo);
            } else if (adeCodigos != null && adeCodigos.length > 0) {
                adeCodigoList.addAll(Arrays.asList(adeCodigos));
            }

            if (adeCodigoList.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Busca a autorização
            List<TransferObject> autdesList = null;
            try {
                autdesList = pesquisarConsignacaoController.buscaAutorizacao(adeCodigoList, true, responsavel);
            } catch (AutorizacaoControllerException ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            boolean exigeMotivo = (ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_CANC, CodedValues.TPC_SIM, responsavel) && FuncaoExigeMotivo.getInstance().exists(CodedValues.FUN_DESLIQ_CONTRATO, responsavel));
            boolean exportacaoInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel);

            boolean marFicaraNegativa = false;
            boolean adeJaEnviadaFolha = false;

            Iterator<TransferObject> it = autdesList.iterator();
            TransferObject autdes = null;

            while (it.hasNext()) {
                autdes = it.next();
                autdes = TransferObjectHelper.mascararUsuarioHistorico((CustomTransferObject) autdes, null, responsavel);

                if (!marFicaraNegativa) {
                    String rseCodigo = autdes.getAttribute(Columns.RSE_CODIGO).toString();
                    String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
                    String csaCodigo = autdes.getAttribute(Columns.CSA_CODIGO).toString();
                    Short adeIncMargem = (Short) autdes.getAttribute(Columns.ADE_INC_MARGEM);
                    BigDecimal adeVlr = (BigDecimal) autdes.getAttribute(Columns.ADE_VLR);

                    MargemDisponivel margemDisponivel = null;
                    try {
                        margemDisponivel = new MargemDisponivel(rseCodigo, csaCodigo, svcCodigo, adeIncMargem, responsavel);
                    } catch (ViewHelperException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }

                    BigDecimal rseMargem = margemDisponivel.getMargemRestante();
                    marFicaraNegativa = (rseMargem.subtract(adeVlr).signum() < 0) ? true : false;
                }

                if (!adeJaEnviadaFolha) {
                    // Verifica se usuário tem permissão de desliquidação avançada e se margem ficará negativa caso este opte por esta operação.
                    if (responsavel.temPermissao(CodedValues.FUN_DESLIQUIDACAO_AVANCADA_CONTRATO)) {
                        // verifica se liquidação já foi enviada para folha
                        adeJaEnviadaFolha = liquidarConsignacaoController.liquidacaoJaEnviadaParaFolha(autdes.getAttribute(Columns.ADE_CODIGO).toString(), responsavel);
                    }
                }
            }

            model.addAttribute("autdesList", autdesList);
            model.addAttribute("exigeMotivo", exigeMotivo);
            model.addAttribute("exportacaoInicial", exportacaoInicial);
            model.addAttribute("marFicaraNegativa", marFicaraNegativa);
            model.addAttribute("adeJaEnviadaFolha", adeJaEnviadaFolha);

            return viewRedirect("jsp/desliquidarConsignacao/confirmarDesliquidacao", request, session, model, responsavel);
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=desliquidar" })
    public String desliquidar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        // Valida o token de sessao para evitar a chamada direta da operacao
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String[] adeCodigos = request.getParameterValues("chkADE");
        if ((adeCodigos == null || adeCodigos.length == 0) && request.getParameter("ADE_CODIGO") != null) {
            adeCodigos = new String[1];
            adeCodigos[0] = request.getParameter("ADE_CODIGO");
        }

        if (adeCodigos == null || adeCodigos.length == 0) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean validaMargem = (!TextHelper.isNull(request.getParameter("VALIDA_MARGEM"))) ? Boolean.valueOf(request.getParameter("VALIDA_MARGEM")) : true;
        boolean reimplanta = (!TextHelper.isNull(request.getParameter("REIMPLANTAR"))) ? Boolean.valueOf(request.getParameter("REIMPLANTAR")) : false;
        boolean compra = (!TextHelper.isNull(request.getParameter("COMPRA"))) ? Boolean.valueOf(request.getParameter("COMPRA")) : false;

        String msg = "";
        for (String adeCodigo : adeCodigos) {
            try {
                CustomTransferObject tmo = null;
                if (!TextHelper.isNull(request.getParameter("TMO_CODIGO"))) {
                    tmo = new CustomTransferObject();
                    tmo.setAttribute(Columns.ADE_CODIGO, adeCodigo);
                    tmo.setAttribute(Columns.TMO_CODIGO, JspHelper.verificaVarQryStr(request, "TMO_CODIGO"));
                    tmo.setAttribute(Columns.OCA_OBS, JspHelper.verificaVarQryStr(request, "ADE_OBS"));
                }
                liquidarConsignacaoController.desliquidar(adeCodigo, validaMargem, reimplanta, compra, tmo, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.desliquidar.consignacao.concluido.sucesso", responsavel));

                if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
                    autorizacaoController.criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_OP_SEGUNDO_USUARIO, (String) session.getAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA), (AcessoSistema) session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA));
                }
            } catch (AutorizacaoControllerException ex) {
                msg += ex.getMessage() + "<BR>";
                session.removeAttribute(CodedValues.MSG_INFO);
            }
        }

        if (session.getAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA) != null) {
            session.removeAttribute(GeraTelaSegundaSenhaHelper.RESPONSAVEL_2A_SENHA);
            session.removeAttribute(GeraTelaSegundaSenhaHelper.OCA_OBS_2A_SENHA);
        }

        session.setAttribute(CodedValues.MSG_ERRO, msg);
        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
        return "jsp/redirecionador/redirecionar";
    }
}
