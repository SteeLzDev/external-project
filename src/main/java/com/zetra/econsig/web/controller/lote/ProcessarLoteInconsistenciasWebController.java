package com.zetra.econsig.web.controller.lote;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaInconsistencia;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ProcessarLoteInconsistenciasWebController</p>
 * <p>Description: Controlador Web base para o caso de uso Processar lote de inconsistências.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 26408 $
 * $Date: 2020-05-26 10:52:11 -0300 (ter, 26 may 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/processarLoteInconsistencia" })
public class ProcessarLoteInconsistenciasWebController extends ControlePaginacaoWebController{

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessarLoteInconsistenciasWebController.class);

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        ParamSession paramSession = ParamSession.getParamSession(session);

        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        boolean podeProcessarArquivo = responsavel.temPermissao(CodedValues.FUN_PROCESSA_LOTE_INCONSISTENCIA);
        boolean podeExcluirArquivo = podeProcessarArquivo && responsavel.temPermissao(CodedValues.FUN_UPL_ARQUIVOS);
        boolean temProcessoRodando = false;

        // Verifica se existe algum processo rodando para o usuário
        String chave1 = "INCONSISTENCIA" + "|" + responsavel.getUsuCodigo();
        temProcessoRodando = request.getAttribute("temProcessoRodando") != null ? (boolean) request.getAttribute("temProcessoRodando") : ControladorProcessos.getInstance().verificar(chave1, session);

        final String tipo = "inconsistencia";

        absolutePath += File.separatorChar + tipo + File.separatorChar + "cse";

        File diretorio = new File(absolutePath);
        if (!diretorio.exists() && !diretorio.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.lst.arq.generico.criacao.diretorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        FileFilter filtro = arq -> {
            String arq_name = arq.getName().toLowerCase();
            return arq.isFile() && (arq_name.endsWith(".txt") || arq_name.endsWith(".zip"));
        };

        List<File> arquivos = new ArrayList<>();
        File[] temp = diretorio.listFiles(filtro);
        if (temp != null) {
            arquivos.addAll(Arrays.asList(temp));
        }

        if (arquivos != null) {
            Collections.sort(arquivos, (o1, o2) -> {
                Long d1 = o1.lastModified();
                Long d2 = o2.lastModified();
                return d2.compareTo(d1);
            });
        }

        // Paginacao
        int size = JspHelper.LIMITE;
        int offset = 0;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception ex) {
        }

        // Monta lista de parâmetros através dos parâmetros de request
        Set<String> params = new HashSet<>(request.getParameterMap().keySet());
        List<String> requestParams = new ArrayList<>(params);

        int total = arquivos.size();
        String linkListagem = "../v3/processarLoteInconsistencia?acao=iniciar";
        String linkRet = JspHelper.verificaVarQryStr(request, "linkRet").toString();
        if (linkRet == null || linkRet.equals("")) {
            linkRet = paramSession.getLastHistory();
        }

        configurarPaginador(linkListagem, "rotulo.listar.arquivos.download.rescisao.titulo.paginacao", total, size, requestParams, false, request, model);

        model.addAttribute("absolutePath", absolutePath);
        model.addAttribute("podeProcessarArquivo", podeProcessarArquivo);
        model.addAttribute("podeExcluirArquivo", podeExcluirArquivo);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("tipo", tipo);
        model.addAttribute("arquivos", arquivos);
        model.addAttribute("size", size);
        model.addAttribute("offset", offset);
        model.addAttribute("linkRet", linkRet);

        return viewRedirect("jsp/processarLote/listarArquivoInconsistencias", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=processar" })
    public String processar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean temProcessoRodando = false;

        // Verifica se existe algum processo rodando para o usuário
        String chave1 = "INCONSISTENCIA" + "|" + responsavel.getUsuCodigo();
        temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

        if (!temProcessoRodando && !JspHelper.verificaVarQryStr(request, "VALIDAR").equals("")) {
            // Se não tem processo rodando para o usuário, e o usuário
            // mandou processar um arquivo, então ...
            String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");

            // Verifica se algum outro usuário da consignatária está processando
            // o arquivo escolhido pelo usuário.
            String chave2 = "INCONSISTENCIA" + "|" + nomeArquivoEntrada;
            temProcessoRodando = ControladorProcessos.getInstance().verificar(chave2, session);

            if (!temProcessoRodando) {
                // Se o arquivo escolhido não está sendo processado
                // então inicia o processamento.
                ProcessaInconsistencia processaInconsistencia = new ProcessaInconsistencia(nomeArquivoEntrada, responsavel);
                processaInconsistencia.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.inconsistencia.arquivo", responsavel, nomeArquivoEntrada));
                processaInconsistencia.start();
                ControladorProcessos.getInstance().incluir(chave1, processaInconsistencia);
                ControladorProcessos.getInstance().incluir(chave2, processaInconsistencia);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.inconsistencia.arquivo.sendo.processado", responsavel, nomeArquivoEntrada));
                temProcessoRodando = true;
                request.setAttribute("temProcessoRodando", temProcessoRodando);
            } else {
                // Se o arquivo está sendo processando por outro usuário,
                // dá mensagem de erro ao usuário e permite que ele escolha
                // outro arquivo
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.inconsistencia.arquivo.sendo.processado", responsavel, nomeArquivoEntrada));
                temProcessoRodando = false;
            }
        }

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException, AutorizacaoControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String nomeArquivo = TextHelper.isNull(request.getParameter("arquivo_nome")) ? ApplicationResourcesHelper.getMessage("rotulo.include.get.file.desconhecido", responsavel) : request.getParameter("arquivo_nome");
        String msg = request.getAttribute("msg") != null ? request.getAttribute("msg").toString() : ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.nao.encontrado", responsavel, nomeArquivo);

        if (!TextHelper.isNull(nomeArquivo)) {
            String tipo = request.getParameter("tipo");

            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            if (absolutePath != null) {
                absolutePath = new File(absolutePath).getCanonicalPath();

                String name = java.net.URLDecoder.decode(nomeArquivo, "UTF-8");
                if (name.indexOf("..") != -1) {
                    session.setAttribute(CodedValues.MSG_ERRO, msg);

                } else {
                    String entidade = "cse";
                    String fileName = absolutePath + File.separatorChar + tipo;
                    fileName += File.separatorChar + entidade;
                    fileName += File.separatorChar + name;
                    File arquivo = new File(fileName);
                    if (!arquivo.exists() || !arquivo.getCanonicalPath().startsWith(absolutePath)) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.nao.encontrado", responsavel, name));
                    } else {
                        request.setAttribute("file", arquivo);
                    }
                }

            } else {
                msg += ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.configuracao.diretorio", responsavel);
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            }
        } else {
            session.setAttribute(CodedValues.MSG_ERRO, msg);
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        Object arquivos = request.getAttribute("file");
        if (arquivos == null) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);

        } else {
            // Gera log de remoção de arquivo
            try {
                LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DELETE_FILE, Log.LOG_INFORMACAO);
                if (arquivos instanceof File) {
                    log.add(ApplicationResourcesHelper.getMessage("rotulo.delete.arquivo.log", responsavel) + ": " + ((File) arquivos).getAbsolutePath());
                } else if (arquivos instanceof List) {
                    for (Object element : ((List<?>) arquivos)) {
                        File fileInList = (File) element;
                        log.add(ApplicationResourcesHelper.getMessage("rotulo.delete.arquivo.log", responsavel) + ": " + fileInList.getAbsolutePath());
                    }
                }
                log.write();
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.compact.erro.interno", responsavel) + ": " + ex.getMessage());
            }
            if (arquivos instanceof File) {
                String ext = JspHelper.verificaVarQryStr(request, "ext");
                if (ext == null || ext.equals("")) {
                    ((File) arquivos).delete();
                } else {
                    FileHelper.rename(((File) arquivos).getAbsolutePath(), ((File) arquivos).getAbsolutePath() + "." + ext);
                }
            } else if (arquivos instanceof List) {
                for (Object element : ((List<?>) arquivos)) {
                    File fileInList = (File) element;
                    fileInList.delete();
                }
            }
        }

        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.removido.sucesso", responsavel));

        return iniciar(request, response, session, model);
    }
}
