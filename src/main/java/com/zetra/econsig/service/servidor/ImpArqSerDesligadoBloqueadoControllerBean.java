package com.zetra.econsig.service.servidor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.query.servidor.ListaRegistroServidorQuery;
import com.zetra.econsig.service.arquivo.HistoricoArquivoController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.filter.XSSPreventionFilter;

/**
 * <p>Title: ImpArqSerDesligadoBloqueadoBean</p>
 * <p>Description: Session Bean para a operação de importação de arquivos de servidores desligados e bloqueados.</p>
 * <p>Copyright: Copyright (c) 2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ImpArqSerDesligadoBloqueadoControllerBean implements ImpArqSerDesligadoBloqueadoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImpArqSerDesligadoBloqueadoControllerBean.class);

    @Autowired
    private SistemaController sistemaController;

    @Autowired
    private HistoricoArquivoController historicoArquivoController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    private static final int TAMANHO_MSG_ERRO_DEFAULT = 100;
    private static final String COMPLEMENTO_DEFAULT = " ";

    @Override
    public String importaDesligadoBloqueado(String nomeArquivoEntrada, boolean validar, AcessoSistema responsavel) throws ServidorControllerException {

        /** Totalizadores de registros */
        int totalDesligados = 0;
        int totalBloqueados = 0;
        int totalRegistros = 0;
        int totalProblema = 0;

        /** Objetos para tradução do arquivo de entrada */
        LeitorArquivoTexto leitor;
        Escritor escritor;
        Tradutor tradutor;

        // Verifica permissão
        if (!responsavel.isCseSup() && !responsavel.isSistema()) {
            throw new ServidorControllerException("mensagem.usuarioNaoTemPermissao", responsavel);
        }

        String tipoEntidade = responsavel.getTipoEntidade();
        String codigoEntidade = responsavel.getCodigoEntidade();

        // Grava o arquivo de lote no sistema de arquivo
        String rootPath = ParamSist.getDiretorioRaizArquivos();
        String pathLote = rootPath + File.separator + "desligado" + File.separator + "cse" + File.separator;

        // Verifica se o caminho para a gravação existe
        File dir = new java.io.File(pathLote);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new ServidorControllerException("mensagem.erro.importar.bloqueio.servidor.diretorio.nao.existe", responsavel);
        }

        // Se o sistema estiver bloqueado ou inativo, nenhum arquivo de lote pode ser processado
        if (sistemaBloqueado(responsavel)) {
            throw new ServidorControllerException("mensagem.erro.sistema.bloqueado.inativo", responsavel);
        }

        // Recupera parâmetros de configuração do sistema
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String pathLoteDefault = absolutePath + File.separatorChar + "conf" + File.separatorChar;

        // Recupera layout de importação dos desligados e bloqueados
        String entradaImpLote = null;
        String tradutorImpLote = null;

        String entradaImpLoteDefault = pathLoteDefault + CodedValues.ARQ_CONF_ENTRADA_DESLIGADO_BLOQUEADO;
        String tradutorImpLoteDefault = pathLoteDefault + CodedValues.ARQ_CONF_TRADUTOR_DESLIGADO_BLOQUEADO;

        File arqConfEntradaDefault = new File(entradaImpLoteDefault);
        File arqConfTradutorDefault = new File(tradutorImpLoteDefault);
        if (!arqConfEntradaDefault.exists() || !arqConfTradutorDefault.exists()) {
            throw new ServidorControllerException("mensagem.erro.sistema.arquivos.importacao.desligado.bloqueado.ausentes", responsavel);
        } else {
            entradaImpLote = entradaImpLoteDefault;
            tradutorImpLote = tradutorImpLoteDefault;
        }

        String fileName = absolutePath + File.separatorChar + "desligado" + File.separatorChar + "cse" + File.separatorChar + nomeArquivoEntrada;

        // Verifica se o arquivo existe
        File arqEntrada = new File(fileName);
        if (!arqEntrada.exists()) {
            LOG.error("Arquivo não encontrado: " + fileName);
            throw new ServidorControllerException("mensagem.erro.sistema.arquivo.desligado.bloqueado.nao.encontrado", responsavel);
        }

        if (!validar) {
            // Renomeia o arquivo que será processado para que não ocorra duplicação do processamento
            FileHelper.rename(fileName, fileName + ".prc");
            fileName += ".prc";
        }

        // Configura o leitor de acordo com o arquivo de entrada
        if (fileName.toLowerCase().endsWith(".zip") || fileName.toLowerCase().endsWith(".zip.prc")) {
            LOG.debug("nome do arquivo ... " + fileName);
            leitor = new LeitorArquivoTextoZip(entradaImpLote, fileName);
        } else {
            LOG.debug("nome do arquivo ... " + fileName);
            leitor = new LeitorArquivoTexto(entradaImpLote, fileName);
        }

        // Hash que recebe os dados do que serão lidos do arquivo de entrada
        HashMap<String, Object> entrada = new HashMap<>();

        // Escritor e tradutor
        escritor = new EscritorMemoria(entrada);
        tradutor = new Tradutor(tradutorImpLote, leitor, escritor);

        try {
            // Grava Log para auditoria
            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, validar ? Log.VALIDA_DESLIGADO_BLOQUEADO : Log.IMP_DESLIGADO_BLOQUEADO, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.arquivo", responsavel, nomeArquivoEntrada));
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.numero.linhas.arquivo", responsavel, String.valueOf(FileHelper.getNumberOfLines(fileName))));
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.leiaute", responsavel, CodedValues.ARQ_CONF_ENTRADA_DESLIGADO_BLOQUEADO, CodedValues.ARQ_CONF_TRADUTOR_DESLIGADO_BLOQUEADO));
            log.write();
        } catch (LogControllerException ex) {
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        String delimitador = leitor.getDelimitador() == null ? "" : leitor.getDelimitador();

        List<String> critica = new ArrayList<>();
        try {
            ControleRestricaoAcesso.RestricaoAcesso restricao = ControleRestricaoAcesso.possuiRestricaoAcesso(responsavel);
            if (restricao.getGrauRestricao() != ControleRestricaoAcesso.GrauRestricao.SemRestricao) {
                critica.add(ApplicationResourcesHelper.getMessage("rotulo.critica.operacao.temporariamente.indisponivel", responsavel, restricao.getDescricao()));
            }
        } catch (ZetraException e) {
            critica.add(ApplicationResourcesHelper.getMessage("mensagem.erro.critica.operacao.temporariamente.indisponivel", responsavel));
        }

        // Inclui histórico do arquivo
        Long harCodigo = null;
        boolean gerouException = false;
        try {
            String harObs = "";
            String harResultado = CodedValues.STS_INATIVO.toString();
            harCodigo = historicoArquivoController.createHistoricoArquivo(tipoEntidade, codigoEntidade, TipoArquivoEnum.ARQUIVO_DESLIGADO_BLOQUEADO, fileName, harObs, null, null, harResultado, responsavel);
        } catch (HistoricoArquivoControllerException e) {
            LOG.error("Não foi possível inserir o histórico do arquivo de servidores bloqueados e desligados '" + nomeArquivoEntrada + "'.", e);
        }

        // Conta quantos servidores ativos
        int totalSerAtivoAntigo = contarRegistroServidor(responsavel);

        String msgErro;
        try {
            tradutor.iniciaTraducao();
        } catch (ParserException e) {
            LOG.error("Erro em iniciar tradução.");
            throw new ServidorControllerException(e);
        }

        boolean proximo = true;
        try {
            // Faz o loop de cada linha do arquivo para realizar as traduções
            while (proximo) {
                try {
                    proximo = tradutor.traduzProximo();

                    // Realiza a validação de segurança contra ataque de XSS nos campos do lote
                    for (String key : entrada.keySet()) {
                        Object value = entrada.get(key);
                        if (value != null && value instanceof String) {
                            // Se for String, realiza o tratamento anti-XSS
                            entrada.put(key, XSSPreventionFilter.stripXSS((String) value));
                        }
                    }

                    if (!proximo) {
                        break;
                    }

                    msgErro = "";

                    // Criar rotina para importação de arquivo de desligados e bloqueados, configurada em leiaute XML
                    if (entrada.get("LINHA_INVALIDA") == null || entrada.get("LINHA_INVALIDA").toString().equals("N")) {
                        String rseMatricula = (String) entrada.get("RSE_MATRICULA");
                        String serCpf = (String) entrada.get("SER_CPF");
                        String estIdentificador = (String) entrada.get("EST_IDENTIFICADOR");
                        String orgIdentificador = (String) entrada.get("ORG_IDENTIFICADOR");
                        String serNome = (String) entrada.get("SER_NOME");
                        String serSobrenome = (String) entrada.get("SER_SOBRENOME");
                        String serDataNasc = (String) entrada.get("SER_DATA_NASC");
                        String srsCodigo = (String) entrada.get("SRS_CODIGO");
                        String rsePedidoDemissao = (String) entrada.get("RSE_PEDIDO_DEMISSAO");
                        String rseDataSaida = (String) entrada.get("RSE_DATA_SAIDA");
                        String rseDataUltSalario = (String) entrada.get("RSE_DATA_ULT_SALARIO");
                        String rseDataRetorno = (String) entrada.get("RSE_DATA_RETORNO");
                        String tmoIdentificador = (String) entrada.get("TMO_IDENTIFICADOR");
                        String tmoObs = (String) entrada.get("TMO_OBS");

                        if (TextHelper.isNull(tmoObs)) {
                            tmoObs = ApplicationResourcesHelper.getMessage("mensagem.arq.imp.servidores.desligados.bloqueados.obs", responsavel);
                        }

                        TransferObject criterios = new CustomTransferObject();
                        if (!TextHelper.isNull(serNome)) {
                            criterios.setAttribute("NOME", serNome);
                        }
                        if (!TextHelper.isNull(serSobrenome)) {
                            criterios.setAttribute("SOBRENOME", serSobrenome);
                        }
                        if (!TextHelper.isNull(serDataNasc)) {
                            try {
                                Date serDataNascimento = DateHelper.parse(serDataNasc, LocaleHelper.getDatePattern());
                                criterios.setAttribute("serDataNascimento", serDataNascimento);
                            } catch (ParseException ex) {
                                throw new ZetraException("mensagem.erro.data.nascimento.informada.invalida", responsavel, serDataNasc);
                            }
                        }

                        // Listar o registro servidor
                        List<TransferObject> lstServidor = pesquisarServidorController.pesquisaServidorExato(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), estIdentificador, orgIdentificador, rseMatricula, serCpf, criterios, responsavel);

                        if (lstServidor == null || lstServidor.isEmpty()) {
                            throw new AutorizacaoControllerException("mensagem.nenhumServidorEncontrado", responsavel);
                        } else if (lstServidor.size() > 1) {
                            throw new AutorizacaoControllerException("mensagem.multiplosServidoresEncontrados", responsavel);
                        }

                        TransferObject servidor = lstServidor.get(0);

                        String rseCodigo = servidor.getAttribute(Columns.RSE_CODIGO).toString();

                        // Verifica se as data são válidas
                        Date dataSaida = null, dataRetorno = null, dataUltimoSalario = null;
                        String tmoCodigo = null;
                        if (!TextHelper.isNull(rseDataSaida)) {
                            dataSaida = DateHelper.parse(rseDataSaida, LocaleHelper.getDatePattern());
                        }
                        if (!TextHelper.isNull(rseDataRetorno)) {
                            dataRetorno = DateHelper.parse(rseDataRetorno, LocaleHelper.getDatePattern());
                        }
                        if (!TextHelper.isNull(rseDataUltSalario)) {
                            dataUltimoSalario = DateHelper.parse(rseDataUltSalario, LocaleHelper.getDatePattern());
                        }
                        if (!TextHelper.isNull(tmoIdentificador)) {
                            try {
                                TipoMotivoOperacaoTransferObject tmo = tipoMotivoOperacaoController.findMotivoOperacaoByCodIdent(tmoIdentificador, responsavel);
                                tmoCodigo = tmo.getTmoCodigo();
                            } catch (TipoMotivoOperacaoControllerException e) {
                                throw new AutorizacaoControllerException("mensagem.erro.motivo.operacao.invalido", responsavel);
                            }
                        }

                        // Realiza as validações
                        if (CodedValues.SRS_BLOQUEADOS.contains(srsCodigo)) {
                            // Data do último salário
                            // Data de saida
                            // Data de retorno futura
                            if (dataUltimoSalario == null) {
                                throw new AutorizacaoControllerException("mensagem.erro.data.ultimo.salario.obrigatoria", responsavel);
                            }
                            if (dataSaida == null) {
                                throw new AutorizacaoControllerException("mensagem.erro.data.saida.obrigatoria", responsavel);
                            }
                            if (dataRetorno == null) {
                                throw new AutorizacaoControllerException("mensagem.erro.data.retorno.obrigatoria", responsavel);
                            }
                            if (dataRetorno.before(DateHelper.getSystemDatetime())) {
                                throw new AutorizacaoControllerException("mensagem.erro.data.retorno.futura", responsavel);
                            }
                            // Neste caso, ignora e não altera o campo pedido de demissão
                            rsePedidoDemissao = null;
                            totalBloqueados++;
                        } else if (srsCodigo.equals(CodedValues.SRS_EXCLUIDO)) {
                            // Data do último salário
                            // data de saida
                            if (dataUltimoSalario == null) {
                                throw new AutorizacaoControllerException("mensagem.erro.data.ultimo.salario.obrigatoria", responsavel);
                            }
                            if (dataSaida == null) {
                                throw new AutorizacaoControllerException("mensagem.erro.data.saida.obrigatoria", responsavel);
                            }
                            // Verfifica Pedido de demissão
                            if (!TextHelper.isNull(rsePedidoDemissao) && !(rsePedidoDemissao.equals("S") || rsePedidoDemissao.equals("N"))) {
                                throw new AutorizacaoControllerException("mensagem.erro.pedido.demissao.invalido", responsavel);
                            }
                            if (TextHelper.isNull(rsePedidoDemissao)) {
                                throw new AutorizacaoControllerException("mensagem.erro.pedido.demissao.obrigatorio", responsavel);
                            }
                            totalDesligados++;
                        } else {
                            throw new AutorizacaoControllerException("mensagem.erro.status.registro.servidor.invalido", responsavel);
                        }

                        if (!validar) {
                            // Realiza as alterações
                            RegistroServidorTO registroServidor = new RegistroServidorTO(rseCodigo);
                            if (dataSaida != null) {
                                registroServidor.setRseDataSaida(dataSaida);
                            }
                            if (dataRetorno != null) {
                                registroServidor.setRseDataRetorno(dataRetorno);
                            }
                            if (dataUltimoSalario != null) {
                                registroServidor.setRseDataUltSalario(dataUltimoSalario);
                            }
                            if (!TextHelper.isNull(rsePedidoDemissao)) {
                                registroServidor.setRsePedidoDemissao(rsePedidoDemissao);
                            }
                            registroServidor.setSrsCodigo(srsCodigo);

                            // Altera o registro Servidor
                            registroServidor.setTipoMotivo(tmoCodigo);
                            registroServidor.setOrsObs(tmoObs);

                            servidorController.updateRegistroServidor(registroServidor, false, false, false, responsavel);

                            // Cria uma Ocorrência Registro Servidor
                            servidorController.criaOcorrenciaRSE(registroServidor.getRseCodigo(), CodedValues.TOC_RSE_ALTERACAO_STATUS_SERVIDOR, tmoObs, tmoCodigo, responsavel);
                        }

                    } else {
                        totalProblema++;
                        msgErro = entrada.get("LINHA_INVALIDA").toString().equalsIgnoreCase("S") ? ApplicationResourcesHelper.getMessage("mensagem.linhaInvalida", responsavel) : entrada.get("LINHA_INVALIDA").toString();
                        critica.add(leitor.getLinha() + delimitador + formataMsgErro(msgErro, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    }
                } catch (ParserException e) {
                    gerouException = true;
                    if (e.getMessageKey() != null) {
                        if (e.getMessageKey().indexOf("mensagem.erro.tradutor.linha.cabecalho.entrada.invalida") != -1) {
                            throw new ServidorControllerException(e);
                        } else if (e.getMessageKey().indexOf("mensagem.erro.leitor.arquivo.numero.maximo.linhas") != -1) {
                            throw new ServidorControllerException(e);
                        }
                    }

                    LOG.error("Erro de Parser no Importar Desligado e Bloqueado : " + e.getMessage(), e);

                    totalProblema++;

                    // Gera linha para arquivo de crítica
                    critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, e.getMessage()));

                } catch (Exception e) {
                    String mensagem = e.getMessage();
                    if (e instanceof ZetraException ze) {
                        mensagem = ze.getResourcesMessage(ZetraException.MENSAGEM_LOTE);
                        if (ze.getMessageKey() != null && ze.getMessageKey().equals("mensagem.linhaInvalida")) {
                            mensagem += ": " + e.getMessage();
                        }
                    }
                    totalProblema++;

                    // Gera linha para arquivo de crítica
                    critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, mensagem));
                }

                totalRegistros++;
            }
        } finally {
            if (harCodigo != null) {
                try {
                    String harResultado = CodedValues.STS_ATIVO.toString();
                    if (gerouException) {
                        harResultado = CodedValues.STS_INATIVO.toString();
                    }
                    historicoArquivoController.updateHistoricoArquivo(harCodigo, null, null, null, harResultado, responsavel);
                } catch (HistoricoArquivoControllerException e) {
                    LOG.error("Não foi possível alterar o histórico do arquivo de desligados e bloqueados '" + nomeArquivoEntrada + "'.", e);
                }
            }

            try {
                tradutor.encerraTraducao();
            } catch (ParserException ex) {
                LOG.error(ex.getMessage());
            }
        }

        String nomeArqSaida = null;
        String nomeArqSaidaTxt = null;
        String nomeArqSaidaZip = null;

        try {
            if (critica.size() > 0) {
                // Grava arquivo contendo as parcelas não encontradas no sistema
                // String absolutePath = ParamSist.getDiretorioRaizArquivos();
                LOG.debug("ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());
                String pathSaida = absolutePath + File.separatorChar + "desligado" + File.separatorChar + "cse" + File.separatorChar;
                File diretorio = new File(pathSaida);
                if (!diretorio.exists() && !diretorio.mkdirs()) {
                    throw new ServidorControllerException("mensagem.erro.criacao.diretorio", responsavel, diretorio.getAbsolutePath());
                }

                if (!validar) {
                    nomeArqSaida = pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel);
                } else {
                    nomeArqSaida = pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.validacao.prefixo", responsavel);
                }

                nomeArqSaida += nomeArquivoEntrada + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");
                nomeArqSaidaTxt = nomeArqSaida + ".txt";
                PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaidaTxt)));
                LOG.debug("nomeArqSaidaTxt: " + nomeArqSaidaTxt);

                if (validar) {
                    arqSaida.println(formataMsgErro("", "-", TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.arquivo", responsavel, nomeArquivoEntrada), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.data", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "dd/MM/yyyy-HHmmss")), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro("", "-", TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.total.registros.validados", responsavel, String.valueOf(totalRegistros)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.total.registros.validados.desligado", responsavel, String.valueOf(totalDesligados)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.total.registros.validados.bloqueado", responsavel, String.valueOf(totalBloqueados)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.total.registros.invalidos", responsavel, String.valueOf(totalProblema)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro("", "-", TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro("", COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                }

                if (leitor.getLinhaHeader() != null && !leitor.getLinhaHeader().trim().equals("")) {
                    // Imprime a linha de header no arquivo
                    arqSaida.println(gerarLinhaArquivoSaida(leitor.getLinhaHeader(), delimitador, null));
                }
                // Imprime as linhas de critica no arquivo
                arqSaida.println(TextHelper.join(critica, System.lineSeparator()));
                if (leitor.getLinhaFooter() != null && !leitor.getLinhaFooter().trim().equals("")) {
                    // Imprime a linha de footer no arquivo
                    arqSaida.println(gerarLinhaArquivoSaida(leitor.getLinhaFooter(), delimitador, null));
                }
                arqSaida.close();

                LOG.debug("FIM ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());
                // Compacta os arquvivos gerados em apenas um
                LOG.debug("compacta os arquivos: " + DateHelper.getSystemDatetime());
                nomeArqSaidaZip = nomeArqSaida + ".zip";
                FileHelper.zip(nomeArqSaidaTxt, nomeArqSaidaZip);
                LOG.debug("fim - compacta os arquivos: " + DateHelper.getSystemDatetime());
                FileHelper.delete(nomeArqSaidaTxt);

                if (!validar) {
                    // Log do resultado geral da importação
                    LogDelegate log = null;
                    log = new LogDelegate(responsavel, Log.REGISTRO_SERVIDOR, Log.IMP_DESLIGADO_BLOQUEADO, Log.LOG_INFORMACAO);
                    try {
                        log.add(ApplicationResourcesHelper.getMessage("mensagem.desligado.bloqueado.resumo.importacao", responsavel, String.valueOf(totalRegistros), String.valueOf(totalDesligados), String.valueOf(totalBloqueados), String.valueOf(totalProblema)));
                        log.write();
                    } catch (LogControllerException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            }
        } catch (IOException ex) {
            //LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException(ex);
        }

        try {
            validaVariacaoMaxServidoresAtivos(totalSerAtivoAntigo, responsavel);
        } catch (ServidorControllerException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw e;
        }

        if (!validar) {
            FileHelper.rename(fileName, fileName + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".ok");
        }

        LOG.debug("FIM IMPORTACAO: " + DateHelper.getSystemDatetime());

        return nomeArqSaidaZip;
    }

    private static String gerarLinhaArquivoSaida(String linha, String delimitador, String mensagem) {
        // Concatena a mensagem de erro no final da linha de entrada
        mensagem = (mensagem == null ? "" : mensagem);
        return (linha + delimitador + formataMsgErro(mensagem, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
    }

    private static String formataMsgErro(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        mensagem = (mensagem == null ? "" : mensagem);
        return TextHelper.removeAccent(TextHelper.formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda)).toUpperCase();
    }

    private void validaVariacaoMaxServidoresAtivos(int totalSerAtivoAntigo, AcessoSistema responsavel) throws ServidorControllerException {
        Object tpcPercMaxSerAtivo = ParamSist.getInstance().getParam(CodedValues.TPC_PERC_MAX_VAR_SER_ATIVO_CAD_MARGENS, responsavel);
        float percMaxVarSerAtivo = TextHelper.isNotNumeric((String) tpcPercMaxSerAtivo) ? 0 : Float.parseFloat(tpcPercMaxSerAtivo.toString());
        if (percMaxVarSerAtivo == 0) {
            return;
        }
        int totalSerAtivoAtual = contarRegistroServidor(responsavel);

        LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.total.servidores.ativos.antes.importacao.arg0", responsavel, String.valueOf(totalSerAtivoAntigo)));
        LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.total.servidores.ativos.apos.importacao.arg0", responsavel, String.valueOf(totalSerAtivoAtual)));
        if (totalSerAtivoAntigo > 0) {
            BigDecimal percSerAtivoCalc = new BigDecimal(((totalSerAtivoAtual * 100.00) / totalSerAtivoAntigo) - 100.00);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.percentual.variacao.servidores.ativos.arg0", responsavel, String.valueOf(percSerAtivoCalc.abs().floatValue())));
            if (percSerAtivoCalc.abs().floatValue() > percMaxVarSerAtivo) {
                throw new ServidorControllerException("mensagem.erro.percentual.maximo.variacao.numero.servidores.ativos.importacao.atingido.arg0.arg1.arg2", responsavel, NumberHelper.format(percMaxVarSerAtivo, NumberHelper.getLang()), NumberHelper.formata(totalSerAtivoAntigo, "0"), NumberHelper.formata(totalSerAtivoAtual, "0"));
            }
        }
    }

    private int contarRegistroServidor(AcessoSistema responsavel) throws ServidorControllerException {
        try {
            ListaRegistroServidorQuery query = new ListaRegistroServidorQuery();
            query.count = true;
            query.recuperaRseExcluido = false;
            return query.executarContador();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ServidorControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se o sistema está bloqueado.
     * @return
     */
    protected boolean sistemaBloqueado(AcessoSistema responsavel) {
        boolean bloqueado = false;
        try {
            Short codigo = sistemaController.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            bloqueado = (codigo.equals(CodedValues.STS_INDISP) || codigo.equals(CodedValues.STS_INATIVO));
        } catch (ConsignanteControllerException e1) {
            LOG.error("Não foi possível verificar bloqueio do sistema. " + e1.getMessage());
        }
        return bloqueado;
    }
}
