package com.zetra.econsig.web.controller.textosistema;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.TextoSistemaTO;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.MensagemControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.sistema.TextoSistemaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ManterTextoSistemaWebController</p>
 * <p>Description: Controlador Web para o caso de uso Manter Textos do Sistema.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/manterTextoSistema" })
public class ManterTextoSistemaWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ManterTextoSistemaWebController.class);

    @Autowired
    private TextoSistemaController textoSistemaController;

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sesssão para evitar a chamada direta da operação
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        int qtdColunas = 4;

        List<TransferObject> lstTextosSistema = null;
        List<TransferObject> lstTextos = new ArrayList<>();
        /* FILTRO DE MENSAGEM */
        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        int filtro_tipo = -1;
        try {
            if (!JspHelper.verificaVarQryStr(request, "FILTRO_TIPO").isEmpty()) {
                filtro_tipo = Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));
            }
        } catch (Exception ex1) {
            session.setAttribute(CodedValues.MSG_ERRO, ex1.getMessage());
            LOG.error(ex1);
        }
        try {
            CustomTransferObject criterio = new CustomTransferObject();

            // -------------- Seta Criterio da Listagem ------------------
            String campo = null;

            switch (filtro_tipo) {
                case 2:
                    campo = Columns.TEX_CHAVE;
                    break;
                case 3:
                    campo = Columns.TEX_TEXTO;
                    break;
                case 4:
                case 5:
                    campo = Columns.TEX_DATA_ALTERACAO;
                    break;
                default:
                    campo = null;
                    break;
            }
            // Pesquisa feita pelo título, consignatária ou correspondente de csa
            if (filtro != null && (filtro_tipo == 2 || filtro_tipo == 3)) {
                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            } else if (filtro_tipo == 4) {
                criterio.setAttribute(campo, CodedValues.IS_NOT_NULL_KEY);
            } else if (filtro_tipo == 5) {
                criterio.setAttribute(campo, CodedValues.IS_NULL_KEY);
            } else {
                criterio.setAttribute(campo, "");
            }
            int total = 0;
            try {
                total = textoSistemaController.countTextoSistema(criterio, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                if (request.getParameter("offset") != null) {
                    offset = Integer.parseInt(request.getParameter("offset"));
                }
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                LOG.error(ex.getMessage(), ex);
            }

            lstTextosSistema = textoSistemaController.lstTextoSistema(criterio, offset, size, responsavel);

                for (TransferObject textoSistemas : lstTextosSistema) {
                    String textoSistema = (String) textoSistemas.getAttribute(Columns.TEX_TEXTO);
                    if (textoSistema.contains("markdown(")) {
                        textoSistemas.setAttribute(Columns.TEX_TEXTO, ApplicationResourcesHelper.removeMarcacaoMarkdown(textoSistema));
                    }
                    lstTextos.add(textoSistemas);
                }

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("senha");
            params.remove("serAutorizacao");
            params.remove("cryptedPasswordFieldName");
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            List<String> requestParams = new ArrayList<>(params);
            configurarPaginador("../v3/manterTextoSistema?acao=listar&FILTRO=" + filtro + "&FILTRO_TIPO=" + filtro_tipo, "rotulo.paginacao.listagem.textos.sistema", total, size, requestParams, false, request, model);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("lstTextos", lstTextos);
        model.addAttribute("qtdColunas", qtdColunas);
        return viewRedirect("jsp/manterTextoSistema/listarTextoSistema", request, session, model, responsavel);
    }

    private void setModelValues(HttpServletRequest request, HttpSession session, Model model) throws ConsignanteControllerException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            throw new ConsignanteControllerException("mensagem.usoIncorretoSistema", responsavel);
        }
        SynchronizerToken.saveToken(request);

        String texChave = JspHelper.verificaVarQryStr(request, "texChave");
        if (TextHelper.isNull(texChave)) {
            throw new ConsignanteControllerException("mensagem.usoIncorretoSistema", responsavel);
        }

        TextoSistemaTO textoSistemaTO = new TextoSistemaTO(texChave);
        if (!TextHelper.isNull(texChave)) {
            // Apenas lista a mensagem existente para edição posterior
            textoSistemaTO = textoSistemaController.findTextoSistema(textoSistemaTO, responsavel);
            textoSistemaTO.setTexTexto(ApplicationResourcesHelper.removeMarcacaoMarkdown(textoSistemaTO.getTexTexto()));
        }

        String btnCancelar = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
        model.addAttribute("btnCancelar", btnCancelar);
        model.addAttribute("textoSistemaTO", textoSistemaTO);
        model.addAttribute("dataAlteracao", DateHelper.toDateTimeString(textoSistemaTO.getTexDataAlteracao()));
        model.addAttribute("responsavel", responsavel);
    }

    @RequestMapping(params = { "acao=visualizar" })
    public String visualizar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            setModelValues(request, session, model);

            return viewRedirect("jsp/manterTextoSistema/visualizarTextoSistema", request, session, model, responsavel);

        } catch (ConsignanteControllerException | InstantiationException | IllegalAccessException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=editar" })
    public String editar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {
            setModelValues(request, session, model);

            return viewRedirect("jsp/manterTextoSistema/editarTextoSistema", request, session, model, responsavel);

        } catch (ConsignanteControllerException | InstantiationException | IllegalAccessException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    /**
     * Verificar rasteiramente se existe tag no texto.
     * @param texto
     * @return
     */
    private boolean temTag(String texto) {
        int pos = texto.indexOf('<');
        while (pos != -1) {
            pos++;
            if (pos < texto.length() && texto.charAt(pos) != ' ') {
                return true;
            }
            pos = texto.indexOf('<', pos);
        }
        return false;
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, MensagemControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String texChave = JspHelper.verificaVarQryStr(request, "texChave");
        String texTexto = JspHelper.verificaVarQryStr(request, "innerTemp");
        // Faz as substituições necessárias para que o editor possa ler o que foi salvo no banco de dados
        texTexto = texTexto.replaceAll("&quot;", "\"");

        // Não permite salvar se houver tags.
        if (temTag(texTexto)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.texto.sistema.edicao.bloqueada", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        TextoSistemaTO textoSistemaTO = new TextoSistemaTO(texChave);
        // Apenas lista a mensagem existente para edição posterior
        try {
            textoSistemaTO = textoSistemaController.findTextoSistema(textoSistemaTO, responsavel);

            if (!TextHelper.isNull(texTexto)) {
                textoSistemaTO.setTexTexto(ApplicationResourcesHelper.addMarcacaoMarkdown(texTexto));
            } else {
                // Aviso caso a mensagem esteja sendo salva sem um texto
                throw new ZetraException("mensagem.erro.escreva.texto.texto.sistema", responsavel);
            }

            // Faz o update se é edição da mensagem
            textoSistemaController.updateTextoSistema(textoSistemaTO, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alteracoes.salvas.sucesso", responsavel));
            ApplicationResourcesHelper.getInstance().reset();
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Repassa o token salvo, pois o método irá revalidar o token
        request.setAttribute(SynchronizerToken.TRANSACTION_TOKEN_KEY, SynchronizerToken.getSessionToken(request));

        return editar(request, response, session, model);
    }
}
