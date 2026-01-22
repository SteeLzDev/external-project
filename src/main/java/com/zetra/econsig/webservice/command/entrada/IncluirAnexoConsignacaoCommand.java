package com.zetra.econsig.webservice.command.entrada;

import static com.zetra.econsig.values.ApplicationResourcesKeys.MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE;
import static com.zetra.econsig.webservice.CamposAPI.ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.CONSIGNACAO;
import static com.zetra.econsig.webservice.CamposAPI.DESCRICAO_ANEXO;
import static com.zetra.econsig.webservice.CamposAPI.MENSAGEM;
import static com.zetra.econsig.webservice.CamposAPI.PERIODO;
import static com.zetra.econsig.webservice.CamposAPI.TIPO_ARQUIVO;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;

import com.zetra.econsig.delegate.PeriodoDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CalendarioControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.UploadControllerException;
import com.zetra.econsig.exception.ValidarDocumentoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.persistence.entity.AnexoAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.Calendario;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.TipoArquivo;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.service.calendario.CalendarioController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.upload.UploadController;
import com.zetra.econsig.service.validardocumento.ValidarDocumentoController;
import com.zetra.econsig.values.AgendamentoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.CamposAPI;
import com.zetra.econsig.webservice.soap.entidade.Anexo;

import jakarta.servlet.ServletException;

