package com.zetra.econsig.helper.folha;

import static com.zetra.econsig.helper.lote.LoteHelper.INCLUSAO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.HistoricoRetMovFinDAO;
import com.zetra.econsig.service.folha.ImportaHistoricoController;
import com.zetra.econsig.service.sdp.PlanoDescontoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: HistoricoHelper.java</p>
 * <p>Description: Helper Class para importações de arquivos de histórico.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class HistoricoHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(HistoricoHelper.class);

    public static final int TAMANHO_MSG_ERRO_DEFAULT = 100;
    public static final String COMPLEMENTO_DEFAULT = " ";

    /** Quantidade de Threads de geração de parcelas **/
    private static final int QTD_THREADS_GERADOR_PARCELA = 4;

    private static final String QUERY_INSERE_PARCELA = "INSERT INTO tb_parcela_desconto (ADE_CODIGO, PRD_NUMERO, SPD_CODIGO, PRD_DATA_DESCONTO, PRD_DATA_REALIZADO, PRD_VLR_PREVISTO, PRD_VLR_REALIZADO) VALUES (?, ?, ?, ?, ?, ?, ?)";
    private static final String QUERY_INSERE_OCORRENCIA_PARCELA = "INSERT INTO tb_ocorrencia_parcela (OCP_CODIGO, TOC_CODIGO, USU_CODIGO, OCP_DATA, OCP_OBS, PRD_CODIGO) SELECT ?, ?, ?, ?, ?, PRD_CODIGO FROM tb_parcela_desconto WHERE ADE_CODIGO = ? AND PRD_NUMERO = ?";

    /**
     *
     * Importa lote histórico de consignações.
     * @param pathArquivo : caminho dos arquivos para importação
     * @param pathXml : caminho dos arquivos de configuração para importação
     * @param cseCodigo : código do consignante
     * @param nomeArquivoEntrada : nome do arquivo contendo os dados
     * @param nomeArqXmlEntrada  : nome do arquivo xml de configuração do leitor
     * @param nomeArqXmlTradutor : nome do arquivo xml de configuração do tradutor
     * @param validaReserva : true para executar a validação dos dados da reserva
     * @param permitirValidacaoTaxa : true para executar validação de taxa de juros
     * @param serAtivo : true se deve validar servidor ativo
     * @param cnvAtivo : true se deve validar convênio ativo
     * @param svcAtivo : true se deve validar serviço ativo
     * @param serCnvAtivo : true se deve validar convênio de servidor ativo
     * @param csaAtivo : true se deve validar consignatária ativa
     * @param orgAtivo : true se deve validar órgão ativo
     * @param estAtivo : true se deve validar estabelecimento ativo
     * @param cseAtivo : true se deve validar consignante ativo
     * @param importaDadosAde : true para importar dados para a tabela tb_dados_autorizacao_desconto
     * @param retornaAdeNum : true para gerar resultado de sucesso com o número da ADE
     * @param selecionaPrimeiroCnvDisponivel : true para selecionar o primeiro CNV disponível, caso retorne mais de um
     * @param margemParam : opções de inclusão avançada
     * @param responsavel : responsável pela operação
     * @throws ConsignanteControllerException
     */
    public void importaLoteConsignacao(String pathArquivo, String pathXml, String cseCodigo, String nomeArquivoEntrada,
            String nomeArqXmlEntrada, String nomeArqXmlTradutor, boolean validaReserva, boolean permitirValidacaoTaxa,
            boolean serAtivo, boolean cnvAtivo, boolean svcAtivo, boolean serCnvAtivo, boolean csaAtivo, boolean orgAtivo, boolean estAtivo, boolean cseAtivo,
            boolean importaDadosAde, boolean retornaAdeNum, boolean selecionaPrimeiroCnvDisponivel, ReservarMargemParametros margemParam, AcessoSistema responsavel) throws ConsignanteControllerException {

        String entradaImpLote = pathXml + File.separatorChar + nomeArqXmlEntrada;
        String tradutorImpLote = pathXml + File.separatorChar + nomeArqXmlTradutor;

        File arqConfEntrada = new File(entradaImpLote);
        File arqConfTradutor = new File(tradutorImpLote);
        if (!arqConfEntrada.exists() || !arqConfTradutor.exists()) {
            throw new ConsignanteControllerException("mensagem.erro.lote.arquivos.configuracao.importacao.ausentes", responsavel);
        }

        // Verifica o arquivo de entrada de dados
        String fileName = pathArquivo + File.separatorChar + nomeArquivoEntrada;

        File arqEntrada = new File(fileName);
        if (!arqEntrada.exists()) {
            throw new ConsignanteControllerException("mensagem.erro.lote.arquivo.nao.encontrado.servidor", responsavel);
        }

        boolean erroDuranteProcessamento = Boolean.FALSE;

        // Lista para armazenar as linhas de entrada que foram criticadas
        List<String> critica = new ArrayList<>();

        // Lista para armazenar os contratos que terão parcelas criadas
        List<Map<String, Object>> adeInsereParcelas = new ArrayList<>();

        // Cache dos dados dos convênios
        Map<String, Map<String, Object>> cacheConvenio = new HashMap<>();

        // Cache dos dados de Planos (SDP)
        HashMap<String, TransferObject> cachePlanos = null;

        // Leitor do arquivo de entrada
        LeitorArquivoTexto leitor = null;

        try {
            //Enquanto estiver em processamento o arquivo é renomeado para .prc
            File arquivo = new File(fileName);
            arquivo.renameTo(new File(fileName + ".prc"));

            // Configura o leitor de acordo com o arquivo de entrada
            if (fileName.toLowerCase().endsWith(".zip")) {
                leitor = new LeitorArquivoTextoZip(entradaImpLote, fileName + ".prc");
            } else {
                leitor = new LeitorArquivoTexto(entradaImpLote, fileName  + ".prc");
            }

            // Hash que recebe os dados do que serão lidos do arquivo de entrada
            Map<String, Object> entrada = new HashMap<>();

            // Escritor e tradutor
            Escritor escritor = new EscritorMemoria(entrada);
            Tradutor tradutor = new Tradutor(tradutorImpLote, leitor, escritor);

            // Cria os delegates necessários
            String hoje = DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMdd");

            // Verifica se módulo SDP esta ativo
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_SDP, CodedValues.TPC_SIM, responsavel)) {
                try {
                    CustomTransferObject criterio = null;
                    criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.PLA_ATIVO, CodedValues.STS_ATIVO);
                    criterio.setAttribute(Columns.NPL_CODIGO, false); // evita listar planos de taxa de uso
                    cachePlanos = new HashMap<>();
                    PlanoDescontoController planoDescontoController = ApplicationContextProvider.getApplicationContext().getBean(PlanoDescontoController.class);
                    List<TransferObject> planos = planoDescontoController.lstPlanoDescontoSemRateio(criterio, -1, -1, responsavel);
                    for (TransferObject plano : planos) {
                        cachePlanos.put(plano.getAttribute(Columns.CSA_IDENTIFICADOR) + ";" + plano.getAttribute(Columns.PLA_IDENTIFICADOR), plano);
                    }
                } catch (Exception ex) {
                    cachePlanos = new HashMap<>();
                }
            }

            ImportaHistoricoController importaHistoricoController = ApplicationContextProvider.getApplicationContext().getBean(ImportaHistoricoController.class);

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.inicio.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            tradutor.iniciaTraducao();
            while (true) {
                try {
                    if (!tradutor.traduzProximo()) {
                        break;
                    }
                    importaHistoricoController.importaLinha(entrada, adeInsereParcelas, critica, leitor.getLinha(),
                            cseCodigo, hoje,
                            validaReserva, permitirValidacaoTaxa, serAtivo, cnvAtivo,
                            svcAtivo, serCnvAtivo, csaAtivo, orgAtivo, estAtivo, cseAtivo, importaDadosAde, retornaAdeNum,
                            false, selecionaPrimeiroCnvDisponivel, cacheConvenio, margemParam, cachePlanos, responsavel);
                } catch (ParserException ex) {
                    LOG.error(ex.getMessage(), ex);
                    critica.add(leitor.getLinha() + formataMsgErro(ex.getMessage(), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                }
            }
            tradutor.encerraTraducao();
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.traducao.fim.data.arg0", responsavel, DateHelper.getSystemDatetime().toString()));

            // Cria as parcelas pagas antigas.
            criaParcelasAntigas(adeInsereParcelas, responsavel);

            // Atualiza valores das parcelas de contratos de serviços percentuais
            HistoricoRetMovFinDAO hrmDAO = DAOFactory.getDAOFactory().getHistoricoRetMovFinDAO();
            if (adeInsereParcelas.size() > 0) {
                List<String> adeCodigos = new ArrayList<>();
                for (Map<String, Object> ade : adeInsereParcelas) {
                    if (!TextHelper.isNull(ade.get("adeCodigo"))) {
                        adeCodigos.add(ade.get("adeCodigo").toString());
                    }
                    if (adeCodigos.size() % 500 == 0) {
                        hrmDAO.atualizaParcelaImportacaoHistorico(adeCodigos);
                        adeCodigos.clear();
                    }
                }
                if (adeCodigos.size() > 0) {
                    hrmDAO.atualizaParcelaImportacaoHistorico(adeCodigos);
                    adeCodigos.clear();
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            erroDuranteProcessamento = true;
            throw new ConsignanteControllerException(ex);
        } finally {
            finalizaImportacaoContratos(critica, pathArquivo, leitor.getLinhaHeader(), leitor.getLinhaFooter(), responsavel);

            File arquivo = new File(fileName + ".prc");

            if (erroDuranteProcessamento) {
                arquivo.renameTo(new File(fileName + ".nok"));
            } else {
                arquivo.renameTo(new File(fileName + ".ok"));
            }
        }
    }

    /**
     * Importa linhas de sem processamento.
     * @param linhasSemProcessamento : lista com as linhas de sem processamento
     * @param caminhoCritica : caminho para gravação da crítica
     * @param responsavel : responsável pela operação
     * @throws ConsignanteControllerException
     */
    public void importaSemProcessamento(List<TransferObject> linhasSemProcessamento, String caminhoCritica, AcessoSistema responsavel) throws ConsignanteControllerException {
        // Lista para armazenar as linhas de entrada que foram criticadas
        List<String> critica = new ArrayList<>();

        // Lista para armazenar os contratos que terão parcelas criadas
        List<Map<String, Object>> adeInsereParcelas = new ArrayList<>();

        try {
            // Map que recebe os dados das linhas não processadas
            Map<String, Object> entrada;

            // Cria os delegates necessários
            String hoje = DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMdd");

            Object param = ParamSist.getInstance().getParam(CodedValues.TPC_IMPORTA_SEM_PROC_APENAS_SER_ATIVO, responsavel);
            boolean serAtivo = (param != null && param.equals(CodedValues.TPC_SIM));

            param = ParamSist.getInstance().getParam(CodedValues.TPC_IMPORTA_SEM_PROC_APENAS_CSA_ATIVA, responsavel);
            boolean csaAtivo = (param != null && param.equals(CodedValues.TPC_SIM));

            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.impHistorico.inicio.importacao.historico.sem.processamento.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.impHistorico.linhas.sem.processamento.arg0", responsavel, String.valueOf((linhasSemProcessamento != null ? linhasSemProcessamento.size() : 0))));

            ImportaHistoricoController importaHistoricoController = ApplicationContextProvider.getApplicationContext().getBean(ImportaHistoricoController.class);

            if (linhasSemProcessamento != null) {
                Iterator<TransferObject> itLinha = linhasSemProcessamento.iterator();
                String linha;

                Map<String, Map<String, Object>> cacheConvenio = new HashMap<>();
                while (itLinha.hasNext()) {
                    TransferObject cto = itLinha.next();
                    linha = (String) cto.getAttribute(Columns.ART_LINHA);

                    entrada = new HashMap<>();
                    entrada.put("OPERACAO", INCLUSAO);
                    entrada.put("EST_IDENTIFICADOR", cto.getAttribute(Columns.ART_EST_IDENTIFICADOR));
                    entrada.put("ORG_IDENTIFICADOR", cto.getAttribute(Columns.ART_ORG_IDENTIFICADOR));
                    entrada.put("SVC_IDENTIFICADOR", cto.getAttribute(Columns.ART_SVC_IDENTIFICADOR));
                    entrada.put("CSA_IDENTIFICADOR", cto.getAttribute(Columns.ART_CSA_IDENTIFICADOR));
                    entrada.put("RSE_MATRICULA", cto.getAttribute(Columns.ART_RSE_MATRICULA));
                    entrada.put("SER_CPF", null);
                    entrada.put("CNV_COD_VERBA", cto.getAttribute(Columns.ART_CNV_COD_VERBA));
                    entrada.put("ADE_VLR", cto.getAttribute(Columns.ART_PRD_VLR_REALIZADO));
                    entrada.put("ADE_CARENCIA", cto.getAttribute(Columns.ART_ADE_CARENCIA));
                    entrada.put("SITUACAO", null);
                    entrada.put("SPD_CODIGO", cto.getAttribute(Columns.ART_SPD_CODIGO));
                    entrada.put("OCP_OBS", cto.getAttribute(Columns.ART_OCP_OBS));
                    entrada.put("ADE_INDICE", cto.getAttribute(Columns.ART_ADE_INDICE));
                    entrada.put("ADE_ANO_MES_INI", cto.getAttribute(Columns.ART_ADE_ANO_MES_INI));
                    entrada.put("ADE_ANO_MES_FIM", cto.getAttribute(Columns.ART_ADE_ANO_MES_FIM));
                    entrada.put("ADE_PRAZO", cto.getAttribute(Columns.ART_ADE_PRAZO));
                    entrada.put("ADE_PRD_PAGAS", cto.getAttribute(Columns.ART_ADE_PRD_PAGAS));

                    importaHistoricoController.importaLinha(entrada, adeInsereParcelas, critica, linha, CodedValues.CSE_CODIGO_SISTEMA, hoje,
                                 false, false, serAtivo, false, false, false, csaAtivo, false, false, false, false, false, true, false,
                                 cacheConvenio, null, null, responsavel);
                }
            }

            //Cria as parcelas pagas antigas.
            criaParcelasAntigas(adeInsereParcelas, responsavel);

            // Atualiza valores das parcelas de contratos de serviços percentuais
            DAOFactory daoFactory = DAOFactory.getDAOFactory();
            HistoricoRetMovFinDAO hrmDAO = daoFactory.getHistoricoRetMovFinDAO();
            if (adeInsereParcelas.size() > 0) {
                List<String> adeCodigos = new ArrayList<>();
                Iterator<Map<String, Object>> it = adeInsereParcelas.iterator();
                while (it.hasNext()) {
                    try {
                        Map<String, Object> ade = it.next();
                        adeCodigos.add(ade.get("adeCodigo").toString());
                    } catch (Exception ex) {
                        // Se der erro na execução desta ADE, tenta pegar e executar a próxima ADE.
                        LOG.error(ex.getMessage(), ex);
                    }
                }
                hrmDAO.atualizaParcelaImportacaoHistorico(adeCodigos);
            }

        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ConsignanteControllerException(ex);
        } finally {
            finalizaImportacaoContratos(critica, caminhoCritica, null, null, responsavel);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.impHistorico.fim.importacao.historico.sem.processamento.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
        }
    }

    /**
     * Fecha conexão com base de dados e grava arquivo de crítica
     * @param conn
     * @param critica
     * @param caminhoCritica
     * @param headerCritica
     * @param footerCritica
     * @throws ConsignanteControllerException
     */
    private void finalizaImportacaoContratos(List<String> critica, String caminhoCritica, String headerCritica, String footerCritica, AcessoSistema responsavel) throws ConsignanteControllerException {
        // Grava o arquivo de Crítica
        try {
            String nomeArqSaida = null;
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.arquivos.critica.inicio.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            if (critica.size() > 0) {
                nomeArqSaida = caminhoCritica + ((caminhoCritica.endsWith(File.separator)) ? "" : File.separator)
                        + ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel) + DateHelper.format(DateHelper.getSystemDatetime(), "dd-MM-yyyy-HHmmss") + ".txt";

                File dir = new File(caminhoCritica);
                if (!dir.exists() && !dir.mkdirs()) {
                    throw new ConsignanteControllerException("mensagem.impHistorico.erro.parametro.importacao.csa.invalido", responsavel, dir.getAbsolutePath());
                }

                PrintWriter arqSaida = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqSaida)));
                if (headerCritica != null && !headerCritica.trim().equals("")) {
                    // Imprime a linha de header no arquivo
                    arqSaida.println(headerCritica + formataMsgErro("", COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                }

                // Imprime as linhas de critica no arquivo
                arqSaida.println(TextHelper.join(critica, System.getProperty("line.separator")));

                if (footerCritica != null && !footerCritica.trim().equals("")) {
                    // Imprime a linha de footer no arquivo
                    arqSaida.println(footerCritica + formataMsgErro("", COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                }
                arqSaida.close();
            }
            String nomeCritica = nomeArqSaida != null ? nomeArqSaida: ApplicationResourcesHelper.getMessage("mensagem.impHistorico.nenhuma.critica.gerada", responsavel);
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.impHistorico.arquivo.critica.arg0", responsavel, nomeCritica));
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.arquivos.critica.inicio.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }


    /**
     * Cria as parcelas antigas de histórico
     * @param adeInsereParcelas
     * @param conn
     * @param responsavel
     */
    private void criaParcelasAntigas(List<Map<String, Object>> adeInsereParcelas, AcessoSistema responsavel) {
        LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.imprHistorico.inicio.historico.parcelas.pagas.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
        Connection conn = null;

        try {
            if (adeInsereParcelas.size() > 0) {
                conn = DBHelper.makeConnection();

                // Define quais ADEs cada gerador irá trabalhar, dividindo equalitariamente
                // Cria os geradores de parcela e inicia a execução dos mesmos.
                int qtdPorThread = Math.round((float) adeInsereParcelas.size() / QTD_THREADS_GERADOR_PARCELA);
                int indiceInicial = 0;
                List<Thread> threadList = new ArrayList<>();

                for (int i = 1; i <= QTD_THREADS_GERADOR_PARCELA; i++) {
                    try {
                        int indiceFinal = (qtdPorThread * i > adeInsereParcelas.size() || i == QTD_THREADS_GERADOR_PARCELA) ? adeInsereParcelas.size() : qtdPorThread * i;
                        LOG.debug("DE " + indiceInicial + " A " + indiceFinal);
                        List<Map<String, Object>> sublista = adeInsereParcelas.subList(indiceInicial, indiceFinal);
                        indiceInicial = indiceFinal;

                        String nome = "GeradorDeParcelas" + i;
                        PreparedStatement insereParcela = conn.prepareStatement(QUERY_INSERE_PARCELA);
                        PreparedStatement insereOcorrenciaParcela = conn.prepareStatement(QUERY_INSERE_OCORRENCIA_PARCELA);
                        GeradorDeParcela gerador = new GeradorDeParcela(sublista, insereParcela, insereOcorrenciaParcela, responsavel);

                        Thread thread = new Thread(gerador);
                        thread.setName("TestaGerador " + nome);
                        thread.start();
                        threadList.add(thread);

                    } catch (SQLException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }

                // Espera todos os geradores terminarem de processar.
                while (true) {
                    boolean terminou = true;
                    for (Object element : threadList) {
                        Thread thread = (Thread) element;
                        if (thread.isAlive()) {
                            terminou = false;
                        }
                    }
                    if (!terminou) {
                        // Aguarda a execução das threads de geração de parelas.
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            LOG.error(ex.getMessage(), ex);
                        }
                    } else {
                        break;
                    }
                }
            }
        } catch (SQLException ex) {
            LOG.error(ex.getMessage(), ex);
        } finally {
            LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.imprHistorico.fim.historico.parcelas.pagas.arg0", responsavel, DateHelper.getSystemDatetime().toString()));
            DBHelper.releaseConnection(conn);
        }
    }

    /**
     * <p>Title: GeradorDeParcela</p>
     * <p>Description: Classe para Thread de geração de parcelas.</p>
     */
    private class GeradorDeParcela implements Runnable {

        private final List<Map<String, Object>> ades;
        private final PreparedStatement insereParcela;
        private final PreparedStatement insereOcorrenciaParcela;
        private final AcessoSistema responsavel;

        public GeradorDeParcela(List<Map<String, Object>> ades, PreparedStatement insereParcela, PreparedStatement insereOcorrenciaParcela, AcessoSistema responsavel) {
            this.ades = ades;
            this.insereParcela = insereParcela;
            this.insereOcorrenciaParcela = insereOcorrenciaParcela;
            this.responsavel = responsavel;
        }

        @Override
        public void run() {
            try {
                Iterator<Map<String, Object>> it = ades.iterator();
                while (it.hasNext()) {
                    try {
                        Map<String, Object> ade = it.next();
                        String adeCodigo = ade.get("adeCodigo").toString();
                        int adePrdPagas = ((Integer) ade.get("adePrdPagas")).intValue();

                        LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.log.debug.ade.nro.parcelas.ade.nro.parcelas.arg0.arg1", responsavel, adeCodigo, String.valueOf(adePrdPagas)));
                        if (adePrdPagas > 0) {
                            String orgCodigo = (String) ade.get("orgCodigo");
                            String spdCodigo = (String) ade.get("spdCodigo");
                            String adeVlr = (String) ade.get("adeVlr");
                            String ocpObs = (String) ade.get("ocpObs");
                            Date adeAnoMesIni = (Date) ade.get("adeAnoMesIni");
                            String adePeriodicidade = (String) ade.get("adePeriodicidade");

                            insereParcela.setString(1, adeCodigo); // ADE_CODIGO
                            insereParcela.setString(3, spdCodigo); // SPD_CODIGO
                            insereParcela.setDouble(6, Double.parseDouble(adeVlr)); // PRD_VLR_PREVISTO
                            insereParcela.setDouble(7, spdCodigo.equals(CodedValues.SPD_REJEITADAFOLHA) ? Double.parseDouble("0.00") : Double.parseDouble(adeVlr)); // PRD_VLR_REALIZADO

                            // INSERT INTO tb_ocorrencia_parcela (OCP_CODIGO, TOC_CODIGO, USU_CODIGO, OCP_DATA, OCP_OBS, PRD_CODIGO) SELECT ?, ?, ?, ?, ?, PRD_CODIGO FROM tb_parcela_desconto WHERE ADE_CODIGO = ? AND PRD_NUMERO = ?
                            insereOcorrenciaParcela.setString(2, CodedValues.TOC_RETORNO); // TOC_CODIGO
                            insereOcorrenciaParcela.setString(3, responsavel != null ? responsavel.getUsuCodigo() : CodedValues.USU_CODIGO_SISTEMA); // USU_CODIGO
                            insereOcorrenciaParcela.setTimestamp(4, new Timestamp(Calendar.getInstance().getTimeInMillis())); // OCP_DATA
                            insereOcorrenciaParcela.setString(5, ocpObs == null || ocpObs.equals("") ? ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.com.retorno", responsavel) : ocpObs);
                            insereOcorrenciaParcela.setString(6, adeCodigo); // ADE_CODIGO

                            for (int i = 1; i <= adePrdPagas; i++) {
                                Date prdDataDesconto = PeriodoHelper.getInstance().calcularAdeAnoMesFim(orgCodigo, adeAnoMesIni, i, adePeriodicidade, responsavel);

                                insereParcela.setInt(2, i); // PRD_NUMERO
                                insereParcela.setDate(4, DateHelper.toSQLDate(prdDataDesconto)); // PRD_DATA_DESCONTO
                                insereParcela.setDate(5, DateHelper.toSQLDate(prdDataDesconto)); // PRD_DATA_REALIZADO
                                insereParcela.execute();

                                insereOcorrenciaParcela.setString(1, DBHelper.getNextId());  // OCP_CODIGO
                                insereOcorrenciaParcela.setInt(7, i); // PRD_NUMERO
                                insereOcorrenciaParcela.execute();
                            }
                        }
                    } catch (Exception ex) {
                        // Se der erro na execução desta ADE, tenta pegar e executar a próxima ADE.
                        LOG.error(ex.getMessage(), ex);
                    }
                }
            } finally {
                try {
                    insereParcela.close();
                    insereOcorrenciaParcela.close();
                } catch (SQLException ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }
        }
    }

    public static String formataMsgErro(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        return TextHelper.removeAccent(TextHelper.formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda)).toUpperCase();
    }

}
