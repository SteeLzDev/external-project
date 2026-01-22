package com.zetra.econsig.web.controller.usuario;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: EditarUsuarioOrgaoWebController</p>
 * <p>Description: Controlador Web base para o caso de uso Editar Usuário Órgão.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarUsuarioOrg" })
public class EditarUsuarioOrgaoWebController extends ConsultarUsuarioOrgaoWebController {

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        // Validação deve ser executada antes de configurar página para consulta
        boolean podeEditarUsu = podeEditarUsuario(responsavel);
        boolean readOnly = model != null && !TextHelper.isNull(model.asMap().get("readOnly")) && (boolean) model.asMap().get("readOnly");
        if (!readOnly) {
            model.addAttribute("readOnly", !podeEditarUsu);
        }

        super.configurarPagina(request, session, model, responsavel);

        String titulo = getTituloPagina(request, responsavel);
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.usuario.perfil.editar.titulo", responsavel, titulo));

    }

    @Override
    protected String getLinkAction() {
        return "../v3/editarUsuarioOrg?acao=editar";
    }
}