/**
 * <p>Title: IncluirAnexoConsignacaoCommand</p>
 * <p>Description: classe command que trata requisição externa ao eConsig de inclusão de anexos de consignação</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class IncluirAnexoConsignacaoCommand extends RequisicaoExternaCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(IncluirAnexoConsignacaoCommand.class);

    public IncluirAnexoConsignacaoCommand(Map<CamposAPI, Object> parametros, AcessoSistema responsavel) {
        super(parametros, responsavel);
    }

    private static final String MENSAGEM_SUCESSO = "mensagem.sucesso";

    private static final String[] EXTENSOES_PERMITIDAS_ANEXO_AUDIO_VIDEO_ZIP_SOAP = { ".zip", ".pdf", ".key", ".fida" };

    @Override
    protected void executaOperacao(Map<CamposAPI, Object> parametros) throws ZetraException {
        @SuppressWarnings("unchecked")
        final List<TransferObject> autdesList = (List<TransferObject>) parametros.get(CONSIGNACAO);

        final TransferObject autdes = autdesList.get(0);
        final String adeCodigo = (String) autdes.getAttribute(Columns.ADE_CODIGO);
        final String adeNumero = String.valueOf(autdes.getAttribute(Columns.ADE_NUMERO));
        final String orgCodigo = (String) autdes.getAttribute(Columns.ORG_CODIGO);
        final boolean validarDocumentos = ParamSist.getBoolParamSist(CodedValues.TPC_CONTRATOS_DEVEM_SER_VALIDADOS_PELA_CSE, responsavel);
        final boolean tarCodigoValidarDocumentos = parametros.containsKey(TIPO_ARQUIVO) && !TextHelper.isNull(parametros.get(TIPO_ARQUIVO)) && (parametros.get(TIPO_ARQUIVO).equals(TipoArquivoEnum.ARQUIVO_ANEXO_RG.getCodigo()) || parametros.get(TIPO_ARQUIVO).equals(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO.getCodigo()) || parametros.get(TIPO_ARQUIVO).equals(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE.getCodigo()) || parametros.get(TIPO_ARQUIVO).equals(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo()));
        final boolean anexoGenerico = !parametros.containsKey(TIPO_ARQUIVO) || TextHelper.isNull(parametros.get(TIPO_ARQUIVO)) || parametros.get(TIPO_ARQUIVO).equals(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_GENERICO.getCodigo());
        final boolean incluirAnexoConsignacaoV7 = !parametros.containsKey(TIPO_ARQUIVO);
        String[] extensoesPermitidas = validarDocumentos && tarCodigoValidarDocumentos ? parametros.get(TIPO_ARQUIVO).equals(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo()) ? UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_PRE_VISUALIZAR_AUDIO_VIDEO_ZIP : UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_PRE_VISUALIZAR : UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO;

        if ((validarDocumentos ? !tarCodigoValidarDocumentos : !anexoGenerico)) {
            throw new ZetraException("mensagem.erro.tipo.arquivo.parametro.incorreto", responsavel);
        }

        final String diretorioRaizArquivos = ParamSist.getDiretorioRaizArquivos();
        if (TextHelper.isNull(diretorioRaizArquivos)) {
            throw new ZetraException("mensagem.erro.diretorio.conf.inexistente", responsavel);
        }

        final String path = diretorioRaizArquivos + File.separatorChar + "anexo" + File.separatorChar + DateHelper.format((Date) autdes.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + adeCodigo;

        final Anexo anexo = getAnexo(parametros.get(ANEXO));
        String aadNome = anexo.getNomeArquivo();

        // Verifica se a extensão do arquivo é válida.
        if (!validaExtensao(aadNome, extensoesPermitidas)) {
            throw new ZetraException("mensagem.erro.copia.impossivel.arquivos.permitidos", responsavel, TextHelper.join(extensoesPermitidas, ", "));
        }

        final String aadDescricao = (TextHelper.isNull(parametros.get(DESCRICAO_ANEXO)) ? aadNome : parametros.get(DESCRICAO_ANEXO).toString());

        aadNome = DateHelper.format(DateHelper.getSystemDatetime(), "yyMMddHHmmssS") + new SecureRandom().nextInt(10) + aadNome.substring(aadNome.lastIndexOf("."), aadNome.length());
        anexo.setNomeArquivo(aadNome);

        final Date periodoContrato = (Date) autdes.getAttribute(Columns.ADE_ANO_MES_INI);
        final java.sql.Date periodoContratoSql = (periodoContrato != null ? DateHelper.toSQLDate(periodoContrato) : null);
        java.sql.Date aadPeriodo = null;

        if ((ParamSist.paramEquals(CodedValues.TPC_PERMITE_AGRUPAR_PERIODOS_EXPORTACAO, CodedValues.TPC_SIM, responsavel) && ParamSist.paramEquals(CodedValues.TPC_PERMITE_ESCOLHER_PERIODO_EM_AGRUPAMENTO, CodedValues.TPC_SIM, responsavel)) || ParamSist.paramEquals(CodedValues.TPC_HABILITA_EXTENSAO_PERIODO_FOLHA_AJUSTES, CodedValues.TPC_SIM, responsavel)) {
            final String aadPeriodoObjct = (String) parametros.get(PERIODO);

            if (!TextHelper.isNull(aadPeriodoObjct)) {
                try {
                    if (aadPeriodoObjct.matches("([0-9]{2})/([0-9]{4})")) {
                        aadPeriodo = DateHelper.parsePeriodString(aadPeriodoObjct);
                    } else {
                        throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
                    }
                } catch (final ParseException e) {
                    throw new ZetraException("mensagem.erro.periodo.invalido", responsavel);
                }
            }

            if (aadPeriodo != null) {
                final PeriodoDelegate perDelegate = new PeriodoDelegate();
                final Set<Date> periodosPermitidos = perDelegate.listarPeriodosPermitidos(orgCodigo, periodoContrato, responsavel);
                if ((periodosPermitidos != null) && !periodosPermitidos.isEmpty() && !periodosPermitidos.contains(aadPeriodo)) {
                    throw new ZetraException("mensagem.erro.periodos.permitidos", responsavel, periodosPermitidos.stream().map(data -> DateHelper.format(data, LocaleHelper.getDatePattern())).collect(Collectors.joining(", ")));
                }
            }
        }

        if (aadPeriodo == null) {
            aadPeriodo = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);
        }

        if ((periodoContratoSql != null) && (periodoContratoSql.compareTo(aadPeriodo) > 0)) {
            aadPeriodo = periodoContratoSql;
        }

        if (incluirAnexoConsignacaoV7 || anexoGenerico) {
            try {
                final File arquivoAnexo = new File(path + File.separatorChar + aadNome);
                if ((arquivoAnexo != null) && arquivoAnexo.exists()) {
                    throw new ZetraException("mensagem.erro.anexo.ja.existe", responsavel);
                }

                salvarAnexo(path, anexo, UploadHelper.EXTENSOES_PERMITIDAS_ANEXO_CONTRATO);

                final EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);
                editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, aadNome, aadDescricao, aadPeriodo, null, responsavel);

                exibirAlertaNumAnexosExigido(parametros, autdes, adeCodigo, editarAnexoConsignacaoController);
            } catch (final IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erroInternoSistema", responsavel);
            }
        } else {
            try {
                aadNome = redefineNomeArquivo((String) parametros.get(TIPO_ARQUIVO), adeNumero, aadPeriodo, FilenameUtils.getExtension(aadNome));
                anexo.setNomeArquivo(aadNome);

                if (parametros.get(TIPO_ARQUIVO).equals(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo()) && aadNome.toLowerCase().endsWith(".zip")) {
                    extensoesPermitidas = EXTENSOES_PERMITIDAS_ANEXO_AUDIO_VIDEO_ZIP_SOAP;
                }

                validaAnexoPeriodo(adeCodigo, aadPeriodo, (String) parametros.get(TIPO_ARQUIVO));
                salvarAnexo(path, anexo, extensoesPermitidas);
                final EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);
                editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, aadNome, aadDescricao, aadPeriodo, TipoArquivoEnum.recuperaTipoArquivo((String) parametros.get(TIPO_ARQUIVO)), responsavel);
                qtdAnexoSolicitacao(adeCodigo, aadPeriodo);
                exibirAlertaNumAnexosExigido(parametros, autdes, adeCodigo, editarAnexoConsignacaoController);
            } catch (UploadControllerException | ServletException | IOException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new ZetraException("mensagem.erroInternoSistema", responsavel);
            }
        }

    }

    //Verifica se existe o tipo de anexo para o período informado, pois se existir ele precisa ser excluído para que seja incluído o novo.
    private void validaAnexoPeriodo(String adeCodigo, Date aadPeriodo, String tarCodigo) throws ServletException, IOException, ZetraException {
        final List<String> tarCodigos = new ArrayList<>();
        final HashMap<String, String> tipoAnexoNome = new HashMap<>();
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_RG.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_OUTRO.getCodigo());

        try {
            final EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);
            final List<AnexoAutorizacaoDesconto> lstAnexos = editarAnexoConsignacaoController.lstAnexoTipoArquivoPeriodo(adeCodigo, tarCodigos, aadPeriodo, responsavel);
            for (final AnexoAutorizacaoDesconto anexosAutorizacao : lstAnexos) {
                final String nomeArquivo = anexosAutorizacao.getAadNome();
                final String tipoArquivoCodigo = anexosAutorizacao.getTipoArquivo().getTarCodigo();
                tipoAnexoNome.put(tipoArquivoCodigo, nomeArquivo);
            }
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException((ex.getMessage() == null ? ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE, responsavel) : ex.getMessage()), responsavel);
        }

        if (tipoAnexoNome.containsKey(tarCodigo)) {
            throw new ZetraException("mensagem.erro.validar.documentos.tipo.arquivo.existe", responsavel, tarCodigo, tipoAnexoNome.get(tarCodigo));
        }
    }

    private String redefineNomeArquivo(String tarCodigo, String adeNumero, Date aadPeriodo, String extensaoArquivo) throws UploadControllerException {
        final UploadController uploadController = ApplicationContextProvider.getApplicationContext().getBean(UploadController.class);
        final TipoArquivo tipoArquivo = uploadController.buscaTipoArquivoByPrimaryKey(tarCodigo, responsavel);

        return tipoArquivo.getTarDescricao() + " " + adeNumero + aadPeriodo + "." + extensaoArquivo;

    }

    //Verificamos se a quantiade de anexo é suficiente e se for criamos então a nova solicitação, porém não deixamos órgão e servidor, pois eles não podem fazer parte do fluxo de validação, então por este
    // motivo não é submetido para nova análise, somente é criado os anexos.
    private void qtdAnexoSolicitacao(String adeCodigo, Date aadPeriodo) throws ZetraException {
        final List<String> tarCodigos = new ArrayList<>();
        List<AnexoAutorizacaoDesconto> lstAnexos = new ArrayList<>();
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_RG.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_PAGAMENTO.getCodigo());
        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_CONTRACHEQUE.getCodigo());

        final EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);
        final ValidarDocumentoController validarDocumentoController = ApplicationContextProvider.getApplicationContext().getBean(ValidarDocumentoController.class);
        try {
            lstAnexos = editarAnexoConsignacaoController.lstAnexoTipoArquivoPeriodo(adeCodigo, tarCodigos, aadPeriodo, responsavel);

            if ((lstAnexos != null) && (lstAnexos.size() >= CodedValues.NUM_MIN_ANEXOS_VALIDACAO_PERIODO) && !responsavel.isSer()) {
                final SolicitacaoAutorizacao solicitacaoAutorizacao = validarDocumentoController.listUltSolicitacaoValidacao(adeCodigo, responsavel);
                if (TextHelper.isNull(solicitacaoAutorizacao)) {
                    throw new AutorizacaoControllerException("mensagem.erro.solicitacao.autorizacao.nao.exite", responsavel);
                }
                if ((solicitacaoAutorizacao.getSsoCodigo() != null) && (solicitacaoAutorizacao.getSsoCodigo().equals(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_REPROVADA.getCodigo()) || solicitacaoAutorizacao.getSsoCodigo().equals(StatusSolicitacaoEnum.AGUARDANDO_DOCUMENTO.getCodigo()))) {
                    validarDocumentoController.submeterContratoNovaAnalise(solicitacaoAutorizacao.getSoaCodigo(), adeCodigo, aadPeriodo, null, responsavel);
                }
            }
        } catch (AutorizacaoControllerException | ValidarDocumentoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ZetraException((ex.getMessage() == null ? ApplicationResourcesHelper.getMessage(MENSAGEM_ERRO_UPLOAD_EDT_ANEXO_ADE, responsavel) : ex.getMessage()), responsavel);
        }
    }

    private void exibirAlertaNumAnexosExigido(Map<CamposAPI, Object> parametros, TransferObject autdes, String adeCodigo, EditarAnexoConsignacaoController editarAnexoConsignacaoController) throws AutorizacaoControllerException {
        if (ParamSist.getBoolParamSist(CodedValues.TPC_BLOQUEIA_CSA_ADE_SEM_MIN_ANEXOS, responsavel) && (!TextHelper.isNull(autdes.getAttribute(Columns.UCA_CSA_CODIGO)) || !TextHelper.isNull(autdes.getAttribute(Columns.UCO_COR_CODIGO)))) {

            try {
                final AgendamentoController agendamentoController = ApplicationContextProvider.getApplicationContext().getBean(AgendamentoController.class);
                final TransferObject agdNumAnexos = agendamentoController.findAgendamento(AgendamentoEnum.BLOQUEIO_CSA_ADE_SEM_NUM_ANEXOS_MINIMO.getCodigo(), responsavel);

                if ((agdNumAnexos != null) && (DateHelper.dayDiff((Date) autdes.getAttribute(Columns.ADE_DATA), (Date) agdNumAnexos.getAttribute(Columns.AGD_DATA_CADASTRO)) > 0)) {
                    CustomTransferObject paramSvcTO = null;
                    try {
                        paramSvcTO = parametroController.getParamSvcCse((String) autdes.getAttribute(Columns.SVC_CODIGO), CodedValues.TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR, responsavel);
                    } catch (final ParametroControllerException e) {
                        parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage(MENSAGEM_SUCESSO, responsavel) + ". " + ApplicationResourcesHelper.getMessage("mensagem.erro.recuperar.anexos.contrato", responsavel));
                        return;
                    }

                    if ((paramSvcTO != null) && !TextHelper.isNull(paramSvcTO.getAttribute(Columns.PSE_VLR)) && !TextHelper.isNull(paramSvcTO.getAttribute(Columns.PSE_VLR_REF))) {
                        final Short diasParaAnexarArqNecessarios = Short.parseShort((String) paramSvcTO.getAttribute(Columns.PSE_VLR));
                        final Short numAnexosMin = Short.parseShort((String) paramSvcTO.getAttribute(Columns.PSE_VLR_REF));

                        if ((diasParaAnexarArqNecessarios != null) && (numAnexosMin != null) && (diasParaAnexarArqNecessarios.shortValue() > 0) && (numAnexosMin.shortValue() > 0)) {

                            final int total = editarAnexoConsignacaoController.countAnexoAutorizacaoDesconto(adeCodigo, responsavel);
                            final int numAnexosFaltantes = numAnexosMin.intValue() - total;

                            if (numAnexosFaltantes > 0) {
                                final CalendarioController calendarioController = ApplicationContextProvider.getApplicationContext().getBean(CalendarioController.class);
                                final List<Calendario> diasUteis = calendarioController.lstCalendariosAPartirDe(DateHelper.getSystemDate(), true, diasParaAnexarArqNecessarios.intValue());

                                final Date diaLimite = diasUteis.get(diasUteis.size() - 1).getCalData();
                                final Date horaMinutoAde = DateHelper.clearData((Date) autdes.getAttribute(Columns.ADE_DATA));
                                final String prazoString = DateHelper.format(diaLimite, LocaleHelper.getDatePattern()) + " " + DateHelper.format(horaMinutoAde, "HH:mm") + ":00";
                                final Date prazoAnexar = DateHelper.parse(prazoString, LocaleHelper.getDateTimePattern());

                                if (prazoAnexar.getTime() >= DateHelper.getSystemDatetime().getTime()) {
                                    parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage(MENSAGEM_SUCESSO, responsavel) + ". " + ApplicationResourcesHelper.getMessage("mensagem.alerta.reservar.margem.anexos.minimos", responsavel, Integer.valueOf(numAnexosMin).toString(), prazoString, Integer.toString(numAnexosFaltantes)));
                                }
                            }

                        }
                    }
                }
            } catch (AutorizacaoControllerException | CalendarioControllerException | ParseException | AgendamentoControllerException e) {
                parametros.put(MENSAGEM, ApplicationResourcesHelper.getMessage(MENSAGEM_SUCESSO, responsavel) + ". " + ApplicationResourcesHelper.getMessage("mensagem.erro.recuperar.anexos.contrato", responsavel));
            }
        }
    }

    private static boolean validaExtensao(String pathname, String[] lstExtensaoArq) {
        for (final String extensao : lstExtensaoArq) {
            if (pathname.toLowerCase().endsWith(extensao.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
