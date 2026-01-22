package com.zetra.econsig.job.process;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.ParametroAgendamento;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.report.reports.ReportTemplate;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.AgendamentoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;

/**
 * <p> Title: ProcessaRelatorioAuditoria</p>
 * <p> Description: Classe para processamento de relatorios de auditoria de lote</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public final class ProcessaRelatorioAuditoria extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioAuditoria.class);

    private final boolean moduloAuditoria;

    private final String PERIODICIDADE_ENVIO_EMAIL_CSE_ORG;
    private final String PERIODICIDADE_ENVIO_EMAIL_CSA_COR;
    private final String PERIODICIDADE_ENVIO_EMAIL_SUP;

    public ProcessaRelatorioAuditoria(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, AcessoSistema responsavel) {
        this(relatorio, parameterMap, session, false, responsavel);
    }

    //DESENV-15723 - O parâmetro boleano "agendado", foi setado como "true" para que o relatório de auditoria gerado manualmente, também
    //seja enviado por email.
    public ProcessaRelatorioAuditoria(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, boolean moduloAuditoria, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, true, responsavel);

        this.moduloAuditoria = moduloAuditoria;

        PERIODICIDADE_ENVIO_EMAIL_CSE_ORG = recuperaPeriodicidade(CodedValues.TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_CSE_ORG, responsavel);
        PERIODICIDADE_ENVIO_EMAIL_CSA_COR = recuperaPeriodicidade(CodedValues.TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_CSA_COR, responsavel);
        PERIODICIDADE_ENVIO_EMAIL_SUP = recuperaPeriodicidade(CodedValues.TPC_PERIODO_ENVIO_EMAIL_AUDITORIA_SUP, responsavel);
    }

    private String recuperaPeriodicidade(String parametro, AcessoSistema responsavel) {
        final ParamSist paramSist = ParamSist.getInstance();
        String retorno = !TextHelper.isNull(paramSist.getParam(parametro, responsavel)) ? paramSist.getParam(parametro, responsavel).toString() : "";

        if (!retorno.equalsIgnoreCase(CodedValues.PER_ENV_EMAIL_AUDIT_DIARIO) &&
                !retorno.equalsIgnoreCase(CodedValues.PER_ENV_EMAIL_AUDIT_SEMANAL) &&
                !retorno.equalsIgnoreCase(CodedValues.PER_ENV_EMAIL_AUDIT_MENSAL)) {
            retorno = "";
        }

        return retorno;
    }

    @Override
    protected void executar() {
        if (moduloAuditoria) {
            try {
                final ParametroDelegate paramDelegate = new ParametroDelegate();
                final UsuarioDelegate usuarioDelegate = new UsuarioDelegate();

                final Map<String, String> paramAgdData = new HashMap<>();
                final Map<String, List<TransferObject>> lstUsuAuditorEnt = usuarioDelegate.lstUsuarioAuditorEntidade(responsavel);
                for (final String chave : lstUsuAuditorEnt.keySet()) {
                    final String tipoEntidade = chave.split("\\|")[0];
                    final String codigoEntidade = chave.split("\\|")[1];
                    final List<String> emails = new ArrayList<>();
                    final List<TransferObject> usuarios = lstUsuAuditorEnt.get(chave);
                    for (final TransferObject to : usuarios) {
                        emails.add(to.getAttribute(Columns.USU_EMAIL).toString());
                    }

                    final boolean isCseOrg = tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE) || tipoEntidade.equals(AcessoSistema.ENTIDADE_ORG);
                    final boolean isSup = tipoEntidade.equals(AcessoSistema.ENTIDADE_SUP);
                    final boolean isCsaCor = tipoEntidade.equals(AcessoSistema.ENTIDADE_CSA) || tipoEntidade.equals(AcessoSistema.ENTIDADE_COR);

                    final List<String> emailList = emails.stream().distinct().collect(Collectors.toList());

                    String periodicidade = "";
                    if (isCseOrg) {
                        periodicidade = PERIODICIDADE_ENVIO_EMAIL_CSE_ORG;
                    } else if (isCsaCor) {
                        periodicidade = PERIODICIDADE_ENVIO_EMAIL_CSA_COR;
                    } else if (isSup) {
                        periodicidade = PERIODICIDADE_ENVIO_EMAIL_SUP;
                    }
                    if (TextHelper.isNull(periodicidade)) {
                        throw new RuntimeException(ApplicationResourcesHelper.getMessage("mensagem.erro.nao.possivel.gerar.relatorio.auditoria.periodicidade.nao.setada", responsavel));
                    }

                    // Verifica quais entidades possuem funções configuradas para serem auditadas, e gerar um relatório por entidade.
                    final List<TransferObject> listFunAudTO = usuarioDelegate.findFuncaoAuditavelPorEntidade(codigoEntidade, tipoEntidade, responsavel);
                    List <String> funcoesAuditaveis = null;
                    if (listFunAudTO != null && !listFunAudTO.isEmpty()) {
                        funcoesAuditaveis = new ArrayList<>();
                        for (final TransferObject element : listFunAudTO) {
                            funcoesAuditaveis.add(element.getAttribute(Columns.FUN_CODIGO).toString());
                        }
                    }

                    // Setar os parâmetros para gerar o relatório
                    String data_fim = "";
                    if (isCseOrg) {
                        data_fim = "data_fim_cse_org";
                    } else if (isCsaCor) {
                        data_fim = "data_fim_csa_cor";
                    } else if (isSup) {
                        data_fim = "data_fim_sup";
                    }
                    final String dtFim = getDataFimUltRelAuditoria(data_fim);

                    final int dias = recuperaDiasPeriodo(periodicidade);
                    String periodoIni = "";
                    String periodoFim = "";
                    if (!TextHelper.isNull(dtFim)) {
                        final Date dataIni = DateHelper.parse(dtFim, "yyyy-MM-dd HH:mm:ss");
                        final Calendar cal = Calendar.getInstance();
                        cal.setTime(dataIni);
                        cal.add(Calendar.DAY_OF_MONTH, 1);
                        periodoIni = DateHelper.format(cal.getTime(), "yyyy-MM-dd 00:00:00");
                        cal.add(Calendar.DAY_OF_MONTH, dias-1);
                        periodoFim = DateHelper.format(cal.getTime(), "yyyy-MM-dd 23:59:59");
                    } else {
                        // Caso o calculo do período fim seja alterado, alterar também o método #getDataFimUltRelAuditoria
                        final Calendar cal = Calendar.getInstance();
                        cal.add(Calendar.DAY_OF_MONTH, -1);
                        periodoFim = DateHelper.format(cal.getTime(), "yyyy-MM-dd 23:59:59");
                        cal.add(Calendar.DAY_OF_MONTH, -1*dias);
                        periodoIni = DateHelper.format(cal.getTime(), "yyyy-MM-dd 00:00:00");
                    }

                    // Valida se deve ser gerado o relatório de auditoria para a entidade
                    final Date datePeriodoFim = DateHelper.parse(periodoFim, "yyyy-MM-dd HH:mm:ss");
                    if (datePeriodoFim.compareTo(DateHelper.getSystemDatetime()) > 0) {
                        continue;
                    }

                    // Gera Relatório de Auditoria
                    final String reportName = geraRelatorioAuditoria(codigoEntidade, tipoEntidade, periodoIni, periodoFim, funcoesAuditaveis);

                    // Salva os parâmetros de agendamento para posterior atualização
                    paramAgdData.put(data_fim, periodoFim);

                    try {
                        // Enviar e-mail para usuários auditores
                        EnviaEmailHelper.enviarEmailRelatorioAuditoria(reportName, codigoEntidade, tipoEntidade, emailList, responsavel);
                    } catch (final Exception ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }

                for (final String pagNome : paramAgdData.keySet()) {
                    final String pagValor = paramAgdData.get(pagNome);
                    paramDelegate.atualizaParamAgendamento(AgendamentoEnum.RELATORIO_AUDITORIA.getCodigo(), pagNome, pagValor, responsavel);
                }
            } catch (final UsuarioControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new RuntimeException(ex.getMessage(), ex);
            } catch (final ParseException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.nao.possivel.recuperar.funcoes.auditaveis.para.gerar.relatorio.auditoria", responsavel), ex);
                throw new RuntimeException(ApplicationResourcesHelper.getMessage("mensagem.erro.nao.possivel.recuperar.funcoes.auditaveis.para.gerar.relatorio.auditoria", responsavel), ex);
            } catch (final ParametroControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new RuntimeException(ex.getMessage(), ex);
            }

        } else {
            geraRelatorioAuditoria();
        }
    }

    private String getDataFimUltRelAuditoria(String data_fim) throws ParametroControllerException {
        final ParametroDelegate paramDelegate = new ParametroDelegate();
        String dtFim = null;
        try {
            // Caso esse método seja alterado, verificar o cálculo do período na execução processo
            final Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -1);
            paramDelegate.findParamAgendamento(AgendamentoEnum.RELATORIO_AUDITORIA.getCodigo(), data_fim, DateHelper.format(cal.getTime(), "yyyy-MM-dd 23:59:59"), responsavel);
        } catch (final ParametroControllerException ex) {
            try {
                final List<ParametroAgendamento> lstParamAgd = paramDelegate.findParamAgendamento(AgendamentoEnum.RELATORIO_AUDITORIA.getCodigo(), data_fim, responsavel);
                if (lstParamAgd.size() > 1) {
                    throw new ParametroControllerException("mensagem.erro.mais.de.um.parametro.encontrado", responsavel);
                }
                dtFim = lstParamAgd.get(0).getPagValor();
            } catch (final ParametroControllerException e) {
                LOG.warn("Não foi possível recuperar o parâmetro da data final do último período do relatório de auditoria gerado.");
            }
        }
        return dtFim;
    }

    private int recuperaDiasPeriodo(String periodicidade) {
        int dias = 0;
        if (periodicidade.equals(CodedValues.PER_ENV_EMAIL_AUDIT_DIARIO)) {
            dias = 1;
        } else if (periodicidade.equals(CodedValues.PER_ENV_EMAIL_AUDIT_SEMANAL)) {
            dias = 7;
        } else if (periodicidade.equals(CodedValues.PER_ENV_EMAIL_AUDIT_MENSAL)) {
            dias = 30;
        }
        return dias;
    }

    private String geraRelatorioAuditoria() {
        return geraRelatorioAuditoria(null, null, null, null, null);
    }

    private String geraRelatorioAuditoria(String codigoEntidadeUsuario, String tipoEntidadeUsuario, String periodoIni, String periodoFim, List<String> funcoesAuditaveis) {
        String reportName = null;
        if (!(parameterMap.containsKey("periodoIni") && parameterMap.containsKey("periodoFim")) &&
                (TextHelper.isNull(periodoIni) || TextHelper.isNull(periodoFim))) {
            if (session != null) {
                session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
            } else {
                throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.periodo.para.geracao.relatorio.nao.informado", responsavel));
            }
        } else {
            final String dataInicio = !TextHelper.isNull(periodoIni) ? periodoIni : reformat(getParametro("periodoIni", parameterMap), LocaleHelper.getDatePattern(), "yyyy-MM-dd") + " " + getParametro("horaIni", parameterMap);
            final String dataFim = !TextHelper.isNull(periodoFim) ? periodoFim : reformat(getParametro("periodoFim", parameterMap), LocaleHelper.getDatePattern(), "yyyy-MM-dd") + " " + getParametro("horaFim", parameterMap);

            List<String> funCodigo = new ArrayList<>();
            if (parameterMap.get("funCodigo") != null) {
                final String [] fun = parameterMap.get("funCodigo");
                if (!TextHelper.isNull(fun[0])) {
                    funCodigo = Arrays.asList(fun);
                }
            }
            if (funcoesAuditaveis != null && !funcoesAuditaveis.isEmpty()) {
                funCodigo = funcoesAuditaveis;
            }

            final String tipoOperador = getParametro("tipoOperador", parameterMap);
            final String operador = getParametro("operador", parameterMap);
            final String tipoEntidade = getParametro("tipoEntidade", parameterMap);
            String entidade = getParametro("entidade", parameterMap);
            final String tloCodigo = getParametro("tipoLog", parameterMap);

            //Critérios utilizados para a geração da SQL que irá recuperar os dados do relatório
            criterio.setAttribute("DATA_INI", dataInicio);
            criterio.setAttribute("DATA_FIM", dataFim);
            criterio.setAttribute("TIPO_OPERADOR", tipoOperador);
            criterio.setAttribute("OPERADOR", operador);
            criterio.setAttribute("TEN_CODIGO", tipoEntidade);
            criterio.setAttribute("ENTIDADE", entidade);
            criterio.setAttribute("TLO_CODIGO", tloCodigo);
            criterio.setAttribute("FUN_CODIGO", funCodigo);
            criterio.setAttribute("CODIGO_ENTIDADE_USUARIO", !TextHelper.isNull(codigoEntidadeUsuario) ? codigoEntidadeUsuario : responsavel.isCsa() ? responsavel.getCodigoEntidade() : null);
            criterio.setAttribute("TIPO_ENTIDADE_USUARIO", !TextHelper.isNull(tipoEntidadeUsuario) ? tipoEntidadeUsuario : responsavel.isCsa() ? responsavel.getTipoEntidade() : null );
            criterio.setAttribute("MODULO_AUDITORIA", moduloAuditoria);

            //Definição dos parâmetros definidos no arquivo iReports (.jasper)
            String periodoInicio = dataInicio;
            String periodoFinal = dataFim;
            try {
                periodoInicio = DateHelper.reformat(dataInicio, "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                periodoFinal = DateHelper.reformat(dataFim, "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
            } catch (final ParseException e) {
                LOG.error(e.getMessage(), e);
            }

            // CONSTROI NOME DO ARQUIVO NO FORMATO: relatorio_periodo_dataHora
            final String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.auditoria", responsavel), responsavel, parameterMap, null);

            List<TransferObject> lstTipoEntidade = null;
            List<TransferObject> lstTipoLog = null;
            try {
                final LogDelegate logDelegate = new LogDelegate();
                lstTipoEntidade = logDelegate.lstTiposEntidadesAuditoria(responsavel);
                lstTipoLog = logDelegate.lstTiposLog();
            } catch (final LogControllerException e) {
                lstTipoEntidade = new ArrayList<>();
                lstTipoLog = new ArrayList<>();
                LOG.warn("Não foi possível recuperar informações do log.", e);
            }

            final StringBuilder titulo = new StringBuilder(relatorio.getTitulo());
            if (moduloAuditoria) {
                if (tipoEntidadeUsuario.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSE)) {
                    titulo.append(" - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel));
                } else if (tipoEntidadeUsuario.equalsIgnoreCase(AcessoSistema.ENTIDADE_SUP)) {
                    titulo.append(" - ").append(ApplicationResourcesHelper.getMessage("rotulo.suporte.singular", responsavel));
                } else if (tipoEntidadeUsuario.equalsIgnoreCase(AcessoSistema.ENTIDADE_CSA)) {
                    titulo.append(" - ").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel));
                } else if (tipoEntidadeUsuario.equalsIgnoreCase(AcessoSistema.ENTIDADE_COR)) {
                    titulo.append(" - ").append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel));
                }
            }

            final StringBuilder subtitulo = new StringBuilder();
            if (!TextHelper.isNull(tipoOperador)) {
                if (tipoOperador.equalsIgnoreCase("CSE")) {
                    subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignante.singular.arg0", responsavel, operador));
                } else if (tipoOperador.equalsIgnoreCase("CSA")) {
                    subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, operador));
                } else if (tipoOperador.equalsIgnoreCase("COR")) {
                    subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, operador));
                } else if (tipoOperador.equalsIgnoreCase("SER")) {
                    subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular.arg0", responsavel, operador));
                } else if (tipoOperador.equalsIgnoreCase("USU")) {
                    subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.usuario.singular.arg0", responsavel, operador));
                } else if (TextHelper.isNull(tipoOperador)) {
                    subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.operador.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel)));
                }
            }

            TransferObject to = null;
            if (!TextHelper.isNull(tipoEntidade) && !TextHelper.isNull(entidade)) {
                for (final TransferObject element : lstTipoEntidade) {
                    to = element;
                    if (to.getAttribute(Columns.TEN_CODIGO).equals(tipoEntidade)) {
                        subtitulo.append(System.getProperty("line.separator")).append(to.getAttribute(Columns.TEN_TITULO).toString()).append(": ").append(entidade.toUpperCase());
                        break;
                    }
                }
            } else {
                subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.entidade.operada.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel)));
            }

            if (!TextHelper.isNull(tloCodigo)) {
                for (final TransferObject element : lstTipoLog) {
                    to = element;
                    if (to.getAttribute(Columns.TLO_CODIGO).equals(tloCodigo)) {
                        subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.log.arg0", responsavel, to.getAttribute(Columns.TLO_DESCRICAO).toString()));
                        break;
                    }
                }
            } else {
                subtitulo.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.tipo.log.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel)));
            }

            // TODO Incluir Operações no subtitulo?

            final HashMap<String, Object> parameters = new HashMap<>();

            // Se a entidade é consignatária, gera o relatório no diretório da entidade
            String dirRelatorioCsa = "";
            if (!TextHelper.isNull(tipoEntidadeUsuario) && tipoEntidadeUsuario.equals(AcessoSistema.ENTIDADE_CSA)) {
                try {
                    final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                    csaDelegate.findConsignataria(codigoEntidadeUsuario, responsavel);
                } catch (final ConsignatariaControllerException e) {
                    LOG.warn("Não foi possível recuperar o identificador da consignatária.");
                }

                dirRelatorioCsa = geraDirExportRelatorioCsa(codigoEntidadeUsuario, tipoEntidadeUsuario);
                parameters.put(ReportManager.REPORT_DIR_EXPORT, dirRelatorioCsa);
            }

            final String estCodigo = getFiltroEstCodigo(parameterMap, subtitulo, null, session, responsavel);
            final List<String> orgCodigo = getFiltroOrgCodigoIn(parameterMap, subtitulo, null, session, responsavel);
            final String rseMatricula = getFiltroRseMatricula(parameterMap, subtitulo, null, session, responsavel);
            final String logCanal = getFiltroCanal(parameterMap, subtitulo, null, session, responsavel);

            final String papCodigo = getParametro("papel", parameterMap);
            final String somenteFuncoesSensiveis = getFiltroSomenteFuncoesSensiveis(parameterMap, subtitulo, null, session, responsavel);

            criterio.setAttribute("EST_CODIGO", estCodigo);
            criterio.setAttribute("ORG_CODIGO", orgCodigo);
            criterio.setAttribute("RSE_MATRICULA", rseMatricula);
            criterio.setAttribute("LOG_CANAL", logCanal);
            criterio.setAttribute("PAP_CODIGO", papCodigo);
            criterio.setAttribute("SOMENTE_FUNCOES_SENSIVEIS", somenteFuncoesSensiveis);

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_PERIODO_INICIO, periodoInicio);
            parameters.put(ReportManager.PARAM_NAME_PERIODO_FIM, periodoFinal);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo.toString());
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
            parameters.put(ReportManager.REPORT_FILE_NAME, nome);
            parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);
            parameters.put(ReportManager.PARAM_FUSO_HORARIO, getFusoHorario());

            try {
                final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
                reportName = reportController.makeReport(parameterMap.containsKey("formato") ? getStrFormato() : "PDF", criterio, parameters, relatorio, responsavel);

                if (moduloAuditoria) {
                    try {
                        final ReportTemplate _report = (ReportTemplate) Class.forName(relatorio.getClasseReport()).getDeclaredConstructor().newInstance();
                        _report.setResponsavel(responsavel);
                        _report.setRelatorio(relatorio);

                        String reportNameZip = null;
                        if (!TextHelper.isNull(dirRelatorioCsa)) {
                            reportNameZip = dirRelatorioCsa + File.separatorChar + nome + ".zip";
                        } else {
                            reportNameZip = _report.getPath() + File.separatorChar + nome + ".zip";
                        }

                        FileHelper.zip(reportName, reportNameZip);
                        FileHelper.delete(reportName);

                        reportName = reportNameZip;
                    } catch (final Exception e) {
                        LOG.error("Não foi possível localizar o diretório do relatório de auditoria.", e);
                    }
                } else {
                    String path = getPath(responsavel);
                    if (path != null) {
                        entidade = getEntidade(responsavel);
                        path += File.separatorChar + "relatorio" + File.separatorChar
                                + entidade + File.separatorChar + relatorio.getTipo();

                        if (!responsavel.isCseSup()) {
                            path += File.separatorChar + responsavel.getCodigoEntidade();
                        }

                        final String fileZip = path + File.separatorChar + nome + ".zip";
                        FileHelper.zip(reportName, fileZip);
                        FileHelper.delete(reportName);

                        setMensagem(fileZip, relatorio.getTipo(), titulo.toString(), session);

                        // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
                        enviaEmail(fileZip);
                    }
                }

            } catch (final ReportControllerException ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(ex.getMessage(), ex);
            } catch (final Exception ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(mensagem, ex);
            }
        }
        return reportName;
    }

    private String geraDirExportRelatorioCsa(String codigoEntidade, String tipoEntidade) {
        String path = null;
        if (relatorio != null) {
            path = ParamSist.getDiretorioRaizArquivos();
            path += File.separatorChar + "relatorio" + File.separatorChar + tipoEntidade.toLowerCase();
            path += File.separatorChar + relatorio.getTipo();
            path += File.separatorChar + codigoEntidade;

            // Garante que existirão os diretórios especificados pelo path.
            new File(path).mkdirs();
        }
        return path;
    }

    private String getFusoHorario() {
        Map<String, String> regionMap = new HashMap<>();
        regionMap.put("America/Sao_Paulo", "São Paulo, SP");
 
        ZoneId zoneId = ZoneId.systemDefault();
       
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
       
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("O");
        String formattedZone = zonedDateTime.format(formatter);
       
        String regionName = regionMap.getOrDefault(zoneId.toString(), zoneId.toString());
       
        return regionName + ", " + formattedZone;
    }
}
