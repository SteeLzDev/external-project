package com.zetra.econsig.web.controller.rest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.web.VisualizarHistoricoDTO;
import com.zetra.econsig.helper.consignacao.StatusAutorizacaoDescontoHelper;
import com.zetra.econsig.helper.margem.ListaHistoricoMargemViewHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoHistoricoMargemEnum;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarHistoricoMargemRestController</p>
 * <p>Description: API REST para requisição de lista de tamanho fixo simples de histórico de margens para uma margem dada.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author:
 * (Seg, 11 jun 2018) $
 */
@RestController
public class ListarHistoricoMargemRestController extends ControlePaginacaoWebController {


    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarHistoricoMargemRestController.class);

    @Autowired
    private ServidorController servidorController;

    @RequestMapping(value = "/v3/listarHistoricoMargem", method = RequestMethod.POST)
    public String listarHistoricoMargem(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String rseCodigo = request.getParameter("rseCodigo");

        int total = 0;

        // Pesquisa o histórico de margem
        List<TransferObject> historico = null;
        List<VisualizarHistoricoDTO> visualizarHistoricoLst = new ArrayList<>();
        try {
            // Define os critérios de pesquisa avançada
            CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute("marCodigo", request.getParameter("marCodigo"));
            criterio.setAttribute("adeNumero", request.getParameter("adeNumero"));

            // Obtem o total de registros
            total = servidorController.countHistoricoMargem(rseCodigo, criterio, responsavel);

            if (total > 0) {
                // Executa a pesquisa paginada do histórico
                historico = servidorController.pesquisarHistoricoMargem(rseCodigo, 0, ListaHistoricoMargemViewHelper.MAXIMO_LINHAS_NA_VIEW - 1, criterio, responsavel);
            }

            // Monta lista de parâmetros através dos parâmetros de request
            Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("senha");
            params.remove("serAutorizacao");
            params.remove("cryptedPasswordFieldName");
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("eConsig.page.token");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            if (historico != null && historico.size() > 0) {

                TransferObject registro = null;

                String adeVlr, labelTipoVlr, adeNumero, adeCodigo;
                String tocCodigo, tocDescricao, ocaObs, ocaTmo;
                String marDescricao;
                String hmrOperacao, hmrData;
                String hmrMargemAntes, hmrMargemDepois;
                String descricao = "";
                OperacaoHistoricoMargemEnum operacaoEnum;

                Iterator<TransferObject> it = historico.iterator();
                while (it.hasNext()) {
                    registro = it.next();

                    marDescricao = registro.getAttribute(Columns.MAR_DESCRICAO).toString();
                    hmrData = DateHelper.toDateTimeString((java.sql.Timestamp) registro.getAttribute(Columns.HMR_DATA));
                    hmrOperacao = registro.getAttribute(Columns.HMR_OPERACAO).toString();
                    operacaoEnum = OperacaoHistoricoMargemEnum.recuperaOperacaoHistoricoMargemEnum(hmrOperacao);
                    if (!operacaoEnum.equals(OperacaoHistoricoMargemEnum.CONSIGNACAO)) {
                        descricao = operacaoEnum.getDescricao();
                    }

                    hmrMargemAntes = registro.getAttribute(Columns.HMR_MARGEM_ANTES) != null ? NumberHelper.reformat(registro.getAttribute(Columns.HMR_MARGEM_ANTES).toString(), "en", NumberHelper.getLang()) : "0,00";
                    hmrMargemDepois = registro.getAttribute(Columns.HMR_MARGEM_DEPOIS) != null ? NumberHelper.reformat(registro.getAttribute(Columns.HMR_MARGEM_DEPOIS).toString(), "en", NumberHelper.getLang()) : "0,00";

                    adeNumero = registro.getAttribute(Columns.ADE_NUMERO) != null ? registro.getAttribute(Columns.ADE_NUMERO).toString() : "";
                    adeCodigo = registro.getAttribute(Columns.ADE_CODIGO) != null ? registro.getAttribute(Columns.ADE_CODIGO).toString() : "";
                    tocCodigo = registro.getAttribute(Columns.TOC_CODIGO) != null ? registro.getAttribute(Columns.TOC_CODIGO).toString() : "";
                    tocDescricao = registro.getAttribute(Columns.TOC_DESCRICAO) != null ? registro.getAttribute(Columns.TOC_DESCRICAO).toString() : "";
                    ocaObs = registro.getAttribute(Columns.OCA_OBS) != null ? registro.getAttribute(Columns.OCA_OBS).toString() : "";
                    ocaTmo = registro.getAttribute(Columns.TMO_DESCRICAO) != null ? registro.getAttribute(Columns.TMO_DESCRICAO).toString() : "";

                    if (!adeCodigo.equals("")) {
                        if (tocCodigo.equals(CodedValues.TOC_INFORMACAO)) {
                            if (ocaObs.startsWith(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.alteracao.status.prefixo", responsavel))) {
                                descricao = StatusAutorizacaoDescontoHelper.formataOcaObsHtml(ocaObs, ocaTmo, responsavel);
                            } else {
                                descricao = tocDescricao + ": " + ocaObs;
                            }
                        } else if (tocCodigo.equals(CodedValues.TOC_TARIF_RESERVA)) {
                            descricao = ApplicationResourcesHelper.getMessage("mensagem.historico.margem.ocorrencia.inclusao", responsavel);
                        } else {
                            descricao = tocDescricao + ": " + ocaObs;
                        }
                    }

                    labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr((String) registro.getAttribute(Columns.ADE_TIPO_VLR));
                    adeVlr = registro.getAttribute(Columns.ADE_VLR) != null ? registro.getAttribute(Columns.ADE_VLR).toString() : "";
                    if (!adeVlr.equals("")) {
                        adeVlr = NumberHelper.format(Double.valueOf(adeVlr).doubleValue(), NumberHelper.getLang());
                    } else {
                        labelTipoVlr = "";
                    }

                    visualizarHistoricoLst.add(new VisualizarHistoricoDTO(hmrData, marDescricao, descricao, adeNumero, labelTipoVlr, adeVlr, hmrMargemAntes, hmrMargemDepois, adeCodigo, hmrOperacao, rseCodigo));

                }
            }

            return ListaHistoricoMargemViewHelper.constroiView(rseCodigo, visualizarHistoricoLst, total, request, responsavel);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
            return "";
        }
    }
}
