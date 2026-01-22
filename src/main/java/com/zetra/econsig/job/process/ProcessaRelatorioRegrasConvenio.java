package com.zetra.econsig.job.process;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioParametrosBean;
import com.zetra.econsig.report.reports.ReportManager;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.relatorio.RelatorioController;
import com.zetra.econsig.service.relatorio.ReportController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

import jakarta.servlet.http.HttpSession;

/**
 * <p>Title: ProcessaRelatorioRegrasConvenio</p>
 * <p>Description: Classe para processamento do relatório de regras de convênio</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaRelatorioRegrasConvenio extends ProcessaRelatorio {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaRelatorioRegrasConvenio.class);

    public ProcessaRelatorioRegrasConvenio(Relatorio relatorio, Map<String, String[]> parameterMap, HttpSession session, Boolean agendado, AcessoSistema responsavel) {
        super(relatorio, parameterMap, session, agendado, responsavel);

        // Seta o proprietário do processo
        owner = responsavel.getUsuCodigo();

        if(relatorio != null) {
	        // Seta a descrição do processo
	        setDescricao(relatorio.getTitulo());
        }
    }

    @Override
    protected void executar() {
        final HashMap<String, Object> parameters = new HashMap<>();

        final String titulo = relatorio.getTitulo() + " - " + getCseNome(responsavel);

        final StringBuilder subTitulo = new StringBuilder(ApplicationResourcesHelper.getMessage("rotulo.relatorio.parametros", responsavel));
        subTitulo.append(":");

        final String diretorioSubReport = getPath(responsavel) + ReportManager.JASPER_DIRECTORY;

        final StringBuilder nome = new StringBuilder((getNomeArquivo(ApplicationResourcesHelper.getMessage("rotulo.nome.arquivo.relatorio.regras.convenio", responsavel), responsavel, parameterMap, null)));

        // Consignatária - setar subtítulo
        final String csaCodigo = getFiltroCsaCodigo(parameterMap, subTitulo, nome, session, responsavel);

        // Órgão - setar subtítulo
        getFiltroOrgCodigo(parameterMap, subTitulo, nome, session, responsavel);

        String reportName = null;
        try {
            final RelatorioController relatorioController = ApplicationContextProvider.getApplicationContext().getBean(RelatorioController.class);
            final String strFormato = getStrFormato();

            parameters.put(ReportManager.PARAM_NAME_CAMINHO_LOGO, getCaminhoLogoCse(responsavel));
            parameters.put(ReportManager.PARAM_NAME_CRITERIO, criterio);
            parameters.put(ReportManager.PARAM_NAME_TEXTO_RODAPE, getTextoRodape(session, responsavel));
            parameters.put(ReportManager.REPORT_FILE_NAME, nome.toString());
            parameters.put(ReportManager.PARAM_NAME_TITULO, titulo);
            parameters.put(ReportManager.PARAM_NAME_SUBTITULO, subTitulo.toString());
            parameters.put(ReportManager.PARAM_SUBREPORT_DIR, diretorioSubReport);
            parameters.put(ReportManager.PARAM_NAME_FORMATO_ARQUIVO, strFormato);
            parameters.put("RESPONSAVEL", responsavel);
            parameters.put("PARAMETROS", getListParametros());
            parameters.put("SERVICOS", relatorioController.listaServicosRegrasConvenio(csaCodigo, responsavel));
            parameters.put("ORGAOSSER", relatorioController.listaOrgaosSerRegrasConvenio(responsavel));
            parameters.put("MARGENS", relatorioController.lstMargensRegrasConvenio(responsavel));
            
            final ReportController reportController = ApplicationContextProvider.getApplicationContext().getBean(ReportController.class);

            reportName = reportController.makeReport(strFormato, criterio, parameters, relatorio, responsavel);

            final String reportNameZip = geraZip(nome.toString(), reportName);

            // Envia e-mail, caso seja relatório agendado pelo usuário e tenha sido informado destinatário
            enviaEmail(reportNameZip);

        } catch (final Exception ex) {
            codigoRetorno = ERRO;
            mensagem = ApplicationResourcesHelper.getMessage("mensagem.log.erro.geracao.relatorio", responsavel) + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.erro.generico.relatorio.nao.foi.possivel.completar.operacao.arg0", responsavel, DateHelper.format(DateHelper.getSystemDatetime(), "HH:mm:ss").toString());
            LOG.error(mensagem, ex);
        }
    }

    public List<RegrasConvenioParametrosBean> getListParametros() throws ZetraException {
        final List<RegrasConvenioParametrosBean> retorno = new ArrayList<>();

        final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
        final ServidorController servidorController = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);

        final String rotuloSim = ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel);
        final String rotuloNao = ApplicationResourcesHelper.getMessage("rotulo.nao", responsavel);
        final String rotuloNadaEncontrado = ApplicationResourcesHelper.getMessage("rotulo.campo.nao.definido.abreviado", responsavel);

        /**
         * 3.4) Informações sobre a configuração do sistema que devem ser exibidas no relatório:
            item                                                            onde obter a informação
            Link sistema                                                    Sistema (256) - Link de acesso ao sistema
            Data Corte                                                      Data de corte definida no calendário folha para a CSE ou para o ORG (caso usuário autenticado for ORG) no período que o relatório foi gerado
            Tipo de movimento financeiro                                    Se Sistema (028) - Exportação Somente Inicial de Autorização for S, exibir "Inicial". Caso contrário, exibir "Mensal"
            Tipo de margem                                                  Se Sistema (023) - Folha envia somente valor da margem disponível for S, exibir "Vazia". Caso contrário, exibir "Cheia"
            Permite compra de contratos                                     Sistema (254) - Permite compra de contratos
            Exigência de Certificado Digital para consignatária             Sistema (264) - Exige certificado digital para consignatária
            Exigência de Certificado Digital para consignante               Sistema (266) - Exige certificado digital para consignante
            Portal do Servidor                                              Sistema (420) - Possui portal do servidor
            Exige senha do servidor ao consultar margem pela consignatária  Sistema (105) - Exige senha do servidor ao consultar margem pela consignatária
            Exige senha do servidor ao consultar margem pelo consignante    Sistema (161) - Exige senha do servidor ao consultar margem pelo consignante
            Formato de acesso do servidor                                   Se "Sistema (029) - Repositório Externo de Senhas" for "S", exibir "Repositório Externo".
                                                                            Caso contrário, exibir conforme "Sistema (362) - Modo de disponibilização das senhas de autorização para o servidor"
            Margens                                                         Listar as margens cadastradas nas configurações de margem da CSE e suas respectivas porcentagens
         */

        final String linkAcessoSistema = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel).toString() : "";
        String diaCorte = "";
        try {
            diaCorte = String.valueOf(PeriodoHelper.getInstance().getProximoDiaCorte(null, responsavel));
        } catch (final PeriodoException e) {
            LOG.error(e.getMessage(), e);
        }
        final boolean exportacaoInicial = ParamSist.getBoolParamSist(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, responsavel);
        final boolean tipoMargem = ParamSist.getBoolParamSist(CodedValues.TPC_ZERA_MARGEM_USADA, responsavel);
        final boolean permiteCompraContrato = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_COMPRA_CONTRATO, responsavel);
        final boolean exigeCertificadoCsaCor = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CERTIFICADO_DIGITAL_CSA_COR, responsavel);
        final boolean exigeCertificadoCseOrg = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CERTIFICADO_DIGITAL_CSE_ORG, responsavel);
        final boolean possuiPortalServidor = ParamSist.getBoolParamSist(CodedValues.TPC_POSSUI_PORTAL_SERVIDOR, responsavel) && ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_CONSIGNACAO, responsavel);
        final boolean exigeSenhaSerConsultaMargemCsa = ParamSist.getBoolParamSist(CodedValues.TPC_SENHA_SER_ACESSAR_CONS_MARGEM, responsavel);
        final boolean exigeSenhaSerConsultaMargemCse = ParamSist.getBoolParamSist(CodedValues.TPC_SENHA_SER_ACESSAR_CONS_MARGEM_CSE, responsavel);
        final boolean exibirRepositorioExternoSenha = ParamSist.getBoolParamSist(CodedValues.TPC_SENHA_EXTERNA, responsavel);
        final String diaPagamentoPrimeiraParcela = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_DIA_PAGTO_PRIMEIRA_PARCELA, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_DIA_PAGTO_PRIMEIRA_PARCELA, responsavel).toString() : "";
        final String modoEntrega = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel)) ? ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_ALTERACAO_SENHA_AUT_SERVIDOR, responsavel).toString() : CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL;
        final boolean temProcessamentoFerias = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, responsavel);
        final boolean permiteReimplantarParcela = ParamSist.getBoolParamSist(CodedValues.TPC_REIMPLANTACAO_AUTOMATICA, responsavel);
        final boolean contratoConcluiPelaDataFim = ParamSist.getBoolParamSist(CodedValues.TPC_PRESERVA_PRD_REJEITADA, responsavel);
        final boolean csaDecideSeDevePreservarParcelas = ParamSist.getBoolParamSist(CodedValues.TPC_CSA_ALTERA_PRESERVA_PRD, responsavel);
        String vlrMinSist = (String) ParamSist.getInstance().getParam(CodedValues.TPC_VLR_PADRAO_MINIMO_CONTRATO, responsavel);
        String valorMinimoSistFormatado = NumberHelper.formata(TextHelper.isNull(vlrMinSist) ? new BigDecimal("0.00").doubleValue() : Double.parseDouble(vlrMinSist), ApplicationResourcesHelper.getMessage("rotulo.moeda.pattern",responsavel));
        final boolean margemCasada = ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_2_3_CASADAS, responsavel) || ParamSist.getBoolParamSist(CodedValues.TPC_MARGEM_1_CASADA_MARGEM_3, responsavel);
        final boolean moduloRescisao = ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_SALDO_DEVEDOR_EXCLUSAO_SERVIDOR, responsavel);
        int qtdeDiasValidadeSdv = 0;
        final String paramQtdeDiasValidadeSdv = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_EXPIRACAO_INF_SALDO_DEVEDOR_RESCISAO, responsavel);
        if (!TextHelper.isNull(paramQtdeDiasValidadeSdv)) {
            qtdeDiasValidadeSdv = Integer.parseInt(paramQtdeDiasValidadeSdv);
        }
        final Integer diasSemAcessoCsa = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSA_SEM_ACESSO, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_BLOQ_USU_CSA_SEM_ACESSO, responsavel).toString()) : 0;
        final boolean restricaoIP = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CADASTRO_IP_CSA_COR, responsavel);
        final boolean exigeCertificadoDigital = ParamSist.getBoolParamSist(CodedValues.TPC_EXIGE_CERTIFICADO_DIGITAL_CSA_COR, responsavel);
        final boolean matriculaSomenteNumerica = ParamSist.getBoolParamSist(CodedValues.TPC_MATRICULA_NUMERICA, responsavel);
        final Integer qtdeMaximaDigitosMatricula = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA_MAX, responsavel)) ? Integer.valueOf(ParamSist.getInstance().getParam(CodedValues.TPC_TAMANHO_MATRICULA_MAX, responsavel).toString()) : 0;

        String mensagemModoEntrega = "";

        // Verifica se a senha pode ser exibida na tela para o usuário, por e-mail ou sms
        if (CodedValues.ALTERACAO_SENHA_AUT_SER_ENVIA_EMAIL.equals(modoEntrega)) {
            mensagemModoEntrega = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servidor.autorizacao.envia.email", responsavel);
        } else if (CodedValues.ALTERACAO_SENHA_AUT_SER_SMS.equals(modoEntrega)) {
            mensagemModoEntrega = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servidor.autorizacao.envia.sms", responsavel);
        } else if (CodedValues.ALTERACAO_SENHA_AUT_SER_EXIBE_TELA.equals(modoEntrega)) {
            mensagemModoEntrega = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servidor.autorizacao.exibe.tela", responsavel);
        } else if (CodedValues.ALTERACAO_SENHA_AUT_SER_SMS_E_EMAIL.equals(modoEntrega)) {
            mensagemModoEntrega = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servidor.autorizacao.envia.email.sms", responsavel);
        } else if (CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_E_TELA.equals(modoEntrega)) {
            mensagemModoEntrega = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servidor.autorizacao.envia.email.tela", responsavel);
        } else if (CodedValues.ALTERACAO_SENHA_AUT_SER_EMAIL_OU_TELA.equals(modoEntrega)) {
            mensagemModoEntrega = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.servidor.autorizacao.envia.email.ou.tela", responsavel);
        } else {
            mensagemModoEntrega = ApplicationResourcesHelper.getMessage("mensagem.usoIncorretoSistema", responsavel);
        }

        final ConsignanteTransferObject consignante = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
        String logradouro = !TextHelper.isNull(consignante.getCseLogradouro()) ? consignante.getCseLogradouro() : rotuloNadaEncontrado;
        String bairro = !TextHelper.isNull(consignante.getCseBairro()) ? consignante.getCseBairro() : rotuloNadaEncontrado;
        String cidade = !TextHelper.isNull(consignante.getCseCidade()) ? consignante.getCseCidade() : rotuloNadaEncontrado;
        String uf = !TextHelper.isNull(consignante.getCseUf()) ? consignante.getCseUf() : rotuloNadaEncontrado;
        String cep = !TextHelper.isNull(consignante.getCseCep()) ? consignante.getCseCep() : rotuloNadaEncontrado;
        String endereco = ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.endereco.dados", responsavel, logradouro, consignante.getCseNro() != null ? consignante.getCseNro().toString() : "", bairro, cidade, uf, cep);
        
        int quantidadeServidores = servidorController.countRegistroServidor(CodedValues.SRS_ATIVOS, null, null, responsavel);
        
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_NOME, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.nome", responsavel), !TextHelper.isNull(consignante.getCseNome()) ? consignante.getCseNome() : rotuloNadaEncontrado));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_CNPJ, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.cnpj", responsavel), !TextHelper.isNull(consignante.getCseCnpj()) ? consignante.getCseCnpj() : rotuloNadaEncontrado));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_ENDERECO, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.endereco", responsavel), endereco));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_TOTAL_SERVIDORES, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.total.servidores", responsavel), String.valueOf(quantidadeServidores)));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_LINK_SISTEMA, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.link.sistema", responsavel), linkAcessoSistema));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_DIA_REPASSE, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.dia.repasse", responsavel), diaPagamentoPrimeiraParcela));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_DATA_CORTE, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.data.corte", responsavel), diaCorte));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_TIPO_MOVIMENTO_FINANCEIRO, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.tipo.movimento.financeiro", responsavel), exportacaoInicial ? "Inicial" : "Mensal"));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_TIPO_MARGEM, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.tipo.margem", responsavel), tipoMargem ? "Vazia" : "Cheia"));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_PROCESSAMENTO_FERIAS, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.processamento.ferias", responsavel), temProcessamentoFerias ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_REIMPLANTE, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.reimplante", responsavel), permiteReimplantarParcela ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_CONCLUIDO_PRAZO_FINAL, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.conluido.prazo.final", responsavel), contratoConcluiPelaDataFim ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_REIMPLANTE_CSA_OPTA, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.reimplante.csa.opta", responsavel), csaDecideSeDevePreservarParcelas ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_VALOR_MIN_PARCELA, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.valor.min.parcela", responsavel), valorMinimoSistFormatado));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_MARGEM_CASADA, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.margem.casada", responsavel), margemCasada ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_PERMITE_COMPRA, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.permite.compra", responsavel), permiteCompraContrato ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_EXIGE_CERTIFICADO_CSA, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.exige.certificado.csa", responsavel), exigeCertificadoCsaCor ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_EXIGE_CERTIFICADO_CSE, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.exige.certificado.cse", responsavel), exigeCertificadoCseOrg ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_PORTAL_SER, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.portal.ser", responsavel), possuiPortalServidor ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_EXIGE_SENHA_SER_CSA, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.exige.senha.ser.csa", responsavel), exigeSenhaSerConsultaMargemCsa ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_EXIGE_SENHA_SER_CSE, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.exige.senha.ser.cse", responsavel), exigeSenhaSerConsultaMargemCse ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_FORMATO_ACESSO_SER, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.formato.acesso.ser", responsavel), exibirRepositorioExternoSenha ? ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.repositorio.externo", responsavel) : mensagemModoEntrega));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_MODULO_RESCISAO, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.modulo.rescisao", responsavel), moduloRescisao ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_MODULO_RESCISAO_SALDO_DEVEDOR, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.modulo.rescisao.saldo.devedor", responsavel), String.valueOf(qtdeDiasValidadeSdv)));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_BLOQUEIO_INATIVIDADE, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.bloqueio.inatividade", responsavel), String.valueOf(diasSemAcessoCsa)));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_RESTRICAO_IP, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.restricao.ip", responsavel), restricaoIP ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_CERTIFICADO_DIGITAL, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.certificado.digital", responsavel), exigeCertificadoDigital ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_MATRICULA_NUMERICA, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.matricula.numerica", responsavel), matriculaSomenteNumerica ? rotuloSim : rotuloNao));
        retorno.add(new RegrasConvenioParametrosBean(CodedValues.REGRAS_CONVENIO_MATRICULA_QUANTIDADE_MAXIMA, ApplicationResourcesHelper.getMessage("rotulo.relatorio.regras.convenio.matricula.quantidade.maxima", responsavel), String.valueOf(qtdeMaximaDigitosMatricula)));

        return retorno;
    }
}
