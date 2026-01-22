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
import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarArquivosDownloadIntegracaoWebController</p>
 * <p>Description: Controlador Web para o casos de uso de download de arquivos de integração.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarArquivosDownloadIntegracao" })
public class ListarArquivosDownloadIntegracaoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarArquivosDownloadIntegracaoWebController.class);

    @Autowired
    private ConsignanteController consignanteController;

    @RequestMapping(params = { "acao=iniciar" })
    public String listarArquivoDownload(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        SynchronizerToken.saveToken(request);
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.isCseSupOrg()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean usuarioPodeConverterArqInt = responsavel.temPermissao(CodedValues.FUN_CONVERTER_ARQ_INTEGRACAO) && responsavel.isCseSupOrg();
        boolean usuarioPodeRemoverArqInt = responsavel.temPermissao(CodedValues.FUN_REMOVER_ARQ_INTEGRACAO) && responsavel.isCseSupOrg();

        String absolutePath = ParamSist.getDiretorioRaizArquivos();

        String tipo = "movimento";
        String entidade = "cse";

        absolutePath += File.separatorChar + tipo + File.separatorChar + entidade;

        if (!responsavel.isCseSup() && !responsavel.isOrg()) {
            absolutePath += File.separatorChar + responsavel.getCodigoEntidade();
        } else if (responsavel.isOrg() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            absolutePath += File.separatorChar + responsavel.getCodigoEntidade();
        }

        FileFilter filtro = arq -> {
            String arqNome = arq.getName().toLowerCase();
            return (arqNome.endsWith(".txt") || arqNome.endsWith(".pdf") || arqNome.endsWith(".zip") || arqNome.endsWith(".csv") || arqNome.endsWith(".xls") || arqNome.endsWith(".xlsx") || arqNome.endsWith(".txt.crypt") || arqNome.endsWith(".zip.crypt"));
        };

        File diretorio = new File(absolutePath);
        if (!diretorio.exists() && !diretorio.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.lst.arq.generico.criacao.diretorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<File> arquivos = null;
        File[] temp = diretorio.listFiles(filtro);
        if (temp != null) {
            arquivos = new ArrayList<>();
            arquivos.addAll(Arrays.asList(temp));
        }

        Map<String, TransferObject> orgaos = new HashMap<>();
        List<TransferObject> orgaosTO = null;

        if (responsavel.isCseSup()) {
            List<String> codigosOrgao = new ArrayList<>();
            String[] nome_subdir = diretorio.list();
            if (nome_subdir != null) {
                for (String element : nome_subdir) {
                    File arq = new File(absolutePath + "/" + element);
                    if (arq.isDirectory()) {
                        arquivos.addAll(Arrays.asList(arq.listFiles(filtro)));
                        codigosOrgao.add(element);
                    }
                }
            }
            if (!codigosOrgao.isEmpty()) {
                try {
                    TransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.ORG_CODIGO, codigosOrgao);

                    orgaosTO = consignanteController.lstOrgaos(criterio, responsavel);
                    Iterator<TransferObject> it = orgaosTO.iterator();
                    while (it.hasNext()) {
                        criterio = it.next();
                        orgaos.put(criterio.getAttribute(Columns.ORG_CODIGO).toString(), criterio);
                    }
                } catch (ConsignanteControllerException ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }
        } else if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            try {
                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.EST_CODIGO, responsavel.getCodigoEntidadePai());
                orgaosTO = consignanteController.lstOrgaos(criterio, responsavel);

                String[] nome_subdir = diretorio.list();
                if (nome_subdir != null) {
                    arquivos.clear();
                    for (String element : nome_subdir) {
                        File arq = new File(absolutePath + "/" + element);
                        if (arq.isDirectory()) {
                            for (TransferObject orgao : orgaosTO) {
                                if (orgao.getAttribute(Columns.ORG_CODIGO).toString().equals(element)) {
                                    arquivos.addAll(Arrays.asList(arq.listFiles(filtro)));
                                    TransferObject criterioOrgao = new CustomTransferObject();
                                    criterioOrgao.setAttribute(Columns.ORG_CODIGO, orgao.getAttribute(Columns.ORG_CODIGO));
                                    criterioOrgao.setAttribute(Columns.ORG_IDENTIFICADOR, orgao.getAttribute(Columns.ORG_IDENTIFICADOR));
                                    criterioOrgao.setAttribute(Columns.EST_IDENTIFICADOR, orgao.getAttribute(Columns.EST_IDENTIFICADOR));
                                    orgaos.put(element, criterioOrgao);
                                }
                            }
                        }
                    }
                }
            } catch (ConsignanteControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

        }

        if (arquivos != null) {
            Collections.sort(arquivos, (f1, f2) -> {
                Long d1 = f1.lastModified();
                Long d2 = f2.lastModified();
                return d2.compareTo(d1);
            });
        }

        // Verifica os arquivos em pasta de ORG e EST se a entidade existe
        List<File> lstArquivos = new ArrayList<>();
        for (File arquivo : arquivos) {
            String nome = arquivo.getPath().substring(absolutePath.length() + 1);
            TransferObject orgao = (nome.indexOf(File.separatorChar) != -1) ? orgaos.get(nome.substring(0, nome.indexOf(File.separatorChar))) : null;
            if ((orgao != null) || (nome.indexOf(File.separatorChar) == -1)) {
                lstArquivos.add(arquivo);
            }
        }
        arquivos.clear();
        arquivos.addAll(lstArquivos);

        // Monta a paginação
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
        params.remove("offset");
        params.remove("back");
        params.remove("linkRet");
        params.remove("linkRet64");
        params.remove("eConsig.page.token");
        params.remove("_skip_history_");
        params.remove("pager");
        params.remove("acao");

        List<String> requestParams = new ArrayList<>(params);

        String linkListagem = "../v3/listarArquivosDownloadIntegracao?acao=iniciar";
        configurarPaginador(linkListagem, "rotulo.paginacao.titulo.download.arq.integracao", total, size, requestParams, false, request, model);

        // Retorna apenas os arquivos da página
        List<ArquivoDownload> arquivosPaginaAtual = ArquivoDownload.carregarArquivos(lstArquivos, absolutePath, orgaos, offset, size, responsavel);

        model.addAttribute("tipo", tipo);
        model.addAttribute("arquivos", arquivosPaginaAtual);
        model.addAttribute("usuarioPodeConverterArqInt", usuarioPodeConverterArqInt);
        model.addAttribute("usuarioPodeRemoverArqInt", usuarioPodeRemoverArqInt);

        return viewRedirect("jsp/listarArquivosDownloadIntegracao/listarArquivosDownloadIntegracao", request, session, model, responsavel);
    }
}
