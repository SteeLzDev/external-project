package com.zetra.econsig.web.controller.taxas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.web.ListarServicosDTO;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.coeficiente.CoeficienteAtivoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.service.servico.ServicoController;
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
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
 */
@Controller
@RequestMapping(value = "/v3/listarServicos")
public class ListarServicosWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarServicosWebController.class);

    @Autowired
    private CoeficienteAtivoController coeficienteAtivoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private SimulacaoController simulacaoController;

    @RequestMapping(params = { "acao=editarTaxa" })
    public String editarCoeficiente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=consultarTaxa" })
    public String consultarCoeficiente(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=listarOcorrencia" })
    public String listarOcorrencia(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String csaCodigo = request.getParameter("CSA_CODIGO");
            final String svcCodigo = request.getParameter("SVC_CODIGO");

            if (TextHelper.isNull(csaCodigo) || TextHelper.isNull(svcCodigo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            final ConsignatariaTransferObject consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);
            final TransferObject servico = servicoController.findServico(svcCodigo);

            final int total = coeficienteAtivoController.countOcorrenciaCoeficiente(csaCodigo, svcCodigo, responsavel);

            final int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (final Exception ex) {
            }

            final List<String> requestParams = Arrays.asList("CSA_CODIGO", "SVC_CODIGO");
            configurarPaginador("../v3/listarServicos?acao=listarOcorrencia", "rotulo.paginacao.titulo.operacao.fila.autorizacao", total, size, requestParams, false, request, model);

            final List<TransferObject> coeficientes = coeficienteAtivoController.listarOcorrenciaCoeficiente(csaCodigo, svcCodigo, offset, size, responsavel);

            model.addAttribute("coeficientes", coeficientes);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("svcCodigo", svcCodigo);
            model.addAttribute("consignataria", consignataria);
            model.addAttribute("servico", servico);

        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterTaxas/listarOcorrencia", request, session, model, responsavel);
    }

    private String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Valida o token
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final Boolean editar = (responsavel.temPermissao(CodedValues.FUN_EDT_COEFICIENTES) || (responsavel.temPermissao(CodedValues.FUN_TAXA_JUROS)));

            final String csaCodigo = (responsavel.isCsa() ? responsavel.getCodigoEntidade() : JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));
            final String titulo = JspHelper.verificaVarQryStr(request, "titulo");

            if ("".equals(csaCodigo) || "".equals(titulo)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Lista os códigos dos serviços que possuem prazos para a consignatária
            List<String> svcCodigos = null;

            // Lista os códigos dos serviços que não possuem prazos habilitados ou não tem convênio para a consignatária
            List<String> svcCodigosSemPrazoConvenioCsaAll = null;
            final List<String> svcCodigosSemPrazoConvenioCsa = new ArrayList<>();
            try {
                svcCodigos = simulacaoController.getSvcCodigosParaCadastroTaxas(csaCodigo, responsavel);

                if (responsavel.isCsa()) {
                    svcCodigosSemPrazoConvenioCsaAll = simulacaoController.getSvcCodigosSemPrazoConvenioCsa(csaCodigo, responsavel);

                    for (final String svcCodigoSemPrazo : svcCodigosSemPrazoConvenioCsaAll) {
                        if (!svcCodigos.contains(svcCodigoSemPrazo)) {
                            svcCodigos.add(svcCodigoSemPrazo);
                            svcCodigosSemPrazoConvenioCsa.add(svcCodigoSemPrazo);
                        }
                    }
                }

            } catch (final Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }

            List<TransferObject> servicos = null;
            int total = 0;

            if ((svcCodigos != null) && !svcCodigos.isEmpty()) {
                try {
                    final CustomTransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.SVC_CODIGO, svcCodigos);

                    if ((svcCodigosSemPrazoConvenioCsa != null) && !svcCodigosSemPrazoConvenioCsa.isEmpty()) {
                        criterio.setAttribute(Columns.SVC_ATIVO, CodedValues.STS_ATIVO);
                    }

                    total = convenioController.countServicos(criterio, responsavel);
                    final int size = JspHelper.LIMITE;
                    int offset = 0;
                    try {
                        offset = Integer.parseInt(request.getParameter("offset"));
                    } catch (final Exception ex) {
                    }

                    servicos = convenioController.lstServicos(criterio, offset, size, true, responsavel);

                } catch (final Exception ex) {
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    servicos = new ArrayList<>();
                }
            } else {
                // Nenhum Serviço com prazo cadastrado
                servicos = new ArrayList<>();
            }

            final List<ListarServicosDTO> listarServicosDTOLst = new ArrayList<>();

            final Iterator<TransferObject> it = servicos.iterator();
            String svcCodigo, svcDescricao, svcIdentificador;
            String coeficienteAtivo;
            CustomTransferObject servico = null;
            while (it.hasNext()) {
                servico = (CustomTransferObject) it.next();
                svcCodigo = (String) servico.getAttribute(Columns.SVC_CODIGO);
                svcDescricao = (String) servico.getAttribute(Columns.SVC_DESCRICAO);
                svcIdentificador = (String) servico.getAttribute(Columns.SVC_IDENTIFICADOR);

                final Set<String> codigos = new TreeSet<>();
                String codVerba = null;

                final List<TransferObject> codVerbas = convenioController.getCnvCodVerba(svcCodigo, csaCodigo, responsavel);
                final Iterator<TransferObject> it2 = codVerbas.iterator();
                while (it2.hasNext()) {
                    codVerba = (String) ((CustomTransferObject) it2.next()).getAttribute(Columns.CNV_COD_VERBA);
                    if ((codVerba != null) && !"".equals(codVerba)) {
                        codigos.add(codVerba);
                    }
                }

                if (codigos.size() > 0) {
                    codVerba = TextHelper.join(codigos.toArray(), ", ");
                } else {
                    codVerba = svcIdentificador;
                }

                coeficienteAtivo = simulacaoController.getTipoCoeficienteAtivo(csaCodigo, svcCodigo, responsavel);
                coeficienteAtivo = (coeficienteAtivo == null) ? "" : coeficienteAtivo;
                final String icone = editar ? "editar.gif" : "pesquisar.gif";
                final String msgTaxaJuros = editar ? ApplicationResourcesHelper.getMessage("mensagem.editar.taxa.juros.clique.aqui", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.consultar.taxa.juros.clique.aqui", responsavel);

                final ListarServicosDTO listarServicosDTO = new ListarServicosDTO(svcCodigo, svcDescricao, svcIdentificador, codVerba, coeficienteAtivo, icone, msgTaxaJuros);

                if ((svcCodigosSemPrazoConvenioCsa != null) && !svcCodigosSemPrazoConvenioCsa.isEmpty() && svcCodigosSemPrazoConvenioCsa.contains(svcCodigo)) {
                    listarServicosDTO.setIsSvcSemPrazoConvenioCsa(true);
                }

                listarServicosDTOLst.add(listarServicosDTO);
            }

            if ((svcCodigosSemPrazoConvenioCsa != null) && !svcCodigosSemPrazoConvenioCsa.isEmpty()) {
                listarServicosDTOLst.sort(Comparator.comparing(ListarServicosDTO::getIsSvcSemPrazoConvenioCsa));
            }

            final String linkRet = "../v3/listarServicos?acao=" + JspHelper.verificaVarQryStr(request, "acao") + "&CSA_CODIGO=" + csaCodigo + "&titulo=" + titulo + "&" + SynchronizerToken.generateToken4URL(request);
            configurarPaginador(linkRet, "rotulo.listar.servico.taxa.juros.titulo", total, JspHelper.LIMITE, null, false, request, model);

            model.addAttribute("total", total);
            model.addAttribute("editar", editar);
            model.addAttribute("listarServicosDTOLst", listarServicosDTOLst);

        } catch (final Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        return viewRedirect("jsp/manterTaxas/listarServicos", request, session, model, responsavel);
    }
}
