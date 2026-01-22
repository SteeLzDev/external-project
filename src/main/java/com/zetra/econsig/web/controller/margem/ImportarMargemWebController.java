package com.zetra.econsig.web.controller.margem;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.CustomTransferObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.Pair;
import com.zetra.econsig.helper.folha.ArquivoIntegracaoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaMargem;
import com.zetra.econsig.job.process.ProcessaTransferidos;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ImportarMargemWebController</p>
 * <p>Description: Controlador Web para o caso de uso Processar Importação de Margem.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/importarMargem" })
public class ImportarMargemWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportarMargemWebController.class);

    private static final String CHAVE_PROCESSO = "PROCESSO_FOLHA(MARGEM/RETORNO)";

    @Autowired
    private ConsignanteController consignanteController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        // Como vem da página inicial ou do menu, apenas salva o token
        SynchronizerToken.saveToken(request);

        try {
            boolean margemTotal = ParamSist.paramEquals(CodedValues.TPC_IMP_MARGEM_TOTAL, CodedValues.TPC_SIM, responsavel);
            boolean geraTrans = ParamSist.paramEquals(CodedValues.TPC_GERA_ARQUIVO_TRANSFERIDOS, CodedValues.TPC_SIM, responsavel);
            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(CHAVE_PROCESSO, session);

            String pathMargem = ParamSist.getDiretorioRaizArquivos();
            String pathTransferidos = pathMargem;
            String pathMargemComplementar = pathMargem;

            if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                pathMargem += File.separatorChar + "margem" + File.separatorChar + "est" + File.separatorChar + responsavel.getCodigoEntidadePai() + File.separatorChar;
                pathTransferidos += File.separatorChar + "transferidos" + File.separatorChar + "est" + File.separatorChar + responsavel.getCodigoEntidadePai() + File.separatorChar;
                pathMargemComplementar += File.separatorChar + "margemcomplementar" + File.separatorChar + "est" + File.separatorChar + responsavel.getCodigoEntidadePai() + File.separatorChar;
            } else if (responsavel.isOrg()) {
                pathMargem += File.separatorChar + "margem" + File.separatorChar + "cse" + File.separatorChar + responsavel.getCodigoEntidade() + File.separatorChar;
                pathTransferidos += File.separatorChar + "transferidos" + File.separatorChar + "cse" + File.separatorChar + responsavel.getCodigoEntidade() + File.separatorChar;
                pathMargemComplementar += File.separatorChar + "margemcomplementar" + File.separatorChar + "cse" + File.separatorChar + responsavel.getCodigoEntidade() + File.separatorChar;
            } else if (responsavel.isCseSup()) {
                pathMargem += File.separatorChar + "margem" + File.separatorChar;
                pathTransferidos += File.separatorChar + "transferidos" + File.separatorChar;
                pathMargemComplementar += File.separatorChar + "margemcomplementar" + File.separatorChar;
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            List<TransferObject> lstEstabelecimentos = consignanteController.lstEstabelecimentos(null, responsavel);
            List<TransferObject> lstOrgaos = consignanteController.lstOrgaos(null, responsavel);

            // Listar arquivos de margem
            List<ArquivoDownload> arquivosMargem = new ArrayList<>();
            for (Pair<File, String> arquivoMargem : ArquivoIntegracaoHelper.listarArquivosMargem(responsavel)) {
                File arquivo = arquivoMargem.first;
                String entidade = arquivoMargem.second;
                String tamanho = "";
                if (arquivo.length() > 1024.00) {
                    tamanho = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                } else {
                    tamanho = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                }
                String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
                String nome = java.net.URLEncoder.encode(arquivo.getPath().substring(pathMargem.length()), "UTF-8");

                arquivosMargem.add(new ArquivoDownload(arquivo, nome, tamanho, data, entidade));
            }

            // Listar arquivos de margem complementar
            List<ArquivoDownload> arquivosMargemComplementar = new ArrayList<>();
            for (Pair<File, String> arquivoMargem : ArquivoIntegracaoHelper.listarArquivosMargemComplementar(responsavel)) {
                File arquivo = arquivoMargem.first;
                String entidade = arquivoMargem.second;
                String tamanho = "";
                if (arquivo.length() > 1024.00) {
                    tamanho = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                } else {
                    tamanho = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                }
                String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
                String nome = java.net.URLEncoder.encode(arquivo.getPath().substring(pathMargemComplementar.length()), "UTF-8");

                arquivosMargemComplementar.add(new ArquivoDownload(arquivo, nome, tamanho, data, entidade));
            }

            // Listar arquivos de transferidos
            List<ArquivoDownload> arquivosTransferidos = new ArrayList<>();
            for (Pair<File, String> arquivoMargem : ArquivoIntegracaoHelper.listarArquivosTransferidos(responsavel)) {
                File arquivo = arquivoMargem.first;
                String entidade = arquivoMargem.second;
                String tamanho = "";
                if (arquivo.length() > 1024.00) {
                    tamanho = Math.round(arquivo.length() / 1024.00) + " " + ApplicationResourcesHelper.getMessage("rotulo.kilobyte.abreviado", responsavel);
                } else {
                    tamanho = arquivo.length() + " " + ApplicationResourcesHelper.getMessage("rotulo.byte.abreviado", responsavel);
                }
                String data = DateHelper.toDateTimeString(new java.sql.Date(arquivo.lastModified()));
                String nome = java.net.URLEncoder.encode(arquivo.getPath().substring(pathTransferidos.length()), "UTF-8");

                arquivosTransferidos.add(new ArquivoDownload(arquivo, nome, tamanho, data, entidade));
            }


            boolean atalhoUpload = responsavel.isSup() && responsavel.temPermissao(CodedValues.FUN_UPL_ARQUIVOS);

            model.addAttribute("atalhoUpload", atalhoUpload);
            model.addAttribute("margemTotal", margemTotal);
            model.addAttribute("geraTrans", geraTrans);
            model.addAttribute("temProcessoRodando", temProcessoRodando);
            model.addAttribute("lstEstabelecimentos", lstEstabelecimentos);
            model.addAttribute("lstOrgaos", lstOrgaos);
            model.addAttribute("arquivosMargem", arquivosMargem);
            model.addAttribute("arquivosMargemComplementar", arquivosMargemComplementar);
            model.addAttribute("arquivosTransferidos", arquivosTransferidos);

            return viewRedirect("jsp/importarMargem/importarMargem", request, session, model, responsavel);
        } catch (UnsupportedEncodingException | ConsignanteControllerException e) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=processar" })
    public String processar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        //Valida o token de sessão para evitar a chamada direta à operação apenas se um arquivo foi selecionado
        if (!SynchronizerToken.isTokenValid(request) && !TextHelper.isNull(request.getParameter("arquivo_nome"))) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        String tipoEntidade = responsavel.getTipoEntidade();
        String codigoEntidade = responsavel.getCodigoEntidade();
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            tipoEntidade = "EST";
            codigoEntidade = responsavel.getCodigoEntidadePai();
        } else if (responsavel.isSup()) {
            tipoEntidade = "CSE";
        }

        // Verifica se existe algum processo rodando para o usuário
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(CHAVE_PROCESSO, session);

        boolean margemTotal = ParamSist.paramEquals(CodedValues.TPC_IMP_MARGEM_TOTAL, CodedValues.TPC_SIM, responsavel);
        boolean geraTrans = ParamSist.paramEquals(CodedValues.TPC_GERA_ARQUIVO_TRANSFERIDOS, CodedValues.TPC_SIM, responsavel);

        if (request.getParameter("arquivo_nome") != null && !temProcessoRodando) {
            try {
                LOG.debug("tipoEntidade = " + tipoEntidade);
                LOG.debug("codigoEntidade = " + codigoEntidade);

                String pathMargem = ParamSist.getDiretorioRaizArquivos();
                String pathTransferidos = pathMargem;
                String pathMargemComplementar = pathMargem;

                String nameArquivo = java.net.URLDecoder.decode(request.getParameter("arquivo_nome"), "UTF-8");
                boolean tipoMargem = (request.getParameter("TIPO") == null ? "" : request.getParameter("TIPO")).equalsIgnoreCase("MARGEM");
                boolean tipoMargemComplementar = (request.getParameter("TIPO") == null ? "" : request.getParameter("TIPO")).equalsIgnoreCase("MARGEMCOMPLEMENTAR");
                // se for margem complementar e o responsável for CSE/SUP, verifica se o usuário selecionou outra entidade para a importação
                if (responsavel.isCseSup() && tipoMargemComplementar) {
                    boolean alterouEntidade = (request.getParameter("ENTIDADEALTERADA") == null ? "" : request.getParameter("ENTIDADEALTERADA")).equalsIgnoreCase("S");
                    if (alterouEntidade) {
                        tipoEntidade = (request.getParameter("TIPOENTIDADE") == null ? tipoEntidade : request.getParameter("TIPOENTIDADE"));
                        codigoEntidade = (request.getParameter("CODIGOENTIDADE") == null ? codigoEntidade : request.getParameter("CODIGOENTIDADE"));
                    }
                }

                String fileName = null;
                if (tipoMargem) {
                    fileName = pathMargem + File.separatorChar + "margem" + File.separatorChar;
                } else if (tipoMargemComplementar) {
                    fileName = pathMargemComplementar + File.separatorChar + "margemcomplementar" + File.separatorChar;
                } else {
                    fileName = pathTransferidos + File.separatorChar + "transferidos" + File.separatorChar;
                }

                if (nameArquivo.indexOf("..") != -1) {
                    if (tipoMargem) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.impossibilidade.importacao.margens", responsavel));
                    } else if (tipoMargemComplementar) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.impossibilidade.importacao.margens.complementares", responsavel));
                    } else {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.impossibilidade.importacao.transferidos", responsavel));
                    }
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    fileName += "est" + File.separatorChar + responsavel.getCodigoEntidadePai() + File.separatorChar + nameArquivo;
                } else if (responsavel.isOrg()) {
                    fileName += "cse" + File.separatorChar + responsavel.getCodigoEntidade() + File.separatorChar + nameArquivo;
                } else if (responsavel.isCseSup()) {
                    // O path /cse ou /est estará no nome do arquivo
                    fileName += nameArquivo;
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                File arqImportacao = new File(fileName);
                if (!arqImportacao.exists()) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.nao.encontrado", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }

                String nomeArquivoOriginal = arqImportacao.getName().replaceAll("\\.crypt", "");

                // Importa as margens
                if (tipoMargem || tipoMargemComplementar) {
                    // Recupera se a margem marcada é total e atualiza o parametro
                    String margemMarcada = request.getParameter("TOTAL") == null ? "N" : request.getParameter("TOTAL");
                    // Recupera se gerar arquivo de critica é marcada e atualiza o parametro
                    String geraTransferidos = request.getParameter("TRANSFERIDOS") == null ? "N" : request.getParameter("TRANSFERIDOS");
                    // Seta a variavel de margem que marca os check Boxes.
                    margemTotal = margemMarcada.equals(CodedValues.TPC_SIM);
                    geraTrans = geraTransferidos.equals(CodedValues.TPC_SIM);

                    // Criar processo de importação de margem
                    ProcessaMargem processo = new ProcessaMargem(fileName, tipoEntidade, codigoEntidade, margemTotal, geraTrans, responsavel);
                    processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.margem.nome", responsavel, nomeArquivoOriginal));
                    processo.start();
                    ControladorProcessos.getInstance().incluir(CHAVE_PROCESSO, processo);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.margem.nome.processando", responsavel, nomeArquivoOriginal));

                } else {
                    ProcessaTransferidos processo = new ProcessaTransferidos(fileName, tipoEntidade, codigoEntidade, responsavel);
                    processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.transferidos.nome", responsavel, nomeArquivoOriginal));
                    processo.start();
                    ControladorProcessos.getInstance().incluir(CHAVE_PROCESSO, processo);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.transferidos.nome.processando", responsavel, nomeArquivoOriginal));
                }
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.folha.falha.importacao.margem", responsavel, ex.getMessage()));
            }
        }

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
        String fluxo = "uploadListarMargem";

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
