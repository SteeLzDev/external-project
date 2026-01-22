package com.zetra.econsig.job.process.agendado;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.lote.LoteHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.persistence.entity.ControleProcessamentoLote;
import com.zetra.econsig.service.lote.LoteController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaControleProcessamentoLote</p>
 * <p>Description: Processamento de exclusão de processamentos que se tornaram inválidos</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaControleProcessamentoLote extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaControleProcessamentoLote.class);

    public ProcessaControleProcessamentoLote(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        try {
            LOG.debug("Início da análise dos lotes em processamento");

            final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
            final LoteController loteController = ApplicationContextProvider.getApplicationContext().getBean(LoteController.class);

            // Busca arquivos de lote que estejam em processamento
            final List<TransferObject> lotes = loteController.lstLotesEmProcessamento(getResponsavel());
            if (lotes != null && !lotes.isEmpty()) {
                for (TransferObject lote : lotes) {
                    try {
                        final String nomeArquivo = (String) lote.getAttribute(Columns.CPL_ARQUIVO_ECONSIG);
                        final String usuCodigo = lote.getAttribute(Columns.CPL_USU_CODIGO) != null ? lote.getAttribute(Columns.CPL_USU_CODIGO).toString() : CodedValues.USU_CODIGO_SISTEMA;
                        final CanalEnum canal = lote.getAttribute(Columns.CPL_CANAL) != null ? CanalEnum.get(lote.getAttribute(Columns.CPL_CANAL).toString()) : CanalEnum.SOAP;
                        final String parametros = (String) lote.getAttribute(Columns.CPL_PARAMETROS);
                        final boolean temBlocoPendente = "S".equalsIgnoreCase((String) lote.getAttribute("TEM_BLOCO_PENDENTE"));
                        final Date dataLote = (Date) lote.getAttribute(Columns.CPL_DATA);
                        final short statusLote = (Short) lote.getAttribute(Columns.CPL_STATUS);

                        LOG.info("Lote não finalizado '" + nomeArquivo + "' iniciado por '" + usuCodigo + "' em '" + DateHelper.toDateTimeString(dataLote) + "' pelo canal '" + canal + "' " + (temBlocoPendente ? "com" : "sem") + " blocos pendentes");

                        final File arquivoEntrada = new File(nomeArquivo);
                        if (!arquivoEntrada.exists()) {
                            boolean achouArquivo = false;
                            if (nomeArquivo.endsWith(".prc")) {
                                // Caso o nome do arquivo termine em .prc, significa que é um processamento iniciado porém
                                // abortado e que o nome do arquivo foi restaurado. Verifica se existe o arquivo sem o .prc
                                final String nomeArquivoSemPrc = nomeArquivo.replaceAll(".prc", "");
                                final File arquivoEntradaSemPrc = new File(nomeArquivoSemPrc);
                                if (arquivoEntradaSemPrc.exists()) {
                                    // Caso o arquivo exista sem a extensão .prc, move o arquivo para a extensão .prc
                                    // esperada pelo sistema para um processamento de lote
                                    FileHelper.rename(nomeArquivoSemPrc, nomeArquivo);
                                    achouArquivo = true;
                                }
                            }
                            if (!achouArquivo) {
                                LOG.error("Lote '" + nomeArquivo + "' não encontrado na pasta de entrada");
                                continue;
                            }
                        }

                        // Busca o registro de controle do processamento
                        final ControleProcessamentoLote controleProcessamento = loteController.findProcessamentoByArquivoeConsig(nomeArquivo);

                        if ((statusLote == CodedValues.CPL_UPLOAD_SUCESSO || statusLote == CodedValues.CPL_PROCESSANDO) && !TextHelper.isNull(parametros)) {
                            // Se o lote nem começou a processar ou estava em processamento, então reinicia o processamento
                            // do arquivo, para que os blocos sejam processados, e o arquivo de crítica gerado

                            // Objeto usado para converter o Map com campos de entrada em JSON
                            final Gson gson = new Gson();
                            // Carrega status através do campo de parâmetros salvo no controle processamento
                            final Map<String, Object> paramMap = gson.fromJson(parametros, Map.class);

                            final String csaCodigo = (String) paramMap.get("csaCodigo");
                            final String corCodigo = (String) paramMap.get("corCodigo");
                            final boolean validar  = (boolean) paramMap.get("validar");
                            final boolean serAtivo = (boolean) paramMap.get("serAtivo");
                            final boolean cnvAtivo = (boolean) paramMap.get("cnvAtivo");
                            final boolean permiteLoteAtrasado = (boolean) paramMap.get("permiteLoteAtrasado");
                            final boolean permiteReducaoLancamentoCartao = (boolean) paramMap.get("permiteReducaoLancamentoCartao");
                            final Date periodoConfiguravel = (!TextHelper.isNull(paramMap.get("periodoConfiguravel")) ? DateHelper.parseExceptionSafe(paramMap.get("periodoConfiguravel").toString(), "yyyy-MM-dd") : null);
                            final String nomeArqXmlEntrada  = (String) paramMap.get("nomeArqXmlEntrada");
                            final String nomeArqXmlTradutor = (String) paramMap.get("nomeArqXmlTradutor");

                            final String ipAcesso = (String) paramMap.get("ipAcesso");
                            final AcessoSistema responsavelLote = AcessoSistema.recuperaAcessoSistema(usuCodigo, ipAcesso, null);
                            responsavelLote.setPermissoes(usuarioController.selectFuncoes(responsavelLote.getUsuCodigo(), responsavelLote.getCodigoEntidade(), responsavelLote.getTipoEntidade(), responsavelLote));
                            responsavelLote.setCanal(canal);

                            final File arqXmlEntrada  = new File(nomeArqXmlEntrada);
                            final File arqXmlTradutor = new File(nomeArqXmlTradutor);

                            if (!arqXmlEntrada.exists() || !arqXmlTradutor.exists()) {
                                LOG.error("Leiaute XML '" + nomeArqXmlEntrada + "' e '" + nomeArqXmlTradutor + "' para o lote '" + nomeArquivo + "' não encontrados na pasta de arquivos.");
                                continue;
                            }

                            final LoteHelper loteHelper = new LoteHelper(csaCodigo, corCodigo, validar, serAtivo, cnvAtivo, permiteLoteAtrasado, permiteReducaoLancamentoCartao, periodoConfiguravel, responsavelLote);
                            loteHelper.importarLote(arqXmlEntrada.getName(), arqXmlTradutor.getName(), arquivoEntrada.getName());
                            continue;
                        }

                        // Nos demais status, avalia a data de envio, caso seja antiga, remove o controle do processamento
                        if (dataLote == null) {
                            // Se não tem data salva, é um lote criado antes das novas implementações, então
                            // atualiza o registro de controle definindo a data atual para que após transcorrido
                            // um tempo razoável, ele seja excluído
                            controleProcessamento.setCplData(DateHelper.getSystemDatetime());
                            loteController.alterarProcessamento(controleProcessamento, getResponsavel());

                        } else {
                            final int diasTranscorridos = DateHelper.dayDiff(dataLote);
                            if (diasTranscorridos > 7) {
                                // Se passou uma semana que o lote foi enviado, e não teve consumo do resultado ou tentativa de recuperação,
                                // então remove o registro de controle do processamento, evitando que fique eternamente esperando
                                loteController.excluirProcessamento(controleProcessamento, getResponsavel());
                            }
                        }
                    } catch (ZetraException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }

            LOG.debug("Fim da análise dos lotes em processamento");
        } catch (ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }
}
