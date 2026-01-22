package com.zetra.econsig.web.controller.folha;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.helper.Pair;
import com.zetra.econsig.helper.folha.ArquivoIntegracaoHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.job.process.ProcessaRetorno;
import com.zetra.econsig.service.folha.ImpRetornoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.controller.AbstractWebController;

/**
 * <p>Title: ListarRetornoAtrasadoWebController</p>
 * <p>Description: Controlador Web para o caso de uso Listar Arquivos de Retorno Atrasao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 28924 $
 * $Date: 2020-02-26 11:54:24 -0300 (qua, 26 fev 2020) $
 */
@Controller
@RequestMapping(value = {"/v3/listarArquivosRetornoAtrasado"})
public class ListarRetornoAtrasadoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarRetornoAtrasadoWebController.class);

    @Autowired
    private ImpRetornoController impRetornoController;

    @RequestMapping(params = {"acao=iniciar"})
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        SynchronizerToken.saveToken(request);

        // Verifica se existe algum processo rodando para o usuário
        String chave = "PROCESSO_FOLHA(RETORNO_ATRASADO)";
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

        String orgCodigo = null;
        String estCodigo = null;

        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            estCodigo = responsavel.getCodigoEntidadePai();
        } else if (responsavel.isOrg()) {
            orgCodigo = responsavel.getCodigoEntidade();
        }

        boolean atalhoUpload = responsavel.isSup() && responsavel.temPermissao(CodedValues.FUN_UPL_ARQUIVOS);

        boolean quinzenal = !PeriodoHelper.folhaMensal(responsavel);

        String periodo = JspHelper.verificaVarQryStr(request, "periodo");
        java.sql.Date periodoRetAtrasado = null;
        if (!TextHelper.isNull(periodo)) {
            try {
                periodoRetAtrasado = DateHelper.parsePeriodString(periodo);
            } catch (ParseException e) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(quinzenal ? "mensagem.folha.formato.periodo.quinzenal" : "mensagem.folha.formato.periodo.mensal", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
        }

        // Path dos arquivos de integração
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        // Path dos arquivos de retorno
        String pathRetorno = absolutePath + File.separatorChar + "retornoatrasado" + File.separatorChar;
        // Se é usuário de órgão, concatena o código do órgão nos paths
        if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            pathRetorno += "est" + File.separatorChar + responsavel.getCodigoEntidadePai() + File.separatorChar;
        } else if (responsavel.isOrg()) {
            pathRetorno += "cse" + File.separatorChar + responsavel.getCodigoEntidade() + File.separatorChar;
        } else if (!responsavel.isCseSup()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Faz as checagens de diretório
        File diretorioRetorno = new File(pathRetorno);
        if ((!diretorioRetorno.exists() && !diretorioRetorno.mkdirs())) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.lst.arq.generico.criacao.diretorio", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        // Lista os arquivos
        List<Pair<File, String>> arquivosRetorno = ArquivoIntegracaoHelper.listarArquivosRetornoAtrasado(responsavel);

        String ultPeriodoImpRet = "";

        try {
            ultPeriodoImpRet = DateHelper.toPeriodString(impRetornoController.getUltimoPeriodoRetorno(orgCodigo, estCodigo, responsavel));
        } catch (ImpRetornoControllerException ex) {
            try {
                ultPeriodoImpRet = DateHelper.toPeriodString(DateHelper.getSystemDatetime());
            } catch (Exception e) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.folha.erro.recuperacao.ultimo.periodo.importacao", responsavel), ex);
            }
        }

        if (responsavel.isCseSup()) {
            carregarListaEstabelecimento(request, session, model, responsavel);
            carregarListaOrgao(request, session, model, responsavel);
        }

        model.addAttribute("ultPeriodoImpRet", ultPeriodoImpRet);
        model.addAttribute("periodoRetAtrasado", periodoRetAtrasado);
        model.addAttribute("pathRetorno", pathRetorno);
        model.addAttribute("arquivosRetorno", arquivosRetorno);
        model.addAttribute("quinzenal", quinzenal);
        model.addAttribute("temProcessoRodando", temProcessoRodando);
        model.addAttribute("atalhoUpload", atalhoUpload);

        return viewRedirect("jsp/listarArquivosRetornoAtrasado/listarArqsRetornoIntegracaoAtrasado", request, session, model, responsavel);
    }

    @RequestMapping(params = {"acao=importarRetornoAtrasado"})
    public String importarRetornoAtrasado(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        String nomeArquivo = JspHelper.verificaVarQryStr(request, "arquivo");

        // Valida o token de sessão se um arquivo foi selecionado (trata a primeira chamada a partir do menu)
        if (!TextHelper.isNull(nomeArquivo) && !SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);

        // Verifica se existe algum processo rodando para o usuário
        String chave = "PROCESSO_FOLHA(RETORNO_ATRASADO)";
        boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

        if (!temProcessoRodando) {
            String orgCodigo = null;
            String estCodigo = null;

            if (responsavel.isCseSup() && !TextHelper.isNull(nomeArquivo)) {
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

            if(responsavel.isOrg() && TextHelper.isNull(orgCodigo)) {
                orgCodigo = responsavel.getCodigoEntidade();
            }

            boolean retAtrasadoSomaParcela = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "retAtrasadoSomaParcela")) && JspHelper.verificaVarQryStr(request, "retAtrasadoSomaParcela").equals("true");
            boolean quinzenal = !PeriodoHelper.folhaMensal(responsavel);

            // DESENV-9749: opção disponível para o retorno atrasado somar o valor realizado vindo da folha ao realizado na parcela histórica, ao invés de substituir o valor
            String tipo = (!retAtrasadoSomaParcela) ? "atrasado" : "atrasado_soma_parcela";

            String periodo = JspHelper.verificaVarQryStr(request, "periodo");
            java.sql.Date periodoRetAtrasado = null;
            if (!TextHelper.isNull(periodo)) {
                try {
                    periodoRetAtrasado = DateHelper.parsePeriodString(periodo);
                } catch (Exception ex) {
                    LOG.error(ex.getMessage());
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(quinzenal ? "mensagem.folha.formato.periodo.quinzenal" : "mensagem.folha.formato.periodo.mensal", responsavel));
                    return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                }
            }

            // Faz a importação do retorno de integração
            if (!TextHelper.isNull(nomeArquivo) && !tipo.equals("")) {
                boolean alterouEntidade = (request.getParameter("ENTIDADEALTERADA") == null ? "" : request.getParameter("ENTIDADEALTERADA")).equalsIgnoreCase("S");
                if (alterouEntidade) {
                    orgCodigo = (request.getParameter("ORG_CODIGO") == null ? orgCodigo : request.getParameter("ORG_CODIGO"));
                    estCodigo = (request.getParameter("EST_CODIGO") == null ? orgCodigo : request.getParameter("EST_CODIGO"));
                }

                String nomeArquivoOriginal = nomeArquivo.replaceAll("\\.crypt", "");
                ProcessaRetorno processo = new ProcessaRetorno(nomeArquivo, orgCodigo, estCodigo, tipo, periodoRetAtrasado, responsavel);
                processo.setDescricao(ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.retorno.nome", responsavel, nomeArquivoOriginal));
                processo.start();
                ControladorProcessos.getInstance().incluir(chave, processo);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.folha.arquivo.retorno.nome.processando", responsavel, nomeArquivoOriginal));
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
        String fluxo = "uploadListarRetornoAtrasado";

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
