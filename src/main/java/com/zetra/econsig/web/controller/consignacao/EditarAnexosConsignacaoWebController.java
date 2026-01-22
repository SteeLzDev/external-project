package com.zetra.econsig.web.controller.consignacao;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_EDITAR_ANEXO_ADE_ALTERADO_SUCESSO;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN;
import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.web.AcaoConsignacao;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.UploadControllerException;
import com.zetra.econsig.exception.ValidarDocumentoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoFuncaoServico;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.texto.TransferObjectHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.helper.web.ParamSession;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.Calendario;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.TipoArquivo;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.upload.UploadController;
import com.zetra.econsig.service.validardocumento.ValidarDocumentoController;
import com.zetra.econsig.values.AgendamentoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: EditarAnexosConsignacaoWebController</p>
 * <p>Description: Controlador Web para o caso de uso EditarAnexosConsignacao.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/editarAnexosConsignacao" })
public class EditarAnexosConsignacaoWebController extends AbstractConsultarConsignacaoWebController {
    private static final String AAD_DESCRICAO = "AAD_DESCRICAO";

	private static final String ADE_CODIGO_MAIUSCULO = "ADE_CODIGO";

	private static final String AUTDES = "autdes";

	private static final String ADE_CODIGO = "adeCodigo";

	private static final String OCA_PERIODO = "ocaPeriodo";

	private static final String NOME_ARQ = "NOME_ARQ";

	private static final String AAD_NOMES = "AAD_NOMES";

