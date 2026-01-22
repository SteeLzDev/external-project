package com.zetra.econsig.web.controller.coeficiente;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarServicosWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Servicos Taxas.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author: moises.souza $
 * $Revision: 24435 $
 * $Date: 2018-05-28 08:58:59 -0300 (Seg, 28 mai 2018) $
 */
@Controller
@RequestMapping(value = "/v3/listarServicosCoeficiente")
public class ListarServicosCoeficienteWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarServicosCoeficienteWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private SimulacaoController simulacaoController;

    @RequestMapping(params = { "acao=editarCoeficiente" })
    public String editarCoeficiente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=consultarCoeficiente" })
    public String consultarCoeficiente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    private String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        try {

            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            String csa_codigo = "";
            if (responsavel.isCsa()) {
                csa_codigo = responsavel.getCodigoEntidade();
            } else {
                csa_codigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");
            }

            String titulo = JspHelper.verificaVarQryStr(request, "titulo");
            String tipo = JspHelper.verificaVarQryStr(request, "tipo");

            if (csa_codigo.equals("") || titulo.equals("")) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Lista os códigos dos serviços que possuem prazos para a consignatária
            List<String> svcCodigos = null;
            try {
                svcCodigos = simulacaoController.getSvcCodigosParaCadastroTaxas(csa_codigo, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            List<TransferObject> servicos = null;

            int total = 0;
            if (svcCodigos != null && svcCodigos.size() > 0) {
                try {
                    CustomTransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.SVC_CODIGO, svcCodigos);
                    criterio.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_ATIVO);

                    total = convenioController.countServicos(criterio, responsavel);
                    int size = JspHelper.LIMITE;
                    int offset = 0;
                    try {
                        offset = Integer.parseInt(request.getParameter("offset"));
                    } catch (Exception ex) {
                    }

                    servicos = convenioController.lstServicos(criterio, offset, size, responsavel);

                } catch (Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    servicos = new ArrayList<>();
                }
            } else {
                // Nenhum Serviço com prazo cadastrado
                servicos = new ArrayList<>();
            }

            Iterator<TransferObject> it = servicos.iterator();
            String svcCodigo, svcDescricao, svcIdentificador;
            String codVerba;
            String coeficienteAtivo;
            TransferObject servico = null;

            List<ListarServicosCoeficienteDTO> dtoLst = new ArrayList<>();

            while (it.hasNext()) {
                servico = it.next();
                svcCodigo = (String) servico.getAttribute(Columns.SVC_CODIGO);
                svcDescricao = (String) servico.getAttribute(Columns.SVC_DESCRICAO);
                svcIdentificador = (String) servico.getAttribute(Columns.SVC_IDENTIFICADOR);

                Set<String> codigos = new TreeSet<>();
                codVerba = null;

                List<TransferObject> codVerbas = convenioController.getCnvCodVerba(svcCodigo, csa_codigo, responsavel);
                Iterator<TransferObject> it2 = codVerbas.iterator();
                while (it2.hasNext()) {
                    codVerba = (String) it2.next().getAttribute(Columns.CNV_COD_VERBA);
                    if (codVerba != null && !codVerba.equals("")) {
                        codigos.add(codVerba);
                    }
                }

                if (codigos.size() > 0) {
                    codVerba = TextHelper.join(codigos.toArray(), ", ");
                } else {
                    codVerba = svcIdentificador;
                }

                coeficienteAtivo = simulacaoController.getTipoCoeficienteAtivo(csa_codigo, svcCodigo, responsavel);
                if (coeficienteAtivo == null) {
                    coeficienteAtivo = "";
                }

                dtoLst.add(new ListarServicosCoeficienteDTO(svcCodigo, svcDescricao, svcIdentificador, codVerba, coeficienteAtivo));
            }

            String linkRet = "../v3/listarServicosCoeficiente?acao="+ JspHelper.verificaVarQryStr(request, "acao") + "&CSA_CODIGO=" + csa_codigo + "&titulo=" + titulo;
            configurarPaginador(linkRet, "rotulo.listage.coeficientes", total, JspHelper.LIMITE, null, false, request, model);

            model.addAttribute("servicos", dtoLst);
            model.addAttribute("csa_codigo", csa_codigo);
            model.addAttribute("titulo", titulo);
            model.addAttribute("tipo", tipo);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterCoeficiente/listarServicosCoeficiente", request, session, model, responsavel);
    }
}
