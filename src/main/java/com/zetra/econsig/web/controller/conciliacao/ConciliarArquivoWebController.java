package com.zetra.econsig.web.controller.conciliacao;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaConciliacao;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ConciliarArquivoWebCOntroller</p>
 * <p>Description: Controlador Web para o caso de uso Conciliar Arquivo.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: leonardo.angoti $
 * $Revision: 29457 $
 * $Date: 2020-05-20 09:10:50 -0300 (qua, 20 mai 2020) $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/conciliarArquivo" })
public class ConciliarArquivoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConciliarArquivoWebController.class);

    @Autowired
    private ConsignatariaController consignatariaController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(csaCodigo) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        if (responsavel.isCsa()) {
            String linkAtiva = SynchronizerToken.updateTokenInURL("../v3/conciliarArquivo?acao=listarXml&tipo=listar&CSA_CODIGO=" + responsavel.getCsaCodigo() + "&MM_update=true&linkRet=../v3/carregarPrincipal", request);
            request.setAttribute("url64", TextHelper.encode64(linkAtiva));
            return "jsp/redirecionador/redirecionar";
        }

        String filtro = JspHelper.verificaVarQryStr(request, "FILTRO");
        String filtro2 = JspHelper.verificaVarQryStr(request, "FILTRO2");
        int filtro_tipo = TextHelper.isNull(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO")) ? -1 : Integer.parseInt(JspHelper.verificaVarQryStr(request, "FILTRO_TIPO"));

        List<?> consignatarias = null;

        try {
            CustomTransferObject criterio = new CustomTransferObject();

            // -------------- Seta Criterio da Listagem ------------------
            // Bloqueado
            if (filtro_tipo == 0) {
                List<Short> statusCsa = new ArrayList<>();
                statusCsa.add(CodedValues.STS_INATIVO);
                statusCsa.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                criterio.setAttribute(Columns.CSA_ATIVO, statusCsa);
                // Desbloqueado
            } else if (filtro_tipo == 1) {
                criterio.setAttribute(Columns.CSA_ATIVO, CodedValues.STS_ATIVO);
                // Outros
            } else if (!filtro.equals("") && filtro_tipo != -1) {
                String campo = null;

                switch (filtro_tipo) {
                    case 2:
                        campo = Columns.CSA_IDENTIFICADOR;
                        break;
                    case 3:
                        campo = Columns.CSA_NOME + CodedValues.OR_KEY + Columns.CSA_NOME_ABREV;
                        break;
                    default:
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                criterio.setAttribute(campo, CodedValues.LIKE_MULTIPLO + filtro + CodedValues.LIKE_MULTIPLO);
            }

            if (!filtro2.equals("")) {
                criterio.setAttribute(Columns.CSA_NOME, filtro2 + CodedValues.LIKE_MULTIPLO);
            }
            // ---------------------------------------

            int total = consignatariaController.countConsignatarias(criterio, responsavel);
            int size = JspHelper.LIMITE;
            int offset = 0;
            try {
                offset = Integer.parseInt(request.getParameter("offset"));
            } catch (Exception ex) {
            }

            consignatarias = consignatariaController.lstConsignatarias(criterio, offset, size, responsavel);

            String linkListagem = "../v3/conciliarArquivo?acao=iniciar";
            configurarPaginador(linkListagem, "rotulo.listar.arquivos.download.rescisao.titulo.paginacao", total, size, null, false, request, model);
        } catch (Exception ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            consignatarias = new ArrayList<>();
        }

        model.addAttribute("filtro", filtro);
        model.addAttribute("filtro_tipo", filtro_tipo);
        model.addAttribute("consignatarias", consignatarias);

        return viewRedirect("jsp/conciliarArquivo/listarConsignataria", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=selecionar" })
    public String selecionar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "XML")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String csaCodigo = responsavel.isCseSup() ? JspHelper.verificaVarQryStr(request, "CSA_CODIGO") : responsavel.isCsa() ? responsavel.getCodigoEntidade() : "";
        String tipoCodigo = csaCodigo;
        String tipoEntidade = (!TextHelper.isNull(csaCodigo) ? "csa" : "");

        if (TextHelper.isNull(csaCodigo) && !responsavel.temPermissao(CodedValues.FUN_IMP_ARQ_CONCILIACAO_MULTIPLAS_CSAS)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String xml = JspHelper.verificaVarQryStr(request, "XML");
        if (TextHelper.isNull(xml)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        boolean podeProcessarArquivo = responsavel.temPermissao(CodedValues.FUN_IMPORTACAO_ARQUIVO_CONCILIACAO);
        boolean podeExcluirArquivo = podeProcessarArquivo && responsavel.temPermissao(CodedValues.FUN_UPL_ARQUIVOS);

        // Verifica se existe algum processo rodando para o usuário
        String chave1 = "CONCILIACAO" + "|" + tipoCodigo + "|" + responsavel.getUsuCodigo();
        boolean temProcessoRodando = !TextHelper.isNull(request.getAttribute("temProcessoRodando")) ? (boolean) request.getAttribute("temProcessoRodando") : ControladorProcessos.getInstance().verificar(chave1, session);

        final String tipo = "conciliacao";
        String entidade = tipoEntidade;

        if (!TextHelper.isNull(tipoEntidade)) {
            absolutePath += File.separatorChar + tipo + File.separatorChar + tipoEntidade;
            absolutePath += File.separatorChar + tipoCodigo + File.separatorChar;
        } else {
            absolutePath += File.separatorChar + tipo + File.separatorChar + "cse" + File.separatorChar;
        }

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
            Collections.sort(arquivos, (f1, f2) -> {
                Long d1 = f1.lastModified();
                Long d2 = f2.lastModified();
                return d2.compareTo(d1);
            });
        }

        int size = JspHelper.LIMITE;
        int offset = 0;
        offset = TextHelper.isNull(request.getParameter("offset")) ? 0 : Integer.parseInt(request.getParameter("offset"));

        String parametros = null;
        if (!TextHelper.isNull(tipoEntidade)) {
            parametros = "$CSA_CODIGO(" + csaCodigo + "|XML(" + xml + "|operacao(listar";

        } else {
            parametros = "$XML(" + xml + "|operacao(listar";
        }

        int total = arquivos.size();

        String linkListagem = request.getRequestURI() + "?acao=selecionar&CSA_CODIGO=" + csaCodigo + "&XML=" + xml;
        configurarPaginador(linkListagem, "rotulo.listar.arquivos.download.rescisao.titulo.paginacao", total, size, null, false, request, model);

        String linkRet = JspHelper.verificaVarQryStr(request, "linkRet").toString();
        if (linkRet == null || linkRet.equals("")) {
            linkRet = "../v3/carregarPrincipal";
        }

        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("podeProcessarArquivo", podeProcessarArquivo);
        model.addAttribute("podeExcluirArquivo", podeExcluirArquivo);
        model.addAttribute("arquivos", arquivos);
        model.addAttribute("offset", offset);
        model.addAttribute("linkRet", linkRet);
        model.addAttribute("xml", xml);
        model.addAttribute("size", size);
        model.addAttribute("tipo", tipo);
        model.addAttribute("tipoCodigo", tipoCodigo);
        model.addAttribute("entidade", entidade);
        model.addAttribute("absolutePath", absolutePath);
        model.addAttribute("parametros", parametros);
        model.addAttribute("conciliacaoMultipla", false);

        return viewRedirect("jsp/conciliarArquivo/listarArquivoImportacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=processa" })
    public String processa(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(JspHelper.verificaVarQryStr(request, "XML")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String csaCodigo = responsavel.isCseSup() ? JspHelper.verificaVarQryStr(request, "CSA_CODIGO") : responsavel.isCsa() ? responsavel.getCodigoEntidade() : "";
        String tipoCodigo = csaCodigo;
        String tipoEntidade = (!TextHelper.isNull(csaCodigo) ? "csa" : "");

        if (TextHelper.isNull(csaCodigo) && !responsavel.temPermissao(CodedValues.FUN_IMP_ARQ_CONCILIACAO_MULTIPLAS_CSAS)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        String xml = JspHelper.verificaVarQryStr(request, "XML");
        if (TextHelper.isNull(xml)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Verifica se existe algum processo rodando para o usuário
        String chave1 = "CONCILIACAO" + "|" + tipoCodigo + "|" + responsavel.getUsuCodigo();
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave1, session);

        if (!temProcessoRodando && !JspHelper.verificaVarQryStr(request, "VALIDAR").equals("")) {
            // Se não tem processo rodando para o usuário, e o usuário
            // mandou processar um arquivo, então ...
            String nomeArquivoEntrada = JspHelper.verificaVarQryStr(request, "arquivo_nome");
            String nomeArqXmlEntrada = xml + "_entrada.xml";
            String nomeArqXmlTradutor = xml + "_tradutor.xml";
            String fileName = ParamSist.getDiretorioRaizArquivos();
            if (!TextHelper.isNull(tipoEntidade)) {
                fileName += File.separatorChar + "conciliacao" + File.separatorChar + tipoEntidade + File.separatorChar + tipoCodigo + File.separatorChar + nomeArquivoEntrada;
            } else {
                fileName += File.separatorChar + "conciliacao" + File.separatorChar + "cse" + File.separatorChar + nomeArquivoEntrada;
            }

            // Verifica se algum outro usuário da consignatária está processando
            // o arquivo escolhido pelo usuário.
            String chave2 = "CONCILIACAO" + "|" + tipoCodigo + "|" + nomeArquivoEntrada;
            temProcessoRodando = ControladorProcessos.getInstance().verificar(chave2, session);

            if (!temProcessoRodando) {
                // Se o arquivo escolhido não está sendo processado
                // então inicia o processamento.
                ProcessaConciliacao processaConciliacao = new ProcessaConciliacao(fileName, nomeArqXmlEntrada, nomeArqXmlTradutor, csaCodigo, !TextHelper.isNull(tipoEntidade) ? tipoEntidade : "cse", tipoCodigo, responsavel);
                processaConciliacao.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.arquivo", responsavel, nomeArquivoEntrada));
                processaConciliacao.start();
                ControladorProcessos.getInstance().incluir(chave1, processaConciliacao);
                ControladorProcessos.getInstance().incluir(chave2, processaConciliacao);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.conciliacao.arquivo.sendo.processado", responsavel, nomeArquivoEntrada));
                temProcessoRodando = true;
            } else {
                // Se o arquivo está sendo processando por outro usuário,
                // dá mensagem de erro ao usuário e permite que ele escolha
                // outro arquivo
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.conciliacao.arquivo.sendo.processado", responsavel, nomeArquivoEntrada));
                temProcessoRodando = false;
            }
        }

        model.addAttribute("temProcessoRodando", temProcessoRodando);

        return selecionar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=listarXml" })
    public String listaXml(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        String csaCodigo = JspHelper.verificaVarQryStr(request, "CSA_CODIGO");

        //Valida o token de sessão para evitar a chamada direta à operação
        if (!TextHelper.isNull(csaCodigo) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        try {
            if (responsavel.isCsa()) {
                csaCodigo = responsavel.getCodigoEntidade();
            }

            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            String pathXml = absolutePath + File.separatorChar + "conf" + File.separatorChar + "conciliacao" + File.separatorChar + csaCodigo;
            String pathXmlDefault = absolutePath + File.separatorChar + "conf" + File.separatorChar + "conciliacao" + File.separatorChar + "xml";

            // Cria filtro para seleção de arquivos .xml
            FileFilter filtro = arq -> {
                String arqNome = arq.getName().toLowerCase();
                return (arqNome.endsWith("_entrada.xml"));
            };

            // Faz as checagens de diretório
            File diretorioXml = new File(pathXml);
            File diretorioXmlDefault = new File(pathXmlDefault);

            if (!diretorioXml.exists() && !diretorioXml.mkdirs() && !diretorioXmlDefault.exists() && !diretorioXmlDefault.mkdirs()) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.lst.arq.generico.criacao.diretorio", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            // Lista os arquivos
            ArrayList<Object> arquivosXmlEntrada = null;
            Object[] temp = null;
            if (diretorioXml.exists()) {
                temp = diretorioXml.listFiles(filtro);
            } else {
                temp = diretorioXmlDefault.listFiles(filtro);
            }
            if (temp != null) {
                arquivosXmlEntrada = new ArrayList<>();
                arquivosXmlEntrada.addAll(Arrays.asList(temp));
            }

            File arqEntrada = null;
            File arqTradutor = null;

            String nomeTabela = null;

            List<String> nomesTabelas = new ArrayList<>();

            if (arquivosXmlEntrada != null) {
                Iterator<?> it = arquivosXmlEntrada.iterator();
                while (it.hasNext()) {
                    arqEntrada = (File) it.next();
                    nomeTabela = arqEntrada.getName().substring(0, arqEntrada.getName().indexOf("_entrada.xml"));

                    arqTradutor = new File(pathXml + File.separatorChar + nomeTabela + "_tradutor.xml");

                    if (arqTradutor.exists()) {
                        nomesTabelas.add(nomeTabela);
                    }
                }

                // Ordena os arquivos baseado na data de modificação
                Collections.sort(nomesTabelas, String::compareTo);
            }

            // Lista os arquivos para o diretório default caso não encontre o xml no diretório específico
            boolean xmlDefault = false;
            if (nomesTabelas.size() == 0) {
                xmlDefault = true;
                arquivosXmlEntrada = null;
                temp = diretorioXmlDefault.listFiles(filtro);
                if (temp != null) {
                    arquivosXmlEntrada = new ArrayList<>();
                    arquivosXmlEntrada.addAll(Arrays.asList(temp));
                }

                arqEntrada = null;
                arqTradutor = null;

                nomeTabela = null;

                if (arquivosXmlEntrada != null) {
                    Iterator<?> it = arquivosXmlEntrada.iterator();
                    while (it.hasNext()) {
                        arqEntrada = (File) it.next();
                        nomeTabela = arqEntrada.getName().substring(0, arqEntrada.getName().indexOf("_entrada.xml"));

                        arqTradutor = new File(pathXmlDefault + File.separatorChar + nomeTabela + "_tradutor.xml");

                        if (arqTradutor.exists()) {
                            nomesTabelas.add(nomeTabela);
                        }
                    }

                    // Ordena os arquivos baseado na data de modificação
                    Collections.sort(nomesTabelas, String::compareTo);
                }
            }

            String linkRet = JspHelper.verificaVarQryStr(request, "linkRet").toString();

            ParamSession paramSession = ParamSession.getParamSession(session);

            if (nomesTabelas.size() == 1) {
                String linkAtiva = "../v3/conciliarArquivo?acao=selecionar&CSA_CODIGO=" + csaCodigo + "&XML=" + nomesTabelas.iterator().next().toString() + "&MM_update=true&linkRet=" + linkRet + "&" + SynchronizerToken.generateToken4URL(request);

                request.setAttribute("url64", TextHelper.encode64(linkAtiva));
                return "jsp/redirecionador/redirecionar";
            } else if (nomesTabelas.size() == 0) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.conciliacao.layout.ausente", responsavel));
            }

            if (linkRet == null || linkRet.equals("")) {
                linkRet = SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request);
            }

            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("nomesTabelas", nomesTabelas);
            model.addAttribute("xmlDefault", xmlDefault);
            model.addAttribute("pathXmlDefault", pathXmlDefault);
            model.addAttribute("pathXml", pathXml);
            model.addAttribute("linkRet", linkRet);
            model.addAttribute("conciliacaoMultipla", false);

            return viewRedirect("jsp/conciliarArquivo/listarArquivosConfiguracao", request, session, model, responsavel);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }
}
