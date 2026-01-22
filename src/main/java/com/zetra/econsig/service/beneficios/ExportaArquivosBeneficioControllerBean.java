package com.zetra.econsig.service.beneficios;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.ExportaArquivosBeneficioControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.parser.EscritorArquivoTexto;
import com.zetra.econsig.parser.Leitor;
import com.zetra.econsig.parser.LeitorListTO;
import com.zetra.econsig.parser.ParserException;
import com.zetra.econsig.parser.Tradutor;
import com.zetra.econsig.persistence.dao.DAOFactory;
import com.zetra.econsig.persistence.dao.ExportaArquivoOperadoraDAO;
import com.zetra.econsig.persistence.dao.RelatorioBeneficiariosDAO;
import com.zetra.econsig.persistence.dao.RelatorioConcessoesDeBeneficiosDAO;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.HistIntegracaoBeneficio;
import com.zetra.econsig.persistence.entity.HistoricoIntegracaoBeneficioHome;
import com.zetra.econsig.persistence.entity.Orgao;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.PeriodoBeneficio;
import com.zetra.econsig.persistence.entity.PeriodoBeneficioHome;
import com.zetra.econsig.persistence.query.arquivo.ListarArquivoRetornoQuery;
import com.zetra.econsig.persistence.query.relatorio.RelatorioBeneficioConsolidadoDIRFQuery;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ExportaArquivosBeneficioControllerBean</p>
 * <p>Description: Classe Bean para exportação de arquivos do modulo Beneficio</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ExportaArquivosBeneficioControllerBean implements ExportaArquivosBeneficioController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExportaArquivosBeneficioControllerBean.class);

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @Autowired
    private PeriodoController periodoController;

    /**
     * Meotodo responsavel por fazer as chamadas dos metodos de geração dos relatorios e enviar o email.
     * @param orgaos
     * @param periodo
     * @param periodoDataInicio
     * @param periodoDataFim
     * @param responsavel
     * @throws ExportaArquivosBeneficioControllerException
     */
    @Override
    public void geraRelatorioBeneficiariosEConcessoesDeBeneficios(List<String> orgaos, Date periodo, Date periodoDataInicio, Date periodoDataFim, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException {
        try {
            // Com base nas datas e orgao passados vamos gravar o periodo.
            LOG.info("Inicio da difinição dos periodos");
            definirPeriodo(orgaos, periodo, periodoDataInicio, periodoDataFim, responsavel);
            LOG.info("Fim da difinição dos periodos");

            // Pegando a data atual que será usada no resto do codigo.
            Calendar dataAtual = Calendar.getInstance();

            // Contem a lista de arquivos zip
            List<String> arquivosRelatorioZip = new ArrayList<>();

            // Gerando os arquivos de beneficiarios.
            // O clone() do dataAtual é para evitar dos metodos interno alterar o valor e ser refletido aqui na volta.
            LOG.info("Inicio da geração dos relatorio de beneficiarios");
            String arquivoBeneficiariosZip = geraRelatorioBeneficiarios(orgaos, periodo, (Calendar) dataAtual.clone(), responsavel);
            LOG.info("Fim da geração dos relatorio de beneficiarios");

            // Pegando o total de arquivos gerados para sabermos quantos minutos devemos acresentar para a chamada da proxima rotina.
            int totalArquivoGerados = FileHelper.contaArquivosZip(arquivoBeneficiariosZip);
            LOG.info("Total de arquivos gerados no relatorio de beneficiarios : " + totalArquivoGerados);
            dataAtual.add(Calendar.MINUTE, totalArquivoGerados);

            // Gerando os arquivos de concessoes de beneficios.
            // O clone() do dataAtual é para evitar dos metodos interno alterar o valor e ser refletido aqui na volta.
            LOG.info("Inicio da geração dos relatorio de concessão e beneficios");
            String arquivoConcessoesDeBeneficiosZip = geraRelatorioConcessoesDeBeneficios(orgaos, periodo, (Calendar) dataAtual.clone(), responsavel);
            LOG.info("Fim da geração dos relatorio de concessão e beneficios");
            totalArquivoGerados = FileHelper.contaArquivosZip(arquivoConcessoesDeBeneficiosZip);
            LOG.info("Total de arquivos gerados no relatorio de concessão e beneficios : " + totalArquivoGerados);

            arquivosRelatorioZip.add(arquivoBeneficiariosZip);
            arquivosRelatorioZip.add(arquivoConcessoesDeBeneficiosZip);

            // Enviado o email.
            LOG.info("Inicio do envio do email com os relatorios anexados.");
            EnviaEmailHelper.enviaEmailRelatorioBeneficiariosEConcessoesDeBeneficios(arquivosRelatorioZip, responsavel);
            LOG.info("Fim do envio do email com os relatorios anexados.");
        } catch (IOException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getCause(), e);
            throw new ExportaArquivosBeneficioControllerException(e);
        }
    }

    @Override
    public void geraRelatorioBeneficiosConsolidadosDirf(List<String> csaCodigos, String orgCodigo, String nomeArqRetorno, Date periodo, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException {
        // Diretório raiz de arquivos eConsig
        String absolutePath = ParamSist.getDiretorioRaizArquivos();

        String pathConf = absolutePath + File.separatorChar + "conf";
        String pathRelatorioConsolidadeDirf = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "dirf";

        // Valindando se o dirietorio final existe
        LOG.info("Analisando se o direito : " + pathRelatorioConsolidadeDirf + " existe.");
        File filePathRelatorioConcessao = new File(pathRelatorioConsolidadeDirf);
        if (!filePathRelatorioConcessao.exists()) {
            // Não existe e vamos tentar criar.
            LOG.info("Diretorio não existe, vamos tentar criar o mesmo.");
            filePathRelatorioConcessao.mkdirs();
        }

        // Arquivos de configuração para processamento do retorno
        // Inicialmente vamos fixar o nome
        String nomeArqConfEntrada = "relatorio_consolidados_dirf_entrada.xml";
        String nomeArqConfSaida = "relatorio_consolidados_dirf_saida.xml";
        String nomeArqConfTradutor = "relatorio_consolidados_dirf_tradutor.xml";

        // Criando o caminho absoluto dele.
        String nomeArqConfEntradaAbsoluto = pathConf + File.separatorChar + nomeArqConfEntrada;
        String nomeArqConfSaidaAbsoluto = pathConf + File.separatorChar + nomeArqConfSaida;
        String nomeArqConfTradutorAbsoluto = pathConf + File.separatorChar + nomeArqConfTradutor;

        LOG.info("Arquivo de configuração de entrada de relatório de benefícios DIRF: " + nomeArqConfEntradaAbsoluto);
        LOG.info("Arquivo de configuração de saida de relatório de benefícios DIRF: " + nomeArqConfSaidaAbsoluto);
        LOG.info("Arquivo de configuração de tradutor de relatório de benefícios DIRF: " + nomeArqConfTradutorAbsoluto);

        // Pegando os nomes do arquivo de como devem ser
        String nomeArquivoFinalTexto = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.movimento.consolidado.dirf", responsavel);

        // Variavel usada para calcular o nome do arquivo final
        Date agora = new Date(System.currentTimeMillis());
        String ddmmaaaa = DateHelper.format(agora, "ddMMyyyy");
        nomeArquivoFinalTexto = nomeArquivoFinalTexto.replaceAll("<<DDMMAAAA>>", ddmmaaaa);
        nomeArquivoFinalTexto = nomeArquivoFinalTexto.replaceAll("<<MES_POR_EXTENSO_AAAA>>", DateHelper.getMonthName(periodo).toUpperCase() + DateHelper.format(periodo, "yyyy"));
        nomeArquivoFinalTexto = nomeArquivoFinalTexto.replaceAll("<<NOME_SISTEMA>>", JspHelper.getNomeSistema(responsavel).replaceAll(" ", "_"));
        try {
            nomeArquivoFinalTexto = nomeArquivoFinalTexto.replaceAll("_<<ORG_IDENTIFICADOR_BENEFICIO>>", (orgCodigo != null && !orgCodigo.isEmpty() &&
                                    !TextHelper.isNull(orgCodigo)) ? "_" + OrgaoHome.findByPrimaryKey(orgCodigo).getOrgIdentificadorBeneficio() : "");
        } catch (FindException e) {
            LOG.error(e.getCause(), e);
            throw new ExportaArquivosBeneficioControllerException(e);
        }

        String nomeArquivoFinalTextoAbsoluto = pathRelatorioConsolidadeDirf + File.separatorChar + nomeArquivoFinalTexto;

        EscritorArquivoTexto escritor = new EscritorArquivoTexto(nomeArqConfSaidaAbsoluto, nomeArquivoFinalTextoAbsoluto);
        RelatorioBeneficioConsolidadoDIRFQuery relDirf = new RelatorioBeneficioConsolidadoDIRFQuery();
        relDirf.periodo = DateHelper.format(periodo, "yyyy-MM-dd");
        relDirf.lstOrgaos = !TextHelper.isNull(orgCodigo) ? Arrays.asList(new String [] {orgCodigo}) : null;
        relDirf.lstCsas = csaCodigos;

        boolean temInconsistencia = false;
        try {
            Leitor leitor = new LeitorListTO(relDirf.executarDTO());
            Tradutor tradutor = new Tradutor(nomeArqConfTradutorAbsoluto, leitor, escritor);
            tradutor.traduz();

            FileHelper.zip(Arrays.asList(new String [] {nomeArquivoFinalTextoAbsoluto}), nomeArquivoFinalTextoAbsoluto.replace(".txt", ".zip"));
            new File(nomeArquivoFinalTextoAbsoluto).delete();

            //Lê linhas de arquivo retorno do período que não foram mapeadas e/ou processadas para montar um arquivo de crítica.
            String orgNome = null;
            try {
                List<String> csaIdns = null;
                if(csaCodigos != null && !csaCodigos.isEmpty()) {
                    csaIdns = new ArrayList<>();
                    for (String csaCodigo: csaCodigos) {
                        csaIdns.add(ConsignatariaHome.findByPrimaryKey(csaCodigo).getCsaIdentificador());
                    }
                }

                List<String> orgIdns = null;
                if(!TextHelper.isNull(orgCodigo)) {
                    Orgao org = OrgaoHome.findByPrimaryKey(orgCodigo);
                    orgNome = org.getOrgNome();
                    orgIdns = new ArrayList<>();
                    orgIdns.add(org.getOrgIdentificador());
                }

                ListarArquivoRetornoQuery lstArqRetorno = new ListarArquivoRetornoQuery();
                lstArqRetorno.nomeArquivo = nomeArqRetorno;
                lstArqRetorno.lstCsaIdentificador = csaIdns;
                lstArqRetorno.lstOrgIdentificador = orgIdns;

                List<TransferObject> linhasArquivo = lstArqRetorno.executarDTO();
                if (linhasArquivo != null && !linhasArquivo.isEmpty()) {
                    BufferedWriter criticaRelatorioDirf = null;
                    String newline = System.getProperty("line.separator");

                    String separadorCritica = null;
                    for(TransferObject linha: linhasArquivo) {
                        if (linha.getAttribute(Columns.ART_MAPEADA).equals("N") || linha.getAttribute(Columns.ART_PROCESSADA).equals("N")) {
                            if (!temInconsistencia) {
                                separadorCritica = ((String) linha.getAttribute(Columns.ART_LINHA)).indexOf(";") == -1 ? ";":"|";
                                temInconsistencia = true;
                            }
                            if (criticaRelatorioDirf == null) {
                                criticaRelatorioDirf = new BufferedWriter(new FileWriter(pathRelatorioConsolidadeDirf + File.separatorChar + "critica_" + nomeArquivoFinalTexto));
                                criticaRelatorioDirf.append("LINHA ARQUIVO RETORNO").append(separadorCritica).append("MAPEADA").append(separadorCritica).append("PROCESSADA");
                                criticaRelatorioDirf.append(newline);
                            }

                            criticaRelatorioDirf.append((String) linha.getAttribute(Columns.ART_LINHA)).append(separadorCritica).append((String) linha.getAttribute(Columns.ART_MAPEADA)).append(separadorCritica).append((String) linha.getAttribute(Columns.ART_PROCESSADA));
                            criticaRelatorioDirf.append(newline);
                        }
                    }

                    if (criticaRelatorioDirf != null) {
                        criticaRelatorioDirf.close();
                    }

                    String arqCriticaNome = pathRelatorioConsolidadeDirf + File.separatorChar + "critica_" + nomeArquivoFinalTexto;
                    File fileArqCritica = new File(arqCriticaNome);
                    if (fileArqCritica.exists()) {
                        FileHelper.zip(Arrays.asList(new String [] {arqCriticaNome}), arqCriticaNome.replace(".txt", ".zip"));
                        fileArqCritica.delete();
                    }
                }
            } catch (FindException e1) {
                LOG.info(ApplicationResourcesHelper.getMessage("mensagem.erro.configuracao.nome.arquivo.retorno.nao.carregado", responsavel));
            }

            // Enviado o email.
            LOG.info("Inicio do envio do email de aviso de relatorio beneficios DIRF gerado.");
            EnviaEmailHelper.enviaEmailConclusaoRelatorioBeneficioDirf(periodo, orgNome, temInconsistencia, responsavel);
            LOG.info("Fim do envio do email de aviso de relatorio beneficios DIRF gerado.");
        } catch (ParserException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaArquivosBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaArquivosBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (IOException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ExportaArquivosBeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Realiza a gravação dos periodos para geração dos relatorios.
     * @param orgaos
     * @param periodo
     * @param periodoDataInicio
     * @param periodoDataFim
     * @param responsavel
     * @throws ExportaArquivosBeneficioControllerException
     */
    private void definirPeriodo(List<String> orgaos, Date periodo, Date periodoDataInicio, Date periodoDataFim, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException {
        // realizando o calculado do periodo e salvando na tb_periodo_beneficio
        try {
            // Caso o usuario informa o periodo vamos criar na tabela os valores informados.
            if (periodoDataInicio != null && periodoDataFim != null) {
                LOG.info("Usando datas informa pelo usuario.");
                Short diaCorte = (short) DateHelper.getDay(periodoDataFim);
                Date pbePeriodoAnt = DateHelper.addMonths(periodo, -1);
                Date pbePeriodoPos = DateHelper.addMonths(periodo, +1);
                Short pbeSequencia = 0;

                for (String orgao : orgaos) {
                    Collection<PeriodoBeneficio> periodosAntigosDoOrg = PeriodoBeneficioHome.findByOrgCodigo(orgao);
                    for (PeriodoBeneficio periodoBeneficio : periodosAntigosDoOrg) {
                        PeriodoBeneficioHome.remove(periodoBeneficio);
                    }

                    PeriodoBeneficioHome.create(orgao, periodo, pbePeriodoAnt, pbePeriodoPos, diaCorte, periodoDataInicio, periodoDataFim, pbeSequencia);
                }
            } else {
                LOG.info("Usando datas da tabela de periodo beneficio.");
                periodoController.obtemPeriodoBeneficio(orgaos, null, true, periodo, responsavel);
            }
        } catch (PeriodoException | CreateException | FindException | RemoveException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getCause(), e);
            throw new ExportaArquivosBeneficioControllerException(e);
        }
    }

    /**
     * Gera os relatorios de beneficios.
     * @param orgaos
     * @param periodo
     * @param dataAtual
     * @param responsavel
     * @return
     * @throws ExportaArquivosBeneficioControllerException
     */
    private String geraRelatorioBeneficiarios(List<String> orgaos, Date periodo, Calendar dataAtual, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException {
        // Diretório raiz de arquivos eConsig
        String absolutePath = ParamSist.getDiretorioRaizArquivos();

        String pathConf = absolutePath + File.separatorChar + "conf";
        String pathRelatorioConcessao = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "concessao";

        // Valindando se o dirietorio final existe
        LOG.info("Analisando se o direito : " + pathRelatorioConcessao + " existe.");
        File filePathRelatorioConcessao = new File(pathRelatorioConcessao);
        if (!filePathRelatorioConcessao.exists()) {
            // Não existe e vamos tentar criar.
            LOG.info("Diretorio não existe, vamos tentar criar o mesmo.");
            filePathRelatorioConcessao.mkdirs();
        }

        // Arquivos de configuração para processamento do retorno
        // Inicialmente vamos fixar o nome
        String nomeArqConfEntrada = "relatorio_beneficiarios_entrada.xml";
        String nomeArqConfSaida = "relatorio_beneficiarios_saida.xml";
        String nomeArqConfTradutor = "relatorio_beneficiarios_tradutor.xml";

        // Criando o caminho absoluto dele.
        String nomeArqConfEntradaAbsoluto = pathConf + File.separatorChar + nomeArqConfEntrada;
        String nomeArqConfSaidaAbsoluto = pathConf + File.separatorChar + nomeArqConfSaida;
        String nomeArqConfTradutorAbsoluto = pathConf + File.separatorChar + nomeArqConfTradutor;

        LOG.info("Arquivo de configuração de entrada: " + nomeArqConfEntradaAbsoluto);
        LOG.info("Arquivo de configuração de saida: " + nomeArqConfSaidaAbsoluto);
        LOG.info("Arquivo de configuração de tradutor: " + nomeArqConfTradutorAbsoluto);

        // Pegando os nomes do arquivo de como devem ser
        String nomeArquivoFinalTexto = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.beneficiarios", responsavel);
        String nomeArquivoFinalZip = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.beneficiarios.zip", responsavel);

        // Variavel usada para calcular o nome do arquivo final
        String mmaaaa = DateHelper.format(new Date(System.currentTimeMillis()), "MMyyyy");
        nomeArquivoFinalZip = nomeArquivoFinalZip.replaceAll("<<MMAAAA>>", mmaaaa);
        nomeArquivoFinalZip = nomeArquivoFinalZip.replaceAll("<<NOME_SISTEMA>>", JspHelper.getNomeSistema(responsavel).replaceAll(" ", "_"));

        // Analisando se os arquivos de configuração existem
        if (!new File(nomeArqConfEntradaAbsoluto).exists() || !new File(nomeArqConfSaidaAbsoluto).exists() || !new File(nomeArqConfTradutorAbsoluto).exists()) {
            throw new ExportaArquivosBeneficioControllerException("mensagem.erro.arquivo.configuracao.layout.relatorio.beneficiarios", responsavel);
        }

        DAOFactory daoFactory = DAOFactory.getDAOFactory();
        RelatorioBeneficiariosDAO relatorioBeneficiariosDAO = daoFactory.getRelatorioBeneficiariosDao();

        // Iniciando a geração e criação do arquivo.
        try {
            List<String> arquivos = relatorioBeneficiariosDAO.geraRelatorioBeneficiarios(orgaos, periodo, nomeArquivoFinalTexto, pathRelatorioConcessao, nomeArqConfEntradaAbsoluto, nomeArqConfSaidaAbsoluto, nomeArqConfTradutorAbsoluto, dataAtual, responsavel);
            FileHelper.zip(arquivos, pathRelatorioConcessao + File.separatorChar + nomeArquivoFinalZip);

            return pathRelatorioConcessao + File.separatorChar + nomeArquivoFinalZip;
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getCause(), e);
            throw new ExportaArquivosBeneficioControllerException(e);
        }
    }

    /**
     * Gera os relatorios de Concessões de Beneficios.
     * @param orgaos
     * @param periodo
     * @param dataAtual
     * @param responsavel
     * @return
     * @throws ExportaArquivosBeneficioControllerException
     */
    private String geraRelatorioConcessoesDeBeneficios(List<String> orgaos, Date periodo, Calendar dataAtual, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException {
        // Diretório raiz de arquivos eConsig
        String absolutePath = ParamSist.getDiretorioRaizArquivos();

        String pathConf = absolutePath + File.separatorChar + "conf";
        String pathRelatorioConcessao = absolutePath + File.separatorChar + "relatorio" + File.separatorChar + "cse" + File.separatorChar + "concessao";

        // Valindando se o dirietorio final existe
        LOG.info("Analisando se o direito : " + pathRelatorioConcessao + " existe.");
        File filePathRelatorioConcessao = new File(pathRelatorioConcessao);
        if (!filePathRelatorioConcessao.exists()) {
            // Não existe e vamos tentar criar.
            LOG.info("Diretorio não existe, vamos tentar criar o mesmo.");
            filePathRelatorioConcessao.mkdirs();
        }

        // Arquivos de configuração para processamento do retorno
        // Inicialmente vamos fixar o nome
        String nomeArqConfEntrada = "relatorio_concessoes_de_beneficios_entrada.xml";
        String nomeArqConfSaida = "relatorio_concessoes_de_beneficios_saida.xml";
        String nomeArqConfTradutor = "relatorio_concessoes_de_beneficios_tradutor.xml";

        // Criando o caminho absoluto dele.
        String nomeArqConfEntradaAbsoluto = pathConf + File.separatorChar + nomeArqConfEntrada;
        String nomeArqConfSaidaAbsoluto = pathConf + File.separatorChar + nomeArqConfSaida;
        String nomeArqConfTradutorAbsoluto = pathConf + File.separatorChar + nomeArqConfTradutor;

        LOG.info("Arquivo de configuração de entrada: " + nomeArqConfEntradaAbsoluto);
        LOG.info("Arquivo de configuração de saida: " + nomeArqConfSaidaAbsoluto);
        LOG.info("Arquivo de configuração de tradutor: " + nomeArqConfTradutorAbsoluto);

        // Pegando os nomes dos arquivos como devem ser
        String nomeArquivoFinalTexto = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.concessoes.de.beneficios", responsavel);
        String nomeArquivoFinalZip = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.concessoes.de.beneficios.zip", responsavel);

        // Variavel usada para calcular o nome do arquivo final
        String mmaaaa = DateHelper.format(new Date(System.currentTimeMillis()), "MMyyyy");
        nomeArquivoFinalZip = nomeArquivoFinalZip.replaceAll("<<MMAAAA>>", mmaaaa);
        nomeArquivoFinalZip = nomeArquivoFinalZip.replaceAll("<<NOME_SISTEMA>>", JspHelper.getNomeSistema(responsavel).replaceAll(" ", "_"));

        // Analisando se os arquivos de configuração existem
        if (!new File(nomeArqConfEntradaAbsoluto).exists() || !new File(nomeArqConfSaidaAbsoluto).exists() || !new File(nomeArqConfTradutorAbsoluto).exists()) {
            throw new ExportaArquivosBeneficioControllerException("mensagem.erro.arquivo.configuracao.layout.relatorio.concessoes.de.beneficios", responsavel);
        }

        DAOFactory daoFactory = DAOFactory.getDAOFactory();
        RelatorioConcessoesDeBeneficiosDAO relatorioConcessoesDeBeneficiosDAO = daoFactory.getRelatorioConcessoesDeBeneficiosDAO();

        boolean reenviaConceCadastroReativacao = ParamSist.paramEquals(CodedValues.TPC_REENVIA_BENEFICIO_CONC_CADASTRO_REATIVADO, CodedValues.TPC_SIM, responsavel);
        try {
            List<String> arquivos = relatorioConcessoesDeBeneficiosDAO.geraRelatorioConcessoesDeBeneficios(orgaos, periodo, nomeArquivoFinalTexto, pathRelatorioConcessao, nomeArqConfEntradaAbsoluto, nomeArqConfSaidaAbsoluto, nomeArqConfTradutorAbsoluto, dataAtual,reenviaConceCadastroReativacao, responsavel);
            FileHelper.zip(arquivos, pathRelatorioConcessao + File.separatorChar + nomeArquivoFinalZip);

            return pathRelatorioConcessao + File.separatorChar + nomeArquivoFinalZip;
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getCause(), e);
            throw new ExportaArquivosBeneficioControllerException(e);
        }
    }

    // Fluxo abaixo relacionada a exportação de arquivos de operadora.

    /**
     * Metodo que analisar o fluxo para exportar arquivos de integração com operadora
     * @param reexporta
     * @param dataInicioIntegracaoOperadoraInformada
     * @param tipoOperacaoArquivoOperadora
     * @param csaCodigo
     * @param rseCodigo
     * @param orgCodigo
     * @param estCodigo
     * @param responsavel
     * @throws ExportaArquivosBeneficioControllerException
     */
    @Override
    public void exportaArquivosOperadoras(boolean reexporta, Date dataInicioIntegracaoOperadoraInformada, List<String> tipoOperacaoArquivoOperadora, List<String> csaCodigo,
            List<String> rseCodigo, List<String> orgCodigo, List<String> estCodigo, AcessoSistema responsavel) throws ExportaArquivosBeneficioControllerException {
        try {
            LOG.info("Inicio da exportação dos arquivos de operadora.");
            // Calculando a data atual que vai servir de controle para geração dos arquivos.
            Date dataAtual = new Date();
            Date dataPassado = null;

            ParamSist paramSist = ParamSist.getInstance();

            // Diretório raiz de arquivos eConsig
            String absolutePath = ParamSist.getDiretorioRaizArquivos();

            // Diretorio de configuralção
            String pathConfCse = absolutePath + File.separatorChar + "conf" + File.separatorChar + "integracaobeneficio" + File.separatorChar + "cse";

            // Arquivos de configuração para processamento
            String nomeArqConfEntrada = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_ENTRADA_EXP_OPERADORA_BENEFICIO, responsavel);
            String nomeArqConfSaida = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_SAIDA_EXP_OPERADORA_BENEFICIO, responsavel);
            String nomeArqConfTradutor = (String) paramSist.getParam(CodedValues.TPC_ARQ_CONF_TRADUTOR_EXP_OPERADORA_BENEFICIO, responsavel);

            // Criando o caminho absoluto dos arquivos de configuração do xml.
            String nomeArqConfEntradaAbsolutoCse = pathConfCse + File.separatorChar + nomeArqConfEntrada;
            String nomeArqConfSaidaAbsolutoCse = pathConfCse + File.separatorChar + nomeArqConfSaida;
            String nomeArqConfTradutorAbsolutoCse = pathConfCse + File.separatorChar + nomeArqConfTradutor;

            // Gerando templates para exportação dos arquivos
            String templatePathIntegracaoBeneficio = absolutePath + File.separatorChar + "integracaobeneficio" + File.separatorChar + "csa" + File.separatorChar + "<CSA_CODIGO>";
            String templatePathConf = absolutePath + File.separatorChar + "conf" + File.separatorChar + "integracaobeneficio" + File.separatorChar + "csa" + File.separatorChar + "<CSA_CODIGO>";

            DAOFactory daoFactory = DAOFactory.getDAOFactory();
            ExportaArquivoOperadoraDAO exportaArquivoOperadoraDAO = daoFactory.getExportaArquivoOperadoraDAO();
            // Contem a lista de arquiv os zip
            List<String> arquivosExportadosZip = new ArrayList<>();

            for (String consignatariaCodigo : csaCodigo) {
                LOG.info("Gerando o arquivo para a CSA_CODIGO: " + consignatariaCodigo);
                LOG.info("É uma reexportação?: " + reexporta);

                Consignataria consignataria = ConsignatariaHome.findByPrimaryKey(consignatariaCodigo);
                if (consignataria == null) {
                    throw new ExportaArquivosBeneficioControllerException("mensagem.erro.csa.codigo.informada.invalido", responsavel);
                }

                // Se usuario informou uma data vamos usar ela, caso não tenha informado tentamos recuperar a data no banco de dados.
                if (dataInicioIntegracaoOperadoraInformada != null) {
                    LOG.info("Usando a data de inicio informada pelo usuario");
                    dataPassado = dataInicioIntegracaoOperadoraInformada;
                } else {
                    LOG.info("Calculando a ultima data de exportação para usarmos a mesmo como data inicio.");
                    dataPassado = obtemUltimoDataHistoricoIntegracaoExportacao(consignatariaCodigo);
                }

                // Se a geração tem necessidade de gerar linhas de alteração tem que existir alguma data do passado ou informada pelo usuario
                if (dataPassado == null && tipoOperacaoArquivoOperadora.contains("A")) {
                    throw new ExportaArquivosBeneficioControllerException("mensagem.erro.nao.existe.data.fim.de.integracao.historico.beneficio.exportacao", responsavel);
                }

                // Se estamos reexportando o usuario é obrigado informar uma data.
                if (reexporta && dataInicioIntegracaoOperadoraInformada == null) {
                    throw new ExportaArquivosBeneficioControllerException("mensagem.erro.nao.existe.data.fim.de.integracao.historico.beneficio.reexportacao", responsavel);
                }

                // Para evitar problemas abaixo estamos calculando a maior e menor data e formatando a mesma.
                List<Date> datas = new ArrayList<>();
                datas.add(dataPassado);
                datas.add(dataAtual);

                dataAtual = datas.stream().max(Date::compareTo).get();
                dataPassado = datas.stream().min(Date::compareTo).get();

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dataFormatadaMin = simpleDateFormat.format(dataPassado);
                String dataFormatadaMax = simpleDateFormat.format(dataAtual);

                LOG.info("Data usadas para a exportação será entre : " + dataFormatadaMin + " e: " + dataFormatadaMax);

                // HashMap que contem XML encontrados e nome do arquivo final, facilitando assim manutenção dos no DAO.
                Map<String, String> configuracao = new HashMap<>();

                // Calculando os PATH
                String pathIntegracaoBeneficio = templatePathIntegracaoBeneficio.replace("<CSA_CODIGO>", consignatariaCodigo);
                String pathConfCsa = templatePathConf.replace("<CSA_CODIGO>", consignatariaCodigo);

                // Criando o caminho absoluto dos arquivos de configuração do xml CSA.
                String nomeArqConfEntradaAbsolutoCsa = pathConfCsa + File.separatorChar + nomeArqConfEntrada;
                String nomeArqConfSaidaAbsolutoCsa = pathConfCsa + File.separatorChar + nomeArqConfSaida;
                String nomeArqConfTradutorAbsolutoCsa = pathConfCsa + File.separatorChar + nomeArqConfTradutor;

                // Analise se existe arquivos de XML personalizado para a CSA que estamos executando.
                if (!new File(nomeArqConfEntradaAbsolutoCsa).exists() || !new File(nomeArqConfSaidaAbsolutoCsa).exists() || !new File(nomeArqConfTradutorAbsolutoCsa).exists()) {
                    // Caso não exista vamos analisar se existe o XML da CSE
                    if (!new File(nomeArqConfEntradaAbsolutoCse).exists() || !new File(nomeArqConfSaidaAbsolutoCse).exists() || !new File(nomeArqConfTradutorAbsolutoCse).exists()) {
                        throw new ExportaArquivosBeneficioControllerException("mensagem.erro.arquivo.configuracao.layout.relatorio.concessoes.de.beneficios", responsavel);
                    } else {
                        configuracao.put("nomeArqConfEntrada", nomeArqConfEntradaAbsolutoCse);
                        configuracao.put("nomeArqConfSaida", nomeArqConfSaidaAbsolutoCse);
                        configuracao.put("nomeArqConfTradutor", nomeArqConfTradutorAbsolutoCse);
                    }
                } else {
                    configuracao.put("nomeArqConfEntrada", nomeArqConfEntradaAbsolutoCsa);
                    configuracao.put("nomeArqConfSaida", nomeArqConfSaidaAbsolutoCsa);
                    configuracao.put("nomeArqConfTradutor", nomeArqConfTradutorAbsolutoCsa);
                }

                LOG.info("Arquivo de configuração de entrada: " + configuracao.get("nomeArqConfEntrada"));
                LOG.info("Arquivo de configuração de saida: " + configuracao.get("nomeArqConfSaida"));
                LOG.info("Arquivo de configuração de tradutor: " + configuracao.get("nomeArqConfTradutor"));

                // Valindando se o dirietorio final existe
                LOG.info("Analisando se o direito : " + pathIntegracaoBeneficio + " existe.");
                File filePathIntegracaoBeneficio = new File(pathIntegracaoBeneficio);
                if (!filePathIntegracaoBeneficio.exists()) {
                    // Não existe e vamos tentar criar.
                    LOG.info("Diretorio não existe, vamos tentar criar o mesmo.");
                    filePathIntegracaoBeneficio.mkdirs();
                }

                // Formatando os nomes.
                String nomeArquivoFinal = ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.exportacao.operadora.beneficio", responsavel);
                simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                nomeArquivoFinal = nomeArquivoFinal.replaceAll("<<DATA>>", simpleDateFormat.format(dataAtual));
                nomeArquivoFinal = nomeArquivoFinal.replaceAll("<<CSA_IDENTIFICADOR>>", consignataria.getCsaIdentificador());
                String nomeArquivoFinalTxt =  nomeArquivoFinal + ".txt";
                String nomeArquivoFinalZip =  nomeArquivoFinal + ".zip";

                // Salvando no HashMap o path final de escrita do arquivo
                configuracao.put("nomeArquivoFinal", pathIntegracaoBeneficio + File.separatorChar + nomeArquivoFinalTxt);

                String arquivoZip = pathIntegracaoBeneficio + File.separatorChar + nomeArquivoFinalZip;
                boolean permiteCancelarBeneficioSemAprovacao = ParamSist.paramEquals(CodedValues.TPC_PERMITE_CANCELAR_BENEFICIO_SEM_APROVACAO, CodedValues.TPC_SIM, responsavel);

                exportaArquivoOperadoraDAO.exportaArquivoOperadora(reexporta, dataFormatadaMin, dataFormatadaMax, tipoOperacaoArquivoOperadora,
                        consignatariaCodigo, rseCodigo, orgCodigo, estCodigo, configuracao, contratoBeneficioController,permiteCancelarBeneficioSemAprovacao, responsavel);

                FileHelper.zip(pathIntegracaoBeneficio + File.separatorChar + nomeArquivoFinalTxt, arquivoZip);
                arquivosExportadosZip.add(arquivoZip);

                // Gera o historico de integração
                LOG.info("Inicio da geralão do historico de integração com operadora.");
                geraHistoricoIntegracaoOperadora(reexporta, consignatariaCodigo, dataPassado, dataAtual, responsavel);
                LOG.info("Fim da geralão do historico de integração com operadora.");
            }

            // Enviando email.
            LOG.info("Inicio do envio do email.");
            EnviaEmailHelper.enviaEmailExportacaoArquivosDeIntegracaoOperadora(arquivosExportadosZip, responsavel);
            LOG.info("Fim do envio do email.");

            LOG.info("Fim da exportação dos arquivos de operadora.");
        } catch (Exception e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getCause(), e);
            throw new ExportaArquivosBeneficioControllerException(e);
        }
    }

    /**
     * Metodo que realiza a gravação do historico de integração.
     * @param reexporta
     * @param csaCodigo
     * @param hibDataIni
     * @param hibDataFim
     * @param responsavel
     * @throws ExportaArquivosBeneficioControllerException
     * @throws CreateException
     */
    private void geraHistoricoIntegracaoOperadora(boolean reexporta, String csaCodigo, Date hibDataIni, Date hibDataFim, AcessoSistema responsavel)
            throws ExportaArquivosBeneficioControllerException, CreateException {
        String usuCodigo = (responsavel != null ? responsavel.getUsuCodigo() : null);

        char hibTipo = CodedValues.HIB_TIPO_EXPORTACAO;
        if (reexporta) {
            hibTipo = CodedValues.HIB_TIPO_REEXPORTACAO;
        }

        HistoricoIntegracaoBeneficioHome.create(csaCodigo, usuCodigo, hibDataIni, hibDataFim, new Date(), String.valueOf(hibTipo));
    }

    /**
     * Metodo que obtem a maior data fim do historico de integração para uma determinada CSA
     * @return
     * @throws FindException
     */
    private Date obtemUltimoDataHistoricoIntegracaoExportacao(String csaCodigo) throws FindException {
        HistIntegracaoBeneficio histIntegracaoBeneficio = HistoricoIntegracaoBeneficioHome.obtemUltimoDataHistoricoIntegracaoExportacao(csaCodigo);

        if (histIntegracaoBeneficio != null) {
            Calendar calendar = Calendar.getInstance();
            Date hibDataFim = histIntegracaoBeneficio.getHibDataFim();
            calendar.setTime(hibDataFim);
            calendar.add(Calendar.SECOND, 1);
            return calendar.getTime();
        } else {
            return null;
        }
    }
}
