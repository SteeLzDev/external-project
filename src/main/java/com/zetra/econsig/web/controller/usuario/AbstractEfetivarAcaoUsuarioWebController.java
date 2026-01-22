package com.zetra.econsig.web.controller.usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.FuncaoExigeMotivo;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: AbstractEfetivarAcaoUsuarioWebController</p>
 * <p>Description: Controlador Web base para o casos de uso de usuário que necessitam informar o motivo de operação.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class AbstractEfetivarAcaoUsuarioWebController extends AbstractListarUsuarioPapelWebController {

    @Autowired
    private UsuarioController usuarioController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);
        // inicializa parâmetros com valores padrões
        model.addAttribute("temPermissaoAnexarReativar", false);
        model.addAttribute("isDestinoRenegociacao", false);
        model.addAttribute("deferirTodos", false);
        model.addAttribute("indeferirTodos", false);
        model.addAttribute("operacaoPermiteSelecionarPeriodo", false);
        model.addAttribute("nomeCampo", "");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.usuario.perfil.titulo", responsavel, getTituloPagina(request, responsavel)));
    }

    @Override
    protected boolean isExigeMotivoOperacao (String funCodigo, AcessoSistema responsavel) {
        // Verifica se a função exige tipo de motivo da operação
        Boolean exigeTipoMotivoOperacao = FuncaoExigeMotivo.getInstance().exists(funCodigo, responsavel);

        // Busca atributos quanto a exigencia de Tipo de motivo da operacao
        if (!ParamSist.paramEquals(CodedValues.TPC_EXIGE_TIPO_MOTIVO_OPERACAO_USUARIO, CodedValues.TPC_SIM, responsavel) || !exigeTipoMotivoOperacao) {
            return false;
        }
        return true;
    }

    protected String informarMotivoOperacao(@RequestParam(value = "FUN_CODIGO", required = true, defaultValue = "") String funCodigo,
                                         @RequestParam(value = "URL_DESTINO", required = true, defaultValue = "") String urlDestino,
                                         @RequestParam(value = "USU_CODIGO", required = true, defaultValue = "") String usuCodigo,
                                         HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token
        if (!SynchronizerToken.isTokenValid(request)) {
          session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
          return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        UsuarioTransferObject usuario = null;

        try {
            usuario = usuarioController.findUsuario(usuCodigo, responsavel);
        } catch (UsuarioControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String reqColumnsStr = "TMO_CODIGO";
        String msgErro = JspHelper.verificaCamposForm(request, session, reqColumnsStr, ApplicationResourcesHelper.getMessage("mensagem.campos.obrigatorios", responsavel), "100%");

        String codigoEntidade = request.getParameter("codigo");
        String status = JspHelper.verificaVarQryStr(request, "STATUS");

        if (TextHelper.isNull(funCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Repassa o per-page-token recebido nos parâmetros
        urlDestino = SynchronizerToken.updateTokenInURL(urlDestino, request);
        model.addAttribute("urlDestino", urlDestino);
        model.addAttribute("usuario", usuario);
        model.addAttribute("usuCodigo", usuCodigo);
        model.addAttribute("msgErro", msgErro);
        model.addAttribute("funCodigo", funCodigo);
        model.addAttribute("_skip_history_", "true");
        model.addAttribute("codigoEntidade", codigoEntidade);
        model.addAttribute("USU_LOGIN", usuario.getUsuLogin());
        model.addAttribute("STATUS", status);

        return viewRedirect("jsp/manterUsuario/efetivarAcaoUsuario", request, session, model, responsavel);

    }

    protected UsuarioTransferObject getUsuario(String usuCodigo, HttpSession session, AcessoSistema responsavel) throws ViewHelperException {
        UsuarioTransferObject usuario = null;
        if (!TextHelper.isNull(usuCodigo)) {
            try {
                usuario = usuarioController.findUsuario(usuCodigo, responsavel);
            } catch (UsuarioControllerException ex) {
                throw new ViewHelperException(ex);
            }
        }
        return usuario;
    }

}
