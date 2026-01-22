package com.zetra.econsig.job.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpSession;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ReportControllerException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.compra.MontaCriterioAcompanhamentoCompra;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaRelatorioCompraContrato</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioCompraContrato extends ProcessaRelatorio {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioCompraContrato.class);

    private static final String SUFIXO_PENDENCIA_INF_SALDO  = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.compra.contrato.sufixo.sem.informacao.saldo", (AcessoSistema) null).trim().replaceAll(" ", "_");
    private static final String SUFIXO_PENDENCIA_APR_SALDO  = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.compra.contrato.sufixo.sem.aprovacao.saldo", (AcessoSistema) null).trim().replaceAll(" ", "_");
    private static final String SUFIXO_PENDENCIA_PGT_SALDO  = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.compra.contrato.sufixo.sem.pagamento.saldo", (AcessoSistema) null).trim().replaceAll(" ", "_");
    private static final String SUFIXO_PENDENCIA_LIQUIDACAO = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.compra.contrato.sufixo.sem.liquidacao", (AcessoSistema) null).trim().replaceAll(" ", "_");

    private final boolean usaDiasUteis;

    public ProcessaRelatorioCompraContrato(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        // Seta a descrição do processo
        setDescricao(relatorio.getTitulo());

        usaDiasUteis = ParamSist.paramEquals(CodedValues.TPC_USA_DIAS_UTEIS_CONTROLE_COMPRA, CodedValues.TPC_SIM, this.responsavel);
    }

    @Override
    protected void executar() {
        String strIniPeriodo = parameterMap.containsKey("periodoIni") ? getParametro("periodoIni", parameterMap) : null;
        String strFimPeriodo = parameterMap.containsKey("periodoFim") ? getParametro("periodoFim", parameterMap) : null;

        if (TextHelper.isNull(strIniPeriodo) || TextHelper.isNull(strFimPeriodo)) {
            LOG.error("Período não informado para a geração do relatório.");
            session.setAttribute(CodedValues.MSG_INFO, ApplicationResourcesHelper.getMessage("mensagem.informe.periodo.para.geracao.relatorio", responsavel));
        } else {
            String csaCodigo = "";
            String corCodigo = "";
            List<String> orgCodigos = null;
            List<String> orgNames = null;

            // Recupera os parâmetros selecionados pelo usuário.
            String filtroConfiguravel = parameterMap.containsKey("filtroConfiguravel") ? getParametro("filtroConfiguravel", parameterMap) : "";
            String cpf = parameterMap.containsKey("CPF") ? getParametro("CPF", parameterMap) : null;
            String matricula = parameterMap.containsKey("RSE_MATRICULA") ? getParametro("RSE_MATRICULA", parameterMap) : null;
            String origem = parameterMap.containsKey("origem") ? getParametro("origem", parameterMap) : null;
            String temSaldoDevedor = parameterMap.containsKey("temSaldoDevedor") ? getParametro("temSaldoDevedor", parameterMap) : null;
            String diasSemSaldoDevedor = parameterMap.containsKey("diasSemSaldoDevedor") ? getParametro("diasSemSaldoDevedor", parameterMap) : null;
            String saldoDevedorAprovado = parameterMap.containsKey("saldoDevedorAprovado") ? getParametro("saldoDevedorAprovado", parameterMap) : null;
            String diasSemAprovacaoSaldoDevedor = parameterMap.containsKey("diasSemAprovacaoSaldoDevedor") ? getParametro("diasSemAprovacaoSaldoDevedor", parameterMap) : null;
            String saldoDevedorPago = parameterMap.containsKey("saldoDevedorPago") ? getParametro("saldoDevedorPago", parameterMap) : null;
            String diasSemPagamentoSaldoDevedor = parameterMap.containsKey("diasSemPagamentoSaldoDevedor") ? getParametro("diasSemPagamentoSaldoDevedor", parameterMap) : null;
            String liquidado = parameterMap.containsKey("liquidado") ? getParametro("liquidado", parameterMap) : null;
            String diasSemLiquidacao = parameterMap.containsKey("diasSemLiquidacao") ? getParametro("diasSemLiquidacao", parameterMap) : null;
            String diasBloqueio = parameterMap.containsKey("diasBloqueio") ? getParametro("diasBloqueio", parameterMap) : null;

            String nome = getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.compra.contrato", responsavel), responsavel, parameterMap, null);

            String tituloRelatorio = relatorio.getTitulo();
            if (!TextHelper.isNull(strIniPeriodo) && !TextHelper.isNull(strFimPeriodo)) {
                tituloRelatorio += " - " + ApplicationResourcesHelper.getMessage("rotulo.periodo.de.arg0.a.arg1", responsavel,  strIniPeriodo, strFimPeriodo);
            }

            StringBuilder subTituloRelatorio = new StringBuilder("");

            // consignataria
            if (responsavel.isCseSupOrg()) {
                if (parameterMap.containsKey("csaCodigo")) {
                    String values[] = (parameterMap.get("csaCodigo"));
                    if (values.length == 0 || values[0].equals("")) {
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todas.simples", responsavel).toUpperCase()));
                    } else {
                        values = values[0].split(";");
                        csaCodigo = values[0];
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, values[2]));
                    }
                }
            } else { // subtitulo depende se o responsavel é csa/cor
                String descricao = responsavel.getNomeEntidade();
                csaCodigo = responsavel.getCsaCodigo();
                if (responsavel.isCsa()) {
                    subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular.arg0", responsavel, descricao));
                } else {
                    subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, descricao));
                    if (!responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                        // Se correspondente nao puder acessar os contratos da csa, entao filtra
                        corCodigo = responsavel.getCorCodigo();
                    }
                }
            }

            // orgao
            if ((!responsavel.isOrg() || (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)))) {
                if (parameterMap.containsKey("orgCodigo")) {
                    String[] values = (parameterMap.get("orgCodigo"));
                    if (values[0].equals("")) {
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                    } else {
                        orgCodigos = new ArrayList<>();
                        orgNames = new ArrayList<>();
                        try {
                            for (final String value : values) {
                                String[] separ = value.split(";");
                                orgCodigos.add(separ[0]);
                                orgNames.add(separ[2] + " ");
                            }
                            subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, String.valueOf(orgNames).replace("[", "").replace("]", "")));
                        } catch (final Exception ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    }
                }
            } else { // nome do orgao deve ser adicionado no subtitulo
                orgCodigos = new ArrayList<>();
                orgCodigos.add(responsavel.getOrgCodigo());
                String descricao = responsavel.getNomeEntidade();
                subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular.arg0", responsavel, descricao));
            }

            // Correspondente
            if (parameterMap.containsKey("corCodigo")) {
                String correspondentes[] = (parameterMap.get("corCodigo"));
                if (!correspondentes[0].equals("")) {
                    String values[] = correspondentes[0].split(";");
                    corCodigo = values[0];
                    String corCodigoSessao = responsavel.getCorCodigo();
                    if (!responsavel.isCor() ||
                            (responsavel.isCor() && (!TextHelper.isNull(corCodigoSessao) && !corCodigo.equals(corCodigoSessao)))) {
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, values[2]));
                    }
                } else {
                    subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.campo.todos.simples", responsavel).toUpperCase()));
                }
            }

            if (!TextHelper.isNull(matricula)) {
                subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula.arg0", responsavel, matricula));
            }

            if (!TextHelper.isNull(cpf)) {
                if (TextHelper.isNull(matricula)) {
                    subTituloRelatorio.append(System.getProperty("line.separator"));
                } else {
                    subTituloRelatorio.append(" - ");
                }
                subTituloRelatorio.append(ApplicationResourcesHelper.getMessage("rotulo.cpf.arg0", responsavel, cpf));
            }

            criterio.setAttribute("periodoIni", !TextHelper.isNull(strIniPeriodo) ? strIniPeriodo : null);
            criterio.setAttribute("periodoFim", !TextHelper.isNull(strFimPeriodo) ? strFimPeriodo : null);
            criterio.setAttribute(Columns.CSA_CODIGO, csaCodigo);
            criterio.setAttribute(Columns.COR_CODIGO, corCodigo);
            criterio.setAttribute(Columns.ORG_CODIGO, orgCodigos);
            criterio.setAttribute(Columns.SER_CPF, cpf);
            criterio.setAttribute(Columns.RSE_MATRICULA, matricula);

            try {
                String path = getPath(responsavel);
                if (path == null) {
                    session.setAttribute(CodedValues.MSG_ERRO, ApplicationResourcesHelper.getMessage("mensagem.erro.interno.contate.administrador", responsavel));
                } else {
                    String entidade = getEntidade(responsavel);

                    path += File.separatorChar + "relatorio" + File.separatorChar
                            + entidade + File.separatorChar + relatorio.getTipo();

                    if (!responsavel.isCseSup()) {
                        path += File.separatorChar + responsavel.getCodigoEntidade();
                    }
                }

                if (filtroConfiguravel.equals("0")) {
                    criterio.setAttribute("origem", origem);
                    criterio.setAttribute("temSaldoDevedor", temSaldoDevedor);
                    criterio.setAttribute("diasSemSaldoDevedor", diasSemSaldoDevedor);
                    criterio.setAttribute("saldoDevedorAprovado", saldoDevedorAprovado);
                    criterio.setAttribute("diasSemAprovacaoSaldoDevedor", diasSemAprovacaoSaldoDevedor);
                    criterio.setAttribute("saldoDevedorPago", saldoDevedorPago);
                    criterio.setAttribute("diasSemPagamentoSaldoDevedor", diasSemPagamentoSaldoDevedor);
                    criterio.setAttribute("liquidado", liquidado);
                    criterio.setAttribute("diasSemLiquidacao", diasSemLiquidacao);

                    if (!TextHelper.isNull(criterio.getAttribute("origem"))) {
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.origem.singular.arg0", responsavel, criterio.getAttribute("origem").equals("0") ? ApplicationResourcesHelper.getMessage("rotulo.contratos.comprados.terceiros", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.contratos.comprados", responsavel)));
                    }
                    if (!TextHelper.isNull(criterio.getAttribute("temSaldoDevedor"))) {
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.informado.arg0", responsavel, criterio.getAttribute("temSaldoDevedor").toString()));
                    }
                    if (!TextHelper.isNull(criterio.getAttribute("diasSemSaldoDevedor"))) {
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage(usaDiasUteis ? "rotulo.dias.uteis.sem.informacao.saldo.devedor.arg0" : "rotulo.dias.sem.informacao.saldo.devedor.arg0", responsavel, criterio.getAttribute("diasSemSaldoDevedor").toString()));
                    }
                    if (!TextHelper.isNull(criterio.getAttribute("saldoDevedorAprovado"))) {
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.aprovado.arg0", responsavel, criterio.getAttribute("saldoDevedorAprovado").toString()));
                    }
                    if (!TextHelper.isNull(criterio.getAttribute("diasSemAprovacaoSaldoDevedor"))) {
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage(usaDiasUteis ? "rotulo.dias.uteis.sem.aprovacao.saldo.devedor.arg0" : "rotulo.dias.sem.aprovacao.saldo.devedor.arg0", responsavel, criterio.getAttribute("diasSemAprovacaoSaldoDevedor").toString()));
                    }
                    if (!TextHelper.isNull(criterio.getAttribute("saldoDevedorPago"))) {
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.saldo.devedor.informado.como.pago.arg0", responsavel, criterio.getAttribute("saldoDevedorPago").toString()));
                    }
                    if (!TextHelper.isNull(criterio.getAttribute("diasSemPagamentoSaldoDevedor"))) {
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage(usaDiasUteis ? "rotulo.dias.uteis.sem.informacao.pagamento.saldo.devedor.arg0" : "rotulo.dias.sem.informacao.pagamento.saldo.devedor.arg0", responsavel, criterio.getAttribute("diasSemPagamentoSaldoDevedor").toString()));
                    }
                    if (!TextHelper.isNull(criterio.getAttribute("liquidado"))) {
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.contrato.liquidado.arg0", responsavel, criterio.getAttribute("liquidado").toString()));
                    }
                    if (!TextHelper.isNull(criterio.getAttribute("diasSemLiquidacao"))) {
                        subTituloRelatorio.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage(usaDiasUteis ? "rotulo.dias.uteis.sem.liquidacao.contrato.arg0" : "rotulo.dias.sem.liquidacao.contrato.arg0", responsavel, criterio.getAttribute("diasSemLiquidacao").toString()));
                    }

                    gerarRelatorio(criterio, nome, tituloRelatorio, subTituloRelatorio.toString());

                    String fileZip = path + File.separatorChar + nome + ".zip";
                    String fileName = nome + "." + (getStrFormato().equalsIgnoreCase("TEXT")? "txt" : getStrFormato().toLowerCase());
                    String file = path + File.separatorChar + fileName;
                    FileHelper.zip(file, fileZip);
                    FileHelper.delete(file);

                    // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
                    enviaEmail(fileZip);

                    setMensagem(fileZip, relatorio.getTipo(), relatorio.getTitulo(), session);

                } else if (filtroConfiguravel.equals("1")) {
                    // Se o filtro é contratos com pendência, gera um relatório para cada tipo de pendência.
                    // Compacta os relatórios em um só arquivo.

                    // 1) Contratos com pendência de informação de saldo devedor.
                    CustomTransferObject criterioPendenciaInfoSaldoDevedor = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaInfoSaldoDevedor(criterio);
                    String nomeArqPendenciaInfoSaldoDevedor = nome + "_" + SUFIXO_PENDENCIA_INF_SALDO;
                    StringBuilder subTituloPendenciaInfoSaldoDevedor = new StringBuilder(subTituloRelatorio.toString());
                    subTituloPendenciaInfoSaldoDevedor.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.pendencia.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.informacao.saldo.devedor", responsavel)));
                    gerarRelatorio(criterioPendenciaInfoSaldoDevedor, nomeArqPendenciaInfoSaldoDevedor, tituloRelatorio, subTituloPendenciaInfoSaldoDevedor.toString());

                    // 2) Contratos com pendência de aprovação de saldo devedor
                    if (responsavel.isCseSupOrg() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                        CustomTransferObject criterioPendenciaAprovSaldoDevedor = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaAprovacaoSaldoDevedor(criterio);
                        String nomeArqPendenciaAprovSaldoDevedor = nome + "_" + SUFIXO_PENDENCIA_APR_SALDO;
                        StringBuilder subTituloPendenciaAprovSaldoDevedor = new StringBuilder(subTituloRelatorio.toString());
                        subTituloPendenciaAprovSaldoDevedor.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.pendencia.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.aprovacao.saldo.devedor", responsavel)));
                        gerarRelatorio(criterioPendenciaAprovSaldoDevedor, nomeArqPendenciaAprovSaldoDevedor, tituloRelatorio, subTituloPendenciaAprovSaldoDevedor.toString());
                    }

                    // 3) Contratos com pendência de pagamento de saldo devedor.
                    CustomTransferObject criterioPendenciaPagtoSaldoDevedor = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaPagtoSaldoDevedor(criterio);
                    String nomeArqPendenciaPagtoSaldoDevedor = nome + "_" + SUFIXO_PENDENCIA_PGT_SALDO;
                    StringBuilder subTituloPendenciaPagtoSaldoDevedor = new StringBuilder(subTituloRelatorio.toString());
                    subTituloPendenciaPagtoSaldoDevedor.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.pendencia.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.informacao.pagamento.saldo.devedor", responsavel)));
                    gerarRelatorio(criterioPendenciaPagtoSaldoDevedor, nomeArqPendenciaPagtoSaldoDevedor, tituloRelatorio, subTituloPendenciaPagtoSaldoDevedor.toString());

                    // 4) Contratos com pendência de liquidação.
                    CustomTransferObject criterioPendenciaLiquidacao = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaPendenciaLiquidacao(criterio);
                    String nomeArqPendenciaLiquidacao = nome + "_" + SUFIXO_PENDENCIA_LIQUIDACAO;
                    StringBuilder subTituloPendenciaLiquidacao = new StringBuilder(subTituloRelatorio.toString());
                    subTituloPendenciaLiquidacao.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.pendencia.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.liquidacao.contrato", responsavel)));
                    gerarRelatorio(criterioPendenciaLiquidacao, nomeArqPendenciaLiquidacao, tituloRelatorio, subTituloPendenciaLiquidacao.toString());

                    compactaRelatoriosDePendencia(nome);

                } else if (filtroConfiguravel.equals("2")) {
                    // Se o filtro é contratos com bloqueio, gera um relatório para cada tipo de bloqueio.
                    // Compacta os relatórios em um só arquivo.
                    criterio.setAttribute("diasBloqueio", diasBloqueio);

                    // 1) Contratos com pendência de informação de saldo devedor.
                    CustomTransferObject criterioBloqueioInfoSaldoDevedor = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaBloqueioInfoSaldoDevedor(criterio);
                    String nomeArqBloqueioInfoSaldoDevedor = nome + "_" + SUFIXO_PENDENCIA_INF_SALDO;
                    StringBuilder subTituloBloqueioInfoSaldoDevedor = new StringBuilder(subTituloRelatorio.toString());
                    subTituloBloqueioInfoSaldoDevedor.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.bloqueio.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.nao.informacao.saldo.devedor", responsavel)));
                    gerarRelatorio(criterioBloqueioInfoSaldoDevedor, nomeArqBloqueioInfoSaldoDevedor, tituloRelatorio, subTituloBloqueioInfoSaldoDevedor.toString());

                    // 2) Contratos com pendência de aprovação de saldo devedor
                    if (responsavel.isCseSupOrg() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                        CustomTransferObject criterioBloqueioAprovSaldoDevedor = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaBloqueioAprovacaoSaldoDevedor(criterio);
                        String nomeArqBloqueioAprovSaldoDevedor = nome + "_" + SUFIXO_PENDENCIA_APR_SALDO;
                        StringBuilder subTituloBloqueioAprovSaldoDevedor = new StringBuilder(subTituloRelatorio.toString());
                        subTituloBloqueioAprovSaldoDevedor.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.bloqueio.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.nao.aprovacao.saldo.devedor", responsavel)));
                        gerarRelatorio(criterioBloqueioAprovSaldoDevedor, nomeArqBloqueioAprovSaldoDevedor, tituloRelatorio, subTituloBloqueioAprovSaldoDevedor.toString());
                    }

                    // 3) Contratos com pendência de pagamento de saldo devedor.
                    CustomTransferObject criterioBloqueioPagtoSaldoDevedor = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaBloqueioPagtoSaldoDevedor(criterio);
                    String nomeArqBloqueioPagtoSaldoDevedor = nome + "_" + SUFIXO_PENDENCIA_PGT_SALDO;
                    StringBuilder subTituloBloqueioPagtoSaldoDevedor = new StringBuilder(subTituloRelatorio.toString());
                    subTituloBloqueioPagtoSaldoDevedor.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.bloqueio.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.nao.informacao.pagamento.saldo.devedor", responsavel)));
                    gerarRelatorio(criterioBloqueioPagtoSaldoDevedor, nomeArqBloqueioPagtoSaldoDevedor, tituloRelatorio, subTituloBloqueioPagtoSaldoDevedor.toString());

                    // 4) Contratos com pendência de liquidação.
                    CustomTransferObject criterioBloqueioLiquidacao = MontaCriterioAcompanhamentoCompra.getCriteriosBuscaBloqueioLiquidacao(criterio);
                    String nomeArqBloqueioLiquidacao = nome + "_" + SUFIXO_PENDENCIA_LIQUIDACAO;
                    StringBuilder subTituloBloqueioLiquidacao = new StringBuilder(subTituloRelatorio.toString());
                    subTituloBloqueioLiquidacao.append(System.getProperty("line.separator")).append(ApplicationResourcesHelper.getMessage("rotulo.bloqueio.singular.arg0", responsavel, ApplicationResourcesHelper.getMessage("rotulo.nao.liquidacao.contrato", responsavel)));
                    gerarRelatorio(criterioBloqueioLiquidacao, nomeArqBloqueioLiquidacao, tituloRelatorio, subTituloBloqueioLiquidacao.toString());

                    compactaRelatoriosDePendencia(nome);
                }

            } catch (ReportControllerException ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(mensagem, ex);
            }
            catch (Exception ex) {
                codigoRetorno = ERRO;
                mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
                LOG.error(mensagem, ex);
            }
        }
    }

    /**
     * Gera o relatório.
     * @param criterio
     * @param nomeArquivoRelatorio
     * @param tituloRelatorio
     * @param subTituloRelatorio
     */
    private void gerarRelatorio(CustomTransferObject criterio, String nomeArquivoRelatorio, String tituloRelatorio, String subTituloRelatorio) throws ReportControllerException {
        HashMap<String, Object> parameters = new HashMap<>();

        String strFormato = getStrFormato();
        parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
        parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
        parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
        parameters.put(ReportManager.REPORT_FILE_NAME, nomeArquivoRelatorio);
        parameters.put(ReportManager.PARAM_NAME_TITULO, tituloRelatorio);
        parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTituloRelatorio);
        parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
        boolean exigeMultiplosSaldos = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, responsavel);
        parameters.put("MULTIPLOS_SALDOS", exigeMultiplosSaldos);
        parameters.put("RESPONSAVEL", responsavel);

        ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);
        reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);
    }

    /**
     * Compacta os relatórios de pendência.
     * @param nomeArquivoRelatorio
     * @return
     * @throws ReportControllerException
     */
    private String compactaRelatoriosDePendencia(String nomeArquivoRelatorio) throws ReportControllerException {
        // Determina o diretório em que os relatórios são armazenados.
        String diretorioRelatorios = getPath(responsavel);
        if (diretorioRelatorios == null) {
            LOG.error("Diretório inexistente");
            throw new ReportControllerException("mensagem.erro.interno.contate.administrador", responsavel);
        } else {
            diretorioRelatorios += File.separatorChar + "relatorio" + File.separatorChar
                    + getEntidade(responsavel) + File.separatorChar + relatorio.getTipo();

            if (!responsavel.isCseSup()) {
                diretorioRelatorios += File.separatorChar + responsavel.getCodigoEntidade();
            }

            // Cria a pasta de relatório caso não exista.
            new File(diretorioRelatorios).mkdirs();
        }

        String formatoRelatorios = getStrFormato();
        String extensao = "";
        if (formatoRelatorios.equals("TEXT")) {
            extensao += ".txt";
        } else {
            extensao += "." + formatoRelatorios.toLowerCase();
        }

        String arquivoPendenciaInfoSaldoDevedor = diretorioRelatorios + File.separator + nomeArquivoRelatorio + "_" + SUFIXO_PENDENCIA_INF_SALDO + extensao;
        String arquivoPendenciaPagtoSaldoDevedor = diretorioRelatorios + File.separator + nomeArquivoRelatorio + "_" + SUFIXO_PENDENCIA_PGT_SALDO + extensao;
        String arquivoPendenciaLiquidacao = diretorioRelatorios + File.separator + nomeArquivoRelatorio + "_" + SUFIXO_PENDENCIA_LIQUIDACAO + extensao;

        List<String> arquivosRelatorios = new ArrayList<>();
        arquivosRelatorios.add(arquivoPendenciaInfoSaldoDevedor);
        arquivosRelatorios.add(arquivoPendenciaPagtoSaldoDevedor);
        arquivosRelatorios.add(arquivoPendenciaLiquidacao);

        if (responsavel.isCseSupOrg() && ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel)) {
            String arquivoPendenciaAprovSaldoDevedor = diretorioRelatorios + File.separator + nomeArquivoRelatorio + "_" + SUFIXO_PENDENCIA_APR_SALDO + extensao;
            arquivosRelatorios.add(arquivoPendenciaAprovSaldoDevedor);
        }

        String nomeArquivoZip = nomeArquivoRelatorio + ".zip";
        String arquivoZip = diretorioRelatorios + File.separator + nomeArquivoZip;
        try {
            FileHelper.zip(arquivosRelatorios, arquivoZip);
        } catch (IOException e) {
            LOG.warn("Não foi possível compactar os arquivos", e);
            arquivoZip = null;
        }

        // Se gerou o arquivo compactado, apaga os arquivos originais.
        if (arquivoZip != null) {
            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(arquivoZip);
            // Seta mensagem de sucesso na geração do relatório
            setMensagem(arquivoZip, relatorio.getTipo(), relatorio.getTitulo(), session);

            for (String arquivoRelatorio : arquivosRelatorios) {
                new File(arquivoRelatorio).delete();
            }

            return nomeArquivoZip;
        } else {
            return null;
        }
    }
}
