package com.zetra.econsig.web.controller.parcela;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
import com.zetra.econsig.helper.consignacao.DuplicaParcelaHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.consignacao.AbstractConsultarConsignacaoWebController;

/**
 * <p>Title: EditarFluxoParcelasWebController</p>
 * <p>Description: Controlador Web para o caso de uso Editar FLuxo de Parcelas.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 29139 $
 * $Date: 2020-03-23 16:44:39 -0300 (seg, 23 mar 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/duplicarParcela" })
public class DuplicarParcelaWebController extends AbstractConsultarConsignacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DuplicarParcelaWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private ConvenioController convenioController;

    @Override
    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        boolean permiteCadIndice = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CAD_INDICE, CodedValues.TPC_SIM, responsavel);

        String csa_codigo = responsavel.getCsaCodigo();
        if (responsavel.isCseSupOrg()) {
            csa_codigo = JspHelper.verificaVarQryStr(request, "csa_codigo");
        }

        String adeIndice = (JspHelper.verificaVarQryStr(request, "adeIndice") != null && !JspHelper.verificaVarQryStr(request, "adeIndice").equals("")) ? JspHelper.verificaVarQryStr(request, "adeIndice").toString() : "";
        String todos = adeIndice.equals("") ? "CHECKED" : "";

        // Lista os Serviços
        List<TransferObject> servicos = null;
        List<TransferObject> consignatarias = null;
        try {
            // -------------- Seta Criterio da Listagem ------------------
            servicos = convenioController.getCsaCodVerba(csa_codigo, responsavel);
            TransferObject criterio = new CustomTransferObject();

            criterio.setAttribute(Columns.CSA_ATIVO, CodedValues.STS_ATIVO);
            consignatarias = consignatariaController.lstConsignatarias(criterio, responsavel);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            servicos = new ArrayList<>();
        }

        // Lista os arquivos de critica para download
        String path = ParamSist.getDiretorioRaizArquivos();

        if (path == null) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        path += File.separatorChar + "duplicacao" + File.separatorChar;

        String entidade = "csa";
        path += entidade;
        path += File.separatorChar + csa_codigo;

        File diretorio = new File(path);
        if (!diretorio.exists() && !diretorio.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.diretorio.inexistente", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        FileFilter filtro = new FileFilter() {
            @Override
            public boolean accept(File arq) {
                return (arq.getName().toLowerCase().endsWith(".txt") || arq.getName().toLowerCase().endsWith(".zip"));
            }
        };

        List<File> arquivos = null;
        File[] temp = diretorio.listFiles(filtro);
        if (temp != null) {
            arquivos = new ArrayList<>();
            arquivos.addAll(Arrays.asList(temp));
        }

        Collections.sort(arquivos, (f1, f2) -> {
            Long d1 = f1.lastModified();
            Long d2 = f2.lastModified();
            return d2.compareTo(d1);
        });

        model.addAttribute("permiteCadIndice", permiteCadIndice);
        model.addAttribute("csa_codigo", csa_codigo);
        model.addAttribute("servicos", servicos);
        model.addAttribute("consignatarias", consignatarias);
        model.addAttribute("todos", todos);
        model.addAttribute("adeIndice", adeIndice);
        model.addAttribute("arquivos", arquivos);
        model.addAttribute("path", path);

        return viewRedirect("jsp/duplicarParcela/duplicarParcela", request, session, model, responsavel);

    }

    @RequestMapping(params = { "acao=validarArquivo" })
    public String validarArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, IOException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        }

        String csa_codigo = responsavel.getCsaCodigo();
        if (responsavel.isCseSupOrg()) {
            csa_codigo = JspHelper.verificaVarQryStr(request, "csa_codigo");
        }

        String adeIndice = (JspHelper.verificaVarQryStr(request, "adeIndice") != null && !JspHelper.verificaVarQryStr(request, "adeIndice").equals("")) ? JspHelper.verificaVarQryStr(request, "adeIndice").toString() : "";


        try {
            BigDecimal prd_mult = (!JspHelper.verificaVarQryStr(request, "prd_mult").equals("")) ? new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "prd_mult"), NumberHelper.getLang(), "en")) : null;
            BigDecimal valor = (!JspHelper.verificaVarQryStr(request, "valor").equals("")) ? new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "valor"), NumberHelper.getLang(), "en")) : null;
            DuplicaParcelaHelper.duplicaParcela(csa_codigo, JspHelper.verificaVarQryStr(request, "rubrica"), adeIndice, prd_mult, valor, true, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.duplicar.parcela.validacao.sucesso", responsavel));

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=processarArquivo" })
    public String processarArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, IOException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        }

        String csa_codigo = responsavel.getCsaCodigo();
        if (responsavel.isCseSupOrg()) {
            csa_codigo = JspHelper.verificaVarQryStr(request, "csa_codigo");
        }
        String adeIndice = (JspHelper.verificaVarQryStr(request, "adeIndice") != null && !JspHelper.verificaVarQryStr(request, "adeIndice").equals("")) ? JspHelper.verificaVarQryStr(request, "adeIndice").toString() : "";


        try {
            BigDecimal prd_mult = (!JspHelper.verificaVarQryStr(request, "prd_mult").equals("")) ? new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "prd_mult"), NumberHelper.getLang(), "en")) : null;
            BigDecimal valor = (!JspHelper.verificaVarQryStr(request, "valor").equals("")) ? new BigDecimal(NumberHelper.reformat(JspHelper.verificaVarQryStr(request, "valor"), NumberHelper.getLang(), "en")) : null;
            DuplicaParcelaHelper.duplicaParcela(csa_codigo, JspHelper.verificaVarQryStr(request, "rubrica"), adeIndice, prd_mult, valor, false, responsavel);

            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.duplicar.parcela.concluido.sucesso", responsavel));

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }


        return iniciar(request, response, session, model);
    }
}
