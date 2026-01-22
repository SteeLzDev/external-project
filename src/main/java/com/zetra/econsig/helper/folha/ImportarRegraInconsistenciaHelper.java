package com.zetra.econsig.helper.folha;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.HistoricoArquivoDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.ImpRetornoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.folha.ImpRetornoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ImportarRegraInconsistenciaHelper.java</p>
 * <p>Description: Helper Class para importações de regra de inconsistências para o sistema eConsig.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ImportarRegraInconsistenciaHelper {
    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ImportarRegraInconsistenciaHelper.class);

    public static final int TAMANHO_MSG_ERRO_DEFAULT = 100;
    public static final String COMPLEMENTO_DEFAULT = " ";

    private final AcessoSistema responsavel;

    /** Objetos para tradução do arquivo de entrada */
    private LeitorArquivoTexto leitor;
    private Escritor escritor;
    private Tradutor tradutor;

    private ImpRetornoController impRetornoController;
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    public ImportarRegraInconsistenciaHelper(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    public String importaInconsistencia(String nomeArquivoEntrada) throws ViewHelperException {
        String tipoEntidade = AcessoSistema.ENTIDADE_CSE;
        String codigoEntidade = CodedValues.CSE_CODIGO_SISTEMA;

        // Grava o arquivo de lote no sistema de arquivo
        String rootPath = ParamSist.getDiretorioRaizArquivos();
        String pathLote = rootPath + File.separator + "inconsistencia" + File.separator + "cse" + File.separator;

        // Verifica se o caminho para a gravação existe
        File dir = new java.io.File(pathLote);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new ViewHelperException("mensagem.erro.diretorio.inexistente", responsavel);
        }

        // Se o sistema estiver bloqueado ou inativo, nenhum arquivo de lote pode ser processado
        if (sistemaBloqueado()) {
            throw new ViewHelperException("mensagem.erro.sistema.bloqueado.inativo", responsavel);
        }

        // Recupera parâmetros de configuração do sistema
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String pathLoteDefault = absolutePath + File.separatorChar + "conf" + File.separatorChar;

        // Recupera layout de importação dos inconsistencias
        String nomeArqXmlEntrada = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_REGRA_INCONSISTENCIA, responsavel);
        String nomeArqXmlTradutor = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_REGRA_INCONSISTENCIA, responsavel);

        if (TextHelper.isNull(nomeArqXmlEntrada) || TextHelper.isNull(nomeArqXmlTradutor)) {
            throw new ViewHelperException("mensagem.erro.sistema.arquivos.importacao.inconsistencia.ausentes", responsavel);
        }

        String entradaImpLote = null;
        String tradutorImpLote = null;

        String entradaImpLoteDefault = pathLoteDefault + nomeArqXmlEntrada;
        String tradutorImpLoteDefault = pathLoteDefault + nomeArqXmlTradutor;

        File arqConfEntradaDefault = new File(entradaImpLoteDefault);
        File arqConfTradutorDefault = new File(tradutorImpLoteDefault);
        if (!arqConfEntradaDefault.exists() || !arqConfTradutorDefault.exists()) {
            throw new ViewHelperException("mensagem.erro.sistema.arquivos.importacao.inconsistencia.ausentes", responsavel);
        } else {
            entradaImpLote = entradaImpLoteDefault;
            tradutorImpLote = tradutorImpLoteDefault;
        }

        String fileName = absolutePath + File.separatorChar + "inconsistencia" + File.separatorChar + "cse" + File.separatorChar + nomeArquivoEntrada;

        // Verifica se o arquivo existe
        File arqEntrada = new File(fileName);
        if (!arqEntrada.exists()) {
            throw new ViewHelperException("mensagem.erro.sistema.arquivo.inconsistencia.nao.encontrado", responsavel);
        }

        // Renomeia o arquivo antes de iniciar o processamento
        FileHelper.rename(fileName, fileName + ".prc");
        fileName += ".prc";

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

        // Inicializa os controllers necessarios
        try {
            impRetornoController =  ApplicationContextProvider.getApplicationContext().getBean(ImpRetornoController.class);
            pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.falhaComunicacao", responsavel);
        }

        try {
            // Grava Log para auditoria
            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.IMP_INCONSISTENCIA, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.arquivo", responsavel, nomeArquivoEntrada));
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.numero.linhas.arquivo", responsavel, String.valueOf(FileHelper.getNumberOfLines(fileName))));
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.leiaute", responsavel, nomeArqXmlEntrada, nomeArqXmlTradutor));

            log.write();
        } catch (LogControllerException ex) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
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
            HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
            harCodigo = hisArqDelegate.createHistoricoArquivo(tipoEntidade, codigoEntidade, TipoArquivoEnum.ARQUIVO_REGRA_INCONSISTENCIA, fileName, harObs, null, null, harResultado, responsavel);
        } catch (HistoricoArquivoControllerException e) {
            LOG.error("Não foi possível inserir o histórico do arquivo de inconsistencia '" + nomeArquivoEntrada + "'.", e);
        }

        String msgErro;

        try {
            tradutor.iniciaTraducao();
        } catch (ParserException e) {
            LOG.error("Erro em iniciar tradução.");
            throw new ViewHelperException(e);
        }

        boolean proximo = true;
        try {
            // Faz o loop de cada linha do arquivo para realizar as traduções
            while (proximo) {
                try {
                    proximo = tradutor.traduzProximo();
                    if (!proximo) {
                        break;
                    }

                    msgErro = "";

                    // Criar rotina para importação de arquivo de inconsistencias, configurada em leiaute XML, contendo os campos: EST_IDENTIFICADOR, ORG_IDENTIFICADOR, RSE_MATRICULA, SER_CPF, sendo a matrícula ou cpf obrigatórios.
                    if (entrada.get("LINHA_INVALIDA") == null || entrada.get("LINHA_INVALIDA").toString().equals("N")) {
                        String adeNumero = (String) entrada.get("ADE_NUMERO");
                        String iiaObs = (String) entrada.get("IIA_OBS");
                        String iiaItem = (String) entrada.get("IIA_ITEM");
                        String iiaPermanente = (String) entrada.get("IIA_PERMANENTE");
                        String iiaData = (String) entrada.get("IIA_DATA");

                        if (TextHelper.isNull(adeNumero)) {
                            throw new ZetraException("mensagem.informe.codigo.contrato", responsavel);
                        }

                        if (TextHelper.isNull(iiaItem)) {
                            throw new ZetraException("mensagem.informe.codigo.regra.inconsistencia", responsavel);
                        }

                        Date dataInconsistencia = null;
                        if (TextHelper.isNull(iiaData)) {
                            dataInconsistencia = DateHelper.getSystemDatetime();
                        } else {
                            dataInconsistencia = DateHelper.parse(iiaData, "yyyy-MM-dd");
                        }

                        Short itemRegraInconsistencia = 0;

                        try {
                            itemRegraInconsistencia = !TextHelper.isNull(iiaItem) ? Short.valueOf(iiaItem) : 0;
                        } catch (NumberFormatException nex) {
                            throw new ZetraException("mensagem.erro.valor.numerico.campo.item", responsavel);
                        }

                        Boolean regraPermanente = false;

                        try {
                            regraPermanente = !TextHelper.isNull(iiaPermanente) && iiaPermanente.equals("1");
                        } catch (NumberFormatException nex) {
                            throw new ZetraException("mensagem.erro.valor.numerico.campo.permanente", responsavel);
                        }

                        TransferObject ade = pesquisarConsignacaoController.findAutDescontoByAdeNumero(Long.valueOf(adeNumero), responsavel);

                        try {
                            impRetornoController.importarRegraInconsistencia((String) ade.getAttribute(Columns.ADE_CODIGO), iiaObs, itemRegraInconsistencia, dataInconsistencia, regraPermanente, responsavel);
                        } catch (ImpRetornoControllerException ex) {
                            throw new ZetraException(ex);
                        }
                    } else {
                        msgErro = entrada.get("LINHA_INVALIDA").toString().equalsIgnoreCase("S") ? ApplicationResourcesHelper.getMessage("mensagem.linhaInvalida", responsavel) + "." : entrada.get("LINHA_INVALIDA").toString();
                        critica.add(leitor.getLinha() + delimitador + formataMsgErro(msgErro, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    }
                } catch (ParserException e) {
                    if (e.getMessageKey().indexOf("mensagem.erro.tradutor.linha.cabecalho.entrada.invalida") != -1) {
                        throw new ViewHelperException(e);
                    } else if (e.getMessageKey().indexOf("mensagem.erro.leitor.arquivo.numero.maximo.linhas") != -1) {
                        throw new ViewHelperException(e);
                    }

                    LOG.error("Erro de Parser no Importar inconsistencia Helper mensagem: " + e.getMessage(), e);

                    // Gera linha para arquivo de crítica
                    critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, e.getMessage()));

                } catch (Exception e) {
                    gerouException = true;
                    String mensagem = e.getMessage();
                    if (e instanceof ZetraException) {
                        ZetraException ze = (ZetraException) e;
                        mensagem = ze.getResourcesMessage(ZetraException.MENSAGEM_LOTE);
                        if (ze.getMessageKey() != null && ze.getMessageKey().equals("mensagem.linhaInvalida")) {
                            mensagem += ": " + e.getMessage();
                        }
                    }

                    // Gera linha para arquivo de crítica
                    critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, mensagem));
                }
            }
        } finally {
            // Renomeia o arquivo processado para .ok
            FileHelper.rename(fileName, fileName + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".ok");

            if (harCodigo != null) {
                try {
                    String harResultado = CodedValues.STS_ATIVO.toString();
                    if (gerouException) {
                        harResultado = CodedValues.STS_INATIVO.toString();
                    }
                    HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                    hisArqDelegate.updateHistoricoArquivo(harCodigo, null, null, null, harResultado, responsavel);
                } catch (HistoricoArquivoControllerException e) {
                    LOG.error("Não foi possível alterar o histórico do arquivo de inconsistencia '" + nomeArquivoEntrada + "'.", e);
                }
            }

            try {
                tradutor.encerraTraducao();
            } catch (ParserException ex) {
                LOG.error(ex.getMessage());
            }
        }

        String nomeArqSaida, nomeArqSaidaTxt, nomeArqSaidaZip;
        try {
            if (critica.size() > 0) {
                // Grava arquivo contendo as parcelas não encontradas no sistema
                // String absolutePath = ParamSist.getDiretorioRaizArquivos();
                LOG.debug("ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());
                String pathSaida = absolutePath + File.separatorChar + "inconsistencia" + File.separatorChar + "cse" + File.separatorChar;
                File diretorio = new File(pathSaida);
                if (!diretorio.exists() && !diretorio.mkdirs()) {
                    throw new ViewHelperException("mensagem.erro.criacao.diretorio", responsavel, diretorio.getAbsolutePath());
                }

                nomeArqSaida = pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel);
                nomeArqSaida += nomeArquivoEntrada + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");
                nomeArqSaidaTxt = nomeArqSaida + ".txt";
                PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaidaTxt)));
                LOG.debug("nomeArqSaidaTxt: " + nomeArqSaidaTxt);

                if (leitor.getLinhaHeader() != null && !leitor.getLinhaHeader().trim().equals("")) {
                    // Imprime a linha de header no arquivo
                    arqSaida.println(gerarLinhaArquivoSaida(leitor.getLinhaHeader(), delimitador, null));
                }
                // Imprime as linhas de critica no arquivo
                arqSaida.println(TextHelper.join(critica, System.getProperty("line.separator")));
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

                return nomeArqSaidaZip;
            }
        } catch (IOException ex) {
            //LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(ex);
        }

        LOG.debug("FIM IMPORTACAO: " + DateHelper.getSystemDatetime());

        return null;
    }

    /**
     * Verifica se o sistema está bloqueado.
     * @return
     */
    protected boolean sistemaBloqueado() {
        boolean bloqueado = false;
        try {
            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            Short codigo = cseDelegate.verificaBloqueioSistema(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            bloqueado = (codigo.equals(CodedValues.STS_INDISP) || codigo.equals(CodedValues.STS_INATIVO));
        } catch (ConsignanteControllerException e1) {
            LOG.error("Não foi possível verificar bloqueio do sistema. " + e1.getMessage());
        }
        return bloqueado;
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
}
