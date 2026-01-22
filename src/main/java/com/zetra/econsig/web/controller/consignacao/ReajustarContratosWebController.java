package com.zetra.econsig.web.controller.consignacao;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
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
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaReajusteAde;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.convenio.ConvenioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p> * Title: ReajustarContratosWebController * </p>
 * <p> * Description: Controlador Web para o caso de uso ReajustarContratos. * </p>
 * <p> * Copyright: Copyright (c) 2002-2017 * </p>
 * <p> * Company: ZetraSoft * </p>
 * $Author: marcos.nolasco $
 * $Revision: 24630 $
 * $Date: 2019-08-20 09:17:47 * -0300 (ter, 20 ago 2019) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/reajustarContratos" })
public class ReajustarContratosWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ReajustarContratosWebController.class);

    @Autowired
    private ConvenioController convenioController;

    @Autowired
    private ConsignatariaController consignatariaController;

    private String ajustarContrato(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String chave = "PROCESSO_REAJUSTE";
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

        String csa_codigo = "";

        if (responsavel.isCsa()) {
            csa_codigo = responsavel.getCodigoEntidade();
        } else {
            csa_codigo = (request.getParameter("csa_codigo") == null ? "" : request.getParameter("csa_codigo"));
        }

        if (responsavel.isCsa() && !TextHelper.isNull(csa_codigo)) {
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        SynchronizerToken.saveToken(request);

        if (!temProcessoRodando) {
            HashMap<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
            ProcessaReajusteAde processo = new ProcessaReajusteAde(csa_codigo, parameterMap, responsavel);
            processo.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.reajuste.contratos.processo", responsavel));
            processo.start();
            ControladorProcessos.getInstance().incluir(chave, processo);
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.reajuste.contratos.processo.em.execucao", responsavel));
            temProcessoRodando = true;
        } else {
            // Se o arquivo está sendo processado por outro usuário,
            // dá mensagem de erro ao usuário e permite que ele escolha outro arquivo
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.reajuste.arquivo.em.processamento", responsavel));
            temProcessoRodando = false;
        }

        return iniciar(request, response, session, model);

    }

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String chave = "PROCESSO_REAJUSTE";
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

        String csa_codigo = "";

        if (responsavel.isCsa()) {
            csa_codigo = responsavel.getCodigoEntidade();
        } else {
            csa_codigo = (request.getParameter("csa_codigo") == null ? "" : request.getParameter("csa_codigo"));
        }

        SynchronizerToken.saveToken(request);

        List<TransferObject> servicos = null;
        if (!temProcessoRodando) {
            try {
                servicos = convenioController.getCsaCodVerbaReajuste(csa_codigo, responsavel);
            } catch (Exception ex) {
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                servicos = new ArrayList<>();
            }
        }

        HashMap<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
        parameterMap.put("acao", new String[] { "iniciar" });
        String linkRefresh = SynchronizerToken.updateTokenInURL(JspHelper.makeURL("../v3/reajustarContratos", parameterMap), request);

        List<TransferObject> consignatarias = null;
        CustomTransferObject criterio = new CustomTransferObject();

        // -------------- Seta Criterio da Listagem ------------------
        criterio.setAttribute(Columns.CSA_ATIVO, CodedValues.STS_ATIVO);

        try {
            consignatarias = consignatariaController.lstConsignatarias(criterio, responsavel);
        } catch (ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/reajustarContratos/reajustarContratos", request, session, model, responsavel);
        }

        Collections.sort(consignatarias, (o1, o2) -> {
            CustomTransferObject c1 = (CustomTransferObject) o1;
            String nome1 = (!TextHelper.isNull(c1.getAttribute(Columns.CSA_NOME_ABREV))) ? (String) c1.getAttribute(Columns.CSA_NOME_ABREV) : (String) c1.getAttribute(Columns.CSA_NOME);
            nome1 += " - " + (String) c1.getAttribute(Columns.CSA_IDENTIFICADOR);
            CustomTransferObject c2 = (CustomTransferObject) o2;
            String nome2 = (!TextHelper.isNull(c2.getAttribute(Columns.CSA_NOME_ABREV))) ? (String) c2.getAttribute(Columns.CSA_NOME_ABREV) : (String) c2.getAttribute(Columns.CSA_NOME);
            nome2 += " - " + (String) c2.getAttribute(Columns.CSA_IDENTIFICADOR);
            return nome1.compareTo(nome2);
        });

        // Lista os arquivos de critica para download
        String path = ParamSist.getDiretorioRaizArquivos();

        if (path == null) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        path += File.separatorChar + "reajuste" + File.separatorChar;

        String entidade = "csa";
        path += entidade;
        if (csa_codigo != null && !csa_codigo.equals("")) {
            path += File.separatorChar + csa_codigo;
        }

        File diretorio = new File(path);
        if (!diretorio.exists() && !diretorio.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.lst.arq.generico.criacao.diretorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        FileFilter filtro = arq -> (arq.getName().toLowerCase().endsWith(".txt") || arq.getName().toLowerCase().endsWith(".zip"));

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

        int total = arquivos.size();
        int size = JspHelper.LIMITE;

        String linkListagem = "../v3/reajustarContratos?acao=iniciar";
        configurarPaginador(linkListagem, "rotulo.listar.consignataria.titulo", total, size, null, false, request, model);

        model.addAttribute("csa_codigo", csa_codigo);
        model.addAttribute("servicos", servicos);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("linkRefresh", linkRefresh);
        model.addAttribute("consignatarias", consignatarias);
        model.addAttribute("arquivos", arquivos);
        model.addAttribute("path", path);

        return viewRedirect("jsp/reajustarContratos/reajustarContratos", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=validar" })
    public String validar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return ajustarContrato(request, response, session, model);
    }

    @RequestMapping(params = { "acao=aplicar" })
    public String aplicar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        return ajustarContrato(request, response, session, model);
    }
}
