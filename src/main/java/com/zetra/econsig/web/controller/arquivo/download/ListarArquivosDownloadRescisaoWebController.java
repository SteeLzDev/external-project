package com.zetra.econsig.web.controller.arquivo.download;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessoAgendado;
import com.zetra.econsig.job.process.agendado.ProcessaExportarArquivoRescisao;
import com.zetra.econsig.values.AgendamentoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ListarArquivosDownloadRescisaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso de download de arquivos de rescisão.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarArquivosDownloadRescisao" })
public class ListarArquivosDownloadRescisaoWebController extends ControlePaginacaoWebController {

    @RequestMapping(params = { "acao=iniciar" })
    public String listarArquivoDownload(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        SynchronizerToken.saveToken(request);
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.isCseSupOrg()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String absolutePath = ParamSist.getDiretorioRaizArquivos();

        final String tipo = "rescisao";
        final String entidade = "cse";

        absolutePath += File.separatorChar + tipo + File.separatorChar + entidade;

        final FileFilter filtro = arq -> {
            final String arqNome = arq.getName().toLowerCase();
            return (arqNome.endsWith(".txt") || arqNome.endsWith(".pdf") || arqNome.endsWith(".zip") || arqNome.endsWith(".csv") || arqNome.endsWith(".xls") || arqNome.endsWith(".xlsx") || arqNome.endsWith(".txt.crypt") || arqNome.endsWith(".zip.crypt"));
        };

        final File diretorio = new File(absolutePath);
        if (!diretorio.exists() && !diretorio.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.listar.arquivos.download.rescisao.criacao.diretorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<File> arquivos = null;
        final File[] temp = diretorio.listFiles(filtro);
        if (temp != null) {
            arquivos = new ArrayList<>();
            arquivos.addAll(Arrays.asList(temp));
        }

        if (arquivos != null) {
            Collections.sort(arquivos, (f1, f2) -> {
                final Long d1 = f1.lastModified();
                final Long d2 = f2.lastModified();
                return d2.compareTo(d1);
            });
        }

        // Monta a paginação
        final int size = JspHelper.LIMITE;
        int offset = 0;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (final Exception ex) {
        }

        final int total = arquivos.size();

        // Monta lista de parâmetros através dos parâmetros de request
        final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

        // Ignora os parâmetros abaixo
        params.remove("offset");
        params.remove("back");
        params.remove("linkRet");
        params.remove("linkRet64");
        params.remove("eConsig.page.token");
        params.remove("_skip_history_");
        params.remove("pager");
        params.remove("acao");

        final List<String> requestParams = new ArrayList<>(params);

        final String linkListagem = "../v3/listarArquivosDownloadRescisao?acao=iniciar";
        configurarPaginador(linkListagem, "rotulo.listar.arquivos.download.rescisao.titulo.paginacao", total, size, requestParams, false, request, model);

        // Retorna apenas os arquivos da página
        final List<ArquivoDownload> arquivosPaginaAtual = ArquivoDownload.carregarArquivos(arquivos, absolutePath, null, offset, size, responsavel);

        final boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(responsavel.getUsuCodigo(), session);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("tipo", tipo);
        model.addAttribute("arquivos", arquivosPaginaAtual);

        return viewRedirect("jsp/rescisao/listarArquivosDownloadRescisao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=executar" })
    public String executarMovimentoRescisao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException, InterruptedException {
        SynchronizerToken.saveToken(request);
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);
        final String link = TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request));
        request.setAttribute("url64", link);

        if (!responsavel.isCseSupOrg()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(responsavel.getUsuCodigo(), session);
            if (!temProcessoRodando) {
                final ProcessoAgendado processo = new ProcessaExportarArquivoRescisao(AgendamentoEnum.GERA_ARQUIVO_MOVIMENTO_RESCISAO.getCodigo(), true, responsavel);
                processo.setDescricao(ApplicationResourcesHelper.getMessage("rotulo.agendamento.movimento.rescisao", responsavel));
                processo.start();
                ControladorProcessos.getInstance().incluir(responsavel.getUsuCodigo(), processo);
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.arquivo.movimento.rescisao.nome.processando", responsavel));
                temProcessoRodando = true;
            }

        String absolutePath = ParamSist.getDiretorioRaizArquivos();

        final String tipo = "rescisao";
        final String entidade = "cse";

        absolutePath += File.separatorChar + tipo + File.separatorChar + entidade;

        final FileFilter filtro = arq -> {
            final String arqNome = arq.getName().toLowerCase();
            return (arqNome.endsWith(".txt") || arqNome.endsWith(".pdf") || arqNome.endsWith(".zip") || arqNome.endsWith(".csv") || arqNome.endsWith(".xls") || arqNome.endsWith(".xlsx") || arqNome.endsWith(".txt.crypt") || arqNome.endsWith(".zip.crypt"));
        };

        final File diretorio = new File(absolutePath);
        if (!diretorio.exists() && !diretorio.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.listar.arquivos.download.rescisao.criacao.diretorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<File> arquivos = null;
        final File[] temp = diretorio.listFiles(filtro);
        if (temp != null) {
            arquivos = new ArrayList<>();
            arquivos.addAll(Arrays.asList(temp));
        }

        if (arquivos != null) {
            Collections.sort(arquivos, (f1, f2) -> {
                final Long d1 = f1.lastModified();
                final Long d2 = f2.lastModified();
                return d2.compareTo(d1);
            });
        }

        // Monta a paginação
        final int size = JspHelper.LIMITE;
        int offset = 0;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (final Exception ex) {
        }

        final int total = arquivos.size();

        // Monta lista de parâmetros através dos parâmetros de request
        final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

        // Ignora os parâmetros abaixo
        params.remove("offset");
        params.remove("back");
        params.remove("linkRet");
        params.remove("linkRet64");
        params.remove("eConsig.page.token");
        params.remove("_skip_history_");
        params.remove("pager");
        params.remove("acao");

        final List<String> requestParams = new ArrayList<>(params);

        final String linkListagem = "../v3/listarArquivosDownloadRescisao?acao=iniciar";
        configurarPaginador(linkListagem, "rotulo.listar.arquivos.download.rescisao.titulo.paginacao", total, size, requestParams, false, request, model);

        // Retorna apenas os arquivos da página
        final List<ArquivoDownload> arquivosPaginaAtual = ArquivoDownload.carregarArquivos(arquivos, absolutePath, null, offset, size, responsavel);

        model.addAttribute("tipo", tipo);
        model.addAttribute("arquivos", arquivosPaginaAtual);
        model.addAttribute("temProcessoRodando", temProcessoRodando);

        return viewRedirect("jsp/rescisao/listarArquivosDownloadRescisao", request, session, model, responsavel);
    }
}
