package com.zetra.econsig.helper.beneficios;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ExportaArquivosBeneficioControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.helper.rotinas.RotinaExternaViaProxy;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.beneficios.ExportaArquivosBeneficioController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ExportaArquivosBeneficio</p>
 * <p>Description: Classe util para fazer exportação de arquivos do modulo de beneficio</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ExportaArquivosBeneficio implements RotinaExternaViaProxy {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ExportaArquivosBeneficio.class);
    private static final String NOME_CLASSE = ExportaArquivosBeneficio.class.getName();

    @Override
    public int executar(String args[]) {
        int status = 0;
        List<String> lstArgs = Arrays.asList(args);
        List<String> estCodigo = new ArrayList<>();
        List<String> orgCodigo = new ArrayList<>();
        List<String> csaCodigo = new ArrayList<>();
        List<String> rseCodigo = new ArrayList<>();

        List<String> rseMatricula = new ArrayList<>();
        List<String> orgIdentificador = new ArrayList<>();
        List<String> estIdentificador = new ArrayList<>();

        String arquivoRetorno = null;

        List<String> tipoOperacaoArquivoOperadora = new ArrayList<String>() {{
           add("I");
           add("E");
           add("A");
        }};

        Date periodo = null;
        Date periodoDataInicio = null;
        Date periodoDataFim = null;
        Date dataIntegracaoOperadora = null;
        int controlePeriodoInicioFim = 0;

        boolean exportaRelatorioConcessaoBeneficiario = false;

        boolean exportaArquivoOperadora = false;
        boolean reexportaArquivoOperadora = false;

        try {
            ExportaArquivosBeneficioController exportaArquivosBeneficioController = ApplicationContextProvider.getApplicationContext().getBean(ExportaArquivosBeneficioController.class);
            AcessoSistema responsavel = new AcessoSistema(CodedValues.USU_CODIGO_SISTEMA);

            // Mostra a ajuda dessa rotina
            if(lstArgs.contains("-h")) {
                ajuda();
            }

            // Analisando que tipo de exportação de arquivo de beneficio estão desejando.
            if (lstArgs.contains("-relatorioConcessaoBeneficiario")) {
                exportaRelatorioConcessaoBeneficiario = true;
            }

            // Analisando se foi informado -org como parametro de execução.
            if ((lstArgs.contains("-org")) ? true : false) {
                String opcoes = getOpcoesParametro("-org", lstArgs, false);
                for (String opcao : opcoes.split("," )) {
                    orgCodigo.add(opcao);
                }
            }

            // Analisando se foi informado -orgIdentificador como parametro de execução.
            if (lstArgs.contains("-orgIdentificador")) {
                String opcoes = getOpcoesParametro("-orgIdentificador", lstArgs, false);
                for (String opcao : opcoes.split("," )) {
                    orgIdentificador.add(opcao);
                }

                // Com base na orgIdentificador informada busco o orgCodigo para não existir alteração no fluxo.
                orgCodigo.addAll(getOrgaosByIdentificador(orgIdentificador, responsavel));
            }

            // Analisando se foi informado -est como parametro de execução.
            if (lstArgs.contains("-est")) {
                String opcoes = getOpcoesParametro("-est", lstArgs, false);
                for (String opcao : opcoes.split("," )) {
                    estCodigo.add(opcao);
                }
            }

            // Analisando se foi informado -estIdentificador como parametro de execução.
            if (lstArgs.contains("-estIdentificador")) {
                String opcoes = getOpcoesParametro("-estIdentificador", lstArgs, false);
                for (String opcao : opcoes.split("," )) {
                    estIdentificador.add(opcao);
                }

                estCodigo.addAll(getEstCodigoByIdentificador(estIdentificador, responsavel));
            }

            // Analisando se foi informado -rse como parametro de execução.
            if (lstArgs.contains("-csa")) {
                String opcoes = getOpcoesParametro("-csa", lstArgs, false);
                for (String opcao : opcoes.split("," )) {
                    csaCodigo.add(opcao);
                }
            }

            // Analisando se foi informado -csa como parametro de execução.
            if (lstArgs.contains("-rse")) {
                String opcoes = getOpcoesParametro("-rse", lstArgs, false);
                for (String opcao : opcoes.split("," )) {
                    rseCodigo.add(opcao);
                }
            }

            // Analisando se foi informado -csa como parametro de execução.
            if (lstArgs.contains("-rseMatricula")) {
                String opcoes = getOpcoesParametro("-rseMatricula", lstArgs, false);
                for (String opcao : opcoes.split("," )) {
                    rseMatricula.add(opcao);
                }

                // Com base na matricula recuperamos o rseCodigo para não alterar o fluxo
                rseCodigo.addAll(getRseCodigoByMatriculas(rseMatricula, responsavel));
            }

            // Analisando se foi informado -periodo como parametro de execução.
            if ((lstArgs.contains("-periodo")) ? true : false) {
                String opcao = getOpcoesParametro("-periodo", lstArgs, true);
                periodo = new SimpleDateFormat("yyyy-MM-dd").parse(opcao);
            }

            // Analisando se foi informado -periodoDataInicio como parametro de execução.
            if ((lstArgs.contains("-periodoDataInicio")) ? true : false) {
                String opcao = getOpcoesParametro("-periodoDataInicio", lstArgs, true);
                periodoDataInicio = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(opcao);
                controlePeriodoInicioFim++;
            }

            // Analisando se foi informado -periodoDataFim como parametro de execução.
            if ((lstArgs.contains("-periodoDataFim")) ? true : false) {
                String opcao = getOpcoesParametro("-periodoDataFim", lstArgs, true);
                periodoDataFim = new SimpleDateFormat("yyyy-MM-dd H:m:s").parse(opcao);
                controlePeriodoInicioFim++;
            }

            // Analisando se foi informado -arquivoRetorno como parametro de execução.
            if (lstArgs.contains("-arquivoRetorno")) {
                arquivoRetorno = getOpcoesParametro("-arquivoRetorno", lstArgs, false);
            }

            // Analisando se foi informado -tipoOperacaoArquivoOperadora como parametro de execução
            if (lstArgs.contains("-tipoOperacaoArquivoOperadora")) {
                tipoOperacaoArquivoOperadora.clear();
                String opcoes = getOpcoesParametro("-tipoOperacaoArquivoOperadora", lstArgs, false);
                for (String opcao : opcoes.split("," )) {
                    tipoOperacaoArquivoOperadora.add(opcao.toUpperCase());
                }
            }

            // Analisando se foi informado -exportaArquivoOperadora
            if (lstArgs.contains("-exportaArquivoOperadora")) {
                exportaArquivoOperadora = true;
            }

            // Analisando se foi informado -reexportaArquivoOperadora
            if (lstArgs.contains("-reexportaArquivoOperadora")) {
                reexportaArquivoOperadora = true;
            }

            // Analisando se foi informado -dataArquivoOperadora
            if (lstArgs.contains("-dataIntegracaoOperadora")) {
                String opcao = getOpcoesParametro("-dataIntegracaoOperadora", lstArgs, true);
                dataIntegracaoOperadora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(opcao);
            }

            // Analisando se foi informado -arquivoRetorno como parametro de execução.
            if (lstArgs.contains("-relatorioBeneficiosConsolidadosDirf")) {
                if(periodo == null) {
                    throw new ExportaArquivosBeneficioControllerException("mensagem.erro.configuracao.periodo.nao.informado", responsavel);
                }

                if (TextHelper.isNull(arquivoRetorno)) {
                    throw new ExportaArquivosBeneficioControllerException("mensagem.erro.configuracao.nome.arquivo.retorno.nao.informado", responsavel);
                }

                if (orgCodigo != null && !orgCodigo.isEmpty()) {
                    LOG.info("Iniciando a geração do relatorio de Benefícios Consolidados DIRF");
                    for(String orgao: orgCodigo) {
                        exportaArquivosBeneficioController.geraRelatorioBeneficiosConsolidadosDirf(csaCodigo, orgao, arquivoRetorno, periodo, responsavel);
                    }
                    LOG.info("Fim da geração do relatorio de Benefícios Consolidados DIRF");
                } else {
                    //Se não foi especificado órgão na chamada, gera para cada órgão ligado a código de verba dirf
                    ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                    List<OrgaoTransferObject> lstOrgs = cseDelegate.listarOrgaosDirf(responsavel);

                    LOG.info("Iniciando a geração do relatorio de Benefícios Consolidados DIRF");
                    for (OrgaoTransferObject org: lstOrgs) {
                        exportaArquivosBeneficioController.geraRelatorioBeneficiosConsolidadosDirf(csaCodigo, org.getOrgCodigo(), arquivoRetorno, periodo, responsavel);
                    }
                    LOG.info("Fim da geração do relatorio de Benefícios Consolidados DIRF");
                }
            } else if (exportaRelatorioConcessaoBeneficiario) {
                if (periodo == null || controlePeriodoInicioFim == 1) {
                    throw new ExportaArquivosBeneficioControllerException("mensagem.erro.combinacao.parametros.invalida", responsavel);
                }

                // Se chegou aqui vazio carregamos todos os orgao do sistema.
                if (orgCodigo.isEmpty()) {
                    orgCodigo = getOrgaos(null);
                }

                LOG.info("Iniciando a geração do relatorio de Beneficiário e Concessões de Benefícios");
                exportaArquivosBeneficioController.geraRelatorioBeneficiariosEConcessoesDeBeneficios(orgCodigo, periodo, periodoDataInicio, periodoDataFim,  responsavel);
                LOG.info("Fim da geração do relatorio de Beneficiário e Concessões de Benefícios");
            } else if (exportaArquivoOperadora || reexportaArquivoOperadora) {
                if (tipoOperacaoArquivoOperadora.size() != 3 && !reexportaArquivoOperadora) {
                    throw new ExportaArquivosBeneficioControllerException("mensagem.erro.combinacao.parametros.invalida", responsavel);
                }

                if (csaCodigo.isEmpty()) {
                    csaCodigo = getCsaCodigo(responsavel);
                }

                LOG.info("Iniciando a exportação dos arquivos de contrato beneficio.");
                exportaArquivosBeneficioController.exportaArquivosOperadoras(reexportaArquivoOperadora, dataIntegracaoOperadora, tipoOperacaoArquivoOperadora, csaCodigo, rseCodigo, orgCodigo, estCodigo, responsavel);
                LOG.info("Fim da exportação dos arquivos de contrato beneficio.");
            } else {
                throw new ExportaArquivosBeneficioControllerException("mensagem.erro.combinacao.parametros.invalida", responsavel);
            }
        } catch (Exception e) {
            LOG.error(e.getCause(), e);
            ajuda();
            status = -1;
        }

        return status;
    }

    /**
     * Se não for informado o orgao pega a lista de orgão cadastrado no sistema
     * @param responsavel
     * @return
     * @throws ConsignanteControllerException
     */
    private static List<String> getOrgaos(AcessoSistema responsavel) throws ConsignanteControllerException {
        ConsignanteDelegate delegate = new ConsignanteDelegate();
        List<TransferObject> orgaos = delegate.lstOrgaos(null, responsavel);
        Iterator<TransferObject> it = orgaos.iterator();
        List<String> codigos = new ArrayList<>();

        Map<String, String> mapAjuda = new TreeMap<>();

        while (it.hasNext()) {
            TransferObject orgao = it.next();
            mapAjuda.put((String) orgao.getAttribute(Columns.ORG_IDENTIFICADOR_BENEFICIO), (String) orgao.getAttribute(Columns.ORG_CODIGO));
        }

        for (Map.Entry<String, String> entry : mapAjuda.entrySet()) {
            codigos.add(entry.getValue());
        }

        return codigos;
    }

    /**
     * Retornar orgCodigo com base no orgIdentificador informado
     * @param responsavel
     * @return
     * @throws ConsignanteControllerException
     */
    private static List<String> getOrgaosByIdentificador(List<String> orgIdentificador, AcessoSistema responsavel) throws ConsignanteControllerException {
        ConsignanteDelegate delegate = new ConsignanteDelegate();
        List<TransferObject> orgaos = delegate.lstOrgaos(null, responsavel);
        Iterator<TransferObject> it = orgaos.iterator();
        List<String> codigos = new ArrayList<>();

        Collections.sort(orgIdentificador);

        Map<String, String> mapAjuda = new TreeMap<>();

        while (it.hasNext()) {
            TransferObject orgao = it.next();
            mapAjuda.put((String) orgao.getAttribute(Columns.ORG_IDENTIFICADOR), (String) orgao.getAttribute(Columns.ORG_CODIGO));
        }

        for (Map.Entry<String, String> entry : mapAjuda.entrySet()) {
            if (orgIdentificador.contains(entry.getKey())) {
                codigos.add(entry.getValue());
            }
        }

        return codigos;
    }

    /**
     * Retornar rseCodigo com base nas matriculas informadas
     * @param rseMatricula
     * @param responsavel
     * @return
     * @throws ServidorControllerException
     */
    private static List<String> getRseCodigoByMatriculas(List<String> rseMatricula, AcessoSistema responsavel) throws ServidorControllerException {
        ServidorDelegate servidorDelegate = new ServidorDelegate();
        List<TransferObject> servidores = servidorDelegate.findRegistroServidoresByMatriculas(rseMatricula, responsavel);
        Iterator<TransferObject> it = servidores.iterator();
        List<String> codigos = new ArrayList<>();
        while (it.hasNext()) {
            TransferObject transferObject = it.next();
            String matricula = (String) transferObject.getAttribute(Columns.RSE_MATRICULA);
            String codigo = (String) transferObject.getAttribute(Columns.RSE_CODIGO);
            if (rseMatricula.contains(matricula)) {
                codigos.add(codigo);
            }

        }
        return codigos;
    }

    /**
     *
     * @param estIdentificadores
     * @param responsavel
     * @return
     * @throws ConsignanteControllerException
     */
    private static List<String> getEstCodigoByIdentificador(List<String> estIdentificadores, AcessoSistema responsavel) throws ConsignanteControllerException {
        ConsignanteDelegate delegate = new ConsignanteDelegate();
        List<TransferObject> estabelecimentos = delegate.lstEstabelecimentos(null, responsavel);
        Iterator<TransferObject> it = estabelecimentos.iterator();
        List<String> codigos = new ArrayList<>();
        while (it.hasNext()) {
            TransferObject transferObject = it.next();
            String identificador = (String) transferObject.getAttribute(Columns.EST_IDENTIFICADOR);
            String codigo = (String) transferObject.getAttribute(Columns.EST_CODIGO);
            if (estIdentificadores.contains(identificador)) {
                codigos.add(codigo);
            }

        }
        return codigos;
    }

    /**
     * Busca todas as CSA do tipo de operado de beneficios.
     * @param responsavel
     * @return
     * @throws ConsignatariaControllerException
     */
    private static List<String> getCsaCodigo(AcessoSistema responsavel) throws ConsignatariaControllerException {
        ConsignatariaDelegate consignatariaDelegate = new ConsignatariaDelegate();
        ConsignatariaTransferObject consignatariaTransferObject = new ConsignatariaTransferObject();
        consignatariaTransferObject.setCsaNcaNatureza(CodedValues.NCA_CODIGO_OPERADORA_BENEFICIOS);
        List<TransferObject> consignatarias = consignatariaDelegate.lstConsignatarias(consignatariaTransferObject, responsavel);
        Iterator<TransferObject> it = consignatarias.iterator();
        List<String> codigos = new ArrayList<>();
        while(it.hasNext()) {
            TransferObject transferObject = it.next();
            String codigo = (String) transferObject.getAttribute(Columns.CSA_CODIGO);
            codigos.add(codigo);
        }

        return codigos;
    }

    /**
     * Realiza parse dos parametro informado.
     * @param parametro
     * @param argumentosEntrada
     * @param esperaData
     * @return
     */
    private static String getOpcoesParametro(String parametro, List<String> argumentosEntrada, boolean esperaData) {
        int lastPos = argumentosEntrada.lastIndexOf(parametro);
        List<String> tmp = argumentosEntrada.subList(lastPos + 1, argumentosEntrada.size());

        StringBuilder volta = new StringBuilder();

        for (String s : tmp) {
            if (s.startsWith("-")) {
                break;
            } else {
                if (!esperaData) {
                    volta.append(s);
                } else {
                    volta.append(s);
                    volta.append(" ");
                }
            }
        }

        return volta.toString().trim();
    }

    public static void ajuda() {
        StringBuilder ajuda = new StringBuilder();

        ajuda.append(System.lineSeparator());
        ajuda.append(NOME_CLASSE).append(": ");
        ajuda.append(System.lineSeparator());
        ajuda.append("-h : Exibe essa ajuda");
        ajuda.append(System.lineSeparator());
        ajuda.append("-relatorioConcessaoBeneficiario: Gera o relatorio de concessão e beneficario.");
        ajuda.append(System.lineSeparator());
        ajuda.append("-exportaArquivoOperadora : Exporta os arquivos de integração com operadora.");
        ajuda.append(System.lineSeparator());
        ajuda.append("-reexportaArquivoOperadora : Reexporta os arquivos de integração com operadora.");
        ajuda.append(System.lineSeparator());
        ajuda.append("-org : Informa org_codigo separado por virgula");
        ajuda.append(System.lineSeparator());
        ajuda.append("-orgIdentificador : Informa org_identificador separado por virgula");
        ajuda.append(System.lineSeparator());
        ajuda.append("-est : Informa est_codigo separado por virgula");
        ajuda.append(System.lineSeparator());
        ajuda.append("-estIdentificador : Informa est_identificador separado por virgula");
        ajuda.append(System.lineSeparator());
        ajuda.append("-csa : Informa csa_codigo separado por virgula");
        ajuda.append(System.lineSeparator());
        ajuda.append("-rse : Informa rse_codigo separado por virgula");
        ajuda.append(System.lineSeparator());
        ajuda.append("-rseMatricula : Informa rse_matricula separado por virgula");
        ajuda.append(System.lineSeparator());
        ajuda.append("-periodo : Informa o período, formato yyyy-MM-dd (obrigatório ao informar o -relatorioConcessaoBeneficiario)");
        ajuda.append(System.lineSeparator());
        ajuda.append("-periodoDataInicio : Informa a data inicial do período, necessita informar o -periodo, formato yyyy-MM-dd HH:mm:ss (opcional ao informar o -relatorioConcessaoBeneficiario, mas obrigatorio ao informar -periodoDataFim)");
        ajuda.append(System.lineSeparator());
        ajuda.append("-periodoDataFim : Informa a data final do período, necessita informar o -periodo, formato yyyy-MM-dd HH:mm:ss (opcional ao informar o -relatorioConcessaoBeneficiario, mas obrigatorio ao informar -periodoDataInicio)");
        ajuda.append(System.lineSeparator());
        ajuda.append("-dataIntegracaoOperadora: Informa a data inicio para exportação dos arquivos de operadora beneficios, formato yyyy-MM-dd HH:mm:ss (Obrigatorio quanto informado o -reexportaArquivoOperadora)");
        ajuda.append(System.lineSeparator());
        ajuda.append("-tipoOperacaoArquivoOperadora: Informa tipos de operação que será gerada ao exporta arquivos de operado beneficios (I, A e E)");
        ajuda.append(System.lineSeparator());
        ajuda.append("-arquivoRetorno : Nome do arquivo de retorno");
        ajuda.append(System.lineSeparator());
        ajuda.append("-relatorioBeneficiosConsolidadosDirf: Gera o relatorio de benefícios consolidados DIRF (informações de período e nome de arquivo de retorno obrigatórios. Órgão e Operadoras opcional).");

        LOG.info(ajuda);
    }

}
