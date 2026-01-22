package com.zetra.econsig.web.controller.beneficiario;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarAnexoBeneficiarioWebController</p>
 * <p>Description: Listar e consultar anexo de beneficiários</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarAnexoBeneficiario" })
public class ListarAnexoBeneficiarioWebController extends ControlePaginacaoWebController {

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Override
    public void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        String titulo = JspHelper.verificaVarQryStr(request, "titulo");
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.lista.anexo.beneficiario.titulo", responsavel, titulo));
        model.addAttribute("linkAction", getLinkAction());
    }

    @RequestMapping(params = { "acao=listar" })
    public String listar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws BeneficioControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean podeEditar = responsavel.temPermissao(CodedValues.FUN_EDITAR_ANEXO_BENEFICIARIOS);
        String bfcCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.BFC_CODIGO));
        String rseCodigo = JspHelper.verificaVarQryStr(request, Columns.getColumnName(Columns.RSE_CODIGO));
        List<TransferObject> anexos = new ArrayList<>();

        CustomTransferObject criterio = new CustomTransferObject();
        criterio.setAttribute(Columns.BFC_CODIGO, bfcCodigo);
        int total = beneficiarioController.listarCountAnexosBeneficiario(criterio, responsavel);

        int size = JspHelper.LIMITE;
        int offset = 0;

        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception ex) {
        }

        anexos = beneficiarioController.listarAnexosBeneficiario(criterio, offset, size, responsavel);

        // Monta lista de parâmetros e link de paginação
        Set<String> params = new HashSet<>(request.getParameterMap().keySet());
        params.remove("offset");

        List<String> requestParams = new ArrayList<>(params);
        configurarPaginador(getLinkAction(), "rotulo.paginacao.titulo.beneficio", total, size, requestParams, false, request, model);

        model.addAttribute("podeEditar", podeEditar);
        model.addAttribute("anexos", anexos);
        model.addAttribute(Columns.BFC_CODIGO, bfcCodigo);
        model.addAttribute(Columns.RSE_CODIGO, rseCodigo);

        return viewRedirect("jsp/manterBeneficio/listarAnexoBeneficiario", request, session, model, responsavel);
    }

    private String getLinkAction() {
        return "../v3/listarAnexoBeneficiario?acao=listar";
    }

}
