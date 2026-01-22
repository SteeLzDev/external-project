package com.zetra.econsig.web.controller.cadastrarretorno;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.helper.Pair;
import com.zetra.econsig.helper.folha.ArquivoIntegracaoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaConclusaoRetorno;
import com.zetra.econsig.job.process.ProcessaRetorno;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.ControlePaginacaoWebController;

/**
 * <p>Title: ListarRetornoIntegracaoWebController</p>
 * <p>Description: Controlador Web para listar o caso de Importar Retorno Integração.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: andrea.giorgini $
 * $Revision: 24194 $
 * $Date: 2018-05-08 13:41:26 -0300 (Ter, 08 mai 2018) $
 */

@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/listarRetornoIntegracao" })
public class ListarRetornoIntegracaoWebController extends ControlePaginacaoWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarRetornoIntegracaoWebController.class);

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String nomeArquivo = JspHelper.verificaVarQryStr(request, "arquivo");
        String tipo = JspHelper.verificaVarQryStr(request, "tipo");

        String orgCodigo = null;
        String estCodigo = null;

        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            estCodigo = responsavel.getCodigoEntidadePai();
        } else if (responsavel.isOrg()) {
            orgCodigo = responsavel.getCodigoEntidade();
        } else if (responsavel.isCseSup() && !nomeArquivo.equals("")) {
            // cse/nome_arquivo.txt
            // cse/org_codigo/nome_arquivo.txt ou
            // est/est_codigo/nome_arquivo.txt ou
            String[] partesNomeArq = nomeArquivo.split(File.separator);
            if (partesNomeArq.length == 3) {
                if (partesNomeArq[0].equals("cse")) {
                    orgCodigo = partesNomeArq[1];
                } else if (partesNomeArq[0].equals("est")) {
                    estCodigo = partesNomeArq[1];
                }
                nomeArquivo = partesNomeArq[1] + File.separator + partesNomeArq[2];
            } else {
                nomeArquivo = partesNomeArq[1];
            }
        }

        // Valida o token de sessão se um arquivo foi selecionado ou é uma conclusão (trata a primeira chamada a partir do menu)
        if ((!TextHelper.isNull(nomeArquivo) || tipo.equals("CONCLUIR")) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Verifica se existe algum processo rodando para o usuário
        String chave = "PROCESSO_FOLHA(MARGEM/RETORNO)";
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

        // Path dos arquivos de integração
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        // Path dos arquivos de retorno
        String pathRetorno = absolutePath + File.separatorChar + "retorno" + File.separatorChar;
        // Path dos arquivos de crítica
        String pathCritica = absolutePath + File.separatorChar + "critica" + File.separatorChar;
        // Se é usuário de órgão, concatena o código do órgão nos paths
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            pathRetorno += "est" + File.separatorChar + responsavel.getCodigoEntidadePai() + File.separatorChar;
            pathCritica += "est" + File.separatorChar + responsavel.getCodigoEntidadePai() + File.separatorChar;
        } else if (responsavel.isOrg()) {
            pathRetorno += "cse" + File.separatorChar + responsavel.getCodigoEntidade() + File.separatorChar;
            pathCritica += "cse" + File.separatorChar + responsavel.getCodigoEntidade() + File.separatorChar;
        } else if (!responsavel.isCseSup()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Faz as checagens de diretório
        File diretorioRetorno = new File(pathRetorno);
        File diretorioCritica = new File(pathCritica);

        if ((!diretorioRetorno.exists() && !diretorioRetorno.mkdirs()) || (!diretorioCritica.exists() && !diretorioCritica.mkdirs())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.lst.arq.generico.criacao.diretorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        List<Pair<File, String>> arquivosRetorno = ArquivoIntegracaoHelper.listarArquivosRetorno(responsavel);
        List<Pair<File, String>> arquivosCritica = ArquivoIntegracaoHelper.listarArquivosCritica(responsavel);

        boolean atalhoUpload = responsavel.isSup() && responsavel.temPermissao(CodedValues.FUN_UPL_ARQUIVOS);

        model.addAttribute("atalhoUpload", atalhoUpload);
        model.addAttribute("responsavel", responsavel);
        model.addAttribute("tipo", tipo);
        model.addAttribute("estCodigo", estCodigo);
        model.addAttribute("orgCodigo", orgCodigo);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("absolutePath", absolutePath);
        model.addAttribute("pathRetorno", pathRetorno);
        model.addAttribute("pathCritica", pathCritica);
        model.addAttribute("diretorioRetorno", diretorioRetorno);
        model.addAttribute("diretorioCritica", diretorioCritica);
        model.addAttribute("arquivosRetorno", arquivosRetorno);
        model.addAttribute("arquivosCritica", arquivosCritica);

        return viewRedirect("jsp/cadastrarRetornoIntegracao/listarRetornoIntegracao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=processar" })
    public String processar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            SynchronizerToken.saveToken(request);

            String nomeArquivo = JspHelper.verificaVarQryStr(request, "arquivo");
            String tipo = JspHelper.verificaVarQryStr(request, "tipo");

            String orgCodigo = null;
            String estCodigo = null;

            // Verifica se existe algum processo rodando para o usuário
            String chave = "PROCESSO_FOLHA(MARGEM/RETORNO)";
            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

            if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                estCodigo = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCseSup() && !nomeArquivo.equals("")) {
                // cse/nome_arquivo.txt
                // cse/org_codigo/nome_arquivo.txt ou
                // est/est_codigo/nome_arquivo.txt ou
                String[] partesNomeArq = nomeArquivo.split(File.separator);
                if (partesNomeArq.length == 3) {
                    if (partesNomeArq[0].equals("cse")) {
                        orgCodigo = partesNomeArq[1];
                    } else if (partesNomeArq[0].equals("est")) {
                        estCodigo = partesNomeArq[1];
                    }
                    nomeArquivo = partesNomeArq[1] + File.separator + partesNomeArq[2];
                } else {
                    nomeArquivo = partesNomeArq[1];
                }
            }

            // Faz a importação do retorno de integração
            if (!temProcessoRodando) {
                if (!nomeArquivo.equals("") && !tipo.equals("")) {
                    String nomeArquivoOriginal = nomeArquivo.replaceAll("\\.crypt", "");
                    ProcessaRetorno processo = new ProcessaRetorno(nomeArquivo, orgCodigo, estCodigo, tipo, null, responsavel);
                    processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.retorno.nome", responsavel, nomeArquivoOriginal));
                    processo.start();
                    ControladorProcessos.getInstance().incluir(chave, processo);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.retorno.nome.processando", responsavel, nomeArquivoOriginal));
                    temProcessoRodando = true;
                }
            }

            model.addAttribute("responsavel", responsavel);
            model.addAttribute("tipo", tipo);
            model.addAttribute("estCodigo", estCodigo);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("temProcessoRodando", temProcessoRodando);

            boolean atalhoUpload = responsavel.isSup() && responsavel.temPermissao(CodedValues.FUN_UPL_ARQUIVOS);
            model.addAttribute("atalhoUpload", atalhoUpload);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return viewRedirect("jsp/cadastrarRetornoIntegracao/listarRetornoIntegracao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=concluir" })
    public String concluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            SynchronizerToken.saveToken(request);

            String nomeArquivo = JspHelper.verificaVarQryStr(request, "arquivo");
            String tipo = JspHelper.verificaVarQryStr(request, "tipo");

            String orgCodigo = null;
            String estCodigo = null;

            // Verifica se existe algum processo rodando para o usuário
            String chave = "PROCESSO_FOLHA(MARGEM/RETORNO)";
            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

            if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                estCodigo = responsavel.getCodigoEntidadePai();
            } else if (responsavel.isOrg()) {
                orgCodigo = responsavel.getCodigoEntidade();
            } else if (responsavel.isCseSup() && !nomeArquivo.equals("")) {
                // cse/nome_arquivo.txt
                // cse/org_codigo/nome_arquivo.txt ou
                // est/est_codigo/nome_arquivo.txt ou
                String[] partesNomeArq = nomeArquivo.split(File.separator);
                if (partesNomeArq.length == 3) {
                    if (partesNomeArq[0].equals("cse")) {
                        orgCodigo = partesNomeArq[1];
                    } else if (partesNomeArq[0].equals("est")) {
                        estCodigo = partesNomeArq[1];
                    }
                    nomeArquivo = partesNomeArq[1] + File.separator + partesNomeArq[2];
                } else {
                    nomeArquivo = partesNomeArq[1];
                }
            }

            // Faz a importação do retorno de integração
            if (!temProcessoRodando) {
                if (tipo.equals("CONCLUIR")) {
                    ProcessaConclusaoRetorno processo = new ProcessaConclusaoRetorno(orgCodigo, estCodigo, responsavel);
                    processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.folha.conclusao.retorno", responsavel));
                    processo.start();
                    ControladorProcessos.getInstance().incluir(chave, processo);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.conclusao.retorno.processada", responsavel));
                    temProcessoRodando = true;
                }
            }

            model.addAttribute("responsavel", responsavel);
            model.addAttribute("tipo", tipo);
            model.addAttribute("estCodigo", estCodigo);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("temProcessoRodando", temProcessoRodando);

            boolean atalhoUpload = responsavel.isSup() && responsavel.temPermissao(CodedValues.FUN_UPL_ARQUIVOS);
            model.addAttribute("atalhoUpload", atalhoUpload);

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        return viewRedirect("jsp/cadastrarRetornoIntegracao/listarRetornoIntegracao", request, session, model, responsavel);
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
        String fluxo = "uploadListarRetornoIntegracao";

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
