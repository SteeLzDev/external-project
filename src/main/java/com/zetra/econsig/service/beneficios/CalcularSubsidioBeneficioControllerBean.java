package com.zetra.econsig.service.beneficios;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.BeneficioControllerException;
import com.zetra.econsig.exception.ContratoBeneficioControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.MargemControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Beneficio;
import com.zetra.econsig.persistence.entity.BeneficioHome;
import com.zetra.econsig.persistence.entity.BeneficioServico;
import com.zetra.econsig.persistence.entity.ContratoBeneficio;
import com.zetra.econsig.persistence.entity.ContratoBeneficioHome;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDesconto;
import com.zetra.econsig.persistence.entity.DadosAutorizacaoDescontoHome;
import com.zetra.econsig.persistence.entity.MemoriaCalculoSubsidioHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.OcorrenciaContratoBeneficioHome;
import com.zetra.econsig.persistence.entity.PeriodoBeneficio;
import com.zetra.econsig.persistence.entity.PeriodoBeneficioHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.RelacionamentoBeneficioServicoHome;
import com.zetra.econsig.persistence.entity.StatusContratoBeneficio;
import com.zetra.econsig.persistence.entity.TipoLancamento;
import com.zetra.econsig.persistence.entity.TipoLancamentoHome;
import com.zetra.econsig.persistence.entity.TipoMotivoOperacao;
import com.zetra.econsig.persistence.entity.TipoMotivoOperacaoHome;
import com.zetra.econsig.persistence.query.beneficios.subsidio.ListaVerbaConvenioRelacionamentoServicoSubsidioQuery;
import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarBeneficiariosCalculoSubsidioQuery;
import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarBeneficiariosEscolhidosQuery;
import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarBeneficiariosForaRegraDependenteQuery;
import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarBeneficiariosRemocaoSubsidioQuery;
import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarBeneficioEscolhidoQuery;
import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarServidoresCalculoSubsidioQuery;
import com.zetra.econsig.persistence.query.beneficios.subsidio.ListarTabelaCalculoSubsidioQuery;
import com.zetra.econsig.persistence.query.orgao.ListaOrgaoQuery;
import com.zetra.econsig.service.consignacao.AutorizacaoController;
import com.zetra.econsig.service.folha.PeriodoController;
import com.zetra.econsig.service.margem.MargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.AcaoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.EstadoCivilEnum;
import com.zetra.econsig.values.GrauParentescoEnum;
import com.zetra.econsig.values.MotivoDependenciaEnum;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;

