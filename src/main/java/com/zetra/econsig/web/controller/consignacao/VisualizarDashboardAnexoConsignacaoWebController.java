package com.zetra.econsig.web.controller.consignacao;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
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
import org.springframework.web.bind.annotation.RequestParam;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.MimeDetector;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.v3.JspHelper;
import com.zetra.econsig.service.consignacao.DashboardAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

import eu.medsea.mimeutil.MimeType;

/**
 * <p>Title: VisualizarDashboardAnexoConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Dashboard Anexos Consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/visualizarDashboardAnexo" })
public class VisualizarDashboardAnexoConsignacaoWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(VisualizarDashboardAnexoConsignacaoWebController.class);

    @Autowired
    private DashboardAnexoConsignacaoController dashboardAnexoConsignacaoController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) throws ViewHelperException {
        carregarListaEstabelecimento(request, session, model, responsavel);
        carregarListaOrgao(request, session, model, responsavel);
        carregarListaConsignataria(request, session, model, responsavel);
        carregarListaCorrespondente(request, session, model, responsavel);
        carregarListaServico(request, session, model, responsavel);
    }

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Lista as consignatárias com pendência de informação de saldo em consignações
        List<TransferObject> lstConsignatarias = null;
        try {
            CustomTransferObject criterio = null;

            int sizeConsignatarias = JspHelper.LIMITE;
            int totalConsignatarias = dashboardAnexoConsignacaoController.countCsasPendenciaAnexo(criterio, responsavel);
            int offsetConsignatarias = 0;
            try {
                offsetConsignatarias = Integer.parseInt(request.getParameter("offsetConsignatarias"));
            } catch (Exception ex) {
            }

            lstConsignatarias = dashboardAnexoConsignacaoController.listCsasPendenciaAnexo(criterio, offsetConsignatarias, sizeConsignatarias, responsavel);

            Set<String> params = new HashSet<>(request.getParameterMap().keySet());
            params.remove("offsetConsignatarias");
            List<String> requestParams = new ArrayList<>(params);

            configurarPaginador("Consignatarias", "../v3/visualizarDashboardAnexo", "mensagem.dashboard.anexos.consignacao.card.consignatarias.rodape", totalConsignatarias, sizeConsignatarias, requestParams, false, request, model);

        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        }

        // Consignações com ou sem pendência de anexo
        boolean temAnexoPendente = "true".equals(request.getParameter("temAnexoPendente"));

        List<TransferObject> adesAnexo = null;
        try {
            CustomTransferObject criterio = new CustomTransferObject();

            String periodoIni = JspHelper.verificaVarQryStr(request, "periodoIni");
            if (!periodoIni.equals("")) {
                criterio.setAttribute("periodoIni", DateHelper.parse(periodoIni, LocaleHelper.getDatePattern()));
            }
            String periodoFim = JspHelper.verificaVarQryStr(request, "periodoFim");
            if (!periodoFim.equals("")) {
                criterio.setAttribute("periodoFim", DateHelper.parse(periodoFim, LocaleHelper.getDatePattern()));
            }

            criterio.setAttribute(Columns.EST_CODIGO, JspHelper.verificaVarQryStr(request, "EST_CODIGO"));
            criterio.setAttribute(Columns.ORG_CODIGO, JspHelper.verificaVarQryStr(request, "ORG_CODIGO"));
            criterio.setAttribute(Columns.CSA_CODIGO, JspHelper.verificaVarQryStr(request, "CSA_CODIGO"));
            criterio.setAttribute(Columns.COR_CODIGO, JspHelper.verificaVarQryStr(request, "COR_CODIGO"));
            criterio.setAttribute(Columns.SVC_CODIGO, JspHelper.verificaVarQryStr(request, "SVC_CODIGO"));
            criterio.setAttribute(Columns.SER_CPF, JspHelper.verificaVarQryStr(request, "SER_CPF"));
            criterio.setAttribute(Columns.RSE_MATRICULA, JspHelper.verificaVarQryStr(request, "RSE_MATRICULA"));

            String adeNumero = JspHelper.verificaVarQryStr(request, "ADE_NUMERO");
            String[] adeNumeros = request.getParameterValues("ADE_NUMERO_LIST");
            if (adeNumeros != null) {
                for (String adeNum : adeNumeros) {
                    if (!adeNum.matches("^[0-9]+$")) {
                        throw new AutorizacaoControllerException("mensagem.erro.ade.numero.invalido.arg0", responsavel, adeNum);
                    }
                }
            }
            List<Long> adeNumeroList = new ArrayList<>();
            if (TextHelper.isNum(adeNumero)) {
                adeNumeroList.add(Long.valueOf(adeNumero));
            }
            if (adeNumeros != null) {
                for (String numero : adeNumeros) {
                    if (TextHelper.isNum(numero)) {
                        adeNumeroList.add(Long.valueOf(numero));
                    }
                }
            }
            criterio.setAttribute(Columns.ADE_NUMERO, adeNumeroList);

            int sizeAnexo = JspHelper.LIMITE;

            int totalAnexo = 0;
            if (temAnexoPendente) {
                totalAnexo = dashboardAnexoConsignacaoController.countConsignacaoSemAnexo(criterio, responsavel);
            } else {
                totalAnexo = dashboardAnexoConsignacaoController.countConsignacaoComAnexo(criterio, responsavel);
            }

            int offsetAnexo = 0;
            try {
                offsetAnexo = Integer.parseInt(request.getParameter("offsetAnexo"));
            } catch (Exception ex) {
            }

            if (temAnexoPendente) {
                adesAnexo = dashboardAnexoConsignacaoController.listConsignacaoSemAnexo(criterio, offsetAnexo, sizeAnexo, responsavel);
            } else {
                adesAnexo = dashboardAnexoConsignacaoController.listConsignacaoComAnexo(criterio, offsetAnexo, sizeAnexo, responsavel);
            }

            Set<String> params = new HashSet<>(request.getParameterMap().keySet());
            params.remove("offsetAnexo");
            List<String> requestParams = new ArrayList<>(params);

            configurarPaginador("Anexo", "../v3/visualizarDashboardAnexo", "mensagem.dashboard.anexos.consignacao.card.ultimos.anexos.rodape", totalAnexo, sizeAnexo, requestParams, false, request, model);

        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        } catch (ParseException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            LOG.error(ex.getMessage(), ex);
        }

        model.addAttribute("lstConsignatarias", lstConsignatarias);
        model.addAttribute("adesAnexo", adesAnexo);

        return viewRedirect("jsp/visualizarDashboardAnexo/visualizarDashboardAnexo", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=downloadAnexos" })
    public void download(@RequestParam(value = "ADE_CODIGO", required = true, defaultValue = "") String adeCodigo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            TransferObject ade = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
            String adeDataFormatada = DateHelper.format((Date) ade.getAttribute(Columns.ADE_DATA), "yyyyMMdd");
            String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();

            CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
            cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);
            List<TransferObject> lstAnexos = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(cto, -1, -1, responsavel);

            if (lstAnexos != null && !lstAnexos.isEmpty()) {
                List<String> fileNames = new ArrayList<>();
                for (TransferObject anexo: lstAnexos) {
                    String fileName = diretorioRaizArquivos
                            + File.separatorChar + "anexo"
                            + File.separatorChar + adeDataFormatada
                            + File.separatorChar + adeCodigo
                            + File.separatorChar + anexo.getAttribute(Columns.AAD_NOME);
                    File file = new File(fileName);
                    if (file.exists() && file.canRead()) {
                        fileNames.add(fileName);
                    } else {
                        fileName = diretorioRaizArquivos
                                 + File.separatorChar + "anexo"
                                 + File.separatorChar + adeCodigo
                                 + File.separatorChar + anexo.getAttribute(Columns.AAD_NOME);
                        file = new File(fileName);
                        if (file.exists() && file.canRead()) {
                            fileNames.add(fileName);
                        }
                    }
                }

                String zipFileName = diretorioRaizArquivos
                                   + File.separatorChar + "temp"
                                   + File.separatorChar + adeNumero + ".zip";
                FileHelper.zip(fileNames, zipFileName);
                File zipFile = new File(zipFileName);

                if (zipFile.exists() && zipFile.canRead()) {
                    // Gera log de download de arquivo
                    try {
                        LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DOWNLOAD_FILE, Log.LOG_INFORMACAO);
                        log.add(ApplicationResourcesHelper.getMessage("rotulo.download.arquivo.log", responsavel) + ": " + zipFile.getAbsolutePath());
                        log.write();
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
                    }

                    long tamanhoArquivoBytes = zipFile.length();

                    Set<MimeType> mimeSet = MimeDetector.MIMEUTIL.detect(zipFile.getAbsolutePath());
                    String mime = mimeSet != null && mimeSet.size() > 0 ? mimeSet.toArray()[0].toString() : "APPLICATION/OCTET-STREAM";
                    response.setContentType(mime);
                    response.setHeader("Content-Disposition", "attachment; filename=\"" + zipFile.getName() + "\"");

                    if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
                        response.addHeader("Content-Length", Long.toString(tamanhoArquivoBytes));
                    } else {
                        response.setContentLength((int) tamanhoArquivoBytes);
                    }

                    BufferedInputStream entrada = new BufferedInputStream(new FileInputStream(zipFile));
                    if (tamanhoArquivoBytes > Integer.MAX_VALUE) {
                        org.apache.commons.io.IOUtils.copyLarge(entrada, response.getOutputStream());
                    } else {
                        org.apache.commons.io.IOUtils.copy(entrada, response.getOutputStream());
                    }
                    response.flushBuffer();
                    entrada.close();

                    // Remove o arquivo gerado para o envio ao usuário
                    zipFile.delete();
                }
            }
        } catch (AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            LOG.error(ex.getMessage(), ex);
        } catch (IOException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erroInternoSistema", responsavel));
            LOG.error(ex.getMessage(), ex);
        }
    }
}
