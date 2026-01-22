package com.zetra.econsig.helper.rescisao;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.HistoricoArquivoDelegate;
import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.VerbaRescisoriaControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.seguranca.ControleRestricaoAcesso;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.entity.VerbaRescisoriaRse;
import com.zetra.econsig.service.rescisao.VerbaRescisoriaController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusVerbaRescisoriaEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.web.filter.XSSPreventionFilter;

public class RescisaoHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RescisaoHelper.class);

    public static final int TAMANHO_MSG_ERRO_DEFAULT = 100;

    public static final String COMPLEMENTO_DEFAULT = " ";

    private int totalRescisoesInformado;

    private int totalRegistros;

    private int totalIncluidos;

    private int totalAlterados;

    private int totalProblema;

    private LeitorArquivoTexto leitor;

    private Escritor escritor;

    private Tradutor tradutor;

    private final AcessoSistema responsavel;

    public RescisaoHelper(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    public String importar(String nomeArquivo, boolean validar, AcessoSistema responsavel) throws ViewHelperException, VerbaRescisoriaControllerException {

        VerbaRescisoriaController verbaRescisoriaController = ApplicationContextProvider.getApplicationContext().getBean(VerbaRescisoriaController.class);
        PesquisarServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarServidorController.class);
        String codEntidade = null;
        String tipoEntidade = null;
        CustomTransferObject svr = new CustomTransferObject();
        List<String> svrCodigos = new ArrayList<>();

        svrCodigos.add(StatusVerbaRescisoriaEnum.CONCLUIDO.getCodigo());
        svrCodigos.add(StatusVerbaRescisoriaEnum.AGUARDANDO_VERBA_RESCISORIA.getCodigo());
        svrCodigos.add(StatusVerbaRescisoriaEnum.CANDIDATO.getCodigo());
        svr.setAttribute(Columns.VRR_SVR_CODIGO, svrCodigos);

        List<TransferObject> listaVerbaRescisoria = null;
        try {
            listaVerbaRescisoria = verbaRescisoriaController.listarVerbaRescisoriaRse(svr, -1, -1, responsavel);
        } catch (VerbaRescisoriaControllerException e) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel);
        }

        HashMap<String, TransferObject> hashVerba = new HashMap<>();

        for (TransferObject list : listaVerbaRescisoria){
            hashVerba.put((String) list.getAttribute(Columns.RSE_MATRICULA) + list.getAttribute(Columns.SER_CPF), list);
        }

        // Verifica sistema de arquivo
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String pathRescisao = absolutePath + File.separatorChar + "rescisao" + File.separatorChar;
        if(responsavel.isCseSup()) {
            pathRescisao += "cse" + File.separatorChar;
            codEntidade = responsavel.getCodigoEntidade();
            tipoEntidade = AcessoSistema.ENTIDADE_CSE;
        } else if (responsavel.isOrg() && responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
            pathRescisao += "est" + File.separatorChar + responsavel.getCodigoEntidadePai() + File.separatorChar;
            codEntidade = responsavel.getCodigoEntidadePai();
            tipoEntidade = AcessoSistema.ENTIDADE_EST;
        }  else if (responsavel.isOrg()) {
            pathRescisao += "cse" + File.separatorChar + responsavel.getCodigoEntidade() + File.separatorChar;
            codEntidade = responsavel.getCodigoEntidade();
            tipoEntidade = AcessoSistema.ENTIDADE_CSE;
        }

        File dir = new java.io.File(pathRescisao);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new ViewHelperException("mensagem.erro.criacao.diretorio", responsavel);
        }

        // Se o sistema estiver bloqueado ou inativo, nenhum arquivo de lote pode ser processado
        if (sistemaBloqueado()) {
            throw new ViewHelperException("mensagem.erro.sistema.bloqueado.inativo", responsavel);
        }

        String pathRescisaoDefault = absolutePath + File.separatorChar + "conf" + File.separatorChar + "rescisao" + File.separatorChar;

        // Recupera layout de importação do saldo devedor
        String nomeArqXmlEntrada = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_RESCISAO, responsavel);
        String nomeArqXmlTradutor = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_RESCISAO, responsavel);


        if (TextHelper.isNull(nomeArqXmlEntrada) || TextHelper.isNull(nomeArqXmlTradutor)) {
            throw new ViewHelperException("mensagem.erro.sistema.arquivos.importacao.rescisao.ausentes", responsavel);
        }

        String entradaRescisao = null;
        String tradutorRescisao = null;
        String entradaImpSaldoDevedorDefault = pathRescisaoDefault + nomeArqXmlEntrada;
        String tradutorImpSaldoDevedorDefault = pathRescisaoDefault + nomeArqXmlTradutor;

        File arqConfEntradaDefault = new File(entradaImpSaldoDevedorDefault);
        File arqConfTradutorDefault = new File(tradutorImpSaldoDevedorDefault);
        if (!arqConfEntradaDefault.exists() || !arqConfTradutorDefault.exists()) {
            throw new ViewHelperException("mensagem.erro.sistema.arquivos.importacao.rescisao.ausentes", responsavel);
        } else {
            entradaRescisao = entradaImpSaldoDevedorDefault;
            tradutorRescisao = tradutorImpSaldoDevedorDefault;
        }

        String fileName = pathRescisao + nomeArquivo;

        // Verifica se o arquivo existe
        File arqEntrada = new File(fileName);
        if (!arqEntrada.exists()) {
            throw new ViewHelperException("mensagem.erro.arquivo.nao.encontrado", responsavel, nomeArquivo);
        }

        // Configura o leitor de acordo com o arquivo de entrada
        if (fileName.toLowerCase().endsWith(".zip") || fileName.toLowerCase().endsWith(".zip.prc")) {
            LOG.debug("nome do arquivo ... " + fileName);
            leitor = new LeitorArquivoTextoZip(entradaRescisao, fileName);
        } else {
            LOG.debug("nome do arquivo ... " + fileName);
            leitor = new LeitorArquivoTexto(entradaRescisao, fileName);
        }

        HashMap<String, Object> entrada = new HashMap<>();

        // Escritor e tradutor
        escritor = new EscritorMemoria(entrada);
        tradutor = new Tradutor(tradutorRescisao, leitor, escritor);

        try {
            // Grava Log para auditoria
            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.IMP_RESCISAO, Log.LOG_INFORMACAO);
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.arquivo", responsavel, nomeArquivo));
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.numero.linhas.arquivo", responsavel, String.valueOf(FileHelper.getNumberOfLines(fileName))));
            log.add(ApplicationResourcesHelper.getMessage("rotulo.log.leiaute", responsavel, nomeArqXmlEntrada, nomeArqXmlTradutor));
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
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

        Long harCodigo = null;
        boolean gerouException = false;
        if(!validar) {
            try {
                String harObs = "";
                String harResultado = CodedValues.STS_INATIVO.toString();
                HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                harCodigo = hisArqDelegate.createHistoricoArquivo(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), TipoArquivoEnum.ARQUIVO_LOTE_RESCISAO, fileName, harObs, null, null, harResultado, responsavel);
            } catch (HistoricoArquivoControllerException ex) {
                LOG.error(ex.getMessage(), ex);
                LOG.error("Não foi possível inserir o histórico do arquivo de rescisão '" + nomeArquivo + "'.", ex);
            }
        }

        String msgErro;

        try {
            tradutor.iniciaTraducao(true);
        } catch (ParserException ex) {
            LOG.error("Erro em iniciar tradução.", ex);
            throw new ViewHelperException(ex);
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
                    if (entrada.get("LINHA_INVALIDA") == null || entrada.get("LINHA_INVALIDA").toString().equals("N")) {
                        // Realiza a validação de segurança contra-ataque de XSS nos campos do lote
                        for (String key : entrada.keySet()) {
                            Object value = entrada.get(key);
                            if (value instanceof String) {
                                // Se for String, realiza o tratamento anti-XSS
                                entrada.put(key, XSSPreventionFilter.stripXSS((String) value));
                            }
                        }
                        String matricula = (String) entrada.get("RSE_MATRICULA");
                        String cpf = (String) entrada.get("SER_CPF");
                        String orgIdentificador = !TextHelper.isNull(entrada.get("ORG_IDENTIFICADOR")) ? (String) entrada.get("ORG_IDENTIFICADOR") : null;
                        BigDecimal vrrVlr = !TextHelper.isNull(entrada.get("VRR_VALOR")) ? NumberHelper.parseDecimal(entrada.get("VRR_VALOR").toString()) : null;

                        if (TextHelper.isNull(matricula) && TextHelper.isNull(cpf)) {
                            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.informe.matricula.cpf.servidor", responsavel));
                            throw new ViewHelperException("mensagem.informe.matricula.cpf.servidor", responsavel);
                        }

                        TransferObject servidor = null;

                            List<TransferObject> listServidores = servidorController.pesquisaServidor(tipoEntidade, codEntidade, null, orgIdentificador, matricula, cpf, responsavel);
                            if (listServidores == null || listServidores.isEmpty()) {
                                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.nenhumServidorEncontrado", responsavel));
                                throw new AutorizacaoControllerException("mensagem.nenhumServidorEncontrado", responsavel);
                            } else if (listServidores.size() > 1) {
                                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.multiplosServidoresEncontrados", responsavel));
                                throw new AutorizacaoControllerException("mensagem.multiplosServidoresEncontrados", responsavel);
                            }

                        servidor = listServidores.get(0);
                        VerbaRescisoriaRse verbaRecisoria = new VerbaRescisoriaRse();

                        int createOrUpdate = 0;
                        TransferObject vr = hashVerba.get(matricula+cpf);

                        if (vr != null){
                            if (!vr.getAttribute(Columns.SVR_CODIGO).equals(StatusVerbaRescisoriaEnum.CONCLUIDO.getCodigo())) {
                                if(responsavel.isOrg() && tipoEntidade.equals(AcessoSistema.ENTIDADE_EST) && servidor.getAttribute(Columns.EST_CODIGO).equals(responsavel.getCodigoEntidadePai()) ||
                                   responsavel.isOrg() && tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE) && servidor.getAttribute(Columns.ORG_CODIGO).equals(responsavel.getCodigoEntidade()) ||
                                   responsavel.getTipoEntidade().equals(AcessoSistema.ENTIDADE_CSE) && responsavel.isCseSup()) {
                                    createOrUpdate = 1;
                                    verbaRecisoria.setVrrCodigo((String) vr.getAttribute(Columns.VRR_CODIGO));
                                    verbaRecisoria.setRseCodigo((String) servidor.getAttribute(Columns.RSE_CODIGO));
                                    verbaRecisoria.setSvrCodigo((String) vr.getAttribute(Columns.SVR_CODIGO));
                                    verbaRecisoria.setVrrDataIni((Date) vr.getAttribute(Columns.VRR_DATA_INI));
                                    verbaRecisoria.setVrrDataFim((Date) vr.getAttribute(Columns.VRR_DATA_FIM));
                                    verbaRecisoria.setVrrDataUltAtualizacao(new java.sql.Timestamp(Calendar.getInstance().getTimeInMillis()));
                                    verbaRecisoria.setVrrValor(vrrVlr);
                                    verbaRecisoria.setVrrProcessado((String) vr.getAttribute(Columns.VRR_PROCESSADO));
                                    totalAlterados++;
                                } else {
                                    throw new ViewHelperException("mensagem.entidadeDivergenteResponsavel", responsavel);
                                }
                            } else {
                                throw new ViewHelperException("mensagem.verbaRescisoriaExiste", responsavel);
                            }
                        } else {
                            if (responsavel.isOrg() && tipoEntidade.equals(AcessoSistema.ENTIDADE_EST) &&
                                    servidor.getAttribute(Columns.EST_CODIGO).equals(responsavel.getCodigoEntidadePai()) ||
                                    responsavel.isOrg() && tipoEntidade.equals(AcessoSistema.ENTIDADE_CSE) &&
                                            servidor.getAttribute(Columns.ORG_CODIGO).equals(responsavel.getCodigoEntidade()) ||
                                    responsavel.getTipoEntidade().equals(AcessoSistema.ENTIDADE_CSE) && responsavel.isCseSup()) {
                                verbaRecisoria.setRseCodigo((String) servidor.getAttribute(Columns.RSE_CODIGO));
                                verbaRecisoria.setVrrValor(vrrVlr);
                                totalIncluidos++;
                            } else {
                                throw new ViewHelperException("mensagem.entidadeDivergenteResponsavel", responsavel);
                            }
                        }

                        if (createOrUpdate == 0 && !validar) {
                            verbaRescisoriaController.createVerbaRescisoriaLote(verbaRecisoria);
                        } else if (createOrUpdate == 1 && !validar) {
                            if(vrrVlr == null || vrrVlr.compareTo(BigDecimal.ZERO) < 1) {
                                throw new ViewHelperException("mensagem.verba.rescisoria.valor.obrigatorio", responsavel);
                            }
                            verbaRescisoriaController.confirmarVerbaRescisoria(vr.getAttribute(Columns.VRR_CODIGO).toString(), vrrVlr, responsavel);
                        }

                        if (validar) {
                            String mensagem = createOrUpdate == 0 ? ApplicationResourcesHelper.getMessage("mensagem.inclusaoValidada.lote", responsavel) : ApplicationResourcesHelper.getMessage("mensagem.alteracaoValidada", responsavel);
                            critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, mensagem));
                        }
                    } else {
                        totalProblema++;
                        msgErro = entrada.get("LINHA_INVALIDA").toString().equalsIgnoreCase("S") ? ApplicationResourcesHelper.getMessage("mensagem.linhaInvalida", responsavel) : entrada.get("LINHA_INVALIDA").toString();
                        critica.add(leitor.getLinha() + delimitador + formataMsgErro(msgErro, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    }

                } catch (ParserException ex) {
                    gerouException = true;
                    if (ex.getMessageKey().indexOf("mensagem.erro.tradutor.linha.cabecalho.entrada.invalida") != -1 || ex.getMessageKey().indexOf("mensagem.erro.leitor.arquivo.numero.maximo.linhas") != -1) {
                        throw new ViewHelperException(ex);
                    }

                    LOG.error("Erro de parser na Importação de rescisão: " + ex.getMessage(), ex);

                    StringBuilder mensagem = new StringBuilder(ex.getMessage());
                    if (ex instanceof ZetraException) {
                        ZetraException ze = ex;
                        mensagem = new StringBuilder(ze.getResourcesMessage(ZetraException.MENSAGEM_LOTE));
                        if (ze.getMessageKey() != null && ze.getMessageKey().equals("mensagem.linhaInvalida")) {
                            mensagem.append(": ").append(ex.getMessage());
                        }
                    }
                    totalProblema++;

                    // Gera linha para arquivo de crítica
                    critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, mensagem.toString()));
                } catch (Exception ex) {
                    StringBuilder mensagem = new StringBuilder(ex.getMessage());
                    if (ex instanceof ZetraException) {
                        ZetraException ze = (ZetraException) ex;
                        mensagem = new StringBuilder(ze.getResourcesMessage(ZetraException.MENSAGEM_LOTE));
                        if (ze.getMessageKey() != null && ze.getMessageKey().equals("mensagem.linhaInvalida")) {
                            mensagem.append(": ").append(ex.getMessage());
                        }
                    }
                    totalProblema++;

                    // Gera linha para arquivo de crítica
                    critica.add(gerarLinhaArquivoSaida(leitor.getLinha(), delimitador, mensagem.toString()));
                }

                totalRegistros++;
            }

        } finally {
            if (!validar) {
                FileHelper.rename(fileName, fileName + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".ok");
            }

            if (harCodigo != null) {
                try {
                    String harResultado = CodedValues.STS_ATIVO.toString();
                    if (gerouException) {
                        harResultado = CodedValues.STS_INATIVO.toString();
                    }
                    HistoricoArquivoDelegate hisArqDelegate = new HistoricoArquivoDelegate();
                    hisArqDelegate.updateHistoricoArquivo(harCodigo, null, null, null, harResultado, responsavel);
                } catch (HistoricoArquivoControllerException e) {
                    LOG.error("Não foi possível alterar o histórico do arquivo de saldo devedor '" + nomeArquivo + "'.", e);
                }
            }

            try {
                tradutor.encerraTraducao();
            } catch (ParserException ex) {
                LOG.error(ex.getMessage());
            }
        }

        String nomeArqSaida;
        String nomeArqSaidaTxt;
        String nomeArqSaidaZip;
        try {
            if (!critica.isEmpty()) {
                LOG.debug("ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());
                String pathSaida = pathRescisao + File.separatorChar;
                File diretorio = new File(pathSaida);
                if (!diretorio.exists() && !diretorio.mkdirs()) {
                    throw new ViewHelperException("mensagem.erro.criacao.diretorio", responsavel, diretorio.getAbsolutePath());
                }

                if(!validar) {
                    nomeArqSaida = pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel);
                } else {
                    nomeArqSaida = pathSaida + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.validacao.prefixo", responsavel);
                }
                nomeArqSaida += nomeArquivo + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss");
                nomeArqSaidaTxt = nomeArqSaida + ".txt";
                PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaidaTxt)));
                LOG.debug("nomeArqSaidaTxt: " + nomeArqSaidaTxt);

                if (leitor.getLinhaHeader() != null && !leitor.getLinhaHeader().trim().equals("")) {
                    arqSaida.println(gerarLinhaArquivoSaida(leitor.getLinhaHeader(), delimitador, null));
                }
                if (validar) {
                    arqSaida.println(formataMsgErro("", "-", TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.arquivo.rescisao", responsavel, nomeArquivo), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.data", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "dd/MM/yyyy-HHmmss")), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro("", "-", TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.total.registros", responsavel, String.valueOf(totalRegistros)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.total.registros.validados.inclusao", responsavel, String.valueOf(totalIncluidos)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.total.registros.validados.alteracao", responsavel, String.valueOf(totalAlterados)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro(ApplicationResourcesHelper.getMessage("rotulo.arq.validacao.lote.total.registros.invalidos", responsavel, String.valueOf(totalProblema)), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro("", "-", TAMANHO_MSG_ERRO_DEFAULT, true));
                    arqSaida.println(formataMsgErro("", COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                }
                arqSaida.println(TextHelper.join(critica, System.getProperty("line.separator")));
                if (leitor.getLinhaFooter() != null && !leitor.getLinhaFooter().trim().equals("")) {
                    arqSaida.println(gerarLinhaArquivoSaida(leitor.getLinhaFooter(), delimitador, null));
                }
                arqSaida.close();

                LOG.debug("FIM ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());
                LOG.debug("compacta os arquivos: " + DateHelper.getSystemDatetime());
                nomeArqSaidaZip = nomeArqSaida + ".zip";
                FileHelper.zip(nomeArqSaidaTxt, nomeArqSaidaZip);
                LOG.debug("fim - compacta os arquivos: " + DateHelper.getSystemDatetime());
                FileHelper.delete(nomeArqSaidaTxt);

                logResumoProcessamento();

                return nomeArqSaidaZip;
            }
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(ex);
        }

        logResumoProcessamento();

        LOG.debug("FIM IMPORTACAO: " + DateHelper.getSystemDatetime());

        return null;
    }

    private void logResumoProcessamento() {
        LogDelegate log = null;
        log = new LogDelegate(responsavel, Log.ARQUIVO, Log.IMP_RESCISAO, Log.LOG_INFORMACAO);
        try {
            log.add(ApplicationResourcesHelper.getMessage("mensagem.arquivo.rescisao.resumo.importacao", responsavel, String.valueOf(totalRegistros), String.valueOf(totalRescisoesInformado), String.valueOf(totalProblema)));
            log.write();
        } catch (LogControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    private static String gerarLinhaArquivoSaida(String linha, String delimitador, String mensagem) {
        mensagem = (mensagem == null ? "" : mensagem);
        return (linha + delimitador + formataMsgErro(mensagem, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
    }

    private static String formataMsgErro(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        mensagem = (mensagem == null ? "" : mensagem);
        return TextHelper.removeAccent(TextHelper.formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda)).toUpperCase();
    }


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

}
