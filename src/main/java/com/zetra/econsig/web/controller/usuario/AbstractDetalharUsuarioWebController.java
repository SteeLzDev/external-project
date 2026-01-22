package com.zetra.econsig.web.controller.usuario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: DetalharUsuarioWebController</p>
 * <p>Description: Controlador Web para o caso de uso Detalhar Usuário.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractDetalharUsuarioWebController extends AbstractListarUsuarioPapelWebController {

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        UsuarioTransferObject usuario = null;

        String usuCodigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");
        UsuarioTransferObject usu = null;
        try {
            usu = usuarioController.findUsuario(usuCodigo, responsavel);
        } catch (UsuarioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String usuLogin = usu.getUsuLogin();

        CustomTransferObject tipoUsuario = null;
        try {
            tipoUsuario = usuarioController.findTipoUsuarioByLogin(usuLogin, responsavel);
        } catch (UsuarioControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String tipo = "";
        String codEntidade = "";
        String cseCodigo = tipoUsuario.getAttribute(Columns.UCE_CSE_CODIGO) != null ? tipoUsuario.getAttribute(Columns.UCE_CSE_CODIGO).toString() : "";
        String csaCodigo = tipoUsuario.getAttribute(Columns.UCA_CSA_CODIGO) != null ? tipoUsuario.getAttribute(Columns.UCA_CSA_CODIGO).toString() : "";
        String corCodigo = tipoUsuario.getAttribute(Columns.UCO_COR_CODIGO) != null ? tipoUsuario.getAttribute(Columns.UCO_COR_CODIGO).toString() : "";
        String orgCodigo = tipoUsuario.getAttribute(Columns.UOR_ORG_CODIGO) != null ? tipoUsuario.getAttribute(Columns.UOR_ORG_CODIGO).toString() : "";
        String cspCodigo = tipoUsuario.getAttribute(Columns.USP_CSE_CODIGO) != null ? tipoUsuario.getAttribute(Columns.USP_CSE_CODIGO).toString() : "";

        // Determina o tipo da entidade do usuário
        if (!cseCodigo.equals("")) {
            tipo = AcessoSistema.ENTIDADE_CSE;
            codEntidade = cseCodigo;
        } else if (!csaCodigo.equals("")) {
            tipo = AcessoSistema.ENTIDADE_CSA;
            codEntidade = csaCodigo;
        } else if (!corCodigo.equals("")) {
            tipo = AcessoSistema.ENTIDADE_COR;
            codEntidade = corCodigo;
        } else if (!orgCodigo.equals("")) {
            tipo = AcessoSistema.ENTIDADE_ORG;
            codEntidade = orgCodigo;
        } else if (!cspCodigo.equals("")) {
            tipo = AcessoSistema.ENTIDADE_SUP;
            codEntidade = cspCodigo;
        }

        try {
            validaTipoEntidade(tipo, codEntidade, responsavel);
        } catch (ZetraException e) {
            session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            usuario = usuarioController.findUsuario(usuCodigo, responsavel);
        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        int qtdColunas = 5;

        List<TransferObject> hist = null;
        try {
            CustomTransferObject filtro = new CustomTransferObject();
            filtro.setAttribute(Columns.OUS_USU_CODIGO, usuario.getUsuCodigo());

            int total = usuarioController.countOcorrenciaUsuario(filtro, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            hist = usuarioController.lstOcorrenciaUsuario(filtro, offset, size, responsavel);

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

            List<String> listParams = new ArrayList<>(params);
            configurarPaginador(getLinkAction() + "?acao=iniciar", "rotulo.paginacao.titulo.historico.usuario", total, size, listParams, false, request, model);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            hist = new ArrayList<>();
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("hist", hist);
        model.addAttribute("qtdColunas", qtdColunas);

        return viewRedirect("jsp/manterUsuario/detalharUsuarioPapel", request, session, model, responsavel);
    }

    /**
     * Valida o tipo de entidade, com a entidade do usuário que está consultando outro usuário
     * @param tipo
     * @param codEntidade
     * @param responsavel
     * @throws LogControllerException
     * @throws ZetraException
     */
    protected void validaTipoEntidade(String tipo, String codEntidade, AcessoSistema responsavel) throws LogControllerException, ZetraException {
        if (isTipoEntidadeInvalido(tipo, codEntidade, responsavel)) {
            // Registra log de erro
            com.zetra.econsig.delegate.LogDelegate log = new com.zetra.econsig.delegate.LogDelegate(responsavel, Log.USUARIO, Log.CREATE, Log.LOG_ERRO_SEGURANCA);
            log.add(ApplicationResourcesHelper.getMessage("mensagem.erro.consulta.usuario.nao.permitida", responsavel).toUpperCase());
            log.write();
            throw new ZetraException("mensagem.erro.interno.contate.administrador", responsavel);
        }
    }
}