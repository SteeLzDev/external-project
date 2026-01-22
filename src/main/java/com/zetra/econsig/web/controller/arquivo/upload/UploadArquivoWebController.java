package com.zetra.econsig.web.controller.arquivo.upload;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.dto.web.ArquivoDownload;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.UploadControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.solicitacaosuporte.jira.JiraUtil;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.job.process.ControladorProcessos;
import com.zetra.econsig.persistence.entity.TipoArquivo;
import com.zetra.econsig.service.arquivo.HistoricoArquivoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.folha.ValidaImportacaoController;
import com.zetra.econsig.service.upload.UploadController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;
import com.zetra.econsig.web.servlet.AudioCaptchaServlet;
import com.zetra.econsig.web.servlet.ImageCaptchaServlet;
import com.zetra.econsig.webclient.util.RestTemplateFactory;
import com.zetra.jira.exception.JiraException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: UploadArquivosWebController</p>
 * <p>Description: Controlador Web para o caso de uso Upload Arquivos.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = {RequestMethod.POST}, value = {"/v3/uploadArquivo"})
public class UploadArquivoWebController extends AbstractWebController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(UploadArquivoWebController.class);

    public static final String KEY_ARQUIVO_SALVO_NAME = "arquivoSalvo_name";
    public static final String KEY_ARQUIVO_SALVO_PATH = "arquivoSalvo_absolutePath";

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private HistoricoArquivoController historicoArquivoController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private ConsignatariaController consignatariaController;

    @Autowired
    private UploadController uploadController;

    @Autowired
    private ValidaImportacaoController validaImportacaoController;

    @Autowired
    private UsuarioController usuarioController;

    @RequestMapping(params = {"acao=iniciar"})
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        final List<TransferObject> consignatarias = null;
        final List<TransferObject> correspondentes = new ArrayList<>();

        final boolean selecionaEstOrgUploadMargemRetorno = false;
        final boolean selecionaEstOrgUploadContracheque = false;
        final String tipo = "";
        final String papCodigo = "";
        final String orgCodigo = "";
        final String estCodigo = "";
        final String csaCodigo = "";

        final String pathCombo = null;
        final String pathDownload = null;

        final List<String> fileNameAbrev = new ArrayList<>();
        final List<String> codigosOrgao = new ArrayList<>();
        final HashMap<Object, CustomTransferObject> orgaos = new HashMap<>();
        final boolean exibirArquivo = false;

        final boolean temProcessoRodando = false;
        final String msgResultadoComando = "";

        boolean temPermissaoEst = false;

        if (responsavel.isOrg()) {
            temPermissaoEst = responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO);
        }

        final boolean exibeCaptcha = false;
        final boolean exibeCaptchaAvancado = false;
        final boolean exibeCaptchaDeficiente = false;
        final boolean exibeCampoUpload = false;

        //Verificação se pode ou não enviar comentário
        final boolean comentario = false;

        final List<TransferObject> lstEstabelecimentos = new ArrayList<>();
        final List<TransferObject> lstOrgaos = new ArrayList<>();

        final String action = "";

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

        return viewRedirect("jsp/uploadArquivo/uploadArquivo", request, session, model, responsavel);
    }

    protected String carregar(String tipo, boolean exibirArquivo, boolean comentario, boolean selecionaEstOrgUploadMargemRetorno, boolean selecionaEstOrgUploadContracheque, TipoArquivoEnum tipoArquivo, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final UploadHelper uploadHelper = new UploadHelper();
            final boolean exibeCampoUpload = true;

            final ParamSession paramSession = ParamSession.getParamSession(session);

            // Quando a página é chamada no início de caso de uso a página apenas cria o token, depois ela passa a verificar
            if (!SynchronizerToken.isTokenValid(request)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }
            SynchronizerToken.saveToken(request);

            final String chave = "Upload" + "|" + responsavel.getUsuCodigo();
            final boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

            List<TransferObject> consignatarias = null;
            List<TransferObject> correspondentes = new ArrayList<>();

            String papCodigo = "";
            String orgCodigo = "";
            String estCodigo = "";
            String csaCodigo = "";
            String fluxo = "";

            String diretorioArquivos = null;
            List<ArquivoDownload> arquivosPaginaAtual = null;

            //Path dos arquivos de integração
            final String absolutePath = ParamSist.getDiretorioRaizArquivos();
            final List<String> fileNameAbrev = new ArrayList<>();
            final List<String> codigosOrgao = new ArrayList<>();
            final Map<String, TransferObject> orgaos = new HashMap<>();

            boolean temPermissaoEst = false;

            if (responsavel.isOrg()) {
                temPermissaoEst = responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO);
            }

            //Parametros de captcha
            boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, responsavel);
            boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, responsavel);
            boolean exibeCaptchaDeficiente = false;
            final UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

            if ((usuarioResp != null) && (usuarioResp.getUsuDeficienteVisual() != null) && "S".equals(usuarioResp.getUsuDeficienteVisual())) {
                exibeCaptcha = false;
                exibeCaptchaAvancado = false;
                exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, responsavel);
            } else if (!exibeCaptcha && !exibeCaptchaAvancado) {
                //Caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples
                exibeCaptcha = true;
            }

            if (!temProcessoRodando) {
                final ParamSist ps = ParamSist.getInstance();

                // Tamanho máximo do arquivo
                int maxSize = 0;
                if (responsavel.isCseSupOrg()) {
                    maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel).toString()) : 30;
                } else if (responsavel.isCsaCor()) {
                    maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR, responsavel).toString()) : 1;
                }
                maxSize = maxSize * 1024 * 1024;

                try {
                    uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);
                } catch (final Throwable ex) {
                    LOG.error(ex.getMessage(), ex);
                    final String msg = ex.getMessage();
                    if (!TextHelper.isNull(msg)) {
                        session.setAttribute(CodedValues.MSG_ERRO, msg);
                    }
                }

                papCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "PAP_CODIGO");
                orgCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "ORG_CODIGO");
                estCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "EST_CODIGO");
                csaCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "CSA_CODIGO");
                 fluxo = JspHelper.verificaVarQryStr(request, uploadHelper, "FLUXO");

                if((fluxo == null) || fluxo.isEmpty()){
                    fluxo = model.getAttribute("FLUXO") == null ? null : (String) model.getAttribute("FLUXO");
                }
                if (exibirArquivo) {

                    final String pathDiretorioArquivos = buscarPathDiretorioArquivos(request);

                    String pathCombo = null;
                    String pathDownload = null;

                    if (pathDiretorioArquivos == null) {

                        pathCombo = absolutePath + File.separatorChar + tipo + File.separatorChar;
                        pathDownload = pathCombo;

                        if (!selecionaEstOrgUploadMargemRetorno && !selecionaEstOrgUploadContracheque) {
                            // Se é usuário de órgão, concatena o código do órgão nos paths
                            if (responsavel.isOrg() && temPermissaoEst) {
                                pathCombo += "est" + File.separatorChar + responsavel.getCodigoEntidadePai();
                            } else if (responsavel.isOrg()) {
                                pathCombo += "cse" + File.separatorChar + responsavel.getCodigoEntidade();
                            } else if (responsavel.isCseSup()) {
                                pathCombo += "cse";
                            } else {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel));
                                request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                                return "jsp/redirecionador/redirecionar";
                            }
                        } else if (!TextHelper.isNull(papCodigo) && AcessoSistema.ENTIDADE_ORG.equals(papCodigo)) {
                            if (responsavel.isOrg()) {
                                orgCodigo = responsavel.getCodigoEntidade();
                            }
                            pathCombo += "cse" + File.separatorChar + (TextHelper.isNull(orgCodigo) ? "" : orgCodigo);
                        } else if (!TextHelper.isNull(papCodigo) && AcessoSistema.ENTIDADE_EST.equals(papCodigo)) {
                            if (responsavel.isOrg()) {
                                estCodigo = responsavel.getEstCodigo();
                            }
                            pathCombo += "est" + File.separatorChar + (TextHelper.isNull(estCodigo) ? "" : estCodigo);
                        } else {
                            pathCombo += "cse";
                        }

                        pathCombo += File.separatorChar;

                    } else {
                        pathCombo = absolutePath + File.separatorChar + pathDiretorioArquivos;
                        pathDownload = pathCombo;
                    }

                    final List<String> listNomeArquivosXmlMargRetMov = getListaNomeArquivosXmlMargRetMov(responsavel);

                    // Cria filtro para seleção de arquivos .txt, .zip, .xls, .xlsx e .csv
                    final FileFilter filtro = arq -> {
                        final String arqNome = arq.getName().toLowerCase();
                        if ((tipoArquivo != null) && (tipoArquivo.getCodigo() != null) && tipoArquivo.getCodigo().equals(TipoArquivoEnum.ARQUIVO_XML_MARGEM_RETORNO_MOVIMENTO.getCodigo())) {
                            return listNomeArquivosXmlMargRetMov.stream().anyMatch(arquivo -> arquivo.equals(arqNome));
                        } else {
                            return (arqNome.endsWith(".txt") ||
                                    arqNome.endsWith(".zip") ||
                                    arqNome.endsWith(".xls") ||
                                    arqNome.endsWith(".xlsx") ||
                                    arqNome.endsWith(".csv") ||
                                    arqNome.endsWith(".txt.crypt") ||
                                    arqNome.endsWith(".zip.crypt"));
                        }
                    };

                    // Faz as checagens de diretório
                    final File diretorioRetorno = new File(pathCombo);
                    if ((!diretorioRetorno.exists() && !diretorioRetorno.mkdirs())) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.upload.criacao.diretorio", responsavel));
                        request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                        return "jsp/redirecionador/redirecionar";
                    }

                    // Alteração para ficar compatível com o FileAbstractWebController, onde os arquivos de integração com a folha
                    // para os papéis CSE e SUP devem conter a pasta no nome do arquivo
                    if (responsavel.isCseSup() && (
                            "margem".equals(tipo) ||
                                    "margemcomplementar".equals(tipo) ||
                                    "transferidos".equals(tipo) ||
                                    "retorno".equals(tipo) ||
                                    "retornoatrasado".equals(tipo) ||
                                    "critica".equals(tipo) ||
                                    "contracheque".equals(tipo) ||
                                    "historico".equals(tipo))) {
                        diretorioArquivos = pathDownload;
                    } else {
                        diretorioArquivos = pathCombo;
                    }

                    // Lista os arquivos
                    final List<File> arquivosCombo = new ArrayList<>();
                    File[] temp = null;
                    if ((!TextHelper.isNull(papCodigo) && (!AcessoSistema.ENTIDADE_ORG.equals(papCodigo) || !TextHelper.isNull(orgCodigo)) && (!AcessoSistema.ENTIDADE_EST.equals(papCodigo) || !TextHelper.isNull(estCodigo))) || ((TextHelper.isNull(papCodigo) && (responsavel.isCseSupOrg())) || ((TextHelper.isNull(papCodigo) && (responsavel.isCsaCor() && TipoArquivoEnum.ARQUIVO_SALDO_DEVEDOR.equals(tipoArquivo)))))) {
                        temp = diretorioRetorno.listFiles(filtro);
                    }

                    if (temp != null) {
                        arquivosCombo.addAll(Arrays.asList(temp));

                        // Pega o identificador dos órgão, e os arquivos dos subdiretórios
                        if (responsavel.isCseSup()) {
                            final String[] nome_subdir = diretorioRetorno.list();
                            if (nome_subdir != null) {
                                for (final String element : nome_subdir) {
                                    final File arq = new File(pathCombo + element);
                                    if (arq.isDirectory()) {
                                        arquivosCombo.addAll(Arrays.asList(arq.listFiles(filtro)));
                                        codigosOrgao.add(element);
                                    }
                                }
                            }

                            if (!codigosOrgao.isEmpty()) {
                                List<TransferObject> orgaosTO = null;
                                try {
                                    TransferObject criterio = new CustomTransferObject();
                                    criterio.setAttribute(Columns.ORG_CODIGO, codigosOrgao);

                                    orgaosTO = consignanteController.lstOrgaos(criterio, responsavel);
                                    final Iterator<TransferObject> it1 = orgaosTO.iterator();
                                    while (it1.hasNext()) {
                                        criterio = it1.next();
                                        orgaos.put((String) criterio.getAttribute(Columns.ORG_CODIGO), criterio);
                                    }
                                } catch (final Exception ex) {
                                    LOG.error(ex.getMessage(), ex);
                                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                                    return "jsp/redirecionador/redirecionar";
                                }
                            }
                        }
                    }

                    // Ordena os arquivos baseado na data de modificação
                    Collections.sort(arquivosCombo, (f1, f2) -> {
                        final Long d1 = f1.lastModified();
                        final Long d2 = f2.lastModified();
                        return d2.compareTo(d1);
                    });

                    arquivosPaginaAtual = ArquivoDownload.carregarArquivos(arquivosCombo, diretorioArquivos, orgaos, responsavel);
                }

                try {
                    final CustomTransferObject criterio = null;
                    consignatarias = consignatariaController.lstConsignatarias(criterio, responsavel);
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                    return "jsp/redirecionador/redirecionar";
                }

                try {
                    if (!TextHelper.isNull(csaCodigo)) {
                        final CustomTransferObject criterio = new CustomTransferObject();
                        final List<Short> status = new ArrayList<>();
                        status.add(CodedValues.STS_ATIVO);
                        status.add(CodedValues.STS_INATIVO);
                        status.add(CodedValues.STS_INATIVO_CSE);
                        status.add(CodedValues.STS_BLOQUEADO_AUTOMATICAMENTE_SEGURANCA);
                        criterio.setAttribute(Columns.COR_ATIVO, status);
                        criterio.setAttribute(Columns.COR_CSA_CODIGO, csaCodigo);
                        correspondentes = consignatariaController.lstCorrespondentes(criterio, responsavel);
                    }

                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                    request.setAttribute("url64", TextHelper.encode64(SynchronizerToken.updateTokenInURL(paramSession.getLastHistory(), request)));
                    return "jsp/redirecionador/redirecionar";
                }
            }

            final StringBuilder msgResultadoComando = new StringBuilder();
            if (temProcessoRodando) {
                session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.aviso.upload.validacao.layout", responsavel));
            }

            List<TransferObject> lstEstabelecimentos;
            List<TransferObject> lstOrgaos;
            try {
                lstEstabelecimentos = consignanteController.lstEstabelecimentos(null, responsavel);
                lstOrgaos = consignanteController.lstOrgaos(null, responsavel);
            } catch (final ConsignanteControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            String action = "";
            if (!TextHelper.isNull(tipo)) {
                final String tipoCapitalize = tipo.substring(0, 1).toUpperCase() + tipo.substring(1);
                action = "../v3/uploadArquivo" + tipoCapitalize + "?acao=upload&" + SynchronizerToken.generateToken4URL(request);
            }

            //Retorna os registros dos arquivos enviados para o retorno integração.
            List<TransferObject> lstArquivoEnviados = null;

            final List<String> arquivoMargem = Arrays.asList(TipoArquivoEnum.ARQUIVO_CADASTRO_MARGENS.getCodigo());
            final List<String> arquivoMargemComplementar = Arrays.asList(TipoArquivoEnum.ARQUIVO_CADASTRO_MARGEM_COMPLEMENTAR.getCodigo());
            final List<String> arquivoTransferidos = Arrays.asList(TipoArquivoEnum.ARQUIVO_TRANSFERIDOS.getCodigo());
            final List<String> arquivoRetorno = Arrays.asList(TipoArquivoEnum.ARQUIVO_RETORNO_INTEGRACAO.getCodigo());
            final List<String> arquivoRetornoAtrasado = Arrays.asList(TipoArquivoEnum.ARQUIVO_RETORNO_ATRASADO.getCodigo());
            final List<String> arquivoCritica = Arrays.asList(TipoArquivoEnum.ARQUIVO_CRITICA.getCodigo());
            final List<String> arquivoRecuperacaoCredito = Arrays.asList(TipoArquivoEnum.ARQUIVO_RECUPERACAO_CREDITO.getCodigo());
            final List<String> arquivoSaldoDevedor = Arrays.asList(TipoArquivoEnum.ARQUIVO_SALDO_DEVEDOR.getCodigo());
            final List<String> arquivoUploadXML = Arrays.asList(TipoArquivoEnum.ARQUIVO_XML_MARGEM_RETORNO_MOVIMENTO.getCodigo());

            String tipoEntidade = (!TextHelper.isNull(orgCodigo)) ? AcessoSistema.ENTIDADE_ORG : (!TextHelper.isNull(estCodigo)) ? AcessoSistema.ENTIDADE_EST : AcessoSistema.ENTIDADE_CSE;
            try {
                if ("margem".equals(tipo)) {
                    lstArquivoEnviados = historicoArquivoController.lstHistoricoArquivoUpload(arquivoMargem, null, tipoEntidade, CodedValues.FUN_UPL_ARQUIVOS, responsavel);
                } else if ("margemcomplementar".equals(tipo)) {
                    lstArquivoEnviados = historicoArquivoController.lstHistoricoArquivoUpload(arquivoMargemComplementar, null, tipoEntidade, CodedValues.FUN_UPL_ARQUIVOS, responsavel);
                } else if ("transferidos".equals(tipo)) {
                    lstArquivoEnviados = historicoArquivoController.lstHistoricoArquivoUpload(arquivoTransferidos, null, tipoEntidade, CodedValues.FUN_UPL_ARQUIVOS, responsavel);
                } else if ("retorno".equals(tipo)) {
                    lstArquivoEnviados = historicoArquivoController.lstHistoricoArquivoUpload(arquivoRetorno, null, tipoEntidade, CodedValues.FUN_UPL_ARQUIVOS, responsavel);
                } else if ("retornoatrasado".equals(tipo)) {
                    lstArquivoEnviados = historicoArquivoController.lstHistoricoArquivoUpload(arquivoRetornoAtrasado, null, tipoEntidade, CodedValues.FUN_UPL_ARQUIVOS, responsavel);
                } else if ("critica".equals(tipo)) {
                    lstArquivoEnviados = historicoArquivoController.lstHistoricoArquivoUpload(arquivoCritica, null, tipoEntidade, CodedValues.FUN_UPL_ARQUIVOS, responsavel);
                } else if ("recuperacaoCredito".equals(tipo)) {
                    lstArquivoEnviados = historicoArquivoController.lstHistoricoArquivoUpload(arquivoRecuperacaoCredito, null, tipoEntidade, CodedValues.FUN_ENVIAR_ARQ_RECUPERACAO_CREDITO, responsavel);
                } else if ("saldodevedor".equals(tipo)) {
                    tipoEntidade = responsavel.isCsa() ? AcessoSistema.ENTIDADE_CSA : tipoEntidade;
                    lstArquivoEnviados = historicoArquivoController.lstHistoricoArquivoUpload(arquivoSaldoDevedor, null, tipoEntidade, CodedValues.FUN_ENVIAR_ARQ_SALDO_DEVEDOR_LOTE, responsavel);
                } else if ("xmlMargemRetornoMovimento".equals(tipo)) {
                    lstArquivoEnviados = historicoArquivoController.lstHistoricoArquivoUpload(arquivoUploadXML, null, tipoEntidade, CodedValues.FUN_UPLOAD_ARQUIVOS_XML, responsavel);
                }
            } catch (final HistoricoArquivoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
            }

            // Define o usuário responsável pelo envio do arquivo de acordo com o histórico
            if ((lstArquivoEnviados != null) && !lstArquivoEnviados.isEmpty()) {
                for (final TransferObject arquivoEnviado : lstArquivoEnviados) {
                    final String usuResponsavel = usuarioController.findUsuario(arquivoEnviado.getAttribute(Columns.HAR_USU_CODIGO).toString(), responsavel).getUsuNome();
                    arquivoEnviado.setAttribute(Columns.USU_NOME, usuResponsavel);
                }
            }

            try {
                if(!comentario && (tipoArquivo != null)) {
                        final TipoArquivo data =  uploadController.buscaTipoArquivoByPrimaryKey(tipoArquivo.getCodigo(), responsavel);
                        comentario = (data != null) && CodedValues.TPC_SIM.equals(data.getTarNotificacaoUpload());
                }
            } catch (final UploadControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
            }

            model.addAttribute("responsavel", responsavel);
            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("correspondentes", correspondentes);
            model.addAttribute("selecionaEstOrgUploadMargemRetorno", selecionaEstOrgUploadMargemRetorno);
            model.addAttribute("selecionaEstOrgUploadContracheque", selecionaEstOrgUploadContracheque);
            model.addAttribute("arquivosCombo", arquivosPaginaAtual);
            model.addAttribute("pathDownload", diretorioArquivos);
            model.addAttribute("fileNameAbrev", fileNameAbrev);
            model.addAttribute("codigosOrgao", codigosOrgao);
            model.addAttribute("temProcessoRodando", temProcessoRodando);
            model.addAttribute("msgResultadoComando", msgResultadoComando.toString());
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
            model.addAttribute("orgaos", orgaos);
            model.addAttribute("lstEstabelecimentos", lstEstabelecimentos);
            model.addAttribute("lstOrgaos", lstOrgaos);
            model.addAttribute("action", action);
            model.addAttribute("exibeCampoUpload", exibeCampoUpload);
            model.addAttribute("lstArquivoEnviados", lstArquivoEnviados);

             if ((fluxo == null) || fluxo.isEmpty()) {
                return viewRedirect("jsp/uploadArquivo/uploadArquivo", request, session, model, responsavel);
            } else {
                String redirect = null;
                if ("uploadListarRetornoAtrasado".equals(fluxo) || "uploadListarRetornoIntegracao".equals(fluxo) || "uploadListarMargem".equals(fluxo) || "uploadListarHistorico".equals(fluxo) || "uploadListarMargemComplementar".equals(fluxo) || "transferidos".equals(fluxo)) {
                    action = "";
                    if (!TextHelper.isNull(tipo)) {
                        final String tipoCapitalize = tipo.substring(0, 1).toUpperCase() + tipo.substring(1);
                        action = "../v3/uploadArquivo" + tipoCapitalize + "?acao=upload&" + SynchronizerToken.generateToken4URL(request);
                    }
                    model.addAttribute("fluxo", fluxo);
                    model.addAttribute("action", action);
                    redirect = viewRedirect("jsp/uploadArquivo/uploadAtalhoArquivo", request, session, model, responsavel);
                }
                return redirect;
            }
        } catch (UsuarioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("rotulo.upload.erro.interno", responsavel) + " " + ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    public void upload(@RequestParam("tipo") String tipo, @RequestParam("validarQtdeArquivo") boolean validarQtdeArquivo, @RequestParam("selecionaEstOrgUploadMargemRetorno") boolean selecionaEstOrgUploadMargemRetorno, @RequestParam("selecionaEstOrgUploadContracheque") boolean selecionaEstOrgUploadContracheque, HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ZetraException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            final ParamSist ps = ParamSist.getInstance();

            // Tamanho máximo do arquivo
            int maxSize = 0;
            if (responsavel.isCseSupOrg()) {
                maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSE_ORG, responsavel).toString()) : 30;
            } else if (responsavel.isCsaCor()) {
                maxSize = !TextHelper.isNull(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR, responsavel)) ? Integer.parseInt(ps.getParam(CodedValues.TPC_TAM_MAX_UPLOAD_ARQUIVOS_CSA_COR, responsavel).toString()) : 1;
            }
            maxSize = maxSize * 1024 * 1024;

            // Processa a requisição para obter os arquivos e parâmetros
            final UploadHelper uploadHelper = new UploadHelper();
            uploadHelper.processarRequisicao(request.getServletContext(), request, maxSize);

            if (TextHelper.isNull(tipo)) {
                throw new ZetraException("mensagem.erro.upload.copia.arquivo", responsavel);
            }

            final String chave = "Upload" + "|" + responsavel.getUsuCodigo();
            boolean temProcessoRodando = ControladorProcessos.getInstance().verificar(chave, session);

            File arquivoSalvo = null;

            final String papCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "PAP_CODIGO");
            String orgCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "ORG_CODIGO");
            final String estCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "EST_CODIGO");
            final String csaCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "CSA_CODIGO");
            final String corCodigo = JspHelper.verificaVarQryStr(request, uploadHelper, "COR_CODIGO");
            final String fluxo = JspHelper.verificaVarQryStr(request, uploadHelper, "FLUXO");

            // Parametros de captcha
            boolean exibeCaptcha = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_TELA_LOGIN, CodedValues.TPC_SIM, responsavel);
            boolean exibeCaptchaAvancado = exibeCaptcha ? false : ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_AVANCADO_TELA_LOGIN, CodedValues.TPC_SIM, responsavel);
            boolean exibeCaptchaDeficiente = false;
            final UsuarioTransferObject usuarioResp = usuarioController.findUsuario(responsavel.getUsuCodigo(), responsavel);

            if ((usuarioResp != null) && (usuarioResp.getUsuDeficienteVisual() != null) && "S".equals(usuarioResp.getUsuDeficienteVisual())) {
                exibeCaptcha = false;
                exibeCaptchaAvancado = false;
                exibeCaptchaDeficiente = ParamSist.paramEquals(CodedValues.TPC_EXIBE_CAPTCHA_DEFICIENTE_TELA_LOGIN, CodedValues.TPC_SIM, responsavel);
            } else if (!exibeCaptcha && !exibeCaptchaAvancado && !CodedValues.FUN_EDITAR_MENSAGEM.equals(responsavel.getFunCodigo())) {
                // Caso não tenha nenhum captcha habilitado mostra o padrão que é captcha simples,
                // se assim estiver definido pelo método abaixo.
                exibeCaptcha = getExibeCaptchaDefault();
            }

            boolean temPermissaoEst = false;

            if (responsavel.isOrg()) {
                temPermissaoEst = responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO);
            }

            // Monta link de retorno
            final Map<String, String[]> parametros = new HashMap<>();
            parametros.put("tipo", new String[]{tipo});
            parametros.put("PAP_CODIGO", new String[]{papCodigo});

            if (uploadHelper.hasArquivosCarregados()) {

                //Validação captcha
                if ((usuarioResp.getUsuDeficienteVisual() == null) || "N".equals(usuarioResp.getUsuDeficienteVisual())) {
                    if (exibeCaptcha) {
                         if (ImageCaptchaServlet.armazenaCaptcha(session.getId(), (String) session.getAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY))
                                    && !ImageCaptchaServlet.validaCaptcha(session.getId(), JspHelper.verificaVarQryStr(request, uploadHelper, "captcha"))) {
                            throw new ZetraException("mensagem.erro.captcha.invalido", responsavel);
                        }
                        session.removeAttribute(ImageCaptchaServlet.IMAGE_CAPTCHA_SESSION_KEY);
                    } else if (exibeCaptchaAvancado) {
                        final String remoteAddr = request.getRemoteAddr();

                        if (!isValidCaptcha(uploadHelper.getValorCampoFormulario("g-recaptcha-response"), remoteAddr, responsavel)) {
                            throw new ZetraException("mensagem.erro.captcha.invalido", responsavel);
                        }
                    }
                } else if (exibeCaptchaDeficiente) {
                    final String captchaAnswer = JspHelper.verificaVarQryStr(request, uploadHelper, "captcha");
                    final String captchaCode = (String) session.getAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                    if ((captchaCode == null) || !captchaCode.equalsIgnoreCase(captchaAnswer)) {
                        throw new ZetraException("mensagem.erro.captcha.invalido", responsavel);
                    }
                    session.removeAttribute(AudioCaptchaServlet.AUDIO_CAPTCHA_SESSION_KEY);
                }

                final List<String> listPath = recuperarPath(tipo, selecionaEstOrgUploadMargemRetorno, selecionaEstOrgUploadContracheque, model, papCodigo, orgCodigo, estCodigo, csaCodigo, corCodigo, temPermissaoEst, responsavel);
                if ((listPath == null) || listPath.isEmpty()) {
                    throw new ZetraException("mensagem.erro.upload.copia.arquivo", responsavel);
                }

                for (final String path : listPath) {
                    final String fileName = uploadHelper.getFileName(0);

                    String tipoEntidade = responsavel.getTipoEntidade();
                    String codigoEntidade = responsavel.getCodigoEntidade();
                    if ((selecionaEstOrgUploadMargemRetorno || selecionaEstOrgUploadContracheque) && AcessoSistema.ENTIDADE_ORG.equals(papCodigo)) {
                        tipoEntidade = AcessoSistema.ENTIDADE_ORG;
                        codigoEntidade = JspHelper.verificaVarQryStr(request, uploadHelper, "ORG_CODIGO");
                        if (TextHelper.isNull(codigoEntidade)) {
                            throw new ZetraException("mensagem.usoIncorretoSistema", responsavel);
                        }
                    }

                    String[] extensaoList = null;
                    // recupera parâmetros de validação de arquivos apenas para os papeis consignante, suporte e órgão
                    if (AcessoSistema.ENTIDADE_CSE.equalsIgnoreCase(tipoEntidade) || AcessoSistema.ENTIDADE_SUP.equalsIgnoreCase(tipoEntidade) || AcessoSistema.ENTIDADE_ORG.equalsIgnoreCase(tipoEntidade)) {

                        final Map<String, String> paramValidacaoArq = validaImportacaoController.lstParamValidacaoArq(tipoEntidade, codigoEntidade, null, null, responsavel);
                        if ("retorno".equals(tipo) || "retornoatrasado".equals(tipo) || "margem".equals(tipo) || "margemcomplementar".equals(tipo) || "contracheque".equals(tipo)) {
                            final String padraoNomeArquivo = (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "padraoNomeArquivoBusca"))) ? paramValidacaoArq.get(tipo + "." + "padraoNomeArquivoBusca") : null;
                            final String padraoNomeArquivoExemplo = (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "padraoNomeArquivoBuscaExemplo"))) ? paramValidacaoArq.get(tipo + "." + "padraoNomeArquivoBuscaExemplo") : null;
                            extensaoList = (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "extensoes"))) ? paramValidacaoArq.get(tipo + "." + "extensoes").split(",") : new String[]{"TXT", "ZIP"};

                            String padrao = "";
                            if (!TextHelper.isNull(padraoNomeArquivo)) {
                                padrao = padraoNomeArquivo;
                            } else {
                                // Constrói padrão baseado nas extensões permitidas
                                padrao = ".*\\.(" + TextHelper.join(extensaoList, "|") + ")";
                            }
                            // Faz o casamento de padrão ignorando maiúsculas/minúsculas
                            final Pattern p = Pattern.compile(padrao, Pattern.CASE_INSENSITIVE);
                            final Matcher m = p.matcher(fileName);
                            if (!m.matches()) {
                                final StringBuilder mensagem = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.erro.upload.nome.arquivo", responsavel, fileName));
                                if (!TextHelper.isNull(padraoNomeArquivoExemplo)) {
                                    mensagem.append(" ").append(ApplicationResourcesHelper.getMessage("rotulo.upload.exemplo.singular.plural", responsavel)).append(" ").append(padraoNomeArquivoExemplo);
                                }

                                // Grava log da tentativa de upload com nome incorreto
                                final LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.UPLOAD_FILE, Log.LOG_ERRO);
                                log.add("ARQUIVO: \"" + fileName + "\"");
                                log.add("USER-AGENT: " + request.getHeader("user-agent"));
                                log.write();

                                throw ZetraException.byMessage(mensagem.toString());
                            }
                        } else if (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "extensoes"))) {
                            // Para os outros tipos de arquivos, o padrão não é .zip e .txt
                            extensaoList = paramValidacaoArq.get(tipo + "." + "extensoes").split(",");
                        } else if("xmlMargemRetornoMovimento".equals(tipo)) {
                            extensaoList = new String[]{"XML"};

                            final List<String> listNomeArquivosXmlMargRetMov = getListaNomeArquivosXmlMargRetMov(responsavel);
                            if (listNomeArquivosXmlMargRetMov.stream().noneMatch(arquivo -> arquivo.equals(fileName))) {
                                throw new ZetraException("mensagem.erro.upload.xmlMargemRetornoMovimento", responsavel, fileName);
                            }
                        }

                    }

                    if("creditoTrabalhador".equals(tipo)){
                        extensaoList = (extensaoList == null) || (extensaoList.length == 0) ? UploadHelper.EXTENSOES_PERMITIDAS_CREDITO_TRABALHADOR : extensaoList;
                    }

                    if ((extensaoList == null) || (extensaoList.length == 0)) {
                        extensaoList = new String[]{"TXT", "ZIP"};
                    }

                    if ("recuperacaoCredito".equals(tipo)) {
                        extensaoList = UploadHelper.EXTENSOES_PERMITIDAS_UPLOAD_RECUPERACAO_CREDITO;
                    }

                    if ("cadastroDependentes".equals(tipo) || "cadastroConsignatarias".equals(tipo)) {
                        extensaoList = UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_TXT;
                    }

                    if ("relatorioCustomizado".equals(tipo)) {
                        extensaoList = UploadHelper.EXTENSOES_PERMITIDAS_UPLOAD_RELATORIO_CUSTOMIZADO;
                    }

                    arquivoSalvo = uploadHelper.salvarArquivo(path, extensaoList, null, session);

                    if(ParamSist.getBoolParamSist(CodedValues.TPC_CONVERTE_AUTOMATICAMENTE_LAYOUT_RET_ORGAO_PARA_GERAL, responsavel) && ("retorno".equals(tipo) || "retornoatrasado".equals(tipo)) && !TextHelper.isNull(orgCodigo)) {

                        //Precisamos converter o arquivo para .txt se sua extensão for uma destas ".doc", ".docx", ".xls", ".xlsx", ".pdf", ".csv"
                        File arquivoConversao = null;
                        String extensaoArquivo  = "";
                        for (final String extensao : UploadHelper.EXTENSOES_PERMITIDAS_PARA_CONVERSAO_TXT_RETORNO_ORGAO) {
                            if(arquivoSalvo.getName().toLowerCase().endsWith(extensao)) {
                                extensaoArquivo = extensao;
                                break;
                            }
                        }

                        if(!TextHelper.isNull(extensaoArquivo) && !".csv".equals(extensaoArquivo)) {
                            FileHelper.rename(arquivoSalvo.getAbsolutePath(), arquivoSalvo.getAbsolutePath() + ".ORIG_CONVERT");
                            arquivoConversao = new File(arquivoSalvo.getAbsolutePath() + ".ORIG_CONVERT");
                            final String arquivoConvertido = converterDocumentosTxt(arquivoConversao, extensaoArquivo, responsavel);

                            if (TextHelper.isNull(arquivoConvertido)) {
                                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.arquivo.conversao.para.txt", responsavel));
                                throw new ZetraException("mensagem.erro.arquivo.conversao.para.txt", responsavel);
                            }

                            final byte[] arq = Base64.decodeBase64(arquivoConvertido);
                            FileUtils.writeByteArrayToFile(new File(arquivoSalvo.getAbsolutePath().replace(extensaoArquivo, ".txt")), arq);
                            arquivoSalvo = new File(arquivoSalvo.getAbsolutePath().replace(extensaoArquivo, ".txt"));
                        } else if (!TextHelper.isNull(extensaoArquivo) && ".csv".equals(extensaoArquivo)) {
                            FileHelper.rename(arquivoSalvo.getAbsolutePath(), arquivoSalvo.getAbsolutePath().replace(extensaoArquivo, ".txt"));
                            arquivoSalvo = new File(arquivoSalvo.getAbsolutePath().replace(extensaoArquivo, ".txt"));
                        }

                        final String mensagemErro = uploadHelper.convertArquivoRetornoOrgaoLayoutCse(arquivoSalvo, orgCodigo, responsavel);
                        if(!TextHelper.isNull(mensagemErro)) {
                            if((arquivoConversao !=null) && arquivoConversao.exists()) {
                                FileHelper.delete(arquivoConversao.getAbsolutePath());
                            }
                            throw ZetraException.byMessage(mensagemErro);
                        }
                    }

                    // Se o arquivo não for de nenhuma da extensões permitidas, altera a extensão do mesmo para txt
                    boolean rename = true;
                    String arquivoSalvoNameLC = arquivoSalvo.getName().toLowerCase();
                    for (final String element : extensaoList) {
                        if (arquivoSalvoNameLC.endsWith(element.toLowerCase())) {
                            rename = false;
                            break;
                        }
                    }

                    if (rename) {
                        FileHelper.rename(arquivoSalvo.getAbsolutePath(), arquivoSalvo.getAbsolutePath() + ".txt");
                        // recupera o arquivo renomeado
                        arquivoSalvo = new File(arquivoSalvo.getAbsolutePath() + ".txt");
                        arquivoSalvoNameLC = arquivoSalvo.getName().toLowerCase();
                    }

                    // Se o arquivo for txt, converte as quebras de linhas para o padrão do servidor em que a aplicação está executando.
                    if (arquivoSalvoNameLC.endsWith(".txt") || arquivoSalvoNameLC.endsWith(".csv")) {
                        FileHelper.convertLineBreaks(arquivoSalvo.getAbsolutePath());
                    }

                    final boolean converteCharset = TextHelper.isNull(ps.getParam(CodedValues.TPC_CONVERTE_CHARSET_ARQUIVO_UPLOAD, responsavel)) || !"N".equalsIgnoreCase(ps.getParam(CodedValues.TPC_CONVERTE_CHARSET_ARQUIVO_UPLOAD, responsavel).toString());
                    if (converteCharset && (arquivoSalvoNameLC.endsWith(".txt") || arquivoSalvoNameLC.endsWith(".csv") || arquivoSalvoNameLC.endsWith(".zip"))) {
                        FileHelper.convertCharset(arquivoSalvo);
                    }

                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.upload.sucesso", responsavel));
                    model.addAttribute(KEY_ARQUIVO_SALVO_NAME, arquivoSalvo.getName());
                    model.addAttribute(KEY_ARQUIVO_SALVO_PATH, arquivoSalvo.getAbsolutePath());

                    final String obs = JspHelper.verificaVarQryStr(request, uploadHelper, "obs");

                    java.util.Date pexPeriodo = null;
                    try {
                        List<String> orgCodigos = null;
                        orgCodigo = (responsavel.isOrg()) ? responsavel.getCodigoEntidade() : orgCodigo;
                        if (!TextHelper.isNull(orgCodigo)) {
                            orgCodigos = new ArrayList<>();
                            orgCodigos.add(orgCodigo);
                        }
                        List<String> estCodigos = null;
                        if (!TextHelper.isNull(estCodigo)) {
                            estCodigos = new ArrayList<>();
                            estCodigos.add(estCodigo);
                        }
                        final TransferObject to = periodoController.obtemPeriodoExportacaoDistinto(orgCodigos, estCodigos, responsavel);
                        pexPeriodo = DateHelper.parse(to.getAttribute(Columns.PEX_PERIODO).toString(), "yyyy-MM-dd");
                    } catch (final Exception e) {
                        LOG.error("Não foi possível localizar o período atual de exportação.", e);
                    }

                    final String tipoEntidadeValidArq = tipoEntidade;
                    tipoEntidade = (!TextHelper.isNull(orgCodigo)) ? AcessoSistema.ENTIDADE_ORG : (!TextHelper.isNull(estCodigo)) ? AcessoSistema.ENTIDADE_EST : null;
                    String codEntidade = (!TextHelper.isNull(orgCodigo)) ? orgCodigo : (!TextHelper.isNull(estCodigo)) ? estCodigo : null;
                    String funCodigo = CodedValues.FUN_UPL_ARQUIVOS;

                    TipoArquivoEnum tipoArquivo = null;
                    if ("margem".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_CADASTRO_MARGENS;
                    } else if ("margemcomplementar".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_CADASTRO_MARGEM_COMPLEMENTAR;
                    } else if ("transferidos".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_TRANSFERIDOS;
                    } else if ("retorno".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_RETORNO_INTEGRACAO;
                    } else if ("retornoatrasado".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_RETORNO_ATRASADO;
                    } else if ("critica".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_CRITICA;
                    } else if ("contracheque".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_CONTRACHEQUES;
                    } else if ("historico".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_HISTORICO;
                    } else if ("bloqueio_ser".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_BLOQUEIO_SERVIDOR;
                    } else if ("falecido".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_FALECIDO;
                    } else if ("desligado".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_DESLIGADO_BLOQUEADO;
                    } else if ("lote".equals(tipo) || "lotemultiplo".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_LOTE;
                    } else if ("conciliacao".equals(tipo) || "conciliacaomultiplo".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_CONCILIACAO;
                    } else if ("consignatarias".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_CONSIGNATARIAS;
                    } else if ("convenios".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_CONVENIO;
                    } else if ("adequacao".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_ADEQUACAO_A_MARGEM;
                    } else if ("inconsistencia".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_REGRA_INCONSISTENCIA;
                    } else if ("dirf".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_DIRF_SERVIDOR;
                    } else if ("recuperacaoCredito".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_RECUPERACAO_CREDITO;
                    } else if ("saldodevedor".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_SALDO_DEVEDOR;
                        funCodigo = CodedValues.FUN_ENVIAR_ARQ_SALDO_DEVEDOR_LOTE;
                        tipoEntidade = !TextHelper.isNull(csaCodigo) ? AcessoSistema.ENTIDADE_CSA : tipoEntidade;
                        codEntidade = !TextHelper.isNull(csaCodigo) ? csaCodigo : codEntidade;
                    } else if ("cadastroDependentes".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_CADASTRO_DEPENDENTE;
                    } else if ("mensagem".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_MENSAGEM;
                    } else if ("relatorioCustomizado".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_RELATORIO_CUSTOMIZADO;
                    } else if ("loterescisao".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_LOTE_RESCISAO;
                    } else if ("consignataria".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_CONSIGNATARIA;
                    } else if ("xmlMargemRetornoMovimento".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_XML_MARGEM_RETORNO_MOVIMENTO;
                        funCodigo = CodedValues.FUN_UPLOAD_ARQUIVOS_XML;
                    }  else if ("cadastroConsignatarias".equals(tipo)) {
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_LOTE_CADASTRO_CONSIGNATARIA;
                    }else if ("creditoTrabalhador".equals(tipo)){
                        tipoArquivo = TipoArquivoEnum.ARQUIVO_INTEGRACAO_CREDITO_TRABALHADOR;
                    }

                    if (tipoArquivo != null) {
                        // Cria o histórico de upload de arquivo
                        historicoArquivoController.createHistoricoArquivo(tipoEntidade, codEntidade, tipoArquivo, arquivoSalvo.getAbsolutePath(), null, null, pexPeriodo, "1", funCodigo, responsavel);

                        // Envia e-mail informando o recebimento do arquivo
                        enviarEmailRecebimentoArquivo(tipo, tipoArquivo, arquivoSalvo.getName(), orgCodigo, obs, responsavel);
                    }

                    if (validarQtdeArquivo) {
                        final String mensagem = validarQtdeArquivo(tipo, orgCodigo, estCodigo, path, fileName, tipoEntidadeValidArq, codigoEntidade, obs, session, responsavel);

                        if (!TextHelper.isNull(mensagem)) {
                            session.setAttribute(CodedValues.MSG_INFO, mensagem);
                        }
                    }
                }
            } else {
                throw new ZetraException("rotulo.upload.arquivo.invalido", responsavel);
            }

            String action = "";
            if (!TextHelper.isNull(tipo)) {
                final String tipoCapitalize = tipo.substring(0, 1).toUpperCase() + tipo.substring(1);
                action = "../v3/uploadArquivo" + tipoCapitalize + "?acao=upload&" + SynchronizerToken.generateToken4URL(request);
            }

            model.addAttribute("csaCodigo", csaCodigo);
            model.addAttribute("selecionaEstOrgUploadMargemRetorno", selecionaEstOrgUploadMargemRetorno);
            model.addAttribute("selecionaEstOrgUploadContracheque", selecionaEstOrgUploadContracheque);
            model.addAttribute("temProcessoRodando", temProcessoRodando);
            model.addAttribute("tipo", tipo);
            model.addAttribute("papCodigo", papCodigo);
            model.addAttribute("estCodigo", estCodigo);
            model.addAttribute("orgCodigo", orgCodigo);
            model.addAttribute("action", action);
            model.addAttribute("FLUXO", fluxo);

        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            throw ex;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException("mensagem.erroInternoSistema", responsavel);
        }
    }

    private void enviarEmailRecebimentoArquivo(String tipo, TipoArquivoEnum tipoArquivo, String nomeArquivo, String orgCodigo, String obs, AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_AMBIENTE_DE_TESTES, CodedValues.TPC_SIM, responsavel)) {
                // Se for ambiente de testes, não envia e-mail de recebimento de arquivos
                return;
            }

            // Busca o tipo de arquivo, e verifica se este envia notificação no upload
            final TipoArquivo tar = uploadController.buscaTipoArquivoByPrimaryKey(tipoArquivo.getCodigo(), responsavel);

            if (!"S".equalsIgnoreCase(tar.getTarNotificacaoUpload())) {
                // Se o tipo de arquivo não envia notificação, então finaliza o método
                return;
            }

            // Verifica tabela de destinatarios de e-mail para determinar se é enviado ou não o e-mail
            final List<String> papeisDestinatarios = uploadController.listarPapeisEnvioEmailUpload(responsavel);
            if ((papeisDestinatarios == null) || papeisDestinatarios.isEmpty()) {
                // Se não há configuração para destinatários de recebimento de e-mail de notificação de upload, então finaliza o método
                return;
            }

            final boolean enviaEmailCSE = papeisDestinatarios.contains(CodedValues.PAP_CONSIGNANTE);
            final boolean enviaEmailORG = papeisDestinatarios.contains(CodedValues.PAP_ORGAO);
            final boolean enviaEmailCSA = papeisDestinatarios.contains(CodedValues.PAP_CONSIGNATARIA);
            final boolean enviaEmailCOR = papeisDestinatarios.contains(CodedValues.PAP_CORRESPONDENTE);

            // Recupera tipo de email que será concatenado no título do email enviado
            String tipoEmail = tipo;
            if ("margem".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.margens.servidores", responsavel).toString().toLowerCase();
            } else if ("margemcomplementar".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.margem.complementar", responsavel).toString().toLowerCase();
            } else if ("retorno".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.retorno.integracao", responsavel).toString().toLowerCase();
            } else if ("retornoatrasado".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.retorno.atrasado", responsavel).toString().toLowerCase();
            } else if ("bloqueio_ser".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.bloqueio.servidor", responsavel).toString().toLowerCase();
            } else if ("critica".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.critica.integracao", responsavel).toString().toLowerCase();
            } else if ("transferidos".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.transferidos", responsavel).toString().toLowerCase();
            } else if ("falecido".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.falecido", responsavel).toString().toLowerCase();
            } else if ("desligado".equals(tipo)) {
                tipoEmail = ApplicationResourcesHelper.getMessage("rotulo.upload.arquivo.selecione.desligado.bloqueado", responsavel).toString().toLowerCase();
            } else {
                // Por compatibilidade, só usa a descrição do tipo de arquivo para os novos tipos
                tipoEmail = tar.getTarDescricao();
            }

            if (enviaEmailCSE || enviaEmailORG) {
                // Envia notificação de recebimento para papel de CSE/ORG
                EnviaEmailHelper.enviarEmailRecebimentoArquivo(tipoEmail, nomeArquivo, enviaEmailCSE, enviaEmailORG, orgCodigo, obs, responsavel);
            }

            // Envia notificação de recebimento para papel de CSA/COR somente se o parâmetro 545 estiver habilitado
            if ((enviaEmailCSA || enviaEmailCOR) && ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_EMAIL_UPLOAD_ARQ_CSE_PARA_CSA, responsavel)) {
                EnviaEmailHelper.enviarEmailUploadArquivoCsa(tipoEmail, nomeArquivo, enviaEmailCSA, enviaEmailCOR, obs, responsavel);
            }
        } catch (final UploadControllerException ex) {
            throw new ViewHelperException(ex);
        }
    }

    protected String validarQtdeArquivo(String tipo, String orgCodigo, String estCodigo, String path, String fileName, String tipoEntidade, String codigoEntidade, String obs, HttpSession session, AcessoSistema responsavel) throws IOException, ParseException, ZetraException, JiraException {
        String mensagem = null;

        //Path dos arquivos de integração
        final boolean integraJira = ParamSist.paramEquals(CodedValues.TPC_INTEGRA_JIRA, CodedValues.TPC_SIM, responsavel);
        final String absolutePath = ParamSist.getDiretorioRaizArquivos();
        final JiraUtil jiraUtil = new JiraUtil();

        final Map<String, String> paramValidacaoArq = validaImportacaoController.lstParamValidacaoArq(tipoEntidade, codigoEntidade, null, null, responsavel);

        // verifica quantidade de arquivos requeridos para o processamento em questão e, baseado nisto, quantos faltam para fazer upload
        int qtdArqNecessarios = 0;
        if ((paramValidacaoArq != null) && !paramValidacaoArq.isEmpty() && paramValidacaoArq.containsKey(tipo + "." + "qtdMinimaArquivos") && !TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "qtdMinimaArquivos"))) {
            qtdArqNecessarios = Integer.parseInt(paramValidacaoArq.get(tipo + "." + "qtdMinimaArquivos"));
        } else {
            qtdArqNecessarios = 0;
        }

        if (qtdArqNecessarios > 1) {
            final int diasIdadeMaximaArquivo = (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "diasIdadeMaximaArquivo"))) ? Integer.parseInt(paramValidacaoArq.get(tipo + "." + "diasIdadeMaximaArquivo")) : 0;
            final String padraoNomeArquivo = (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "padraoNomeArquivoBusca"))) ? paramValidacaoArq.get(tipo + "." + "padraoNomeArquivoBusca") : null;
            final String[] extensoes = (!TextHelper.isNull(paramValidacaoArq.get(tipo + "." + "extensoes"))) ? paramValidacaoArq.get(tipo + "." + "extensoes").split(",") : new String[]{"TXT", "ZIP"};

            final FileFilter filtroExtArq = pathname -> {
                // Verifica a idade máxima do arquivo
                if ((diasIdadeMaximaArquivo == 0) || (DateHelper.dayDiff(new Date(pathname.lastModified())) <= diasIdadeMaximaArquivo)) {
                    // Se tem a idade máxima correta, verifica o padrão de nome ou as extensões requeridas
                    String padrao = "";
                    if (!TextHelper.isNull(padraoNomeArquivo)) {
                        padrao = padraoNomeArquivo;
                    } else {
                        // Constrói padrão baseado nas extensões permitidas
                        padrao = ".*\\.(" + TextHelper.join(extensoes, "|") + ")";
                    }
                    // Faz o casamento de padrão ignorando maiúsculas/minúsculas
                    final Pattern p = Pattern.compile(padrao, Pattern.CASE_INSENSITIVE);
                    final Matcher m = p.matcher(pathname.getName());
                    return m.matches();
                }
                return false;
            };

            final List<String> nameList = FileHelper.getFilesInDir(absolutePath + File.separatorChar + path, filtroExtArq);

            final int totalCopiado = ((nameList != null) && !nameList.isEmpty()) ? nameList.size() : 1;

            final int restantes = qtdArqNecessarios - totalCopiado;

            if (restantes > 0) {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.upload.sucesso.parcial.requeridos.disponiveis", responsavel, Integer.toString(totalCopiado), Integer.toString(qtdArqNecessarios));
                if (integraJira) {
                    jiraUtil.atualizaStatusProducao(tipo, responsavel, "comentar", null, fileName, path, obs, orgCodigo, estCodigo);
                }
            } else {
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.upload.sucesso.todos.requeridos.disponiveis", responsavel);
                if (integraJira) {
                    jiraUtil.atualizaStatusProducao(tipo, responsavel, "aguardarValidacaoArquivos", null, fileName, path, obs, orgCodigo, estCodigo);
                }
            }
        } else {
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.upload.sucesso", responsavel);
            if (integraJira) {
                jiraUtil.atualizaStatusProducao(tipo, responsavel, "aguardarValidacaoArquivos", null, fileName, path, obs, orgCodigo, estCodigo);
            }
        }

        return mensagem;
    }

    protected String buscarPathDiretorioArquivos(HttpServletRequest request) {
        return null;
    }

    protected List<String> recuperarPath(String tipo, boolean selecionaEstOrgUploadMargemRetorno, boolean selecionaEstOrgUploadContracheque, Model model, String papCodigo, String orgCodigo, String estCodigo, String csaCodigo, String corCodigo, boolean temPermissaoEst, AcessoSistema responsavel) throws ZetraException {
        throw new ZetraException("mensagem.erro.metodo.nao.implementado", responsavel);
    }

    protected boolean getExibeCaptchaDefault() {
        return true;
    }

    private String converterDocumentosTxt(File file, String extensao, AcessoSistema responsavel) throws IOException, InterruptedException {
        final String urlBase = (String) ParamSist.getInstance().getParam(CodedValues.TPC_URL_CONVERSOR_AUDIO_MP3_DOCUMENT_PDF, responsavel);

        if (TextHelper.isNull(urlBase)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.conversor", responsavel));
            return null;
        }

        final String arquivoBase64 = Base64.encodeBase64String(Files.readAllBytes(file.toPath()));

        final HashMap<String, String> camposJson = new HashMap<>();
        camposJson.put("file", arquivoBase64);
        camposJson.put("type", extensao);
        camposJson.put("typeTo", "txt");

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        final JSONObject jsonObject = new JSONObject(camposJson);
        final HttpEntity<String> httpEntity = new HttpEntity<>(jsonObject.toString(), headers);

        final RestTemplate restTemplateSimple = RestTemplateFactory.getRestTemplate(responsavel);

        final ResponseEntity<String> response = restTemplateSimple.postForEntity(urlBase + "/api/converter/v1/base64", httpEntity, String.class);

        if ((response.getStatusCode() != HttpStatus.OK) || (response.getBody() == null)) {
            return null;
        }

        return (response.getStatusCode() != HttpStatus.OK) || (response.getBody() == null) ? null : (String) response.getBody();
    }

    private List<String> getListaNomeArquivosXmlMargRetMov(AcessoSistema responsavel) {
        return Collections.unmodifiableList(
            new ArrayList<>() {{
                add((String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_MARGEM, responsavel));
                add((String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_SAIDA_IMP_MARGEM, responsavel));
                add((String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_MARGEM, responsavel));
                add((String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_EXP_MOV_FIN, responsavel));
                add((String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_SAIDA_EXP_MOV_FIN, responsavel));
                add((String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_EXP_MOV_FIN, responsavel));
                add((String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_RETORNO, responsavel));
                add((String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_RETORNO, responsavel));
            }});
    }
}
