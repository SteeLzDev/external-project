package com.zetra.econsig.service.folha;

import static com.zetra.econsig.values.CodedValues.TPA_SIM;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConciliacaoControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.HistoricoArquivoControllerException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.folha.ConciliacaoHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamCsa;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.LeitorList;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.BatchManager;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.RelatorioConciliacaoBeneficioDAO;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.query.consignacao.ObtemConsignacaoCompativelQuery;
import com.zetra.econsig.persistence.query.consignacao.ObtemConsignacaoPorCnvSerQuery;
import com.zetra.econsig.persistence.query.convenio.ListaConvenioPelosIdentificadoresQuery;
import com.zetra.econsig.persistence.query.orgao.ListaOrgaoQuery;
import com.zetra.econsig.persistence.query.parcela.ListaOcorrenciaParcelaQuery;
import com.zetra.econsig.persistence.query.servico.ListaServicoNaturezaServicoQuery;
import com.zetra.econsig.service.arquivo.HistoricoArquivoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.lote.LoteController;
import com.zetra.econsig.service.servidor.PesquisarServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: ConciliacaoControllerBean</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ConciliacaoControllerBean implements ConciliacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConciliacaoControllerBean.class);

    @Autowired
    private LoteController loteController;

    @Autowired
    private ConsignatariaController csaController;

    @Autowired
    private PesquisarServidorController pesquisarServidorController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private HistoricoArquivoController historicoArquivoController;

    private static final int TAMANHO_MSG_ERRO_DEFAULT = 100;
    private static final String COMPLEMENTO_DEFAULT = " ";

    /**
     * Metodo para verificar as informações traduzidas do arquivo de lote,
     * formatar e validar valores. Apenas verifica a formatação e informações minimas necessarias
     * @param entrada - Map da tradução de um arquivo
     * @param processandoConciliacaoBeneficio
     * @throws AutorizacaoControllerException
     */
    private Map<String, Object> validaEntrada(Map<String, Object> entrada, boolean processandoConciliacaoBeneficio, AcessoSistema responsavel) throws ConciliacaoControllerException {
        Map<String, Object> retorno = new HashMap<>();
        retorno.putAll(entrada);

        /**
         * Valores que se estiverem no Map serão convertidos para data
         */
        String[][] vlrData = { { "PERIODO_FOLHA", "ADE_ANO_MES_INI", "ADE_ANO_MES_FIM", "ADE_ANO_MES_INI_REF", "ADE_ANO_MES_FIM_REF" },
                { ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.periodo.atual", responsavel), ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.data.inicial", responsavel), ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.data.final", responsavel), ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.data.inicial.referencia", responsavel), ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.data.final.referencia", responsavel) } };

        /**
         * Valores que se estiverem no Map serão convertidos para BigDecimal
         */
        String[][] vlrBigDecimal = { { "ADE_VLR_VERIFICAR", "ADE_VLR", "ADE_VLR_TAC", "ADE_VLR_IOF", "ADE_VLR_LIQUIDO", "ADE_VLR_MENS_VINC", "ADE_TAXA_JUROS" }, { ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.busca", responsavel), ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.encontrado", responsavel), ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.tac", responsavel), ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.iof", responsavel),
                ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.liquido", responsavel), ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.mensalidade.vinculada", responsavel), ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.valor.taxa.juros", responsavel) } };

        /**
         * Valores que se estiverem no Map serão convertidos para Integer
         */
        String[][] vlrInteger = { { "ADE_CARENCIA", "ADE_PRAZO" }, { ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.carencia", responsavel), ApplicationResourcesHelper.getMessage("rotulo.validar.entrada.arquivo.prazo", responsavel) } };

        for (int i = 0; i < vlrData[0].length; i++) {
            if (retorno.get(vlrData[0][i]) != null) {
                if (!retorno.get(vlrData[0][i]).toString().equals("")) {
                    try {
                        retorno.put(vlrData[0][i], DateHelper.toPeriodDate(DateHelper.parse(retorno.get(vlrData[0][i]).toString(), "yyyy-MM-dd")));
                    } catch (ParseException e) {
                        LOG.debug("Erro de Parser -> " + vlrData[0][i] + ": " + e.getMessage());
                        throw new ConciliacaoControllerException("mensagem.erro.valor.informado.para.campo.invalido", responsavel, vlrData[1][i]);
                    }
                } else {
                    retorno.remove(vlrData[0][i]);
                }
            }
        }

        for (int i = 0; i < vlrInteger[0].length; i++) {
            if (retorno.get(vlrInteger[0][i]) != null) {
                if (!retorno.get(vlrInteger[0][i]).toString().equals("")) {
                    try {
                        Integer vlr = Integer.valueOf(retorno.get(vlrInteger[0][i]).toString());
                        if (vlr.compareTo(Integer.valueOf("0")) < 0) {
                            LOG.debug("Erro de Parser -> " + vlrInteger[0][i] + ": VALOR INFORMADO NEGATIVO");
                            throw new ConciliacaoControllerException("mensagem.erro.valor.informado.para.campo.nao.pode.ser.negativo", responsavel, vlrInteger[1][i]);
                        }
                        retorno.put(vlrInteger[0][i], Integer.valueOf(retorno.get(vlrInteger[0][i]).toString()));
                    } catch (NumberFormatException e) {
                        LOG.debug("Erro de Parser -> " + vlrInteger[0][i] + ": " + e.getMessage());
                        throw new ConciliacaoControllerException("mensagem.erro.valor.informado.para.campo.invalido", responsavel, vlrInteger[1][i]);
                    }
                } else {
                    retorno.remove(vlrInteger[0][i]);
                }
            }
        }

        for (int i = 0; i < vlrBigDecimal[0].length; i++) {
            if (retorno.get(vlrBigDecimal[0][i]) != null) {
                if (!retorno.get(vlrBigDecimal[0][i]).toString().equals("")) {
                    try {
                        BigDecimal vlr = new BigDecimal(retorno.get(vlrBigDecimal[0][i]).toString());
                        // Verifica se o valor informado é positivo.
                        if (vlr.compareTo(new BigDecimal("0")) < 0) {
                            LOG.debug("Erro de Parser -> " + vlrBigDecimal[0][i] + ": VALOR INFORMADO NEGATIVO");
                            throw new AutorizacaoControllerException("mensagem.erro.valor.informado.para.campo.nao.pode.ser.negativo", responsavel, vlrBigDecimal[1][i]);
                        }
                        retorno.put(vlrBigDecimal[0][i], vlr);
                    } catch (Exception ex) {
                        LOG.debug("Erro de Parser -> " + vlrBigDecimal[0][i] + ": " + ex.getMessage());
                        throw new ConciliacaoControllerException("mensagem.erro.valor.informado.para.campo.invalido", responsavel, vlrBigDecimal[1][i]);
                    }
                } else {
                    retorno.remove(vlrBigDecimal[0][i]);
                }
            }
            // ade_vlr é obrigatório
            if (vlrBigDecimal[0][i].equals("ADE_VLR") && (retorno.get("ADE_VLR") == null || retorno.get("ADE_VLR").toString().equals(""))) {
                LOG.debug("Erro de Parser -> ADE_VLR: " + retorno.get("ADE_VLR"));
                throw new ConciliacaoControllerException("mensagem.erro.valor.contrato.nao.informado", responsavel);
            }
        }

        if (processandoConciliacaoBeneficio) {
            if (temProblemaCampoChave(retorno.get("CBE_NUMERO")) && temProblemaCampoChave(retorno.get("SER_CPF"))) {
                LOG.debug("Erro de Parse -> CBE_NUMERO: " + retorno.get("CBE_NUMERO"));
                LOG.debug("Erro de Parse -> SER_CPF: " + retorno.get("SER_CPF"));
                LOG.debug("Genitleza definir algum valor para o campo CBE_NUMERO ou SER_CPF.");
                throw new ConciliacaoControllerException("mensagem.conciliacao.contrato.beneficio.numero.e.cpf.nao.informado", responsavel);
            }

            if (temProblemaCampoChave(entrada.get("TLA_CODIGO"))) {
                LOG.debug("Erro de Parse -> TLA_CODIGO: " + entrada.get("TLA_CODIGO"));
                LOG.debug("Genitleza definir algum valor para o campo TLA_CODIGO.");
                throw new ConciliacaoControllerException("mensagem.conciliacao.tipo.lancamento.nao.informado", responsavel);
            }

        } else if (temProblemaCampoChave(retorno.get("RSE_MATRICULA")) && temProblemaCampoChave(retorno.get("SER_CPF"))) {
            LOG.debug("Erro de Parse -> RSE_MATRICULA: " + retorno.get("RSE_MATRICULA"));
            LOG.debug("Erro de Parse -> SER_CPF: " + retorno.get("SER_CPF"));
            LOG.debug("Genitleza definir algum valor para o campo RSE_MATRICULA ou SER_CPF");
            throw new ConciliacaoControllerException("mensagem.conciliacao.matricula.e.cpf.nao.informado", responsavel);
        }

        return retorno;
    }

    /**
     * Metodo simples para avalidar se o campo chave tem algum tipo de problema.
     * Deixar o IF mais simples de ser lido.
     * @param entrada
     * @return
     */
    private boolean temProblemaCampoChave(Object entrada) {
        boolean valido = TextHelper.isNull(entrada);

        if (valido == false) {
            valido = entrada.toString().equals("");
        }

        return valido;
    }

    private List<TransferObject> buscaConsignacaoPorCnvSer(String codVerba, String csaCodigo, String rseMatricula, String serCpf, String orgIdentificador, String svcIdentificador, String estIdentificador, boolean cnvAtivo, TransferObject criterio, String nseCodigo, Date dataConciliacao, boolean processandoConciliacaoBeneficio, AcessoSistema responsavel) throws ConciliacaoControllerException {
        ObtemConsignacaoPorCnvSerQuery adesPorCnvSer = new ObtemConsignacaoPorCnvSerQuery();
        adesPorCnvSer.codVerba = codVerba;
        adesPorCnvSer.csaCodigo = csaCodigo;
        adesPorCnvSer.rseMatricula = rseMatricula;
        adesPorCnvSer.serCpf = serCpf;
        adesPorCnvSer.orgIdentificador = orgIdentificador;
        adesPorCnvSer.svcIdentificador = svcIdentificador;
        adesPorCnvSer.estIdentificador = estIdentificador;
        adesPorCnvSer.sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;
        adesPorCnvSer.cnvAtivo = cnvAtivo;
        adesPorCnvSer.criterio = criterio;
        adesPorCnvSer.nseCodigo = nseCodigo;
        adesPorCnvSer.buscaContratoBeneficio = processandoConciliacaoBeneficio;

        List<Object> dataConciliacaoList = new ArrayList<>();
        dataConciliacaoList.add(CodedValues.NOT_EQUAL_KEY);
        dataConciliacaoList.add(dataConciliacao);
        adesPorCnvSer.adeDataUltConciliacao = dataConciliacaoList;

        List<TransferObject> ades = new ArrayList<>();
        try {
            ades = adesPorCnvSer.executarDTO();
        } catch (HQueryException ex) {
            throw new ConciliacaoControllerException(ex);
        }

        return ades;
    }

    private BigDecimal retornaPossivelVlrLiquidacao(String adeCodigo, AcessoSistema responsavel) throws ConciliacaoControllerException {
        try {
            BigDecimal total = BigDecimal.ZERO;

            ObtemConsignacaoCompativelQuery query = new ObtemConsignacaoCompativelQuery();
            query.adeCodigo = adeCodigo;

            List<TransferObject> lista = query.executarDTO();
            if (lista != null && !lista.isEmpty()) {
                for (TransferObject to : lista) {
                    BigDecimal valor = (BigDecimal) to.getAttribute(Columns.ADE_VLR);
                    total = total.add(valor);
                }
            }

            return total;
        } catch (HQueryException ex) {
            throw new ConciliacaoControllerException(ex);
        }
    }

    private List<TransferObject> getParcelas(String adeCodigo, List<String> spdCodigos, AcessoSistema responsavel) throws ConciliacaoControllerException {
        try {
            ListaOcorrenciaParcelaQuery query = new ListaOcorrenciaParcelaQuery();
            query.adeCodigo = adeCodigo;
            query.spdCodigos = spdCodigos;
            return query.executarDTO();
        } catch (HQueryException ex) {
            throw new ConciliacaoControllerException(ex);
        }
    }

    /**
     * Método que faz a conciliação
     * @param csaCodigo
     * @param nomeArqXmlEntrada
     * @param nomeArqXmlTradutor
     * @param tipoEntidade
     * @param codigoEntidade
     * @param nomeArquivoEntrada
     * @param responsavel
     * @throws ViewHelperException
     */
    @Override
    public void conciliar(String csaCodigo, String nomeArqXmlEntrada, String nomeArqXmlTradutor, String tipoEntidade, String codigoEntidade, String nomeArquivoEntrada, AcessoSistema responsavel) throws ViewHelperException {

        if (!TextHelper.isNull(csaCodigo)) {
            LOG.debug(("IMPORTACAO - CONCILIAR ") + csaCodigo + " - Arquivo " + nomeArquivoEntrada + " -> " + DateHelper.getSystemDatetime());
            LOG.debug("Tipo de Entidade: " + tipoEntidade + "\t Codigo: " + codigoEntidade + "\t" + DateHelper.getSystemDatetime());
        } else {
            if (!responsavel.isCseSup()) {
                throw new ViewHelperException("mensagem.erroInternoSistema", responsavel);
            }
            LOG.debug(("IMPORTACAO - CONCILIAR ") + "MÚLTIPLAS CONSIGNATÁRIAS" + " - Arquivo " + nomeArquivoEntrada + " -> " + DateHelper.getSystemDatetime());
        }
        LOG.debug("XML de entrada: " + nomeArqXmlEntrada + "\t XML de Tradução: " + nomeArqXmlTradutor);

        // Verifica se o modulo benificio está ativado.
        final boolean temModuloBeneficio = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel);
        boolean processandoConciliacaoBeneficio = false;

        // Analisando se o sistema tem modulo beneficio.
        // Se tiver é provavel que vamos precissar desse DAO
        RelatorioConciliacaoBeneficioDAO relatorioConciliacaoBeneficio = null;
        if (temModuloBeneficio) {
            relatorioConciliacaoBeneficio = DAOFactory.getDAOFactory().getRelatorioConciliacaoBeneficioDAO();
        }

        final Map<String, String> orgaoMap = new HashMap<>();
        final Map<String, String> consignatariaMap = new HashMap<>();
        final Map<String, String> naturezaServicoMap = new HashMap<>();
        final Map<String, String> estadoIdentificadorMap = new HashMap<>();

        // verifica se consignatária solicita geração de lote de sincronia
        boolean geraLoteSincronia = false;
        List<Map<String, Object>> lstLoteSincronia = new ArrayList<>();
        Timestamp dataConciliacao = new Timestamp(Calendar.getInstance().getTimeInMillis());
        List<String> adesSincronizados = new ArrayList<>();

        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String pathLote = absolutePath + File.separatorChar + "conf" + File.separatorChar + "conciliacao" + File.separatorChar;

        String pathLoteDefault = absolutePath + File.separatorChar + "conf" + File.separatorChar + "conciliacao" + File.separatorChar + "xml" + File.separatorChar;

        String entradaImpLote = null;
        String tradutorImpLote = null;

        if (!TextHelper.isNull(csaCodigo)) {
            entradaImpLote = pathLote + csaCodigo + File.separatorChar + nomeArqXmlEntrada;
            tradutorImpLote = pathLote + csaCodigo + File.separatorChar + nomeArqXmlTradutor;
        } else {
            entradaImpLote = pathLote + "cse" + File.separatorChar + nomeArqXmlEntrada;
            tradutorImpLote = pathLote + "cse" + File.separatorChar + nomeArqXmlTradutor;
        }

        String entradaImpLoteDefault = pathLoteDefault + nomeArqXmlEntrada;
        String tradutorImpLoteDefault = pathLoteDefault + nomeArqXmlTradutor;

        File arqConfEntrada = null;
        File arqConfTradutor = null;
        String fileName = null;

        arqConfEntrada = new File(entradaImpLote);
        arqConfTradutor = new File(tradutorImpLote);

        if (!arqConfEntrada.exists() || !arqConfTradutor.exists()) {
            File arqConfEntradaDefault = new File(entradaImpLoteDefault);
            File arqConfTradutorDefault = new File(tradutorImpLoteDefault);
            if (!arqConfEntradaDefault.exists() || !arqConfTradutorDefault.exists()) {
                throw new ViewHelperException("mensagem.erro.arquivos.configuracao.conciliacao.ausentes", responsavel);
            } else {
                arqConfEntrada = arqConfEntradaDefault;
                arqConfTradutor = arqConfTradutorDefault;
                entradaImpLote = entradaImpLoteDefault;
                tradutorImpLote = tradutorImpLoteDefault;
            }
        }

        // Verifica o arquivo de entrada de dados
        if (!TextHelper.isNull(csaCodigo)) {
            fileName = absolutePath + File.separatorChar + "conciliacao" + File.separatorChar + tipoEntidade.toLowerCase() + File.separatorChar + codigoEntidade + File.separatorChar + nomeArquivoEntrada;
        } else {
            fileName = absolutePath + File.separatorChar + "conciliacao" + File.separatorChar + "cse" + File.separatorChar + nomeArquivoEntrada;
        }

        File arqEntrada = new File(fileName);
        if (!arqEntrada.exists()) {
            throw new ViewHelperException("mensagem.erro.arquivos.conciliacao.nao.encontrados.servidor", responsavel);
        }

        // Configura o leitor de acordo com o arquivo de entrada
        LOG.debug("nome do arquivo ... " + fileName);
        LeitorArquivoTexto leitor = null;
        if (fileName.toLowerCase().endsWith(".zip") || fileName.toLowerCase().endsWith(".zip.prc")) {
            leitor = new LeitorArquivoTextoZip(entradaImpLote, fileName);
        } else {
            leitor = new LeitorArquivoTexto(entradaImpLote, fileName);
        }

        // Hash que recebe os dados do que serão lidos do arquivo de entrada
        Map<String, Object> entrada = new HashMap<>();

        // Escritor e tradutor
        Escritor escritor = new EscritorMemoria(entrada);
        Tradutor tradutor = new Tradutor(tradutorImpLote, leitor, escritor);

        /**
         * Log para auditoria
         */
        try {
            LogDelegate log = new LogDelegate(responsavel, Log.ARQUIVO, Log.IMP_ARQ_CONCILIACAO, Log.LOG_INFORMACAO);
            if (!TextHelper.isNull(csaCodigo)) {
                log.setConsignataria(csaCodigo);
            }
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.arquivo.arg0", responsavel, nomeArquivoEntrada));
            log.add(ApplicationResourcesHelper.getMessage("mensagem.informacao.numero.linhas.arquivo.arg0", responsavel, String.valueOf(FileHelper.getNumberOfLines(fileName))));
            log.write();
        } catch (LogControllerException ex) {
            throw new ViewHelperException("mensagem.erro.arquivos.conciliacao.nao.encontrados.servidor", responsavel);
        }

        // Inclui histórico do arquivo
        Long harCodigo = null;
        try {
            String harObs = "";
            String pathLoteHist = "";
            if (!TextHelper.isNull(csaCodigo)) {
                pathLoteHist = absolutePath + File.separator + "conciliacao" + File.separator + tipoEntidade + File.separator + codigoEntidade + File.separator;
            } else {
                pathLoteHist = absolutePath + File.separator + "conciliacao" + File.separator + "cse" + File.separator;
            }
            String arquivoEntrada = pathLoteHist + nomeArquivoEntrada;

            String harResultado = CodedValues.STS_INATIVO.toString();
            harCodigo = historicoArquivoController.createHistoricoArquivo(tipoEntidade, codigoEntidade, TipoArquivoEnum.ARQUIVO_CONCILIACAO, arquivoEntrada, harObs, null, null, harResultado, responsavel);
        } catch (HistoricoArquivoControllerException e) {
            LOG.error("Não foi possível inserir o histórico do arquivo de lote '" + nomeArquivoEntrada + "'.", e);
        }

        try {
            tradutor.iniciaTraducao();
        } catch (ParserException e) {
            LOG.error("Erro em iniciar tradução.", e);
            throw new ViewHelperException(e);
        }

        // Cache de parâmetros
        Map<String, Map<String, Boolean>> mapCacheParamCsa = new HashMap<>();
        // Carrega o cache de parâmetros de CSA
        if (!TextHelper.isNull(csaCodigo)) {
            try {
                atualizaCacheParamCsa(mapCacheParamCsa, csaCodigo, responsavel);
            } catch (AutorizacaoControllerException e) {
                throw new ViewHelperException(e);
            }
        }

        Map<String, Map<String, Object>> cacheParametrosCnv = new HashMap<>();
        Map<String, List<String>> critica = new LinkedHashMap<>();
        boolean gerouException = false;
        List<String> csaCodigosSincronia = new ArrayList<>();

        BatchManager batchManager = new BatchManager(SessionUtil.getSession());

        try {
            boolean proximo = true;
            LOG.debug("Início da conciliação de arquivos: (" + DateHelper.getSystemDatetime() + "). ");
            while (proximo) {
                List<String> observacao = new ArrayList<>();
                try {
                    proximo = tradutor.traduzProximo();
                    if (!proximo) {
                        break;
                    }

                    // Analisando se o processamento atual é um XML de previa do modulo beneficio.
                    processandoConciliacaoBeneficio = temModuloBeneficio && entrada.containsKey("ARQ_IDENTIFICADOR") && "PREVIA".equalsIgnoreCase((String) entrada.get("ARQ_IDENTIFICADOR"));

                    // Se csaCodigo não foi preenchido, significa importação de arquivo de conciliação de múltiplas consignatárias
                    String csaCodigoLinha = null;
                    if (TextHelper.isNull(csaCodigo)) {
                        // Porém, com o modulo beneficio a busca pela consignatárias pode ser realizada linha a linha ou pelo CBE_NUMERO.
                        if (processandoConciliacaoBeneficio && !entrada.containsKey("CSA_IDENTIFICADOR")) {
                            // Se o modulo benefício estiver ativo e o XML conter o campo CBE_NUMERO e não conter o CSA_IDENTIFICADOR vamos buscar os dados
                            // Nas tabelas tb_contrato_beneficio e  tb_beneficio.
                            String numeroContratoBenificio = (String) entrada.get("CBE_NUMERO");

                            // Se não tiverem informado no xml o campo CBE_NUMERO não tem como continuar.
                            if (TextHelper.isNull(numeroContratoBenificio)) {
                                observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.beneficio.consiliacao.numero.contrato.beneficio.nao.informada", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                critica.put(leitor.getLinha(), observacao);
                                continue;
                            }

                            try {
                                Consignataria consignataria = csaController.findConsignatariaByNumeroContratoBeneficio(numeroContratoBenificio, responsavel);
                                csaCodigoLinha = consignataria.getCsaCodigo();
                                consignataria = null;
                            } catch (ConsignatariaControllerException e) {
                                throw new AutorizacaoControllerException("mensagem.consignatariaNaoInformada", responsavel);
                            }
                        } else {
                            // sendo assim, busca a consignatária linha a linha pelo campo CSA_IDENTIFICADOR
                            String csaIdentificador = (String) entrada.get("CSA_IDENTIFICADOR");
                            if (TextHelper.isNull(csaIdentificador)) {
                                observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.consignatariaNaoInformada", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                critica.put(leitor.getLinha(), observacao);
                                continue;
                            }
                            try {
                                ConsignatariaTransferObject csa = new ConsignatariaTransferObject();
                                csa.setCsaIdentificador(csaIdentificador);
                                csa = csaController.findConsignataria(csa, responsavel);
                                csaCodigoLinha = csa.getCsaCodigo();
                            } catch (ConsignatariaControllerException e) {
                                throw new AutorizacaoControllerException("mensagem.consignatariaNaoInformada", responsavel);
                            }
                            if (mapCacheParamCsa.get(csaCodigoLinha) == null) {
                                atualizaCacheParamCsa(mapCacheParamCsa, csaCodigoLinha, responsavel);
                            }
                        }

                        entrada.put("CSA_CODIGO", csaCodigoLinha);
                    }

                    geraLoteSincronia = false;
                    Map<String, Boolean> cacheParametrosCsa = mapCacheParamCsa.get(!TextHelper.isNull(csaCodigo) ? csaCodigo : csaCodigoLinha);
                    if (cacheParametrosCsa.containsKey(CodedValues.TPA_GERAR_LOTE_SINCRONIA_CONCILIACAO)) {
                        geraLoteSincronia = cacheParametrosCsa.get(CodedValues.TPA_GERAR_LOTE_SINCRONIA_CONCILIACAO).booleanValue();
                    }

                    // Consignatárias configuradas para gerar lote de sincronia
                    if (geraLoteSincronia) {
                        if (csaCodigosSincronia != null && !csaCodigosSincronia.contains(!TextHelper.isNull(csaCodigo) ? csaCodigo : csaCodigoLinha)) {
                            csaCodigosSincronia.add(!TextHelper.isNull(csaCodigo) ? csaCodigo : csaCodigoLinha);
                        }
                    }

                    if (entrada.get("LINHA_INVALIDA") == null || entrada.get("LINHA_INVALIDA").toString().equals("N")) {
                        // Valida valores de entrada

                        Map<String, Object> entradaValida = validaEntrada(entrada, processandoConciliacaoBeneficio, responsavel);
                        LOG.debug(entradaValida.toString());

                        // Buscando dados do XML
                        String orgao = (String) entradaValida.get("ORG_IDENTIFICADOR");
                        String estabelecimento = (String) entradaValida.get("EST_IDENTIFICADOR");
                        String matricula = (String) entradaValida.get("RSE_MATRICULA");
                        String cpf = (String) entradaValida.get("SER_CPF");
                        BigDecimal valor = (BigDecimal) entradaValida.get("ADE_VLR");
                        Date dataInicio = (Date) entradaValida.get("ADE_ANO_MES_INI");
                        Date dataFim = (Date) entradaValida.get("ADE_ANO_MES_FIM");
                        Integer prazo = (Integer) entradaValida.get("ADE_PRAZO");
                        Integer prazoRest = !TextHelper.isNull(entradaValida.get("PRAZO_RESTANTE")) ? Integer.parseInt(entradaValida.get("PRAZO_RESTANTE").toString()) : null;
                        String codVerba = (String) entradaValida.get("CNV_COD_VERBA");
                        String servico = (String) entradaValida.get("SVC_IDENTIFICADOR");
                        String nseCodigo = null;
                        String adeNumero = (String) entradaValida.get("ADE_NUMERO");
                        String adeIndice = (String) entradaValida.get("ADE_INDICE");
                        String adeIdentificador = (String) entradaValida.get("ADE_IDENTIFICADOR");
                        // Campos do modulo Benificio.
                        String numeroContratoBenificio = (String) entrada.get("CBE_NUMERO");
                        String tipoLancamento = (String) entradaValida.get("TLA_CODIGO");

                        List<TransferObject> servidores = pesquisarServidor(tipoEntidade, codigoEntidade, estabelecimento, orgao, matricula, cpf, numeroContratoBenificio, processandoConciliacaoBeneficio, responsavel);

                        List<TransferObject> contratos = recuperaContrato(entradaValida, valor, dataInicio, dataFim, prazo, codVerba, (!TextHelper.isNull(csaCodigo) ? csaCodigo : csaCodigoLinha), tipoEntidade, codigoEntidade, matricula, cpf, orgao, servico, estabelecimento, adeNumero, adeIndice, adeIdentificador, true, nseCodigo, geraLoteSincronia, dataConciliacao, lstLoteSincronia, numeroContratoBenificio, tipoLancamento, processandoConciliacaoBeneficio, responsavel);

                        // marca se o contrato do arquivo foi mapeado, ou seja, se possui contrato equivalente no sistema
                        boolean mapeado = !contratos.isEmpty() && contratos.size() == 1;

                        TransferObject contrato = null;
                        TransferObject servidor = null;

                        // Criando observação ser a lista de servidores forem vazias.
                        if (servidores.isEmpty()) {
                            observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.nenhumServidorEncontrado", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                        }

                        // Ser a lista de servidor for igual a 1 já temos o nosso servidor, simples e facil.
                        if (servidores.size() == 1) {
                            servidor = servidores.get(0);
                        }

                        // Agora começamos a analisar os contratos
                        // Se temos mais que um contrato criamos a ocorrencia.
                        if (contratos.size() > 1) {
                            mapeado = false;
                            observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.maisDeUmaConsignacaoEncontrada", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                        } else if (contratos.isEmpty()) {
                            mapeado = false;
                            // Ser a lista de contratos for vaziar vamos criar a linha de sincronia se aquela CSA permitir.
                            observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.nenhumaConsignacaoEncontrada", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                            // Gera lote de sincronia, exceto se for conciliação de benefícios, que gera sincronia apenas para exclusões
                            if (geraLoteSincronia && !processandoConciliacaoBeneficio) {
                                // Se existir multiplos servidores vamos tentar achar um com maior margem.
                                if (servidor == null) {
                                    servidor = escolherServidorMaiorMargem(entradaValida, codVerba, !TextHelper.isNull(csaCodigo) ? csaCodigo : csaCodigoLinha, tipoEntidade, codigoEntidade, estabelecimento, orgao, matricula, cpf, numeroContratoBenificio, mapCacheParamCsa, cacheParametrosCnv, processandoConciliacaoBeneficio, responsavel);
                                    // recupera o TransferObject referente ao servidor escolhido
                                    for (TransferObject ser : servidores) {
                                        if (ser.getAttribute(Columns.RSE_CODIGO).equals(servidor.getAttribute(Columns.RSE_CODIGO))) {
                                            servidor = ser;
                                            break;
                                        }
                                    }
                                }
                                if (servidor != null) {
                                    // Criando a linha de sincronia
                                    lstLoteSincronia = addLinhaToSincroniaList(entradaValida, servidor, lstLoteSincronia, csaCodigo, AcaoSincronia.INCLUSAO, processandoConciliacaoBeneficio, responsavel);
                                }
                            }
                        } else {
                            // Se passou por todos if podemos pegar o primeiro contrato.
                            contrato = contratos.get(0);

                            // Se o servidor ainda for null é porque temos multiplos dele.
                            // Abaixo estamos fazendo o casamento do primeiro RSE_CODIGO que bate com a lista de servidores que voltou.
                            if (servidor == null) {
                                for (TransferObject tmp : servidores) {
                                    if (tmp.getAttribute(Columns.RSE_CODIGO).equals(contrato.getAttribute(Columns.RSE_CODIGO))) {
                                        servidor = tmp;
                                        break;
                                    }
                                }
                            }
                        }

                        // Verificando se achamos um servidor e podemos criar a ocorrencia de multiplos servidores.
                        if (servidor == null && servidores.size() > 1) {
                            observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.multiplosServidoresEncontrados", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                        }

                        // Validação do servidor
                        if (servidor != null) {
                            if (!TextHelper.isNull(matricula) && !servidor.getAttribute(Columns.RSE_MATRICULA).equals(matricula)) {
                                observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.matriculaInvalida", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                            }

                            String columnsCpfASerAnalisado = null;
                            if (processandoConciliacaoBeneficio) {
                                if (!TextHelper.isNull(numeroContratoBenificio) && (TextHelper.isNull(servidor.getAttribute(Columns.CBE_NUMERO)) || !servidor.getAttribute(Columns.CBE_NUMERO).equals(numeroContratoBenificio))) {
                                    mapeado = false;
                                    observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.numero.contrato.beneficio.invalido.servidor", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                }

                                columnsCpfASerAnalisado = Columns.BFC_CPF;
                            } else {
                                columnsCpfASerAnalisado = Columns.SER_CPF;
                            }

                            if (!TextHelper.isNull(cpf) && !servidor.getAttribute(columnsCpfASerAnalisado).equals(cpf)) {
                                observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.cpfInvalido", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                            }

                            if (CodedValues.SRS_INATIVOS.contains(servidor.getAttribute(Columns.SRS_CODIGO))) {
                                observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.servidorExcluido", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                            }
                        }

                        // Validação do contrato
                        // verifica se houve alteração no contrato para geração de lote de sincronia, caso tenha permissão
                        boolean isAlteracaoSincronia = false;
                        if (contrato != null) {
                            String rseMatricula = contrato.getAttribute(Columns.RSE_MATRICULA).toString();
                            String serCpf = contrato.getAttribute(Columns.SER_CPF).toString();

                            // Exceto para conciliação de benefícios, que gera lote de sincronia apenas para exclusão
                            if (processandoConciliacaoBeneficio) {
                                // Gera observação para crítica
                                if (!TextHelper.isNull(numeroContratoBenificio) && (TextHelper.isNull(contrato.getAttribute(Columns.CBE_NUMERO)) || !contrato.getAttribute(Columns.CBE_NUMERO).equals(numeroContratoBenificio))) {
                                    mapeado = false;
                                    observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.numero.contrato.beneficio.invalido.contrato", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                }
                            } else {
                                BigDecimal adeVlr = (BigDecimal) contrato.getAttribute(Columns.ADE_VLR);
                                Date adeAnoMesIni = (Date) contrato.getAttribute(Columns.ADE_ANO_MES_INI);
                                Date adeAnoMesFim = (Date) contrato.getAttribute(Columns.ADE_ANO_MES_FIM);
                                Integer adePrazo = (Integer) contrato.getAttribute(Columns.ADE_PRAZO);
                                Integer adePrazoRest = (Integer) contrato.getAttribute("PRAZO_RESTANTE");

                                if (!TextHelper.isNull(matricula) && !matricula.equals(rseMatricula)) {
                                    observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.matriculaInvalidaContrato", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                }
                                if (!TextHelper.isNull(cpf) && !cpf.equals(serCpf)) {
                                    observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.cpfInvalidoContrato", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                }
                                if (valor != null && valor.compareTo(adeVlr) != 0) {
                                    observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.valorContratoDiferente", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                    isAlteracaoSincronia = geraLoteSincronia ? true : false;
                                }
                                if (dataInicio != null && dataInicio.compareTo(adeAnoMesIni) != 0) {
                                    observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.dataInicioContratoDiferente", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                    isAlteracaoSincronia = geraLoteSincronia ? true : false;
                                }
                                if (dataFim != null && dataFim.compareTo(adeAnoMesFim) != 0) {
                                    observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.dataFimContratoDiferente", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                    isAlteracaoSincronia = geraLoteSincronia ? true : false;
                                }
                                if (prazo != null && prazo.compareTo(adePrazo) != 0) {
                                    observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.prazoContratoDiferente", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                    isAlteracaoSincronia = geraLoteSincronia ? true : false;
                                }
                                if (prazoRest != null && prazoRest.compareTo(adePrazoRest) != 0) {
                                    observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.prazoRestContratoDiferente", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                                    isAlteracaoSincronia = geraLoteSincronia ? true : false;
                                }
                            }
                        }

                        if (servidor != null && contrato != null && !processandoConciliacaoBeneficio) {
                            List<String> validacao = validaParcelaContrato(tipoEntidade, servidor, contrato, responsavel);
                            if (validacao != null && !validacao.isEmpty()) {
                                observacao.addAll(validacao);
                            }
                        }

                        // se houve divergência do contrato no arquivo de conciliação e o registrado no sistema e gera lote de sincronia
                        // cria uma linha de alteração de contrato no arquivo de lote de sincronia
                        if (contrato != null && isAlteracaoSincronia && (adesSincronizados != null && !adesSincronizados.contains(contrato.getAttribute(Columns.ADE_CODIGO)))) {
                            if (TextHelper.isNull(entradaValida.get("ADE_VLR"))) {
                                entradaValida.put("ADE_VLR", contrato.getAttribute(Columns.ADE_VLR));
                            }

                            lstLoteSincronia = addLinhaToSincroniaList(contrato, entradaValida, lstLoteSincronia, orgaoMap, consignatariaMap, naturezaServicoMap, estadoIdentificadorMap, AcaoSincronia.ALTERACAO, processandoConciliacaoBeneficio, responsavel);
                            AutDesconto adeBean = AutDescontoHome.findByPrimaryKey((String) contrato.getAttribute(Columns.ADE_CODIGO));
                            adeBean.setAdeDataUltConciliacao(dataConciliacao);
                            adesSincronizados.add(adeBean.getAdeCodigo());

                            // atualiza no contrato a última data de conciliação do contrato
                            try {
                                AutDescontoHome.update(adeBean);
                            } catch (UpdateException uex) {
                                LOG.warn("Erro ao gerar linha de lote de sincronia para contrato: ADE_NUMERO: " + adeNumero + ", ADE_IDENTIFICADOR: " + adeIdentificador + ", ADE_INDICE: " + adeIndice);
                            }
                        } else if (contrato != null && geraLoteSincronia && observacao.size() == 0 && (adesSincronizados != null && !adesSincronizados.contains(contrato.getAttribute(Columns.ADE_CODIGO)))) {
                            // Se a linha não tem nenhum problema podemos marcar ela como sincronizada.
                            AutDesconto adeBean = AutDescontoHome.findByPrimaryKey((String) contrato.getAttribute(Columns.ADE_CODIGO));
                            adeBean.setAdeDataUltConciliacao(dataConciliacao);
                            adesSincronizados.add(adeBean.getAdeCodigo());
                            // atualiza no contrato a última data de conciliação do contrato
                            try {
                                AutDescontoHome.update(adeBean);
                            } catch (UpdateException uex) {
                                LOG.warn("Erro ao gerar linha de lote de sincronia para contrato: ADE_NUMERO: " + adeNumero + ", ADE_IDENTIFICADOR: " + adeIdentificador + ", ADE_INDICE: " + adeIndice);
                            }
                        }

                        // Add a linha para ser carregada na tabela temporária no final da iteração do loop.
                        if (processandoConciliacaoBeneficio) {
                            relatorioConciliacaoBeneficio.adcionaLinhaParaRelatorio(matricula, cpf, numeroContratoBenificio, tipoLancamento, valor, mapeado);
                        }

                    } else {
                        observacao.add(formataMsgErro(entrada.get("LINHA_INVALIDA").toString(), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    }
                } catch (ParserException e) {
                    gerouException = true;
                    LOG.error(e.getMessage(), e);
                    observacao.add(formataMsgErro(e.getMessage(), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                } catch (Exception e) {
                    LOG.error(e.getMessage(), e);
                    observacao.add(formataMsgErro(e.getMessage(), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                }
                if (observacao.isEmpty()) {
                    observacao.add(formataMsgErro(ApplicationResourcesHelper.getMessage("mensagem.conciliacao.contratoSemProblemas", responsavel), COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                }
                if (!TextHelper.isNull(leitor.getLinha())) {
                    critica.put(leitor.getLinha(), observacao);
                }

                batchManager.iterate();
            }
            LOG.debug("Fim da conciliação de arquivos: (" + DateHelper.getSystemDatetime() + "). ");

            if (processandoConciliacaoBeneficio) {
                relatorioConciliacaoBeneficio.executa(csaCodigo);
                EnviaEmailHelper.enviaEmailConciliacaoBeneficio(csaCodigo, relatorioConciliacaoBeneficio.getTotalContratoAtivosMensalidadePlanoSaude(), relatorioConciliacaoBeneficio.getTotalContratoAtivosMensalidadeOdotologico(), relatorioConciliacaoBeneficio.getListaContratoNoSistemaNaoConciliacaoPlanoSaude(), relatorioConciliacaoBeneficio.getListaContratoNaoSistemaNoConciliacaoPlanoSaude(), relatorioConciliacaoBeneficio.getListaContratoNoSistemaNaoConciliacaoOdontologico(), relatorioConciliacaoBeneficio.getListaContratoNaoSistemaNoConciliacaoOdontologico(), responsavel);
            }

            if (csaCodigosSincronia != null && csaCodigosSincronia.size() > 0) {
                CustomTransferObject criterio = new CustomTransferObject();
                List<Object> dataConciliacaoObj = new ArrayList<>();

                List<String> tntCodigos = new ArrayList<>();
                tntCodigos.addAll(CodedValues.TNT_BENEFICIO_MENSALIDADE);

                dataConciliacaoObj.add(CodedValues.NOT_EQUAL_KEY);
                dataConciliacaoObj.add(dataConciliacao);
                criterio.setAttribute("dataConciliacao", dataConciliacaoObj);
                criterio.setAttribute("csaCodigos", csaCodigosSincronia);
                criterio.setAttribute("usaModuloBeneficio", processandoConciliacaoBeneficio);
                criterio.setAttribute("tntCodigos", tntCodigos);
                List<String> sadCodigos = new ArrayList<>();
                sadCodigos.add(CodedValues.NOT_EQUAL_KEY);
                sadCodigos.addAll(CodedValues.SAD_CODIGOS_INATIVOS);

                try {
                    int sizeAdeList = 0;
                    int count = 0;
                    int offset = 0;
                    int maxResults = 3000;
                    LOG.debug("Início da criação de lote de sincronia: (" + DateHelper.getSystemDatetime() + "). ");
                    do {
                        List<TransferObject> adeList = pesquisarConsignacaoController.pesquisaAutorizacao(responsavel.getTipoEntidade(), responsavel.getCodigoEntidade(), null, null, null, sadCodigos, null, offset, maxResults, criterio, responsavel);

                        if (!adeList.isEmpty()) {
                            sizeAdeList = adeList.size();
                            for (TransferObject ade : adeList) {
                                TransferObject adeToExclude = ade;
                                Map<String, Object> entradaValida = new HashMap<>();
                                String adeCodigo = (String) adeToExclude.getAttribute(Columns.ADE_CODIGO);
                                if (adesSincronizados != null && !adesSincronizados.contains(adeCodigo)) {
                                    // se há geração de lote de sincronia para a consignatária, após a leitura de todo o arquivo de conciliação
                                    // , pesquisa por contratos ativos que não foram verificados por esta conciliação
                                    lstLoteSincronia = addLinhaToSincroniaList(adeToExclude, entradaValida, lstLoteSincronia, orgaoMap, consignatariaMap, naturezaServicoMap, estadoIdentificadorMap, AcaoSincronia.EXCLUSAO, processandoConciliacaoBeneficio, responsavel);
                                }
                            }
                        }
                        adeList.clear();
                        count++;
                        offset = (count * maxResults) + 1;

                    } while (sizeAdeList == maxResults);

                    // chamada para geração do lote de sincronia
                    if (lstLoteSincronia != null && !lstLoteSincronia.isEmpty()) {
                        try {
                            geraLoteSincronia(csaCodigo, lstLoteSincronia, responsavel);
                            lstLoteSincronia = null;
                        } catch (ViewHelperException vex) {
                            lstLoteSincronia = null;
                            LOG.warn("Erro ao gerar lote de sincronia no processo de conciliação: " + vex.getMessage());
                        }
                    }
                    LOG.debug("Fim da criação de lote de sincronia: (" + DateHelper.getSystemDatetime() + "). ");
                } catch (AutorizacaoControllerException e) {
                    LOG.warn("Erro ao recuperar contratos para geração de linhas de exclusão no lote de sincronia na conciliação.");
                }
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
                    LOG.error("Não foi possível inserir o histórico do arquivo de lote '" + nomeArquivoEntrada + "'.", e);
                }
            }

            try {
                tradutor.encerraTraducao();
            } catch (ParserException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }

        // Gera arquivo de crítica
        try {
            geraCritica(critica, tipoEntidade, codigoEntidade, csaCodigo, nomeArquivoEntrada, leitor, responsavel);
        } catch (ConciliacaoControllerException e) {
            LOG.debug(e.getMessage(), e);
        }

    }

    /**
     * Metodo TOTALMENTE baseado no LoteHelper
     * @param entradaValida
     * @param csaCodigo
     * @param tipoEntidade
     * @param codigoEntidade
     * @param estabelecimento
     * @param orgao
     * @param matricula
     * @param cpf
     * @param numeroContratoBenificio
     * @param mapCacheParamCsa
     * @param cacheParametrosCnv
     * @param responsavel
     * @return
     * @throws AutorizacaoControllerException
     * @throws ViewHelperException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private TransferObject escolherServidorMaiorMargem(Map<String, Object> entradaValida, String codVerba, String csaCodigo, String tipoEntidade, String codigoEntidade, String estabelecimento, String orgao, String matricula, String cpf, String numeroContratoBenificio, Map<String, Map<String, Boolean>> mapCacheParamCsa, Map<String, Map<String, Object>> cacheParametrosCnv, boolean processandoConciliacaoBeneficio, AcessoSistema responsavel) throws AutorizacaoControllerException, ViewHelperException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        boolean tentarTodosRegistrosServidores = false;

        List<Map<String, Object>> serCnvRegisters = loteController.lstServidorPorCnv(codVerba, csaCodigo, tipoEntidade, codigoEntidade, estabelecimento, orgao, matricula, cpf, true, true, null, null, false, false, null, numeroContratoBenificio, processandoConciliacaoBeneficio, responsavel);

        if (processandoConciliacaoBeneficio && serCnvRegisters.isEmpty()) {
            if (!TextHelper.isNull(numeroContratoBenificio)) {
                // procurando somente por cpf e numero contrato beneficio
                serCnvRegisters = loteController.lstServidorPorCnv(codVerba, csaCodigo, tipoEntidade, codigoEntidade, estabelecimento, orgao, null, cpf, true, true, null, null, false, false, null, numeroContratoBenificio, processandoConciliacaoBeneficio, responsavel);

                if (serCnvRegisters.isEmpty()) {
                    // procurando somente por matricula e numero contrato beneficio
                    serCnvRegisters = loteController.lstServidorPorCnv(codVerba, csaCodigo, tipoEntidade, codigoEntidade, estabelecimento, orgao, matricula, null, true, true, null, null, false, false, null, numeroContratoBenificio, processandoConciliacaoBeneficio, responsavel);

                    if (serCnvRegisters.isEmpty()) {
                        // procurando somente por numero contrato beneficio
                        serCnvRegisters = loteController.lstServidorPorCnv(codVerba, csaCodigo, tipoEntidade, codigoEntidade, estabelecimento, orgao, null, null, true, true, null, null, false, false, null, numeroContratoBenificio, processandoConciliacaoBeneficio, responsavel);
                    }
                }
            }

            if (!TextHelper.isNull(cpf) && serCnvRegisters.isEmpty()) {
                // procurando somente por cpf e matricula
                serCnvRegisters = loteController.lstServidorPorCnv(codVerba, csaCodigo, tipoEntidade, codigoEntidade, estabelecimento, orgao, matricula, cpf, true, true, null, null, false, false, null, null, processandoConciliacaoBeneficio, responsavel);

                if (serCnvRegisters.isEmpty()) {
                    // procurando somente por cpf
                    serCnvRegisters = loteController.lstServidorPorCnv(codVerba, csaCodigo, tipoEntidade, codigoEntidade, estabelecimento, orgao, null, cpf, true, true, null, null, false, false, null, numeroContratoBenificio, processandoConciliacaoBeneficio, responsavel);
                }
            }

            if (!TextHelper.isNull(matricula) && serCnvRegisters.isEmpty()) {
                serCnvRegisters = loteController.lstServidorPorCnv(codVerba, csaCodigo, tipoEntidade, codigoEntidade, estabelecimento, orgao, matricula, null, true, true, null, null, false, false, null, null, processandoConciliacaoBeneficio, responsavel);
            }
        } else {
            if (!TextHelper.isNull(cpf) && serCnvRegisters.isEmpty()) {
                serCnvRegisters = loteController.lstServidorPorCnv(codVerba, csaCodigo, tipoEntidade, codigoEntidade, estabelecimento, orgao, null, cpf, true, true, null, null, false, false, null, numeroContratoBenificio, processandoConciliacaoBeneficio, responsavel);
            }

            if (!TextHelper.isNull(matricula) && serCnvRegisters.isEmpty()) {
                serCnvRegisters = loteController.lstServidorPorCnv(codVerba, csaCodigo, tipoEntidade, codigoEntidade, estabelecimento, orgao, matricula, null, true, true, null, null, false, false, null, numeroContratoBenificio, processandoConciliacaoBeneficio, responsavel);
            }
        }

        if (serCnvRegisters.size() > 0) {
            // Atualiza o cache de parâmetros para os registros retornados
            ConciliacaoHelper.atualizaCacheParamCnv(entradaValida, serCnvRegisters, csaCodigo, cacheParametrosCnv, responsavel);

            // Filtra os serviços que não permitem importação de lote
            ConciliacaoHelper.removeSvcSemImportacaoLote(serCnvRegisters, cacheParametrosCnv);

            if (serCnvRegisters.size() > 1) {
                // Se existe mais de um registro, provavelmente é porque há mais de um registro servidor.
                // Verifica se pode ser utilizado aquele de maior margem
                int countServidores = ConciliacaoHelper.countEntidadesDistintas(serCnvRegisters, Columns.SER_CODIGO);
                if (countServidores > 1 && !ConciliacaoHelper.permiteInclusaoComServidorDuplicado(csaCodigo, mapCacheParamCsa)) {
                    throw new AutorizacaoControllerException("mensagem.conciliacao.multiplosServidoresEncontrados", responsavel);
                }
                int countRegistrosServidores = ConciliacaoHelper.countEntidadesDistintas(serCnvRegisters, Columns.RSE_CODIGO);
                if (countRegistrosServidores > 1) {
                    BigDecimal adeVlr = (BigDecimal) entradaValida.get("ADE_VLR");
                    Integer adePrazo = (Integer) entradaValida.get("ADE_PRAZO");

                    // Se encontrou mais de um servidor, verifica se o parâmetro de consignatária
                    // permite a tentativa de inclusão em todas as matrículas disponíveis, ou
                    // se escolhe apenas a com a maior margem
                    tentarTodosRegistrosServidores = ConciliacaoHelper.tentarIncluirTodosRegistrosServidores(csaCodigo, mapCacheParamCsa);
                    if (ConciliacaoHelper.utilizarRegistroServidorMaiorMargem(csaCodigo, mapCacheParamCsa) || tentarTodosRegistrosServidores) {
                        serCnvRegisters = ConciliacaoHelper.filtraRegistroMargemOrdenada(serCnvRegisters, cacheParametrosCnv, adeVlr, adePrazo, true);
                    } else if (ConciliacaoHelper.utilizarRegistroServidorMenorMargem(csaCodigo, mapCacheParamCsa)) {
                        serCnvRegisters = ConciliacaoHelper.filtraRegistroMargemOrdenada(serCnvRegisters, cacheParametrosCnv, adeVlr, adePrazo, false);
                    } else {
                        throw new AutorizacaoControllerException("mensagem.conciliacao.multiplosServidoresEncontrados", responsavel);
                    }
                }
            }
        }

        // Verifica quantos registros servidores foram retornados (deve haver apenas 1)
        int countRegistrosServidores = ConciliacaoHelper.countEntidadesDistintas(serCnvRegisters, Columns.RSE_CODIGO);
        if (countRegistrosServidores == 0) {
            throw new AutorizacaoControllerException("mensagem.conciliacao.nenhumServidorEncontrado", responsavel);
        } else if (countRegistrosServidores > 1) {
            throw new AutorizacaoControllerException("mensagem.conciliacao.multiplosServidoresEncontrados", responsavel);
        }

        TransferObject servidor = CustomTransferObject.class.getDeclaredConstructor().newInstance();
        servidor.setAtributos(serCnvRegisters.get(0));

        return servidor;
    }

    /**
     *
     * @param valor
     * @param dataInicio
     * @param dataFim
     * @param prazo
     * @param codVerba
     * @param csaCodigo
     * @param tipoEntidade
     * @param codigoEntidade
     * @param matricula
     * @param cpf
     * @param orgao
     * @param servico
     * @param estabelecimento
     * @param adeNumero
     * @param adeIndice
     * @param adeIdentificador
     * @param cnvAtivo
     * @param nseCodigo
     * @param numeroContratoBenificio
     * @param tipoLancamento
     * @param responsavel
     * @return
     * @throws ConciliacaoControllerException
     */
    private List<TransferObject> recuperaContrato(Map<String, Object> entradaValida, BigDecimal valor, Date dataInicio, Date dataFim, Integer prazo, String codVerba, String csaCodigo, String tipoEntidade, String codigoEntidade, String matricula, String cpf, String orgao, String servico, String estabelecimento, String adeNumero, String adeIndice, String adeIdentificador, boolean cnvAtivo, String nseCodigo, boolean geraLoteSincronia, Date dataConciliacao, List<Map<String, Object>> lstLoteSincronia, String numeroContratoBenificio, String tipoLancamento, boolean processandoConciliacaoBeneficio, AcessoSistema responsavel)
            throws ConciliacaoControllerException {

        List<TransferObject> contratos = pesquisarContrato(entradaValida, codVerba, csaCodigo, tipoEntidade, codigoEntidade, matricula, cpf, orgao, servico, estabelecimento, adeNumero, adeIndice, adeIdentificador, false, nseCodigo, geraLoteSincronia, dataConciliacao, lstLoteSincronia, numeroContratoBenificio, tipoLancamento, processandoConciliacaoBeneficio, responsavel);

        if (contratos.size() > 1) {
            Map<String, Object> prioridade = new LinkedHashMap<>();
            prioridade.put(Columns.ADE_VLR, valor);
            prioridade.put(Columns.ADE_ANO_MES_INI, dataInicio);
            prioridade.put(Columns.ADE_ANO_MES_FIM, dataFim);
            prioridade.put(Columns.ADE_PRAZO, prazo);

            List<TransferObject> copia = new ArrayList<>();
            for (String chave : prioridade.keySet()) {
                Object value = prioridade.get(chave);

                if (value == null) {
                    continue;
                }

                if (contratos.isEmpty()) {
                    contratos.addAll(copia);
                }

                copia.clear();
                copia.addAll(contratos);
                contratos.clear();
                for (TransferObject c : copia) {
                    if (value.equals(c.getAttribute(chave))) {
                        contratos.add(c);
                    }
                }

                // Se encontrou o contrato candidato
                if (contratos.size() == 1) {
                    break;
                }
            }
        }

        for (TransferObject contrato : contratos) {
            contrato.setAttribute(Columns.ADE_IDENTIFICADOR, adeIdentificador);
            contrato.setAttribute(Columns.ADE_NUMERO, adeNumero);
            contrato.setAttribute(Columns.ADE_INDICE, adeIndice);
        }

        return contratos;
    }

    /**
     *
     * @param tipoEntidade
     * @param codigo
     * @param estIdentificador
     * @param orgIdentificador
     * @param rseMatricula
     * @param serCPF
     * @param numeroContratoBenificio
     * @param processandoConciliacaoBeneficio
     * @param responsavel
     * @return
     * @throws ConciliacaoControllerException
     */
    private List<TransferObject> pesquisarServidor(String tipoEntidade, String codigo, String estIdentificador, String orgIdentificador, String rseMatricula, String serCPF, String numeroContratoBenificio, boolean processandoConciliacaoBeneficio, AcessoSistema responsavel) throws ConciliacaoControllerException {
        try {
            // Procura servidor exato pela matrícula e cpf e numero contrato beneficio se o processandoConciliacaoBeneficio for true
            List<TransferObject> retorno = pesquisarServidorController.pesquisaServidorExato(tipoEntidade, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, numeroContratoBenificio, processandoConciliacaoBeneficio, responsavel);

            if (processandoConciliacaoBeneficio && retorno.isEmpty()) {
                if (!TextHelper.isNull(numeroContratoBenificio)) {
                    // procurando somente por cpf e numero contrato beneficio
                    retorno = pesquisarServidorController.pesquisaServidorExato(tipoEntidade, codigo, estIdentificador, orgIdentificador, null, serCPF, numeroContratoBenificio, processandoConciliacaoBeneficio, responsavel);

                    if (retorno.isEmpty()) {
                        // procurando somente por matricula e numero contrato beneficio
                        retorno = pesquisarServidorController.pesquisaServidorExato(tipoEntidade, codigo, estIdentificador, orgIdentificador, rseMatricula, null, numeroContratoBenificio, processandoConciliacaoBeneficio, responsavel);

                        if (retorno.isEmpty()) {
                            // procurando somente por numero contrato beneficio
                            retorno = pesquisarServidorController.pesquisaServidorExato(tipoEntidade, codigo, estIdentificador, orgIdentificador, null, null, numeroContratoBenificio, processandoConciliacaoBeneficio, responsavel);
                        }
                    }
                }

                if (!TextHelper.isNull(serCPF) && retorno.isEmpty()) {
                    // procurando somente por cpf e matricula
                    retorno = pesquisarServidorController.pesquisaServidorExato(tipoEntidade, codigo, estIdentificador, orgIdentificador, rseMatricula, serCPF, null, processandoConciliacaoBeneficio, responsavel);

                    if (retorno == null || retorno.isEmpty()) {
                        // procurando somente por cpf
                        retorno = pesquisarServidorController.pesquisaServidorExato(tipoEntidade, codigo, estIdentificador, orgIdentificador, null, serCPF, null, processandoConciliacaoBeneficio, responsavel);
                    }
                }

                if (!TextHelper.isNull(rseMatricula) && retorno.isEmpty()) {
                    retorno = pesquisarServidorController.pesquisaServidorExato(tipoEntidade, codigo, estIdentificador, orgIdentificador, rseMatricula, null, null, processandoConciliacaoBeneficio, responsavel);
                }
            } else //Se não encontrou servidor pela matricula e cpf, procura somente pela matrícula
            if (!TextHelper.isNull(rseMatricula) && retorno.isEmpty()) {
                retorno = pesquisarServidorController.pesquisaServidorExato(tipoEntidade, codigo, estIdentificador, orgIdentificador, rseMatricula, null, responsavel);

                //Se não encontrou servidor somente pela matricula, procura somente pelo cpf
                if (!TextHelper.isNull(serCPF) && retorno.isEmpty()) {
                    retorno = pesquisarServidorController.pesquisaServidorExato(tipoEntidade, codigo, estIdentificador, orgIdentificador, null, serCPF, responsavel);
                }

            }

            return retorno;
        } catch (ServidorControllerException e) {
            LOG.error("Erro ao tentar utilizar o controller. ServidorControllerException: " + e.getMessage());
            throw new ConciliacaoControllerException(e);
        }
    }

    /**
     *
     * @param codVerba
     * @param csaCodigo
     * @param tipoEntidade
     * @param codigoEntidade
     * @param rseMatricula
     * @param serCpf
     * @param orgIdentificador
     * @param svcIdentificador
     * @param estIdentificador
     * @param adeNumero
     * @param adeIndice
     * @param adeIdentificador
     * @param cnvAtivo
     * @param nseCodigo
     * @param numeroContratoBenificio
     * @param tipoLancamento
     * @param responsavel
     * @return
     * @throws ConciliacaoControllerException
     */
    private List<TransferObject> pesquisarContrato(Map<String, Object> entradaValida, String codVerba, String csaCodigo, String tipoEntidade, String codigoEntidade, String rseMatricula, String serCpf, String orgIdentificador, String svcIdentificador, String estIdentificador, String adeNumero, String adeIndice, String adeIdentificador, boolean cnvAtivo, String nseCodigo, boolean geraLoteSincronia, Date dataConciliacao, List<Map<String, Object>> lstLoteSincronia, String numeroContratoBenificio, String tipoLancamento, boolean processandoConciliacaoBeneficio, AcessoSistema responsavel) throws ConciliacaoControllerException {
        TransferObject criterio = new CustomTransferObject();
        criterio.setAttribute("tipo", tipoEntidade);
        criterio.setAttribute("codigo", codigoEntidade);
        criterio.setAttribute(Columns.ADE_NUMERO, adeNumero);
        criterio.setAttribute(Columns.ADE_INDICE, adeIndice);
        criterio.setAttribute(Columns.ADE_IDENTIFICADOR, adeIdentificador);

        // Criterio do Modulo Beneficio
        criterio.setAttribute("numeroContratoBenificio", numeroContratoBenificio);
        criterio.setAttribute("tipoLancamento", tipoLancamento);

        List<TransferObject> ades = buscaConsignacaoPorCnvSer(codVerba, csaCodigo, rseMatricula, serCpf, orgIdentificador, svcIdentificador, estIdentificador, cnvAtivo, criterio, nseCodigo, geraLoteSincronia ? dataConciliacao : null, processandoConciliacaoBeneficio, responsavel);

        if (processandoConciliacaoBeneficio && ades.isEmpty()) {
            if (!TextHelper.isNull(numeroContratoBenificio)) {
                // procurando somente por cpf e numero contrato beneficio
                ades = buscaConsignacaoPorCnvSer(codVerba, csaCodigo, null, serCpf, orgIdentificador, svcIdentificador, estIdentificador, cnvAtivo, criterio, nseCodigo, geraLoteSincronia ? dataConciliacao : null, processandoConciliacaoBeneficio, responsavel);

                if (ades.isEmpty()) {
                    // procurando somente por matricula e numero contrato beneficio
                    ades = buscaConsignacaoPorCnvSer(codVerba, csaCodigo, rseMatricula, null, orgIdentificador, svcIdentificador, estIdentificador, cnvAtivo, criterio, nseCodigo, geraLoteSincronia ? dataConciliacao : null, processandoConciliacaoBeneficio, responsavel);

                    if (ades.isEmpty()) {
                        // procurando somente por numero contrato beneficio
                        ades = buscaConsignacaoPorCnvSer(codVerba, csaCodigo, null, null, orgIdentificador, svcIdentificador, estIdentificador, cnvAtivo, criterio, nseCodigo, geraLoteSincronia ? dataConciliacao : null, processandoConciliacaoBeneficio, responsavel);
                    }
                }
            }

            if (!TextHelper.isNull(serCpf) && ades.isEmpty()) {
                criterio.setAttribute("numeroContratoBenificio", null);
                // procurando somente por cpf e matricula
                ades = buscaConsignacaoPorCnvSer(codVerba, csaCodigo, rseMatricula, serCpf, orgIdentificador, svcIdentificador, estIdentificador, cnvAtivo, criterio, nseCodigo, geraLoteSincronia ? dataConciliacao : null, processandoConciliacaoBeneficio, responsavel);

                if (ades.isEmpty()) {
                    // procurando somente por cpf
                    ades = buscaConsignacaoPorCnvSer(codVerba, csaCodigo, null, serCpf, orgIdentificador, svcIdentificador, estIdentificador, cnvAtivo, criterio, nseCodigo, geraLoteSincronia ? dataConciliacao : null, processandoConciliacaoBeneficio, responsavel);
                }
            }

            if (!TextHelper.isNull(rseMatricula) && ades.isEmpty()) {
                criterio.setAttribute("numeroContratoBenificio", null);
                ades = buscaConsignacaoPorCnvSer(codVerba, csaCodigo, rseMatricula, null, orgIdentificador, svcIdentificador, estIdentificador, cnvAtivo, criterio, nseCodigo, geraLoteSincronia ? dataConciliacao : null, processandoConciliacaoBeneficio, responsavel);
            }

        } else {

            // Se não encontrou contrato para matrícula e cpf faz a busca de contratos somente pelo cpf
            if (!TextHelper.isNull(serCpf) && ades.isEmpty()) {
                ades = buscaConsignacaoPorCnvSer(codVerba, csaCodigo, null, serCpf, orgIdentificador, svcIdentificador, estIdentificador, cnvAtivo, criterio, nseCodigo, geraLoteSincronia ? dataConciliacao : null, processandoConciliacaoBeneficio, responsavel);
            }

            // Se não encontrou contrato para o cpf faz a busca de contratos somente pela matrícula
            if (!TextHelper.isNull(rseMatricula) && ades.isEmpty()) {
                ades = buscaConsignacaoPorCnvSer(codVerba, csaCodigo, rseMatricula, null, orgIdentificador, svcIdentificador, estIdentificador, cnvAtivo, criterio, nseCodigo, geraLoteSincronia ? dataConciliacao : null, processandoConciliacaoBeneficio, responsavel);
            }
        }

        return ades;
    }

    /**
     *
     * @param tipoEntidade
     * @param servidor
     * @param contrato
     * @param responsavel
     * @return
     * @throws ConciliacaoControllerException
     */
    private List<String> validaParcelaContrato(String tipoEntidade, TransferObject servidor, TransferObject contrato, AcessoSistema responsavel) throws ConciliacaoControllerException {
        List<String> critica = new ArrayList<>();
        String adeCodigo = contrato.getAttribute(Columns.ADE_CODIGO).toString();
        String adeIncMargem = (!TextHelper.isNull(contrato.getAttribute(Columns.ADE_INC_MARGEM))) ? contrato.getAttribute(Columns.ADE_INC_MARGEM).toString() : CodedValues.INCIDE_MARGEM_SIM.toString();
        BigDecimal adeValor = (BigDecimal) contrato.getAttribute(Columns.ADE_VLR);
        Integer adePrazo = (Integer) contrato.getAttribute(Columns.ADE_PRAZO);
        Integer adePrdPagas = (Integer) contrato.getAttribute(Columns.ADE_PRD_PAGAS);

        List<String> spdRejeito = new ArrayList<>();
        spdRejeito.add(CodedValues.SPD_REJEITADAFOLHA);
        spdRejeito.add(CodedValues.SPD_SEM_RETORNO);

        List<String> spdCodigos = new ArrayList<>();
        spdCodigos.addAll(spdRejeito);
        spdCodigos.add(CodedValues.SPD_LIQUIDADAFOLHA);
        spdCodigos.add(CodedValues.SPD_LIQUIDADAMANUAL);

        // Recupera parcelas não pagas
        List<TransferObject> parcelas = getParcelas(adeCodigo, spdCodigos, responsavel);

        BigDecimal vlrDevido = BigDecimal.ZERO;
        if (parcelas != null && !parcelas.isEmpty()) {
            TransferObject ultParcela = parcelas.get(parcelas.size() - 1);
            String statusUltParcela = ultParcela.getAttribute(Columns.SPD_CODIGO).toString();
            if (spdRejeito.contains(statusUltParcela)) {
                // Se a última parcela está rejeitada, verifica o motivo de rejeito
                String ocpObs = (String) ultParcela.getAttribute(Columns.OCP_OBS);
                if (!TextHelper.isNull(ocpObs) && !ocpObs.equalsIgnoreCase(ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.ocp.obs.parcela.sem.retorno", responsavel))) {
                    // Se tem obs e não é observação de sem retorno, então devolve a mensagem para o usuário
                    ocpObs = ApplicationResourcesHelper.getMessage("mensagem.erro.conciliacao.validacao.parcela.contrato.motivo.nao.desconto", responsavel, ocpObs.replaceFirst("[^:]*: ", ""));
                    critica.add(formataMsgErro(ocpObs, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                }

                // Se não foi paga a última parcela sugerir uma das situações para a consignatária caso existam
                BigDecimal margemRestante = BigDecimal.ZERO;
                if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM.toString())) {
                    margemRestante = (BigDecimal) servidor.getAttribute(Columns.RSE_MARGEM_REST);
                } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_2.toString())) {
                    margemRestante = (BigDecimal) servidor.getAttribute(Columns.RSE_MARGEM_REST_2);
                } else if (adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM_3.toString())) {
                    margemRestante = (BigDecimal) servidor.getAttribute(Columns.RSE_MARGEM_REST_3);
                }

                // Se a margem está negativa
                if (margemRestante.compareTo(BigDecimal.ZERO) < 0) {
                    // 1.Reduzir o valor do contrato em até 50% para que a margem não fique negativa, e sugerir um novo valor de prestação para refinanciamento.
                    BigDecimal multiply = adeValor.multiply(new BigDecimal(50));
                    BigDecimal divide = multiply.divide(new BigDecimal(100), 2, java.math.RoundingMode.DOWN);
                    if (divide.add(margemRestante).compareTo(BigDecimal.ZERO) >= 0) {
                        String msg = ApplicationResourcesHelper.getMessage("mensagem.conciliacao.possivelRenegociacaoConsignacao", responsavel, NumberHelper.format(margemRestante.add(adeValor).doubleValue(), NumberHelper.getLang()));
                        critica.add(formataMsgErro(msg, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    }

                    // 2.Verificar se o servidor possui outro serviço com a mesma consignatária, de modo que uma margem possa ser liberada para esta operação.
                    BigDecimal vlrLiquidacao = retornaPossivelVlrLiquidacao(adeCodigo, responsavel);
                    if (margemRestante.add(vlrLiquidacao).compareTo(BigDecimal.ZERO) >= 0) {
                        String msg = ApplicationResourcesHelper.getMessage("mensagem.conciliacao.consignacaoPossivelLiquidacao", responsavel);
                        critica.add(formataMsgErro(msg, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
                    }
                }
            }

            // Se o contrato estiver sendo pago, mas houve um rejeito passado, anotar crítica,
            // somar o valor devido vencido para anexar à crítica, e continuar iteração.
            for (TransferObject parcela : parcelas) {
                String status = parcela.getAttribute(Columns.SPD_CODIGO).toString();
                if (spdRejeito.contains(status)) {
                    BigDecimal vlrPrevisto = (BigDecimal) parcela.getAttribute(Columns.PRD_VLR_PREVISTO);
                    vlrDevido = vlrDevido.add(vlrPrevisto);
                }
            }

            if (vlrDevido.compareTo(BigDecimal.ZERO) != 0) {
                // Se possui valor devido de parcelas rejeitadas, calcula
                // o capital devido para fazer um mínimo entre os valores
                int prazoRest = (adePrazo != null ? adePrazo.intValue() : 99999) - (adePrdPagas != null ? adePrdPagas.intValue() : 0);
                BigDecimal capitalDevido = adeValor.multiply(new BigDecimal(prazoRest));
                // Retorna o mínimo pois caso haja mais parcelas rejeitadas do que
                // prazo restante, o capital devido será levado em conta.
                vlrDevido = vlrDevido.min(capitalDevido);

                String msg = ApplicationResourcesHelper.getMessage("mensagem.conciliacao.valorDevidoVencido", responsavel, NumberHelper.format(vlrDevido.doubleValue(), NumberHelper.getLang()));
                critica.add(formataMsgErro(msg, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
            }
        }

        return critica;
    }

    /**
     *
     * @param critica
     * @param tipo
     * @param tipoCodigo
     * @param csaCodigo
     * @param nomeArquivoEntrada
     * @param leitor
     * @param responsavel
     * @return
     * @throws ConciliacaoControllerException
     */
    private String geraCritica(Map<String, List<String>> critica, String tipo, String tipoCodigo, String csaCodigo, String nomeArquivoEntrada, LeitorArquivoTexto leitor, AcessoSistema responsavel) throws ConciliacaoControllerException {

        try {
            String nomeArqSaida, nomeArqSaidaTxt, nomeArqSaidaZip = "";
            String delimitador = leitor.getDelimitador() == null ? "" : leitor.getDelimitador();
            if (critica.size() > 0) {
                // Grava arquivo contendo as parcelas não encontradas no sistema
                String absolutePath = ParamSist.getDiretorioRaizArquivos();
                LOG.debug("ARQUIVOS CRITICA: " + DateHelper.getSystemDatetime());
                String pathSaida = absolutePath + File.separatorChar + "conciliacao" + File.separatorChar + (tipo != null ? tipo : "csa") + File.separatorChar + (tipoCodigo != null ? tipoCodigo : csaCodigo) + File.separatorChar;
                File dir = new File(pathSaida);
                if (!dir.exists() && !dir.mkdirs()) {
                    throw new ConciliacaoControllerException("mensagem.erro.criacao.diretorio", responsavel, dir.getAbsolutePath());
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
                for (String linha : critica.keySet()) {
                    arqSaida.println(linha + delimitador + TextHelper.join(critica.get(linha), " "));
                }

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

            }

            return nomeArqSaidaZip;
        } catch (IOException ex) {
            throw new ConciliacaoControllerException(ex);
        }
    }

    /**
     *
     * @param linha
     * @param delimitador
     * @param mensagem
     * @return
     */
    private static String gerarLinhaArquivoSaida(String linha, String delimitador, String mensagem) {
        mensagem = (mensagem == null ? "" : mensagem);
        return (linha + delimitador + formataMsgErro(mensagem, COMPLEMENTO_DEFAULT, TAMANHO_MSG_ERRO_DEFAULT, true));
    }

    /**
     *
     * @param mensagem
     * @param complemento
     * @param tamanho
     * @param alinhaEsquerda
     * @return
     */
    private static String formataMsgErro(String mensagem, String complemento, int tamanho, boolean alinhaEsquerda) {
        mensagem = (mensagem == null ? "" : mensagem);
        return TextHelper.removeAccent(TextHelper.formataMensagem(mensagem, complemento, tamanho, alinhaEsquerda)).toUpperCase();
    }

    private List<Map<String, Object>> addLinhaToSincroniaList(TransferObject adeTO, Map<String, Object> entradaValida, List<Map<String, Object>> entradaAde, Map<String, String> orgaoMap, Map<String, String> consignatariaMap, Map<String, String> naturezaServicoMap, Map<String, String> estadoIdentificadorMap, AcaoSincronia acaoSincronia, boolean processandoConciliacaoBeneficio, AcessoSistema responsavel) {
        if (entradaAde == null) {
            entradaAde = new ArrayList<>();
        }

        switch (acaoSincronia) {
            case INCLUSAO:
                entradaValida.put("OPERACAO", "I");
                break;
            case EXCLUSAO:
                entradaValida.put("OPERACAO", "E");
                break;
            case ALTERACAO:
                entradaValida.put("OPERACAO", "A");
                break;
        }

        entradaValida.put("RSE_MATRICULA", adeTO.getAttribute(Columns.RSE_MATRICULA));
        entradaValida.put("SER_CPF", adeTO.getAttribute(Columns.SER_CPF));

        if (processandoConciliacaoBeneficio) {
            entradaValida.put("BFC_CPF", adeTO.getAttribute(Columns.BFC_CPF));
        }

        if (entradaValida.get("ADE_VLR") == null) {
            entradaValida.put("ADE_VLR", adeTO.getAttribute(Columns.ADE_VLR));
        }
        if (entradaValida.get("ADE_VLR_VERIFICAR") == null) {
            entradaValida.put("ADE_VLR_VERIFICAR", adeTO.getAttribute(Columns.ADE_VLR));
        }
        if (entradaValida.get("ADE_PRAZO") == null) {
            entradaValida.put("ADE_PRAZO", adeTO.getAttribute(Columns.ADE_PRAZO));
        }
        if (TextHelper.isNull(entradaValida.get("SER_NOME"))) {
            entradaValida.put("SER_NOME", adeTO.getAttribute(Columns.SER_NOME));
        }

        if (TextHelper.isNull(entradaValida.get("ORG_IDENTIFICADOR"))) {
            if (acaoSincronia == AcaoSincronia.ALTERACAO) {
                entradaValida.put("ORG_IDENTIFICADOR", adeTO.getAttribute(Columns.ORG_IDENTIFICADOR));
            } else if (acaoSincronia == AcaoSincronia.EXCLUSAO) {
                try {
                    if (!orgaoMap.containsKey(adeTO.getAttribute(Columns.ORG_CODIGO))) {
                        Orgao org = OrgaoHome.findByPrimaryKey((String) adeTO.getAttribute(Columns.ORG_CODIGO));
                        orgaoMap.put((String) adeTO.getAttribute(Columns.ORG_CODIGO), org.getOrgIdentificador());
                    }
                    entradaValida.put("ORG_IDENTIFICADOR", orgaoMap.get(adeTO.getAttribute(Columns.ORG_CODIGO)));
                } catch (FindException e) {
                    LOG.warn(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel) + " não encontrado na geração de lote de sincronia.");
                }
            }
        }

        if (TextHelper.isNull(entradaValida.get("EST_IDENTIFICADOR"))) {
            ListaOrgaoQuery lstOrgQry = new ListaOrgaoQuery();
            lstOrgQry.orgCodigo = adeTO.getAttribute(Columns.ORG_CODIGO);

            try {
                if (!estadoIdentificadorMap.containsKey(adeTO.getAttribute(Columns.ORG_CODIGO))) {
                    List<TransferObject> lstOrg = lstOrgQry.executarDTO();
                    if (lstOrg != null && !lstOrg.isEmpty()) {
                        estadoIdentificadorMap.put((String) adeTO.getAttribute(Columns.ORG_CODIGO), (String) lstOrg.get(0).getAttribute(Columns.EST_IDENTIFICADOR));
                        entradaValida.put("EST_IDENTIFICADOR", estadoIdentificadorMap.get(adeTO.getAttribute(Columns.ORG_CODIGO)));
                    }
                } else {

                    entradaValida.put("EST_IDENTIFICADOR", estadoIdentificadorMap.get(adeTO.getAttribute(Columns.ORG_CODIGO)));
                }

            } catch (HQueryException e) {
                LOG.warn(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel) + " não encontrado na geração de lote de sincronia.");
            }
        }

        if (TextHelper.isNull(entradaValida.get("SVC_IDENTIFICADOR"))) {
            ListaServicoNaturezaServicoQuery lstNseQry = new ListaServicoNaturezaServicoQuery();
            lstNseQry.svcIdentificador = (String) entradaValida.get("SVC_IDENTIFICADOR");

            try {
                if (!naturezaServicoMap.containsKey(adeTO.getAttribute(Columns.SVC_IDENTIFICADOR))) {
                    List<TransferObject> lstNse = lstNseQry.executarDTO();
                    if (lstNse != null && !lstNse.isEmpty()) {
                        naturezaServicoMap.put((String) adeTO.getAttribute(Columns.SVC_IDENTIFICADOR), (String) lstNse.get(0).getAttribute(Columns.NSE_CODIGO));
                    }
                }

                entradaValida.put((String) adeTO.getAttribute(Columns.SVC_IDENTIFICADOR), naturezaServicoMap.get(adeTO.getAttribute(Columns.SVC_IDENTIFICADOR)));

            } catch (HQueryException e) {
                LOG.warn(ApplicationResourcesHelper.getMessage("rotulo.natureza.servico.titulo", responsavel) + " não encontrada na geração de lote de sincronia.");
            }
        }

        if (TextHelper.isNull(entradaValida.get("CSA_IDENTIFICADOR"))) {
            if (acaoSincronia == AcaoSincronia.ALTERACAO) {
                Consignataria csa = null;
                try {
                    if (!consignatariaMap.containsKey(adeTO.getAttribute(Columns.CSA_CODIGO))) {
                        csa = ConsignatariaHome.findByPrimaryKey((String) adeTO.getAttribute(Columns.CSA_CODIGO));

                        consignatariaMap.put((String) adeTO.getAttribute(Columns.CSA_CODIGO), csa.getCsaIdentificador());
                    }
                    entradaValida.put("CSA_IDENTIFICADOR", consignatariaMap.get(adeTO.getAttribute(Columns.CSA_CODIGO)));

                } catch (FindException e) {
                    LOG.warn(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel) + " não encontrada na geração de lote de sincronia.");
                }
            } else if (acaoSincronia == AcaoSincronia.EXCLUSAO) {
                entradaValida.put("CSA_IDENTIFICADOR", adeTO.getAttribute(Columns.CSA_IDENTIFICADOR));
            }
        }

        if (entradaValida.get("ADE_VLR_LIQUIDO") == null) {
            entradaValida.put("ADE_VLR_LIQUIDO", (adeTO.getAttribute(Columns.ADE_VLR_LIQUIDO) != null) ? adeTO.getAttribute(Columns.ADE_VLR_LIQUIDO) : " ");
        }
        if (TextHelper.isNull(entradaValida.get("CNV_COD_VERBA"))) {
            entradaValida.put("CNV_COD_VERBA", (adeTO.getAttribute(Columns.CNV_COD_VERBA) != null) ? adeTO.getAttribute(Columns.CNV_COD_VERBA) : adeTO.getAttribute(Columns.CNV_COD_VERBA_REF));
        }
        if (entradaValida.get("ADE_DATA") == null) {
            entradaValida.put("ADE_DATA", DateHelper.format((Date) adeTO.getAttribute(Columns.ADE_DATA), "yyyy-MM-dd"));
        } else {
            entradaValida.put("ADE_DATA", DateHelper.format((Date) entradaValida.get("ADE_DATA"), "yyyy-MM-dd"));
        }
        if (entradaValida.get("ADE_ANO_MES_INI") == null) {
            entradaValida.put("ADE_ANO_MES_INI", DateHelper.format((Date) adeTO.getAttribute(Columns.ADE_ANO_MES_INI), "yyyy-MM-dd"));
        } else {
            entradaValida.put("ADE_ANO_MES_INI", DateHelper.format((Date) entradaValida.get("ADE_ANO_MES_INI"), "yyyy-MM-dd"));
        }
        if (entradaValida.get("ADE_ANO_MES_FIM") == null) {
            entradaValida.put("ADE_ANO_MES_FIM", DateHelper.format((Date) adeTO.getAttribute(Columns.ADE_ANO_MES_FIM), "yyyy-MM-dd"));
        } else {
            entradaValida.put("ADE_ANO_MES_FIM", DateHelper.format((Date) entradaValida.get("ADE_ANO_MES_FIM"), "yyyy-MM-dd"));
        }
        if (entradaValida.get("ADE_ANO_MES_INI_REF") == null) {
            entradaValida.put("ADE_ANO_MES_INI_REF", DateHelper.format((Date) adeTO.getAttribute(Columns.ADE_ANO_MES_INI_REF), "yyyy-MM-dd"));
        } else {
            entradaValida.put("ADE_ANO_MES_INI_REF", DateHelper.format((Date) entradaValida.get("ADE_ANO_MES_INI_REF"), "yyyy-MM-dd"));
        }
        if (entradaValida.get("ADE_ANO_MES_FIM_REF") == null) {
            entradaValida.put("ADE_ANO_MES_FIM_REF", DateHelper.format((Date) adeTO.getAttribute(Columns.ADE_ANO_MES_FIM_REF), "yyyy-MM-dd"));
        } else {
            entradaValida.put("ADE_ANO_MES_FIM_REF", DateHelper.format((Date) entradaValida.get("ADE_ANO_MES_FIM_REF"), "yyyy-MM-dd"));
        }
        if (TextHelper.isNull(entradaValida.get("ADE_IDENTIFICADOR"))) {
            entradaValida.put("ADE_IDENTIFICADOR", (adeTO.getAttribute(Columns.ADE_IDENTIFICADOR) != null) ? adeTO.getAttribute(Columns.ADE_IDENTIFICADOR) : " ");
        }
        if (TextHelper.isNull(entradaValida.get("ADE_IDENTIFICADOR_VERIFICAR"))) {
            entradaValida.put("ADE_IDENTIFICADOR_VERIFICAR", adeTO.getAttribute(Columns.ADE_IDENTIFICADOR));
        }
        if (entradaValida.get("ADE_VLR_TAC") == null) {
            entradaValida.put("ADE_VLR_TAC", (adeTO.getAttribute(Columns.ADE_VLR_TAC) != null) ? adeTO.getAttribute(Columns.ADE_VLR_TAC) : " ");
        }
        if (entradaValida.get("ADE_VLR_IOF") == null) {
            entradaValida.put("ADE_VLR_IOF", (adeTO.getAttribute(Columns.ADE_VLR_IOF) != null) ? adeTO.getAttribute(Columns.ADE_VLR_IOF) : " ");
        }
        if (entradaValida.get("ADE_VLR_MENS_VINC") == null) {
            entradaValida.put("ADE_VLR_MENS_VINC", (adeTO.getAttribute(Columns.ADE_VLR_MENS_VINC) != null) ? adeTO.getAttribute(Columns.ADE_VLR_MENS_VINC) : " ");
        }
        if (entradaValida.get("ADE_TAXA_JUROS") == null) {
            entradaValida.put("ADE_TAXA_JUROS", (adeTO.getAttribute(Columns.ADE_TAXA_JUROS) != null) ? adeTO.getAttribute(Columns.ADE_TAXA_JUROS) : " ");
        }
        if (TextHelper.isNull(entradaValida.get("ADE_CARENCIA"))) {
            entradaValida.put("ADE_CARENCIA", adeTO.getAttribute(Columns.ADE_CARENCIA));
        }
        if (TextHelper.isNull(entradaValida.get("ADE_INDICE"))) {
            entradaValida.put("ADE_INDICE", adeTO.getAttribute(Columns.ADE_INDICE));
        }

        entradaAde.add(entradaValida);

        return entradaAde;
    }

    private List<Map<String, Object>> addLinhaToSincroniaList(Map<String, Object> entradaValida, TransferObject servidor, List<Map<String, Object>> entradaAde, String csaCodigo, AcaoSincronia acaoSincronia, boolean processandoConciliacaoBeneficio, AcessoSistema responsavel) {
        if (entradaAde == null) {
            entradaAde = new ArrayList<>();
        }

        Consignataria csa = null;
        try {
            csa = ConsignatariaHome.findByPrimaryKey(csaCodigo);
        } catch (FindException e) {
            LOG.warn(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel) + " não encontrada na geração de lote de sincronia.");
        }

        switch (acaoSincronia) {
            case INCLUSAO:
                entradaValida.put("OPERACAO", "I");
                break;
            case EXCLUSAO:
                entradaValida.put("OPERACAO", "E");
                break;
            case ALTERACAO:
                entradaValida.put("OPERACAO", "A");
                break;
        }

        if (processandoConciliacaoBeneficio) {
            if (TextHelper.isNull(entradaValida.get("BFC_CPF"))) {
                entradaValida.put("BFC_CPF", servidor.getAttribute(Columns.BFC_CPF));
            }
        }

        if (TextHelper.isNull(entradaValida.get("SER_CPF"))) {
            entradaValida.put("SER_CPF", servidor.getAttribute(Columns.SER_CPF));
        }

        if (TextHelper.isNull(entradaValida.get("SER_NOME"))) {
            entradaValida.put("SER_NOME", servidor.getAttribute(Columns.SER_NOME));
        }

        if (TextHelper.isNull(entradaValida.get("ORG_IDENTIFICADOR"))) {
            entradaValida.put("ORG_IDENTIFICADOR", servidor.getAttribute(Columns.ORG_IDENTIFICADOR));
        }

        if (TextHelper.isNull(entradaValida.get("EST_IDENTIFICADOR"))) {
            entradaValida.put("EST_IDENTIFICADOR", servidor.getAttribute(Columns.EST_IDENTIFICADOR));
        }

        if (TextHelper.isNull(entradaValida.get("CSA_IDENTIFICADOR")) && csa != null) {
            entradaValida.put("CSA_IDENTIFICADOR", csa.getCsaIdentificador());
        }

        if (TextHelper.isNull(entradaValida.get("RSE_MATRICULA"))) {
            entradaValida.put("RSE_MATRICULA", servidor.getAttribute(Columns.RSE_MATRICULA));
        }

        if (TextHelper.isNull(entradaValida.get("CNV_COD_VERBA"))) {
            ListaConvenioPelosIdentificadoresQuery lstConvenios = new ListaConvenioPelosIdentificadoresQuery();
            lstConvenios.orgIdentificador = !TextHelper.isNull(entradaValida.get("ORG_IDENTIFICADOR")) ? (String) entradaValida.get("ORG_IDENTIFICADOR") : (String) servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
            lstConvenios.svcIdentificador = (String) entradaValida.get("SVC_IDENTIFICADOR");
            lstConvenios.csaIdentificador = csa.getCsaIdentificador();

            List<TransferObject> lstCnv;
            try {
                lstCnv = lstConvenios.executarDTO();

                for (TransferObject convenio : lstCnv) {
                    if (convenio.getAttribute(Columns.CNV_SCV_CODIGO).equals(CodedValues.SCV_ATIVO)) {
                        String codVerba = !TextHelper.isNull(convenio.getAttribute(Columns.CNV_COD_VERBA)) ? (String) convenio.getAttribute(Columns.CNV_COD_VERBA) : (String) convenio.getAttribute(Columns.CNV_COD_VERBA_REF);
                        if (!TextHelper.isNull(codVerba)) {
                            entradaValida.put("CNV_COD_VERBA", codVerba);
                            if (TextHelper.isNull(entradaValida.get("SVC_IDENTIFICADOR"))) {
                                entradaValida.put("SVC_IDENTIFICADOR", convenio.getAttribute(Columns.SVC_IDENTIFICADOR));
                            }
                            if (TextHelper.isNull(entradaValida.get("ORG_IDENTIFICADOR"))) {
                                entradaValida.put("ORG_IDENTIFICADOR", convenio.getAttribute(Columns.ORG_IDENTIFICADOR));
                            }
                            if (TextHelper.isNull(entradaValida.get("EST_IDENTIFICADOR"))) {
                                entradaValida.put("EST_IDENTIFICADOR", convenio.getAttribute(Columns.EST_IDENTIFICADOR));
                            }
                            break;
                        }
                    }
                }
            } catch (HQueryException e) {
                LOG.warn("Nenhum " + ApplicationResourcesHelper.getMessage("rotulo.convenio.singular", responsavel) + " encontrado na geração de lote de sincronia.");
            }
        } else {
            ListaConvenioPelosIdentificadoresQuery lstConvenios = new ListaConvenioPelosIdentificadoresQuery();
            lstConvenios.cnvCodVerba = (String) entradaValida.get("CNV_COD_VERBA");
            lstConvenios.orgIdentificador = !TextHelper.isNull(entradaValida.get("ORG_IDENTIFICADOR")) ? (String) entradaValida.get("ORG_IDENTIFICADOR") : (String) servidor.getAttribute(Columns.ORG_IDENTIFICADOR);
            lstConvenios.svcIdentificador = (String) entradaValida.get("SVC_IDENTIFICADOR");
            lstConvenios.csaIdentificador = csa.getCsaIdentificador();

            List<TransferObject> lstCnv;
            try {
                lstCnv = lstConvenios.executarDTO();

                for (TransferObject convenio : lstCnv) {
                    if (convenio.getAttribute(Columns.CNV_SCV_CODIGO).equals(CodedValues.SCV_ATIVO)) {
                        if (TextHelper.isNull(entradaValida.get("SVC_IDENTIFICADOR"))) {
                            entradaValida.put("SVC_IDENTIFICADOR", convenio.getAttribute(Columns.SVC_IDENTIFICADOR));
                        }
                        if (TextHelper.isNull(entradaValida.get("ORG_IDENTIFICADOR"))) {
                            entradaValida.put("ORG_IDENTIFICADOR", convenio.getAttribute(Columns.ORG_IDENTIFICADOR));
                        }
                        if (TextHelper.isNull(entradaValida.get("EST_IDENTIFICADOR"))) {
                            entradaValida.put("EST_IDENTIFICADOR", convenio.getAttribute(Columns.EST_IDENTIFICADOR));
                        }
                        break;
                    }
                }
            } catch (HQueryException e) {
                LOG.warn("Nenhum " + ApplicationResourcesHelper.getMessage("rotulo.convenio.singular", responsavel) + " encontrado na geração de lote de sincronia.");
            }
        }

        if (TextHelper.isNull(entradaValida.get("NSE_CODIGO")) && !TextHelper.isNull(entradaValida.get("SVC_IDENTIFICADOR"))) {
            ListaServicoNaturezaServicoQuery lstNseQry = new ListaServicoNaturezaServicoQuery();
            lstNseQry.svcIdentificador = (String) entradaValida.get("SVC_IDENTIFICADOR");

            try {
                List<TransferObject> lstNse = lstNseQry.executarDTO();
                if (lstNse != null && !lstNse.isEmpty()) {
                    entradaValida.put("NSE_CODIGO", lstNse.get(0).getAttribute(Columns.NSE_CODIGO));
                }
            } catch (HQueryException e) {
                LOG.warn(ApplicationResourcesHelper.getMessage("rotulo.natureza.servico.titulo", responsavel) + " não encontrada na geração de lote de sincronia.");
            }
        }

        if (entradaValida.get("ADE_ANO_MES_INI") != null) {
            entradaValida.put("ADE_ANO_MES_INI", DateHelper.format((Date) entradaValida.get("ADE_ANO_MES_INI"), "yyyy-MM-dd"));
        }
        if (entradaValida.get("ADE_ANO_MES_FIM") != null) {
            entradaValida.put("ADE_ANO_MES_FIM", DateHelper.format((Date) entradaValida.get("ADE_ANO_MES_FIM"), "yyyy-MM-dd"));
        }

        entradaAde.add(entradaValida);

        return entradaAde;
    }

    private String geraLoteSincronia(String csaCodigo, List<Map<String, Object>> entradaAde, AcessoSistema responsavel) throws ViewHelperException {
        // Pega parâmetros de configuração do sistema
        String absolutePath = ParamSist.getDiretorioRaizArquivos();
        String pathLoteSincronia = null;
        if (!TextHelper.isNull(csaCodigo)) {
            pathLoteSincronia = absolutePath + File.separatorChar + "conf" + File.separatorChar + "sincronia" + File.separatorChar + "csa" + File.separatorChar + csaCodigo;
        } else {
            pathLoteSincronia = absolutePath + File.separatorChar + "conf" + File.separatorChar + "sincronia" + File.separatorChar + "cse";
        }

        File dirLoteSincronia = new File(pathLoteSincronia);
        if (!dirLoteSincronia.exists()) {
            dirLoteSincronia.mkdirs();
        }

        String caminhoSaida = null;
        if (!TextHelper.isNull(csaCodigo)) {
            caminhoSaida = absolutePath + File.separatorChar + "conciliacao" + File.separatorChar + "csa" + File.separatorChar + csaCodigo;
        } else {
            caminhoSaida = absolutePath + File.separatorChar + "conciliacao" + File.separatorChar + "cse";
        }

        File dirCaminhoSaida = new File(caminhoSaida);
        if (!dirCaminhoSaida.exists()) {
            dirCaminhoSaida.mkdirs();
        }

        String pathSincroniaDefault = absolutePath + File.separatorChar + "conf" + File.separatorChar + "sincronia" + File.separatorChar + "xml" + File.separatorChar;

        String saidaSincronia = pathLoteSincronia + File.separatorChar + "saida_lote_sincronia.xml";
        String tradutorSincronia = pathLoteSincronia + File.separatorChar + "tradutor_lote_sincronia.xml";

        String saidaSincroniaDefault = pathSincroniaDefault + "saida_lote_sincronia.xml";
        String tradutorSincroniaDefault = pathSincroniaDefault + "tradutor_lote_sincronia.xml";

        File arqConfSaida = new File(saidaSincronia);
        File arqConfTradutor = new File(tradutorSincronia);

        if (!arqConfSaida.exists() || !arqConfTradutor.exists()) {
            File arqConfSaidaDefault = new File(saidaSincroniaDefault);
            File arqConfTradutorDefault = new File(tradutorSincroniaDefault);
            if (!arqConfSaidaDefault.exists() || !arqConfTradutorDefault.exists()) {
                throw new ViewHelperException("mensagem.erro.arquivos.configuracao.conciliacao.ausentes", responsavel);
            } else {
                arqConfSaida = arqConfSaidaDefault;
                arqConfTradutor = arqConfTradutorDefault;
                saidaSincronia = saidaSincroniaDefault;
                tradutorSincronia = tradutorSincroniaDefault;
            }
        }

        String dataAtual = DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss");
        String nomeArqSaida = caminhoSaida + File.separatorChar + "lote_sincronia_" + dataAtual + ".TXT";

        LeitorList leitor = new LeitorList(entradaAde);
        EscritorArquivoTexto escritor = new EscritorArquivoTexto(saidaSincronia, nomeArqSaida);
        Tradutor tradutor = new Tradutor(tradutorSincronia, leitor, escritor);
        try {
            tradutor.traduz();
        } catch (ParserException e) {
            throw new ViewHelperException(e);
        }

        return nomeArqSaida;
    }

    /**
     * Carrega no cache de parâmetros de consignatária os parâmetros relativo a conciliação
     * @throws ViewHelperException
     * @param csaCodigo
     * @param responsavel
     */
    private void atualizaCacheParamCsa(Map<String, Map<String, Boolean>> mapCacheParamCsa, String csaCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException, ViewHelperException {
        Map<String, Boolean> cacheParametrosCsa = mapCacheParamCsa.get(csaCodigo);
        if (cacheParametrosCsa == null || cacheParametrosCsa.isEmpty()) {
            cacheParametrosCsa = new HashMap<>();

            // Parâmetro TPA_GERAR_LOTE_SINCRONIA_CONCILIACAO
            cacheParametrosCsa.put(CodedValues.TPA_GERAR_LOTE_SINCRONIA_CONCILIACAO, TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_GERAR_LOTE_SINCRONIA_CONCILIACAO, responsavel)));

            // Parâmetro TPA_PERMITE_INCLUSAO_COM_SER_DUPLICADO_LOTE
            cacheParametrosCsa.put(CodedValues.TPA_PERMITE_INCLUSAO_COM_SER_DUPLICADO_LOTE, TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_PERMITE_INCLUSAO_COM_SER_DUPLICADO_LOTE, responsavel)));

            // Parâmetro TPA_UTILIZA_SERVIDOR_COM_MAIOR_MARGEM_LOTE
            final boolean utilizarRegistroServidorMaiorMargem = TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_UTILIZA_SERVIDOR_COM_MAIOR_MARGEM_LOTE, responsavel));
            cacheParametrosCsa.put(CodedValues.TPA_UTILIZA_SERVIDOR_COM_MAIOR_MARGEM_LOTE, utilizarRegistroServidorMaiorMargem);

            // Parâmetro TPA_UTILIZA_SERVIDOR_COM_MENOR_MARGEM_LOTE
            cacheParametrosCsa.put(CodedValues.TPA_UTILIZA_SERVIDOR_COM_MENOR_MARGEM_LOTE, !utilizarRegistroServidorMaiorMargem && TPA_SIM.equals(ParamCsa.getParamCsa(csaCodigo, CodedValues.TPA_UTILIZA_SERVIDOR_COM_MENOR_MARGEM_LOTE, responsavel)));

            mapCacheParamCsa.put(csaCodigo, cacheParametrosCsa);
        }
    }

    /**
     * define constantes para mapear ações de lote de sincronia
     * @author fagner
     *
     */
    private enum AcaoSincronia {
        INCLUSAO, EXCLUSAO, ALTERACAO
    }
}