	private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AbstractWebController.class);

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private AgendamentoController agendamentoController;

    @Autowired
    private CalendarioController calendarioController;

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private ValidarDocumentoController validarDocumentoController;

    @Autowired
    private UploadController uploadController;

    protected String getAcaoFormulario() {
        return "../v3/editarAnexosConsignacao";
    }

    protected String getTipoArquivo() {
        return "anexo_consignacao";
    }

    @Override
    protected void configurarPagina(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        model.addAttribute("tituloPagina", ApplicationResourcesHelper.getMessage("rotulo.editar.anexo.consignacao.titulo", responsavel));
        model.addAttribute("acaoFormulario", getAcaoFormulario());
        model.addAttribute("tipoArquivo", getTipoArquivo());
        model.addAttribute("imageHeader", "i-operacional");
    }

    @Override
    protected List<String> definirSadCodigoPesquisa(HttpServletRequest request, HttpSession session, AcessoSistema responsavel) {
        return new ArrayList<>();
    }

    @Override
    protected List<AcaoConsignacao> definirAcoesListaConsignacao(HttpServletRequest request, AcessoSistema responsavel) {
        final List<AcaoConsignacao> acoes = new ArrayList<>();

        String link = null;
        if(!ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
            link = getAcaoFormulario() + "?acao=exibir&tipo=anexar_documento";
        } else {
            link = "../v3/editarAnexosConsignacao?acao=exibir&validarDocumentos=true";
        }
        String descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.anexar", responsavel);
        String descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.anexar", responsavel);
        String msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.editar.anexo.consignacao.clique.aqui", responsavel);
        String msgConfirmacao = "";
        final String msgAdicionalConfirmacao = "";

        acoes.add(new AcaoConsignacao("EDITAR_ANEXO_CONSIGNACAO", CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO, descricao, descricaoCompleta, "attach.png", "btnEditarAnexoConsignacao", msgAlternativa, msgConfirmacao, msgAdicionalConfirmacao, link, null, null));

        // Adiciona o editar consignação
        link = getAcaoFormulario() + "?acao=detalharConsignacao";
        descricao = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.abreviado", responsavel);
        descricaoCompleta = ApplicationResourcesHelper.getMessage("rotulo.acoes.editar", responsavel);
        msgAlternativa = ApplicationResourcesHelper.getMessage("mensagem.consultar.consignacao.clique.aqui", responsavel);
        msgConfirmacao = "";

        acoes.add(new AcaoConsignacao("DETALHAR", CodedValues.FUN_CONS_CONSIGNACAO, descricao, descricaoCompleta, "editar.gif", "btnConsultarConsignacao", msgAlternativa, msgConfirmacao, null, link, null, null));

        return acoes;
    }

    @Override
    protected TransferObject recuperarCriteriosPesquisaPadrao(HttpServletRequest request, AcessoSistema responsavel) {
        final TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("TIPO_OPERACAO", "anexar_documento");
        return criterio;
    }

    @RequestMapping(params = { "acao=exibir" })
    public String exibir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String erro = validarTokenFuncaoAutorizacao(request, session, model, responsavel);
        if (erro != null) {
            return erro;
        }
        montarPaginacaoListaAnexos(request, model, responsavel);

        if(TextHelper.isNull(JspHelper.verificaVarQryStr(request, "validarDocumentos"))) {
            return viewRedirect("jsp/editarAnexoConsignacao/editarAnexosConsignacao", request, session, model, responsavel);
        } else {
            final String filtroTable = JspHelper.verificaVarQryStr(request, "filtroTable");
            if(!TextHelper.isNull(filtroTable)) {
                final ParamSession paramSession = ParamSession.getParamSession(session);
                model.addAttribute("filtroTable", filtroTable);
                model.addAttribute("voltar",paramSession.getLastHistory() + "&filtroTable=" + filtroTable);
            }
            return viewRedirect("jsp/editarAnexoConsignacao/editarAnexoConsignacaoValidarDocumento", request, session, model, responsavel);
        }

    }

    @RequestMapping(params = { "acao=upload" })
    public String upload(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws IOException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String erro = validarTokenFuncaoAutorizacao(request, session, model, responsavel);
        if (erro != null) {
            return erro;
        }
        final String adeCodigo = (String) model.asMap().get(ADE_CODIGO);
        final CustomTransferObject autdes = (CustomTransferObject) model.asMap().get(AUTDES);
        String paramTamMaxArqAnexo = null;
        int tamMaxArqAnexo;
        if (responsavel.isSer()) {
            paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO_ASSINATURA_DIGITAL, responsavel);
            tamMaxArqAnexo = !TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 200;
        } else {
            paramTamMaxArqAnexo = (String) ParamSist.getInstance().getParam(CodedValues.TPC_TAM_MAX_ARQ_ANEXO_CONTRATO, responsavel);
            tamMaxArqAnexo = !TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 200;
        }
        final UploadHelper uploadHelper = new UploadHelper();
        try {
            uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            final String msg = ex.getMessage();
            if (!TextHelper.isNull(msg)) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            }
        }
        if (uploadHelper.getValorCampoFormulario("FORM") != null) {
            final String path = "anexo" + File.separatorChar + DateHelper.format((Date) autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + adeCodigo;
            File anexo = null;
            String msgErro = null;
            //Valido a extensão dos documentos anexados para o credito eletrônico.
            boolean isOrigemLeilao = false;
            try {
                isOrigemLeilao = leilaoSolicitacaoController.temSolicitacaoLeilao(adeCodigo, true, responsavel);
            } catch (final LeilaoSolicitacaoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                msgErro = ex.getMessage();
                session.setAttribute(CodedValues.MSG_ERRO, msgErro);
            }
            final boolean exigeAssinaturaDigital = ParamSist.paramEquals(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

            // DESENV-10005: Troca o nome do arquivo anexo para padronização
            String novoNomeAnexo = null;
            if (uploadHelper.hasArquivosCarregados()) {
                final String aadNome = uploadHelper.getFileName(0);
                final File arquivoAnexo = new File(ParamSist.getDiretorioRaizArquivos() + File.separatorChar + path + File.separatorChar + aadNome);
                if (arquivoAnexo != null && arquivoAnexo.exists()) {
                    try {
                        // Verifica se existe o registro no banco relacionando o anexo à consignação, e caso não exista, deixa sobrepor
                        final AnexoAutorizacaoDesconto aad = editarAnexoConsignacaoController.findAnexoAutorizacaoDesconto(adeCodigo, aadNome, responsavel);
                        if (aad != null) {
                            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.anexo.ja.existe", responsavel));
                            montarPaginacaoListaAnexos(request, model, responsavel);
                            return viewRedirect("jsp/editarAnexoConsignacao/editarAnexosConsignacao", request, session, model, responsavel);
                        } else {
                            // O arquivo existe porém não está associado à consignação. Remove o anexo do disco
                            Optional.ofNullable(arquivoAnexo).ifPresent(anx -> {
    							try {
    								Files.delete(anx.toPath());
    							} catch (final IOException e) {
    								LOG.error(ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE, responsavel), e);
    							}
    						});
                        }
                    } catch (final AutorizacaoControllerException ex) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN, responsavel));
                        return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
                    }
                }

                novoNomeAnexo = DateHelper.format(DateHelper.getSystemDatetime(), "yyMMddHHmmssS") + new SecureRandom().nextInt(10);
            }

            // verifica anexo obrigatorio
            final String nomeAnexo = uploadHelper.getValorCampoFormulario("FILE1");
            final String idAnexo = session.getId();
            String aadDescricao = uploadHelper.getValorCampoFormulario(AAD_DESCRICAO);

            // Define os valores padrões, caso não sejam informados
            TipoArquivoEnum tipoArquivo = null;
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, CodedValues.TPC_SIM, responsavel) && responsavel.isSer() && CodedValues.FUN_EDITAR_ANEXO_SOLICITACAO.equals(responsavel.getFunCodigo())) {
                tipoArquivo = TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CREDITO_ELETRONICO;
            }

            try {
                if (responsavel.isSer() && exigeAssinaturaDigital && !isOrigemLeilao) {
                    anexo = uploadHelper.salvarArquivo(path, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CREDITO_ELETRONICO, novoNomeAnexo);
                } else {
                    anexo = uploadHelper.salvarArquivo(path, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO, novoNomeAnexo);
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                msgErro = ex.getMessage();
                session.setAttribute(CodedValues.MSG_ERRO, msgErro);
            }
            if (anexo != null && anexo.exists()) {
                try {
                    aadDescricao = uploadHelper.getValorCampoFormulario(AAD_DESCRICAO);
                    aadDescricao = !TextHelper.isNull(aadDescricao) && aadDescricao.length() <= 255 ? aadDescricao : anexo.getName();

                    java.sql.Date aadPeriodo = null;
                    final Date periodoContrato = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
                    final java.sql.Date periodoContratoSql = DateHelper.toSQLDate(periodoContrato);
                    if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel) || ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {
                        final String aadPeriodoStr = uploadHelper.getValorCampoFormulario(OCA_PERIODO);
                        if (!TextHelper.isNull(aadPeriodoStr)) {
                            aadPeriodo = DateHelper.toSQLDate(DateHelper.parse(aadPeriodoStr, "yyyy-MM-dd"));
                        }
                    }

                    if (aadPeriodo == null) {
                        aadPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(autdes.getAttribute(Columns.ORG_CODIGO).toString(), responsavel);
                    }

                    if (periodoContratoSql.compareTo(aadPeriodo) > 0) {
                        aadPeriodo = periodoContratoSql;
                    }

                    editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), aadDescricao, aadPeriodo, tipoArquivo, responsavel);
                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.editar.anexo.consignacao.incluido.sucesso", responsavel));
                } catch (final Exception ex) {
                	Files.delete(anexo.toPath());
                    msgErro = ex.getMessage();
                    session.setAttribute(CodedValues.MSG_ERRO, msgErro);
                }
            } else if (!TextHelper.isNull(nomeAnexo) && !TextHelper.isNull(adeCodigo)) {
                try {
                    String[] anexosName;
                    anexosName = nomeAnexo.split(";");
                    for (final String nomeAnexoCorrente : anexosName) {
                        anexo = UploadHelper.moverArquivoAnexoTemporario(nomeAnexoCorrente, adeCodigo, idAnexo, responsavel);
                        if (anexo != null && anexo.exists()) {
                            aadDescricao = !TextHelper.isNull(aadDescricao) && aadDescricao.length() <= 255 ? aadDescricao : anexo.getName();

                            java.sql.Date aadPeriodo = null;
                            final Date periodoContrato = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
                            final java.sql.Date periodoContratoSql = DateHelper.toSQLDate(periodoContrato);
                            if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel) || ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {
                                final String aadPeriodoStr = uploadHelper.getValorCampoFormulario(OCA_PERIODO);
                                if (!TextHelper.isNull(aadPeriodoStr)) {
                                    aadPeriodo = DateHelper.toSQLDate(DateHelper.parse(aadPeriodoStr, "yyyy-MM-dd"));
                                }
                            }

                            if (aadPeriodo == null) {
                                aadPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(autdes.getAttribute(Columns.ORG_CODIGO).toString(), responsavel);
                            }

                            if (periodoContratoSql.compareTo(aadPeriodo) > 0) {
                                aadPeriodo = periodoContratoSql;
                            }

                            editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), aadDescricao, aadPeriodo, tipoArquivo, responsavel);
                            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.editar.anexo.consignacao.incluido.sucesso", responsavel));
                        }
                    }
                } catch (final Exception ex) {
                    Optional.ofNullable(anexo).ifPresent(anx -> {
            			try {
            				Files.delete(anx.toPath());
            			} catch (final IOException e) {
            				LOG.error(ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE, responsavel), e);
            			}
            		});
                    msgErro = ex.getMessage();
                    session.setAttribute(CodedValues.MSG_ERRO, msgErro == null ? ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE, responsavel) : msgErro);
                }
            }

            exibeAlertaAnexosNecessarios(session, adeCodigo, autdes, responsavel);
        }
        montarPaginacaoListaAnexos(request, model, responsavel);
        return viewRedirect("jsp/editarAnexoConsignacao/editarAnexosConsignacao", request, session, model, responsavel);
    }

    @RequestMapping(params = { "acao=ativar" })
    public String ativar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model)  {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String erro = validarTokenFuncaoAutorizacao(request, session, model, responsavel);
        if (erro != null) {
            return erro;
        }
        final String adeCodigo = (String) model.asMap().get(ADE_CODIGO);
        final CustomTransferObject autdes = (CustomTransferObject) model.asMap().get(AUTDES);
        final String nomeArq = JspHelper.verificaVarQryStr(request, NOME_ARQ);
        if (!TextHelper.isNull(nomeArq)) {
            final CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.AAD_NOME, nomeArq);
            cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
            cto.setAttribute(Columns.ADE_DATA, DateHelper.format((Date) autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd"));
            try {
                cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);
                editarAnexoConsignacaoController.updateAnexoAutorizacaoDesconto(cto, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage(MENSAGEM_EDITAR_ANEXO_ADE_ALTERADO_SUCESSO, responsavel));
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }
        montarPaginacaoListaAnexos(request, model, responsavel);
        if(!ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
            return viewRedirect("jsp/editarAnexoConsignacao/editarAnexosConsignacao", request, session, model, responsavel);
        } else {
            return viewRedirect("jsp/editarAnexoConsignacao/editarAnexoConsignacaoValidarDocumento", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=bloquear" })
    public String bloquear(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String erro = validarTokenFuncaoAutorizacao(request, session, model, responsavel);
        if (erro != null) {
            return erro;
        }
        final String adeCodigo = (String) model.asMap().get(ADE_CODIGO);
        final CustomTransferObject autdes = (CustomTransferObject) model.asMap().get(AUTDES);
        final String nomeArq = JspHelper.verificaVarQryStr(request, NOME_ARQ);
        if (!TextHelper.isNull(nomeArq)) {
            final CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.AAD_NOME, nomeArq);
            cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
            cto.setAttribute(Columns.ADE_DATA, DateHelper.format((Date) autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd"));
            try {
                cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_INATIVO);
                editarAnexoConsignacaoController.updateAnexoAutorizacaoDesconto(cto, responsavel);
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage(MENSAGEM_EDITAR_ANEXO_ADE_ALTERADO_SUCESSO, responsavel));
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }
        montarPaginacaoListaAnexos(request, model, responsavel);
        if(!ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
            return viewRedirect("jsp/editarAnexoConsignacao/editarAnexosConsignacao", request, session, model, responsavel);
        } else {
            return viewRedirect("jsp/editarAnexoConsignacao/editarAnexoConsignacaoValidarDocumento", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=descrever" })
    public String descrever(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String erro = validarTokenFuncaoAutorizacao(request, session, model, responsavel);
        if (erro != null) {
            return erro;
        }
        final String adeCodigo = JspHelper.verificaVarQryStr(request, ADE_CODIGO_MAIUSCULO);
        final CustomTransferObject autdes = (CustomTransferObject) model.asMap().get(AUTDES);
        final String nomeArq = JspHelper.verificaVarQryStr(request, NOME_ARQ);
        if (!TextHelper.isNull(nomeArq)) {
            final CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.AAD_NOME, nomeArq);
            cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
            cto.setAttribute(Columns.ADE_DATA, DateHelper.format((Date) autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd"));
            if (JspHelper.verificaVarQryStr(request, "DESCRICAO") != null) {
                try {
                    final String descricao = JspHelper.verificaVarQryStr(request, "DESCRICAO");
                    if (!descricao.isBlank() && descricao.length() <= 255) {
                        cto.setAttribute(Columns.AAD_DESCRICAO, descricao);
                        editarAnexoConsignacaoController.updateAnexoAutorizacaoDesconto(cto, responsavel);
                        session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage(MENSAGEM_EDITAR_ANEXO_ADE_ALTERADO_SUCESSO, responsavel));
                    } else if (descricao.isBlank()) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.anexo.consignacao.descricao", responsavel));
                    } else {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.editar.anexo.consignacao.descricao.maxima", responsavel));
                    }
                } catch (final Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
                }
            }
        }
        montarPaginacaoListaAnexos(request, model, responsavel);
        if(!ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
            return viewRedirect("jsp/editarAnexoConsignacao/editarAnexosConsignacao", request, session, model, responsavel);
        } else {
            return viewRedirect("jsp/editarAnexoConsignacao/editarAnexoConsignacaoValidarDocumento", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=excluirAnexoTemp" })
    public ResponseEntity<String> excluirAnexoTemp(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String adeCodigo = JspHelper.verificaVarQryStr(request, ADE_CODIGO_MAIUSCULO);
        final String nomeArq = JspHelper.verificaVarQryStr(request, NOME_ARQ);
        if (!TextHelper.isNull(nomeArq)) {
            final CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.AAD_NOME, nomeArq);
            cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
            cto.setAttribute("hashDir", session.getId());
            try {
                editarAnexoConsignacaoController.removeAnexoAutorizacaoDescontoTemp(cto, responsavel);
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            }
        }
        final JsonObjectBuilder result = Json.createObjectBuilder();
        result.add("nomeArquivo", nomeArq);
        return new ResponseEntity<>(result.build().toString(), HttpStatus.OK);
    }

    @RequestMapping(params = { "acao=excluir" })
    public String excluir(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String erro = validarTokenFuncaoAutorizacao(request, session, model, responsavel);
        String[] anexos;
        String nomeAnexos;
        int i = 0;
        int erros = 0;
        if (erro != null) {
            return erro;
        }
        final String adeCodigo = (String) model.asMap().get(ADE_CODIGO);
        final CustomTransferObject autdes = (CustomTransferObject) model.asMap().get(AUTDES);
        final String nomeArq = JspHelper.verificaVarQryStr(request, NOME_ARQ);

        if(JspHelper.verificaVarQryStr(request, AAD_NOMES) != null && JspHelper.verificaVarQryStr(request, AAD_NOMES) != "") {
        	nomeAnexos = JspHelper.verificaVarQryStr(request, AAD_NOMES);
        	nomeAnexos = StringUtils.chop(nomeAnexos);
            anexos = nomeAnexos.split(",");
        } else {
        	nomeAnexos = nomeArq;
        	anexos = nomeAnexos.split("         ");
        }
        for (i = 0; i < anexos.length; i++) {
	        if (!TextHelper.isNull(anexos[i])) {
	            final CustomTransferObject cto = new CustomTransferObject();
	            cto.setAttribute(Columns.AAD_NOME, anexos[i]);
	            cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
	            cto.setAttribute(Columns.ADE_DATA, DateHelper.format((Date) autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd"));
	            try {
	                editarAnexoConsignacaoController.removeAnexoAutorizacaoDesconto(cto, responsavel);
	            } catch(final AutorizacaoControllerException ex) {
	                if (ex.getMessageKey() != null && "mensagem.usuarioNaoTemPermissao".equals(ex.getMessageKey())) {
	                    erros++;
	                } else {
	                    session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
	                }
	            } catch (final Exception ex) {
	                LOG.error(ex.getMessage(), ex);
	                session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
	            }
	        }
         }
        try {
            final SolicitacaoAutorizacao solicitacaoAutorizacao = validarDocumentoController.listUltSolicitacaoValidacao(adeCodigo, responsavel);
            if (solicitacaoAutorizacao != null && StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo().equals(solicitacaoAutorizacao.getSsoCodigo())) {
                validarDocumentoController.submeterContratoAguardandoDocumentacao(solicitacaoAutorizacao.getSoaCodigo(), adeCodigo, solicitacaoAutorizacao.getSoaPeriodo(), null, responsavel);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
        }

        if (erros == 0) {
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage(i > 1 ? "mensagem.editar.anexos.consignacao.excluidos.sucesso" : "mensagem.editar.anexo.consignacao.excluido.sucesso", responsavel));
        } else if (i == erros) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.usuarioNaoTemPermissao", responsavel));
        } else {
            session.setAttribute(CodedValues.MSG_ALERT, ApplicationResourcesHelper.getMessage("mensagem.editar.anexos.consignacao.falta.permissao", responsavel));
        }

        montarPaginacaoListaAnexos(request, model, responsavel);
        if(!ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel)) {
            return viewRedirect("jsp/editarAnexoConsignacao/editarAnexosConsignacao", request, session, model, responsavel);
        } else {
            return viewRedirect("jsp/editarAnexoConsignacao/editarAnexoConsignacaoValidarDocumento", request, session, model, responsavel);
        }

    }

    private String validarTokenFuncaoAutorizacao(HttpServletRequest request, HttpSession session, Model model, AcessoSistema responsavel) {
        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN, responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        SynchronizerToken.saveToken(request);
        final String adeCodigo = JspHelper.verificaVarQryStr(request, ADE_CODIGO_MAIUSCULO);
        // Recupera a autorização desconto
        CustomTransferObject autdes = null;
        try {
            autdes = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            autdes = TransferObjectHelper.mascararUsuarioHistorico(autdes, null, responsavel);
        } catch (final AutorizacaoControllerException ex) {
            session.setAttribute(CodedValues.MSG_ERRO, ex.getMessage());
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        final String svcCodigo = autdes.getAttribute(Columns.SVC_CODIGO).toString();
        if (!AcessoFuncaoServico.temAcessoFuncao(request, CodedValues.FUN_EDITAR_ANEXO_CONSIGNACAO, responsavel.getUsuCodigo(), svcCodigo)) {
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        final String csaCodigoAdes = (String) autdes.getAttribute(Columns.CSA_CODIGO);
        final String respCsaCodigo = responsavel.isCsa() ? responsavel.getCodigoEntidade() : responsavel.getCodigoEntidadePai();
        if ((responsavel.isCsa() || responsavel.isCor()) && !csaCodigoAdes.equals(respCsaCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN, responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
        model.addAttribute(ADE_CODIGO, adeCodigo);
        model.addAttribute(AUTDES, autdes);
        return null;
    }

    private void montarPaginacaoListaAnexos(HttpServletRequest request, Model model, AcessoSistema responsavel) {
        final String adeCodigo = (String) model.asMap().get(ADE_CODIGO);
        final CustomTransferObject autdes = (CustomTransferObject) model.asMap().get(AUTDES);
        final String linkListagem = getAcaoFormulario() + "?acao=exibir";
        int offset = 0;
        try {
        	offset = Integer.parseInt(request.getParameter("offset"));
        } catch (final Exception ex) {

        }

        List<TransferObject> anexos = null;
        try {
            final CustomTransferObject cto = new CustomTransferObject();
            cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
            final int size = JspHelper.LIMITE;
            anexos = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(cto, offset, size, responsavel);
            final int total = editarAnexoConsignacaoController.countAnexoAutorizacaoDesconto(cto, responsavel);

            final Set<String> params = new HashSet<>(request.getParameterMap().keySet());

            // Ignora os parâmetros abaixo
            params.remove("offset");
            params.remove("back");
            params.remove("linkRet");
            params.remove("linkRet64");
            params.remove("_skip_history_");
            params.remove("pager");
            params.remove("acao");

            final List<String> requestParams = new ArrayList<>(params);
            configurarPaginador(linkListagem, "rotulo.lst.arq.generico.titulo", total, size, requestParams, false, request, model);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }

        try {
            final Set<Date> periodos = periodoController.listarPeriodosPermitidos((String) autdes.getAttribute(Columns.ORG_CODIGO), null, responsavel);
            if (periodos != null && !periodos.isEmpty()) {
                model.addAttribute("lstPeriodos", periodos);
            }
        } catch (final PeriodoException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        boolean exigeAssinaturaDigital = false;

        try {
            // Recupero o parâmetro de consignatária
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
                final List<String> tpsCsaCodigos = new ArrayList<>();
                tpsCsaCodigos.add(CodedValues.TPS_EXIGE_ASSINATURA_DIGITAL_SOLICITACOES);
                final List<TransferObject> paramSvcCsa = parametroController.selectParamSvcCsa((String) autdes.getAttribute(Columns.SVC_CODIGO), (String) autdes.getAttribute(Columns.CSA_CODIGO), tpsCsaCodigos, false, responsavel);

                for (final TransferObject vo : paramSvcCsa) {
                    if (vo.getAttribute(Columns.PSC_VLR) != null && !"".equals(vo.getAttribute(Columns.PSC_VLR))) {
                        String exige = null;
                        exige = vo.getAttribute(Columns.PSC_VLR).toString();
                        exigeAssinaturaDigital = exige != null && "S".equals(exige);
                    }
                }
            }

            model.addAttribute("anexoObrigatorio", parametroController.isObrigatorioAnexoInclusao((String) autdes.getAttribute(Columns.SVC_CODIGO), responsavel));

        } catch (final ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }

        model.addAttribute("exigeAssinaturaDigital", exigeAssinaturaDigital);

        model.addAttribute(ADE_CODIGO, adeCodigo);
        model.addAttribute("offset", Integer.valueOf(offset));
        model.addAttribute("responsavel", responsavel);
        model.addAttribute(AUTDES, autdes);
        model.addAttribute("anexos", anexos);
    }

    private void exibeAlertaAnexosNecessarios(HttpSession session, String adeCodigo, CustomTransferObject autdes, AcessoSistema responsavel) {
        if (ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_CSA_ADE_SEM_MIN_ANEXOS, responsavel) && (!TextHelper.isNull(autdes.getAttribute(Columns.UCA_CSA_CODIGO)) || !TextHelper.isNull(autdes.getAttribute(Columns.UCO_COR_CODIGO)))) {
            try {
                final TransferObject agdNumAnexos = agendamentoController.findAgendamento(AgendamentoEnum.BLOQUEIO_CSA_ADE_SEM_NUM_ANEXOS_MINIMO.getCodigo(), responsavel);

                if (agdNumAnexos != null && DateHelper.dayDiff((Date) autdes.getAttribute(Columns.ADE_DATA), (Date) agdNumAnexos.getAttribute(Columns.AGD_DATA_CADASTRO)) > 0) {
                    CustomTransferObject paramSvcTO = null;
                    try {
                        paramSvcTO = parametroController.getParamSvcCse((String) autdes.getAttribute(Columns.SVC_CODIGO), CodedValues.TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR, responsavel);
                    } catch (final ParametroControllerException e) {
                        session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrado", responsavel));
                    }

                    if (paramSvcTO != null && !TextHelper.isNull(paramSvcTO.getAttribute(Columns.PSE_VLR)) && !TextHelper.isNull(paramSvcTO.getAttribute(Columns.PSE_VLR_REF))) {
                        final Short diasParaAnexarArqNecessarios = Short.parseShort((String) paramSvcTO.getAttribute(Columns.PSE_VLR));
                        final Short numAnexosMin = Short.parseShort((String) paramSvcTO.getAttribute(Columns.PSE_VLR_REF));

                        if (diasParaAnexarArqNecessarios != null && numAnexosMin != null && diasParaAnexarArqNecessarios.shortValue() > 0 && numAnexosMin.shortValue() > 0) {
                            final List<Calendario> diasUteis = calendarioController.lstCalendariosAPartirDe(DateHelper.getSystemDate(), true, diasParaAnexarArqNecessarios.intValue());

                            final int total = editarAnexoConsignacaoController.countAnexoAutorizacaoDesconto(adeCodigo, responsavel);
                            final int numAnexosFaltantes = numAnexosMin.intValue() - total;

                            if (numAnexosFaltantes > 0) {
                                final Date diaLimite = diasUteis.get(diasUteis.size() - 1).getCalData();
                                final Date horaMinutoAde = DateHelper.clearData((Date) autdes.getAttribute(Columns.ADE_DATA));
                                final String prazoString = DateHelper.format(diaLimite, LocaleHelper.getDatePattern()) + " " + DateHelper.format(horaMinutoAde, "HH:mm") + ":00";
                                final Date prazoAnexar = DateHelper.parse(prazoString, LocaleHelper.getDateTimePattern());

                                if (prazoAnexar.getTime() >= DateHelper.getSystemDatetime().getTime()) {
                                    session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.alerta.reservar.margem.anexos.minimos", responsavel, Integer.toString(numAnexosMin), prazoString, Integer.toString(numAnexosFaltantes)));
                                }
                            }
                        }
                    }
                }
            } catch (AutorizacaoControllerException | CalendarioControllerException | ParseException | AgendamentoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN, responsavel));
            }
        }
    }

    @RequestMapping(params = { "acao=uploadValidarDocumentos" })
    public String uploadValidarDocumentos(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) throws ServletException, IOException, UploadControllerException {
        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);
        final String erro = validarTokenFuncaoAutorizacao(request, session, model, responsavel);
        if (erro != null) {
            return erro;
        }
        final String adeCodigo = (String) model.asMap().get(ADE_CODIGO);
        final CustomTransferObject autdes = (CustomTransferObject) model.asMap().get(AUTDES);
        final HashMap<String, String> tipoAnexoNome = new HashMap<>();
        final String paramTamMaxArqAnexo = null;
        final int tamMaxArqAnexo = !TextHelper.isNull(paramTamMaxArqAnexo) ? Integer.parseInt(paramTamMaxArqAnexo) : 200;
        final UploadHelper uploadHelper = new UploadHelper();
        try {
            uploadHelper.processarRequisicao(request.getServletContext(), request, tamMaxArqAnexo * 1024);
        } catch (final ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
            final String msg = ex.getMessage();
            if (!TextHelper.isNull(msg)) {
                LOG.error(ex.getMessage(), ex);
                session.setAttribute(CodedValues.MSG_ERRO, msg);
            }
        }

        if (uploadHelper.getValorCampoFormulario("FORM") != null) {
            File anexo = null;
            String msgErro = null;

            final String idAnexo = session.getId();
            String aadDescricao = uploadHelper.getValorCampoFormulario(AAD_DESCRICAO);
            java.sql.Date aadPeriodo = null;

            try {
                final Date periodoContrato = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
                final java.sql.Date periodoContratoSql = DateHelper.toSQLDate(periodoContrato);
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel) || ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {
                    final String aadPeriodoStr = uploadHelper.getValorCampoFormulario(OCA_PERIODO);
                    if (!TextHelper.isNull(aadPeriodoStr)) {
                        aadPeriodo = DateHelper.toSQLDate(DateHelper.parse(aadPeriodoStr, "yyyy-MM-dd"));
                    }
                }

                if (aadPeriodo == null) {
                    aadPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(autdes.getAttribute(Columns.ORG_CODIGO).toString(), responsavel);
                }

                if (periodoContratoSql.compareTo(aadPeriodo) > 0) {
                    aadPeriodo = periodoContratoSql;
                }
            } catch (final Exception ex) {
                msgErro = ex.getMessage();
                session.setAttribute(CodedValues.MSG_ERRO, msgErro == null ? ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE, responsavel) : msgErro);
                LOG.error(ex.getMessage(), ex);
            }

            final List<String> tarCodigos = new ArrayList<>();
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_RG.getCodigo());
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO.getCodigo());
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE.getCodigo());
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo());

            //Verifica se existe o tipo de anexo para o período informado, pois se existir ele precisa ser excluído para que seja incluído o novo.
            List<AnexoAutorizacaoDesconto> lstAnexos = new ArrayList<>();
            try {
                lstAnexos = editarAnexoConsignacaoController.lstAnexoTipoArquivoPeriodo(adeCodigo, tarCodigos, aadPeriodo, responsavel);
                for(final AnexoAutorizacaoDesconto anexosAutorizacao : lstAnexos) {
                    final String nomeArquivo = anexosAutorizacao.getAadNome();
                    final String tipoArquivoCodigo = anexosAutorizacao.getTipoArquivo().getTarCodigo();
                    tipoAnexoNome.put(tipoArquivoCodigo, nomeArquivo);
                }
            } catch (final AutorizacaoControllerException ex) {
                msgErro = ex.getMessage();
                session.setAttribute(CodedValues.MSG_ERRO, msgErro == null ? ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE, responsavel) : msgErro);
                LOG.error(ex.getMessage(), ex);
            }

            // Define os valores padr�es, caso n�o sejam informados
            TipoArquivo tipoArquivo = null;
            String msgSucesso ="";
            boolean existeAnexoUpload = false;
            for(int i =1; i<5; i++) {
                final String nomeAnexo = uploadHelper.getValorCampoFormulario("FILE"+i);
                final String tipoDescricao ="";
                if (!TextHelper.isNull(nomeAnexo) && !TextHelper.isNull(adeCodigo)) {

                    switch (i) {
                        case 1:
                            tipoArquivo = uploadController.buscaTipoArquivoByPrimaryKey(TipoArquivoEnum.ARQUIVO_ANEXO_RG.getCodigo(), responsavel);
                            break;
                        case 2:
                            tipoArquivo = uploadController.buscaTipoArquivoByPrimaryKey(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO.getCodigo(), responsavel);
                            break;
                        case 3:
                            tipoArquivo = uploadController.buscaTipoArquivoByPrimaryKey(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE.getCodigo(), responsavel);
                            break;
                        case 4:
                            tipoArquivo = uploadController.buscaTipoArquivoByPrimaryKey(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo(), responsavel);
                            break;
                        default:
                            continue;
                    }

                    final String nomeArquivo = tipoAnexoNome.get(tipoArquivo.getTarCodigo());
                    if(!TextHelper.isNull(nomeArquivo)) {
                        if(TextHelper.isNull(msgErro)) {
                            msgErro = ApplicationResourcesHelper.getMessage("mensagem.erro.validar.documentos.tipo.arquivo.existe", responsavel, tipoDescricao, nomeArquivo);
                        } else {
                            msgErro = new StringBuilder(msgErro).append("<BR>").append(ApplicationResourcesHelper.getMessage("mensagem.erro.validar.documentos.tipo.arquivo.existe", responsavel, tipoDescricao, nomeArquivo)).toString();
                        }
                        session.setAttribute(CodedValues.MSG_ERRO,msgErro);
                        LOG.error(msgErro);
                        continue;
                    }

                    try {
                        String[] anexosName;
                        final String extensaoDoArquivo = "." + FilenameUtils.getExtension(nomeAnexo);
                        final String nomeArquivoFinal = tipoArquivo.getTarDescricao() + " " + autdes.getAttribute(Columns.ADE_NUMERO) + aadPeriodo + extensaoDoArquivo;
                        anexosName = nomeAnexo.split(";");
                        for (final String nomeAnexoCorrente : anexosName) {

                            if(tipoArquivo.getTarCodigo().equals(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo()) && nomeAnexoCorrente.endsWith(".zip")) {
                                anexo = UploadHelper.moverArquivoAnexoTemporario(nomeAnexoCorrente, adeCodigo, idAnexo, nomeArquivoFinal,true,UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_AUDIO_VIDEO_ZIP, responsavel);
                            } else {
                                anexo = UploadHelper.moverArquivoAnexoTemporario(nomeAnexoCorrente, adeCodigo, idAnexo, nomeArquivoFinal, responsavel);
                            }
                            if (anexo != null && anexo.exists()) {
                                aadDescricao = !TextHelper.isNull(aadDescricao) && aadDescricao.length() <= 255 ? aadDescricao : anexo.getName();
                                editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), aadDescricao, aadPeriodo, TipoArquivoEnum.recuperaTipoArquivo(tipoArquivo.getTarCodigo()), responsavel);
                                aadDescricao = null;
                                if(msgSucesso.isEmpty()) {
                                    msgSucesso = ApplicationResourcesHelper.getMessage("mensagem.anexo.validar.documentos.sucesso", responsavel, nomeAnexoCorrente, nomeArquivoFinal);
                                } else {
                                    msgSucesso = new StringBuilder(msgSucesso).append("<BR>" + ApplicationResourcesHelper.getMessage("mensagem.anexo.validar.documentos.sucesso", responsavel, nomeAnexoCorrente, nomeArquivoFinal)).toString();
                                }
                            }
                        }

                        //Verificamos se algum anexo foi carregado para então permitir criar a solicitação de aprovação
                        if(!existeAnexoUpload) {
                            existeAnexoUpload = true;
                        }
                    } catch (final Exception ex) {
                    	Optional.ofNullable(anexo).ifPresent(anx -> {
							try {
								Files.delete(anx.toPath());
							} catch (final IOException e) {
								LOG.error(ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE, responsavel), e);
							}
						}) ;
                        msgErro = ex.getMessage();
                        session.setAttribute(CodedValues.MSG_ERRO, msgErro == null ? ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE, responsavel) : msgErro);
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }

            if(!TextHelper.isNull(msgSucesso)) {
                session.setAttribute(CodedValues.MSG_INFO, msgSucesso);
            } else if(!existeAnexoUpload && TextHelper.isNull(msgErro)) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.validar.documentos.upload.anexo.nao.escolhido", responsavel));
            }

            //Verificamos se a quantiade de anexo é suficiente e se for criamos então a nova solicitação, porém não deixamos órgão e servidor, pois eles não podem fazer parte do fluxo de validação, então por este
            // motivo não é submetido para nova análise, somente é criado os anexos.
            try {
            	tarCodigos.remove(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo());
            	lstAnexos = editarAnexoConsignacaoController.lstAnexoTipoArquivoPeriodo(adeCodigo, tarCodigos, aadPeriodo, responsavel);

            	if(existeAnexoUpload && lstAnexos !=null && lstAnexos.size() >= CodedValues.NUM_MIN_ANEXOS_VALIDACAO_PERIODO && !responsavel.isSer()) {
            		final SolicitacaoAutorizacao solicitacaoAutorizacao = validarDocumentoController.listUltSolicitacaoValidacao(adeCodigo, responsavel);
            		if(TextHelper.isNull(solicitacaoAutorizacao)) {
                        throw new AutorizacaoControllerException("mensagem.erro.solicitacao.autorizacao.nao.exite", responsavel);
            		}
            		if(solicitacaoAutorizacao.getSsoCodigo() != null && (solicitacaoAutorizacao.getSsoCodigo().equals(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_REPROVADA.getCodigo()) || solicitacaoAutorizacao.getSsoCodigo().equals(StatusSolicitacaoEnum.AGUARDANDO_DOCUMENTO.getCodigo()))){
            		    validarDocumentoController.submeterContratoNovaAnalise(solicitacaoAutorizacao.getSoaCodigo(), adeCodigo, aadPeriodo, null, responsavel);
            		}
            	}
            } catch (AutorizacaoControllerException | ValidarDocumentoControllerException ex) {
            	msgErro = ex.getMessage();
            	session.setAttribute(CodedValues.MSG_ERRO, msgErro == null ? ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE, responsavel) : msgErro);
            	LOG.error(ex.getMessage(), ex);
            }
            exibeAlertaAnexosNecessarios(session, adeCodigo, autdes, responsavel);
        }
        final String filtroTable = JspHelper.verificaVarQryStr(request, "filtroTable");
        if(!TextHelper.isNull(filtroTable)) {
            final ParamSession paramSession = ParamSession.getParamSession(session);
            model.addAttribute("voltar",paramSession.getLastHistory() + "&filtroTable=" + filtroTable);
        }

        montarPaginacaoListaAnexos(request, model, responsavel);
        return viewRedirect("jsp/editarAnexoConsignacao/editarAnexoConsignacaoValidarDocumento", request, session, model, responsavel);
    }
}
