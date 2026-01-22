package com.zetra.econsig.job.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ServicoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.exception.ServicoControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.RelatorioTransfereAde;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;

/**
 * <p> Title: ProcessaRelatorioTransfereAde</p>
 * <p> Description: Classe para processamento de relatorios de transferência de contratos.</p>
 * <p> Copyright: Copyright (c) 2011 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioTransfereAde extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioTransfereAde.class);

    public ProcessaRelatorioTransfereAde(Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(new Relatorio("transf_contratos", ApplicationResourcesHelper.getMessage("rotulo.relatorio.transferencia.contratos", responsavel), "", RelatorioTransfereAde.class.getName(), null, "TransfereAde.jasper", "", "", "", "", "", true, false, "N", null), parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());
    }

    @Override
    protected void executar() {
        try {
            String path = ParamSist.getDiretorioRaizArquivos();
            HashMap<String, Object> parameters = new HashMap<>();

            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            ServicoDelegate svcDelegate = new ServicoDelegate();

            String csaCodigoOrigem = null;
            String csaCodigoDestino = null;
            String svcCodigoOrigem = null;
            String svcCodigoDestino = null;
            String orgCodigo = null;
            String strIniPeriodo = "";
            String strFimPeriodo = "";
            String paramIniPeriodo = "";
            String paramFimPeriodo = "";

            List<String> sadCodigo = null;
            List<Long> adeNumero = null;

            String rseMatricula = parameterMap.containsKey("rseMatricula") ? getParametro("rseMatricula", parameterMap) : null;
            String serCpf = parameterMap.containsKey("serCpf") ? getParametro("serCpf", parameterMap) : null;

            String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.transf.contratos", responsavel), responsavel, parameterMap, null);
            StringBuilder subtitulo = new StringBuilder();

            if (parameterMap.containsKey("csaCodigoOrigem")) {
                csaCodigoOrigem = getParametro("csaCodigoOrigem", parameterMap);
                if (TextHelper.isNull(csaCodigoOrigem)) {
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.origem.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase())).append(System.lineSeparator());
                } else {
                    try {
                        ConsignatariaTransferObject consignataria = csaDelegate.findConsignataria(csaCodigoOrigem, responsavel);
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.origem.arg0", responsavel, consignataria.getCsaNome())).append(System.lineSeparator());
                    } catch (ConsignatariaControllerException e) {
                        LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.aviso.transferencia.consignataria.origem.nao.encontrado", responsavel));
                    }
                }
            }
            if (parameterMap.containsKey("csaCodigoDestino")) {
                csaCodigoDestino = getParametro("csaCodigoDestino", parameterMap);
                if (TextHelper.isNull(csaCodigoDestino)) {
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.destino.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase())).append(System.lineSeparator());
                } else {
                    try {
                        ConsignatariaTransferObject consignataria = csaDelegate.findConsignataria(csaCodigoDestino, responsavel);
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.destino.arg0", responsavel, consignataria.getCsaNome())).append(System.lineSeparator());
                    } catch (ConsignatariaControllerException e) {
                        LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.aviso.transferencia.consignataria.destino.nao.encontrado", responsavel));
                    }
                }
            }
            if (parameterMap.containsKey("svcCodigoOrigem")) {
                svcCodigoOrigem = getParametro("svcCodigoOrigem", parameterMap);
                if (TextHelper.isNull(svcCodigoOrigem)) {
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servico.origem.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase())).append(System.lineSeparator());
                } else {
                    try {
                        TransferObject servico = svcDelegate.findServico(svcCodigoOrigem);
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servico.origem.arg0", responsavel, servico.getAttribute(Columns.SVC_DESCRICAO).toString())).append(System.lineSeparator());
                    } catch (ServicoControllerException e) {
                        LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.aviso.transferencia.servico.origem.nao.encontrado", responsavel));
                    }
                }
            }
            if (parameterMap.containsKey("svcCodigoDestino")) {
                svcCodigoDestino = getParametro("svcCodigoDestino", parameterMap);
                if (TextHelper.isNull(svcCodigoDestino)) {
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servico.destino.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase())).append(System.lineSeparator());
                } else {
                    try {
                        TransferObject servico = svcDelegate.findServico(svcCodigoDestino);
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servico.destino.arg0", responsavel, servico.getAttribute(Columns.SVC_DESCRICAO).toString())).append(System.lineSeparator());
                    } catch (ServicoControllerException e) {
                        LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.aviso.transferencia.servico.destino.nao.encontrado", responsavel));
                    }
                }
            }
            if (parameterMap.containsKey("orgCodigo")) {
                orgCodigo = getParametro("orgCodigo", parameterMap);
                if (TextHelper.isNull(orgCodigo)) {
                    subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase())).append(System.lineSeparator());
                } else {
                    try {
                        OrgaoTransferObject orgao = cseDelegate.findOrgao(orgCodigo, responsavel);
                        subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, orgao.getOrgNome())).append(System.lineSeparator());
                    } catch (ConsignanteControllerException e) {
                        LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.aviso.transferencia.orgao.nao.encontrado", responsavel));
                    }
                }
            }

            if (parameterMap.get("sadCodigo") != null) {
                sadCodigo = Arrays.asList(parameterMap.get("sadCodigo"));
            }

            if (parameterMap.get("adeNumero") != null) {
                List<String> adeNumeroList = Arrays.asList(parameterMap.get("adeNumero"));

                if (!adeNumeroList.isEmpty()) {
                    adeNumero = new ArrayList<>();
                    for (String numero : adeNumeroList) {
                        if (!TextHelper.isNull(numero)) {
                            if (!TextHelper.isNum(numero)) {
                                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.transf.contratos.ade.numero.invalido", responsavel));
                                return;
                            } else {
                                adeNumero.add(Long.valueOf(numero));
                            }
                        }
                    }
                }
            }

            if (parameterMap.containsKey("periodoIni") && parameterMap.containsKey("periodoFim")) {
                strIniPeriodo = getParametro("periodoIni", parameterMap);
                strFimPeriodo = getParametro("periodoFim", parameterMap);
                paramIniPeriodo = reformat(strIniPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 00:00:00");
                paramFimPeriodo = reformat(strFimPeriodo, LocaleHelper.getDatePattern(), "yyyy-MM-dd 23:59:59");
            } else {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
                return;
            }

            if (!TextHelper.isNull(rseMatricula)) {
                subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.arg0", responsavel, rseMatricula));
                subtitulo.append(System.lineSeparator());
            }

            if (!TextHelper.isNull(serCpf)) {
                subtitulo.append(ApplicationResourcesHelper.getMessage("rotulo.cpf.arg0", responsavel, serCpf));
                subtitulo.append(System.lineSeparator());
            }

            // No mínimo as consignatárias de origem e destino ou os serviços de origem e destino devem ser informados.
            if (!((!TextHelper.isNull(csaCodigoOrigem) && !TextHelper.isNull(csaCodigoDestino)) ||
                    (!TextHelper.isNull(svcCodigoOrigem) && !TextHelper.isNull(svcCodigoDestino)))) {
                session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.transferencia.servico.ou.consignataria.obrigatorio", responsavel));
                return;
            }

            String titulo = relatorio.getTitulo() + " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel, strIniPeriodo, strFimPeriodo);

            String reportName = null;
            try {
                criterio.setAttribute("csaCodigoOrigem", csaCodigoOrigem);
                criterio.setAttribute("csaCodigoDestino", csaCodigoDestino);
                criterio.setAttribute("svcCodigoOrigem", svcCodigoOrigem);
                criterio.setAttribute("svcCodigoDestino", svcCodigoDestino);
                criterio.setAttribute("orgCodigo", orgCodigo);
                criterio.setAttribute("sadCodigo", sadCodigo);
                criterio.setAttribute("periodoIni", paramIniPeriodo);
                criterio.setAttribute("periodoFim", paramFimPeriodo);
                criterio.setAttribute("adeNumero", adeNumero);
                criterio.setAttribute("rseMatricula", rseMatricula);
                criterio.setAttribute("serCpf", serCpf);
                criterio.setAttribute("somenteConveniosAtivos", Boolean.TRUE);

                String strFormato = "PDF";
                parameterMap.put("formato", new String[]{strFormato});

                String fileName = path + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "consignacoes";

                parameters.put(ReportManager.REPORT_DIR_EXPORT, fileName);
                parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
                parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
                parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
                parameters.put(ReportManager.REPORT_FILE_NAME, nome);
                parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
                parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subtitulo.toString());
                parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
                parameters.put(ReportManager.PARAM_RESPONSAVEL, responsavel);

                ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
                reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

                String reportNameZip = geraZip(nome.toString(), reportName);

                setMensagem(reportNameZip, relatorio.getTipo(), relatorio.getTitulo(), session);

                // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
                enviaEmail(reportNameZip);

            } catch (ReportControllerException ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(mensagem, ex);
            } catch (Exception ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(mensagem, ex);
            }

            /*
             * Seta na sessão a mensagem do processamento do relatório.
             * A mensagem está sendo setada aqui porque esse relatório não é controlado pela classe ControladorProcessos.
             *
             */
            if (session != null) {
                if (getCodigoRetorno() == Processo.SUCESSO) {
                    session.setAttribute(CodedValues.MSG_INFO, getMensagem());
                } else {
                    session.setAttribute(CodedValues.MSG_ERRO, getMensagem());
                }
            }
        } catch (ConsignatariaControllerException | ServicoControllerException ex) {
            LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.interno.sistema.motivo.arg0", responsavel, ex.getMessage()));
        }
    }
}
