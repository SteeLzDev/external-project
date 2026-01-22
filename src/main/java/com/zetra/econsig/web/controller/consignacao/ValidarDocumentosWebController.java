package com.zetra.econsig.web.controller.consignacao;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ValidarDocumentoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.validardocumento.ValidarDocumentoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.controller.AbstractWebController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ValidarDocumentosWebController</p>
 * <p>Description: Controlador Web para o caso de uso Validar Documentos da Consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marcos.nolasco $
 * $Revision:  $
 * $Date: $
 */
@Controller
@RequestMapping(method = { RequestMethod.POST }, value = { "/v3/validarDocumentos" })
public class ValidarDocumentosWebController extends AbstractWebController {
    private static final String AUDITORIA = "auditoria";

    private static final String APROVADOS = "aprovados";

    private static final String REPROVADOS = "reprovados";

    private static final String PENDENTES = "pendentes";

    private static final String PENDENTESTODASCSA = "pendentesTodasCsa";

    private static final String AUDITORIAUSUARIOS = "auditoriaUsuarios";

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ValidarDocumentosWebController.class);

    @Autowired
    private ValidarDocumentoController validarDocumentoController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private ConsignanteController consignanteController;

    @Autowired
    private PeriodoController periodoController;

    @RequestMapping(params = { "acao=iniciar" })
    public String iniciar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        try {
            // Como neste caso de uso não escolhemos órgão, precisamos saber quais os órgão tem no sistema para buscar os períodos deles e listar os contratos do período.
            final List<Date> periodos = new ArrayList<>();
            periodos.add(PeriodoHelper.getInstance().getPeriodoAtual(null, responsavel));

            final CustomTransferObject criterio = new CustomTransferObject();
            criterio.setAttribute(Columns.ORG_ATIVO, CodedValues.STS_ATIVO);

            final List<TransferObject> lstOrgaos = consignanteController.lstOrgaos(criterio, responsavel);
            final List<String> orgCodigos = new ArrayList<>();
            for (final TransferObject orgao : lstOrgaos) {
                orgCodigos.add((String) orgao.getAttribute(Columns.ORG_CODIGO));
            }

            final List<TransferObject> lstPeriodoAtual = periodoController.obtemPeriodoAtual(orgCodigos, null, responsavel);

            for (final TransferObject periodo : lstPeriodoAtual) {
                final Date periodoOrgao = (Date) periodo.getAttribute(Columns.PEX_PERIODO);
                if (!periodos.contains(periodoOrgao)) {
                    periodos.add(periodoOrgao);
                }
            }

            final HashMap<String, String> hashAnexos = new HashMap<>();

            final List<String> tarCodigos = new ArrayList<>();
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_RG.getCodigo());
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO.getCodigo());
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE.getCodigo());
            tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo());

            boolean pendentes = (JspHelper.verificaVarQryStr(request, PENDENTES) != null) && "true".equals(JspHelper.verificaVarQryStr(request, PENDENTES));
            boolean pendentesTodasCsa = (JspHelper.verificaVarQryStr(request, "pendentesTodasCsa") != null) && "true".equals(JspHelper.verificaVarQryStr(request, "pendentesTodasCsa"));
            final boolean reprovados = (JspHelper.verificaVarQryStr(request, "reprovados") != null) && "true".equals(JspHelper.verificaVarQryStr(request, "reprovados"));
            final boolean aprovados = (JspHelper.verificaVarQryStr(request, "aprovados") != null) && "true".equals(JspHelper.verificaVarQryStr(request, "aprovados"));
            final boolean auditoria = (JspHelper.verificaVarQryStr(request, "auditoria") != null) && "true".equals(JspHelper.verificaVarQryStr(request, "auditoria"));
            final boolean auditoriaUsuarios = (JspHelper.verificaVarQryStr(request, "auditoriaUsuarios") != null) && "true".equals(JspHelper.verificaVarQryStr(request, "auditoriaUsuarios"));

            List<TransferObject> lstSituacaoContratos = new ArrayList<>();
            int contabilizaPeriodo = 0;
            for (final Date periodoAtual : periodos) {
                if (((pendentes && responsavel.isSup()) || ((pendentes || (!reprovados && !aprovados && !auditoria && !pendentesTodasCsa && !auditoriaUsuarios)) && responsavel.isCse())) && (contabilizaPeriodo < 1)) {
                    lstSituacaoContratos.addAll(validarDocumentoController.listarContratosStatusSolicitacaoIniFimPeriodo(StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo(), null, responsavel));
                    pendentes = true;
                } else if ((pendentes || (!reprovados && !aprovados && !auditoria)) && responsavel.isCsaCor() && (contabilizaPeriodo < 1)) {
                    lstSituacaoContratos = validarDocumentoController.listarContratosStatusSolicitacaoIniFimPeriodo(StatusSolicitacaoEnum.AGUARDANDO_DOCUMENTO.getCodigo(), null, responsavel);
                    lstSituacaoContratos.addAll(validarDocumentoController.listarContratosStatusSolicitacaoIniFimPeriodo(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_REPROVADA.getCodigo(), null, responsavel));
                    pendentes = true;
                } else if (reprovados && !responsavel.isCsaCor()) {
                    lstSituacaoContratos.addAll(validarDocumentoController.listarContratosStatusSolicitacaoIniFimPeriodo(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_REPROVADA.getCodigo(), periodoAtual, responsavel));
                } else if (auditoria) {
                    boolean incluiOrgao = ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_ORGAO_AUDITORIA, responsavel);
                    lstSituacaoContratos.addAll(validarDocumentoController.auditoriaContratos(periodoAtual, incluiOrgao, responsavel));
                } else if (aprovados) {
                    lstSituacaoContratos.addAll(validarDocumentoController.listarContratosStatusSolicitacaoIniFimPeriodo(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_APROVADA.getCodigo(), periodoAtual, responsavel));
                } else if ((pendentesTodasCsa || (!reprovados && !aprovados && !auditoria && !pendentes && !auditoriaUsuarios)) && responsavel.isSup() && (contabilizaPeriodo < 1)) {
                    lstSituacaoContratos = validarDocumentoController.listarContratosStatusSolicitacaoIniFimPeriodo(StatusSolicitacaoEnum.AGUARDANDO_DOCUMENTO.getCodigo(), null, responsavel);
                    lstSituacaoContratos.addAll(validarDocumentoController.listarContratosStatusSolicitacaoIniFimPeriodo(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_REPROVADA.getCodigo(), null, responsavel));
                    pendentesTodasCsa = true;
                } else if (auditoriaUsuarios) {
                    lstSituacaoContratos.addAll(validarDocumentoController.auditoriaUsuarios(periodoAtual, responsavel));
                }
                contabilizaPeriodo++;
            }

            //Listamos todos os anexos do período para criar um hashMap com adeCodigo e tipoArquivo para gerar a pré visualização, para este caso de uso é obrigatório que exista
            //somente um anexo do tipoArquivo por périodo, por este motivo buscamos por período, mas caso venha a existir dois verificamos se já não existe o último e por este motivo o método ordena pelo mais rescente.
            final List<TransferObject> lstAnexos = editarAnexoConsignacaoController.lstAnexoMaxPeriodo(tarCodigos, responsavel);
            for (final TransferObject anexo : lstAnexos) {
                final String adeCodigo = (String) anexo.getAttribute(Columns.ADE_CODIGO);
                final String tipoArquivo = (String) anexo.getAttribute(Columns.TAR_CODIGO);
                final String aadNome = (String) anexo.getAttribute(Columns.AAD_NOME);
                final String key = adeCodigo + ";" + tipoArquivo;

                if (!hashAnexos.containsKey(key)) {
                    hashAnexos.put(key, aadNome);
                }
            }

            model.addAttribute("lstSituacaoContratos", lstSituacaoContratos);
            model.addAttribute(PENDENTES, pendentes);
            model.addAttribute(PENDENTESTODASCSA, pendentesTodasCsa);
            model.addAttribute(REPROVADOS, reprovados);
            model.addAttribute(APROVADOS, aprovados);
            model.addAttribute(AUDITORIA, auditoria);
            model.addAttribute(AUDITORIAUSUARIOS, auditoriaUsuarios);
            model.addAttribute("hashAnexos", hashAnexos);
            model.addAttribute("tituloColunas", adicionaColunasTabela(auditoria, auditoriaUsuarios, responsavel));
            model.addAttribute("linhasColunas", criaLinhasTabela(auditoria, aprovados, reprovados, pendentes, pendentesTodasCsa, auditoriaUsuarios, lstSituacaoContratos, hashAnexos, responsavel));
            model.addAttribute("filtroTable", JspHelper.verificaVarQryStr(request, "filtroTable"));

            return viewRedirect("jsp/validarDocumentos/validarDocumentos", request, session, model, responsavel);

        } catch (final ZetraException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN, responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    @RequestMapping(params = { "acao=aprovarReprovar" })
    public String aprovarReprovar(HttpServletRequest request, HttpServletResponse response, HttpSession session, Model model) {

        final AcessoSistema responsavel = JspHelper.getAcessoSistema(request);

        if (!SynchronizerToken.isTokenValid(request)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN, responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        final String soaCodigo = JspHelper.verificaVarQryStr(request, "SOA_CODIGO");
        final String adeCodigo = JspHelper.verificaVarQryStr(request, "ADE_CODIGO");
        final String observacao = JspHelper.verificaVarQryStr(request, "SOA_OBS");
        final boolean aprovar = !TextHelper.isNull(JspHelper.verificaVarQryStr(request, "aprovar")) && "true".equals(JspHelper.verificaVarQryStr(request, "aprovar"));

        if (TextHelper.isNull(soaCodigo) || TextHelper.isNull(adeCodigo)) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.validar.documentos.adecodigo.soacodigo.nulos", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        } else if (!aprovar && TextHelper.isNull(observacao) && !responsavel.isCsaCor()) {
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.validar.documentos.reprovar.obs", responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }

        try {
            final String mensagemSucesso = aprovar ? ApplicationResourcesHelper.getMessage("mensagem.validar.documentos.aprovar.sucesso", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.validar.documentos.reprovar.sucesso", responsavel);

            if (aprovar) {
                validarDocumentoController.aprovarContrato(soaCodigo, adeCodigo, observacao, responsavel);
            } else {
                validarDocumentoController.reprovarContrato(soaCodigo, adeCodigo, observacao, responsavel);
            }
            session.setAttribute(CodedValues.MSG_INFO, mensagemSucesso);
            return iniciar(request, response, session, model);
        } catch (final ValidarDocumentoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_INTERNO_CONTATE_ADMIN, responsavel));
            return viewRedirect("jsp/visualizarPaginaErro/visualizarMensagem", request, session, model, responsavel);
        }
    }

    private List<String> adicionaColunasTabela(boolean auditoria, boolean auditoriaUsuario, AcessoSistema responsavel) throws ZetraException {

        final List<String> titulosColunas = new ArrayList<>();
        if (!auditoria && !auditoriaUsuario) {
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_ORGAO, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_CONSIGNATARIA, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_RESPONSAVEL, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.consultar.consignacao.dados.responsavel", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_NUMERO_ADE, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero.ade.abreviado", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_IDENTIFICADOR, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.consignacao.identificador", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_SERVIDOR, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_SERVICO, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_DATA_INICIAL, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inicial", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_DATA_FINAL, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.final", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_VALOR_PRESTACAO, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_VALOR_LIQUIDO_LIBERADO, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.liquido.liberado", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_VALOR_TOTAL, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.total", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_PRAZO, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.prazo.singular", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_CPF, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_NUMERO_DE_VALIDACOES, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.numero.validacoes", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_ORIGEM, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.origem", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_DATA_SOLICITACAO, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.solicitacao", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_RG, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.rg.frente", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_MANDADO_DE_PAGAMENTO, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.mandado.pgt", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_CONTRACHEQUE, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.contracheque", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_OUTROS, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.outros", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_OBSERVACAO, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.obs", responsavel));
            }
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_ACOES, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.acoes", responsavel));
            }
        } else if (auditoria) {
            titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel));
            if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_ORGAO_AUDITORIA, responsavel)) {
                titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel));
            }
            titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.auditoria.pendente", responsavel));
            titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.auditoria.aprovado", responsavel));
            titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.auditoria.reprovado", responsavel));
        } else {
            titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.usuario.singular", responsavel));
            titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.auditoria.aprovado", responsavel));
            titulosColunas.add(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.auditoria.reprovado", responsavel));
        }

        return titulosColunas;
    }

    private String criaLinhasTabela(boolean auditoria, boolean aprovados, boolean reprovados, boolean pendentes, boolean pendentesTodasCsa, boolean auditoriaUsuario, List<TransferObject> lstContratosValidacao, HashMap<String, String> hashAnexos, AcessoSistema responsavel) throws ZetraException, ParseException {

        //DESENV-18314: Todas as linhas são criadas neste momento para não deixar a responsabilidade para o navegador do usuário, assim criamos no controller para o servidor ser responsavél por montar as linhas
        // fizemos isso para melhorar a performance da tabela.

        final String tipoArquivoRgFrente = TipoArquivoEnum.ARQUIVO_ANEXO_RG.getCodigo();
        final String tipoArquivoAutorizaco = TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO.getCodigo();
        final String tipoArquivoContraCheque = TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE.getCodigo();
        final String tipoArquivoOutro = TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo();
        final String rotuloMoeda = ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel) + " ";

        final StringBuilder linha = new StringBuilder();

        for (final TransferObject contratos : lstContratosValidacao) {
            String csaNome = (String) contratos.getAttribute(Columns.CSA_NOME_ABREV);
            csaNome = csaNome.replace("\"", "\\\"").replace("'", "\\'").replaceAll("\\r\\n|\\n", " ");

            final String usuNome = (String) contratos.getAttribute(Columns.USU_LOGIN);
            if (!auditoria && !auditoriaUsuario) {
                String serNome = (String) contratos.getAttribute(Columns.SER_NOME);
                serNome = serNome.replace("\"", "\\\"").replace("'", "\\'").replaceAll("\\r\\n|\\n", " ");

                final String soaCodigo = (String) contratos.getAttribute(Columns.SOA_CODIGO);
                final String adeCodigo = (String) contratos.getAttribute(Columns.ADE_CODIGO);
                final Long adeNumero = (Long) contratos.getAttribute(Columns.ADE_NUMERO);
                final String adeIdentifiador = (String) contratos.getAttribute(Columns.ADE_IDENTIFICADOR);
                final String svcDescricao = (String) contratos.getAttribute(Columns.SVC_DESCRICAO);
                final String cnvCodVerba = (String) contratos.getAttribute(Columns.CNV_COD_VERBA);
                final String adeAnoMesIni = DateHelper.format((Date) contratos.getAttribute(Columns.ADE_ANO_MES_INI), LocaleHelper.getDatePattern());
                final String adeAnoMesFim = contratos.getAttribute(Columns.ADE_ANO_MES_FIM) != null ? DateHelper.format((Date) contratos.getAttribute(Columns.ADE_ANO_MES_FIM), LocaleHelper.getDatePattern()) : "";
                final String soaDataSolicitacao = DateHelper.reformat(contratos.getAttribute("DATA_SOLICITACAO").toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                final String adeVlr = NumberHelper.format(((java.math.BigDecimal) contratos.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang());
                final String adeVlrLiquido = contratos.getAttribute(Columns.ADE_VLR_LIQUIDO) != null ? NumberHelper.format(((java.math.BigDecimal) contratos.getAttribute(Columns.ADE_VLR_LIQUIDO)).doubleValue(), NumberHelper.getLang()) : "";
                final String vlrTotal = NumberHelper.format(new java.math.BigDecimal(String.valueOf(contratos.getAttribute("VALOR_TOTAL"))).doubleValue(), NumberHelper.getLang());
                final String adePrazo = contratos.getAttribute(Columns.ADE_PRAZO) != null ? String.valueOf(contratos.getAttribute(Columns.ADE_PRAZO)) : ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel);
                final String serCpf = (String) contratos.getAttribute(Columns.SER_CPF);
                final String numValidacoes = String.valueOf(contratos.getAttribute("NUMERO_VALIDACOES"));
                final String origem = (String) contratos.getAttribute(Columns.OSO_DESCRICAO);
                final String obs = !TextHelper.isNull(contratos.getAttribute(Columns.SOA_OBS)) ? (String) contratos.getAttribute(Columns.SOA_OBS) : "";
                final String observacao = obs.replace("\"", "\\\"").replace("'", "\\'").replaceAll("\\r\\n|\\n", " ");
                final String adeData = DateHelper.format((Date) contratos.getAttribute(Columns.ADE_DATA), "yyyyMMdd");
                final String rgFrenteVerso = hashAnexos.containsKey(adeCodigo + ";" + tipoArquivoRgFrente) ? hashAnexos.get(adeCodigo + ";" + tipoArquivoRgFrente) : "";
                final String autPgt = hashAnexos.containsKey(adeCodigo + ";" + tipoArquivoAutorizaco) ? hashAnexos.get(adeCodigo + ";" + tipoArquivoAutorizaco) : "";
                final String contraCheque = hashAnexos.containsKey(adeCodigo + ";" + tipoArquivoContraCheque) ? hashAnexos.get(adeCodigo + ";" + tipoArquivoContraCheque) : "";
                final String outro = hashAnexos.containsKey(adeCodigo + ";" + tipoArquivoOutro) ? hashAnexos.get(adeCodigo + ";" + tipoArquivoOutro) : "";
                final boolean audio = !TextHelper.isNull(outro) && (outro.toLowerCase().endsWith(".mp3") || outro.toLowerCase().endsWith(".wma"));
                final boolean rgFrentePdf = !TextHelper.isNull(rgFrenteVerso) && validaExtensaoPdf(rgFrenteVerso);
                final boolean autPgtPdf = !TextHelper.isNull(autPgt) && validaExtensaoPdf(autPgt);
                final boolean contraChequePdf = !TextHelper.isNull(contraCheque) && validaExtensaoPdf(contraCheque);
                final String orgNome = (String) contratos.getAttribute(Columns.ORG_NOME);

                linha.append("[");
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_ORGAO, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(orgNome)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_CONSIGNATARIA, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(csaNome)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_RESPONSAVEL, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(usuNome)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_NUMERO_ADE, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(adeNumero)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_IDENTIFICADOR, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(adeIdentifiador)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_SERVIDOR, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(serNome)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_SERVICO, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(cnvCodVerba + '-' + svcDescricao)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_DATA_INICIAL, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(adeAnoMesIni)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_DATA_FINAL, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(adeAnoMesFim)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_VALOR_PRESTACAO, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(rotuloMoeda + adeVlr)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_VALOR_LIQUIDO_LIBERADO, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(!TextHelper.isNull(adeVlrLiquido) ? rotuloMoeda + adeVlrLiquido : "")).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_VALOR_TOTAL, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(rotuloMoeda + vlrTotal)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_PRAZO, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(adePrazo)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_CPF, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(serCpf)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_NUMERO_DE_VALIDACOES, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(numValidacoes)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_ORIGEM, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(origem)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_DATA_SOLICITACAO, responsavel)) {
                    linha.append("'").append(TextHelper.forHtmlContent(soaDataSolicitacao)).append("',");
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_RG, responsavel)) {
                    if (!TextHelper.isNull(rgFrenteVerso)) {
                        linha.append("\"<a href='#' data-bs-toggle='modal' data-bs-target='");
                        if (rgFrentePdf) {
                            linha.append("#modalPdf' onClick=").append("\\\"").append("loadPdf('").append(adeCodigo).append("','").append(adeData).append("','").append(rgFrenteVerso).append("');\\\"");
                        } else if (rgFrenteVerso.endsWith(".fad")) {
                            linha.append("' onClick=").append("\\\"").append("verificarDownload('").append(adeCodigo).append("', '").append(adeData).append("', '").append(rgFrenteVerso).append("');\\\"");
                        } else {
                            linha.append("#modalImg' onClick=").append("\\\"").append("loadImg('").append(adeCodigo).append("','").append(adeData).append("','").append(rgFrenteVerso).append("');\\\"");
                        }
                        linha.append(" '>").append(TextHelper.forHtmlContent(rgFrenteVerso)).append("</a>\",");
                    } else {
                        linha.append("'',");
                    }
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_MANDADO_DE_PAGAMENTO, responsavel)) {
                    if (!TextHelper.isNull(autPgt)) {
                        linha.append("\"<a href='#' ");
                        if (autPgtPdf) {
                            linha.append("#modalPdf' onClick=").append("\\\"").append("loadPdf('").append(adeCodigo).append("', '").append(adeData).append("', '").append(autPgt).append("');\\\"");
                        } else if (autPgt.endsWith(".fad")) {
                            linha.append("' onClick=").append("\\\"").append("verificarDownload('").append(adeCodigo).append("', '").append(adeData).append("', '").append(autPgt).append("');\\\"");
                        } else {
                            linha.append(" onClick=").append("\\\"").append("loadImg('").append(adeCodigo).append("', '").append(adeData).append("', '").append(autPgt).append("');\\\"");
                        }
                        linha.append(" '>").append(TextHelper.forHtmlContent(autPgt)).append("</a>\",");
                    } else {
                        linha.append("'',");
                    }
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_CONTRACHEQUE, responsavel)) {
                    if (!TextHelper.isNull(contraCheque)) {
                        linha.append("\"<a href='#' ");
                        if (contraChequePdf) {
                            linha.append(" onClick=").append("\\\"").append("loadPdf('").append(adeCodigo).append("', '").append(adeData).append("', '").append(contraCheque).append("');\\\"");
                        } else if (contraCheque.endsWith(".fad")) {
                            linha.append("' onClick=").append("\\\"").append("verificarDownload('").append(adeCodigo).append("', '").append(adeData).append("', '").append(contraCheque).append("');\\\"");
                        } else {
                            linha.append(" onClick=").append("\\\"").append("loadImg('").append(adeCodigo).append("', '").append(adeData).append("', '").append(contraCheque).append("');\\\"");
                        }
                        linha.append(" '>").append(TextHelper.forHtmlContent(contraCheque)).append("</a>\",");
                    } else {
                        linha.append("'',");
                    }
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_OUTROS, responsavel)) {
                    if (!TextHelper.isNull(outro)) {
                        linha.append("\"<a href='#' ");
                        if (audio) {
                            linha.append("#modalAudio' onClick=").append("\\\"").append("loadAudio('").append(adeCodigo).append("', '").append(adeData).append("', '").append(outro).append("');\\\"");
                        } else if (outro.endsWith(".zip") || outro.endsWith(".fad")) {
                            linha.append("' onClick=").append("\\\"").append("verificarDownload('").append(adeCodigo).append("', '").append(adeData).append("', '").append(outro).append("');\\\"");
                        } else {
                            linha.append(" onClick=").append("\\\"").append("loadVideo('").append(adeCodigo).append("', '").append(adeData).append("', '").append(outro).append("');\\\"");
                        }
                        linha.append(" '>").append(TextHelper.forHtmlContent(outro)).append("</a>\",");
                    } else {
                        linha.append("'',");
                    }
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_OBSERVACAO, responsavel)) {
                    if (reprovados || aprovados || pendentesTodasCsa || (pendentes && responsavel.isCsaCor())) {
                        if (!TextHelper.isNull(observacao)) {
                            linha.append("'").append(TextHelper.forHtmlContent(observacao)).append("',");
                        } else {
                            linha.append("'',");
                        }
                    } else {
                        linha.append("\"<textarea id='").append(soaCodigo).append("'></textarea>\",");
                    }
                }
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_ACOES, responsavel)) {
                    linha.append("\"<div class='actions'>");
                    linha.append("<div class='dropdown'>");
                    linha.append("<a class='dropdown-toggle ico-action' href='#' role='button' id='userMenu' data-bs-toggle='dropdown' aria-haspopup='true' aria-expanded='false'>");
                    linha.append("<div class='form-inline'> <span class='mr-1' data-bs-toggle='tooltip' title='' data-original-title='").append(ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)).append("'");
                    linha.append("aria-label='").append(ApplicationResourcesHelper.getMessage("rotulo.mais.acoes", responsavel)).append("'> <svg> <use xmlns:xlink='http://www.w3.org/1999/xlink' xlink:href='#i-engrenagem'></use></svg> </span>");
                    linha.append(ApplicationResourcesHelper.getMessage("rotulo.botao.opcoes", responsavel)).append("</div> </a> <div class='dropdown-menu dropdown-menu-right' aria-labelledby='userMenu'>");
                    if (pendentes && !responsavel.isCsaCor()) {
                        linha.append("<a class='dropdown-item' href='#no-back' onClick=").append("\\\"").append("aprovar('").append(adeCodigo).append("','").append(soaCodigo).append("','").append(observacao).append("');\\\">");
                        linha.append(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.acao.aprovar", responsavel)).append("</a>");
                        linha.append("<a class='dropdown-item' href='#no-back' onClick=").append("\\\"").append("validaReprovacao('").append(soaCodigo).append("','").append(adeCodigo).append("');\\\">");
                        linha.append(ApplicationResourcesHelper.getMessage("rotulo.validar.documentos.acao.reprovar", responsavel)).append("</a>");
                    }
                    if (pendentes && responsavel.isCsaCor()) {
                        linha.append("<a class='dropdown-item' href='#no-back' onClick=").append("\\\"").append("editarAnexos('").append(adeCodigo).append("');\\\">");
                        linha.append(ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.anexos", responsavel)).append("</a>");
                    }
                    if ((!TextHelper.isNull(rgFrenteVerso) || !TextHelper.isNull(autPgt) || !TextHelper.isNull(contraCheque) || !TextHelper.isNull(outro)) && !pendentesTodasCsa) {
                        linha.append("<a class='dropdown-item' href='#no-back' onClick=").append("\\\"").append("downloads('").append(adeCodigo).append("','").append(adeData).append("');\\\">");
                        linha.append(ApplicationResourcesHelper.getMessage("rotulo.acoes.download", responsavel)).append("</a>");
                    }
                    if (pendentesTodasCsa && responsavel.isSup()) {
                        linha.append("<a class='dropdown-item' href='#no-back' onClick=").append("\\\"").append("editarAnexos('").append(adeCodigo).append("');\\\">");
                        linha.append(ApplicationResourcesHelper.getMessage("rotulo.acoes.editar.anexos", responsavel)).append("</a>");
                    } else {
                        linha.append("<a class='dropdown-item' href='#no-back' onClick=").append("\\\"").append("visualizar('").append(adeCodigo).append("');\\\">");
                        linha.append(ApplicationResourcesHelper.getMessage("rotulo.acoes.visualizar", responsavel)).append("</a>");
                    }
                    linha.append("</div> </div> </div>\"");
                }
                linha.append("],");
            } else if (auditoria) {
            	final String orgNome = (String) contratos.getAttribute(Columns.ORG_NOME);
                final String pendente = String.valueOf(contratos.getAttribute("PENDENTE"));
                final String aprovado = String.valueOf(contratos.getAttribute("APROVADO"));
                final String reprovado = String.valueOf(contratos.getAttribute("REPROVADO"));
                linha.append("[");
                linha.append("'").append(csaNome).append("','");
                if (ShowFieldHelper.showField(FieldKeysConstants.VALIDAR_DOCUMENTOS_ORGAO_AUDITORIA, responsavel)) {
                    linha.append(orgNome).append("','");
                }
                linha.append(pendente).append("','").append(aprovado).append("','").append(reprovado).append("'],");
            } else {
                final String aprovadoUsuario = String.valueOf(contratos.getAttribute("APROVADO"));
                final String reprovadoUsuario = String.valueOf(contratos.getAttribute("REPROVADO"));
                linha.append("[");
                linha.append("'").append(usuNome).append("','").append(aprovadoUsuario).append("','").append(reprovadoUsuario).append("'],");
            }
        }
        return linha.toString();
    }

    private boolean validaExtensaoPdf(String nomeArquivo) {
        final String[] arrayExtensoesConverterPdf = { ".rtf", ".doc", ".docx", ".xls", ".xlsx", ".txt", ".csv", ".pdf" };

        if (nomeArquivo != null) {
            for (final String extensoes : arrayExtensoesConverterPdf) {
                if (nomeArquivo.toLowerCase().endsWith(extensoes.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }
}
