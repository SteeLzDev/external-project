package com.zetra.econsig.web.controller.servidor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ConfirmarMargemFolhaServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso confirmar margem folha do servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/confirmarMargemFolhaServidor" })
public class ConfirmarMargemFolhaServidorWebController extends ControlePaginacaoWebController {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfirmarMargemFolhaServidorWebController.class);

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            ParamSession paramSession = ParamSession.getParamSession(session);
            String linkRet = JspHelper.verificaVarQryStr(request, "linkRet");

            if (TextHelper.isNull(linkRet)) {
                linkRet = (SynchronizerToken.updateTokenInURL((responsavel.isSer()) ? "../v3/carregarPrincipal" : paramSession.getLastHistory(), request));
            } else {
                linkRet = linkRet.replace('$', '?').replace('(', '=').replace('|', '&');
                linkRet += ((linkRet.indexOf("?") > -1) ? "&" : "?") + SynchronizerToken.generateToken4URL(request);
            }
            String detalheAut = JspHelper.verificaVarQryStr(request, "detalheAut");
            String linkRetorno = JspHelper.verificaVarQryStr(request, "linkRet");

            if (!TextHelper.isNull(detalheAut)) {
                linkRet = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
            }

            if (TextHelper.isNull(linkRetorno)) {
                linkRetorno = linkRet.replace('?', '$').replace('&', '|').replace('=', '(');
            } else {
                linkRet = SynchronizerToken.updateTokenInURL(linkRetorno.replace('$', '?').replace('|', '&').replace('(', '='), request);
            }

            List<String> estCodigos = new ArrayList<>();
            List<String> orgCodigos = new ArrayList<>();

            if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                estCodigos.add(responsavel.getCodigoEntidadePai());
            } else if (responsavel.isOrg()) {
                orgCodigos.add(responsavel.getCodigoEntidade());
            }

            List<TransferObject> servidores = pesquisarServidorController.listarServidorMargemFolha(orgCodigos, estCodigos, responsavel);

            // Monta lista de descrições das margens possíveis para os servidores
            List<Short> incidencias = new ArrayList<>();
            List<TransferObject> descricoesMargens = new ArrayList<>();
            if (servidores != null && !servidores.isEmpty()) {
                for (TransferObject servidor : servidores) {
                    Map<Short, MargemTO> margens = (Map<Short, MargemTO>) servidor.getAttribute("MARGENS");
                    for (MargemTO margem : margens.values()) {
                        if (!incidencias.contains(margem.getMarCodigo())) {
                            TransferObject to = new CustomTransferObject();
                            to.setAttribute(Columns.MAR_CODIGO, margem.getMarCodigo());
                            to.setAttribute(Columns.MAR_DESCRICAO, margem.getMarDescricao());

                            descricoesMargens.add(to);
                            incidencias.add(margem.getMarCodigo());
                        }
                    }
                }
            }

            model.addAttribute("linkRet", linkRet);
            model.addAttribute("linkRetorno", linkRetorno);
            model.addAttribute("servidores", servidores);
            model.addAttribute("descricoesMargens", descricoesMargens);

            return viewRedirect("jsp/consultarServidor/confirmarMargemFolhaServidor", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=salvar" })
    public String salvar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token de sessão para evitar a chamada direta à operação
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            ParamSession paramSession = ParamSession.getParamSession(session);
            String operacao = JspHelper.verificaVarQryStr(request, "operacao");

            String[] strRseCodigos = request.getParameterValues("chkConfirmarMargem");
            List<String> rseCodigos = strRseCodigos != null ? Arrays.asList(strRseCodigos) : new ArrayList<>();

            if (rseCodigos.isEmpty()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.pelo.menos.um.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            if (operacao.equals("confirmar")) {
                servidorController.confirmarMargemFolha(rseCodigos, responsavel);
            } else {
                servidorController.rejeitarMargemFolha(rseCodigos, responsavel);
            }

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.confirmar.margem.folha.concluido.sucesso", responsavel));

            request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
            return "jsp/redirecionador/redirecionar";

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.confirmar.margem.folha.servidor.titulo", responsavel));
    }
}
