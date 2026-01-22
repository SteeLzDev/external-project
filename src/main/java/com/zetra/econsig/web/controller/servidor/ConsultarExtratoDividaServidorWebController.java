package com.zetra.econsig.web.controller.servidor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.web.ExtratoDividaServidor;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ConsultarExtratoDividaServidorWebController</p>
 * <p>Description: Controlador Web para o caso de uso Consultar Extrato de Divida do Servidor.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/consultarExtratoDivida" })
public class ConsultarExtratoDividaServidorWebController extends AbstractConsultarServidorWebController {

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Override
    protected String continuarOperacao(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException {
        return consultarExtrato(rseCodigo, adeNumero, request, response, session, model);
    }

    @Override
    protected String definirProximaOperacao(HttpServletRequest request, AcessoSistema responsavel) {
        return "consultarExtrato";
    }

    @RequestMapping(params = { "acao=consultarExtrato" })
    public String consultarExtrato(@RequestParam(value = "RSE_CODIGO", required = true, defaultValue = "") String rseCodigo, @RequestParam(value = "ADE_NUMERO", required = true, defaultValue = "") String _adeNumero, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws AutorizacaoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request) && !responsavel.isSer()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo = responsavel.getCsaCodigo();
        if (responsavel.isSer()) {
            rseCodigo = responsavel.getRseCodigo();
        }

        if (TextHelper.isNull(rseCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean senhaServidorOK = (request.getAttribute("senhaServidorOK") != null);

        String serNome = null;
        String rseMatricula = null;
        List<MargemTO> lstMargens = null;

        // Pesquisa os dados do servidor
        try {
            CustomTransferObject servidor = pesquisarServidorController.buscaServidor(rseCodigo, responsavel);
            serNome = servidor.getAttribute(Columns.SER_NOME).toString();
            rseMatricula = servidor.getAttribute(Columns.RSE_MATRICULA).toString();
            String srsCodigo = servidor.getAttribute(Columns.SRS_CODIGO).toString();
            boolean serAtivo = (!srsCodigo.equals(CodedValues.STU_ATIVO) && !srsCodigo.equals(CodedValues.STU_BLOQUEADO));
            // Consulta as margens deste servidor
            lstMargens = consultarMargemController.consultarMargem(rseCodigo, null, null, csaCodigo, senhaServidorOK, (String) request.getAttribute("senhaServidorOK"), serAtivo, null, responsavel);
        } catch (ServidorControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Pesquisa os contratos do servidor
        List<TransferObject> ades = null;
        try {
            String tipoEntidade = responsavel.getTipoEntidade();
            String codigoEntidade = (responsavel.isSer()) ? responsavel.getUsuCodigo() : responsavel.getCodigoEntidade();
            ades = pesquisarConsignacaoController.pesquisaAutorizacao(tipoEntidade, codigoEntidade, rseCodigo, null, null, CodedValues.SAD_CODIGOS_ATIVOS, null, null, responsavel);
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Separa as ade's por tipo de margem
        List<TransferObject> adesNaoIncMargem = new ArrayList<>();
        List<TransferObject> adesMargem1 = new ArrayList<>();
        List<TransferObject> adesMargem2 = new ArrayList<>();
        List<TransferObject> adesMargem3 = new ArrayList<>();

        Iterator<TransferObject> it = ades.iterator();
        while (it.hasNext()) {
            TransferObject ade = it.next();
            Short incMargem = ade.getAttribute(Columns.ADE_INC_MARGEM) != null ? Short.valueOf(ade.getAttribute(Columns.ADE_INC_MARGEM).toString()) : null;

            if (incMargem == null || incMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                adesMargem1.add(ade);
            } else if (incMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2)) {
                adesMargem2.add(ade);
            } else if (incMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3)) {
                adesMargem3.add(ade);
            } else if (incMargem.equals(CodedValues.INCIDE_MARGEM_NAO)) {
                adesNaoIncMargem.add(ade);
            }
        }

        Map<Short, MargemTO> margens = new HashMap<>();
        Map<Short, List<ExtratoDividaServidor>> extratoDivida = new HashMap<>();
        Map<Short, BigDecimal> vlrTotalPorMargem = new HashMap<>();

        for (short i = 1; i <= 4; i++) {
            ades = (i == 1) ? adesMargem1 : ((i == 2) ? adesMargem2 : ((i == 3) ? adesMargem3 : adesNaoIncMargem));

            BigDecimal vlrTotal = new BigDecimal("0");
            List<ExtratoDividaServidor> lstEstrato = new ArrayList<>();

            String csaNome, csaIdentificador, servico, adeNumero, adeData, adeVlr, adeVlrFolha, adeTipoVlr, adePrazo, adePrdPagas, sadDescricao;

            for (TransferObject ade : ades) {
                csaNome = (String) ade.getAttribute(Columns.CSA_NOME_ABREV);
                csaIdentificador = (String) ade.getAttribute(Columns.CSA_IDENTIFICADOR);
                if (TextHelper.isNull(csaNome)) {
                    csaNome = ade.getAttribute(Columns.CSA_NOME).toString();
                }
                csaNome = csaIdentificador + " - " + csaNome;

                servico = (String) ade.getAttribute(Columns.CNV_COD_VERBA);
                if (TextHelper.isNull(servico)) {
                    servico = ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString();
                }
                servico += " - " + ade.getAttribute(Columns.SVC_DESCRICAO).toString();

                adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                adeData = DateHelper.toDateTimeString((Date) ade.getAttribute(Columns.ADE_DATA));
                adePrazo = ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : "Indet.";
                adePrdPagas = ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0";
                sadDescricao = ade.getAttribute(Columns.SAD_DESCRICAO).toString();

                adeTipoVlr = (String) ade.getAttribute(Columns.ADE_TIPO_VLR);

                adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? ade.getAttribute(Columns.ADE_VLR).toString() : "";
                adeVlrFolha = ade.getAttribute(Columns.ADE_VLR_FOLHA) != null ? ade.getAttribute(Columns.ADE_VLR_FOLHA).toString() : "";

                if (adeTipoVlr != null && adeTipoVlr.equals(CodedValues.TIPO_VLR_PERCENTUAL) && !adeVlrFolha.equals("")) {
                    // Se é tipo percentual, e o vlr folha é diferente de null,
                    // atribui o vlr folha ao ade_vlr
                    adeVlr = adeVlrFolha;
                    adeTipoVlr = CodedValues.TIPO_VLR_FIXO;
                }

                if (!adeVlr.equals("")) {
                    vlrTotal = vlrTotal.add(new BigDecimal(adeVlr));
                    adeVlr = NumberHelper.format(Double.valueOf(adeVlr).doubleValue(), NumberHelper.getLang());
                }

                // Formata o valor com o tipo de valor à frente
                adeVlr = ParamSvcTO.getDescricaoTpsTipoVlr(adeTipoVlr) + " " + adeVlr;

                lstEstrato.add(new ExtratoDividaServidor(csaNome, servico, adeNumero, adeData, adeVlr, adePrazo, adePrdPagas, sadDescricao));
            }

            vlrTotalPorMargem.put(i, vlrTotal);
            extratoDivida.put(i, lstEstrato);
        }

        for (short i = 1; i <= 3; i++) {
            for (MargemTO margem : lstMargens) {
                if (margem.getMarCodigo() == i) {
                    margens.put(i, margem);
                }
            }
        }

        model.addAttribute("rseMatricula", rseMatricula);
        model.addAttribute("serNome", serNome);
        model.addAttribute("lstMargens", margens);

        model.addAttribute("extratoDivida", extratoDivida);
        model.addAttribute("vlrTotalPorMargem", vlrTotalPorMargem);

        return viewRedirect("jsp/consultarExtratoDividaServidor/consultarExtratoDivida", request, session, model, responsavel);
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.extrato.divida.servidor.titulo", responsavel));
        model.addAttribute("acaoFormulario", "../v3/consultarExtratoDivida");
        model.addAttribute("imageHeader", "i-operacional");
        model.addAttribute("omitirAdeNumero", true);
    }
}
