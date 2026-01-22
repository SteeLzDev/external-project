package com.zetra.econsig.web.controller.servidor;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarOcorrenciaServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Ocorrencia Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarOcorrenciaServidor" })
public class ListarOcorrenciaServidorWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarOcorrenciaServidorWebController.class);

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
		try {

			String serCodigo = responsavel.isSer() ? responsavel.getSerCodigo() : JspHelper.verificaVarQryStr(request, "SER_CODIGO");
			String rseCodigo = responsavel.isSer() ? responsavel.getRseCodigo() : JspHelper.verificaVarQryStr(request, "RSE_CODIGO");

			CustomTransferObject servidor = null;

			if (TextHelper.isNull(serCodigo)) {
				// Busca os dados do servidor
				servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
				serCodigo = (String) servidor.getAttribute(Columns.SER_CODIGO);
			}

			List<TransferObject> lstOcorrencias = null;
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.OCS_SER_CODIGO, serCodigo);
            criterio.setAttribute(Columns.ORS_RSE_CODIGO, rseCodigo);


            int total = servidorController.countOcorrenciaSerUnionRse(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            lstOcorrencias = servidorController.lstOcorrenciaSerUnionRse(criterio, offset, size, responsavel);
            model.addAttribute("lstOcorrencias", lstOcorrencias);

            List<String> listParams = Arrays.asList(new String [] {"RSE_CODIGO", "SER_CODIGO"});
            String linkListagem = "../v3/listarOcorrenciaServidor?acao=iniciar";
            configurarPaginador(linkListagem , "rotulo.paginacao.titulo.consignacao", total, size, listParams, false, request, model);

            if (servidor == null) {
                servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            }

            RegistroServidorTO rseTo = servidorController.findRegistroServidor(rseCodigo, responsavel);

            String serNome = (String) servidor.getAttribute(Columns.SER_NOME);
            String rseMatricula = (String) rseTo.getAttribute(Columns.RSE_MATRICULA);

            model.addAttribute("serNome", serNome);
            model.addAttribute("rseMatricula", rseMatricula);

            return viewRedirect("jsp/editarServidor/listarOcorrenciaServidor", request, session, model, responsavel);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
