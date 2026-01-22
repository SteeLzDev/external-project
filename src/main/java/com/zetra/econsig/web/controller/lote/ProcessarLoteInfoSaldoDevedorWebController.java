package com.zetra.econsig.web.controller.lote;

import java.io.File;
import java.io.FileFilter;
import java.text.ParseException;
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
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaInfoSaldoDevedor;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ProcessarLoteInfoSaldoDevedorWebController</p>
 * <p>Description: Controlador Web base para o caso de uso Processar lote de Informação de Saldo Devedor.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/processarLoteInfoSaldoDevedor" })
public class ProcessarLoteInfoSaldoDevedorWebController extends ControlePaginacaoWebController{

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = { "acao=listarConsignataria" })
    public String listarConsignataria(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        if (responsavel.isCsa()) {
            if (request.getParameter("back") != null && request.getParameter("back").equals("1")) {
                String link = "../v3/carregarPrincipal";
                model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
                return "jsp/redirecionador/redirecionar";
            } else {
                String link = "../v3/processarLoteInfoSaldoDevedor?acao=listarArquivosImportacao&CSA_CODIGO=" + responsavel.getCsaCodigo();
                model.addAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(link, request)));
                return "jsp/redirecionador/redirecionar";
            }
        }
        if (!responsavel.isCsa() && !responsavel.isSup() && !responsavel.temPermissao(CodedValues.FUN_PROCESSA_LOTE_INF_SALDO_DEVEDOR)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<TransferObject> consignatarias = null;

        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        String filtro2 = JspHelper.verificaVarQryStr(request, "FILTRO2");
        int filtroTipo = -1;
        filtroTipo = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO")) ? Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO")): filtroTipo;

        try {
            CustomTransferObject criterio = new CustomTransferObject();

            // -------------- Seta Criterio da Listagem ------------------
            // Bloqueado
            if (filtroTipo == 0) {
                List<Short> statusCsa = new ArrayList<>();
                statusCsa.add(CodedValues.STS_INATIVO);
                statusCsa.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                criterio.setAttribute(Columns.CSA_ATIVO, statusCsa);
                // Desbloqueado
            } else if (filtroTipo == 1) {
                criterio.setAttribute(Columns.CSA_ATIVO, CodedValues.STS_ATIVO);
                // Outros
            } else if (!filtro.equals("") && filtroTipo != -1) {
                String campo = null;

                switch (filtroTipo) {
                case 2: campo = Columns.CSA_IDENTIFICADOR; break;
                case 3: campo = Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV; break;
                default:
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            }

            if (!filtro2.equals("")) {
                criterio.setAttribute(Columns.CSA_NOME, filtro2 + CodedValues.LIKE_MULTIPLO);
            }

            int total = consignatariaController.countConsignatarias(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            offset = !TextHelper.isNull(request.getParameter("offset")) ? Integer.parseInt(request.getParameter("offset")) : offset;

            consignatarias = consignatariaController.lstConsignatarias(criterio, offset, size, responsavel);

            String linkListagem = "../v3/processarLoteInfoSaldoDevedor?acao=listarConsignataria";
            configurarPaginador(linkListagem, "rotulo.processar.lote.saldo.devedor.titulo", total, size, null, false, request, model);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            consignatarias = new ArrayList<>();
        }

        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtroTipo);
        model.addAttribute("consignatarias", consignatarias);
        model.addAttribute("saldoDevedor", true);

        return viewRedirect("jsp/processarLote/listarConsignataria", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=listarArquivosImportacao" })
    public String listarArquivosImportacao(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, InstantiationException, IllegalAccessException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Valida o token de sessão para evitar a chamada direta à operação
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo = responsavel.isSup() ? JspHelper.verificaVarQryStr(request, "CSA_CODIGO") : responsavel.isCsa() ? responsavel.getCodigoEntidade() : "";

        if (!responsavel.isCsa() && !responsavel.isSup() && !responsavel.temPermissao(CodedValues.FUN_PROCESSA_LOTE_INF_SALDO_DEVEDOR)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        boolean podeExcluirArqLote = responsavel.temPermissao(CodedValues.FUN_UPL_ARQUIVOS);
        boolean temProcessoRodando = false;

        // Verifica se existe algum processo rodando para o usuário
        String chave1 = "impInfoSaldoDevedor" + "|" + csaCodigo;
        temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

        final String tipo = "saldodevedor";
        absolutePath += File.separatorChar + tipo + File.separatorChar + "csa" + File.separatorChar + csaCodigo + File.separatorChar;

        File diretorio = new File(absolutePath);
        if (!diretorio.exists() && !diretorio.mkdirs()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.diretorio.inexistente", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        FileFilter filtro = arq -> {
            String arqName = arq.getName().toLowerCase();
            return arq.isFile() && (arqName.endsWith(".txt") || arqName.endsWith(".zip"));
        };

        List<File> arquivos = null;
        File[] temp = diretorio.listFiles(filtro);
        if (temp != null) {
            arquivos = new ArrayList<>();
            arquivos.addAll(Arrays.asList(temp));
        }

        if (arquivos != null) {
            Collections.sort(arquivos, (f1, f2) -> {
                Long d1 = f1.lastModified();
                Long d2 = f2.lastModified();
                return d2.compareTo(d1);
            });
        }

        // Paginacao
        int size = JspHelper.LIMITE;
        int offset = 0;
        offset = !TextHelper.isNull(request.getParameter("offset")) ? Integer.parseInt(request.getParameter("offset")) : offset;

        String parametros = "CSA_CODIGO=" + csaCodigo;

        int total = arquivos !=null && !arquivos.isEmpty() ? arquivos.size() : 0;

        String linkListagem = "../v3/processarLoteInfoSaldoDevedor?acao=listarArquivosImportacao&" + parametros;
        configurarPaginador(linkListagem, "rotulo.processar.lote.saldo.devedor.titulo", total, size, null, false, request, model);

        model.addAttribute("parametros", parametros);
        model.addAttribute("tipo", tipo);
        model.addAttribute("tipoCodigo", csaCodigo);
        model.addAttribute("entidade", "csa");
        model.addAttribute("offset", offset);
        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("quinzenal", false);
        model.addAttribute("podeValidarLote", false);
        model.addAttribute("podeProcessarLote", false);
        model.addAttribute("responsavel", responsavel);
        model.addAttribute("podeExcluirArqLote", podeExcluirArqLote);
        model.addAttribute("arquivos", arquivos);
        model.addAttribute("size", size);
        model.addAttribute("absolutePath", absolutePath);
        model.addAttribute("saldoDevedor",true);

        return viewRedirect("jsp/processarLote/listarArquivosImportacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=validar" })
    public String validar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ParseException, InstantiationException, IllegalAccessException, ParametroControllerException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        String csaCodigo = responsavel.isCseSup() ? JspHelper.verificaVarQryStr(request, "CSA_CODIGO") : responsavel.isCsa() ? responsavel.getCodigoEntidade() : "";

        if (!responsavel.isCsa() && !responsavel.isSup() && !responsavel.temPermissao(CodedValues.FUN_PROCESSA_LOTE_INF_SALDO_DEVEDOR)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean temProcessoRodando = false;

        String chave1 = "impInfoSaldoDevedor" + "|" + csaCodigo;
        temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);
        String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");

        if (!temProcessoRodando) {
            // Se o arquivo escolhido não está sendo processado
            // então inicia o processamento.
            ProcessaInfoSaldoDevedor processaInfoSaldoDevedor = new ProcessaInfoSaldoDevedor(nomeArquivoEntrada, csaCodigo, true, responsavel);
            processaInfoSaldoDevedor.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.arquivo.validar", responsavel, nomeArquivoEntrada));
            processaInfoSaldoDevedor.start();
            ControladorProcessos.getInstance().incluir(chave1, processaInfoSaldoDevedor);
            temProcessoRodando = true;
            request.setAttribute("temProcessoRodando", temProcessoRodando);
        } else {
            // Se o arquivo está sendo processando por outro usuário,
            // dá mensagem de erro ao usuário e permite que ele escolha
            // outro arquivo
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.arquivo.sendo.processado", responsavel, nomeArquivoEntrada));
            temProcessoRodando = false;
        }

        paramSession.halfBack();

        return listarArquivosImportacao(request, response, session, model);
    }

    @RequestMapping(params = { "acao=processar" })
    public String processar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws InstantiationException, IllegalAccessException, ParseException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        ParamSession paramSession = ParamSession.getParamSession(session);

        String csaCodigo = responsavel.isSup() ? JspHelper.verificaVarQryStr(request, "CSA_CODIGO") : responsavel.isCsa() ? responsavel.getCodigoEntidade() : "";

        if (!responsavel.isCsa() && !responsavel.isSup() && !responsavel.temPermissao(CodedValues.FUN_PROCESSA_LOTE_INF_SALDO_DEVEDOR)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        boolean temProcessoRodando = false;
        String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");
        String chave1 = "impInfoSaldoDevedor" + "|" + csaCodigo;
        temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

        if (!temProcessoRodando) {
            // Se o arquivo escolhido não está sendo processado
            // então inicia o processamento.
            ProcessaInfoSaldoDevedor processaInfoSaldoDevedor = new ProcessaInfoSaldoDevedor(nomeArquivoEntrada, csaCodigo, false, responsavel);
            processaInfoSaldoDevedor.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.arquivo.processar", responsavel, nomeArquivoEntrada));
            processaInfoSaldoDevedor.start();
            ControladorProcessos.getInstance().incluir(chave1, processaInfoSaldoDevedor);
            temProcessoRodando = true;
            request.setAttribute("temProcessoRodando", temProcessoRodando);
        } else {
            // Se o arquivo está sendo processando por outro usuário,
            // dá mensagem de erro ao usuário e permite que ele escolha
            // outro arquivo
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.saldo.devedor.arquivo.sendo.processado", responsavel, nomeArquivoEntrada));
            temProcessoRodando = false;
        }

        paramSession.halfBack();

        return listarArquivosImportacao(request, response, session, model);
    }
}
