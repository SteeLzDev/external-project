package com.zetra.econsig.web.controller.lote;

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

import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaCadastroConsignatarias;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: processarLoteCadastrarConsignatarias</p>
 * <p>Description: Controlador Web base para o caso de uso Processar lote cadastrar consignatárias.</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author: $
 * $Revision: $
 * $Date: $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/processarLoteCadastrarConsignatarias" })
public class ProcessarLoteCadastrarConsignatariasWebController extends ControlePaginacaoWebController{

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        SynchronizerToken.saveToken(request);

        final ParamSession paramSession = ParamSession.getParamSession(session);

        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final boolean podeProcessarArquivo = responsavel.temPermissao(CodedValues.FUN_EDT_CONSIGNATARIAS);
        final boolean podeExcluirArquivo = podeProcessarArquivo && responsavel.temPermissao(CodedValues.FUN_UPL_ARQUIVOS);
        boolean temProcessoRodando = false;

        final String chave1 = "CADASTROCONSIGNATARIAS" + "|" + responsavel.getUsuCodigo();
        temProcessoRodando = request.getAttribute("temProcessoRodando") != null ? (boolean) request.getAttribute("temProcessoRodando") : ControladorProcessos.getInstance().verificar(chave1, session);

        final String tipo = "cadastroConsignatarias";

        absolutePath += File.separatorChar + tipo + File.separatorChar + "cse" + File.separatorChar;

        final File diretorio = new File(absolutePath);
        if (!diretorio.exists() && !diretorio.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.lst.arq.generico.criacao.diretorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final FileFilter filtro = arq -> {
            final String arq_name = arq.getName().toLowerCase();
            return arq.isFile() && (arq_name.endsWith(".txt") || arq_name.endsWith(".zip"));
        };

        final List<File> arquivos = new ArrayList<>();
        final File[] temp = diretorio.listFiles(filtro);
        if (temp != null) {
            arquivos.addAll(Arrays.asList(temp));
        }

        if (arquivos != null) {
            Collections.sort(arquivos, (o1, o2) -> {
                final Long d1 = o1.lastModified();
                final Long d2 = o2.lastModified();
                return d2.compareTo(d1);
            });
        }

        // Paginacao
        final int size = JspHelper.LIMITE;
        int offset = 0;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (final Exception ex) {
        }

        // Monta lista de parâmetros através dos parâmetros de request
        final Set<String> params = new HashSet<>(request.getParameterMap().keySet());
        final List<String> requestParams = new ArrayList<>(params);

        final int total = arquivos.size();
        final String linkListagem = "../v3/processarLoteCadastrarConsignatarias?acao=iniciar";
        String linkRet = JspHelper.verificaVarQryStr(request, "linkRet").toString();
        if ((linkRet == null) || "".equals(linkRet)) {
            linkRet = paramSession.getLastHistory();
        }

        configurarPaginador(linkListagem, "rotulo.listar.arquivos.download.cadastrar.consignatarias.titulo.paginacao", total, size, requestParams, false, request, model);

        model.addAttribute("absolutePath", absolutePath);
        model.addAttribute("podeProcessarArquivo", podeProcessarArquivo);
        model.addAttribute("podeExcluirArquivo", podeExcluirArquivo);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("tipo", tipo);
        model.addAttribute("arquivos", arquivos);
        model.addAttribute("size", size);
        model.addAttribute("offset", offset);
        model.addAttribute("linkRet", linkRet);

        return viewRedirect("jsp/processarLote/listarArquivoCadastrarConsignatarias", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=processar" })
    public String processar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final ParamSession paramSession = ParamSession.getParamSession(session);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        boolean temProcessoRodando = false;

        // Verifica se existe algum processo rodando para o usuário
        final String chave1 = "CADASTROCONSIGNATARIAS" + "|" + responsavel.getUsuCodigo();
        temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

        final boolean validar = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "validar"));
        final String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");

        if (!temProcessoRodando) {
            final ProcessaCadastroConsignatarias processaCadastrarConsignatarias = new ProcessaCadastroConsignatarias(nomeArquivoEntrada, validar, responsavel);
            processaCadastrarConsignatarias.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.cadastrar.consignatarias.arquivo", responsavel, nomeArquivoEntrada));
            processaCadastrarConsignatarias.start();
            ControladorProcessos.getInstance().incluir(chave1, processaCadastrarConsignatarias);
            final String mensagemInfo = ApplicationResourcesHelper.getMessage(validar ? "mensagem.cadastrar.consignatarias.arquivo.sendo.validado" : "mensagem.cadastrar.consignatarias.arquivo.sendo.processado", responsavel, nomeArquivoEntrada);
            session.setAttribute(CodedValues.MSG_ALERT, mensagemInfo);
            temProcessoRodando = true;
            request.setAttribute("temProcessoRodando", temProcessoRodando);
        } else {
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.cadastrar.consignatarias.arquivo.sendo.processado", responsavel, nomeArquivoEntrada));
            temProcessoRodando = false;
        }

        paramSession.halfBack();
        return iniciar(request, response, session, model);
    }
}
