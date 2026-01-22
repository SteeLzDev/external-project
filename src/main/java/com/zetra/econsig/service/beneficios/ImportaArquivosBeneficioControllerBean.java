package com.zetra.econsig.service.beneficios;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ImportaArquivosBeneficioControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.parser.Escritor;
import com.zetra.econsig.parser.EscritorMemoria;
import com.zetra.econsig.parser.LeitorArquivoTexto;
import com.zetra.econsig.parser.LeitorArquivoTextoZip;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.ImportaArquivoRetornoOperadoraDAO;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.HistoricoIntegracaoBeneficioHome;
import com.zetra.econsig.persistence.query.beneficios.ObtemUltimoDataHistoricoIntegracaoBeneficioQuery;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;


/**
 * <p>Title: ImportaArquivosBeneficioControllerBean</p>
 * <p>Description: Controler Bean do caso de uso importa arquivo benefio.</p>
 * <p>Copyright: Copyright (c) 2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ImportaArquivosBeneficioControllerBean implements ImportaArquivosBeneficioController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExportaArquivosBeneficioControllerBean.class);

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private CalcularSubsidioBeneficioController calcularSubsidioBeneficioController;

    /**
     * metodo que realiza os fluxo de importação do arquivo;
     * @param csaCodigo
     * @param nomeArquivo
     * @param responsavel
     * @throws ImportaArquivosBeneficioControllerException
     */
    @Override
    public void importaArquivoRetornoOperadora(String csaCodigo, String nomeArquivo, AcessoSistema responsavel) throws ImportaArquivosBeneficioControllerException {
        try {
            Consignataria consignataria;
            try {
                consignataria = ConsignatariaHome.findByPrimaryKey(csaCodigo);
            } catch (FindException e) {
                throw new ImportaArquivosBeneficioControllerException("mensagem.erro.consignataria.nao.encontrada", responsavel);
            }

            // Busca data do ultimo historico de integração
            Date hibDataFim = obtemUltimoDataHistoricoIntegracaoImportacao(csaCodigo);

            if (hibDataFim == null) {
                hibDataFim = new Date();
            }

            ParamSist paramSist = ParamSist.getInstance();

            // Diretório raiz de arquivos eConsig
            String absolutePath = ParamSist.getDiretorioRaizArquivos();

            String pathRetornoBeneficio = absolutePath + File.separatorChar + "retornobeneficio" + File.separatorChar + "csa" + File.separatorChar + csaCodigo;
            String nomeArquivoAbsoluto = pathRetornoBeneficio + File.separatorChar +  nomeArquivo;

            if (!new File(pathRetornoBeneficio).exists()) {
                new File(pathRetornoBeneficio).mkdirs();
            }

            if (!new File(nomeArquivoAbsoluto).exists()) {
                throw new ImportaArquivosBeneficioControllerException("mensagem.erro.arquivo.retorno.operadora.ausente", responsavel, nomeArquivo, pathRetornoBeneficio);
            }

            String pathConf = absolutePath + File.separatorChar + "conf";
            String pathConfCsa = absolutePath + File.separatorChar + "conf" + File.separatorChar + "retornobeneficio"  + File.separatorChar + csaCodigo;

            // Arquivos de configuração para processamento do retorno
            // Inicialmente vamos fixar o nome
            String nomeArqConfEntrada = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_IMP_ARQ_OPERADORA, responsavel);
            String nomeArqConfTradutor = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_IMP_ARQ_OPERADORA, responsavel);

            // Criando o caminho absoluto dele.
            String nomeArqConfEntradaAbsoluto = pathConf + File.separatorChar + nomeArqConfEntrada;
            String nomeArqConfTradutorAbsoluto = pathConf + File.separatorChar + nomeArqConfTradutor;

            // Analisando se os arquivos de configuração existem
            if (!new File(nomeArqConfEntradaAbsoluto).exists() || !new File(nomeArqConfTradutorAbsoluto).exists()) {
                nomeArqConfEntradaAbsoluto = pathConfCsa + File.separatorChar + nomeArqConfEntrada;
                nomeArqConfTradutorAbsoluto = pathConfCsa + File.separatorChar + nomeArqConfTradutor;

                if (!new File(nomeArqConfEntradaAbsoluto).exists() || !new File(nomeArqConfTradutorAbsoluto).exists()) {
                    throw new ImportaArquivosBeneficioControllerException("mensagem.erro.arquivo.configuracao.layout.importacao.arquivos.operadora", responsavel);
                }
            }

            LOG.info("Arquivo de configuração de entrada: " + nomeArqConfEntradaAbsoluto);
            LOG.info("Arquivo de configuração de tradutor: " + nomeArqConfTradutorAbsoluto);

            // Hash que recebe os dados do que serão lidos do arquivo de entrada
            Map<String, Object> entrada = new HashMap<>();
            // Configura o leitor de acordo com o arquivo de entrada
            LeitorArquivoTexto leitor = null;
            if (nomeArquivoAbsoluto.toLowerCase().endsWith(".zip")) {
                leitor = new LeitorArquivoTextoZip(nomeArqConfEntradaAbsoluto, nomeArquivoAbsoluto);
            } else {
                leitor = new LeitorArquivoTexto(nomeArqConfEntradaAbsoluto, nomeArquivoAbsoluto);
            }
            // Prepara tradução do arquivo de retorno.
            Escritor escritor = new EscritorMemoria(entrada);
            Tradutor tradutor = new Tradutor(nomeArqConfTradutorAbsoluto, leitor, escritor);

            ImportaArquivoRetornoOperadoraDAO importaArquivoRetornoOperadoraDAO = DAOFactory.getDAOFactory().getImportaArquivoRetornoOperadoraDAO();

            // Criar a tabela temporaria que vai conter os campos de mapeado e etc.
            importaArquivoRetornoOperadoraDAO.deletaTabelaTemporariaArquivoRetorno();
            importaArquivoRetornoOperadoraDAO.criarTabelaTemporariaArquivoRetorno();

            // Com base no arquivo de entrada vamos carregar ele para a tabela.
            carregaArquivoRetornoOperadora(csaCodigo, entrada, leitor, tradutor, importaArquivoRetornoOperadoraDAO, responsavel);

            // Gravando o perido atual na tabela para ser utilzado na criação de ocorrencia.
            definirPeriodo(responsavel);

            // Realizar o mapeamento de linhas de INCLUSAO que achamos
            LOG.info("Inicio do mapemanento dos contratos, operação de inclusão.");
            importaArquivoRetornoOperadoraDAO.realizarMapeamentoContratosBeneficioOperacaoInclusao();
            LOG.info("Fim do mapemanento dos contratos, operação de inclusão.");

            LOG.info("Inicio da alteração dos contratos, operação de inclusão.");
            List<String> rseCodigosInclusao = importaArquivoRetornoOperadoraDAO.realizarAlteracaoContratosBeneficioOperacaoInclusao(responsavel);
            LOG.info("Fim da alteração dos contratos, operação de inclusão.");

            // Realizar o mapeamento de linhas de INCLUSAO que tem MIGRACAO que achamos.
            LOG.info("Inicio da alteração dos contratos, operação de inclusão com migração.");
            List<String> rseCodigosInclusaoMigracao = importaArquivoRetornoOperadoraDAO.realizarMapeamentoContratosBeneficioOperacaoInclusaoMigracao(responsavel);
            LOG.info("Fim da alteração dos contratos, operação de inclusão com migração.");

            // Realizar o mapeamento de linhas de EXCLUSÂO que achamos
            LOG.info("Inicio do mapemanento dos contratos, operação de exclusão.");
            importaArquivoRetornoOperadoraDAO.realizarMapeamentoContratosBeneficioOperacaoExclusao();
            LOG.info("Fim do mapemanento dos contratos, operação de exclusão.");

            LOG.info("Inicio da alteração dos contratos, operação de exclusão.");
            List<String> rseCodigosExclusao = importaArquivoRetornoOperadoraDAO.realizarAlteracaoContratosBeneficioOperacaoExclusao(responsavel);
            LOG.info("Fim da alteração dos contratos, operação de exclusão.");

            // Realizando o calculo da ordem de dependencia apos aprovação da operadora.
            Set<String> rseCodigos = new HashSet<>();
            rseCodigos.addAll(rseCodigosInclusao);
            rseCodigos.addAll(rseCodigosInclusaoMigracao);
            rseCodigos.addAll(rseCodigosExclusao);

            // Se teve alguma matricula que teve contrato incluido ou excluido vamos fazer o recalculo de subcidio
            if (!rseCodigos.isEmpty()) {
                LOG.info("Inicio do recalculo de subsidio.");
                calcularSubsidioBeneficioController.calcularSubsidioContratosBeneficios(null, false, null, "RSE", new ArrayList<>(rseCodigos), responsavel);
                LOG.info("Fim do recalculo de subsidio.");

                LOG.info("Inicio do recalculo de margem.");
                margemController.recalculaMargemComHistorico("RSE", new ArrayList<>(rseCodigos), responsavel);
                LOG.info("Fim do recalculo de margem.");
            }

            // Buscando as linhas que não foram mapeadas.
            List<String> linhasSemMapeamento = importaArquivoRetornoOperadoraDAO.geraLinhasNaoMapedasParaCritica(responsavel);
            String sufixCritica = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.critica.prefixo", responsavel);
            String tmpNome = nomeArquivo.substring(0, nomeArquivo.lastIndexOf('.'));
            String arquivoCritica = tmpNome + "_" + DateHelper.format(DateHelper.getSystemDatetime(), "yyyyMMddHHmmss") + ".txt";
            String arquivoCriticaAbsoluto = pathRetornoBeneficio + File.separatorChar + sufixCritica + arquivoCritica;
            if (!linhasSemMapeamento.isEmpty()) {
                PrintWriter printWriterArquivoCritica = new PrintWriter(new BufferedWriter(new FileWriter(arquivoCriticaAbsoluto)));
                for (String linha : linhasSemMapeamento) {
                    printWriterArquivoCritica.println(linha);
                }
                printWriterArquivoCritica.close();
            }

            //Enviando email com o resultado do processamento.
            EnviaEmailHelper.enviaEmailImportacaoArquivosOperadora(nomeArquivo, arquivoCritica, consignataria.getCsaNome(), linhasSemMapeamento.size(), responsavel);

            geraHistoricoIntegracaoOperadora(csaCodigo, hibDataFim, new Date(), responsavel);

            // Renomeia o arquivo de entrada depois de concluido com sucesso
            File renomear = new File(nomeArquivoAbsoluto);
            renomear.renameTo(new File(nomeArquivoAbsoluto + ".prc"));

            importaArquivoRetornoOperadoraDAO.deletaTabelaTemporariaArquivoRetorno();
        } catch (ImportaArquivosBeneficioControllerException | MargemControllerException | IOException | CreateException | HQueryException | PeriodoException | BeneficioControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ImportaArquivosBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Metodo que realizar a carga do arquivo para a tabela temporaria
     * @param csaCodigo
     * @param entrada
     * @param leitor
     * @param tradutor
     * @param importaArquivoRetornoOperadoraDAO
     * @param responsavel
     * @throws ImportaArquivosBeneficioControllerException
     */
    private void carregaArquivoRetornoOperadora(String csaCodigo, Map<String, Object> entrada, LeitorArquivoTexto leitor, Tradutor tradutor, ImportaArquivoRetornoOperadoraDAO importaArquivoRetornoOperadoraDAO, AcessoSistema responsavel) throws ImportaArquivosBeneficioControllerException {
        try {
            LOG.info("Inicio da importação do arquivo");
            tradutor.iniciaTraducao();

            int totalProcessado = 0;
            final String nomeArquivo = leitor.getNomeArquivo();

            while (tradutor.traduzProximo()) {
                String operacao = (String) entrada.get("OPERACAO");

                // Dados do contrato beneficio
                String cbeNumero = (String) entrada.get("CBE_NUMERO");
                String cbeDataInicioVigencia = (String) entrada.get("CBE_DATA_INICIO_VIGENCIA");
                String cbeDataFimVigencia = (String) entrada.get("CBE_DATA_FIM_VIGENCIA");

                // Dados do beneficiario
                String bfcCpf = (String) entrada.get("BFC_CPF");

                // Dados do beneficio
                String benCodigoContrato = (String) entrada.get("BEN_CODIGO_CONTRATO");

                // Realizando os escape
                int numeroLinha = leitor.getNumeroLinha();
                String linha = leitor.getLinha();

                importaArquivoRetornoOperadoraDAO.realizarInsertTabelaTemporariaArquivoRetorno(nomeArquivo, numeroLinha, operacao, csaCodigo, benCodigoContrato, cbeNumero, cbeDataInicioVigencia, cbeDataFimVigencia, bfcCpf, linha);

                totalProcessado++;

                if (totalProcessado % 1000 == 0) {
                    LOG.info("Linha importadas: " + totalProcessado);
                }
            }

            LOG.info("Linha importadas: " + totalProcessado);
            LOG.info("Fim da importação do arquivo");
        } catch (ParserException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new ImportaArquivosBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private void definirPeriodo(AcessoSistema responsavel) throws PeriodoException{
        periodoController.obtemPeriodoBeneficio(null, null, true, null, responsavel);
    }

    /**
     * Metodo que grava no banco de dados o historico de integração
     * @param csaCodigo
     * @param hibDataIni
     * @param hibDataFim
     * @param responsavel
     * @throws CreateException
     */
    private void geraHistoricoIntegracaoOperadora(String csaCodigo, Date hibDataIni, Date hibDataFim, AcessoSistema responsavel)
            throws CreateException {
        String usuCodigo = (responsavel != null ? responsavel.getUsuCodigo() : null);

        char hibTipo = CodedValues.HIB_TIPO_RETORNO;

        HistoricoIntegracaoBeneficioHome.create(csaCodigo, usuCodigo, hibDataIni, hibDataFim, new Date(), String.valueOf(hibTipo));
    }

    /**
     * Metodo que obtem a maior data fim do historico de integração para uma determinada CSA
     * @return
     * @throws HQueryException
     * @throws FindException
     */
    private Date obtemUltimoDataHistoricoIntegracaoImportacao(String csaCodigo) throws HQueryException{
        ObtemUltimoDataHistoricoIntegracaoBeneficioQuery query = new ObtemUltimoDataHistoricoIntegracaoBeneficioQuery();
        query.csaCodigo = csaCodigo;
        query.hibTipo = CodedValues.HIB_TIPO_RETORNO;

        List<TransferObject> resultado = query.executarDTO();

        if (resultado == null || resultado.isEmpty()) {
            return null;
        } else {
            Calendar calendar = Calendar.getInstance();
            TransferObject hib = resultado.get(0);
            Date hibDataFim = (Date) hib.getAttribute(Columns.HIB_DATA_FIM);
            calendar.setTime(hibDataFim);
            calendar.add(Calendar.SECOND, 1);
            return calendar.getTime();
        }
    }
}
