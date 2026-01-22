package com.zetra.econsig.web.controller.lote;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaRescisao;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;
import com.zetra.econsig.web.controller.arquivo.DeleteWebController;

@Controller
@RequestMapping(method = {RequestMethod.POST}, value = {"/v3/processarLoteRescisao"})
public class ProcessarLoteRescisaoWebController extends ControlePaginacaoWebController {

    @RequestMapping(params = {"acao=validar"})
    public String validar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        boolean temProcessoRodando = false;
        boolean validar = true;

        String chave = "impRescisao";
        temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);
        String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");

        if (!temProcessoRodando) {
            // Se o arquivo escolhido não está sendo processado
            // então inicia o processamento.
            ProcessaRescisao processaRescisao = new ProcessaRescisao(nomeArquivoEntrada, validar, responsavel);
            processaRescisao.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.rescisao.arquivo.validar", responsavel, nomeArquivoEntrada));
            processaRescisao.start();
            ControladorProcessos.getInstance().incluir(chave, processaRescisao);
            temProcessoRodando = true;
            request.setAttribute("temProcessoRodando", temProcessoRodando);
        } else {
            // Se o arquivo está sendo processando por outro usuário,
            // dá mensagem de erro ao usuário e permite que ele escolha
            // outro arquivo
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.rescisao.arquivo.sendo.processado", responsavel, nomeArquivoEntrada));
        }

        paramSession.halfBack();

        return listarArquivos(request, response, session, model);
    }

    @RequestMapping(params = {"acao=processar"})
    public String processar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        boolean temProcessoRodando = false;
        boolean validar = false;

        String chave = "impRescisao";
        temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);
        String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");

        if (!temProcessoRodando) {
            // Se o arquivo escolhido não está sendo processado
            // então inicia o processamento.
            ProcessaRescisao processaRescisao = new ProcessaRescisao(nomeArquivoEntrada, validar, responsavel);
            processaRescisao.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.rescisao.arquivo.validar", responsavel, nomeArquivoEntrada));
            processaRescisao.start();
            ControladorProcessos.getInstance().incluir(chave, processaRescisao);
            temProcessoRodando = true;
            request.setAttribute("temProcessoRodando", temProcessoRodando);
        } else {
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.rescisao.arquivo.sendo.processado", responsavel, nomeArquivoEntrada));
        }

        paramSession.halfBack();

        return listarArquivos(request, response, session, model);
    }

    @RequestMapping(params = {"acao=listarArquivos"})
    public String listarArquivos(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!responsavel.temPermissao(CodedValues.FUN_PROCESSA_LOTE_RECISAO)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String tipo = "rescisao";
        final String subTipo = "rescisaoLote";
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String tipoEntidade = AcessoSistema.ENTIDADE_CSE;
        String tipoCodigo = null;
        boolean temProcessoRodando = false;

        String chave1 = "impRescisao";
        temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            tipoCodigo = responsavel.getCodigoEntidadePai();
            tipoEntidade = AcessoSistema.ENTIDADE_EST;
        } else if (responsavel.isOrg() && !responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            tipoCodigo = responsavel.getCodigoEntidade();
        }

        if (responsavel.isCseSup()) {
            absolutePath += File.separatorChar + tipo + File.separatorChar + tipoEntidade.toLowerCase() + File.separatorChar;
        } else {
            absolutePath += File.separatorChar + tipo + File.separatorChar + tipoEntidade.toLowerCase() + File.separatorChar + tipoCodigo + File.separatorChar;
        }

        File diretorio = new File(absolutePath);
        if (!diretorio.exists() && !diretorio.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.diretorio.inexistente", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        FileFilter filtro = arq -> {
            String arq_name = arq.getName().toLowerCase();
            return arq.isFile() && (arq_name.endsWith(".txt") || arq_name.endsWith(".zip"));
        };

        List<File> arquivos = null;
        File[] temp = diretorio.listFiles(filtro);
        if (temp != null) {
            arquivos = new ArrayList<>();
            arquivos.addAll(Arrays.asList(temp));
        }

        if (arquivos != null) {
            Collections.sort(arquivos, (o1, o2) -> {
                Long d1 = o1.lastModified();
                Long d2 = o2.lastModified();
                return d2.compareTo(d1);
            });
        }

        int size = JspHelper.LIMITE;
        int offset = 0;
        try {
            offset = Integer.parseInt(request.getParameter("offset"));
        } catch (Exception ex) {
        }

        int total = arquivos != null ? arquivos.size() : 0;

        String linkListagem = "../v3/processarLoteRescisao?acao=listarArquivos";
        configurarPaginador(linkListagem, "rotulo.processar.lote.titulo", total, size, null, false, request, model);

        List<ArquivoDownload> arquivosPaginaAtual = ArquivoDownload.carregarArquivos(arquivos, absolutePath, null, offset, size, responsavel);

        model.addAttribute("tipo", subTipo);
        model.addAttribute("tipoCodigo", tipoCodigo);
        model.addAttribute("entidade", tipoEntidade);
        model.addAttribute("offset", offset);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("responsavel", responsavel);
        model.addAttribute("arquivos", arquivosPaginaAtual);
        model.addAttribute("size", size);
        model.addAttribute("absolutePath", absolutePath);

        return viewRedirect("jsp/processarLoteRescisao/listarArquivos", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=excluirArquivo"})
    public String excluirArquivo(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, IOException {
        return new DeleteWebController().excluirArquivo(request, response, session, model);
    }

}
