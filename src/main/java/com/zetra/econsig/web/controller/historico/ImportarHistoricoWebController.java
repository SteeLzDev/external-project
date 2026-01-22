package com.zetra.econsig.web.controller.historico;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.Pair;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.folha.ArquivoIntegracaoHelper;
import com.zetra.econsig.helper.folha.HistoricoHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>
 * Title: ImportarHistoricoWebController
 * </p>
 * <p>
 * Description: Controlador Web para o caso de uso Importar Histórico.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002-2017
 * </p>
 * <p>
 * Company: ZetraSoft
 * </p>
 * $Author: rodrigo.rosa $ $Revision: 29011 $ $Date: 2020-04-01 13:25:44 -0300
 * (qui, 01 apr 2020) $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/importarHistorico" })
public class ImportarHistoricoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportarHistoricoWebController.class);

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ZetraException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Como vem da página inicial apenas salva o token
        SynchronizerToken.saveToken(request);

        // Se não é usuário suporte da erro
        if (!responsavel.isSup()) {
            throw new ZetraException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        String tipo = "historico";

        // Path dos arquivos de historico
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        // Path dos arquivos de retorno
        String pathHistorico = absolutePath + File.separatorChar + "historico" + File.separatorChar;

        // Faz as checagens de diretório
        File diretorioRetorno = new File(pathHistorico);

        if ((!diretorioRetorno.exists() && !diretorioRetorno.mkdirs())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.lst.arq.generico.criacao.diretorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<TransferObject> lstMtvOperacao = tipoMotivoOperacaoController.lstMotivoOperacaoConsignacao(CodedValues.STS_ATIVO, responsavel);
        String motivo = null;
        if (!TextHelper.isNull(request.getParameter("tmoCodigo"))) {
            motivo = tipoMotivoOperacaoController.findMotivoOperacao(request.getParameter("tmoCodigo"), responsavel).getTmoDescricao();
        }

        // Lista os arquivos
        List<Pair<File, String>> arquivosHistorico = ArquivoIntegracaoHelper.listarArquivosHistorico(responsavel);
        boolean atalhoUpload = responsavel.isSup() && responsavel.temPermissao(CodedValues.FUN_UPL_ARQUIVOS);

        model.addAttribute("atalhoUpload", atalhoUpload);
        model.addAttribute("tipo", tipo);
        model.addAttribute("pathHistorico", pathHistorico);
        model.addAttribute("arquivosHistorico", arquivosHistorico);
        model.addAttribute("lstMtvOperacao", lstMtvOperacao);
        model.addAttribute("motivo", motivo);

        if (request.getParameter("fluxoExcluirArquivo") != null) {
            //Se veio do fluxo de excluir arquivo seta mensagem para o usuário
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.excluido.sucesso", responsavel));
        }

        return viewRedirect("jsp/importarHistorico/importarHistorico", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=importar" })
    public String importar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ZetraException {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String nomeArquivo = JspHelper.verificaVarQryStr(request, "arquivo_nome");
        String tipo = "historico";

        // Path dos arquivos de historico
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        // Path dos arquivos de retorno
        String pathHistorico = absolutePath + File.separatorChar + "historico" + File.separatorChar;
        // Diretório raiz do arquivos eConsig
        String pathConf = absolutePath + File.separatorChar + "conf";

        // Faz a importação do retorno de integração
        if (!nomeArquivo.equals("") && !tipo.equals("")) {
            int timeout = session.getMaxInactiveInterval();
            try {
                session.setMaxInactiveInterval(-1);

                ReservarMargemParametros margemParam = new ReservarMargemParametros();
                margemParam.setValidaTaxaJuros(Boolean.valueOf(request.getParameter("validarTaxaJuros_")));
                margemParam.setValidaPrazo(Boolean.valueOf(request.getParameter("validarPrazo_")));
                margemParam.setValidaDadosBancarios(Boolean.valueOf(request.getParameter("validarDadosBancarios_")));
                margemParam.setValidaSenhaServidor(Boolean.valueOf(request.getParameter("validarSenhaServidor_")));
                margemParam.setValidaDataNascimento(Boolean.valueOf(request.getParameter("validarDataNasc_")));
                margemParam.setValidaLimiteAde(Boolean.valueOf(request.getParameter("validarLimiteContrato_")));
                margemParam.setTmoCodigo(request.getParameter("tmoCodigo_"));
                margemParam.setOcaObs(request.getParameter("adeObs_"));

                boolean serAtivo = Boolean.valueOf(request.getParameter("servidoresAtivos_"));
                boolean cnvAtivo = Boolean.valueOf(request.getParameter("conveniosAtivos_"));
                boolean svcAtivo = Boolean.valueOf(request.getParameter("servicosAtivos_"));
                boolean serCnvAtivo = Boolean.valueOf(request.getParameter("servidoresConvenioAtivos_"));
                boolean csaAtivo = Boolean.valueOf(request.getParameter("consignatariasAtivas_"));
                boolean orgAtivo = Boolean.valueOf(request.getParameter("orgaosAtivos_"));
                boolean estAtivo = Boolean.valueOf(request.getParameter("estabelecimentosAtivos_"));
                boolean cseAtivo = Boolean.valueOf(request.getParameter("consignantesAtivos_"));

                String xmlEntrada = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_HISTORICO, responsavel);
                String xmlTradutor = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_HISTORICO, responsavel);

                if (TextHelper.isNull(xmlEntrada)) {
                    xmlEntrada = "imp_consignacao_entrada.xml";
                }

                if (TextHelper.isNull(xmlTradutor)) {
                    xmlTradutor = "imp_consignacao_tradutor.xml";
                }

                File arquivoHistorico = new File(pathHistorico + nomeArquivo);
                if (arquivoHistorico.exists() && arquivoHistorico.getCanonicalPath().startsWith(absolutePath)) {
                    HistoricoHelper historicoHelper = new HistoricoHelper();
                    historicoHelper.importaLoteConsignacao(arquivoHistorico.getParent(), pathConf, CodedValues.CSE_CODIGO_SISTEMA, arquivoHistorico.getName(), xmlEntrada, xmlTradutor, false, false, serAtivo, cnvAtivo, svcAtivo, serCnvAtivo, csaAtivo, orgAtivo, estAtivo, cseAtivo, true, false, false, margemParam, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.sucesso.importacao.retorno", responsavel));
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            } finally {
                session.setMaxInactiveInterval(timeout);
            }
        }

        return iniciar(request, response, session, model);
    }

    @RequestMapping(params = { "acao=excluirArquivoHistorico" })
    public String excluirArquivoHistorico(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, ZetraException {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String nomeArquivo = TextHelper.isNull(request.getParameter("arquivo_nome")) ? ApplicationResourcesHelper.getMessage("rotulo.include.get.file.desconhecido", responsavel) : request.getParameter("arquivo_nome");
        String msg = ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.nao.encontrado", responsavel, nomeArquivo);

        String tipo = "historico";
        File arquivo = null;

        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        if (absolutePath != null) {
            absolutePath = new File(absolutePath).getCanonicalPath();

            String name = java.net.URLDecoder.decode(nomeArquivo, "UTF-8");
            if (name.indexOf("..") != -1) {
                session.setAttribute(CodedValues.MSG_ERRO, msg);

            }

            String fileName = null;
            if (tipo.equals("xml")) {
                fileName = absolutePath + File.separatorChar + "conf";
            } else {
                fileName = absolutePath + File.separatorChar + tipo;
            }

            fileName += File.separatorChar + name;

            arquivo = new File(fileName);
            if (!arquivo.exists() || !arquivo.getCanonicalPath().startsWith(absolutePath)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.nao.encontrado", responsavel, name));
            } else {
                request.setAttribute("file", arquivo);
            }

        } else {
            msg += ApplicationResourcesHelper.getMessage("mensagem.erro.include.get.file.configuracao.diretorio", responsavel);
            session.setAttribute(CodedValues.MSG_ERRO, msg);
        }

        Object arquivos = arquivo;
        if (arquivos == null) {
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } else {
            // Gera log de remoção de arquivo
            try {
                LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.DELETE_FILE, Log.LOG_INFORMACAO);
                if (arquivos instanceof File) {
                    log.add(ApplicationResourcesHelper.getMessage("rotulo.delete.arquivo.log", responsavel) + ": " + ((File) arquivos).getAbsolutePath());
                } else if (arquivos instanceof List) {
                    for (File fileInList : ((List<File>) arquivos)) {
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
                for (File fileInList : ((List<File>) arquivos)) {
                    fileInList.delete();
                }
            }
        }
        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.excluido.sucesso", responsavel));
        return iniciar(request, response, session, model);

    }

    @RequestMapping(params = { "acao=atalhoUpload"})
    public String carregarUpload(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model){
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        List<TransferObject> consignatarias = null;
        List<TransferObject> correspondentes = new ArrayList<>();

        boolean selecionaEstOrgUploadMargemRetorno = false;
        boolean selecionaEstOrgUploadContracheque = false;
        String tipo = "";
        String papCodigo = "";
        String orgCodigo = "";
        String estCodigo = "";
        String csaCodigo = "";

        String pathCombo = null;
        String pathDownload = null;

        List<String> fileNameAbrev = new ArrayList<>();
        List<String> codigosOrgao = new ArrayList<>();
        HashMap<Object, CustomTransferObject> orgaos = new HashMap<>();
        boolean exibirArquivo = false;

        boolean temProcessoRodando = false;
        String msgResultadoComando = "";

        boolean temPermissaoEst = false;

        if (responsavel.isOrg()) {
            temPermissaoEst = responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO);
        }

        boolean exibeCaptcha = false;
        boolean exibeCaptchaAvancado = false;
        boolean exibeCaptchaDeficiente = false;
        boolean exibeCampoUpload = false;

        //Verificação se pode ou não enviar comentário
        boolean comentario = false;

        List<TransferObject> lstEstabelecimentos = new ArrayList<>();
        List<TransferObject> lstOrgaos = new ArrayList<>();

        String action = "";
        String fluxo = "uploadListarHistorico";

        model.addAttribute("responsavel", responsavel);
        model.addAttribute("csaCodigo", csaCodigo);
        model.addAttribute("correspondentes", correspondentes);
        model.addAttribute("selecionaEstOrgUploadMargemRetorno", selecionaEstOrgUploadMargemRetorno);
        model.addAttribute("selecionaEstOrgUploadContracheque", selecionaEstOrgUploadContracheque);
        model.addAttribute("fileNameAbrev", fileNameAbrev);
        model.addAttribute("codigosOrgao", codigosOrgao);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("msgResultadoComando", msgResultadoComando);
        model.addAttribute("tipo", tipo);
        model.addAttribute("papCodigo", papCodigo);
        model.addAttribute("estCodigo", estCodigo);
        model.addAttribute("orgCodigo", orgCodigo);
        model.addAttribute("temPermissaoEst", temPermissaoEst);
        model.addAttribute("consignatarias", consignatarias);
        model.addAttribute("exibeCaptcha", exibeCaptcha);
        model.addAttribute("exibeCaptchaAvancado", exibeCaptchaAvancado);
        model.addAttribute("exibeCaptchaDeficiente", exibeCaptchaDeficiente);
        model.addAttribute("comentario", comentario);
        model.addAttribute("exibirArquivo", exibirArquivo);
        model.addAttribute("pathCombo", pathCombo);
        model.addAttribute("pathDownload", pathDownload);
        model.addAttribute("orgaos", orgaos);
        model.addAttribute("lstEstabelecimentos", lstEstabelecimentos);
        model.addAttribute("lstOrgaos", lstOrgaos);
        model.addAttribute("action", action);
        model.addAttribute("exibeCampoUpload", exibeCampoUpload);
        model.addAttribute("fluxo", fluxo);

        return viewRedirect("jsp/uploadArquivo/uploadAtalhoArquivo", request, session, model, responsavel);
    }

}