/**
 * <p>Title: CalcularSubsidioBeneficioControllerBean</p>
 * <p>Description: Rotina de cálculo de subsídio para módulo de benefícios</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class CalcularSubsidioBeneficioControllerBean implements CalcularSubsidioBeneficioController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(CalcularSubsidioBeneficioControllerBean.class);

    @Autowired
    @Qualifier("autorizacaoController")
    private AutorizacaoController autorizacaoController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PeriodoController periodoController;

    @Autowired
    private MargemController margemController;

    @Autowired
    private ServidorController servidorController;

    /**
     * Carrega a tabela "tb_periodo_beneficio" com o período a ser usado para o cálculo de subsídio
     * @param periodo
     * @param dataIni
     * @param dataFim
     * @param responsavel
     * @throws BeneficioControllerException
     */
    @Override
    public void definirPeriodoCalculoSubsidio(Date periodo, Date dataIni, Date dataFim, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            List<String> orgaos = new ArrayList<>();
            ListaOrgaoQuery query = new ListaOrgaoQuery();
            List<TransferObject> transferOrgaos = query.executarDTO();

            transferOrgaos.forEach(o -> {
                String orgao = o.getAttribute(Columns.ORG_CODIGO).toString();
                orgaos.add(orgao);
            });

            if (dataIni != null && dataFim != null) {
                dataIni = DateHelper.clearHourTime(dataIni);
                dataFim = DateHelper.getEndOfDay(dataFim);
                for (String  orgCodigo : orgaos) {
                    Collection<PeriodoBeneficio> periodos = PeriodoBeneficioHome.findByOrgCodigo(orgCodigo);
                    for (PeriodoBeneficio bean : periodos) {
                        PeriodoBeneficioHome.remove(bean);
                    }

                    Integer diaCorte = DateHelper.getDay(dataFim);
                    Date periodoAnt = null;
                    Date periodoPos = null;

                    if (!PeriodoHelper.folhaMensal(responsavel)) {
                        periodoAnt = periodoController.obtemPeriodoAposPrazo(orgCodigo, -1, periodo, false, responsavel);
                        periodoPos = periodoController.obtemPeriodoAposPrazo(orgCodigo, +1, periodo, false, responsavel);
                    } else {
                        periodoAnt = DateHelper.addMonths(periodo, -1);
                        periodoPos = DateHelper.addMonths(periodo, +1);
                    }

                    PeriodoBeneficioHome.create(orgCodigo, periodo, periodoAnt, periodoPos, diaCorte.shortValue(), dataIni, dataFim, (short) 1);
                }
            } else {
                periodoController.obtemPeriodoBeneficio(orgaos, null, true, periodo, responsavel);
            }
        } catch (HQueryException | FindException | RemoveException | CreateException ex) {
            LOG.error(ex.getCause(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new BeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (PeriodoException ex) {
            LOG.error(ex.getCause(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new BeneficioControllerException(ex);
        }
    }

    /**
     * Realiza o cálculo da ordem de dependencia dos benefícios.
     * @param tipoEntidade
     * @param entCodigos
     * @param bfcCodigos
     * @param simulacao
     * @param responsavel
     * @throws BeneficioControllerException
     */
    @Override
    public void calcularOrdemDependenciaBeneficiario(String tipoEntidade, List<String> entCodigos, List<String> bfcCodigos, boolean simulacao, AcessoSistema responsavel) throws BeneficioControllerException {
        beneficiarioController.calcularOrdemDependenciaBeneficiario(tipoEntidade, entCodigos, bfcCodigos, simulacao, responsavel);
    }

    /**
     * Realiza o cálculo do subsídio dos contratos de benefícios.
     *
     * @param periodoReferencia : Informa o periodo de refencia, se for null usa o periodo atual
     * @param validar           : TRUE se deve executar apenas a validação
     * @param nomeArqCritica    : Nome do arquivo de saída para geração de críticas
     * @param tipoEntidade      : RSE, ORG ou EST
     * @param entCodigos        : Códigos das entidades, sejam RSE_CODIGO, ORG_CODIGO ou EST_CODIGO
     * @param responsavel       : Responsável pela operação
     * @throws BeneficioControllerException
     * @throws UpdateException
     * @throws CreateException
     * @throws ContratoBeneficioControllerException
     * @throws ParametroControllerException
     */
    @Override
    public void calcularSubsidioContratosBeneficios(Date periodoReferencia, boolean validar, String nomeArqCritica, String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws BeneficioControllerException {
        PrintWriter arqCritica = null;

        try {
            Date dataInicioCalculoSubsidio = DateHelper.getSystemDatetime();
            LOG.info("Inicio busca de parametros para executar o calculo do subsidio");
            boolean calcularSubsidioInativos = ParamSist.paramEquals(CodedValues.TPC_APLICA_SUBSIDIO_FAMILIA_SERVIDOR_INATIVO, CodedValues.TPC_SIM, responsavel);
            boolean usarMenorFaixaSalarialInativos = ParamSist.paramEquals(CodedValues.TPC_MENOR_FAIXA_SAL_FAMILIA_SER_INATIVO, CodedValues.TPC_SIM, responsavel);
            boolean calcularSubsidioSalarioNulo = ParamSist.paramEquals(CodedValues.TPC_APLICA_SUBSIDIO_FAMILIA_SER_SALARIO_NULO, CodedValues.TPC_SIM, responsavel);
            boolean ordemPrioridadeGpFamiliarDependencia = ParamSist.paramEquals(CodedValues.TPC_ORDEM_PRIORIDADE_SUBSIDIO_GRUPO_FAMILIAR, CodedValues.ORDEM_PRIORIDADE_SUBSIDIO_GP_FAMILIAR_DEPENDENCIA, responsavel);
            boolean ordemPrioridadeGpFamiliarParentesco = ParamSist.paramEquals(CodedValues.TPC_ORDEM_PRIORIDADE_SUBSIDIO_GRUPO_FAMILIAR, CodedValues.ORDEM_PRIORIDADE_SUBSIDIO_GP_FAMILIAR_GRAU_PARENTESCO, responsavel);
            int qtdMaxDependentesPodemTerSubsidio = TextHelper.parseIntErrorSafe(ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_DEPENDENTES_SUBSIDIO, responsavel), 1);
            int qtdMaxSubsidiosPorDependente = TextHelper.parseIntErrorSafe(ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_SUBSIDIO_POR_DEPENDENTE, responsavel), 1);
            int qtdMaxSubsidiosPorTitular = TextHelper.parseIntErrorSafe(ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_SUBSIDIO_POR_TITULAR, responsavel), 1);
            double percentualLimiteSubsidioSalario = TextHelper.parseDoubleErrorSafe(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITA_VAL_SUBSIDIO_BEN_PERCENTUAL_SAL_BASE, responsavel), -1.0);
            boolean validaPercentualLimiteSubsidioSalario = percentualLimiteSubsidioSalario == -1.0 ? false : true;
            boolean excluirDependenteForaRegra = ParamSist.paramEquals(CodedValues.TPC_EXCLUI_DEPENDENTE_FORA_DA_REGRA, CodedValues.TPC_SIM, responsavel);
            boolean dependenteSubsidioIlimitado = ParamSist.paramEquals(CodedValues.TPC_DEPENDENTES_SUBSIDIO_ILIMITADO, CodedValues.TPC_SIM, responsavel);

            LOG.info("calcularSubsidioInativos: " + calcularSubsidioInativos);
            LOG.info("usarMenorFaixaSalarialInativos: " + usarMenorFaixaSalarialInativos);
            LOG.info("calcularSubsidioSalarioNulo: " + calcularSubsidioSalarioNulo);
            LOG.info("ordemPrioridadeGpFamiliarDependencia: " + ordemPrioridadeGpFamiliarDependencia);
            LOG.info("ordemPrioridadeGpFamiliarParentesco: " + ordemPrioridadeGpFamiliarParentesco);
            LOG.info("qtdMaxDependentesPodemTerSubsidio: " + qtdMaxDependentesPodemTerSubsidio);
            LOG.info("qtdMaxSubsidiosPorDependente: " + qtdMaxSubsidiosPorDependente);
            LOG.info("qtdMaxSubsidiosPorTitular: " + qtdMaxSubsidiosPorTitular);
            LOG.info("percentualLimiteSubsidioSalario: " + percentualLimiteSubsidioSalario);
            LOG.info("validaPercentualLimiteSubsidioSalario: " + validaPercentualLimiteSubsidioSalario);
            LOG.info("excluirDependenteForaRegra: " + excluirDependenteForaRegra);
            LOG.info("dependenteSubsidioIlimitado: " + dependenteSubsidioIlimitado);

            // Calcula a ordem de dependencia dos beneficiários
            if (ordemPrioridadeGpFamiliarDependencia) {
                LOG.info("Iniciando o calculo da ordem de dependencia do beneficiarios");
                calcularOrdemDependenciaBeneficiario(tipoEntidade, entCodigos, null, false, responsavel);
                LOG.info("Fim do calculo da ordem de dependencia do beneficiarios");
            }

            List<String> srsCodigos = new ArrayList<>();
            srsCodigos.add(CodedValues.SRS_ATIVO);
            if (calcularSubsidioInativos) {
                srsCodigos.addAll(CodedValues.SRS_BLOQUEADOS);
            }

            LOG.info("Inicio do busca das regras de calculo do subsidios cadastradas.");
            ListarTabelaCalculoSubsidioQuery queryCalculo = new ListarTabelaCalculoSubsidioQuery();
            queryCalculo.tipoEntidade = tipoEntidade;
            queryCalculo.entCodigos = entCodigos;
            List<TransferObject> regrasCalculoSubsidio = queryCalculo.executarDTO();
            if (regrasCalculoSubsidio == null || regrasCalculoSubsidio.isEmpty()) {
                throw new BeneficioControllerException("mensagem.erro.calculo.subsidio.ausencia.regras", responsavel);
            }
            LOG.info("Numero de regras de calculo do subsidios cadastradas: " + regrasCalculoSubsidio.size());
            LOG.info("Fim da busca das regras de calculo do subsidios cadastradas.");

            try {
                if (!TextHelper.isNull(nomeArqCritica)) {
                    arqCritica = new PrintWriter(new BufferedWriter(new FileWriter(nomeArqCritica)));
                }
            } catch (IOException ex) {
                LOG.error(ex.getCause(), ex);
                throw new BeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }

            LOG.info("Inicio da busca dos servidores para realizar o calculo do subsidio");
            ListarServidoresCalculoSubsidioQuery queryServidores = new ListarServidoresCalculoSubsidioQuery();
            queryServidores.srsCodigos = srsCodigos;
            queryServidores.tipoEntidade = tipoEntidade;
            queryServidores.entCodigos = entCodigos;
            List<TransferObject> servidores = queryServidores.executarDTO();

            ListarServidoresCalculoSubsidioQuery queryServidoresForaFolha = new ListarServidoresCalculoSubsidioQuery();
            queryServidoresForaFolha.srsCodigos = srsCodigos;
            queryServidoresForaFolha.tipoEntidade = tipoEntidade;
            queryServidoresForaFolha.entCodigos = entCodigos;
            queryServidoresForaFolha.servidoresForaFolha = true;
            List<TransferObject> servidoresForaFolha = queryServidoresForaFolha.executarDTO();

            if(servidoresForaFolha != null && !servidoresForaFolha.isEmpty()) {
                servidores.addAll(servidoresForaFolha);
            }

            LOG.info("O numero de servidores a serem processados: " + servidores.size());
            LOG.info("Fim da busca dos servidores para realizar o calculo do subsidio");
            for (TransferObject servidor : servidores) {
                SessionUtil.clearSession(SessionUtil.getSession());

                // Criando uma memoria de calculo para cada servidor calculado.
                // O primeiro Map é composto por key: bfcCodigo e o valor: Uma lista de memorias de calculos.
                // A lista de memoria de calculos tem o segundo Map que é composto por key: Chave do objeto relevanto ao calculo e o valor: é o objeto referente a key
                // Assim podemos salvar para "depois" a gravação no banco de dados sem atrapalhar outros sistemas que serviço tem prioridade
                Map<String, List<Map<String, Object>>> memoriaCalculoPorBeneficiario = new LinkedHashMap<>();

                String serCpf = servidor.getAttribute(Columns.SER_CPF).toString();
                String serCodigo = servidor.getAttribute(Columns.SER_CODIGO).toString();
                String srsCodigo = servidor.getAttribute(Columns.SRS_CODIGO).toString();
                String orgCodigo = servidor.getAttribute(Columns.ORG_CODIGO).toString();
                Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);

                LOG.info("Calculando subsídio para grupo familiar \"" + serCodigo + "\":");

                // Salário utilizado para a busca das regras de cálculo
                LOG.info("Analisando o salario para o SER_CODIGO: " + servidor.getAttribute(Columns.SER_CODIGO).toString());
                BigDecimal rseSalario = (BigDecimal) servidor.getAttribute(Columns.RSE_SALARIO);
                if (rseSalario == null || rseSalario.signum() <= 0) {
                    if (!calcularSubsidioSalarioNulo) {
                        if (arqCritica != null) {
                            arqCritica.println(ApplicationResourcesHelper.getMessage("mensagem.erro.calculo.subsidio.salario.invalido", responsavel, serCpf));
                        }
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.calculo.subsidio.salario.invalido", responsavel, serCpf));
                        continue;
                    } else {
                        rseSalario = BigDecimal.valueOf(0.01);
                    }
                }

                // Salário utilizado para a busca das regras de cálculo
                BigDecimal salarioTitular = rseSalario;
                if (usarMenorFaixaSalarialInativos && CodedValues.SRS_BLOQUEADOS.contains(srsCodigo)) {
                    salarioTitular = BigDecimal.valueOf(0.01);
                }

                // Calcula o valor máximo que pode ser descontado em folha com base no salário do titular
                double vlrLimiteSubsidioSalario = rseSalario.doubleValue() * (percentualLimiteSubsidioSalario / 100.0);

                LOG.info("Inicio da busca dos beneficiario do grupo familiar.");
                ListarBeneficiariosCalculoSubsidioQuery queryBeneficiarios = new ListarBeneficiariosCalculoSubsidioQuery();
                queryBeneficiarios.serCodigo = serCodigo;
                queryBeneficiarios.tntCodigos = CodedValues.TNT_BENEFICIO_MENSALIDADE;
                List<TransferObject> beneficiarios = queryBeneficiarios.executarDTO();
                LOG.info("Fim da busca dos beneficiario do grupo familiar.");

                // Filtrar beneficiários que podem receber subsídio
                LOG.info("Inicio do filtro dos beneficiarios com direito a subsidio.");
                beneficiarios = filtrarBeneficiariosDireitoSubsidio(beneficiarios, orgCodigo, false, null, responsavel);
                LOG.info("Fim do filtro dos beneficiarios com direito a subsidio.");

                // Ordenar beneficiários que podem receber subsídio
                LOG.info("Inicio da ordenação dos beneficiarios com direito a subsidio.");
                beneficiarios = ordenarBeneficiariosDireitoSubsidio(beneficiarios, ordemPrioridadeGpFamiliarDependencia, ordemPrioridadeGpFamiliarParentesco, responsavel);
                LOG.info("Fim da ordenação dos beneficiarios com direito a subsidio.");

                // Controles sobre os subsídios concedidos
                int qtdSubsidioConcedidoTitular = 0;
                Map<String, Integer> qtdSubsidioConcedidoPorDependente = new HashMap<>();
                Map<String, Map<String, Integer>> qtdSubsidioConcedidoPorNatureza = new HashMap<>();

                int ordem = 0;

                for (TransferObject beneficiario : beneficiarios) {
                    ordem++;
                    LOG.info("Calculando subsídio para beneficiário \"" + beneficiario.getAttribute(Columns.BFC_CODIGO) + "\" (" + ordem + "):");
                    String bfcCodigo = beneficiario.getAttribute(Columns.BFC_CODIGO).toString();
                    String tibCodigo = beneficiario.getAttribute(Columns.TIB_CODIGO).toString();
                    String nseCodigo = beneficiario.getAttribute(Columns.NSE_CODIGO).toString();
                    String benCodigo = beneficiario.getAttribute(Columns.BEN_CODIGO).toString();
                    String cbeCodigo = beneficiario.getAttribute(Columns.CBE_CODIGO).toString();
                    String cbeNumero = beneficiario.getAttribute(Columns.CBE_NUMERO).toString();
                    String adeCodigoMensalidade = beneficiario.getAttribute(Columns.ADE_CODIGO).toString();
                    String tlaCodigo = beneficiario.getAttribute(Columns.TLA_CODIGO).toString();
                    String tntCodigo = beneficiario.getAttribute(Columns.TNT_CODIGO).toString();
                    String csaCodigo = beneficiario.getAttribute(Columns.CSA_CODIGO).toString();
                    String mdeCodigo = (String) beneficiario.getAttribute(Columns.MDE_CODIGO);

                    String subsidioConcedido = beneficiario.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO) != null ? beneficiario.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO).toString() : CodedValues.TPC_NAO;

                    Date bfcDataNascimento = (Date) beneficiario.getAttribute(Columns.BFC_DATA_NASCIMENTO);

                    int idadeBeneficiario = calcularIdadeBeneficiario(bfcDataNascimento, csaCodigo, orgCodigo, false, responsavel);

                    //DESENV-14450 - Cálculo benefício passa a filtrar por grau parentesco
                    String grauParentesco ="";
                    if (!tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                        grauParentesco = beneficiario.getAttribute(Columns.BFC_GRP_CODIGO).toString();
                    }

                    // Atualizar a ADE referente ao subsídio do contrato com o novo valor do subsídio.
                    LOG.info("Inicio da busca de relacionamento para o ADE_CODIGO: " + adeCodigoMensalidade);
                    List<RelacionamentoAutorizacao> relacionamentos = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigoMensalidade, CodedValues.TNT_BENEFICIO_SUBSIDIO);
                    if (relacionamentos == null || relacionamentos.size() != 1) {
                        if (arqCritica != null) {
                            arqCritica.println(ApplicationResourcesHelper.getMessage("mensagem.erro.calculo.subsidio.relacionamento.ade.invalido", responsavel, cbeNumero, serCpf));
                        }
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.calculo.subsidio.relacionamento.ade.invalido", responsavel, cbeNumero, serCpf));
                        continue;
                    }
                    LOG.info("Fim da busca de relacionamento para o ADE_CODIGO: " + adeCodigoMensalidade);

                    String adeCodigoSubsidio = relacionamentos.get(0).getAdeCodigoDestino();

                    if (TipoBeneficiarioEnum.TITULAR.equals(tibCodigo)) {
                        // Verifica número máximo de benefícios com subsídio que o titular pode ter.
                        if (qtdSubsidioConcedidoTitular >= qtdMaxSubsidiosPorTitular) {
                            // Interromper a aplicação do subsídio para o titular quando o limite for atingido.
                            LOG.info("Quantidade maxima de Subsidios por Titular atingida, ignorando calculo para esse beneficiario.");
                            continue;
                        }
                    } else {
                        // Verifica número máximo de dependentes que podem ter subsídio (titular não entra na contagem).
                        if (qtdSubsidioConcedidoPorDependente.size() >= qtdMaxDependentesPodemTerSubsidio && !(dependenteSubsidioIlimitado && subsidioConcedido.equals(CodedValues.TPC_SIM))) {
                            // Interromper a aplicação do subsídio para o grupo familiar quando o limite for atingido.
                            LOG.info("Quantidade maxima de Subsidios por Dependentes atingida, ignorando calculo para esse beneficiario.");
                            continue;
                        }

                        // Verifica número máximo de benefícios com subsídio que um dependente pode ter.
                        if (qtdSubsidioConcedidoPorDependente.get(bfcCodigo) != null &&
                                qtdSubsidioConcedidoPorDependente.get(bfcCodigo) >= qtdMaxSubsidiosPorDependente && !(dependenteSubsidioIlimitado && subsidioConcedido.equals(CodedValues.TPC_SIM))) {
                            // Interromper a aplicação do subsídio para o beneficiário quando o limite for atingido.
                            LOG.info("Quantidade maxima de Subsidios para o mesmo Dependente atingida, ignorando calculo para esse beneficiario.");
                            continue;
                        }
                    }

                    // Verificar se o beneficiário (incluindo titular) pode ter mais de um subsídio para serviços da mesma natureza.
                    int qtdMaxSubsidioConcedidoMesmaNatureza = TextHelper.parseIntErrorSafe(beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA), -1);
                    if (qtdMaxSubsidioConcedidoMesmaNatureza != -1 && qtdSubsidioConcedidoPorNatureza.containsKey(bfcCodigo)) {
                        Map<String, Integer> subsidioConcedidoPorNatureza = qtdSubsidioConcedidoPorNatureza.get(bfcCodigo);
                        if (subsidioConcedidoPorNatureza.get(nseCodigo) != null && subsidioConcedidoPorNatureza.get(nseCodigo) >= qtdMaxSubsidioConcedidoMesmaNatureza) {
                            // Interromper a aplicação do subsídio para o beneficiário quando o limite for atingido.
                            LOG.info("Quantidade maxima de de subsídio aplicado para a mesma natureza: " + nseCodigo);
                            continue;
                        }
                    }

                    // Verificar se o valor do subsídio configurado na tb_calculo_beneficio é em moeda (R$), em percentual
                    // sobre o salário do titular ou em percentual sobre o valor do benefício.
                    String formaCalculoSubsidio = (String) beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_TIPO_CALCULO_SUBSIDIO);

                    LOG.info("formaCalculoSubsidio: " + formaCalculoSubsidio);

                    // Filtrar os valores na tabela de cálculo de benefício por: Órgão do titular, tipo do beneficiário, grau parentesco, motivo dependência, código do benefício, faixa salarial e faixa etária.
                    LOG.info("Inicio do filtro da regras de calculo Subsidio para o beneficiario.");
                    List<TransferObject> regrasSubsidioBeneficio = filtrarRegrasCalculoSubsidio(regrasCalculoSubsidio, orgCodigo, benCodigo, tibCodigo, grauParentesco, mdeCodigo, salarioTitular, idadeBeneficiario, responsavel);

                    // Caso mais de um registro seja retornado na pesquisa, interromper o processo e informar erro de configuração na tabela de cálculo de benefício.
                    if (regrasSubsidioBeneficio == null || regrasSubsidioBeneficio.isEmpty()) {
                        if (arqCritica != null) {
                            arqCritica.println(ApplicationResourcesHelper.getMessage("mensagem.erro.calculo.subsidio.nenhuma.regra.encontradas", responsavel, cbeNumero, serCpf));
                        }
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.calculo.subsidio.nenhuma.regra.encontradas", responsavel, cbeNumero, serCpf));
                        continue;
                    } else if (regrasSubsidioBeneficio.size() != 1) {
                        if (arqCritica != null) {
                            arqCritica.println(ApplicationResourcesHelper.getMessage("mensagem.erro.calculo.subsidio.multiplas.regras.encontradas", responsavel, cbeNumero, serCpf));
                        }
                        LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.calculo.subsidio.multiplas.regras.encontradas", responsavel, cbeNumero, serCpf));
                        continue;
                    }
                    LOG.info("Fim do filtro da regras de calculo Subsidio para o beneficiario.");

                    TransferObject regraCalculoSubsidio = regrasSubsidioBeneficio.get(0);

                    BigDecimal clbValorSubsidio = (BigDecimal) regraCalculoSubsidio.getAttribute(Columns.CLB_VALOR_SUBSIDIO);
                    BigDecimal clbValorMensalidade = (BigDecimal) regraCalculoSubsidio.getAttribute(Columns.CLB_VALOR_MENSALIDADE);

                    LOG.info("Valor encontrado para clbValorSubsidio: " + clbValorSubsidio);
                    LOG.info("Valor encontrado para clbValorMensalidade: " + clbValorMensalidade);

                    double valorSubsidio = 0;
                    if (CodedValues.PSE_TIPO_CALCULO_SUBSIDIO_VALOR.equals(formaCalculoSubsidio)) {
                        valorSubsidio = clbValorSubsidio.doubleValue();
                    } else if (CodedValues.PSE_TIPO_CALCULO_SUBSIDIO_PERC_SALARIO.equals(formaCalculoSubsidio)) {
                        valorSubsidio = rseSalario.multiply(clbValorSubsidio.divide(BigDecimal.valueOf(100))).doubleValue();
                    } else if (CodedValues.PSE_TIPO_CALCULO_SUBSIDIO_PERC_BENEFICIO.equals(formaCalculoSubsidio)) {
                        valorSubsidio = clbValorMensalidade.multiply(clbValorSubsidio.divide(BigDecimal.valueOf(100))).doubleValue();
                    } else if (CodedValues.PSE_TIPO_CALCULO_SUBSIDIO_PERC_DESCONTO_SALARIO.equals(formaCalculoSubsidio)) {
                        double valorSubsidioTemp = clbValorMensalidade.subtract(rseSalario.multiply(clbValorSubsidio.divide(BigDecimal.valueOf(100)))).doubleValue();
                        valorSubsidio = valorSubsidioTemp < 0 || valorSubsidioTemp == clbValorMensalidade.doubleValue() ? 0 : valorSubsidioTemp;
                    }

                    BigDecimal novoValorSubsidio = BigDecimal.valueOf(valorSubsidio).setScale(2, RoundingMode.HALF_UP);
                    LOG.info("Subsídio calculado: " + novoValorSubsidio);

                    // Atualiza o contador de subsídios concedidos por titular e dependentes
                    if (TipoBeneficiarioEnum.TITULAR.equals(tibCodigo)) {
                        qtdSubsidioConcedidoTitular++;
                    } else {
                        Integer qtd = qtdSubsidioConcedidoPorDependente.get(bfcCodigo);
                        qtd = (qtd == null ? 1 : qtd + 1);
                        qtdSubsidioConcedidoPorDependente.put(bfcCodigo, qtd);
                    }

                    // Atualiza o contador de subsídios concedidos por natureza de serviço
                    Map<String, Integer> subsidioConcedidoPorNatureza = qtdSubsidioConcedidoPorNatureza.get(bfcCodigo);
                    if (subsidioConcedidoPorNatureza == null) {
                        subsidioConcedidoPorNatureza = new HashMap<>();
                        qtdSubsidioConcedidoPorNatureza.put(bfcCodigo, subsidioConcedidoPorNatureza);
                    }
                    Integer qtd = subsidioConcedidoPorNatureza.get(nseCodigo);
                    qtd = (qtd == null ? 1 : qtd + 1);
                    subsidioConcedidoPorNatureza.put(nseCodigo, qtd);

                    // Analisando se o beneficiario já tem uma lias de memoria de calculos, se não tiver criamos uma.
                    List<Map<String, Object>> memorias = memoriaCalculoPorBeneficiario.get(bfcCodigo);
                    memorias = memorias == null ? new ArrayList<>() : memorias;

                    HashMap<String, Object> memoria = new HashMap<>();

                    // Memoria relacionada ao criação Json
                    memoria.put("beneficiario", beneficiario);
                    memoria.put("regraCalculoSubsidio", regraCalculoSubsidio);
                    memoria.put("ordem", ordem);
                    memoria.put("idadeBeneficiario", idadeBeneficiario);
                    memoria.put("vlrLimiteSubsidioSalario", vlrLimiteSubsidioSalario);
                    memoria.put("valorSubsidio", valorSubsidio);

                    // Memoria relacionada ao calculo em si.
                    memoria.put("cbeCodigo", cbeCodigo);
                    memoria.put("adeCodigoMensalidade",adeCodigoMensalidade);
                    memoria.put("adeCodigoSubsidio", adeCodigoSubsidio);
                    memoria.put("clbValorMensalidade", clbValorMensalidade);
                    memoria.put("novoValorSubsidio", novoValorSubsidio);
                    memoria.put("periodoAtual", periodoAtual);
                    memoria.put("periodoReferencia", periodoReferencia);
                    memoria.put("tlaCodigo", tlaCodigo);
                    memoria.put("tntCodigo", tntCodigo);
                    memorias.add(memoria);

                    memoriaCalculoPorBeneficiario.put(bfcCodigo, memorias);
                }

                // Verificando se o parâmetro de percentual máximo de desconto sobre o salário está ativo.
                // Caso esteja ativo, temos que verfificar se o somatório do valor a pagar por beneficiário passou do teto definido no parâmetro.
                if (validaPercentualLimiteSubsidioSalario) {
                    LOG.info("Inicio analise se os subsidios calculados excede o maximo do salario.");
                    // Convertendo o valor de decimal para BigDecimal
                    BigDecimal vlrLimiteSubsidioSalarioBigDecimal = new BigDecimal(vlrLimiteSubsidioSalario).setScale(2,java.math.RoundingMode.HALF_UP);

                    boolean vlrLimiteConsumido = false;
                    // A memoria de calculo tem a chave (bfcCodigo) que para cada valor eu pego um lista de cada contrato
                    // Cada posição da lista é um Map composta da memoria daquela contrato.
                    for (String key : memoriaCalculoPorBeneficiario.keySet()) {
                        // Recumenrado as memorias do calculo do beneficiario
                        List<Map<String, Object>> memorias = memoriaCalculoPorBeneficiario.get(key);

                        // Realizando o somatorio de quando o beneficiario tera que pagar
                        // Assim comparamos se passou do teto configurado
                        BigDecimal vlrTotalAPagarBeneficiario = new BigDecimal("0.00");
                        for (Map<String, Object> memoria : memorias) {
                            BigDecimal mensalidade = (BigDecimal) memoria.get("clbValorMensalidade");
                            BigDecimal subsidio = (BigDecimal) memoria.get("novoValorSubsidio");
                            vlrTotalAPagarBeneficiario = vlrTotalAPagarBeneficiario.add(mensalidade.subtract(subsidio)).setScale(2,java.math.RoundingMode.HALF_UP);
                        }
                        // Calculamos o valor acima do limite de desconto com base no salário do servidor
                        BigDecimal valorAcimaTetoSalario = vlrTotalAPagarBeneficiario.subtract(vlrLimiteSubsidioSalarioBigDecimal).setScale(2,java.math.RoundingMode.HALF_UP);

                        // Vamos analisar cada memoria calculada para verificar e refazer os calculos se necessarios dos valores.
                        for (Map<String, Object> memoria : memorias) {
                            boolean valorAlterada = false;
                            BigDecimal mensalidade = (BigDecimal) memoria.get("clbValorMensalidade");
                            BigDecimal subsidio = (BigDecimal) memoria.get("novoValorSubsidio");
                            String tntCodigo = (String) memoria.get("tntCodigo");

                            mensalidade.setScale(2,java.math.RoundingMode.HALF_UP);
                            subsidio.setScale(2,java.math.RoundingMode.HALF_UP);

                            // Analisamos se o total a pagar por beneficiario está passando do limite máximo permitido sobre o salário do servidor
                            if (valorAcimaTetoSalario.compareTo(new BigDecimal("0.00")) >= 0) {
                                valorAlterada = true;

                                // Tentamos inicialmente pegar o valor que passou do teto e verificamos se podemos usar ele todo para o primeiro contrato
                                // Caso o possível novo valor do subsídio passe o valor da mensalidade, damos 100% de subsídio para o contrato e recalculamos
                                // o valor que o beneficiário terá que pagar.
                                //
                                // Se o possível novo valor do subsídio for menor que o valor da mensalidade, acresentamos no subsídio o valor que passou do teto
                                // e recalculamos o valor que o beneficiário terá que pagar
                                //
                                // A ideia de recalcularmos é que caso sobre algum valor, o próximo serviço pode ser beneficiário dele.
                                if (subsidio.add(valorAcimaTetoSalario).compareTo(mensalidade) >= 0) {
                                    valorAcimaTetoSalario = valorAcimaTetoSalario.subtract(mensalidade.subtract(subsidio));
                                    subsidio = new BigDecimal(mensalidade.toString());
                                } else if(!TextHelper.isNull(tntCodigo) && tntCodigo.equals(CodedValues.TNT_MENSALIDADE_PLANO_SAUDE) && mensalidade.subtract(subsidio).compareTo(vlrLimiteSubsidioSalarioBigDecimal) >=0) {
                                    subsidio = mensalidade.subtract(vlrLimiteSubsidioSalarioBigDecimal).setScale(2,java.math.RoundingMode.HALF_UP);
                                    valorAcimaTetoSalario = new BigDecimal("0.00");
                                    vlrLimiteConsumido = true;
                                } else if(!TextHelper.isNull(tntCodigo) && tntCodigo.equals(CodedValues.TNT_MENSALIDADE_ODONTOLOGICO) && vlrLimiteConsumido) {
                                    subsidio = mensalidade;
                                    valorAcimaTetoSalario = new BigDecimal("0.00");
                                } else {
                                    subsidio = subsidio.add(valorAcimaTetoSalario).setScale(2,java.math.RoundingMode.HALF_UP);
                                    valorAcimaTetoSalario = new BigDecimal("0.00");
                                }
                            }

                            // Atualizando a memória com os novos cálculos.
                            if (valorAlterada) {
                                memoria.put("novoValorSubsidio", subsidio);
                            }
                        }
                    }

                    LOG.info("Inicio analise se os subsidios calculados excede o maximo do salario.");
                }

                // Após todas as lógicas serem calculadas, vamos percorrer todos os cálculos e vamos salvar no banco ou no arquivo de validação
                for (String key : memoriaCalculoPorBeneficiario.keySet()) {
                    List<Map<String, Object>> memorias = memoriaCalculoPorBeneficiario.get(key);
                    for (Map<String, Object> memoria : memorias) {
                        // Criando o Json para salvar na tabela de memoria de calculo
                        LOG.info("Inicio da criação do JSON da memoria de calculo para o beneficiario: " + key);
                        String jsonMemoriaCalculo = montarJsonMemoriaCalculo(
                                (TransferObject) memoria.get("beneficiario"),
                                (TransferObject) memoria.get("regraCalculoSubsidio"),
                                (Integer) memoria.get("ordem"),
                                (Integer) memoria.get("idadeBeneficiario"),
                                (Double) memoria.get("vlrLimiteSubsidioSalario"),
                                (Double) memoria.get("valorSubsidio"),
                                ((BigDecimal) memoria.get("novoValorSubsidio")).doubleValue(),
                                (String) memoria.get("tlaCodigo"),
                                (String) memoria.get("tntCodigo"),
                                responsavel);
                        LOG.info("Fim da criação do JSON da memoria de calculo para o beneficiario: " + key);
                        if (validar) {
                            LOG.info("Somente simulando o calculo para o beneficiario: " + key);
                            if (arqCritica != null) {
                                arqCritica.println(jsonMemoriaCalculo);
                            }
                        } else {
                            LOG.info("Inicio da atualização dos contratos com novos valores para o beneficiario: " + key);
                            atualizarSubsidioContratoBeneficio(
                                    (String) memoria.get("cbeCodigo"),
                                    (String) memoria.get("adeCodigoMensalidade"),
                                    (String) memoria.get("adeCodigoSubsidio"),
                                    (BigDecimal) memoria.get("clbValorMensalidade"),
                                    (BigDecimal) memoria.get("novoValorSubsidio"),
                                    (Date) memoria.get("periodoAtual"),
                                    (Date) memoria.get("periodoReferencia"),
                                    jsonMemoriaCalculo,
                                    responsavel
                                    );
                            LOG.info("Fim da atualização dos contratos com novos valores para o beneficiario: " + key);
                        }
                    }
                    memorias.clear();
                }

                memoriaCalculoPorBeneficiario.clear();

                // DESENV-9415 : Excluir contratos de benefícios de beneficiários quando não atenderem mais a condição de dependentes
                if(excluirDependenteForaRegra) {
                    excluirContratoBeneficioForaRegraDependente(serCodigo, responsavel);
                }
            }

            if (!validar) {
                // Os beneficiários que não entrarem na regra para recebimento de subsídio devem ter os contratos
                // alterados para zerar o subsídio. Esse procedimento garantirá que dependentes que perderam a
                // prioridade deixarão de receber o subsídio. Aplicar o valor 0.00 para o subsídio e recalcular o
                // valor da mensalidade.
                ListarBeneficiariosRemocaoSubsidioQuery queryRemocaoSubsidio = new ListarBeneficiariosRemocaoSubsidioQuery();
                queryRemocaoSubsidio.dataCalculoSubsidio = dataInicioCalculoSubsidio;
                queryRemocaoSubsidio.tipoEntidade = tipoEntidade;
                queryRemocaoSubsidio.entCodigos = entCodigos;
                List<TransferObject> contratosRemocaoSubsidio = queryRemocaoSubsidio.executarDTO();

                for (TransferObject contrato : contratosRemocaoSubsidio) {
                    SessionUtil.clearSession(SessionUtil.getSession());
                    LOG.info("Removendo subsídio para contrato \"" + contrato.getAttribute(Columns.CBE_CODIGO) + "\":");

                    String serCpf = contrato.getAttribute(Columns.SER_CPF).toString();
                    String cbeCodigo = contrato.getAttribute(Columns.CBE_CODIGO).toString();
                    String cbeNumero = contrato.getAttribute(Columns.CBE_NUMERO).toString();
                    String orgCodigo = contrato.getAttribute(Columns.ORG_CODIGO).toString();
                    String srsCodigo = contrato.getAttribute(Columns.SRS_CODIGO).toString();
                    String benCodigo = contrato.getAttribute(Columns.BEN_CODIGO).toString();
                    String tibCodigo = contrato.getAttribute(Columns.TIB_CODIGO).toString();
                    String adeCodigoMensalidade = contrato.getAttribute(Columns.ADE_CODIGO).toString();
                    String tlaCodigo = contrato.getAttribute(Columns.TLA_CODIGO).toString();
                    String tntCodigo = contrato.getAttribute(Columns.TNT_CODIGO).toString();
                    String csaCodigo = contrato.getAttribute(Columns.CSA_CODIGO).toString();
                    String mdeCodigo = (String) contrato.getAttribute(Columns.MDE_CODIGO);

                    // periodoAtual é usado para criação de ocorrencia de Autorização, como ainda estamos usando o periodo
                    // de consignados não vamos alterar isso aqui agora.
                    Date periodoAtual = PeriodoHelper.getInstance().getPeriodoBeneficioAtual(orgCodigo, responsavel);

                    // Salário utilizado para a busca das regras de cálculo
                    BigDecimal rseSalario = (BigDecimal) contrato.getAttribute(Columns.RSE_SALARIO);
                    if (rseSalario == null || rseSalario.signum() <= 0) {
                        if (!calcularSubsidioSalarioNulo) {
                            if (arqCritica != null) {
                                arqCritica.println(ApplicationResourcesHelper.getMessage("mensagem.erro.calculo.subsidio.salario.invalido", responsavel, serCpf));
                            }
                            continue;
                        } else {
                            rseSalario = BigDecimal.valueOf(0.01);
                        }
                    }

                    // Salário utilizado para a busca das regras de cálculo
                    BigDecimal salarioTitular = rseSalario;
                    if (usarMenorFaixaSalarialInativos && CodedValues.SRS_BLOQUEADOS.contains(srsCodigo)) {
                        salarioTitular = BigDecimal.valueOf(0.01);
                    }

                    Date bfcDataNascimento = (Date) contrato.getAttribute(Columns.BFC_DATA_NASCIMENTO);
                    int idadeBeneficiario = calcularIdadeBeneficiario(bfcDataNascimento, csaCodigo, orgCodigo, false, responsavel);

                    // Atualizar a ADE referente ao subsídio do contrato com o novo valor do subsídio.
                    List<RelacionamentoAutorizacao> relacionamentos = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigoMensalidade, CodedValues.TNT_BENEFICIO_SUBSIDIO);
                    if (relacionamentos == null || relacionamentos.size() != 1) {
                        if (arqCritica != null) {
                            arqCritica.println(ApplicationResourcesHelper.getMessage("mensagem.erro.calculo.subsidio.relacionamento.ade.invalido", responsavel, cbeNumero, serCpf));
                        }
                        continue;
                    }

                    String adeCodigoSubsidio = relacionamentos.get(0).getAdeCodigoDestino();

                    //DESENV-14450 - Cálculo benefício passa a filtrar por grau parentesco
                    String grauParentesco = "";
                    if (!tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                        grauParentesco = contrato.getAttribute(Columns.BFC_GRP_CODIGO).toString();
                    }

                    // Filtrar os valores na tabela de cálculo de benefício por: Órgão do titular, tipo do beneficiário, código do benefício, faixa salarial e faixa etária.
                    List<TransferObject> regrasSubsidioBeneficio = filtrarRegrasCalculoSubsidio(regrasCalculoSubsidio, orgCodigo, benCodigo, tibCodigo, grauParentesco, mdeCodigo, salarioTitular, idadeBeneficiario, responsavel);

                    // Caso mais de um registro seja retornado na pesquisa, interromper o processo e informar erro de configuração na tabela de cálculo de benefício.
                    if (regrasSubsidioBeneficio == null || regrasSubsidioBeneficio.isEmpty()) {
                        if (arqCritica != null) {
                            arqCritica.println(ApplicationResourcesHelper.getMessage("mensagem.erro.calculo.subsidio.nenhuma.regra.encontradas", responsavel, cbeNumero, serCpf));
                        }
                        continue;
                    } else if (regrasSubsidioBeneficio.size() != 1) {
                        if (arqCritica != null) {
                            arqCritica.println(ApplicationResourcesHelper.getMessage("mensagem.erro.calculo.subsidio.multiplas.regras.encontradas", responsavel, cbeNumero, serCpf));
                        }
                        continue;
                    }

                    TransferObject regraCalculoSubsidio = regrasSubsidioBeneficio.get(0);
                    BigDecimal clbValorMensalidade = (BigDecimal) regraCalculoSubsidio.getAttribute(Columns.CLB_VALOR_MENSALIDADE);

                    // DESENV-8978 Criar memória de cálculo
                    String jsonMemoriaCalculo = montarJsonMemoriaCalculo(contrato, regraCalculoSubsidio, -1, idadeBeneficiario, 0.0, 0.0, 0.0, tlaCodigo, tntCodigo, responsavel);

                    atualizarSubsidioContratoBeneficio(cbeCodigo, adeCodigoMensalidade, adeCodigoSubsidio, clbValorMensalidade, BigDecimal.ZERO, periodoAtual, periodoReferencia, jsonMemoriaCalculo, responsavel);
                }
            }
        } catch (HQueryException | FindException | CreateException | UpdateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new BeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (PeriodoException | ContratoBeneficioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new BeneficioControllerException(ex);
        } catch (BeneficioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw ex;
        } finally {
            if (arqCritica != null) {
                arqCritica.close();
            }
        }
    }

    /**
     *
     * @param validar
     * @param adeCodigo
     * @param orgCodigo
     * @param responsavel
     * @throws BeneficioControllerException
     * @throws ParametroControllerException
     */
    @Override
    public void calcularSubsidioContratosBeneficiosProRata(boolean validar, String adeCodigo, String orgCodigo, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            // Analisamos se o contrato que esta chegando é to tipo Pro Rata e se o serviço para esse contrato está ativado a necessidade de existir uma pro rata.
            ListarBeneficiariosCalculoSubsidioQuery queryBeneficiarios = new ListarBeneficiariosCalculoSubsidioQuery();
            queryBeneficiarios.adeCodigo = adeCodigo;
            queryBeneficiarios.tntCodigos = CodedValues.TNT_BENEFICIO_PRO_RATA;
            queryBeneficiarios.ignoraPeriodo = true;
            List<TransferObject> beneficiariosProRata = queryBeneficiarios.executarDTO();

            int ordem = 0;
            for (TransferObject beneficiarioProRata : beneficiariosProRata) {
                ordem++;
                LOG.debug("Calculando subsídio pro rata para beneficiário \"" + beneficiarioProRata.getAttribute(Columns.BFC_NOME) + "\" (" + ordem + "):");

                // Recuperando campos importante para a logica abaixo e do JSON
                String cbeCodigo = beneficiarioProRata.getAttribute(Columns.CBE_CODIGO).toString();
                String csaCodigo = beneficiarioProRata.getAttribute(Columns.CSA_CODIGO).toString();
                String tlaCodigo = beneficiarioProRata.getAttribute(Columns.TLA_CODIGO).toString();
                String tntCodigo = beneficiarioProRata.getAttribute(Columns.TNT_CODIGO).toString();
                String rseCodigo = beneficiarioProRata.getAttribute(Columns.RSE_CODIGO).toString();
                String orgCodigoBeneficiario = beneficiarioProRata.getAttribute(Columns.ORG_CODIGO).toString();

                // Calculando a data
                Date bfcDataNascimento = (Date) beneficiarioProRata.getAttribute(Columns.BFC_DATA_NASCIMENTO);
                int idadeBeneficiario = calcularIdadeBeneficiario(bfcDataNascimento, csaCodigo, orgCodigoBeneficiario, false, responsavel);

                // Pegamos o periodo atual.
                Date dataInicialPeriodoAtual = PeriodoHelper.getInstance().getDataIniPeriodoBeneficioAtual(orgCodigo, responsavel);
                Date periodoAtual = PeriodoHelper.getInstance().getPeriodoBeneficioAtual(orgCodigo, responsavel);

                // Procuramos a revisão mais recente antes do inicio do periodo atual.
                // Assim podemos analisar qual era o valor total e do subsidio da ultima "modificação"
                List<ContratoBeneficio> revisions = ContratoBeneficioHome.findRevisionLessThenDate(cbeCodigo, dataInicialPeriodoAtual);
                AutDesconto autDescontoProRata = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

                // Cria os valores zerados para case não existe uma revisão podermos continuar com o calculo.
                BigDecimal cbeValorTotal = new BigDecimal("0.00");
                BigDecimal cbeValorSubsidio = new BigDecimal("0.00");
                BigDecimal valorSubsidioProRata = new BigDecimal("0.00");

                // Caso exista uma revisao realiza o calculo com os valores encontrados
                if (revisions != null && revisions.size() > 0) {
                    ContratoBeneficio contratoBeneficio = revisions.get(0);
                    cbeValorTotal = contratoBeneficio.getCbeValorTotal();
                    cbeValorSubsidio = contratoBeneficio.getCbeValorSubsidio();

                    valorSubsidioProRata = cbeValorSubsidio.multiply(autDescontoProRata.getAdeVlr());
                    valorSubsidioProRata = valorSubsidioProRata.divide(cbeValorTotal, java.math.RoundingMode.HALF_EVEN);
                }

                LOG.debug("Valor calculo de subsidio Pro Rata: " + valorSubsidioProRata);

                // Busca necessaria para analisarmos se temos que criar um novo contrato ou não
                List<RelacionamentoAutorizacao> relacionamentosAutorizacao = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_BENEFICIO_SUBSIDIO_PRO_RATA);
                if (relacionamentosAutorizacao != null && relacionamentosAutorizacao.size() == 0) {
                    // Com o contrato de entrada tentamos encontrar um relacionamento de um serviço de destino que se encontre no TNT_BENEFICIO_SUBSIDIO_PRO_RATA
                    ListaVerbaConvenioRelacionamentoServicoSubsidioQuery listaVerbaConvenioRelacionamentoServicoSubsidioQuery = new ListaVerbaConvenioRelacionamentoServicoSubsidioQuery();
                    listaVerbaConvenioRelacionamentoServicoSubsidioQuery.svcCodigo = autDescontoProRata.getVerbaConvenio().getConvenio().getServico().getSvcCodigo();
                    listaVerbaConvenioRelacionamentoServicoSubsidioQuery.csaCodigo = csaCodigo;
                    listaVerbaConvenioRelacionamentoServicoSubsidioQuery.orgCodigo = orgCodigo;
                    listaVerbaConvenioRelacionamentoServicoSubsidioQuery.tntCodigos = CodedValues.TNT_BENEFICIO_SUBSIDIO_PRO_RATA;

                    List<TransferObject> resultadosVerbaConvenioRelacionamentoServicoSubsidio = listaVerbaConvenioRelacionamentoServicoSubsidioQuery.executarDTO();
                    if (resultadosVerbaConvenioRelacionamentoServicoSubsidio == null | resultadosVerbaConvenioRelacionamentoServicoSubsidio.size() != 1) {
                        throw new BeneficioControllerException("mensagem.erro.calculo.subsidio.multiplas.regras.ou.nenhuma.relacionamento.servico.encontrado",
                                responsavel,
                                autDescontoProRata.getVerbaConvenio().getConvenio().getServico().getSvcCodigo(),
                                csaCodigo,
                                orgCodigo,
                                TextHelper.join(CodedValues.TNT_BENEFICIO_SUBSIDIO_PRO_RATA, "','"));
                    } else if (!validar) {
                        TransferObject resultadoVerbaConvenioRelacionamentoServicoSubsidio = resultadosVerbaConvenioRelacionamentoServicoSubsidio.get(0);
                        String vcoCodigoRelacionamento = resultadoVerbaConvenioRelacionamentoServicoSubsidio.getAttribute(Columns.VCO_CODIGO).toString();
                        String tntCodigoRelacionamento = resultadoVerbaConvenioRelacionamentoServicoSubsidio.getAttribute(Columns.TNT_CODIGO).toString();

                        // Calculando o novo valor da mensalidade pro rota
                        BigDecimal novoAdeVlrAutDescontoProRata = autDescontoProRata.getAdeVlr().subtract(valorSubsidioProRata).setScale(2, java.math.RoundingMode.HALF_EVEN);

                        // Criando a memoria de calculo.
                        String jsonMemoriaCalculo = montarJsonMemoriaCalculoProRata(beneficiarioProRata, new CustomTransferObject(), ordem, idadeBeneficiario, -1.00,
                                valorSubsidioProRata.doubleValue(), valorSubsidioProRata.setScale(2, java.math.RoundingMode.HALF_EVEN).doubleValue(), tlaCodigo, tntCodigo,
                                autDescontoProRata.getAdeVlr().doubleValue(), novoAdeVlrAutDescontoProRata.doubleValue(), cbeValorTotal.doubleValue(), cbeValorSubsidio.doubleValue(),
                                dataInicialPeriodoAtual, responsavel);

                        // Grava memória de cálculo do subsídio
                        MemoriaCalculoSubsidioHome.create(cbeCodigo, periodoAtual, autDescontoProRata.getAdeVlr(), valorSubsidioProRata.setScale(2, java.math.RoundingMode.HALF_EVEN), jsonMemoriaCalculo);

                        // Arredondando o valor
                        valorSubsidioProRata = valorSubsidioProRata.setScale(2, java.math.RoundingMode.HALF_EVEN);

                        // Descobrindo o tipo de lançamento.
                        TipoLancamento tipoLancamento = TipoLancamentoHome.findByTntCodigo(tntCodigoRelacionamento);

                        // criando a nova ade de subsidio com base na ade de pro rota
                        AutDesconto autDescontoSubcidio = AutDescontoHome.create(
                                autDescontoProRata.getStatusAutorizacaoDesconto() == null ? null : autDescontoProRata.getStatusAutorizacaoDesconto().getSadCodigo(),
                                vcoCodigoRelacionamento,
                                autDescontoProRata.getRegistroServidor() == null ? null : autDescontoProRata.getRegistroServidor().getRseCodigo(),
                                autDescontoProRata.getCorrespondente() == null ? null : autDescontoProRata.getCorrespondente().getCorCodigo(),
                                autDescontoProRata.getUsuario() == null ? null : autDescontoProRata.getUsuario().getUsuCodigo(),
                                autDescontoProRata.getAdeIdentificador(),
                                String.valueOf(autDescontoProRata.getAdeNumero()),
                                autDescontoProRata.getAdeCodReg(),
                                autDescontoProRata.getAdePrazo(),
                                autDescontoProRata.getAdePrdPagas(),
                                autDescontoProRata.getAdeAnoMesIni() == null ? null : new java.sql.Date(autDescontoProRata.getAdeAnoMesIni().getTime()),
                                autDescontoProRata.getAdeAnoMesFim() == null ? null : new java.sql.Date(autDescontoProRata.getAdeAnoMesFim().getTime()),
                                autDescontoProRata.getAdeAnoMesIniRef() == null ? null : new java.sql.Date(autDescontoProRata.getAdeAnoMesIniRef().getTime()),
                                autDescontoProRata.getAdeAnoMesFimRef() == null ? null : new java.sql.Date(autDescontoProRata.getAdeAnoMesFimRef().getTime()),
                                valorSubsidioProRata,
                                autDescontoProRata.getAdeVlrTac(),
                                autDescontoProRata.getAdeVlrIof(),
                                autDescontoProRata.getAdeVlrLiquido(),
                                autDescontoProRata.getAdeVlrMensVinc(),
                                autDescontoProRata.getAdeTaxaJuros(),
                                autDescontoProRata.getAdeVlrSegPrestamista(),
                                autDescontoProRata.getAdeTipoVlr(),
                                autDescontoProRata.getAdeIntFolha(),
                                autDescontoProRata.getAdeIncMargem(),
                                autDescontoProRata.getAdeCarencia(),
                                autDescontoProRata.getAdeCarenciaFinal(),
                                autDescontoProRata.getAdeDataHoraOcorrencia() == null ? null : new Timestamp(autDescontoProRata.getAdeDataHoraOcorrencia().getTime()),
                                autDescontoProRata.getAdeVlrSdoMov(),
                                autDescontoProRata.getAdeVlrSdoRet(),
                                autDescontoProRata.getAdeBanco(),
                                autDescontoProRata.getAdeAgencia(),
                                autDescontoProRata.getAdeConta(),
                                autDescontoProRata.getAdeTipoTaxa(),
                                autDescontoProRata.getAdeVlrPercentual(),
                                autDescontoProRata.getAdePeriodicidade(),
                                autDescontoProRata.getCidade() == null ? null : autDescontoProRata.getCidade().getCidCodigo(),
                                autDescontoProRata.getContratoBeneficio() == null ? null : autDescontoProRata.getContratoBeneficio().getCbeCodigo(),
                                tipoLancamento.getTlaCodigo());

                        // Criando a ocorrencia da nova ade de subsidio
                        // Contrato ainda usando o periodo consignado
                        OcorrenciaAutorizacaoHome.create(autDescontoSubcidio.getAdeCodigo(), CodedValues.TOC_TARIF_RESERVA, responsavel.getUsuCodigo(),
                                ApplicationResourcesHelper.getMessage("mensagem.informacao.ptf.inclusao.reserva", responsavel),
                                new BigDecimal("0.00"), autDescontoSubcidio.getAdeVlr(), responsavel.getIpUsuario(),
                                null, PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel), null);

                        if (novoAdeVlrAutDescontoProRata.compareTo(autDescontoProRata.getAdeVlr()) != 0) {
                            // Contrato ainda usando o periodo consignado
                            OcorrenciaAutorizacaoHome.create(autDescontoSubcidio.getAdeCodigo(), CodedValues.TOC_TARIF_RESERVA, responsavel.getUsuCodigo(),
                                    ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.oca.obs.alteracao.contrato", responsavel),
                                    autDescontoProRata.getAdeVlr(), novoAdeVlrAutDescontoProRata, responsavel.getIpUsuario(),
                                    null, PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel), null);

                            autDescontoProRata.setAdeVlr(novoAdeVlrAutDescontoProRata);
                            AutDescontoHome.update(autDescontoProRata);
                        }

                        LOG.debug("ADE COD SUBSIDIO: " + autDescontoSubcidio.getAdeCodigo());

                        RelacionamentoAutorizacaoHome.create(autDescontoProRata.getAdeCodigo(),autDescontoSubcidio.getAdeCodigo(), tntCodigoRelacionamento, responsavel.getUsuCodigo());

                        List<DadosAutorizacaoDesconto> listaDadosAutorizacaoDescontoProRta = DadosAutorizacaoDescontoHome.findByAdeCodigo(autDescontoProRata.getAdeCodigo());
                        for (DadosAutorizacaoDesconto dadosAutorizacaoDesconto : listaDadosAutorizacaoDescontoProRta) {
                            autorizacaoController.setDadoAutDesconto(autDescontoSubcidio.getAdeCodigo(),
                                    dadosAutorizacaoDesconto.getTipoDadoAdicional().getTdaCodigo(),
                                    dadosAutorizacaoDesconto.getDadValor(),
                                    responsavel);
                        }

                        String tdaCodigo = "VALORTOTALRESERVADO";
                        String dadValor = NumberHelper.format(autDescontoProRata.getAdeVlr().doubleValue(), "en");
                        autorizacaoController.setDadoAutDesconto(adeCodigo, tdaCodigo, dadValor, responsavel);
                        autorizacaoController.setDadoAutDesconto(autDescontoSubcidio.getAdeCodigo(), tdaCodigo, dadValor, responsavel);

                        tdaCodigo = "SUBSIDIO";
                        dadValor = NumberHelper.format(valorSubsidioProRata.doubleValue(), "en");
                        autorizacaoController.setDadoAutDesconto(adeCodigo, tdaCodigo, dadValor, responsavel);
                        autorizacaoController.setDadoAutDesconto(autDescontoSubcidio.getAdeCodigo(), tdaCodigo, dadValor, responsavel);

                        // Recalculando a margem já que estamos criando contratos, alterando valor etc.
                        List<String> rseCodigos = new ArrayList<>();
                        rseCodigos.add(rseCodigo);
                        margemController.recalculaMargemComHistorico("RSE", rseCodigos, responsavel);
                    }
                }
            }
        } catch (HQueryException | FindException | CreateException | UpdateException | PeriodoException | AutorizacaoControllerException | MargemControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new BeneficioControllerException(ex);
        }
    }

    /**
     * Filtra a lista de beneficiários e seus contratos deixando apenas aqueles que tem
     * direito à recebimento de subsídio.
     * @param beneficiarios
     * @return
     * @throws BeneficioControllerException
     * @throws ParseException
     */
    private List<TransferObject> filtrarBeneficiariosDireitoSubsidio(List<TransferObject> beneficiarios, String orgCodigo, boolean simulacao, String csaCodigoSimulacao, AcessoSistema responsavel) throws BeneficioControllerException {
        List<TransferObject> candidatos = new ArrayList<>();
        Set<String> contratosCandidatosCadastrado = new HashSet<>();

        Date dataAtual = Calendar.getInstance().getTime();

        for (TransferObject beneficiario : beneficiarios) {
            String tibCodigo = (String) beneficiario.getAttribute(Columns.TIB_CODIGO);
            String csaCodigo = (String) beneficiario.getAttribute(Columns.CSA_CODIGO);

            String cbeCodigo = "";
            String key = "";

            if (!simulacao) {
                cbeCodigo = (String) beneficiario.getAttribute(Columns.CBE_CODIGO);
                key = cbeCodigo;
            } else {
                csaCodigo = csaCodigoSimulacao;
                key = UUID.randomUUID().toString();
            }

            // ATENÇÃO: não tentar "otimizar" este código fazendo IF/ELSE pois o tibCodigo pode ser alterado
            if (TipoBeneficiarioEnum.TITULAR.equals(tibCodigo)) {
                // O titular sempre terá direito a subsídio
                candidatos.add(beneficiario);
                contratosCandidatosCadastrado.add(key);
            }

            if (TipoBeneficiarioEnum.DEPENDENTE.equals(tibCodigo)) {
                // O dependente terá direito a benefício, desde que, caso sejam filhos
                // a idade ainda esteja de acordo, ou sejam outros tipos de depenente
                String bfcGrauParentesco = (String) beneficiario.getAttribute(Columns.BFC_GRP_CODIGO);
                if (GrauParentescoEnum.FILHO.equals(bfcGrauParentesco) || GrauParentescoEnum.ENTEADO.equals(bfcGrauParentesco)) {
                    String mdeCodigo = (String) beneficiario.getAttribute(Columns.MDE_CODIGO);
                    Date bfcDataNasc = (Date) beneficiario.getAttribute(Columns.BFC_DATA_NASCIMENTO);
                    int idadeFilho = (bfcDataNasc != null ? calcularIdadeBeneficiario(bfcDataNasc, csaCodigo, orgCodigo, simulacao, responsavel) : 999);
                    boolean tratarComoAgregado = false;

                    if (MotivoDependenciaEnum.ESTUDANTE.equals(mdeCodigo)) {
                        int idadeMaxFilhoEstudanteDireitoSubsidio = TextHelper.parseIntErrorSafe(beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO), 999);
                        if (idadeFilho <= idadeMaxFilhoEstudanteDireitoSubsidio) {
                            candidatos.add(beneficiario);
                            contratosCandidatosCadastrado.add(key);
                        } else {
                            tratarComoAgregado = true;
                        }

                    } else {
                        int idadeMaxFilhoDireitoSubsidio = TextHelper.parseIntErrorSafe(beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO), 999);
                        if (idadeFilho <= idadeMaxFilhoDireitoSubsidio) {
                            candidatos.add(beneficiario);
                            contratosCandidatosCadastrado.add(key);
                        } else {
                            tratarComoAgregado = true;
                        }
                    }

                    if (tratarComoAgregado) {
                        tibCodigo = TipoBeneficiarioEnum.AGREGADO.tibCodigo;
                        beneficiario.setAttribute(Columns.TIB_CODIGO, tibCodigo);
                    }
                } else {
                    // Se não for filho, então é outro tipo dependente que não possui exceção
                    candidatos.add(beneficiario);
                    contratosCandidatosCadastrado.add(key);
                }
            }

            if (TipoBeneficiarioEnum.AGREGADO.equals(tibCodigo)) {
                // O agregado terá direito em casos excepcionais:
                // 1) Caso o serviço permita qualquer agregado
                boolean agregadosPodemTerSubsidio = CodedValues.PSE_BOOLEANO_SIM.equals(beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO));
                if (agregadosPodemTerSubsidio) {
                    candidatos.add(beneficiario);
                    contratosCandidatosCadastrado.add(key);
                } else {
                    // 1) Caso o serviço permita que Pai e Mãe de titular Solteiro ou Divorciado podem ter subsídio
                    boolean paisPodemTerSubsidio = CodedValues.PSE_BOOLEANO_SIM.equals(beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO));
                    String data = (String) beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO);

                    // Se não tem data configurada e erá para analisar se pais podem ter subsidio nos simplesmente imprimimos no log
                    if (TextHelper.isNull(data) && paisPodemTerSubsidio) {
                        LOG.error("O parâmetro de serviço " + CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO + " não está configurando, impossibilitando a analise do parametro de serviço " + CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO);
                    } else if (!TextHelper.isNull(data) && paisPodemTerSubsidio) {
                        // Formatando a data para US
                        Date dataLimitePaisPodemTerSubsidio;
                        try {
                            dataLimitePaisPodemTerSubsidio = DateHelper.parse(data, LocaleHelper.getDatePattern());
                        } catch (ParseException ex) {
                            throw new BeneficioControllerException(ex);
                        }

                        // Pegando a data do contrato para realizar a comparação
                        Date cbeDataInicioVigencia = (Date) beneficiario.getAttribute(Columns.CBE_DATA_INICIO_VIGENCIA);

                        // A data de inicio pode ser null para os contratos que se encontram em simulação.
                        if (cbeDataInicioVigencia == null) {
                            cbeDataInicioVigencia = dataAtual;
                        }

                        // Garantido que vamos comprar somente as datas ignorando hora, minutos e segundos.
                        cbeDataInicioVigencia = DateHelper.clearHourTime(cbeDataInicioVigencia);
                        dataLimitePaisPodemTerSubsidio = DateHelper.clearHourTime(dataLimitePaisPodemTerSubsidio);

                        // Analisando se o sistema pode ter pais com subsidio e a data de inicio do contrato se encontra antes ou igual da data configurada do serviço
                        if (paisPodemTerSubsidio && (cbeDataInicioVigencia.before(dataLimitePaisPodemTerSubsidio) || cbeDataInicioVigencia.equals(dataLimitePaisPodemTerSubsidio))) {
                            String bfcGrauParentesco = (String) beneficiario.getAttribute(Columns.BFC_GRP_CODIGO);
                            String bfcEstadoCivilTitular = obterEstadoCivilTitular(beneficiarios);

                            // Analisando se esta no Grau Permitidos.
                            if ((GrauParentescoEnum.PAI.equals(bfcGrauParentesco) || GrauParentescoEnum.MAE.equals(bfcGrauParentesco)) && (EstadoCivilEnum.DIVORCIADO.equals(bfcEstadoCivilTitular) || EstadoCivilEnum.SOLTEIRO.equals(bfcEstadoCivilTitular))) {
                                candidatos.add(beneficiario);
                                contratosCandidatosCadastrado.add(key);
                            }
                        }
                    }
                }
            }

            // Verificando se o beneficiário tem a marcação de subsídio concedido, se tiver ele vai ganhar o subsídio não importando as regras acima.
            String beneficiarioSubsidioConcedido = (beneficiario.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO) != null ? beneficiario.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO).toString() : CodedValues.TPC_NAO);
            if (CodedValues.TPC_SIM.equals(beneficiarioSubsidioConcedido)) {
                if (!contratosCandidatosCadastrado.contains(cbeCodigo)) {
                    candidatos.add(beneficiario);
                    contratosCandidatosCadastrado.add(cbeCodigo);
                }
            }
        }
        return candidatos;
    }

    /**
     * Ordena a lista de beneficiários e seus contratos de acordom com as regras
     * de ordenação específicas para o sistema e serviços.
     * @param beneficiarios
     * @param ordemPrioridadeGpFamiliarDependencia
     * @param ordemPrioridadeGpFamiliarParentesco
     * @return
     */
    @Override
    public List<TransferObject> ordenarBeneficiariosDireitoSubsidio(List<TransferObject> beneficiarios, final boolean ordemPrioridadeGpFamiliarDependencia, final boolean ordemPrioridadeGpFamiliarParentesco, AcessoSistema responsavel) {
        Collections.sort(beneficiarios, (t1, t2) -> {
            // Estamos analisando se o beneficiario t1 tem a marcação de subcidios concedido, se tiver ignoramos qualquer regras e damos o subcidios para ele primeiro.
            String bfcSubsidioConcedido = (t1.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO) != null ? t1.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO).toString() : CodedValues.TPC_NAO);
            int ordemBeneficiarioSubsidioConcedido1 = !TextHelper.isNull(bfcSubsidioConcedido) && CodedValues.TPC_SIM.equals(bfcSubsidioConcedido) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            // Estamos analisando se o beneficiario t2 tem a marcação de subcidios concedido, se tiver ignoramos qualquer regras e damos o subcidios para ele primeiro.
            bfcSubsidioConcedido = (t2.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO) != null ? t2.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO).toString() : CodedValues.TPC_NAO);
            int ordemBeneficiarioSubsidioConcedido2 = !TextHelper.isNull(bfcSubsidioConcedido) && CodedValues.TPC_SIM.equals(bfcSubsidioConcedido) ? Integer.MIN_VALUE : Integer.MAX_VALUE;

            if (ordemBeneficiarioSubsidioConcedido2 > ordemBeneficiarioSubsidioConcedido1) {
                return -1;
            } else if (ordemBeneficiarioSubsidioConcedido2 < ordemBeneficiarioSubsidioConcedido1) {
                return 1;
            } else {
                int ordemPrioridadeServico1 = TextHelper.parseIntErrorSafe(t1.getAttribute(Columns.PSE_VLR + CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO), 9999);
                int ordemPrioridadeServico2 = TextHelper.parseIntErrorSafe(t2.getAttribute(Columns.PSE_VLR + CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO), 9999);

                if (ordemPrioridadeServico2 > ordemPrioridadeServico1) {
                    return -1;
                } else if (ordemPrioridadeServico2 < ordemPrioridadeServico1) {
                    return 1;
                } else {
                    int ordemTitularidade1 = (TipoBeneficiarioEnum.TITULAR.equals((String) t1.getAttribute(Columns.TIB_CODIGO)) ? 1 : 2);
                    int ordemTitularidade2 = (TipoBeneficiarioEnum.TITULAR.equals((String) t2.getAttribute(Columns.TIB_CODIGO)) ? 1 : 2);

                    if (ordemTitularidade2 > ordemTitularidade1) {
                        return -1;
                    } else if (ordemTitularidade2 < ordemTitularidade1) {
                        return 1;
                    } else if (ordemPrioridadeGpFamiliarDependencia) {
                        int ordemDependencia1 = TextHelper.parseIntErrorSafe(t1.getAttribute(Columns.BFC_ORDEM_DEPENDENCIA), 9999);
                        int ordemDependencia2 = TextHelper.parseIntErrorSafe(t2.getAttribute(Columns.BFC_ORDEM_DEPENDENCIA), 9999);

                        if (ordemDependencia2 > ordemDependencia1) {
                            return -1;
                        } else if (ordemDependencia2 < ordemDependencia1) {
                            return 1;
                        } else {
                            return 0;
                        }

                    } else if (ordemPrioridadeGpFamiliarParentesco) {
                        int ordemGrauParentesco1 = TextHelper.parseIntErrorSafe(t1.getAttribute(Columns.BFC_GRP_CODIGO), 99);
                        int ordemGrauParentesco2 = TextHelper.parseIntErrorSafe(t2.getAttribute(Columns.BFC_GRP_CODIGO), 99);

                        if (ordemGrauParentesco2 > ordemGrauParentesco1) {
                            return -1;
                        } else if (ordemGrauParentesco2 < ordemGrauParentesco1) {
                            return 1;
                        } else {
                            int ordemIdade1 = (t1.getAttribute(Columns.BFC_DATA_NASCIMENTO) != null ? DateHelper.getAge((Date) t1.getAttribute(Columns.BFC_DATA_NASCIMENTO)) : 999);
                            int ordemIdade2 = (t2.getAttribute(Columns.BFC_DATA_NASCIMENTO) != null ? DateHelper.getAge((Date) t2.getAttribute(Columns.BFC_DATA_NASCIMENTO)) : 999);

                            if (ordemIdade2 > ordemIdade1) {
                                return -1;
                            } else if (ordemIdade2 < ordemIdade1) {
                                return 1;
                            } else {
                                String bfcNome1 = t1.getAttribute(Columns.BFC_NOME).toString();
                                String bfcNome2 = t2.getAttribute(Columns.BFC_NOME).toString();

                                return bfcNome1.compareTo(bfcNome2);
                            }
                        }
                    } else {
                        return 0;
                    }
                }
            }
        });

        return beneficiarios;
    }

    /**
     * Itera sobre uma lista de beneficiários (todos do mesmo titular -> ser_codigo)
     * retornando o estado civil daquele que é o titular
     * @param beneficiarios
     * @return
     */
    private String obterEstadoCivilTitular(List<TransferObject> beneficiarios) {
        for (TransferObject beneficiario : beneficiarios) {
            String tibCodigo = (String) beneficiario.getAttribute(Columns.TIB_CODIGO);
            if (TipoBeneficiarioEnum.TITULAR.equals(tibCodigo)) {
                String bfcEstadoCivil = null;
                if (!TextHelper.isNull(beneficiario.getAttribute(Columns.BFC_ESTADO_CIVIL))) {
                    // No hibernate o campo BFC_ESTADO_CIVIL está mapeado como Character pois ele pode ser null.
                    bfcEstadoCivil = ((Character) beneficiario.getAttribute(Columns.BFC_ESTADO_CIVIL)).toString();
                }
                return bfcEstadoCivil;
            }
        }
        return null;
    }

    /**
     * Filtra a lista de regras de cálculo subsídio pelas informações de órgão, benefício,
     * tipo de beneficiário, faixa salarial e faita etária.
     * @param regrasCalculoSubsidio
     * @param orgCodigo
     * @param benCodigo
     * @param tibCodigo
     * @param salarioTitular
     * @param idadeBeneficiario
     * @return
     */
    private List<TransferObject> filtrarRegrasCalculoSubsidio(List<TransferObject> regrasCalculoSubsidio, String orgCodigo, String benCodigo, String tibCodigo, String grauParentesco, String motivoDependencia, BigDecimal salarioTitular, Integer idadeBeneficiario, AcessoSistema responsavel) {
        List<TransferObject> candidatos = new ArrayList<>();
        boolean existeRegraMde = false;

        // DESENV-15617 - Possibilita o filtro de regras por motivo dependência
        if (!TextHelper.isNull(motivoDependencia)) {
            for (TransferObject regrasMde : regrasCalculoSubsidio) {
                String mdeCodigo = (String) regrasMde.getAttribute(Columns.MDE_CODIGO);
                if (!TextHelper.isNull(mdeCodigo) && mdeCodigo.equals(motivoDependencia)) {
                    existeRegraMde = true;
                    break;
                }
            }
        }

        for (TransferObject regraCalculoSubsidio : regrasCalculoSubsidio) {
            String orgCodigoRegra = (String) regraCalculoSubsidio.getAttribute(Columns.ORG_CODIGO);
            String benCodigoRegra = (String) regraCalculoSubsidio.getAttribute(Columns.BEN_CODIGO);
            String tibCodigoRegra = (String) regraCalculoSubsidio.getAttribute(Columns.TIB_CODIGO);
            String grauParentescoRegra = (String) regraCalculoSubsidio.getAttribute(Columns.GRP_CODIGO);
            String motivoDependenciaRegra = (String) regraCalculoSubsidio.getAttribute(Columns.MDE_CODIGO);
            BigDecimal faixaSalarialIni = (BigDecimal) regraCalculoSubsidio.getAttribute(Columns.CLB_FAIXA_SALARIAL_INI);
            BigDecimal faixaSalarialFim = (BigDecimal) regraCalculoSubsidio.getAttribute(Columns.CLB_FAIXA_SALARIAL_FIM);
            Short faixaEtariaIni = (Short) regraCalculoSubsidio.getAttribute(Columns.CLB_FAIXA_ETARIA_INI);
            Short faixaEtariaFim = (Short) regraCalculoSubsidio.getAttribute(Columns.CLB_FAIXA_ETARIA_FIM);

            if (orgCodigoRegra != null && !orgCodigoRegra.equals(orgCodigo)) {
                // A regra tem órgão especificado, e não é igual ao órgão passado
                continue;
            }
            if (benCodigoRegra != null && !benCodigoRegra.equals(benCodigo)) {
                // A regra tem benefício especificado, e não é igual ao benefício passado
                continue;
            }
            if (tibCodigoRegra != null && !tibCodigoRegra.equals(tibCodigo)) {
                // A regra tem tipo de beneficiário especificado, e não é igual ao tipo de beneficiário passado
                continue;
            }
            if (grauParentescoRegra != null && !grauParentescoRegra.equals(grauParentesco)) {
                // A regra tem grau parentesco especificado, e não é igual ao grau parentesco passado
                continue;
            }

            // DESENV-15617 - Possibilita o filtro de regras por motivo dependência
            if (existeRegraMde) {
                if (TextHelper.isNull(motivoDependenciaRegra) || !motivoDependenciaRegra.equals(motivoDependencia)) {
                    continue;
                }
            } else if (!TextHelper.isNull(motivoDependenciaRegra)) {
                continue;
            }

            if (faixaSalarialIni != null && faixaSalarialIni.compareTo(salarioTitular) > 0) {
                // A regra tem faixa salarial inicial especificada, e o salário do titular é menor que a faixa inicial
                continue;
            }
            if (faixaSalarialFim != null && faixaSalarialFim.compareTo(salarioTitular) < 0) {
                // A regra tem faixa salarial final especificada, e o salário do titular é maior que a faixa final
                continue;
            }
            if (faixaEtariaIni != null && faixaEtariaIni.compareTo(idadeBeneficiario.shortValue()) > 0) {
                // A regra tem faixa etária inicial especificada, e a idade do titular é menor que a faixa inicial
                continue;
            }
            if (faixaEtariaFim != null && faixaEtariaFim.compareTo(idadeBeneficiario.shortValue()) < 0) {
                // A regra tem faixa etária final especificada, e a idade do titular é maior que a faixa final
                continue;
            }

            // Se passou, significa que a regra é válida
            candidatos.add(regraCalculoSubsidio);
        }
        return candidatos;
    }

    /**
     * Atualiza o valor do subsídio do contrato de benefício e atualiza as consignações
     * associadas a este contrato, sejam elas a mensalidade e o subsídio
     * @param cbeCodigo
     * @param adeCodigoMensalidade
     * @param adeCodigoSubsidio
     * @param novoValorMensalidade
     * @param novoValorSubsidio
     * @param periodoAtual
     * @param jsonMemoriaCalculo
     * @param responsavel
     * @throws BeneficioControllerException
     */
    private void atualizarSubsidioContratoBeneficio(String cbeCodigo, String adeCodigoMensalidade, String adeCodigoSubsidio, BigDecimal novoValorMensalidade, BigDecimal novoValorSubsidio, Date periodoAtual, Date periodoReferencia, String jsonMemoriaCalculo, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            // Atualizar a tabela de contrato (tb_contrato_beneficio) com os valores da mensalidade e o novo valor do subsídio.
            ContratoBeneficio cbeBean = ContratoBeneficioHome.findByPrimaryKey(cbeCodigo);
            BigDecimal cbeValorTotalAnterior = cbeBean.getCbeValorTotal();
            BigDecimal cbeValorSubsidioAnterior = cbeBean.getCbeValorSubsidio();
            double diferencaValorTotal = novoValorMensalidade.subtract(cbeValorTotalAnterior).doubleValue();
            double diferencaValorSubsidio = novoValorSubsidio.subtract(cbeValorSubsidioAnterior).doubleValue();

            boolean alterouContratoBeneficio = false;
            String msgOcorrenciaContratoBeneficio = "";

            LOG.info("Valor calculado do diferencaValorTotal: " + diferencaValorTotal);
            LOG.info("Valor calculado do diferencaValorSubsidio: " + diferencaValorSubsidio);

            if (diferencaValorTotal != 0) {
                alterouContratoBeneficio = true;
                msgOcorrenciaContratoBeneficio += (!msgOcorrenciaContratoBeneficio.isEmpty() ? "<BR>" : "");
                msgOcorrenciaContratoBeneficio += ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.total.beneficio.alterado.arg0.de.arg1.para.arg2", responsavel,
                        (diferencaValorTotal > 0 ? NumberHelper.format(diferencaValorTotal, NumberHelper.getLang()) : "(" + NumberHelper.format(Math.abs(diferencaValorTotal), NumberHelper.getLang()) + ")"),
                        NumberHelper.format(cbeValorTotalAnterior.doubleValue(), NumberHelper.getLang()),
                        NumberHelper.format(novoValorMensalidade.doubleValue(), NumberHelper.getLang()));
            }
            if (diferencaValorSubsidio != 0) {
                alterouContratoBeneficio = true;
                msgOcorrenciaContratoBeneficio += (!msgOcorrenciaContratoBeneficio.isEmpty() ? "<BR>" : "");
                msgOcorrenciaContratoBeneficio += ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.subsidio.beneficio.alterado.arg0.de.arg1.para.arg2", responsavel,
                        (diferencaValorSubsidio > 0 ? NumberHelper.format(diferencaValorSubsidio, NumberHelper.getLang()) : "(" + NumberHelper.format(Math.abs(diferencaValorSubsidio), NumberHelper.getLang()) + ")"),
                        NumberHelper.format(cbeValorSubsidioAnterior.doubleValue(), NumberHelper.getLang()),
                        NumberHelper.format(novoValorSubsidio.doubleValue(), NumberHelper.getLang()));
            }


            // Se os valores foram alterados, atualiza o registro no banco e
            // inclui ocorrência de modificação na tb_ocorrencia_ctt_beneficio.
            if (alterouContratoBeneficio) {
                LOG.info("Inicio da atualizando os valores do contrato beneficio e criando ocorrencias.");
                // Atualiza Contrato Benefício
                cbeBean.setCbeValorTotal(novoValorMensalidade);
                cbeBean.setCbeValorSubsidio(novoValorSubsidio);
                ContratoBeneficioHome.update(cbeBean);

                // Cria ocorrência
                OcorrenciaContratoBeneficioHome.create(CodedValues.TOC_ALTERACAO_CONTRATO_BENEFICIO, cbeCodigo, null, msgOcorrenciaContratoBeneficio, responsavel);

                // Grava log do contrato benefício alterado
                LogDelegate log = new LogDelegate(responsavel, Log.CONTRATO_BENEFICIO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setContratoBeneficio(cbeCodigo);
                log.addChangedField(Columns.CBE_VALOR_TOTAL, NumberHelper.format(novoValorMensalidade.doubleValue(), NumberHelper.getLang()), NumberHelper.format(cbeValorTotalAnterior.doubleValue(), NumberHelper.getLang()));
                log.addChangedField(Columns.CBE_VALOR_SUBSIDIO, NumberHelper.format(novoValorSubsidio.doubleValue(), NumberHelper.getLang()), NumberHelper.format(cbeValorSubsidioAnterior.doubleValue(), NumberHelper.getLang()));
                log.write();

                LOG.info("Fim da atualizando os valores do contrato beneficio e criando ocorrencias.");
            }

            periodoReferencia = periodoReferencia == null ? periodoAtual : periodoReferencia;

            // Grava memória de cálculo do subsídio
            LOG.info("Inicio da gravação da memoria de calculo no banco.");
            MemoriaCalculoSubsidioHome.create(cbeCodigo, periodoReferencia, novoValorMensalidade, novoValorSubsidio, jsonMemoriaCalculo);
            LOG.info("Fim da gravação da memoria de calculo no banco.");

            AutDesconto adeBeanSubsidio = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigoSubsidio);
            BigDecimal adeVlrSubsidioAnterior = adeBeanSubsidio.getAdeVlr();
            double diferencaAdeValorSubsidio = novoValorSubsidio.subtract(adeVlrSubsidioAnterior).doubleValue();

            LOG.info("Valor calculado do diferencaAdeValorSubsidio: " + diferencaAdeValorSubsidio);

            if (diferencaAdeValorSubsidio != 0) {
                LOG.info("Inicio da atualizando os valores da autorização desconto (Subsidio) e criando ocorrencias.");
                // Atualiza o valor da Aut Desconto de Subsídio
                adeBeanSubsidio.setAdeVlr(novoValorSubsidio);
                AutDescontoHome.update(adeBeanSubsidio);

                // Obtém o tipo de valor do contrato para criação da ocorrência
                String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(adeBeanSubsidio.getAdeTipoVlr());

                // Cria ocorrência de alteração
                String msgOcorrenciaAutDesconto = ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.alterado.arg0.arg1.de.arg2.arg3.para.arg4.arg5", responsavel,
                        labelTipoVlr, (diferencaAdeValorSubsidio > 0 ? NumberHelper.format(diferencaAdeValorSubsidio, NumberHelper.getLang()) : "(" + NumberHelper.format(Math.abs(diferencaAdeValorSubsidio), NumberHelper.getLang()) + ")"),
                        labelTipoVlr, NumberHelper.format(adeVlrSubsidioAnterior.doubleValue(), NumberHelper.getLang()),
                        labelTipoVlr, NumberHelper.format(novoValorSubsidio.doubleValue(), NumberHelper.getLang()));

                // Cria ocorrência autorização
                OcorrenciaAutorizacaoHome.create(adeBeanSubsidio.getAdeCodigo(), CodedValues.TOC_ALTERACAO_CONTRATO, responsavel.getUsuCodigo(), msgOcorrenciaAutDesconto, adeVlrSubsidioAnterior, novoValorSubsidio, responsavel.getIpUsuario(), null, periodoAtual, null);

                // Grava log do contrato benefício alterado
                LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeBeanSubsidio.getAdeCodigo());
                log.setContratoBeneficio(cbeCodigo);
                log.addChangedField(Columns.ADE_VLR, NumberHelper.format(novoValorSubsidio.doubleValue(), NumberHelper.getLang()), NumberHelper.format(adeVlrSubsidioAnterior.doubleValue(), NumberHelper.getLang()));
                log.write();

                LOG.info("Fim da atualizando os valores da autorização desconto (Subsidio) e criando ocorrencias.");
            }

            // Atualizar a ADE referente ao valor que será descontado do servidor (valor mensalidade menos o valor do subsídio).
            AutDesconto adeBeanMensalidade = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigoMensalidade);
            BigDecimal adeVlrMensalidadeAnterior = adeBeanMensalidade.getAdeVlr();
            BigDecimal adeVlrMensalidadeNovo = novoValorMensalidade.subtract(novoValorSubsidio);
            double diferencaAdeValorMensalidade = adeVlrMensalidadeNovo.subtract(adeVlrMensalidadeAnterior).doubleValue();

            LOG.info("Valor calculado do diferencaAdeValorMensalidade: " + diferencaAdeValorMensalidade);

            if (diferencaAdeValorMensalidade != 0) {
                LOG.info("Inicio da atualizando os valores da autorização desconto (Mensalidade) e criando ocorrencias.");

                // Atualiza o valor da Aut Desconto de Mensalidade
                adeBeanMensalidade.setAdeVlr(adeVlrMensalidadeNovo);
                AutDescontoHome.update(adeBeanMensalidade);

                // Obtém o tipo de valor do contrato para criação da ocorrência
                String labelTipoVlr = ParamSvcTO.getDescricaoTpsTipoVlr(adeBeanMensalidade.getAdeTipoVlr());

                // Cria ocorrência de alteração
                String msgOcorrenciaAutDesconto = ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.alterado.arg0.arg1.de.arg2.arg3.para.arg4.arg5", responsavel,
                        labelTipoVlr, (diferencaAdeValorMensalidade > 0 ? NumberHelper.format(diferencaAdeValorMensalidade, NumberHelper.getLang()) : "(" + NumberHelper.format(Math.abs(diferencaAdeValorMensalidade), NumberHelper.getLang()) + ")"),
                        labelTipoVlr, NumberHelper.format(adeVlrMensalidadeAnterior.doubleValue(), NumberHelper.getLang()),
                        labelTipoVlr, NumberHelper.format(adeVlrMensalidadeNovo.doubleValue(), NumberHelper.getLang()));

                // Cria ocorrência autorização
                OcorrenciaAutorizacaoHome.create(adeBeanMensalidade.getAdeCodigo(), CodedValues.TOC_ALTERACAO_CONTRATO, responsavel.getUsuCodigo(), msgOcorrenciaAutDesconto, adeVlrMensalidadeAnterior, adeVlrMensalidadeNovo, responsavel.getIpUsuario(), null, periodoAtual, null);

                // Em caso de alteração apenas no valor da mensalidade, cria ocorrência também para o contrato de subsídio informando
                // alteração no valor da mensalidade para que o contrato de subsídio também seja enviado na exportação do movimento financeiro
                if (diferencaAdeValorSubsidio == 0) {
                    // Ocorrência de alteração do contrato de subsídio em função da alteração do valor da mensalidade
                    String msgOcorrenciaAutDescontoSubsidio = ApplicationResourcesHelper.getMessage("mensagem.informacao.valor.mensalidade.beneficio.alterado", responsavel);
                    // Cria ocorrência autorização
                    OcorrenciaAutorizacaoHome.create(adeBeanSubsidio.getAdeCodigo(), CodedValues.TOC_ALTERACAO_CONTRATO, responsavel.getUsuCodigo(), msgOcorrenciaAutDescontoSubsidio, adeVlrSubsidioAnterior, novoValorSubsidio, responsavel.getIpUsuario(), null, periodoAtual, null);
                }

                // Grava log do contrato benefício alterado
                LogDelegate log = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.UPDATE, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeBeanMensalidade.getAdeCodigo());
                log.setContratoBeneficio(cbeCodigo);
                log.addChangedField(Columns.ADE_VLR, NumberHelper.format(adeVlrMensalidadeNovo.doubleValue(), NumberHelper.getLang()), NumberHelper.format(adeVlrMensalidadeAnterior.doubleValue(), NumberHelper.getLang()));
                log.write();

                LOG.info("Fim da atualizando os valores da autorização desconto (Mensalidade) e criando ocorrencias.");
            }

            // Alterar os dados de valor total reservado e subsídio na tb_dados_autorizacao_desconto
            // (TDA_CODIGO=VALORTOTALRESERVADO e TDA_CODIGO=SUBSIDIO), pois esta tabela ainda é a principal
            // fonte de dados das rotinas de benefícios.
            LOG.info("Inicio da atualização de valores na Dados Autorização Desconto.");
            String tdaCodigo = "VALORTOTALRESERVADO";
            String dadValor = NumberHelper.format(novoValorMensalidade.doubleValue(), "en");
            autorizacaoController.setDadoAutDesconto(adeCodigoMensalidade, tdaCodigo, dadValor, responsavel);
            autorizacaoController.setDadoAutDesconto(adeCodigoSubsidio, tdaCodigo, dadValor, responsavel);

            tdaCodigo = "SUBSIDIO";
            dadValor = NumberHelper.format(novoValorSubsidio.doubleValue(), "en");
            autorizacaoController.setDadoAutDesconto(adeCodigoMensalidade, tdaCodigo, dadValor, responsavel);
            autorizacaoController.setDadoAutDesconto(adeCodigoSubsidio, tdaCodigo, dadValor, responsavel);

            LOG.info("Fim da atualização de valores na Dados Autorização Desconto.");
        } catch (FindException | UpdateException | CreateException | LogControllerException | AutorizacaoControllerException ex) {
            LOG.error(ex.getCause(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new BeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    private String montarJsonMemoriaCalculo(TransferObject beneficiario, TransferObject regraCalculoSubsidio, int ordem,
            int idadeBeneficiario, double vlrLimiteSubsidioSalario, double valorSubsidioCalculado, double valorSubsidioFinal, String tlaCodigo, String tntCodigo, AcessoSistema responsavel) {

        // Dados do servidor
        String serCodigo = (String) beneficiario.getAttribute(Columns.SER_CODIGO);
        String srsCodigo = (String) beneficiario.getAttribute(Columns.SRS_CODIGO);
        String orgCodigo = (String) beneficiario.getAttribute(Columns.ORG_CODIGO);
        BigDecimal rseSalario = (BigDecimal) beneficiario.getAttribute(Columns.RSE_SALARIO);

        // Dados do beneficiário
        String bfcNome = (String) beneficiario.getAttribute(Columns.BFC_NOME);
        Date bfcDataNasc = (Date) beneficiario.getAttribute(Columns.BFC_DATA_NASCIMENTO);
        String bfcCodigo = (String) beneficiario.getAttribute(Columns.BFC_CODIGO);
        String tibCodigo = (String) beneficiario.getAttribute(Columns.TIB_CODIGO);
        String bfcGrauParentesco = (String) beneficiario.getAttribute(Columns.BFC_GRP_CODIGO);
        Integer bfcOrdemDependencia = (Integer) beneficiario.getAttribute(Columns.BFC_ORDEM_DEPENDENCIA);
        String mdeCodigo = (String) beneficiario.getAttribute(Columns.MDE_CODIGO);

        // Dados do contrato benefício
        String cbeCodigo = (String) beneficiario.getAttribute(Columns.CBE_CODIGO);
        String adeCodigo = (String) beneficiario.getAttribute(Columns.ADE_CODIGO);
        String benCodigo = (String) beneficiario.getAttribute(Columns.BEN_CODIGO);

        // Parametros de sistema
        String psi582 = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_DEPENDENTES_SUBSIDIO, responsavel);
        String psi583 = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_SUBSIDIO_POR_DEPENDENTE, responsavel);
        String psi584 = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_SUBSIDIO_POR_TITULAR, responsavel);
        String psi585 = (String) ParamSist.getInstance().getParam(CodedValues.TPC_ORDEM_PRIORIDADE_SUBSIDIO_GRUPO_FAMILIAR, responsavel);
        String psi586 = (String) ParamSist.getInstance().getParam(CodedValues.TPC_LIMITA_VAL_SUBSIDIO_BEN_PERCENTUAL_SAL_BASE, responsavel);
        String psi587 = (String) ParamSist.getInstance().getParam(CodedValues.TPC_APLICA_SUBSIDIO_FAMILIA_SERVIDOR_INATIVO, responsavel);
        String psi588 = (String) ParamSist.getInstance().getParam(CodedValues.TPC_MENOR_FAIXA_SAL_FAMILIA_SER_INATIVO, responsavel);
        String psi589 = (String) ParamSist.getInstance().getParam(CodedValues.TPC_APLICA_SUBSIDIO_FAMILIA_SER_SALARIO_NULO, responsavel);

        // Parâmetros de serviço
        String pse256 = (String) beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_TEM_SUBSIDIO);
        String pse257 = (String) beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO);
        String pse258 = (String) beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO);
        String pse259 = (String) beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_TIPO_CALCULO_SUBSIDIO);
        String pse260 = (String) beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO);
        String pse261 = (String) beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA);
        String pse262 = (String) beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO);
        String pse263 = (String) beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO);
        String pse270 = (String) beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_DATA_LIMITE_VIGENCIA_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO);

        // Dados da regra de cálculo de subsídi
        String orgCodigoRegra = (String) regraCalculoSubsidio.getAttribute(Columns.ORG_CODIGO);
        String benCodigoRegra = (String) regraCalculoSubsidio.getAttribute(Columns.BEN_CODIGO);
        String tibCodigoRegra = (String) regraCalculoSubsidio.getAttribute(Columns.TIB_CODIGO);
        BigDecimal faixaSalarialIni = (BigDecimal) regraCalculoSubsidio.getAttribute(Columns.CLB_FAIXA_SALARIAL_INI);
        BigDecimal faixaSalarialFim = (BigDecimal) regraCalculoSubsidio.getAttribute(Columns.CLB_FAIXA_SALARIAL_FIM);
        Short faixaEtariaIni = (Short) regraCalculoSubsidio.getAttribute(Columns.CLB_FAIXA_ETARIA_INI);
        Short faixaEtariaFim = (Short) regraCalculoSubsidio.getAttribute(Columns.CLB_FAIXA_ETARIA_FIM);
        BigDecimal clbValorSubsidio = (BigDecimal) regraCalculoSubsidio.getAttribute(Columns.CLB_VALOR_SUBSIDIO);
        BigDecimal clbValorMensalidade = (BigDecimal) regraCalculoSubsidio.getAttribute(Columns.CLB_VALOR_MENSALIDADE);

        // Constrói o objeto Raiz do JSON
        JsonObjectBuilder raiz = Json.createObjectBuilder();
        JsonObjectBuilder servidorJson = Json.createObjectBuilder();
        JsonObjectBuilder beneficiarioJson = Json.createObjectBuilder();
        JsonObjectBuilder parametrosSistemaJson = Json.createObjectBuilder();
        JsonObjectBuilder parametrosServicoJson = Json.createObjectBuilder();
        JsonObjectBuilder regraCalculoJson = Json.createObjectBuilder();
        JsonObjectBuilder contratoBeneficio = Json.createObjectBuilder();

        // Adiciona os valor ao objeto servidor
        addValueToJsonObject(servidorJson, "codigo", serCodigo);
        addValueToJsonObject(servidorJson, "status", srsCodigo);
        addValueToJsonObject(servidorJson, "orgao", orgCodigo);
        addValueToJsonObject(servidorJson, "salario", rseSalario);
        addValueToJsonObject(servidorJson, "salarioLimite", vlrLimiteSubsidioSalario);

        // Adiciona ao Raiz
        raiz.add("servidor", servidorJson);

        // Adiciona os valor ao objeto beneficiario
        addValueToJsonObject(beneficiarioJson, "ordem", ordem);
        addValueToJsonObject(beneficiarioJson, "codigo", bfcCodigo);
        addValueToJsonObject(beneficiarioJson, "nome", bfcNome);
        addValueToJsonObject(beneficiarioJson, "dataNasc", bfcDataNasc);
        addValueToJsonObject(beneficiarioJson, "idadeCalculada", idadeBeneficiario);
        addValueToJsonObject(beneficiarioJson, "tipo", tibCodigo);
        addValueToJsonObject(beneficiarioJson, "grauParentesco", bfcGrauParentesco);
        addValueToJsonObject(beneficiarioJson, "ordemDependencia", bfcOrdemDependencia);
        addValueToJsonObject(beneficiarioJson, "motivoDependencia", mdeCodigo);

        // Adiciona ao Raiz
        raiz.add("beneficiario", beneficiarioJson);

        // Adiciona os valor ao objeto de parâmetros de sistema
        addValueToJsonObject(parametrosSistemaJson, "psi582", psi582);
        addValueToJsonObject(parametrosSistemaJson, "psi583", psi583);
        addValueToJsonObject(parametrosSistemaJson, "psi584", psi584);
        addValueToJsonObject(parametrosSistemaJson, "psi585", psi585);
        addValueToJsonObject(parametrosSistemaJson, "psi586", psi586);
        addValueToJsonObject(parametrosSistemaJson, "psi587", psi587);
        addValueToJsonObject(parametrosSistemaJson, "psi588", psi588);
        addValueToJsonObject(parametrosSistemaJson, "psi589", psi589);

        // Adiciona ao Raiz
        raiz.add("parametrosSistema", parametrosSistemaJson);

        // Adiciona os valor ao objeto de parâmetros de sistema
        addValueToJsonObject(parametrosServicoJson, "pse256", pse256);
        addValueToJsonObject(parametrosServicoJson, "pse257", pse257);
        addValueToJsonObject(parametrosServicoJson, "pse258", pse258);
        addValueToJsonObject(parametrosServicoJson, "pse259", pse259);
        addValueToJsonObject(parametrosServicoJson, "pse260", pse260);
        addValueToJsonObject(parametrosServicoJson, "pse261", pse261);
        addValueToJsonObject(parametrosServicoJson, "pse262", pse262);
        addValueToJsonObject(parametrosServicoJson, "pse263", pse263);
        addValueToJsonObject(parametrosServicoJson, "pse270", pse270);

        // Adiciona ao Raiz
        raiz.add("parametrosServico", parametrosServicoJson);

        // Adiciona os valor ao objeto de regra de cálculo
        addValueToJsonObject(regraCalculoJson, "orgao", orgCodigoRegra);
        addValueToJsonObject(regraCalculoJson, "beneficio", benCodigoRegra);
        addValueToJsonObject(regraCalculoJson, "tipoBeneficiario", tibCodigoRegra);
        addValueToJsonObject(regraCalculoJson, "faixaEtariaIni", faixaEtariaIni);
        addValueToJsonObject(regraCalculoJson, "faixaEtariaFim", faixaEtariaFim);
        addValueToJsonObject(regraCalculoJson, "faixaSalarialIni", faixaSalarialIni);
        addValueToJsonObject(regraCalculoJson, "faixaSalarialFim", faixaSalarialFim);
        addValueToJsonObject(regraCalculoJson, "valorMensalidade", clbValorMensalidade);
        addValueToJsonObject(regraCalculoJson, "valorSubsidio", clbValorSubsidio);

        // Adiciona ao Raiz
        raiz.add("regraCalculo", regraCalculoJson);

        // Contrato Beneficio
        addValueToJsonObject(contratoBeneficio, "contratoCodigo", cbeCodigo);
        addValueToJsonObject(contratoBeneficio, "consignacaoPrincipal", adeCodigo);
        addValueToJsonObject(contratoBeneficio, "tipoLancamento", tlaCodigo);
        addValueToJsonObject(contratoBeneficio, "tipoNatureza", tntCodigo);
        addValueToJsonObject(contratoBeneficio, "beneficio", benCodigo);

        // Adiciona ao Raiz
        raiz.add("contratoBeneficio", contratoBeneficio);

        // Adiciona os valores ao objeto Raiz
        addValueToJsonObject(raiz, "valorSubsidioCalculado", valorSubsidioCalculado);
        addValueToJsonObject(raiz, "valorSubsidioFinal", valorSubsidioFinal);

        return raiz.build().toString();
    }

    /**
     * Monta o JSon especificamente para contrato de ProRata
     * @param beneficiario
     * @param regraCalculoSubsidio
     * @param ordem
     * @param idadeBeneficiario
     * @param vlrLimiteSubsidioSalario
     * @param valorSubsidioCalculado
     * @param valorSubsidioFinal
     * @param tlaCodigo
     * @param tntCodigo
     * @param valorEntradaLancamento
     * @param valorFinalLancamento
     * @param valorTotalContratoMensalidade
     * @param valorSubsidioContratoMensalidade
     * @param dataInicialPeriodoAtual
     * @param responsavel
     * @return
     */
    private String montarJsonMemoriaCalculoProRata(TransferObject beneficiario, TransferObject regraCalculoSubsidio, int ordem,
            int idadeBeneficiario, double vlrLimiteSubsidioSalario, double valorSubsidioCalculado, double valorSubsidioFinal, String tlaCodigo,
            String tntCodigo, double valorEntradaLancamento, double valorFinalLancamento, double valorTotalContratoMensalidade, double valorSubsidioContratoMensalidade,
            Date dataInicialPeriodoAtual, AcessoSistema responsavel) {
        String json  = montarJsonMemoriaCalculo(beneficiario, regraCalculoSubsidio, ordem, idadeBeneficiario, vlrLimiteSubsidioSalario,
                valorSubsidioCalculado, valorSubsidioFinal, tlaCodigo, tntCodigo, responsavel);

        // Remonta o objeto da string.
        JsonObject antigaRaiz = Json.createReader(new StringReader(json)).readObject();
        JsonObjectBuilder novaRaiz = Json.createObjectBuilder();
        // JsonObject immutable, copio todos os antigos elementes para a nova raiz para poder adcionar
        antigaRaiz.entrySet().forEach(e -> novaRaiz.add(e.getKey(), e.getValue()));

        // Criando a estrutura para o "contratoBeneficioProRata"
        JsonObjectBuilder contratoBeneficioProRata = Json.createObjectBuilder();

        addValueToJsonObject(contratoBeneficioProRata, "valorTotalLancamento", valorEntradaLancamento);
        addValueToJsonObject(contratoBeneficioProRata, "valorFinalLancamento", valorFinalLancamento);
        addValueToJsonObject(contratoBeneficioProRata, "valorFinalSubsidio", valorSubsidioFinal);
        addValueToJsonObject(contratoBeneficioProRata, "valorTotalContratoMensalidade", valorTotalContratoMensalidade);
        addValueToJsonObject(contratoBeneficioProRata, "valorSubsidioContratoMensalidade", valorSubsidioContratoMensalidade);
        addValueToJsonObject(contratoBeneficioProRata, "periodoReferencia", dataInicialPeriodoAtual);

        novaRaiz.add("contratoBeneficioProRata", contratoBeneficioProRata);

        return novaRaiz.build().toString();
    }

    private void addValueToJsonObject(JsonObjectBuilder json, String nome, Object valor) {
        if (!TextHelper.isNull(valor)) {
            if (valor instanceof BigDecimal) {
                json.add(nome, (BigDecimal) valor);
            } else if (valor instanceof Integer) {
                json.add(nome, (Integer) valor);
            } else if (valor instanceof Long) {
                json.add(nome, (Long) valor);
            } else if (valor instanceof Double) {
                json.add(nome, (Double) valor);
            } else if (valor instanceof String) {
                json.add(nome, (String) valor);
            } else if (valor instanceof Boolean) {
                json.add(nome, (Boolean) valor);
            } else if (valor instanceof Date) {
                json.add(nome, DateHelper.format((Date) valor, "yyyy-MM-dd"));
            } else {
                json.add(nome, valor.toString());
            }
        } else {
            json.addNull(nome);
        }
    }

    /**
     * Deve ser considerada a idade que o beneficiário tinha no período (mês) anterior ao atual para definir a faixa etária.
     * Por exemplo, caso um beneficiário completar uma determinada idade em 15/Fevereiro, ele só será cobrado com o novo valor
     * em Março (considerando o período 02/Fevereiro a 01/março).
     * @param bfcDataNascimento
     * @param csaCodigo
     * @param orgCodigo
     * @param simulacao
     * @param responsavel
     * @return
     * @throws BeneficioControllerException
     */
    private int calcularIdadeBeneficiario(Date bfcDataNascimento, String csaCodigo, String orgCodigo, boolean simulacao, AcessoSistema responsavel) throws BeneficioControllerException {
        try {
            Calendar dataCalculo = Calendar.getInstance();

            String parametro = "C";

            if (!simulacao) {
                parametro = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_DATA_REFERENCIA_CALCULO_SUBSIDIO, responsavel);
            }

            if(parametro != null) {
                if (parametro.equals("P")) {
                    dataCalculo.setTime(PeriodoHelper.getInstance().getDataIniPeriodoBeneficioAtual(orgCodigo, responsavel));
                    dataCalculo.add(Calendar.DATE, -1);
                } else if (parametro.equals("M")) {
                    dataCalculo.add(Calendar.MONTH, -1);
                    dataCalculo.set(Calendar.DAY_OF_MONTH, dataCalculo.getActualMaximum(Calendar.DAY_OF_MONTH));
                } else if (parametro.equals("A")) {
                    dataCalculo.setTime(PeriodoHelper.getInstance().getPeriodoBeneficioAnterior(orgCodigo, responsavel));
                    dataCalculo.add(Calendar.DATE, -1);
                }
            }

            int idadeBeneficiario = 999;
            if (bfcDataNascimento != null) {
                if (bfcDataNascimento.after(dataCalculo.getTime())) {
                    idadeBeneficiario = 0;
                } else {
                    idadeBeneficiario = DateHelper.getAge(bfcDataNascimento, dataCalculo.getTime());
                }
            }
            return idadeBeneficiario;
        } catch (ParametroControllerException |PeriodoException ex) {
            LOG.error(ex.getCause(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new BeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Excluir contratos de benefícios de beneficiários quando não atenderem mais a condição de dependentes.
     * @param serCodigo
     * @param responsavel
     * @return
     * @throws UpdateException
     * @throws FindException
     * @throws CreateException
     * @throws HQueryException
     * @throws ContratoBeneficioControllerException
     */
    private void excluirContratoBeneficioForaRegraDependente(String serCodigo, AcessoSistema responsavel) throws UpdateException, FindException, CreateException, HQueryException, ContratoBeneficioControllerException {
        List<String> scbCodigos = new ArrayList<>();
        scbCodigos.add(StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO.getCodigo());
        scbCodigos.add(StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO_BENEFICIARIO.getCodigo());
        scbCodigos.add(StatusContratoBeneficioEnum.AGUARD_EXCLUSAO_OPERADORA.getCodigo());
        scbCodigos.add(StatusContratoBeneficioEnum.CANCELADO.getCodigo());

        String subsidioConcedidoStr = CodedValues.TPP_SIM;
        Character subsidioConcedidoChar=subsidioConcedidoStr.charAt(0);

        ListarBeneficiariosForaRegraDependenteQuery query = new ListarBeneficiariosForaRegraDependenteQuery();
        query.serCodigo = serCodigo;
        query.tntCodigos = CodedValues.TNT_BENEFICIO_MENSALIDADE;
        query.scbCodigos = scbCodigos;
        query.subsidioConcedido = subsidioConcedidoChar;
        List<TransferObject> beneficiarios = query.executarDTO();

        for(TransferObject beneficiario : beneficiarios) {
            String mdeCodigo = beneficiario.getAttribute(Columns.MDE_CODIGO) != null ? beneficiario.getAttribute(Columns.MDE_CODIGO).toString() : "";
            int idadeMaximaEstudanteSubsidio = Integer.parseInt(beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO).toString());
            int idadeMaximaDireitoSubsidio = Integer.parseInt(beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO).toString());
            String cbeCodigo = beneficiario.getAttribute(Columns.CBE_CODIGO).toString();
            Date bfcDataNascimento = (Date) beneficiario.getAttribute(Columns.BFC_DATA_NASCIMENTO);

            boolean gravaContratoBeneficio = false;

            // Define a troca do status do contrato de benefício
            if(mdeCodigo.equals(MotivoDependenciaEnum.ESTUDANTE.mdeCodigo)) {
                if((bfcDataNascimento != null ? DateHelper.getAge(bfcDataNascimento) : 999) > idadeMaximaEstudanteSubsidio) {
                    gravaContratoBeneficio = true;
                }
            } else if((bfcDataNascimento != null ? DateHelper.getAge(bfcDataNascimento) : 999) > idadeMaximaDireitoSubsidio) {
                gravaContratoBeneficio = true;
            }

            if(gravaContratoBeneficio) {
                String serEmail = beneficiario.getAttribute(Columns.SER_EMAIL).toString();
                String bfcNome = beneficiario.getAttribute(Columns.BFC_NOME).toString();
                String benDescricao = beneficiario.getAttribute(Columns.BEN_DESCRICAO).toString();
                String bfcCpf = beneficiario.getAttribute(Columns.BFC_CPF).toString();
                String csaNome = beneficiario.getAttribute(Columns.CSA_NOME).toString();

                ContratoBeneficio contrato = new ContratoBeneficio();
                contrato = ContratoBeneficioHome.findByPrimaryKey(cbeCodigo);

                contrato.setStatusContratoBeneficio(new StatusContratoBeneficio(StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO.getCodigo()));

                try {
                    TipoMotivoOperacao tipoMotivoOperacao = TipoMotivoOperacaoHome.findByAcao(AcaoEnum.CANCELAMENTO_BEN_MOTIVO_PERDA_CONDICAO_DEPENDENTE.getCodigo(), responsavel);
                    contratoBeneficioController.update(contrato, tipoMotivoOperacao.getTmoCodigo(), responsavel);
                } catch(Exception ex) {
                    throw new ContratoBeneficioControllerException("mensagem.beneficio.erro.tipo.motivo.operacao", responsavel);
                }

                if(serEmail != null && !serEmail.isEmpty()) {
                    EnviaEmailHelper.enviarEmailContratoBeneficioCancelamento(serEmail, bfcNome, benDescricao, bfcCpf, csaNome, responsavel);
                }
            }
        }
    }

    /**
     * Simular o cálculo de mensalidade e subsídio dos beneficiários selecionados para nova adesão de benefícios
     * @param rseCodigo
     * @param responsavel
     * @param benCodigo
     * @param bfcCodigos
     * @param svcCodigo
     * @return
     * @throws BeneficioControllerException
     */
    @Override
    public List<TransferObject> simularCalculoSubsidio(Map<String, List<String>> dadosSimulacao, String rseCodigo, boolean isMigracao, AcessoSistema responsavel) throws BeneficioControllerException {
        // Recupera parâmetros para cálculo de benefícios
        boolean usarMenorFaixaSalarialInativos = ParamSist.paramEquals(CodedValues.TPC_MENOR_FAIXA_SAL_FAMILIA_SER_INATIVO, CodedValues.TPC_SIM, responsavel);
        boolean calcularSubsidioSalarioNulo = ParamSist.paramEquals(CodedValues.TPC_APLICA_SUBSIDIO_FAMILIA_SER_SALARIO_NULO, CodedValues.TPC_SIM, responsavel);
        boolean ordemPrioridadeGpFamiliarDependencia = ParamSist.paramEquals(CodedValues.TPC_ORDEM_PRIORIDADE_SUBSIDIO_GRUPO_FAMILIAR, CodedValues.ORDEM_PRIORIDADE_SUBSIDIO_GP_FAMILIAR_DEPENDENCIA, responsavel);
        boolean ordemPrioridadeGpFamiliarParentesco = ParamSist.paramEquals(CodedValues.TPC_ORDEM_PRIORIDADE_SUBSIDIO_GRUPO_FAMILIAR, CodedValues.ORDEM_PRIORIDADE_SUBSIDIO_GP_FAMILIAR_GRAU_PARENTESCO, responsavel);
        int qtdMaxDependentesPodemTerSubsidio = TextHelper.parseIntErrorSafe(ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_DEPENDENTES_SUBSIDIO, responsavel), 1);
        int qtdMaxSubsidiosPorDependente = TextHelper.parseIntErrorSafe(ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_SUBSIDIO_POR_DEPENDENTE, responsavel), 1);
        int qtdMaxSubsidiosPorTitular = TextHelper.parseIntErrorSafe(ParamSist.getInstance().getParam(CodedValues.TPC_MAXIMO_SUBSIDIO_POR_TITULAR, responsavel), 1);
        boolean dependenteSubsidioIlimitado = ParamSist.paramEquals(CodedValues.TPC_DEPENDENTES_SUBSIDIO_ILIMITADO, CodedValues.TPC_SIM, responsavel);
        double percentualLimiteSubsidioSalario = TextHelper.parseDoubleErrorSafe(ParamSist.getInstance().getParam(CodedValues.TPC_LIMITA_VAL_SUBSIDIO_BEN_PERCENTUAL_SAL_BASE, responsavel), -1.0);
        boolean validaPercentualLimiteSubsidioSalario = percentualLimiteSubsidioSalario == -1.0 ? false : true;

        try {
            List<TransferObject> resultado = new ArrayList<>();
            if (dadosSimulacao != null && !dadosSimulacao.isEmpty()) {

                RegistroServidorTO registroServidor = servidorController.findRegistroServidor(rseCodigo, responsavel);
                ServidorTransferObject servidor = servidorController.findServidorByRseCodigo(rseCodigo, responsavel);

                List<String> svcCodigosUtilizados = new ArrayList<>();
                Set<String> cacheBeneficiarioComContratos = new HashSet<>();

                List<TransferObject> beneficiarioParaSeremSimulados = new ArrayList<>();

                Set<String> nseCodigosSendoSimulados = new HashSet<>();

                Set<String> bfcCodigosSendoSimulados = new HashSet<>();

                for (String key : dadosSimulacao.keySet()) {
                    Beneficio beneficio = BeneficioHome.findByPrimaryKey(key);
                    nseCodigosSendoSimulados.add(beneficio.getNaturezaServico().getNseCodigo());
                }

                // Já tento buscar todos os contratos da pessoa que estou simulando.
                ListarBeneficiariosCalculoSubsidioQuery queryBeneficiarios = new ListarBeneficiariosCalculoSubsidioQuery();
                queryBeneficiarios.rseCodigo = rseCodigo;
                queryBeneficiarios.tntCodigos = CodedValues.TNT_BENEFICIO_MENSALIDADE;
                queryBeneficiarios.scbCodigos = Arrays.asList(StatusContratoBeneficioEnum.SOLICITADO.getCodigo(),
                        StatusContratoBeneficioEnum.AGUARD_INCLUSAO_OPERADORA.getCodigo(),
                        StatusContratoBeneficioEnum.ATIVO.getCodigo(),
                        StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO.getCodigo(),
                        StatusContratoBeneficioEnum.CANCELAMENTO_SOLICITADO_BENEFICIARIO.getCodigo(),
                        StatusContratoBeneficioEnum.AGUARD_EXCLUSAO_OPERADORA.getCodigo());
                queryBeneficiarios.ignoraPeriodo = true;
                List<TransferObject> beneficiariosComContratos = queryBeneficiarios.executarDTO();

                // Populando o cache
                for (TransferObject beneficiarioComContrato : beneficiariosComContratos) {
                    String bfcCodigo = beneficiarioComContrato.getAttribute(Columns.BFC_CODIGO).toString();
                    String nseCodigo = beneficiarioComContrato.getAttribute(Columns.NSE_CODIGO).toString();
                    String svcCodigo = beneficiarioComContrato.getAttribute(Columns.SVC_CODIGO).toString();

                    if (isMigracao && nseCodigosSendoSimulados.contains(nseCodigo)) {
                        continue;
                    }

                    // Já coloco o objeto "pronto" na "memoria" para ser validado as regras do casos de uso.
                    beneficiarioComContrato.setAttribute("contratoVirtual", false);
                    beneficiarioParaSeremSimulados.add(beneficiarioComContrato);
                    svcCodigosUtilizados.add(svcCodigo);

                    cacheBeneficiarioComContratos.add(bfcCodigo + nseCodigo);
                }

                // Com base nos benenficio selecionado procuro os serviços que vão fazer partes da simulação para o "titular"
                List<String> benCodigos = new ArrayList<>();
                List<String> svcCodigosTitular = new ArrayList<>();
                for (Map.Entry<String,List<String>> entry : dadosSimulacao.entrySet()) {
                    benCodigos.add(entry.getKey());
                    // Verifica se o benefício possui relacionamento com serviços para titular
                    List<BeneficioServico> servicosTitular = RelacionamentoBeneficioServicoHome.findByBenCodigoTibCodigo(entry.getKey(), TipoBeneficiarioEnum.TITULAR.tibCodigo);
                    if (servicosTitular == null || servicosTitular.isEmpty()) {
                        throw new BeneficioControllerException("mensagem.erro.calculo.subsidio.beneficio.servico.nao.encontrado", responsavel);
                    } else {
                        // Considerar o serviço do titular para buscar as parametrizações necessárias para realizar os cálculos
                        svcCodigosTitular.add(servicosTitular.get(0).getServico().getSvcCodigo());
                    }

                    List<String> bfcCodigos = entry.getValue();
                    bfcCodigosSendoSimulados.addAll(bfcCodigos);
                }

                // Calcula a ordem de dependencia dos beneficiários
                if (ordemPrioridadeGpFamiliarDependencia) {
                    LOG.info("Iniciando o calculo da ordem de dependencia do beneficiarios");
                    calcularOrdemDependenciaBeneficiario("RSE", Arrays.asList(rseCodigo), new ArrayList<>(bfcCodigosSendoSimulados), true, responsavel);
                    LOG.info("Fim do calculo da ordem de dependencia do beneficiarios");
                }

                // Recupera os dados dos benefícios selecionados com os parâmetros de serviço necessários.
                // A lista é ordenada pelo parâmetro de serviço de prioridade de subsídio.
                ListarBeneficioEscolhidoQuery listarBeneficioEscolhidoQuery = new ListarBeneficioEscolhidoQuery();
                listarBeneficioEscolhidoQuery.benCodigos = benCodigos;
                listarBeneficioEscolhidoQuery.svcCodigos = svcCodigosTitular;
                List<TransferObject> listaBeneficioTO = listarBeneficioEscolhidoQuery.executarDTO();

                // Cria map para armazenar o somatório dos valores a pagar por beneficiário
                Map<String, BigDecimal> valorTotalAPagarPorBfc = new HashMap<>();
                // Valor referente ao percentual do salário que pode ser comprometido por beneficiário
                double vlrMaximoDescontoPorBeneficiario = 0.00;

                // Para cada beneficio encontrado vamos montar os beneficiarios necessarios para a simulação
                for (TransferObject beneficioTO : listaBeneficioTO) {
                    String benCodigo = (String) beneficioTO.getAttribute(Columns.BEN_CODIGO);
                    String csaCodig = (String) beneficioTO.getAttribute(Columns.CSA_CODIGO);
                    String tntCodigo = (String) beneficioTO.getAttribute(Columns.TNT_CODIGO);

                    List<String> bfcCodigos = dadosSimulacao.get(benCodigo);

                    String nseCodigo = beneficioTO.getAttribute(Columns.NSE_CODIGO).toString();


                    List<BeneficioServico> servicosAgregados = new ArrayList<>();
                    boolean utilizarServicosDiferentes = false;

                    // Recupera o relacionamento de serviços com o benefício informado para simulação de dependentes
                    List<BeneficioServico> servicosDependente = RelacionamentoBeneficioServicoHome.findByBenCodigoTibCodigo(benCodigo, TipoBeneficiarioEnum.DEPENDENTE.tibCodigo);
                    servicosDependente.sort((o1,o2) -> o1.getBseOrdem().compareTo(o2.getBseOrdem()));

                    if (ParamSist.paramEquals(CodedValues.TPC_MOD_BENEFICIO_PERMITE_AGREGADO, CodedValues.TPC_SIM, responsavel)) {
                        // Recupera o relacionamento de serviços com o benefício informado para simulação de Agregado
                        servicosAgregados = RelacionamentoBeneficioServicoHome.findByBenCodigoTibCodigo(benCodigo, TipoBeneficiarioEnum.AGREGADO.tibCodigo);

                        if (servicosAgregados.isEmpty()) {
                            throw new BeneficioControllerException("mensagem.erro.simulacao.beneficio.servico.nao.encontrado.agregado", responsavel);
                        }

                        servicosAgregados.sort((o1, o2) -> o1.getBseOrdem().compareTo(o2.getBseOrdem()));

                        if(servicosAgregados.equals(servicosDependente)) {
                            // Removendo os serviços que já estão sendo utilizados
                            if (svcCodigosUtilizados != null && !svcCodigosUtilizados.isEmpty()) {
                                servicosDependente = servicosPermitidos(servicosDependente, svcCodigosUtilizados);
                            }
                        } else if (!servicosAgregados.equals(servicosDependente)) {
                            servicosDependente = servicosPermitidos(servicosDependente, svcCodigosUtilizados);
                            servicosAgregados = servicosPermitidos(servicosAgregados, svcCodigosUtilizados);
                            utilizarServicosDiferentes = true;
                        }

                    } else // Removendo os serviços que já estão sendo utilizados
                    if (svcCodigosUtilizados != null && !svcCodigosUtilizados.isEmpty()) {
                        servicosDependente = servicosPermitidos(servicosDependente, svcCodigosUtilizados);
                    }

                    // Lista os beneficiários selecionados na tela para simulação.
                    ListarBeneficiariosEscolhidosQuery listarBeneficiariosEscolhidosQuery = new ListarBeneficiariosEscolhidosQuery();
                    listarBeneficiariosEscolhidosQuery.bfcCodigos = bfcCodigos;
                    List<TransferObject> beneficiariosTO = listarBeneficiariosEscolhidosQuery.executarDTO();

                    int proximoSvc = 0;
                    for (TransferObject beneficiario : beneficiariosTO) {
                        String bfcCodigo = beneficiario.getAttribute(Columns.BFC_CODIGO).toString();
                        String tibCodigo = beneficiario.getAttribute(Columns.TIB_CODIGO).toString();

                        if (cacheBeneficiarioComContratos.contains(bfcCodigo + nseCodigo)) {
                            continue;
                        }

                        beneficiario.setAttribute("contratoVirtual", true);
                        beneficiario.setAttribute(Columns.BEN_CODIGO, benCodigo);
                        beneficiario.setAttribute(Columns.CSA_CODIGO, csaCodig);
                        beneficiario.setAttribute(Columns.NSE_CODIGO, nseCodigo);
                        beneficiario.setAttribute(Columns.TNT_CODIGO, tntCodigo);

                        // Recupera o serviço que será utilizado para solicitação do benefício
                        String svcCodigoSolicitacao = "";
                        BeneficioServico svc = new BeneficioServico();
                        if (TipoBeneficiarioEnum.TITULAR.equals(tibCodigo)) {
                            svcCodigoSolicitacao = (String) beneficioTO.getAttribute(Columns.SVC_CODIGO);
                        } else {
                            try {
                                if (utilizarServicosDiferentes && TipoBeneficiarioEnum.AGREGADO.tibCodigo.equals(beneficiario.getAttribute(Columns.TIB_CODIGO))) {
                                    svc = servicosAgregados.get(proximoSvc++);
                                } else {
                                    svc = servicosDependente.get(proximoSvc++);
                                }
                                svcCodigoSolicitacao = svc.getServico().getSvcCodigo();
                            } catch (IndexOutOfBoundsException ex) {
                                LOG.error(ex.getCause(), ex);
                                throw new BeneficioControllerException("mensagem.erro.calculo.subsidio.beneficio.servico.nao.encontrado", responsavel);
                            }
                        }

                        // Buscando os dados do serviço
                        ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigoSolicitacao, responsavel);

                        beneficiario.setAttribute(Columns.SVC_CODIGO, svcCodigoSolicitacao);
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_EST_SUBSIDIO, paramSvcCse.getTpsIdadeMaxDependenteEstSubsidio());
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_IDADE_MAX_DEPENDENTE_DIREITO_SUBSIDIO, paramSvcCse.getTpsIdadeMaxDependenteDireitoSubsidio());
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_AGREGADO_PODE_TER_SUBSIDIO, paramSvcCse.getTpsAgregadoPodeTerSubsidio());
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO, paramSvcCse.getTpsPaiMaeTitularesDivorciadosSubsidio());
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO, paramSvcCse.getTpsOrdemPrioridadeSubsidio());
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA, paramSvcCse.getTpsQtdeSubsidioPorNatureza());
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_TIPO_CALCULO_SUBSIDIO, paramSvcCse.getTpsTipoCalculoSubsidio());
                        beneficiario.setAttribute(Columns.PSE_VLR + CodedValues.TPS_PAI_MAE_TITULARES_DIVORCIADOS_SUBSIDIO, paramSvcCse.getTpsDataLimiteVigenciaPaiMaeTitularesDivorciadosSubsidio());

                        beneficiarioParaSeremSimulados.add(beneficiario);
                    }
                }

                if (beneficiarioParaSeremSimulados.isEmpty()) {
                    return new ArrayList<>();
                }

                if (responsavel.isSer() && ParamSist.paramEquals(CodedValues.TPC_MOD_BENEFICIO_PERMITE_AGREGADO, CodedValues.TPC_NAO, responsavel)) {
                    beneficiarioParaSeremSimulados = filtrarBeneficiariosDireitoSubsidio(beneficiarioParaSeremSimulados, registroServidor.getOrgCodigo(), true, beneficiarioParaSeremSimulados.get(0).getAttribute(Columns.CSA_CODIGO).toString(), responsavel);
                }

                beneficiarioParaSeremSimulados = ordenarBeneficiariosDireitoSubsidio(beneficiarioParaSeremSimulados, ordemPrioridadeGpFamiliarDependencia, ordemPrioridadeGpFamiliarParentesco, responsavel);

                // Controles sobre os subsídios concedidos
                int qtdSubsidioConcedidoTitular = 0;
                Map<String, Integer> qtdSubsidioConcedidoPorDependente = new HashMap<>();
                Map<String, Map<String, Integer>> qtdSubsidioConcedidoPorNatureza = new HashMap<>();

                int ordem = 0;
                for(TransferObject beneficiario : beneficiarioParaSeremSimulados) {
                    ordem++;
                    LOG.info("Calculando subsídio para beneficiário \"" + beneficiario.getAttribute(Columns.BFC_NOME) + "\" (" + ordem + "):");
                    String benCodigo = beneficiario.getAttribute(Columns.BEN_CODIGO).toString();
                    String bfcCodigo = beneficiario.getAttribute(Columns.BFC_CODIGO).toString();
                    String bfcCpf = beneficiario.getAttribute(Columns.BFC_CPF).toString();
                    String tibCodigo = beneficiario.getAttribute(Columns.TIB_CODIGO).toString();
                    String nseCodigo = beneficiario.getAttribute(Columns.NSE_CODIGO).toString();
                    String csaCodigo = beneficiario.getAttribute(Columns.CSA_CODIGO).toString();
                    Date bfcDataNascimento = (Date) beneficiario.getAttribute(Columns.BFC_DATA_NASCIMENTO);
                    String subsidioConcedido = beneficiario.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO) != null ? beneficiario.getAttribute(Columns.BFC_SUBSIDIO_CONCEDIDO).toString() : CodedValues.TPC_NAO;
                    String mdeCodigo = (String) beneficiario.getAttribute(Columns.MDE_CODIGO);
                    String tntCodigo = (String) beneficiario.getAttribute(Columns.TNT_CODIGO);

                    int idadeBeneficiario = calcularIdadeBeneficiario(bfcDataNascimento, csaCodigo, registroServidor.getOrgCodigo(), true, responsavel);

                    String grauParentesco ="";
                    //DESENV-14450 - Cálculo benefício passa a filtrar por grau parentesco
                    if (!tibCodigo.equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                        grauParentesco = beneficiario.getAttribute(Columns.BFC_GRP_CODIGO).toString();
                    }

                    boolean quantidadeSubsidioExcedida = false;

                    String serCpf = servidor.getAttribute(Columns.SER_CPF).toString();

                    // Salário utilizado para a busca das regras de cálculo
                    BigDecimal rseSalario = (BigDecimal) registroServidor.getAttribute(Columns.RSE_SALARIO);
                    if (rseSalario == null || rseSalario.signum() <= 0) {
                        if (!calcularSubsidioSalarioNulo) {
                            throw new BeneficioControllerException("mensagem.erro.calculo.subsidio.salario.invalido", responsavel, serCpf);
                        } else {
                            rseSalario = BigDecimal.valueOf(0.01);
                        }
                    }
                    vlrMaximoDescontoPorBeneficiario = rseSalario.doubleValue() * (percentualLimiteSubsidioSalario / 100.0);

                    // Salário utilizado para a busca das regras de cálculo
                    BigDecimal salarioTitular = rseSalario;
                    if (usarMenorFaixaSalarialInativos && CodedValues.SRS_BLOQUEADOS.contains(registroServidor.getSrsCodigo())) {
                        salarioTitular = BigDecimal.valueOf(0.01);
                    }

                    if (TipoBeneficiarioEnum.TITULAR.equals(tibCodigo)) {
                        // Verifica número máximo de benefícios com subsídio que o titular pode ter.
                        if (qtdSubsidioConcedidoTitular >= qtdMaxSubsidiosPorTitular) {
                            // Interromper a aplicação do subsídio para o titular quando o limite for atingido.
                            quantidadeSubsidioExcedida = true;
                        }
                    } else {
                        // Verifica número máximo de dependentes que podem ter subsídio (titular não entra na contagem).
                        if (qtdSubsidioConcedidoPorDependente.size() >= qtdMaxDependentesPodemTerSubsidio && !(dependenteSubsidioIlimitado && subsidioConcedido.equals(CodedValues.TPC_SIM))) {
                            // Interromper a aplicação do subsídio para o grupo familiar quando o limite for atingido.
                            quantidadeSubsidioExcedida = true;
                        }

                        // Verifica número máximo de benefícios com subsídio que um dependente pode ter.
                        if (qtdSubsidioConcedidoPorDependente.get(bfcCodigo) != null &&
                                qtdSubsidioConcedidoPorDependente.get(bfcCodigo) >= qtdMaxSubsidiosPorDependente && !(dependenteSubsidioIlimitado && subsidioConcedido.equals(CodedValues.TPC_SIM))) {
                            // Interromper a aplicação do subsídio para o beneficiário quando o limite for atingido.
                            quantidadeSubsidioExcedida = true;
                        }
                    }

                    // Verificar se o beneficiário (incluindo titular) pode ter mais de um subsídio para serviços da mesma natureza.
                    int qtdMaxSubsidioConcedidoMesmaNatureza = TextHelper.parseIntErrorSafe(beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_QTDE_SUBSIDIO_POR_NATUREZA), -1);
                    if (qtdMaxSubsidioConcedidoMesmaNatureza != -1 && qtdSubsidioConcedidoPorNatureza.containsKey(bfcCodigo)) {
                        Map<String, Integer> subsidioConcedidoPorNatureza = qtdSubsidioConcedidoPorNatureza.get(bfcCodigo);
                        if (subsidioConcedidoPorNatureza.get(nseCodigo) != null && subsidioConcedidoPorNatureza.get(nseCodigo) >= qtdMaxSubsidioConcedidoMesmaNatureza) {
                            // Interromper a aplicação do subsídio para o beneficiário quando o limite for atingido.
                            quantidadeSubsidioExcedida = true;
                        }
                    }

                    // Verificar se o valor do subsídio configurado na tb_calculo_beneficio é em moeda (R$), em percentual
                    // sobre o salário do titular ou em percentual sobre o valor do benefício.
                    String formaCalculoSubsidio = (String) beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_TIPO_CALCULO_SUBSIDIO);

                    ListarTabelaCalculoSubsidioQuery queryCalculo = new ListarTabelaCalculoSubsidioQuery();
                    queryCalculo.orgCodigo = registroServidor.getOrgCodigo();
                    queryCalculo.benCodigo = benCodigo;
                    queryCalculo.simulacao = true;
                    List<TransferObject> regrasCalculoSubsidio = queryCalculo.executarDTO();

                    // Filtrar os valores na tabela de cálculo de benefício por: Órgão do titular, tipo do beneficiário, código do benefício, faixa salarial e faixa etária.
                    List<TransferObject> regrasSubsidioBeneficio = filtrarRegrasCalculoSubsidio(regrasCalculoSubsidio, registroServidor.getOrgCodigo(), benCodigo, tibCodigo, grauParentesco, mdeCodigo, salarioTitular, idadeBeneficiario, responsavel);

                    // Caso mais de um registro seja retornado na pesquisa, interromper o processo e informar erro de configuração na tabela de cálculo de benefício.
                    if (regrasSubsidioBeneficio == null || regrasSubsidioBeneficio.isEmpty()) {
                        throw new BeneficioControllerException("mensagem.erro.simulacao.calculo.beneficio.nenhuma.regra.encontradas", responsavel, bfcCpf);
                    } else if (regrasSubsidioBeneficio.size() != 1) {
                        throw new BeneficioControllerException("mensagem.erro.simulacao.calculo.beneficio.multiplas.regras.encontradas", responsavel, bfcCpf);
                    }

                    BigDecimal clbValorSubsidio = (BigDecimal) regrasSubsidioBeneficio.get(0).getAttribute(Columns.CLB_VALOR_SUBSIDIO);
                    BigDecimal clbValorMensalidade = (BigDecimal) regrasSubsidioBeneficio.get(0).getAttribute(Columns.CLB_VALOR_MENSALIDADE);

                    if (quantidadeSubsidioExcedida) {
                        clbValorSubsidio = new BigDecimal("0.00");
                    }
                    double valorSubsidio = 0;
                    if (CodedValues.PSE_TIPO_CALCULO_SUBSIDIO_VALOR.equals(formaCalculoSubsidio)) {
                        valorSubsidio = clbValorSubsidio.doubleValue();
                    } else if (CodedValues.PSE_TIPO_CALCULO_SUBSIDIO_PERC_SALARIO.equals(formaCalculoSubsidio)) {
                        valorSubsidio = rseSalario.multiply(clbValorSubsidio.divide(BigDecimal.valueOf(100))).doubleValue();
                    } else if (CodedValues.PSE_TIPO_CALCULO_SUBSIDIO_PERC_BENEFICIO.equals(formaCalculoSubsidio)) {
                        valorSubsidio = clbValorMensalidade.multiply(clbValorSubsidio.divide(BigDecimal.valueOf(100))).doubleValue();
                    } else if (CodedValues.PSE_TIPO_CALCULO_SUBSIDIO_PERC_DESCONTO_SALARIO.equals(formaCalculoSubsidio)) {
                        double valorSubsidioTemp = clbValorMensalidade.subtract(rseSalario.multiply(clbValorSubsidio.divide(BigDecimal.valueOf(100)))).doubleValue();
                        valorSubsidio = valorSubsidioTemp < 0 || valorSubsidioTemp == clbValorMensalidade.doubleValue() ? 0 : valorSubsidioTemp;
                    }

                    LOG.info("Subsídio calculado: " + valorSubsidio);
                    BigDecimal novoValorSubsidio = BigDecimal.valueOf(valorSubsidio).setScale(2, RoundingMode.HALF_UP);

                    // Atualiza o contador de subsídios concedidos por titular e dependentes
                    if (TipoBeneficiarioEnum.TITULAR.equals(tibCodigo)) {
                        qtdSubsidioConcedidoTitular++;
                    } else {
                        Integer qtd = qtdSubsidioConcedidoPorDependente.get(bfcCodigo);
                        qtd = (qtd == null ? 1 : qtd + 1);
                        qtdSubsidioConcedidoPorDependente.put(bfcCodigo, qtd);
                    }

                    // Atualiza o contador de subsídios concedidos por natureza de serviço
                    Map<String, Integer> subsidioConcedidoPorNatureza = qtdSubsidioConcedidoPorNatureza.get(bfcCodigo);
                    if (subsidioConcedidoPorNatureza == null) {
                        subsidioConcedidoPorNatureza = new HashMap<>();
                        qtdSubsidioConcedidoPorNatureza.put(bfcCodigo, subsidioConcedidoPorNatureza);
                    }
                    Integer qtd = subsidioConcedidoPorNatureza.get(nseCodigo);
                    qtd = (qtd == null ? 1 : qtd + 1);
                    subsidioConcedidoPorNatureza.put(nseCodigo, qtd);

                    // Somatório dos valores a pagar por beneficiário
                    if (validaPercentualLimiteSubsidioSalario) {
                        BigDecimal total = new BigDecimal("0.00");
                        if (valorTotalAPagarPorBfc != null && !valorTotalAPagarPorBfc.isEmpty() && valorTotalAPagarPorBfc.get(bfcCodigo) != null) {
                            total = valorTotalAPagarPorBfc.get(bfcCodigo);
                        }
                        BigDecimal mensalidade = clbValorMensalidade;
                        BigDecimal subsidio = novoValorSubsidio;
                        total = total.add(mensalidade.subtract(subsidio)).setScale(2,java.math.RoundingMode.HALF_UP);
                        valorTotalAPagarPorBfc.put(bfcCodigo, total);
                    }


                    TransferObject resultadoTO = new CustomTransferObject();
                    resultadoTO.setAttribute(Columns.BEN_CODIGO, benCodigo);
                    resultadoTO.setAttribute("PRIORIDADE", beneficiario.getAttribute(Columns.PSE_VLR + CodedValues.TPS_ORDEM_PRIORIDADE_SUBSIDIO));
                    resultadoTO.setAttribute(Columns.BFC_CODIGO, beneficiario.getAttribute(Columns.BFC_CODIGO).toString());
                    resultadoTO.setAttribute("VALOR_MENSALIDADE", clbValorMensalidade);
                    resultadoTO.setAttribute("VALOR_SUBSIDIO", novoValorSubsidio);
                    resultadoTO.setAttribute("SVC_CODIGO", beneficiario.getAttribute(Columns.SVC_CODIGO));
                    resultadoTO.setAttribute(Columns.TIB_CODIGO, tibCodigo);
                    resultadoTO.setAttribute(Columns.NSE_CODIGO, nseCodigo);
                    resultadoTO.setAttribute("contratoVirtual", beneficiario.getAttribute("contratoVirtual"));
                    resultadoTO.setAttribute(Columns.CSA_CODIGO, csaCodigo);
                    resultadoTO.setAttribute(Columns.TNT_CODIGO, tntCodigo);
                    resultado.add(resultadoTO);
                }

                // Verificando se o parâmetro de percentual máximo de desconto sobre o salário está ativo.
                // Caso esteja ativo, verfificar se o somatório do valor a pagar por beneficiário passou do teto definido no parâmetro.
                if (validaPercentualLimiteSubsidioSalario) {
                    // Ordena os benefícios simulados pela prioridade
                    resultado.sort((o1,o2) -> ((String) o1.getAttribute("PRIORIDADE")).compareTo((String) o2.getAttribute("PRIORIDADE")));
                    // Convertendo o valor de decimal para BigDecimal
                    BigDecimal vlrMaximoDescontoBigDecimal = new BigDecimal(vlrMaximoDescontoPorBeneficiario).setScale(2,java.math.RoundingMode.HALF_UP);

                    List<TransferObject> resultadoAlterado = new ArrayList<>();

                    // verifica se o valor total a pagar por benefíciário excede ao percentual definido no parâmetro
                    for (String bfcCodigo : valorTotalAPagarPorBfc.keySet()) {
                        BigDecimal valorTotalBeneficiario = valorTotalAPagarPorBfc.get(bfcCodigo);
                        if (valorTotalBeneficiario.compareTo(vlrMaximoDescontoBigDecimal) > 0) {
                            // Valor que ultrapassou o máximo permitido para desconto por beneficiário
                            BigDecimal valorAcimaTetoSalario = valorTotalBeneficiario.subtract(vlrMaximoDescontoBigDecimal).setScale(2,java.math.RoundingMode.HALF_UP);
                            boolean vlrLimiteConsumido = false;
                            // O valor que ultrapassou o limite deve ser adicionado ao subsídio dos contratos do beneficiário pela ordem
                            // de prioridade configurada em cada benefício
                            for (TransferObject resultadoTO : resultado) {
                                String tntCodigo = (String) resultadoTO.getAttribute(Columns.TNT_CODIGO);
                                if (resultadoTO.getAttribute(Columns.BFC_CODIGO).equals(bfcCodigo)) {
                                    if (valorAcimaTetoSalario.compareTo(new BigDecimal("0.00")) >= 0) {
                                        BigDecimal mensalidade = (BigDecimal) resultadoTO.getAttribute("VALOR_MENSALIDADE");
                                        BigDecimal subsidio = (BigDecimal) resultadoTO.getAttribute("VALOR_SUBSIDIO");

                                        if (subsidio.add(valorAcimaTetoSalario).compareTo(mensalidade) >= 0) {
                                            valorAcimaTetoSalario = valorAcimaTetoSalario.subtract(mensalidade.subtract(subsidio));
                                            subsidio = new BigDecimal(mensalidade.toString());
                                        } else if(!TextHelper.isNull(tntCodigo) && tntCodigo.equals(CodedValues.TNT_MENSALIDADE_PLANO_SAUDE) && mensalidade.subtract(subsidio).compareTo(vlrMaximoDescontoBigDecimal) >=0) {
                                            subsidio = mensalidade.subtract(vlrMaximoDescontoBigDecimal).setScale(2,java.math.RoundingMode.HALF_UP);
                                            valorAcimaTetoSalario = new BigDecimal("0.00");
                                            vlrLimiteConsumido = true;
                                        } else if(!TextHelper.isNull(tntCodigo) && tntCodigo.equals(CodedValues.TNT_MENSALIDADE_ODONTOLOGICO) && vlrLimiteConsumido) {
                                            subsidio = mensalidade;
                                            valorAcimaTetoSalario = new BigDecimal("0.00");
                                        } else {
                                            subsidio = subsidio.add(valorAcimaTetoSalario).setScale(2,java.math.RoundingMode.HALF_UP);
                                            valorAcimaTetoSalario = new BigDecimal("0.00");
                                        }

                                        // atualiza o resultado
                                        resultadoTO.setAttribute("VALOR_MENSALIDADE", mensalidade);
                                        resultadoTO.setAttribute("VALOR_SUBSIDIO", subsidio);
                                        resultadoAlterado.add(resultadoTO);
                                    }
                                }
                            }
                        }
                    }
                    // Atualiza o resultado
                    if (resultadoAlterado!= null && !resultadoAlterado.isEmpty()) {
                        List<TransferObject> resultadoFinal = new ArrayList<>();
                        for (TransferObject resultadoTO : resultado) {
                            String benCodigo = (String) resultadoTO.getAttribute(Columns.BEN_CODIGO);
                            String bfcCodigo = (String) resultadoTO.getAttribute(Columns.BFC_CODIGO);
                            boolean alterouValor = false;
                            for (TransferObject resultadoAlteradoTO : resultadoAlterado) {
                                if (resultadoAlteradoTO.getAttribute(Columns.BEN_CODIGO).equals(benCodigo) &&
                                        resultadoAlteradoTO.getAttribute(Columns.BFC_CODIGO).equals(bfcCodigo)) {
                                    alterouValor = true;
                                    resultadoFinal.add(resultadoAlteradoTO);
                                }
                            }
                            if (!alterouValor) {
                                resultadoFinal.add(resultadoTO);
                            }
                        }
                        resultado = resultadoFinal;
                    }
                }
            }
            return resultado;
        } catch (HQueryException | ServidorControllerException | FindException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new BeneficioControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (BeneficioControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw ex;
        }
    }

    private List<BeneficioServico> servicosPermitidos(List<BeneficioServico> servicosPermitidos, List<String> svcCodigosUtilizados) {
        Iterator<BeneficioServico> it = servicosPermitidos.iterator();
        while (it.hasNext()) {
            BeneficioServico bse = it.next();
            if (svcCodigosUtilizados.contains(new String(bse.getServico().getSvcCodigo()))) {
                it.remove();
            }
        }
        return servicosPermitidos;
    }
}
