package com.zetra.econsig.web.controller.usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ExcluirUsuarioCorrespondenteWebController</p>
 * <p>Description: Controlador Web base para o caso de uso Excluir Usuário Correspondente.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/excluirUsuarioCor" })
public class ExcluirUsuarioCorrespondenteWebController extends ConsultarUsuarioCorrespondenteWebController {

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        super.configurarPagina(request, session, model, responsavel);

        String usuCodigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");
        UsuarioTransferObject usuario = getUsuario(usuCodigo, session, responsavel);
        String usuLogin = !TextHelper.isNull(usuario.getUsuLogin()) ? usuario.getUsuLogin() : "";

        String tituloPagina = ApplicationResourcesHelper.getMessage("rotulo.efetiva.acao.usuario.excluir", responsavel);
        String msgConfirmacao = ApplicationResourcesHelper.getMessage("mensagem.confirmacao.exclusao.usuario", responsavel, usuLogin);

        model.addAttribute("tituloPagina", tituloPagina);
        model.addAttribute("msgConfirmacao", msgConfirmacao);
    }

    @RequestMapping(params = { "acao=efetivarAcao" })
    public String efetivarAcao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String urlDestino = getLinkExcluirUsuario() + "?acao=excluir";
        String usuCodigo = JspHelper.verificaVarQryStr(request, "USU_CODIGO");
        String funCodigo = CodedValues.FUN_EXCL_USUARIO_COR;

        // Valida o tipo de entidade, com a entidade do usuário que está criando novo usuário
        try {
        	validaTipoEntidade(request, responsavel);
        } catch (ZetraException e) {
        	// Redireciona para página de erro
        	session.setAttribute(CodedValues.MSG_ERRO, e.getMessage());
        	return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (!super.isExigeMotivoOperacao(funCodigo, responsavel)) {
            // Realiza um forward para passar pelo filtro de segurança e exigir segunda senha, caso habilitado
            return "forward:" + forwardUrl(urlDestino) + "&_skip_history_=true";
        } else {
            return super.informarMotivoOperacao(funCodigo, urlDestino, usuCodigo, request, response, session, model);
        }
    }
}
