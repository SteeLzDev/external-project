package com.zetra.econsig.service.sdp;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.DespesaIndividualControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.LogControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.PeriodoException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.DespesaIndividualHome;
import com.zetra.econsig.persistence.entity.EnderecoConjHabitacional;
import com.zetra.econsig.persistence.entity.EnderecoConjuntoHabitacionalHome;
import com.zetra.econsig.persistence.entity.OcorrenciaDespIndividual;
import com.zetra.econsig.persistence.entity.OcorrenciaDespesaIndividualHome;
import com.zetra.econsig.persistence.entity.Permissionario;
import com.zetra.econsig.persistence.entity.PermissionarioHome;
import com.zetra.econsig.persistence.entity.Plano;
import com.zetra.econsig.persistence.entity.PlanoHome;
import com.zetra.econsig.persistence.entity.PostoRegistroServidor;
import com.zetra.econsig.persistence.entity.PostoRegistroServidorHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoQuery;
import com.zetra.econsig.persistence.query.sdp.despesaindividual.ListaDespesaTaxaUsoAtualizacaoQuery;
import com.zetra.econsig.service.consignacao.AlterarConsignacaoController;
import com.zetra.econsig.service.consignacao.CancelarConsignacaoController;
import com.zetra.econsig.service.consignacao.LiquidarConsignacaoController;
import com.zetra.econsig.service.consignacao.ReservarMargemController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: DespesaIndividualControllerBean</p>
 * <p>Description: Session Bean para a operações relacionada a Despesa Individual.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class DespesaIndividualControllerBean implements DespesaIndividualController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(DespesaIndividualControllerBean.class);

    @Autowired
    @Qualifier("reservarMargemController")
    private ReservarMargemController reservarMargemController;

    @Autowired
    private AlterarConsignacaoController alterarConsignacaoController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private LiquidarConsignacaoController liquidarConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    protected String criaOcorrencia(String adeCodigo, String tocCodigo, String odiObs, AcessoSistema responsavel) throws DespesaIndividualControllerException {
        try {
            OcorrenciaDespIndividual ocorrencia = OcorrenciaDespesaIndividualHome.create(adeCodigo, tocCodigo, responsavel.getUsuCodigo(), responsavel.getIpUsuario(), odiObs);
            return ocorrencia.getOdiCodigo();
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new DespesaIndividualControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public String createDespesaIndividual(TransferObject despesaIndividual, ReservarMargemParametros margemParam, AcessoSistema responsavel) throws DespesaIndividualControllerException {
        try {
            String plaCodigo = despesaIndividual.getAttribute(Columns.DEI_PLA_CODIGO).toString();
            String prmCodigo = despesaIndividual.getAttribute(Columns.DEI_PRM_CODIGO).toString();
            String decCodigo = !TextHelper.isNull(despesaIndividual.getAttribute(Columns.DEI_DEC_CODIGO)) ? despesaIndividual.getAttribute(Columns.DEI_DEC_CODIGO).toString() : null;
            Date decDataRetroativa = (!TextHelper.isNull(despesaIndividual.getAttribute("decDataRetroativa")) ? (Date) despesaIndividual.getAttribute("decDataRetroativa") : null);

            Permissionario permissionario = PermissionarioHome.findByPrimaryKey(prmCodigo);
            if (TextHelper.isNull(decDataRetroativa)) {
                if (!permissionario.getPrmAtivo().equals(CodedValues.STS_ATIVO)) {
                    throw new DespesaIndividualControllerException("mensagem.erro.incluir.despesa.individual.permissionario.bloqueado", responsavel);
                }
            } else if (!(decDataRetroativa.compareTo(permissionario.getPrmDataOcupacao()) >= 0 && decDataRetroativa.compareTo(!TextHelper.isNull(permissionario.getPrmDataDesocupacao()) ? permissionario.getPrmDataDesocupacao() : DateHelper.getSystemDate()) <= 0)) {
                throw new DespesaIndividualControllerException("mensagem.erro.incluir.despesa.individual.permissionario", responsavel);
            }

            // Verifica se o registro servidor informado corresponde ao permissionário
            if (!permissionario.getRegistroServidor().getRseCodigo().equals(margemParam.getRseCodigo())) {
                throw new DespesaIndividualControllerException("mensagem.erro.incluir.despesa.individual.permissionario.servidor.invalido", responsavel);
            }

            // Verifica se o plano está ativo
            Plano plano = PlanoHome.findByPrimaryKey(plaCodigo);
            if (!plano.getPlaAtivo().equals(CodedValues.STS_ATIVO)) {
                throw new DespesaIndividualControllerException("mensagem.erro.incluir.despesa.comum.plano.bloqueado", responsavel);
            }

            // Seleciona os parâmetros do plano
            Map<String, String> parametrosPlano = parametroController.getParamPlano(plaCodigo, responsavel);
            boolean planoExclusaoAutomatica = (!parametrosPlano.containsKey(CodedValues.TPP_EXCLUSAO_AUTOMATICA) || parametrosPlano.get(CodedValues.TPP_EXCLUSAO_AUTOMATICA).equals(CodedValues.TPP_SIM));

            BigDecimal adeVlr = margemParam.getAdeVlr();
            Integer adePrazo = margemParam.getAdePrazo();
            String adeIndice = margemParam.getAdeIndice();

            // Valida se o convênio está ativo
            Convenio convenio = ConvenioHome.findByPrimaryKey(margemParam.getCnvCodigo());
            if (!convenio.getStatusConvenio().getScvCodigo().equals(CodedValues.SCV_ATIVO)) {
                throw new DespesaIndividualControllerException("mensagem.erro.incluir.despesa.individual.convenio.bloqueado", responsavel);
            }

            // Valida se o convênio pertence ao plano selecionado
            if (!plano.getServico().getSvcCodigo().equals(convenio.getServico().getSvcCodigo())) {
                throw new DespesaIndividualControllerException("mensagem.erro.incluir.despesa.individual.servico.plano.invalido", responsavel);
            }

            // Validar parâmetros do plano
            String svcCodigo = plano.getServico().getSvcCodigo();
            parametroController.validaParametrosPlanoDesconto(plaCodigo, svcCodigo, adeVlr, adePrazo, adeIndice, responsavel);

            // validação de prazo para permissionário que não está ativo
            // Se a exclusão é automática ou a despesa comum é indeterminada, a despesa individual terá prazo equivalente em meses
            // entre a data da despesa comum e a data de desocupação.
            BigDecimal adeVlrProporcional = new BigDecimal("0");
            boolean lancaDespesaProporcional = false;
            boolean lancaSomenteDespesaProporcional = false;
            if (!permissionario.getPrmAtivo().equals(CodedValues.STS_ATIVO) && (planoExclusaoAutomatica || adePrazo == null)) {
                // calcula prazo entre a data da despesa e a data da desocupação do permissionário
                BigDecimal prazoEmDias = new BigDecimal(DateHelper.dayDiff(permissionario.getPrmDataDesocupacao(), decDataRetroativa));
                BigDecimal mes = new BigDecimal("30");
                BigDecimal prazoLimite = prazoEmDias.divide(mes, 2, java.math.RoundingMode.UP);

                if (adePrazo == null || prazoLimite.compareTo(new BigDecimal(adePrazo)) < 0) {
                    // calcula meses e dias avulsos
                    BigDecimal[] resultado = prazoEmDias.divideAndRemainder(mes);
                    adePrazo = resultado[0].intValue();
                    // atualiza prazo para parcelas com valor integral
                    margemParam.setAdePrazo(adePrazo);
                    BigDecimal diasRestantes = resultado[1];
                    if (diasRestantes.compareTo(new BigDecimal("0")) > 0) {
                        // calcula valor proporcional para os dias restantes
                        adeVlrProporcional = adeVlr.divide(mes, 9, java.math.RoundingMode.DOWN).multiply(diasRestantes);
                        adeVlrProporcional = adeVlrProporcional.setScale(2, java.math.RoundingMode.DOWN);
                        if (adeVlrProporcional != null && adeVlrProporcional.compareTo(new BigDecimal("0.00")) > 0) {
                            lancaDespesaProporcional = true;
                        }
                        // se o prazo não possuir meses inteiros deve incluir apenas a despesa proporcional
                        if (prazoLimite.compareTo(new BigDecimal("1")) < 0) {
                            adePrazo = 0;
                            lancaSomenteDespesaProporcional = true;
                        }
                    }
                }
            }

            // Cria autorização desconto
            String adeCodigo = "";
            if (lancaDespesaProporcional) {
                // lanca a despesa individual proporcional para o permissionário
                ReservarMargemParametros margemParamProporcional = new ReservarMargemParametros();
                margemParamProporcional.setAdePrazo(1);
                margemParamProporcional.setAdeCarencia(adePrazo); // lança a despesa proporcional no final
                margemParamProporcional.setComSerSenha(Boolean.FALSE);
                margemParamProporcional.setAdeTipoVlr(margemParam.getAdeTipoVlr());
                margemParamProporcional.setAdeIntFolha(margemParam.getAdeIntFolha());
                margemParamProporcional.setAdeIncMargem(margemParam.getAdeIncMargem());
                margemParamProporcional.setAdeIndice(margemParam.getAdeIndice());
                margemParamProporcional.setAdeIdentificador(margemParam.getAdeIdentificador());
                margemParamProporcional.setValidar(Boolean.FALSE);
                margemParamProporcional.setPermitirValidacaoTaxa(Boolean.TRUE);
                margemParamProporcional.setSerAtivo(Boolean.TRUE);
                margemParamProporcional.setCnvAtivo(Boolean.TRUE);
                margemParamProporcional.setSerCnvAtivo(Boolean.TRUE);
                margemParamProporcional.setSvcAtivo(Boolean.TRUE);
                margemParamProporcional.setCsaAtivo(Boolean.TRUE);
                margemParamProporcional.setOrgAtivo(Boolean.TRUE);
                margemParamProporcional.setEstAtivo(Boolean.TRUE);
                margemParamProporcional.setCseAtivo(Boolean.TRUE);
                margemParamProporcional.setAcao("RESERVAR");
                margemParamProporcional.setNomeResponsavel(responsavel.getUsuNome());
                margemParamProporcional.setRseCodigo(margemParam.getRseCodigo());
                margemParamProporcional.setAdeVlr(adeVlrProporcional);
                margemParamProporcional.setCnvCodigo(margemParam.getCnvCodigo());

                adeCodigo = reservarMargemController.reservarMargem(margemParamProporcional, responsavel);

                // Associa a autorização desconto criada a despesa individual
                DespesaIndividualHome.create(adeCodigo, plaCodigo, prmCodigo, decCodigo);

                // Criar ocorrência de criação de despesa individual
                criaOcorrencia(adeCodigo, CodedValues.TOC_INCLUSAO_DESPESA_INDIVIDUAL, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.odi.obs.inclusao.despesa.individual", responsavel), responsavel);

                LogDelegate logDelegate = new LogDelegate(responsavel, Log.DESPESA_INDIVIDUAL, Log.CREATE, Log.LOG_INFORMACAO);
                logDelegate.setAutorizacaoDesconto(adeCodigo);
                logDelegate.setPlano(plaCodigo);
                logDelegate.setPermissionario(prmCodigo);
                logDelegate.setDespesaComum(decCodigo);
                logDelegate.write();
            }

            if (!lancaSomenteDespesaProporcional) {
                adeCodigo = reservarMargemController.reservarMargem(margemParam, responsavel);

                // Associa a autorização desconto criada a despesa individual
                DespesaIndividualHome.create(adeCodigo, plaCodigo, prmCodigo, decCodigo);

                // Criar ocorrência de criação de despesa individual
                criaOcorrencia(adeCodigo, CodedValues.TOC_INCLUSAO_DESPESA_INDIVIDUAL, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.odi.obs.inclusao.despesa.individual", responsavel), responsavel);

                LogDelegate logDelegate = new LogDelegate(responsavel, Log.DESPESA_INDIVIDUAL, Log.CREATE, Log.LOG_INFORMACAO);
                logDelegate.setAutorizacaoDesconto(adeCodigo);
                logDelegate.setPlano(plaCodigo);
                logDelegate.setPermissionario(prmCodigo);
                logDelegate.setDespesaComum(decCodigo);
                logDelegate.write();
            }
            return adeCodigo;

        } catch (LogControllerException | CreateException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DespesaIndividualControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (AutorizacaoControllerException | ParametroControllerException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DespesaIndividualControllerException(ex);
        }
    }

    @Override
    public void alterarTaxaUso(String adeCodigo, BigDecimal adeVlr, AcessoSistema responsavel) throws DespesaIndividualControllerException {
        try {
            AutDesconto autorizacao = AutDescontoHome.findByPrimaryKey(adeCodigo);

            Integer adePrazo = null;
            String adeIdentificador = autorizacao.getAdeIdentificador();
            String adeIndice = autorizacao.getAdeIndice();
            BigDecimal adeVlrTac = null;
            BigDecimal adeVlrIof = null;
            BigDecimal adeVlrLiquido = null;
            BigDecimal adeVlrMensVinc = null;
            BigDecimal adeTaxaJuros = null;
            BigDecimal adeVlrSegPrestamista = null;
            Integer adeCarencia = null;
            String serLogin = null;
            String serSenha = null;

            AlterarConsignacaoParametros alterarParam = new AlterarConsignacaoParametros(adeCodigo, adeVlr, adePrazo, adeIdentificador, adeIndice, adeVlrTac, adeVlrIof, adeVlrLiquido, adeVlrMensVinc, adeTaxaJuros, adeVlrSegPrestamista, adeCarencia, serLogin, serSenha);
            alterarParam.setAdePeriodicidade(autorizacao.getAdePeriodicidade());

            if (responsavel.isSistema()) {
                alterarParam.setPermiteAltEntidadesBloqueadas(true);
            }

            alterarConsignacaoController.alterar(alterarParam, responsavel);

        } catch (AutorizacaoControllerException | FindException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new DespesaIndividualControllerException("mensagem.erro.alterar.taxa.uso.arg0", responsavel, e, e.getMessage());
        }
    }

    @Override
    public void alterarTaxaUsoByRse(String rseCodigo, AcessoSistema responsavel) throws DespesaIndividualControllerException {
        try {
            if (TextHelper.isNull(rseCodigo)) {
                throw new DespesaIndividualControllerException("mensagem.erro.recalculo.taxa.uso.informacao.ausente", responsavel);
            }

            // Buscar as despesas de taxa de uso ligadas ao registro servidor que pode possuir mais de um permissionário
            ListaDespesaTaxaUsoAtualizacaoQuery query = new ListaDespesaTaxaUsoAtualizacaoQuery();
            query.rseCodigo = rseCodigo;
            List<TransferObject> lista = query.executarDTO();

            if (lista != null && !lista.isEmpty()) {
                for (TransferObject autorizacao : lista) {
                    try {
                        String adeCodigo = autorizacao.getAttribute(Columns.ADE_CODIGO).toString();
                        String echCodigo = autorizacao.getAttribute(Columns.ECH_CODIGO).toString();
                        BigDecimal valorTaxaUsoAtual = (BigDecimal) autorizacao.getAttribute(Columns.ADE_VLR);

                        // Calculo do valor da taxa de uso deve ser considerado o posto do permissionario
                        // Se o endereço for condomínio, utiliza a taxa de uso de condomínio
                        RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(rseCodigo);
                        if (registroServidor.getPostoRegistroServidor() != null) {
                            PostoRegistroServidor posto = PostoRegistroServidorHome.findByPrimaryKey(registroServidor.getPostoRegistroServidor().getPosCodigo());
                            EnderecoConjHabitacional endereco = EnderecoConjuntoHabitacionalHome.findByPrimaryKey(echCodigo);
                            BigDecimal valorSoldo = posto.getPosVlrSoldo();
                            BigDecimal taxaUso = endereco.getEchCondominio().equals(CodedValues.TPC_SIM) ? posto.getPosPercTxUsoCond() : posto.getPosPercTxUso();

                            BigDecimal valorTaxaUso = valorSoldo.multiply(taxaUso).divide(new BigDecimal(100), 2, java.math.RoundingMode.DOWN);

                            // Se o novo valor da taxa de uso for diferente do valor atual, atualiza o valor da taxa de uso
                            if (valorTaxaUso.compareTo(valorTaxaUsoAtual) != 0) {
                                if (valorTaxaUso.compareTo(BigDecimal.ZERO) < 0) {
                                    throw new DespesaIndividualControllerException("mensagem.erro.recalculo.taxa.uso.generico", responsavel);
                                }

                                // Alterar os contratos de taxa de uso
                                alterarTaxaUso(adeCodigo, valorTaxaUso, responsavel);
                            }
                        } else {
                            LOG.warn("Taxa de uso do permissionário não foi recalculada pois o posto não encontrado.");
                        }
                    } catch (FindException e) {
                        LOG.warn("Taxa de uso do permissionário não foi recalculada pois o servidor, posto ou endereço não encontrado. " + e.getMessage());
                    }
                }
            }

        } catch (HQueryException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new DespesaIndividualControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public void cancelaDespesaIndividual(String adeCodigo, AcessoSistema responsavel) throws DespesaIndividualControllerException {
        try {
            AutDesconto autorizacao = AutDescontoHome.findByPrimaryKey(adeCodigo);
            Long adeNumero = autorizacao.getAdeNumero();
            String sadCodigo = autorizacao.getStatusAutorizacaoDesconto().getSadCodigo();
            RegistroServidor registroServidor = RegistroServidorHome.findByPrimaryKey(autorizacao.getRegistroServidor().getRseCodigo());
            String orgCodigo = registroServidor.getOrgao().getOrgCodigo();

            Date dataInicial = autorizacao.getAdeAnoMesIni();
            Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(orgCodigo, responsavel);

            int diferencaDias = DateHelper.dayDiff(periodoAtual, dataInicial);
            boolean cancela = true;
            if (diferencaDias > 0 && !sadCodigo.equals(CodedValues.SAD_AGUARD_DEFER) && !sadCodigo.equals(CodedValues.SAD_AGUARD_CONF)) {
                cancela = false;
            }

            if (!sadCodigo.equals(CodedValues.SAD_CANCELADA) && !sadCodigo.equals(CodedValues.SAD_LIQUIDADA) && !sadCodigo.equals(CodedValues.SAD_CONCLUIDO) && !sadCodigo.equals(CodedValues.SAD_ENCERRADO) && !sadCodigo.equals(CodedValues.SAD_INDEFERIDA)) {
                if (sadCodigo.equals(CodedValues.SAD_AGUARD_CONF) || sadCodigo.equals(CodedValues.SAD_AGUARD_DEFER) || sadCodigo.equals(CodedValues.SAD_DEFERIDA) || sadCodigo.equals(CodedValues.SAD_EMANDAMENTO) || sadCodigo.equals(CodedValues.SAD_ESTOQUE) || sadCodigo.equals(CodedValues.SAD_ESTOQUE_MENSAL)) {
                    if (cancela) {
                        cancelarConsignacaoController.cancelar(adeCodigo, responsavel);
                    } else {
                        liquidarConsignacaoController.liquidar(adeCodigo, null, null, responsavel);
                    }
                } else {
                    throw new DespesaIndividualControllerException("mensagem.erro.cancelar.despesa.individual.status.invalido", responsavel, String.valueOf(adeNumero));
                }
            }

        } catch (PeriodoException | FindException | IllegalStateException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new DespesaIndividualControllerException("mensagem.erroInternoSistema", responsavel, e);
        } catch (AutorizacaoControllerException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw new DespesaIndividualControllerException(e);
        }
    }

    @Override
    public void cancelaDespesasIndividuais(String prmCodigo, AcessoSistema responsavel) throws DespesaIndividualControllerException {
        try {
            List<TransferObject> despesasIndividuais = lstDespesasIndividuais(prmCodigo, true, responsavel);

            String adeCodigo = null;
            for (TransferObject despesaIndividual : despesasIndividuais) {
                adeCodigo = despesaIndividual.getAttribute(Columns.ADE_CODIGO).toString();
                cancelaDespesaIndividual(adeCodigo, responsavel);
            }
        } catch (DespesaIndividualControllerException e) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(e.getMessage(), e);
            throw e;
        }
    }

    private List<TransferObject> lstDespesasIndividuais(String prmCodigo, boolean planoExclusaoAutomatica, AcessoSistema responsavel) throws DespesaIndividualControllerException {
        try {
            ListaConsignacaoQuery listaConsignacaoQuery = new ListaConsignacaoQuery(responsavel);
            listaConsignacaoQuery.tipo = responsavel.getTipoEntidade();
            listaConsignacaoQuery.codigo = responsavel.getCodigoEntidade();
            listaConsignacaoQuery.tipoOperacao = "cons_despesa_permissionario";
            listaConsignacaoQuery.prmCodigo = prmCodigo;
            if (planoExclusaoAutomatica) {
                listaConsignacaoQuery.planoExclusaoAutomatica = planoExclusaoAutomatica;
            }

            return listaConsignacaoQuery.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DespesaIndividualControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
    public List<TransferObject> findDespesasIndividuais(String decCodigo, AcessoSistema responsavel) throws DespesaIndividualControllerException {
        try {
            ListaConsignacaoQuery listaConsignacaoQuery = new ListaConsignacaoQuery(responsavel);
            listaConsignacaoQuery.tipo = responsavel.getTipoEntidade();
            listaConsignacaoQuery.codigo = responsavel.getCodigoEntidade();
            listaConsignacaoQuery.tipoOperacao = "cons_despesa_permissionario";
            listaConsignacaoQuery.decCodigo = decCodigo;

            return listaConsignacaoQuery.executarDTO();
        } catch (HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new DespesaIndividualControllerException(ex);
        }
    }
}
