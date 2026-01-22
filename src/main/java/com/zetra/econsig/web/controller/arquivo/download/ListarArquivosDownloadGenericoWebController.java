package com.zetra.econsig.web.controller.arquivo.download;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarArquivosDownloadGenericoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Download de arquivos Genérico</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Date$
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarArquivosDownloadGenerico" })
public class ListarArquivosDownloadGenericoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarArquivosDownloadGenericoWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = { "acao=iniciar" })
    public String listarArquivoDownload(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        SynchronizerToken.saveToken(request);
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String absolutePath = ParamSist.getDiretorioRaizArquivos();

        final String tipo = "generico";

        String pathCse = absolutePath + File.separatorChar + tipo + File.separatorChar + "cse";
        String pathCsa = absolutePath + File.separatorChar + tipo + File.separatorChar + "csa";

        FileFilter filtro = arq -> {
            String arq_name = arq.getName().toLowerCase();
            for (String element : UploadHelper.EXTENSOES_PERMITIDAS_UPLOAD_GENERICO) {
                if (arq_name.endsWith(element)) {
                    return true;
                }
            }
            return false;
        };

        File diretorioCse = new File(pathCse);
        File diretorioCsa = new File(pathCsa);
        if ((!diretorioCse.exists() && !diretorioCse.mkdirs()) || (!diretorioCsa.exists() && !diretorioCsa.mkdirs())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.lst.arq.generico.criacao.diretorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String campoTexto = JspHelper.verificaVarQryStr(request, "campoTexto");
        int campoSelect = 0;
        try {
              campoSelect = Integer.parseInt(JspHelper.verificaVarQryStr(request, "campoSelect"));
        } catch (Exception ex1) {
        }

        //Lista os arquivos do diretório da consignante
        File[] temp = diretorioCse.listFiles(filtro);
        List<File> arquivos = new ArrayList<>();

        if (temp != null && campoSelect != 1) {
            //busca arquivo conforme o campoTexto estiver preenchido.
            if(campoSelect == 2 && campoTexto !=""){
                String arqNome = "";
                for (File element : Arrays.asList(temp)) {
                    arqNome = element.getName();
                    if (arqNome.toString().toLowerCase().contains(campoTexto.toLowerCase())){
                        arquivos.add(element);
                    }
                }
            } else {
                arquivos.addAll(Arrays.asList(temp));
            }
        }

        Map<String, TransferObject> consignatarias = new HashMap<>();

        // Se é consignante, lista os arquivos de todas as consignatárias
        if (responsavel.isCseSupOrg()) {
            List<String> codigosCsa = new ArrayList<>();
            String[] nome_subdir = diretorioCsa.list();
            //se o responsavel esta pesquisando por nome de consignatária.
            if (campoSelect == 1 && campoTexto !="" && nome_subdir !=null) {
                List<TransferObject> pesqCsa = null;
                try {
                    CustomTransferObject criterio = new CustomTransferObject();
                    String campo = Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV;
                    criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + campoTexto + CodedValues.LIKE_MULTIPLO);
                    pesqCsa = consignatariaController.lstConsignatarias(criterio, responsavel);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
                if (pesqCsa.size() > 0) {
                    for (TransferObject cdgCsa : pesqCsa) {
                        for (String element : nome_subdir) {
                            if (element.equals(cdgCsa.getAttribute(Columns.CSA_CODIGO))) {
                                File arq = new File(pathCsa + File.separatorChar + element);
                                if (arq.isDirectory()) {
                                    arquivos.addAll(Arrays.asList(arq.listFiles(filtro)));
                                    codigosCsa.add(element);
                                }
                            }
                        }
                    }
                    if (codigosCsa.size() > 0) {
                        List<TransferObject> csaTO = null;
                        try {
                            TransferObject criterio = new CustomTransferObject();
                            criterio.setAttribute(Columns.CSA_CODIGO, codigosCsa);
                            csaTO = consignatariaController.lstConsignatarias(criterio, responsavel);
                            Iterator<TransferObject> it = csaTO.iterator();
                            while (it.hasNext()) {
                                criterio = it.next();
                                consignatarias.put(criterio.getAttribute(Columns.CSA_CODIGO).toString(), criterio);
                            }
                        } catch (Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                        }
                    }
                }
            } else if (campoSelect != 1 && nome_subdir !=null) {
                for (String element : nome_subdir) {
                    File arq = new File(pathCsa + File.separatorChar + element);
                    if (arq.isDirectory()) {
                        //busca arquivo conforme o campoTexto estiver preenchido.
                        if(campoSelect == 2 && campoTexto !=""){
                            String arqNome = "";
                            for (File arquivo : Arrays.asList(arq.listFiles(filtro))) {
                                arqNome = arquivo.getName();
                                if (arqNome.toString().toLowerCase().contains(campoTexto.toLowerCase())){
                                    arquivos.add(arquivo);
                                    codigosCsa.add(element);
                                }
                            }

                        } else {
                            arquivos.addAll(Arrays.asList(arq.listFiles(filtro)));
                            codigosCsa.add(element);
                        }
                    }
                }
                if (codigosCsa.size() > 0) {
                    List<TransferObject> csaTO = null;
                    try {
                        TransferObject criterio = new CustomTransferObject();
                        criterio.setAttribute(Columns.CSA_CODIGO, codigosCsa);
                        csaTO = consignatariaController.lstConsignatarias(criterio, responsavel);
                        Iterator<TransferObject> it = csaTO.iterator();
                        while (it.hasNext()) {
                            criterio = it.next();
                            consignatarias.put(criterio.getAttribute(Columns.CSA_CODIGO).toString(), criterio);
                        }
                    } catch (Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }
            }

        } else if (responsavel.isCsaCor()) {

            // Se é consignatária ou correspondente, lista os arquivos desta consignatária
            String csa_codigo = responsavel.getCsaCodigo();
            diretorioCsa = new File(pathCsa + File.separatorChar + csa_codigo);

            if (!diretorioCsa.exists() && !diretorioCsa.mkdirs()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.lst.arq.generico.criacao.diretorio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            temp = diretorioCsa.listFiles(filtro);
            if (temp != null) {
                //busca arquivo conforme o campoTexto estiver preenchido.
                if(campoSelect == 2 && campoTexto !=""){
                    String arqNome = "";
                    for (File element : Arrays.asList(temp)) {
                        arqNome = element.getName();
                        if (arqNome.toString().toLowerCase().contains(campoTexto.toLowerCase())){
                            arquivos.add(element);
                        }
                    }
                } else {
                    arquivos.addAll(Arrays.asList(temp));
                }
            }

        } else {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        if (arquivos != null) {
            Collections.sort(arquivos, (f1, f2) -> {
                Long d1 = f1.lastModified();
                Long d2 = f2.lastModified();
                return d2.compareTo(d1);
            });
        }

        int size = JspHelper.LIMITE;
        int offset = 0;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception ex) {
        }

        int total = arquivos.size();

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
        params.remove("FILTRO");

        List<String> requestParams = new ArrayList<>(params);

        String linkListagem = "../v3/listarArquivosDownloadGenerico?acao=iniciar";
        configurarPaginador(linkListagem, "rotulo.lst.arq.generico.titulo", total, size, requestParams, false, request, model);

        model.addAttribute("arquivos", arquivos);
        model.addAttribute("pathCsa", pathCsa);
        model.addAttribute("size", size);
        model.addAttribute("offset", offset);
        model.addAttribute("consignatarias", consignatarias);
        model.addAttribute("campoTexto", campoTexto);
        model.addAttribute("campoSelect", campoSelect);

        return viewRedirect("jsp/listarArquivosDownloadGenerico/listarArquivosDownloadGenerico", request, session, model, responsavel);
    }

}
