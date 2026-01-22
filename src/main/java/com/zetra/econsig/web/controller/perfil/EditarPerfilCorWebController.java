package com.zetra.econsig.web.controller.perfil;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.CorrespondenteControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.Correspondente;
import com.zetra.econsig.persistence.entity.Perfil;
import com.zetra.econsig.service.correspondente.CorrespondenteController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: EditarPerfilCorWebController</p>
 * <p>Description: Web Controller para edição de perfil de COR</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarPerfilCor" })
public class EditarPerfilCorWebController extends AbstractManterPerfilWebController {

    @Autowired
    private UsuarioController usuarioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private CorrespondenteController correspondenteController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        boolean podeEditarPerfil = responsavel.temPermissao(CodedValues.FUN_EDT_PERFIL_COR);
        String codigo = getCodigo(request);
        List<String> perFunCodigos = null;
        String perCodigo = "";
        String perDescricao = "";
        String perVisivel = "";
        String linkAction = "../v3/manterPerfilCor?acao=editar";
        Date perDataExpiracao = null;
        String perAutoDesbloqueio = CodedValues.TPA_NAO;

        try {
            perCodigo = JspHelper.verificaVarQryStr(request, "PER_CODIGO");
            Perfil perfil = usuarioController.findPerfil(perCodigo, responsavel);
            perDescricao = perfil.getPerDescricao();
            perVisivel = (perfil.getPerVisivel() != null ? perfil.getPerVisivel() : "");
            perFunCodigos = usuarioController.getFuncaoPerfil(AcessoSistema.ENTIDADE_COR, codigo, perCodigo, responsavel);
            perDataExpiracao = perfil.getPerDataExpiracao();
            perAutoDesbloqueio = perfil.getPerAutoDesbloqueio();
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        String titulo = ApplicationResourcesHelper.getMessage("rotulo.perfil.edicao.perfil.de", responsavel) + " " + JspHelper.verificaVarQryStr(request, "titulo");

        // Exibe Botao que leva ao rodapé
        boolean exibeBotaoRodape = ParamSist.paramEquals(CodedValues.TPC_EXIBE_BOTAO_RESPONSAVEL_PELO_RODAPE_DA_PAGINA, CodedValues.TPC_SIM, responsavel);

        String exibeAutoDesbloqueio = null;
        try {
            Correspondente cor = correspondenteController.findCorrespondenteByPrimaryKey(codigo, responsavel);
            exibeAutoDesbloqueio = parametroController.getParamCsa(cor.getCsaCodigo(), CodedValues.TPA_AUTO_DESBLOQUEIO_USUARIO_CSA_COR, responsavel);
        } catch (ParametroControllerException | CorrespondenteControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        model.addAttribute("exibeBotaoRodape", exibeBotaoRodape);
        model.addAttribute("podeEditarPerfil", podeEditarPerfil);
        model.addAttribute("exibeAutoDesbloqueio", exibeAutoDesbloqueio);
        model.addAttribute("perAutoDesbloqueio", perAutoDesbloqueio);
        model.addAttribute("perFunCodigos", perFunCodigos);
        model.addAttribute("perDescricao", perDescricao);
        model.addAttribute("perVisivel", perVisivel);
        model.addAttribute("perDataExpiracao", perDataExpiracao);
        model.addAttribute("titulo", titulo);
        model.addAttribute("linkAction", linkAction);
    }

    @Override
    protected String getColunaAtivo(HttpServletRequest request) {
        return "";
    }

    @Override
    protected String getTipo(HttpServletRequest request) {
        return AcessoSistema.ENTIDADE_COR;
    }

    @Override
    protected String getOperacao(HttpServletRequest request) {
        return "editar";
    }

}
