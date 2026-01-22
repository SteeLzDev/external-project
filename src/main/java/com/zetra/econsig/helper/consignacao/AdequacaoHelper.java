package com.zetra.econsig.helper.consignacao;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipFile;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignacaoDelegate;
import com.zetra.econsig.delegate.ConvenioDelegate;
import com.zetra.econsig.delegate.HistoricoArquivoDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.delegate.TipoMotivoOperacaoDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.MargemTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.ServicoTransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConvenioControllerException;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: AdequacaoHelper</p>
 * <p>Description: Rotina de adequação de contratos à margem do servidor</p>
 * <p>Copyright: Copyright (c) 2002-2015</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AdequacaoHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AdequacaoHelper.class);

    private static final String SEPARADOR = ";";

    public static void processar(String nomeArquivoEntrada, boolean validar, AcessoSistema responsavel) throws ViewHelperException {
        BufferedReader entrada = null;
        PrintWriter saida = null;
        ZipFile entradaZip = null;
        String linha = null;

        // Determina o caminho dos arquivos de entrada e saída
        String caminho = ParamSist.getDiretorioRaizArquivos()
                       + File.separator + "adequacao"
                       + File.separator + "cse"
                       + File.separator;

        // Verifica se o caminho para a gravação existe
        File dir = new java.io.File(caminho);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new ViewHelperException("mensagem.erro.diretorio.inexistente", responsavel);
        }

        // Determina o nome do arquivo de crítica
        String nomeArquivoSaida = (validar ? ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.validacao.prefixo", responsavel)
                                : ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel))
                                + nomeArquivoEntrada.substring(0, nomeArquivoEntrada.lastIndexOf('.'))
                                + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss")
                                + ".txt";

        try {
            // Delegates necessários para as operações
            ConsignacaoDelegate csgDelegate = new ConsignacaoDelegate();
            AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            ConvenioDelegate cnvDelegate = new ConvenioDelegate();
            ParametroDelegate parDelegate = new ParametroDelegate();
            TipoMotivoOperacaoDelegate tmoDelegate = new TipoMotivoOperacaoDelegate();

            ConsultarMargemController consultarMargemController = ApplicationContextProvider.getApplicationContext().getBean(ConsultarMargemController.class);

            // Parâmetros de sistema necessários
            boolean exportacaoInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel);

            // Abre fluxo de entrada: verifica se é arquivo zip
            if (FileHelper.isZip(caminho + nomeArquivoEntrada)) {
                entradaZip = new ZipFile(caminho + nomeArquivoEntrada);
                entrada = new BufferedReader(new InputStreamReader(entradaZip.getInputStream(entradaZip.entries().nextElement())));
            } else {
                // Remove espaços do final do arquivo
                FileHelper.trimTextFile(caminho + nomeArquivoEntrada);
                entrada = new BufferedReader(new FileReader(caminho + nomeArquivoEntrada));
            }

            // Abre fluxo de saída para arquivo de crítica
            saida = new PrintWriter(new BufferedWriter(new FileWriter(caminho + nomeArquivoSaida)));

            // Inicia leitura do arquivo, que deve estar no formato: <ADE_NUMERO>;<TMO_IDENTIFICADOR>;<OBS>
            while ((linha = entrada.readLine()) != null) {
                if (!TextHelper.isNull(linha)) {
                    saida.print(linha + SEPARADOR);

                    String [] campos = linha.split(SEPARADOR);
                    String numero = (campos.length > 0 ? campos[0] : null);
                    String tmoIdentificador = (campos.length > 1 ? campos[1] : null);
                    String ocaObs = (campos.length > 2 ? campos[2] : null);

                    if (TextHelper.isNull(numero)) {
                        // Gera crítica e passa para a próxima linha
                        saida.println(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.erro.ade.numero.obrigatorio", responsavel));
                        continue;
                    } else if (!TextHelper.isNum(numero)) {
                        // Gera crítica e passa para a próxima linha
                        saida.println(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.erro.ade.numero.invalido", responsavel));
                        continue;
                    }

                    Long adeNumero = Long.valueOf(numero);
                    TransferObject ade = null;
                    try {
                        ade = adeDelegate.findAutDescontoByAdeNumero(adeNumero, responsavel);
                    } catch (AutorizacaoControllerException ex) {
                        // Gera crítica e passa para a próxima linha
                        saida.println(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.erro.ade.numero.inexistente", responsavel));
                        continue;
                    }

                    TipoMotivoOperacaoTransferObject tipoMotivoOperacao = null;
                    String tmoCodigo = null;
                    if (!TextHelper.isNull(tmoIdentificador)) {
                        try {
                            tipoMotivoOperacao = tmoDelegate.findMotivoOperacaoByCodIdent(tmoIdentificador, responsavel);
                            tmoCodigo = tipoMotivoOperacao.getTmoCodigo();
                        } catch (TipoMotivoOperacaoControllerException ex) {
                            // Gera crítica e passa para a próxima linha
                            saida.println(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.erro.tmo.identificador.inexistente", responsavel));
                            continue;
                        }
                    }

                    String adeCodigo = (String) ade.getAttribute(Columns.ADE_CODIGO);
                    String rseCodigo = (String) ade.getAttribute(Columns.RSE_CODIGO);
                    Short adeIncMargem = (Short) ade.getAttribute(Columns.ADE_INC_MARGEM);
                    BigDecimal adeVlr = (BigDecimal) ade.getAttribute(Columns.ADE_VLR);
                    Integer adePrazo = (Integer) ade.getAttribute(Columns.ADE_PRAZO);
                    Integer adePrdPagas = (ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? (Integer) ade.getAttribute(Columns.ADE_PRD_PAGAS) : 0);

                    if (adePrazo == null) {
                        // Gera crítica e passa para a próxima linha
                        saida.println(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.erro.ade.prazo.indeterminado", responsavel));
                        continue;
                    }

                    MargemTO margem = null;
                    try {
                        List<MargemTO> margens = consultarMargemController.consultarMargem(rseCodigo, null, null, null, adeIncMargem, true, false, true, null, responsavel);
                        if (margens != null && margens.size() > 0) {
                            margem = margens.get(0);
                            if (margem.getMrsMargemRest().signum() >= 0) {
                                // Gera crítica e passa para a próxima linha
                                saida.println(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.erro.rse.margem.rest.positiva", responsavel));
                                continue;
                            }
                        } else {
                            // Gera crítica e passa para a próxima linha
                            saida.println(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.erro.margem.inexistente", responsavel));
                            continue;
                        }
                    } catch (ServidorControllerException ex) {
                        // Gera crítica e passa para a próxima linha
                        saida.println(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.erro.consulta.margem", responsavel, ex.getMessage()));
                        continue;
                    }

                    if (adeVlr.compareTo(margem.getMrsMargemRest().negate()) <= 0) {
                        // Gera crítica e passa para a próxima linha
                        saida.println(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.erro.margem.negativa.excessiva", responsavel));
                        continue;
                    }

                    // Calcular o valor máximo da parcela, que será o valor atual da parcela (ade_vlr) abatido do valor de margem negativa, da margem ao qual o contrato incide.
                    BigDecimal adeVlrMax = adeVlr.subtract(margem.getMrsMargemRest().negate());

                    // Calcular o capital devido, que é o valor da parcela original multiplicado pelo prazo restante (ade_vlr * (ade_prazo - ade_prd_pagas)).
                    BigDecimal capitalDevido = adeVlr.multiply(new BigDecimal(adePrazo - adePrdPagas));

                    // Calcular o novo prazo, que será o capital devido dividido pelo valor máximo de parcela, arredondando para cima.
                    Integer adePrazoNovo = capitalDevido.divide(adeVlrMax, 0, RoundingMode.UP).intValue();

                    // Calcular o novo valor da parcela, que será o capital devido dividido pelo novo prazo.
                    BigDecimal adeVlrNovo = capitalDevido.divide(new BigDecimal(adePrazoNovo), 2, RoundingMode.DOWN);

                    if (adePrazoNovo <= 0 || adeVlrNovo.signum() <= 0 || adeVlrNovo.compareTo(adeVlrMax) > 0) {
                        // Gera crítica e passa para a próxima linha
                        saida.println(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.erro.valor.prazo.invalidos", responsavel));
                        continue;
                    }

                    // Validar prazo novo pelo prazo máximo do serviço
                    try {
                        ServicoTransferObject svc = cnvDelegate.findServicoByAdeCodigo(adeCodigo, responsavel);
                        // Busca os parâmetros de serviço
                        List<String> tpsCodigos = new ArrayList<>();
                        tpsCodigos.add(CodedValues.TPS_MAX_PRAZO);
                        tpsCodigos.add(CodedValues.TPS_MAX_PRAZO_RENEGOCIACAO_PORTABILIDADE);
                        tpsCodigos.add(CodedValues.TPS_MAX_PRAZO_RELATIVO_AOS_RESTANTES);
                        ParamSvcTO paramSvc = parDelegate.selectParamSvcCse(svc.getSvcCodigo(), tpsCodigos, responsavel);

                        boolean isDestinoRenegociacao = adeDelegate.isDestinoRenegociacaoPortabilidade(adeCodigo);
                        int maxPrazoReserva = (paramSvc.getTpsMaxPrazo() != null && !paramSvc.getTpsMaxPrazo().equals("")) ? Integer.parseInt(paramSvc.getTpsMaxPrazo()) : -1;
                        int maxPrazoRenegociacao = (paramSvc.getTpsMaxPrazoRenegociacao() != null && !paramSvc.getTpsMaxPrazoRenegociacao().equals("")) ? Integer.parseInt(paramSvc.getTpsMaxPrazoRenegociacao()) : -1;
                        boolean validaPrzMaxRelativoRestantes = paramSvc.isTpsMaxPrazoRelativoAosRestantes();

                        int maxPrazo = (isDestinoRenegociacao && maxPrazoRenegociacao != -1) ? maxPrazoRenegociacao : maxPrazoReserva;
                        int totalPrazoNovo = adePrazoNovo + adePrdPagas;

                        if (maxPrazo != -1 && ((validaPrzMaxRelativoRestantes) ? (totalPrazoNovo - adePrdPagas) : totalPrazoNovo) > maxPrazo) {
                            // Gera crítica e passa para a próxima linha
                            saida.println(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.erro.novo.prazo.maior.permitido", responsavel));
                            continue;
                        }
                    } catch (ConvenioControllerException ex) {
                        saida.println(ex.getMessage());
                        continue;
                    } catch (ParametroControllerException ex) {
                        saida.println(ex.getMessage());
                        continue;
                    } catch (AutorizacaoControllerException ex) {
                        saida.println(ex.getMessage());
                        continue;
                    }

                    LOG.debug("==================================================");
                    LOG.debug("ADE. NUMERO  : " + adeNumero);
                    LOG.debug("Valor Atual  : " + adeVlr);
                    LOG.debug("Valor Novo   : " + adeVlrNovo);
                    LOG.debug("Prazo Atual  : " + (adePrazo - adePrdPagas));
                    LOG.debug("Prazo Novo   : " + adePrazoNovo);
                    LOG.debug("Devido Atual : " + capitalDevido);
                    LOG.debug("Devido Novo  : " + adeVlrNovo.multiply(new BigDecimal(adePrazoNovo)));

                    try {
                        // Em caso de sucesso, executar caso de uso de alteração de consignação informando o novo valor de parcela e novo prazo do contrato.
                        AlterarConsignacaoParametros alterarParam = new AlterarConsignacaoParametros(adeCodigo, adeVlrNovo, adePrazoNovo, null, validar, null, null, null, null, null, null, null, null);
                        alterarParam.setTmoCodigo(tmoCodigo);
                        alterarParam.setOcaObs(ocaObs);
                        alterarParam.setAdePeriodicidade((String) ade.getAttribute(Columns.ADE_PERIODICIDADE));

                        // Se o motivo foi informado, seta parâmetros de alteração avançada
                        if (!TextHelper.isNull(tmoCodigo)) {
                            alterarParam.setAlteracaoAvancada(true);
                            alterarParam.setAlterarValorPrazoSemLimite(true);
                            alterarParam.setPermiteAltEntidadesBloqueadas(true);
                            alterarParam.setValidaTaxaJuros(false);
                            alterarParam.setValidaLimiteAde(false);
                            alterarParam.setExigeSenha(false);
                        }

                        csgDelegate.alterarConsignacao(alterarParam, responsavel);
                    } catch (AutorizacaoControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                        // Gera crítica e passa para a próxima linha
                        saida.println(ex.getMessage());
                        continue;
                    }

                    if (!validar) {
                        // Em caso de sistema inicial, executar reimplante do contrato após a alteração.
                        if (exportacaoInicial) {
                            try {
                                csgDelegate.reimplantarConsignacao(adeCodigo, ocaObs, tipoMotivoOperacao, responsavel);
                            } catch (AutorizacaoControllerException ex) {
                                // Gera crítica e passa para a próxima linha
                                saida.println(ex.getMessage());
                                continue;
                            }
                        }
                    }

                    // Adicionar mensagem de sucesso ao arquivo de crítica
                    if (validar) {
                        saida.print(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.info.linha.valida", responsavel, NumberHelper.format(adeVlrNovo.doubleValue(), NumberHelper.getLang()), String.valueOf(adePrazoNovo)));
                    } else {
                        saida.print(ApplicationResourcesHelper.getMessage("mensagem.adequacao.margem.info.sucesso", responsavel));
                    }
                    saida.println();
                }
            }
        } catch (FileNotFoundException ex) {
            throw new ViewHelperException("mensagem.adequacao.margem.erro.arquivo.invalido", responsavel, ex);
        } catch (IOException ex) {
            throw new ViewHelperException("mensagem.adequacao.margem.erro.processar.arquivo", responsavel, ex);
        } finally {
            try {
                if (entrada != null) {
                    entrada.close();
                }
                if (entradaZip != null) {
                    entradaZip.close();
                }
                if (saida != null) {
                    saida.close();
                }
            } catch (IOException ex) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.adequacao.margem.fechar.arquivo", responsavel, nomeArquivoEntrada), ex);
            }
        }

        String nomeArquivoEntradaProcessado = caminho + nomeArquivoEntrada + (!validar ? ".ok" : "");
        try {
            if (!validar) {
                // Renomear arquivo de entrada
                FileHelper.rename(caminho + nomeArquivoEntrada, nomeArquivoEntradaProcessado);
            }
            // Compactar arquivo de crítica
            FileHelper.zip(caminho + nomeArquivoSaida, caminho + nomeArquivoSaida.replaceAll(".txt", ".zip"));
            // Apaga o arquivo de crítica versão txt
            FileHelper.delete(caminho + nomeArquivoSaida);
        } catch (IOException ex) {
            throw new ViewHelperException("mensagem.adequacao.margem.erro.processar.arquivo", responsavel, ex, ex.getMessage());
        }

        try {
            if (!validar) {
                // Gravar histórico de importação de arquivo
                HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                hisArqDelegate.createHistoricoArquivo(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), TipoArquivoEnum.ARQUIVO_ADEQUACAO_A_MARGEM, nomeArquivoEntradaProcessado, "", null, null, "1", responsavel);
            }
        } catch (HistoricoArquivoControllerException ex) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.adequacao.margem.gravar.historico", responsavel, nomeArquivoEntrada), ex);
        }

        try {
            // Grava Log para auditoria
            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, (!validar ? Log.IMP_ARQ_ADEQUACAO : Log.VALID_ARQ_ADEQUACAO), Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.arquivo", responsavel, nomeArquivoEntrada));
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.numero.linhas.arquivo", responsavel, String.valueOf(FileHelper.getNumberOfLines(nomeArquivoEntradaProcessado))));
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.erro.adequacao.margem.gravar.log", responsavel, nomeArquivoEntrada), ex);
        }
    }
}
