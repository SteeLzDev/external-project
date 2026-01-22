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
 * <p>Title: InserirUsuarioCorrespondenteWebController</p>
 * <p>Description: Controlador Web base para o caso de uso Inserir Usuário Correspondente.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/inserirUsuarioCor" })
public class InserirUsuarioCorrespondenteWebController extends ConsultarUsuarioCorrespondenteWebController {

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        // Validação deve ser executada antes de configurar página para consulta
        boolean podeCriarUsu = responsavel.temPermissao(CodedValues.FUN_CRIAR_USUARIOS_COR);
        boolean readOnly = model != null && !TextHelper.isNull(model.asMap().get("readOnly")) && (boolean) model.asMap().get("readOnly");
        if (!readOnly) {
            model.addAttribute("readOnly", !podeCriarUsu);
        }

        super.configurarPagina(request, session, model, responsavel);

        //DESENV-10463
        if (ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_CRIACAO_SENHA_NOVO_USU_CSA_COR, CodedValues.TPC_SIM, responsavel)) {
            model.addAttribute("emailObrigatorio", true);
        }

        String titulo = getTituloPagina(request, responsavel);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.usuario.perfil.inserir.titulo", responsavel, titulo));
        model.addAttribute("exibeQtdConsultasMargem", Boolean.TRUE);
        model.addAttribute("inserirUsuario", true);
    }

    @Override
    protected String getLinkAction() {
        return "../v3/inserirUsuarioCor?acao=inserir";
    }

}
