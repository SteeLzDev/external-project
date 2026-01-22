package com.zetra.econsig.web.controller.penalidade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.sistema.PenalidadeController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarTipoPenalidade" })
public class EditarTipoPenalidadeWebController extends AbstractWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EditarTipoPenalidadeWebController.class);

    @Autowired
    private PenalidadeController penalidadeController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        boolean possuiDesblAutCsaPrazoPenalidade = ParamSist.getBoolParamSist(CodedValues.TPC_DESBL_AUTOMAT_CSA_PRAZO_PENALIDADE, responsavel);

        boolean temPermissaoEditar = responsavel.temPermissao(CodedValues.FUN_EDT_TIPO_PENALIDADE);

        List<TransferObject> penalidades = null;

        try {
            penalidades = penalidadeController.lstTiposPenalidade(responsavel);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("possuiDesblAutCsaPrazoPenalidade", possuiDesblAutCsaPrazoPenalidade);
        model.addAttribute("temPermissaoEditar", temPermissaoEditar);
        model.addAttribute("penalidades", penalidades);

        return viewRedirect("jsp/penalidade/listarTipoPenalidade", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            boolean possuiDesblAutCsaPrazoPenalidade = ParamSist.getBoolParamSist(CodedValues.TPC_DESBL_AUTOMAT_CSA_PRAZO_PENALIDADE, responsavel);
            ParamSession paramSession = ParamSession.getParamSession(session);

            String tpeCodigo = JspHelper.verificaVarQryStr(request, "TPE_CODIGO");

            String linkVoltar = JspHelper.verificaVarQryStr(request, "link_voltar");
            String link = linkVoltar.equals("") ? paramSession.getLastHistory() : linkVoltar;
            link = SynchronizerToken.updateTokenInURL(link, request);

            String reqColumnsStr = "TPE_DESCRICAO";
            String msgErro = ""; //JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");

            String descricao = "";
            String prazo = "";
            if (!TextHelper.isNull(tpeCodigo)) {
                try {
                    TransferObject to = penalidadeController.findTipoPenalidade(tpeCodigo, responsavel);
                    descricao = to.getAttribute(Columns.TPE_DESCRICAO).toString();
                    prazo = !TextHelper.isNull(to.getAttribute(Columns.TPE_PRAZO_PENALIDADE)) ? to.getAttribute(Columns.TPE_PRAZO_PENALIDADE).toString() : "";

                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }

            model.addAttribute("possuiDesblAutCsaPrazoPenalidade", possuiDesblAutCsaPrazoPenalidade);
            model.addAttribute("reqColumnsStr", reqColumnsStr);
            model.addAttribute("msgErro", msgErro);
            model.addAttribute("descricao", descricao);
            model.addAttribute("prazo", prazo);
            model.addAttribute("link", link);
            model.addAttribute("tpeCodigo", tpeCodigo);

            return viewRedirect("jsp/penalidade/editarTipoPenalidade", request, session, model, responsavel);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String modificar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            ParamSession paramSession = ParamSession.getParamSession(session);

            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String linkVoltar = JspHelper.verificaVarQryStr(request, "link_voltar");
            String link = linkVoltar.equals("") ? paramSession.getLastHistory() : linkVoltar;
            String codigo = JspHelper.verificaVarQryStr(request, "tpeCodigo");
            String descricao = JspHelper.verificaVarQryStr(request, "tpeDescricao");

            //Inclui ou altera tipo de penalidade
            if (!TextHelper.isNull(descricao)) {
                // Salva os campos do tipo de penalidade

                try {
                    String tpeDescricao = JspHelper.verificaVarQryStr(request, "tpeDescricao");
                    Short prazo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "tpePrazoPenalidade")) ? Short.valueOf(JspHelper.verificaVarQryStr(request, "tpePrazoPenalidade")) : null;

                    if (TextHelper.isNull(codigo)) {
                        penalidadeController.insereTipoPenalidade(descricao, prazo, responsavel);
                    } else {
                        penalidadeController.alteraTipoPenalidade(codigo, descricao, prazo, responsavel);
                    }

                    model.addAttribute("tpeDescricao", tpeDescricao);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.penalidade.alteracoes.salvas.sucesso", responsavel));

                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            model.addAttribute("linkVoltar", linkVoltar);
            model.addAttribute("link", link);
            //request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

            return iniciar(request, response, session, model);

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String codigo = JspHelper.verificaVarQryStr(request, "tpeCodigo");
        String descricao = JspHelper.verificaVarQryStr(request, "tpeDescricao");

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            penalidadeController.excluiTipoPenalidade(codigo, descricao, responsavel);
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.penalidade.excluida.sucesso", responsavel));
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.penalidade.excluida.erro", responsavel));
            LOG.error(ex.getMessage(), ex);
        }

        return iniciar(request, response, session, model);
    }
}
