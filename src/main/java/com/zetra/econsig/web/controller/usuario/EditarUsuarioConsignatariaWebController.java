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
 * <p>Title: EditarUsuarioConsignatariaWebController</p>
 * <p>Description: Controlador Web base para o caso de uso Editar Usuário Consignatária.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarUsuarioCsa" })
public class EditarUsuarioConsignatariaWebController extends ConsultarUsuarioConsignatariaWebController {

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
        model.addAttribute("exibeQtdConsultasMargem", Boolean.TRUE);

    }

    @Override
    protected String getLinkAction() {
        return "../v3/editarUsuarioCsa?acao=editar";
    }
}
