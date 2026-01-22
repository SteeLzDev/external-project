package com.zetra.econsig.web.controller.perfil;

import java.util.Arrays;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarOcorrenciaPerfilWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Ocorrencia Perfil.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision:  $
 * $Date: 2020-12-14 14:15:47 -0300 (seg, 14 dez 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarOcorrenciaPerfil" })
public class ListarOcorrenciaPerfilWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarOcorrenciaPerfilWebController.class);

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		try {

			String perCodigo = JspHelper.verificaVarQryStr(request, "PER_CODIGO");

			if (TextHelper.isNull(perCodigo)) {
			    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.perfil.erro.exibir.ocorrencia", responsavel));
	            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
			}

			List<TransferObject> lstOcorrencias = null;

            int total = usuarioController.countOcorrenciaPerfil(perCodigo, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            lstOcorrencias = usuarioController.lstOcorrenciaPerfil(perCodigo, offset, size, responsavel);
            model.addAttribute("lstOcorrencias", lstOcorrencias);

            List<String> listParams = Arrays.asList(new String [] {"PER_CODIGO"});
            String linkListagem = "../v3/listarOcorrenciaPerfil?acao=iniciar";
            configurarPaginador(linkListagem , "rotulo.paginacao.titulo.perfil", total, size, listParams, false, request, model);

            return viewRedirect("jsp/manterPerfil/listarOcorrenciaPerfil", request, session, model, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
