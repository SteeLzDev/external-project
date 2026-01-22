package com.zetra.econsig.web.controller.perfil;

import com.zetra.econsig.exception.CorrespondenteControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.persistence.entity.Correspondente;
import com.zetra.econsig.service.correspondente.CorrespondenteController;
import com.zetra.econsig.service.parametro.ParametroController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.helper.parametro.ParamSist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.CodedValues;

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/inserirPerfilCor" })
public class InserirPerfilCorWebController extends AbstractManterPerfilWebController {

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private CorrespondenteController correspondenteController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        boolean podeEditarPerfil = responsavel.temPermissao(CodedValues.FUN_EDT_PERFIL_COR);
        String linkAction = "../v3/manterPerfilCor?acao=inserir";
        String titulo = ApplicationResourcesHelper.getMessage("rotulo.perfil.novo.perfil.de", responsavel) + " " + JspHelper.verificaVarQryStr (request, "titulo");
        String codigo = getCodigo(request);

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
        model.addAttribute("exibeAutoDesbloqueio", exibeAutoDesbloqueio);
        model.addAttribute("podeEditarPerfil", podeEditarPerfil);
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
        return "inserir";
    }
    
}

