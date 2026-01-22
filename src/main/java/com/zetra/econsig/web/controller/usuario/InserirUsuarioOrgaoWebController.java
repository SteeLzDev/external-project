package com.zetra.econsig.web.controller.usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: InserirUsuarioOrgaoWebController</p>
 * <p>Description: Controlador Web base para o caso de uso Inserir Usuário Órgão.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/inserirUsuarioOrg" })
public class InserirUsuarioOrgaoWebController extends ConsultarUsuarioOrgaoWebController {

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        // Validação deve ser executada antes de configurar página para consulta
        boolean podeCriarUsu = responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_ORG);
        boolean readOnly = model != null && !TextHelper.isNull(model.asMap().get("readOnly")) && (boolean) model.asMap().get("readOnly");
        if (!readOnly) {
            model.addAttribute("readOnly", !podeCriarUsu);
        }

        super.configurarPagina(request, session, model, responsavel);

        //DESENV-10463
        if (ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_CSE_ORG, CodedValues.TPC_SIM, responsavel)) {
            model.addAttribute("emailObrigatorio", true);
        }

        String titulo = getTituloPagina(request, responsavel);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.usuario.perfil.inserir.titulo", responsavel, titulo));

        model.addAttribute("inserirUsuario", true);
    }

    @Override
    protected String getLinkAction() {
        return "../v3/inserirUsuarioOrg?acao=inserir";
    }

}
