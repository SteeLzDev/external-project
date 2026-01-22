package com.zetra.econsig.service.consignacao;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.assembler.RegistroServidorDtoAssembler;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.AlterarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.LiquidarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.RenegociarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.FinanciamentoDividaControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametrosException;
import com.zetra.econsig.exception.SaldoDevedorControllerException;
import com.zetra.econsig.helper.consignacao.AutorizacaoHelper;
import com.zetra.econsig.helper.consignacao.SimulacaoHelper;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.job.process.ProcessaEnvioEmailEntidadesAltAde;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Coeficiente;
import com.zetra.econsig.persistence.entity.CoeficienteDesconto;
import com.zetra.econsig.persistence.entity.CoeficienteDescontoHome;
import com.zetra.econsig.persistence.entity.CoeficienteHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.HistoricoMargemRse;
import com.zetra.econsig.persistence.entity.OrgaoHome;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodoHome;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.Servidor;
import com.zetra.econsig.persistence.entity.ServidorHome;
import com.zetra.econsig.persistence.query.consignacao.ListaConsignacaoRenegociavelNativeQuery;
import com.zetra.econsig.persistence.query.consignacao.ListaContratosRenegociacaoLiberaMargemQuery;
import com.zetra.econsig.persistence.query.convenio.ObtemConsignatariasPorAdeCodigoQuery;
import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentosQuery;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.financiamentodivida.FinanciamentoDividaController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.values.CanalEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoEConsigEnum;
import com.zetra.econsig.values.StatusCompraEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;

/**
 * <p>Title: RenegociarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Renegociação de Contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service("renegociarConsignacaoController")
@Transactional
public class RenegociarConsignacaoControllerBean extends ReservarMargemControllerBean implements RenegociarConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(RenegociarConsignacaoControllerBean.class);

    @Autowired
    private AlterarConsignacaoController alterarConsignacaoController;

    @Autowired
    private FinanciamentoDividaController financiamentoDividaController;

    @Autowired
    private LiquidarConsignacaoController liquidarConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private SaldoDevedorController saldoDevedorController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;
    
    @Autowired
    private ConsignatariaController consignatariaController;
    
    @Autowired
    private ServidorController servidorController;

    @Override
    public String renegociar(RenegociarConsignacaoParametros renegociarParam, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            renegociarParam.checkNotNullSafe();
        } catch(final ParametrosException pe) {
            throw new AutorizacaoControllerException(pe);
        }

        final Map<String, Object> parametros = renegociarParam.getParametros();
        String cnvCodigo = renegociarParam.getCnvCodigo();
        final String cnvCodigoOriginal = cnvCodigo;
        final List<String> adeCodigos = renegociarParam.getAdeCodigosRenegociacao();
        final String rseCodigo = renegociarParam.getRseCodigo();
        final BigDecimal adeVlr = renegociarParam.getAdeVlr();
        final String corCodigo = renegociarParam.getCorCodigo();
        final Integer adePrazo = renegociarParam.getAdePrazo();
        final Boolean validaPrazo = renegociarParam.getValidaPrazo();
        Integer adeCarencia = renegociarParam.getAdeCarencia();
        final String adeIdentificador = renegociarParam.getAdeIdentificador();
        final boolean comSerSenha = renegociarParam.getComSerSenha();
        final boolean serAtivo = renegociarParam.getSerAtivo() != null ? renegociarParam.getSerAtivo() : true;
        final String adeIndice = renegociarParam.getAdeIndice();
        BigDecimal adeVlrTac = renegociarParam.getAdeVlrTac();
        BigDecimal adeVlrIof = renegociarParam.getAdeVlrIof();
        final BigDecimal adeVlrLiquido = renegociarParam.getAdeVlrLiquido();
        final BigDecimal adeVlrMensVinc = renegociarParam.getAdeVlrMensVinc();
        final BigDecimal adeTaxaJuros = renegociarParam.getAdeTaxaJuros();
        final boolean compraContrato = renegociarParam.getCompraContrato();
        final BigDecimal adeVlrSegPrestamista = renegociarParam.getAdeVlrSegPrestamista();
        final Timestamp adeDtHrOcorrencia = renegociarParam.getAdeDtHrOcorrencia();
        String adePeriodicidade = renegociarParam.getAdePeriodicidade();
        BigDecimal margemRestInicial = BigDecimal.ZERO;

        if (TextHelper.isNull(adePeriodicidade)) {
            adePeriodicidade = PeriodoHelper.getPeriodicidadeFolha(responsavel);
        }
        if (PeriodoHelper.folhaMensal(responsavel) && !CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
            throw new AutorizacaoControllerException("mensagem.erro.ade.periodicidade.invalida", responsavel);
        }

        final String nomeAnexo = renegociarParam.getNomeAnexo();
        final String idAnexo = renegociarParam.getIdAnexo();
        final String aadDescricao = renegociarParam.getAadDescricao();

        boolean verificaMargem = true;

        try {
            List<HistoricoMargemRse> historicosMargem = null;

            // Dados do convênio
            Convenio convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
            String svcCodigo = convenio.getServico().getSvcCodigo();
            final String orgCodigo = convenio.getOrgao().getOrgCodigo();
            final String csaCodigo = convenio.getConsignataria().getCsaCodigo();

            // Se tem relacionamento para compartilhamento de taxas, verifica em qual serviço/convênio
            // o servidor ainda não tem contratos
            final boolean temRelacionamentoCompTaxas = ParamSist.getBoolParamSist(CodedValues.TPC_TEM_COMPARTILHAMENTO_TAXAS_POR_SVC, responsavel);
            try {
                if (temRelacionamentoCompTaxas) {
                    // Se o serviço atual é o destino de um relacionamento, então pega o código do serviço
                    // de origem, pois será nele que o cadastro de taxas existirá
                    final ListaRelacionamentosQuery queryRel = new ListaRelacionamentosQuery();
                    queryRel.tntCodigo = CodedValues.TNT_COMPARTILHA_CADASTRO_TAXAS;
                    queryRel.svcCodigoOrigem = null;
                    queryRel.svcCodigoDestino = svcCodigo;
                    final List<TransferObject> svcCodigosRel = queryRel.executarDTO();
                    if ((svcCodigosRel != null) && (svcCodigosRel.size() > 0)) {
                        final CustomTransferObject cto = (CustomTransferObject) svcCodigosRel.get(0);
                        final String svcCodigoRel = cto.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM).toString();
                        if (!svcCodigoRel.equals(svcCodigo)) {
                            convenio = ConvenioHome.findByChave(svcCodigoRel, csaCodigo, orgCodigo);
                            svcCodigo = convenio.getServico().getSvcCodigo();
                            cnvCodigo = convenio.getCnvCodigo();
                        }
                    }
                }
            } catch (final Exception ex) {
                LOG.error(ex.getMessage(), ex);
            }

            final RegistroServidorTO rseTo = RegistroServidorDtoAssembler.createDto(RegistroServidorHome.findByPrimaryKey(rseCodigo), true);

            // Se não valida situação do servidor e ele está excluído, caso a consignatária permita inclusão
            // para servidor excluído via Host-Host, não valida a margem pois esta estará inválida.
            if (!serAtivo && rseTo.isExcluido() && CanalEnum.SOAP.equals(responsavel.getCanal())) {
                final String pcsVlr = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_PERMITE_INC_ADE_RSE_EXCLUIDO_HOST_A_HOST, responsavel);
                if (!TextHelper.isNull(pcsVlr) && "S".equalsIgnoreCase(pcsVlr)) {
                    verificaMargem = false;
                }
            }

            // Validação da renegociação
            final BigDecimal vlrTotalCompradoRenegociado = validaRenegociacao(adeCodigos, rseCodigo, adeVlr, corCodigo, responsavel, adePrazo,
                                                                        adeCarencia, adePeriodicidade, adeIdentificador, cnvCodigo, comSerSenha, adeIndice,
                                                                        adeVlrTac, adeVlrIof, adeVlrLiquido, adeVlrMensVinc, verificaMargem, adeTaxaJuros, compraContrato, false, serAtivo, rseTo, parametros);

            // DESENV-16086 - Necessário armazenar a margem restante antes do recálculo para verificar se irá ou não criar ocorrência 190 para contratos de compra
            if (verificaMargem) {
                margemRestInicial = obtemMargemRestante(rseTo, renegociarParam.getAdeIncMargem(), csaCodigo, true, adeCodigos, responsavel);
            }

            // Validação do processo de financiamento de dívida de cartão
            try {
                financiamentoDividaController.validarConclusaoFinanciamento(renegociarParam, svcCodigo, responsavel);
            } catch (final FinanciamentoDividaControllerException ex) {
                throw new AutorizacaoControllerException(ex);
            }

            final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

            // Verifica se o serviço permite renegociação
            final boolean permiteRenegociacao = paramSvcCse.isTpsPermiteRenegociacao();
            if (!permiteRenegociacao) {
                throw new AutorizacaoControllerException("mensagem.erro.servico.nao.permitido.renegociacao", responsavel);
            }

            final String adeTipoVlr = paramSvcCse.getTpsTipoVlr();
            final Short adeIntFolha = paramSvcCse.getTpsIntegraFolha();
            Short adeIncMargem = paramSvcCse.getTpsIncideMargem();
            final boolean preservaDataMaisAntigaRenegociacao = paramSvcCse.isTpsPreservaDataMaisAntigaReneg();
            final boolean preservaDataRenegociacao = paramSvcCse.isTpsPreservaDataRenegociacao();
            final boolean validaTaxa = paramSvcCse.isTpsValidarTaxaJuros();

            // Valor para inclusão além da margem
            final BigDecimal vlrLimiteSemMargem = (!TextHelper.isNull(paramSvcCse.getTpsVlrLimiteAdeSemMargem()) ? new BigDecimal(paramSvcCse.getTpsVlrLimiteAdeSemMargem()) : new BigDecimal("0.00"));

            // Número de contratos envolvidos na compra/renegociação
            int qtdMaxContratos = Integer.MAX_VALUE;

            try {
                if (compraContrato && !TextHelper.isNull(paramSvcCse.getTpsQtdeMaxAdeCompra())) {
                    qtdMaxContratos = Integer.parseInt(paramSvcCse.getTpsQtdeMaxAdeCompra());
                } else if (!compraContrato && !TextHelper.isNull(paramSvcCse.getTpsQtdeMaxAdeRenegociacao())) {
                    qtdMaxContratos = Integer.parseInt(paramSvcCse.getTpsQtdeMaxAdeRenegociacao());
                }
            } catch (final NumberFormatException ex) {
                if (compraContrato) {
                    throw new AutorizacaoControllerException("mensagem.erro.limite.contratos.compra", responsavel);
                } else {
                    throw new AutorizacaoControllerException("mensagem.erro.valor.incorreto.para.parametro.que.limita.numero.contratos.na.renegociacao", responsavel);
                }
            }

            // verifica se há limite de prazo específico para renegociação/portabilidade.
            Integer maxPrazoRenegociacao = ((paramSvcCse.getTpsMaxPrazoRenegociacao() != null) && !"".equals(paramSvcCse.getTpsMaxPrazoRenegociacao())) ? Integer.parseInt(paramSvcCse.getTpsMaxPrazoRenegociacao()) : null;
            Integer maxPrazo = ((paramSvcCse.getTpsMaxPrazo() != null) && !"".equals(paramSvcCse.getTpsMaxPrazo())) ? Integer.parseInt(paramSvcCse.getTpsMaxPrazo()) : null;


            // Se o serviço ou servidor tem prazo máximo, porém é contrato quinzenal, a unidade do prazo máximo
            // será em meses, portanto ao comparar com o contrato quinzenal, deve multiplicar por 2
            if (!PeriodoHelper.folhaMensal(responsavel) && !CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adePeriodicidade)) {
            	if (maxPrazoRenegociacao != null){
            		maxPrazoRenegociacao = ((maxPrazoRenegociacao > 1) && (maxPrazoRenegociacao != Integer.MAX_VALUE)) ? PeriodoHelper.converterPrazoMensalEmPeriodicidade(maxPrazoRenegociacao, responsavel) : maxPrazoRenegociacao;
            	}
            	if (maxPrazo != null){
            		maxPrazo = ((maxPrazo > 1) && (maxPrazo != Integer.MAX_VALUE)) ? PeriodoHelper.converterPrazoMensalEmPeriodicidade(maxPrazo, responsavel) : maxPrazo;
            	}
            }


            if ((maxPrazoRenegociacao != null) && (maxPrazoRenegociacao.intValue() > 0) && (adePrazo != null && adePrazo.intValue() > maxPrazoRenegociacao.intValue())) {
                if (compraContrato) {
                    throw new AutorizacaoControllerException("mensagem.compra.consignacao.prazo.maior.limite", responsavel, String.valueOf(maxPrazoRenegociacao));
                } else {
                    throw new AutorizacaoControllerException("mensagem.renegociar.consignacao.prazo.maior.limite", responsavel, String.valueOf(maxPrazoRenegociacao));
                }
            } else if (TextHelper.isNull(maxPrazoRenegociacao) && (maxPrazo != null) && (maxPrazo.intValue() > 0) && (adePrazo != null && adePrazo.intValue() > maxPrazo.intValue())) {
                throw new AutorizacaoControllerException("mensagem.reserva.prazo.maior.limite", responsavel, String.valueOf(maxPrazo));
            }

            if (adeCodigos.size() > qtdMaxContratos) {
                if (compraContrato) {
                    throw new AutorizacaoControllerException("mensagem.erro.limite.contratos.compra.operacao", responsavel, String.valueOf(qtdMaxContratos));
                } else {
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.renegociar.mais.que.arg0.contratos.por.operacao", responsavel, String.valueOf(qtdMaxContratos));
                }
            }

            // Limita o valor do contrato ao somatório dos demais contratos comprados ou renegociados
            final boolean limitaValorASomaDosDemaisContratos =
                    (compraContrato && paramSvcCse.isTpsVlrMaxCompraIgualSomaContratos()) ||
                    (!compraContrato && paramSvcCse.isTpsVlrMaxRenegIgualSomaContratos());

            // Limita o prazo do contrato ao maior prazo restante dos demais contratos comprados ou renegociados
            final boolean limitaPrazoAoMaiorDosDemaisContratos =
                    (compraContrato && paramSvcCse.isTpsPrzMaxCompraIgualMaiorContratos()) ||
                    (!compraContrato && paramSvcCse.isTpsPrzMaxRenegIgualMaiorContratos());

            // Limita o valor liberado do novo contrato ao somatório do saldo devedor dos demais contratos comprados ou renegociados
            final boolean limitaSaldoDevedorRefin =
                    (compraContrato && paramSvcCse.isTpsLimitaSaldoDevedorAnteriorCompra()) ||
                    (!compraContrato && paramSvcCse.isTpsLimitaSaldoDevedorAnteriorReneg());

            // Margem de erro para a a limitação do valor liberado do novo contrato ao somatório do saldo devedor dos demais contratos comprados ou renegociados
            BigDecimal margemErroSuperiorLimiteSaldoRefin = new BigDecimal("1.00");
            BigDecimal margemErroInferiorLimiteSaldoRefin = new BigDecimal("0.00");

            try {
                if (compraContrato) {
                    margemErroSuperiorLimiteSaldoRefin = (!TextHelper.isNull(paramSvcCse.getTpsMargemErroLimiteSaldoAntCompra()) ? new BigDecimal(1.00 + (Double.valueOf(paramSvcCse.getTpsMargemErroLimiteSaldoAntCompra()) / 100.00)).setScale(2, java.math.RoundingMode.HALF_UP) : new BigDecimal("1.00"));
                    margemErroInferiorLimiteSaldoRefin = (!TextHelper.isNull(paramSvcCse.getTpsMargemErroLimiteSaldoAntCompraRef()) ? new BigDecimal(1.00 - (Double.valueOf(paramSvcCse.getTpsMargemErroLimiteSaldoAntCompraRef()) / 100.00)).setScale(2, java.math.RoundingMode.HALF_UP) : new BigDecimal("0.00"));
                } else {
                    margemErroSuperiorLimiteSaldoRefin = (!TextHelper.isNull(paramSvcCse.getTpsMargemErroLimiteSaldoAntReneg()) ? new BigDecimal(1.00 + (Double.valueOf(paramSvcCse.getTpsMargemErroLimiteSaldoAntReneg()) / 100.00)).setScale(2, java.math.RoundingMode.HALF_UP) : new BigDecimal("1.00"));
                    margemErroInferiorLimiteSaldoRefin = (!TextHelper.isNull(paramSvcCse.getTpsMargemErroLimiteSaldoAntRenegRef()) ? new BigDecimal(1.00 - (Double.valueOf(paramSvcCse.getTpsMargemErroLimiteSaldoAntRenegRef()) / 100.00)).setScale(2, java.math.RoundingMode.HALF_UP) : new BigDecimal("0.00"));
                }
            } catch (final NumberFormatException ex) {
                if (compraContrato) {
                    throw new AutorizacaoControllerException("mensagem.erro.valor.incorreto.parametro.limitacao.saldo.compra", responsavel);
                } else {
                    throw new AutorizacaoControllerException("mensagem.erro.valor.incorreto.para.parametro.com.margem.erro.na.limitacao.saldo.anterior.renegociacao", responsavel);
                }
            }

            // Percentual mínimo a ser mantido do valor original dos contratos renegociados
            BigDecimal percentualPreservaValorReneg = null;
            try {
                if (!compraContrato && !TextHelper.isNull(paramSvcCse.getTpsPercentualMinimoManterValorReneg())) {
                    percentualPreservaValorReneg = new BigDecimal(Double.valueOf(paramSvcCse.getTpsPercentualMinimoManterValorReneg()) / 100.00).setScale(2, java.math.RoundingMode.HALF_UP);
                    if (percentualPreservaValorReneg.signum() == 0) {
                        // Percentual = 0 -> significa que não deve ser mantido valor.
                        percentualPreservaValorReneg = null;
                    }
                }
            } catch (final NumberFormatException ex) {
                throw new AutorizacaoControllerException("mensagem.erro.parametro.percentual.valor.contratos", responsavel);
            }

            // Delegates necessários
            // Lista que irá armazenar as ades localizadas
            final List<AutDesconto> adeTO = new ArrayList<>();
            BigDecimal vlrTotalRenegociacao = new BigDecimal("0.00");
            BigDecimal vlrProvisionadoRenegociado = new BigDecimal("0.00");
            BigDecimal vlrTotalSaldoDevedor = new BigDecimal("0.00");
            Integer maiorPrazoRestante = null;
            Iterator<String> ades = adeCodigos.iterator();
            // Pecorre a lista de ade_codigos
            while (ades.hasNext()) {
                try {
                    final String adeCodigo = ades.next();
                    final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                    // Adiciona na lista a ade encontrada
                    adeTO.add(autdes);

                    // Verifica se o valor do contrato renegociado pode ser utilizado como margem disponível
                    if (AutorizacaoHelper.valorMargemDisponivelRenegociacao(adeIncMargem, autdes.getAdeIncMargem(), responsavel)) {
                        vlrTotalRenegociacao = vlrTotalRenegociacao.add(autdes.getAdeVlr());
                    }

                    // Apenas contratos deferidos interessam para a validação de provisionamento de margem.
                    if (CodedValues.SAD_DEFERIDA.equals(autdes.getStatusAutorizacaoDesconto().getSadCodigo())) {
                        vlrProvisionadoRenegociado = vlrProvisionadoRenegociado.add(autdes.getAdeVlr());
                    }

                    // Caso tenha limitação pelo saldo devedor atual, calcula o saldo de cada contrato envolvido no processo.
                    if (limitaSaldoDevedorRefin) {
                        try {
                            vlrTotalSaldoDevedor = vlrTotalSaldoDevedor.add(saldoDevedorController.calcularSaldoDevedor(autdes, svcCodigo, csaCodigo, orgCodigo, false, responsavel));
                        } catch (final SaldoDevedorControllerException ex) {
                            if ("mensagem.erro.nao.possivel.calcular.saldo.devedor.informacoes.financeiras.nao.cadastradas.entre.contato.consignataria.solicitando.saldo".equals(ex.getMessageKey())) {
                                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.renegociar.este.contrato.pois.nao.contrato.nao.possui.informacoes.para.calcular.saldo.devedor", responsavel);
                            } else {
                                throw new AutorizacaoControllerException(ex);
                            }
                        }
                    }

                    // Obtém o maior prazo restante dos contratos renegociados/comprados
                    if (autdes.getAdePrazo() != null) {
                        final int prazoRestAdeReneg = autdes.getAdePrazo() - (autdes.getAdePrdPagas() != null ? autdes.getAdePrdPagas() : 0);
                        if ((maiorPrazoRestante == null) || (maiorPrazoRestante < prazoRestAdeReneg)) {
                            maiorPrazoRestante = prazoRestAdeReneg;
                        }
                    }

                    // Se o prazo atual for igual a quantidade já paga somado da quantidade de parcelas em processamento,
                    // então a última parcela já está na folha, e possível alteração pode não ser efetivada.
                    // Verifica parâmetro que bloqueia a alteração independente do prazo e em caso positivo, bloqueia a
                    // alteração e envia mensagem de erro ao usuário.
                    final Integer adePrazoOld = autdes.getAdePrazo();
                    final int adePrdPagas = (autdes.getAdePrdPagas() != null ? autdes.getAdePrdPagas() : 0);

                    // Busca as parcelas do periodo
                    final List<ParcelaDescontoPeriodo> parcelasPeriodo = ParcelaDescontoPeriodoHome.findByAutDescontoStatus(adeCodigo, CodedValues.SPD_EMPROCESSAMENTO);
                    // Verifica a quantidade de parcelas em processamento
                    final int qtdParcelasEmProcessamento = (parcelasPeriodo != null ? parcelasPeriodo.size() : 0);

                    validaAlteracaoUltimasParcelas(orgCodigo, adePrazoOld, adePrdPagas, null, qtdParcelasEmProcessamento, responsavel);

                } catch (final FindException ex) {
                    throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.encontrado", responsavel);
                }
            }

            if (compraContrato) {
                try {
                    //verifica o limite de consignatárias que podem ser envolvidas na compra de contratos de terceiros
                    final Object paramQtdeMaxCsa = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_MAX_CSA_POR_COMPRA, responsavel);
                    if (!TextHelper.isNull(paramQtdeMaxCsa)) {
                        final int maxQtdCsaPorCompra = Integer.parseInt(paramQtdeMaxCsa.toString());
                        final ObtemConsignatariasPorAdeCodigoQuery csasAdeCodigoQuery = new ObtemConsignatariasPorAdeCodigoQuery();
                        csasAdeCodigoQuery.adeCodigos = adeCodigos;

                        final List<String> listCsas = csasAdeCodigoQuery.executarLista();
                        if (listCsas.size() > maxQtdCsaPorCompra) {
                            throw new AutorizacaoControllerException("mensagem.erro.quantidade.maxima.consignataria.compra", responsavel, String.valueOf(maxQtdCsaPorCompra));
                        }
                    }
                } catch (final NumberFormatException ex) {
                    throw new AutorizacaoControllerException("mensagem.erro.limite.consignatarias.compra", responsavel);
                }
            }

            LOG.debug("validaTaxa: " + validaTaxa);
            if (validaTaxa) {
                // Pega a data atual
                final Date agora = DateHelper.toSQLDate(DateHelper.getSystemDate());
                // Pega o período atual
                final Date prazoIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, adeCarencia, adePeriodicidade, responsavel);

                final BigDecimal[] valores = validarTaxaJuros(adeVlr, adeVlrLiquido, adeVlrTac,
                        adeVlrIof, adeVlrMensVinc, adePrazo,
                        agora, prazoIni, svcCodigo, csaCodigo,
                        orgCodigo, false, null, adePeriodicidade, rseCodigo, responsavel);
                // return new BigDecimal[]{adeVlr, vlrTotal, adeVlrTac, adeVlrIof, adeVlrMensVinc};
                // Atualiza o valor de Tac e Iof pelo calculado na rotina de validação. Os demais dados não são alterados
                adeVlrTac = valores[2];
                adeVlrIof = valores[3];
            }

            // Testa se o sistema permite um valor maior que a soma dos demais contratos.
            if (limitaValorASomaDosDemaisContratos && (adeVlr.compareTo(vlrTotalRenegociacao) > 0)) {
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.renegociar.este.contrato.pois.valor.parcela.nao.pode.ser.maior.soma.demais.contratos", responsavel);
            }

            if (validaPrazo && limitaPrazoAoMaiorDosDemaisContratos && (maiorPrazoRestante != null) && (adePrazo != null) &&
                    (adePrazo > maiorPrazoRestante)) {
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.renegociar.este.contrato.pois.novo.prazo.nao.pode.ser.maior.prazo.restante.demais.contratos", responsavel);
            }

            // Calcula o valor excedente da margem proporcional, ou seja, o valor que foi utilizado da margem
            // proporcional para a inclusão do novo contrato de renegociação/compra. No caso de compra, como a
            // a margem consumida é da consignatária do contrato, então não calcular e gravar o excedente.
            BigDecimal valorMargemExcedente = null;
            BigDecimal valorAntigoResidual = null;
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)
                    && ParamSist.paramEquals(CodedValues.TPC_MARGEM_1_PROPORCIONAL_USADO_MARGEM_3, CodedValues.TPC_SIM, responsavel)
                    && adeIncMargem.equals(CodedValues.INCIDE_MARGEM_SIM)) {
                // O valor excedente será o mínimo entre a margem proporcional disponível (abatida a margem disponível cartão) e o valor do contrato
                final BigDecimal margemDisponivel1 = obtemMargemRestante(rseTo, CodedValues.INCIDE_MARGEM_SIM, csaCodigo, true, adeCodigos, responsavel);
                final BigDecimal margemDisponivel3 = obtemMargemRestante(rseTo, CodedValues.INCIDE_MARGEM_SIM_3, csaCodigo, true, adeCodigos, responsavel);
                final BigDecimal margemDisponivel = new BigDecimal(margemDisponivel1.doubleValue() - Math.max(margemDisponivel3.doubleValue(), 0)).setScale(2, java.math.RoundingMode.HALF_UP);
                valorMargemExcedente = margemDisponivel.min(adeVlr);

                // No processo de renegociação, caso o contrato antigo deva ser preservado, calcula se
                // o valor da nova reserva está dentro do permitido, e calcula o valor a ser ajustado.
                if (!compraContrato && (percentualPreservaValorReneg != null)) {
                    final BigDecimal valorMinimoResidual = vlrTotalRenegociacao.multiply(percentualPreservaValorReneg);
                    final BigDecimal valorAntigoConsumido = adeVlr.subtract(valorMargemExcedente).subtract(new BigDecimal(Math.max(margemDisponivel3.doubleValue(), 0)));
                    valorAntigoResidual = vlrTotalRenegociacao.subtract(valorAntigoConsumido);
                    if (valorAntigoResidual.compareTo(valorMinimoResidual) == -1) {
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.renegociar.este.contrato.pois.valor.residual.reserva.deve.ser.maior.que.arg0", responsavel, NumberHelper.format(valorMinimoResidual.doubleValue(), NumberHelper.getLang()));
                    } else if ((valorAntigoResidual.compareTo(vlrTotalRenegociacao) > 0) || (valorAntigoResidual.signum() <= 0)) {
                        LOG.info("valorAntigoConsumido: " + valorAntigoConsumido);
                        LOG.info("valorMinimoResidual: " + valorMinimoResidual);
                        LOG.info("valorAntigoResidual: " + valorAntigoResidual);
                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.renegociar.este.contrato.pois.valor.residual.calculado.invalido", responsavel);
                    }
                }
            }

            // Testa se o sistema permite um valor liberado maior que a soma dos saldo devedor dos contratos renegociados.
            LOG.debug("validaSaldoDevedorLimite: " + limitaSaldoDevedorRefin);
            if (limitaSaldoDevedorRefin) {
                if (adeVlrLiquido == null) {
                    throw new AutorizacaoControllerException("mensagem.erro.valor.liquido.liberado.deve.ser.informado.para.validacao.pelo.saldo.devedor.atual", responsavel);
                }

                LOG.debug("Total Saldo Devedor Atual: " + vlrTotalSaldoDevedor.setScale(2, java.math.RoundingMode.HALF_UP));
                // Aplica a margem de erro sobre o saldo calculado dos contratos antigos: Positiva e Negativa
                final BigDecimal saldoDevedorLimiteSuperior = vlrTotalSaldoDevedor.multiply(margemErroSuperiorLimiteSaldoRefin).setScale(2, java.math.RoundingMode.HALF_UP);
                final BigDecimal saldoDevedorLimiteInferior = vlrTotalSaldoDevedor.multiply(margemErroInferiorLimiteSaldoRefin).setScale(2, java.math.RoundingMode.HALF_UP);
                LOG.debug("Total Saldo Devedor Atual + Margem de Erro: de " + saldoDevedorLimiteInferior + " a " + saldoDevedorLimiteSuperior);
                LOG.debug("Valor Liberado Informado: " + adeVlrLiquido.setScale(2, java.math.RoundingMode.HALF_UP));

                if (adeVlrLiquido.compareTo(saldoDevedorLimiteSuperior) > 0) {
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.renegociar.este.contrato.pois.valor.liberado.maior.que.soma.saldo.devedor.atual.margem.erro.arg0", responsavel, NumberHelper.format(saldoDevedorLimiteSuperior.doubleValue(), NumberHelper.getLang()));
                } else if (adeVlrLiquido.compareTo(saldoDevedorLimiteInferior) < 0) {
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.renegociar.este.contrato.pois.valor.liberado.menor.que.soma.saldo.devedor.atual.margem.erro.arg0", responsavel, NumberHelper.format(saldoDevedorLimiteInferior.doubleValue(), NumberHelper.getLang()));
                }
            }

            // Verifica se contratos de origem possuem solicitação de saldo de devedor em ciclo fixo de processo de compra.
            // Se houver, altera o status das solicitações de saldo de pendente para cancelada.
            if (compraContrato && ParamSist.getBoolParamSist(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, responsavel)) {
                for (final AutDesconto adeRenegociacao : adeTO) {
                    final String adeCodigo = adeRenegociacao.getAdeCodigo();
                    saldoDevedorController.atualizaStatusSolicitacaoSaldoDevedor(adeCodigo, StatusSolicitacaoEnum.CANCELADA, responsavel);
                }
            }

            String sadCodigo = null;
            final boolean podeConfirmar = podeConfirmarRenegociacao(adeVlr, svcCodigo, csaCodigo, vlrTotalRenegociacao, responsavel);
            final boolean podeDeferir = usuarioPodeDeferir(svcCodigo, csaCodigo, rseCodigo, comSerSenha, responsavel);
            final boolean podeConfirmarLiquidacao = usuarioPodeConfirmarLiquidacao(adeTO, true, podeConfirmar, responsavel);
            final boolean reservaPodeSerCriadaDeferida = podeConfirmar && podeDeferir && podeConfirmarLiquidacao && !responsavel.isSer();
            final int diasCancelamentoRenegociacao = !TextHelper.isNull(paramSvcCse.getTpsPrazoDiasCancelamentoRenegociacao()) ? Integer.parseInt(paramSvcCse.getTpsPrazoDiasCancelamentoRenegociacao()) : 0;

            if (valorAntigoResidual != null) {
                if (valorAntigoResidual.compareTo(vlrTotalRenegociacao) < 0) {
                    // Apenas ajusta o contrato a ser renegociado, reduzindo o valor,
                    // deixando este na situação em que se encontra
                    final AutDesconto autDesAlt = adeTO.get(0);
                    final AlterarConsignacaoParametros alterarParam = new AlterarConsignacaoParametros(autDesAlt.getAdeCodigo(), valorAntigoResidual, autDesAlt.getAdePrazo(), autDesAlt.getAdeIdentificador(), false, autDesAlt.getAdeIndice(), autDesAlt.getAdeVlrTac(), autDesAlt.getAdeVlrIof(), autDesAlt.getAdeVlrLiquido(), autDesAlt.getAdeVlrMensVinc(), autDesAlt.getAdeTaxaJuros(), autDesAlt.getAdeAnoMesFim(), null);
                    alterarParam.setAdePeriodicidade(autDesAlt.getAdePeriodicidade());
                    alterarConsignacaoController.alterar(alterarParam, responsavel);
                }

                // O status do novo contrato será Deferido
                sadCodigo = CodedValues.SAD_DEFERIDA;

            } else {
                // Define a data inicial da nova consignação fruto da renegociação
                Date anoMesIniNovaAde = null;
                if (renegociarParam.getAdeAnoMesIni() == null) {
                    // Calcula início do contrato
                    anoMesIniNovaAde = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, parametroController.calcularAdeCarenciaDiaCorteCsa(adeCarencia, csaCodigo, orgCodigo, responsavel), adePeriodicidade, responsavel);
                    renegociarParam.setAdeAnoMesIni(anoMesIniNovaAde);
                } else {
                    // Usa a data informada por parâmetro
                    anoMesIniNovaAde = DateHelper.toPeriodDate(renegociarParam.getAdeAnoMesIni());
                }

                // Se deve alterar a data de encerramento dos contratos antigos para que fiquem igual à data inicial
                // do contrato novo, usa a data inicial previamente definda, senão, calcula a data de encerramento
                final java.util.Date ocaPeriodoRenegociacao = renegociarParam.isAlterarDataEncerramento() ? anoMesIniNovaAde :
                    AutorizacaoHelper.recuperarPeriodoOcorrenciaLiquidacao(orgCodigo, csaCodigo, svcCodigo, DateHelper.getSystemDatetime(), anoMesIniNovaAde, responsavel);

                if (!compraContrato && reservaPodeSerCriadaDeferida || (compraContrato && renegociarParam.getPortabilidadeCartao())) {
                    final LiquidarConsignacaoParametros parametrosLiquidacao = new LiquidarConsignacaoParametros();
                    parametrosLiquidacao.setRenegociacao(true);
                    parametrosLiquidacao.setPodeConfirmarRenegociacao(podeConfirmar);
                    parametrosLiquidacao.setVerificaBloqueioOperacao(false);
                    parametrosLiquidacao.setVerificaReservaCartaoCredito(false);
                    parametrosLiquidacao.setOcaPeriodo(ocaPeriodoRenegociacao);
                    if (renegociarParam.getPortabilidadeCartao() && responsavel.isSer()) {
                        parametrosLiquidacao.setLiquidarPortabilidadeCartao(true);
                    }
                    // Se usuário pode confirmar e não requer deferimento,
                    // então liquida a autorização e insere reserva como Deferida
                    for (final String adeLiquidada : adeCodigos) {
                        liquidarConsignacaoController.liquidar(adeLiquidada, null, parametrosLiquidacao, responsavel);
                    }
                    sadCodigo = CodedValues.SAD_DEFERIDA;

                    // Calcula a carência mínima do novo contrato de acordo com os parâmetros de serviço
                    final Integer adeCarenciaNova = AutorizacaoHelper.calcularCarenciaContratoDestinoRenegociacao(orgCodigo, csaCodigo, svcCodigo, adeCarencia, ocaPeriodoRenegociacao, responsavel);
                    if (adeCarenciaNova.intValue() > adeCarencia.intValue()) {
                        adeCarencia = adeCarenciaNova;
                    }
                } else {
                    // Verifica se o valor da nova consignação é maior do que o antigo,
                    // e se for, prende da margem do servidor a diferença dos valores
                    final BigDecimal diff = adeVlr.subtract(vlrTotalRenegociacao);

                    if (diff.signum() == 1) {
                        try {
                            // Tenta reservar na margem a diferença entre o novo contrato e os renegociados
                            historicosMargem = atualizaMargem(rseCodigo, adeIncMargem, diff, verificaMargem, true, serAtivo, null, csaCodigo, svcCodigo, adeCodigos, responsavel);
                        } catch (final AutorizacaoControllerException ex) {
                            /*
                             * Se não foi possível reservar todo o valor, verifica se o serviço tem
                             * limite para autorizações sem margem. Verifica também se não é um compulsório,
                             * e caso positivo, pesquisa o vlr total dos contratos que podem ser estocados
                             * liberando assim vlr de margem para a inclusão
                             */
                            historicosMargem = atualizaMargemCompulsorios(rseCodigo, svcCodigo, csaCodigo, null, null, diff, vlrLimiteSemMargem, adeIncMargem, parametros, true, serAtivo, false, responsavel);
                        }
                    }

                    // Se usuário não pode confirmar ou requer deferimento, coloca
                    // autorização em Aguard Liquidação e insere reserva sem incidir na margem
                    adeIncMargem = CodedValues.INCIDE_MARGEM_NAO;

                    // Por padrão a nova reserva será criada com status de aguard confirmação
                    sadCodigo = CodedValues.SAD_AGUARD_CONF;

                    // Porém, no caso da renegociação de serviço que requer deferimento, e o usuário pode confirmar
                    // reserva e confirmar liquidação, então a reserva já é criada como aguard deferimento
                    if (!compraContrato && podeConfirmar && podeConfirmarLiquidacao && !podeDeferir) {
                        sadCodigo = CodedValues.SAD_AGUARD_DEFER;
                    }

                    final String sadCodigoModifica = compraContrato ? CodedValues.SAD_AGUARD_LIQUI_COMPRA : CodedValues.SAD_AGUARD_LIQUIDACAO;
                    for (final AutDesconto adeRenegociacao : adeTO) {
                        // public String modificaSituacaoADE(AutDesconto autdes, String status, AcessoSistema responsavel, boolean geraOcorrencia,
                        //        java.util.Date ocaPeriodo, boolean liberaMargem) throws AutorizacaoControllerException {
                        modificaSituacaoADE(adeRenegociacao, sadCodigoModifica, responsavel, true, ocaPeriodoRenegociacao, true);
                    }

                    // DESENV-16740 : cria ocorrência de confirmação de liquidação caso a consignatária exija dupla
                    // confirmação para operações que resultem em liquidações de contratos.
                    if (efetuarLiquidacaoDuasEtapas(responsavel) && exigirDuplaConfirmacaoLiquidacao(responsavel)) {
                        // Se a liquidação é em duas etapas com dupla confirmação obrigatória, verifica se o usuário que
                        // realiza a operação tem permissão para contar como uma confirmação de liquidação
                        if ((compraContrato && responsavel.temPermissao(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA)) ||
                                (!compraContrato && responsavel.temPermissao(CodedValues.FUN_CONF_LIQUIDACAO))) {
                            for (final AutDesconto adeRenegociacao : adeTO) {
                                final String adeCodigoReneg = adeRenegociacao.getAdeCodigo();
                                criaOcorrenciaADE(adeCodigoReneg, CodedValues.TOC_CONFIRMACAO_LIQUIDACAO_ADE, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.confirmacao.liquidacao", responsavel), ocaPeriodoRenegociacao, responsavel);
                            }
                        }
                    }
                }

                // Se na renegociação deve alterar a data de encerramento dos contratos antigos para que fiquem igual
                // à data inicial do contrato novo, cria uma ocorrência para marcar qual período deverá ser usado
                // quando a operação for confirmada/deferida e as consignações forem efetivamente liquidadas
                if (!compraContrato && renegociarParam.isAlterarDataEncerramento()) {
                    for (final AutDesconto adeRenegociacao : adeTO) {
                        final String adeCodigoReneg = adeRenegociacao.getAdeCodigo();
                        criaOcorrenciaADE(adeCodigoReneg, CodedValues.TOC_RENEGOCIACAO_ALTERACAO_DT_ENCERRAMENTO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.renegociacao.alteracao.data.encerramento", responsavel), ocaPeriodoRenegociacao, responsavel);
                    }
                }
            }

            // Verifica se é uma reserva de cartão de crédito e se há valor reservado suficiente para a diferença entre o valor anterior e o novo.
            // OBS: Somente se o novo contrato nascer deferido, seu valor será considerado para o provisionamento.
            BigDecimal novoValorProvisionado;
            if (((sadCodigo == null) && reservaPodeSerCriadaDeferida) || (CodedValues.SAD_DEFERIDA.equals(sadCodigo))) {
                novoValorProvisionado = adeVlr;
            } else {
                novoValorProvisionado = BigDecimal.ZERO;
            }

            verificaReservaCartaoCredito(rseCodigo, novoValorProvisionado.subtract(vlrProvisionadoRenegociado), cnvCodigoOriginal, responsavel);

            // Monta o objeto de parâmetro da reserva
            final ReservarMargemParametros reservaParam = new ReservarMargemParametros();

            reservaParam.setRseCodigo(rseCodigo);
            reservaParam.setAdeVlr(adeVlr);
            reservaParam.setCorCodigo(corCodigo);
            reservaParam.setAdePrazo(adePrazo);
            reservaParam.setValidaPrazo(renegociarParam.getValidaPrazo());
            reservaParam.setAdeCarencia(parametroController.calcularAdeCarenciaDiaCorteCsa(adeCarencia, csaCodigo, orgCodigo, responsavel));
            reservaParam.setAdeAnoMesIni(renegociarParam.getAdeAnoMesIni());
            reservaParam.setAdeAnoMesFim(renegociarParam.getAdeAnoMesFim());
            reservaParam.setAdeIdentificador(adeIdentificador);
            reservaParam.setCnvCodigo(cnvCodigo);
            reservaParam.setSadCodigo(sadCodigo);
            reservaParam.setSerSenha(renegociarParam.getSerSenha());
            reservaParam.setComSerSenha(comSerSenha);
            reservaParam.setAdeTipoVlr(adeTipoVlr);
            reservaParam.setAdeIntFolha(adeIntFolha);
            reservaParam.setAdeIncMargem(adeIncMargem);
            reservaParam.setAdeIndice(adeIndice);
            reservaParam.setAdeVlrTac(adeVlrTac);
            reservaParam.setAdeVlrIof(adeVlrIof);
            reservaParam.setAdeVlrLiquido(adeVlrLiquido);
            reservaParam.setAdeVlrMensVinc(adeVlrMensVinc);
            reservaParam.setValidar(Boolean.FALSE);
            reservaParam.setPermitirValidacaoTaxa(Boolean.TRUE);
            reservaParam.setSerAtivo(renegociarParam.getSerAtivo());
            reservaParam.setCnvAtivo(Boolean.TRUE);
            reservaParam.setSerCnvAtivo(Boolean.TRUE);
            reservaParam.setSvcAtivo(Boolean.TRUE);
            reservaParam.setCsaAtivo(Boolean.TRUE);
            reservaParam.setOrgAtivo(Boolean.TRUE);
            reservaParam.setEstAtivo(Boolean.TRUE);
            reservaParam.setCseAtivo(Boolean.TRUE);
            reservaParam.setAcao(compraContrato ? "COMPRAR" : "RENEGOCIAR");
            reservaParam.setAdeTaxaJuros(adeTaxaJuros);
            reservaParam.setAdeVlrSegPrestamista(adeVlrSegPrestamista);
            reservaParam.setAdeDtHrOcorrencia(adeDtHrOcorrencia);
            reservaParam.setAdeCodigosRenegociacao(adeCodigos);
            reservaParam.setAdeBanco(renegociarParam.getAdeBanco());
            reservaParam.setAdeAgencia(renegociarParam.getAdeAgencia());
            reservaParam.setAdeConta(renegociarParam.getAdeConta());
            reservaParam.setVlrTotalCompradoRenegociado(vlrTotalCompradoRenegociado);
            reservaParam.setAdePeriodicidade(adePeriodicidade);
            reservaParam.setSerAtivo(renegociarParam.getSerAtivo() != null ? renegociarParam.getSerAtivo() : true);

            reservaParam.setValidaAnexo(renegociarParam.getValidaAnexo());
            reservaParam.setNomeAnexo(nomeAnexo);
            reservaParam.setAadDescricao(aadDescricao);
            reservaParam.setIdAnexo(idAnexo);
            reservaParam.setTdaModalidadeOperacao(renegociarParam.getTdaModalidadeOperacao());
            reservaParam.setTdaMatriculaSerCsa(renegociarParam.getTdaMatriculaSerCsa());
            reservaParam.setTdaTelSolicitacaoSer(renegociarParam.getTdaTelSolicitacaoSer());
            // Seta os dados genéricos
            reservaParam.setDadosAutorizacaoMap(renegociarParam.getDadosAutorizacaoMap());

            reservaParam.setTelaConfirmacaoDuplicidade(renegociarParam.isTelaConfirmacaoDuplicidade());
            reservaParam.setChkConfirmarDuplicidade(renegociarParam.isChkConfirmarDuplicidade());
            reservaParam.setMotivoOperacaoCodigoDuplicidade(renegociarParam.getMotivoOperacaoCodigoDuplicidade());
            reservaParam.setMotivoOperacaoObsDuplicidade(renegociarParam.getMotivoOperacaoObsDuplicidade());

            reservaParam.setCftCodigo(renegociarParam.getCftCodigo());
            reservaParam.setDtjCodigo(renegociarParam.getDtjCodigo());
            reservaParam.setCdeRanking(renegociarParam.getCdeRanking());
            reservaParam.setCdeVlrLiberado(renegociarParam.getCdeVlrLiberado());

            reservaParam.setCftCodigo(renegociarParam.getCftCodigo());
            reservaParam.setDtjCodigo(renegociarParam.getDtjCodigo());
            reservaParam.setCdeRanking(renegociarParam.getCdeRanking());
            reservaParam.setCdeVlrLiberado(renegociarParam.getCdeVlrLiberado());
            reservaParam.setDestinoAprovacaoLeilaoReverso(renegociarParam.isDestinoAprovacaoLeilaoReverso());

            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_FINANC_DIVIDA_CARTAO, CodedValues.TPC_SIM, responsavel)
                    && !TextHelper.isNull(renegociarParam.getPpdCodigo())
                    && financiamentoDividaController.propostaAprovada(renegociarParam.getPpdCodigo(), responsavel)) {
                // Desabilita consumo de senha, caso seja financiamento de dívida com proposta aprovada.
                reservaParam.setConsomeSenha(false);
            } else {
                // Repassa a informação de consumo de senha de autorização
                reservaParam.setConsomeSenha(renegociarParam.getConsomeSenha());
            }

            final String adeCodigoNovo = reservarMargem(reservaParam, responsavel);

            // prende a margem quando o valor da renegociação é menor que o
            // contrato novo, até que o prazo de cancelamento tenha sido cumprido, predemos o valor total da renegocição que é o que ele já tinha
            // na margem
            if (ParamSist.getBoolParamSist(CodedValues.TPC_PRENDER_MARGEM_RENEG_CONTRATRO_NOVO_MENOR_ANTIGOS, responsavel) && (diasCancelamentoRenegociacao > 0) && (vlrTotalRenegociacao.subtract(adeVlr).signum() == 1) && CodedValues.SAD_DEFERIDA.equals(sadCodigo)) {
                final String ocaCodigo = criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_RETENCAO_MARGEM_DENTRO_PRAZO_RENEGOCIACAO, ApplicationResourcesHelper.getMessage("mensagem.info.ocorrencia.margem.preza.renegociacao", responsavel), vlrTotalRenegociacao, adeVlr, responsavel);
                try {
                    historicosMargem = atualizaMargem(rseCodigo, adeIncMargem, vlrTotalRenegociacao, verificaMargem, true, serAtivo, ocaCodigo, csaCodigo, svcCodigo, adeCodigos, responsavel);
                } catch (final AutorizacaoControllerException ex) {
                    historicosMargem = atualizaMargemCompulsorios(rseCodigo, svcCodigo, csaCodigo, null, ocaCodigo, vlrTotalRenegociacao, vlrLimiteSemMargem, adeIncMargem, parametros, true, serAtivo, false, responsavel);
                }
            }

            // Cria os relacionamentos de autorização
            for (final String adeCodigoOca : adeCodigos) {
                if (compraContrato && !renegociarParam.getPortabilidadeCartao()) {
                    RelacionamentoAutorizacaoHome.create(adeCodigoOca, adeCodigoNovo, CodedValues.TNT_CONTROLE_COMPRA, responsavel.getUsuCodigo(), StatusCompraEnum.AGUARDANDO_INF_SALDO);
                } else if (compraContrato && renegociarParam.getPortabilidadeCartao()) {
                    RelacionamentoAutorizacaoHome.create(adeCodigoOca, adeCodigoNovo, CodedValues.TNT_CONTROLE_COMPRA, responsavel.getUsuCodigo());
                } else {
                    RelacionamentoAutorizacaoHome.create(adeCodigoOca, adeCodigoNovo, CodedValues.TNT_CONTROLE_RENEGOCIACAO, responsavel.getUsuCodigo());
                }
            }

            // Se for compra de contrato apaga os registros de saldo devedor existentes para
            // os contratos comprados, evitando que um saldo devedor fique desatualizado
            if (compraContrato) {
                for (final String adeCodigoSdv : adeCodigos) {
                    if (TextHelper.isNull(renegociarParam.getPpdCodigo())) {
                        // Não remove registro de saldo caso seja oriundo de proposta
                        // de financiamento de dívida, já que o saldo será utilizado.
                        saldoDevedorController.removeSaldoDevedor(adeCodigoSdv, responsavel);
                    }
                    // Remove também as informações de pagamento de saldo devedor
                    saldoDevedorController.removePagamentoSaldoDevedor(adeCodigoSdv);
                }
            }

            AutDesconto autdesNovo = null;

            // Verifica se o novo contrato possui carencia no final do contrato
            // caso possua carencia será setado como ade_ano_mes_ini_ref o ade_ano_mes_ini
            // do contrato renegociado
            try {
                autdesNovo = AutDescontoHome.findByPrimaryKey(adeCodigoNovo);
            } catch (final FindException ex) {
                throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.encontrado", responsavel);
            }

            // Se o usuario pode confirmar a nova reserva é cadastrado na ade como S
            // caso contrario será setado como N
            autdesNovo.setAdePodeConfirmar(podeConfirmar ? "S" : "N");

            // Seta o valor de ade_ano_mes_ini_ref igual ao ade_ano_mes_ini_ref do contrato
            // renegociado, de acordo com os parâmetros de serviço
            if (preservaDataRenegociacao) {

                AutDesconto adeTempData = null;

                if (adeTO.size() == 1) {
                    // Se tem apenas um contrato, pega o existente
                    adeTempData = adeTO.get(0);

                } else {
                    // Busca a data maior ou menor de acordo com o parametro de serviço
                    // Ordena os contratos em ordem crescente, pelas datas inicias
                    // Após a ordenação dos contratos verifica o parametro que informa se é utilizada
                    // a menor ou maior data, se o parametro for igual a 1, quer dizer que ele
                    // vai utilizar a data mais antiga, caso contrario a mais recente.
                    Collections.sort(adeTO, (ade1, ade2) -> {
                        if ((ade1.getAdeAnoMesIniRef() != null) && (ade2.getAdeAnoMesIniRef() != null) ) {
                            return ade1.getAdeAnoMesIniRef().compareTo(ade2.getAdeAnoMesIniRef());
                        } else {
                            return ade1.getAdeAnoMesIni().compareTo(ade2.getAdeAnoMesIni());
                        }
                    });

                    if (preservaDataMaisAntigaRenegociacao) {
                        adeTempData = adeTO.get(0);
                    } else {
                        adeTempData = adeTO.get(adeTO.size() - 1);
                    }
                }

                if (adeTempData.getAdeAnoMesIniRef() != null) {
                    autdesNovo.setAdeAnoMesIniRef(adeTempData.getAdeAnoMesIniRef());
                } else {
                    autdesNovo.setAdeAnoMesIniRef(adeTempData.getAdeAnoMesIni());
                }
            }

            AbstractEntityHome.update(autdesNovo);

            // Atualiza os históricos de margem com a ocorrencia de inclusão
            // do novo contrato fruto do alongamento
            atualizaHistoricosMargem(historicosMargem, null, adeCodigoNovo, CodedValues.TOC_TARIF_RESERVA);

            // Se é compra de contrato, grava informações adicionais
            if (compraContrato) {
                // Verifica se o e-mail do servidor foi informado, e armazena na tabela de dados de autorização
                if (!TextHelper.isNull(renegociarParam.getSerEmail())) {
                    setDadoAutDesconto(adeCodigoNovo, CodedValues.TDA_SDV_EMAIL_SERVIDOR, renegociarParam.getSerEmail(), responsavel);
                }

                // Verifica se o número de portabilidade CIP foi informado, e armazena na tabela de dados de autorização
                if (!TextHelper.isNull(renegociarParam.getNumCipCompra())) {
                    setDadoAutDesconto(adeCodigoNovo, CodedValues.TDA_NUM_PORTABILIDADE_CIP, renegociarParam.getNumCipCompra(), responsavel);
                }

                if (ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OPCIONAL, responsavel) ||
                        ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OBRIGATORIO, responsavel)) {
                    // Salva anexo de documentação adicional
                    if (!TextHelper.isNull(renegociarParam.getAnexoDocAdicionalCompra())) {
                        final File anexo = UploadHelper.moverArquivoAnexoTemporario(renegociarParam.getAnexoDocAdicionalCompra(), adeCodigoNovo, idAnexo, responsavel);
                        if ((anexo != null) && anexo.exists()) {
                            final Date aadPeriodo = autdesNovo.getAdeAnoMesIniRef() != null ? new Date(autdesNovo.getAdeAnoMesIniRef().getTime()) : null;
                            editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigoNovo, anexo.getName(), anexo.getName(), aadPeriodo, TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DOC_ADICIONAL_COMPRA, responsavel);
                        }
                    } else if (ParamSist.paramEquals(CodedValues.TPC_INFORMA_ANEXO_ADE_DOC_ADICIONAL_COMPRA, CodedValues.ANEXO_ADE_DOC_ADICIONAL_COMPRA_OBRIGATORIO, responsavel) && !renegociarParam.isDestinoAprovacaoLeilaoReverso()) {
                        // Se não foi informado, mas é obrigatório, reporta o erro ao usuário
                        throw new AutorizacaoControllerException("mensagem.erro.nao.pode.inserir.nova.reserva.para.este.servico.pois.anexo.obrigatorio", responsavel);
                    }
                }

                // DESENV-16086 - Cria nova ocorrência caso seja uma compra que permite que o servidor continue com margem negativa
                if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_PORTABILIDADE_MARGEM_NEGATIVA, CodedValues.TPC_SIM, responsavel) && verificaMargem) {
                    BigDecimal adeVlrAnterior = BigDecimal.ZERO;

                    for (final String adeCodigoReneg : adeCodigos) {
                        final CustomTransferObject autDesconto = pesquisarConsignacaoController.buscaAutorizacao(adeCodigoReneg, responsavel);
                        adeVlrAnterior = adeVlrAnterior.add((BigDecimal) autDesconto.getAttribute(Columns.ADE_VLR));
                    }

                    if ((margemRestInicial.add(adeVlrAnterior).subtract(adeVlr).signum() < 0) && verificaPermitePortabilidadeServidorMargemNegativa(adePrazo, adeVlr, null, adeCodigos, true, responsavel)) {
                        criaOcorrenciaADE(adeCodigoNovo, CodedValues.TOC_PORTABILIDADE_MARGEM_NEGATIVA, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.renegociar.permite.margem.negativa", responsavel), responsavel);
                    }
                }
            }

            // Grava o valor de margem excedente, caso não seja nulo
            if (valorMargemExcedente != null) {
                setDadoAutDesconto(adeCodigoNovo, CodedValues.TDA_VALOR_EXCEDENTE_MARGEM_PROPORCIONAL, valorMargemExcedente.toString(), responsavel);
            }

            // Realiza a conclusão da operação de financiamento de dívida
            try {
                financiamentoDividaController.concluirFinanciamento(adeCodigoNovo, adeCodigos, renegociarParam.getPpdCodigo(), svcCodigo, compraContrato, responsavel);
            } catch (final FinanciamentoDividaControllerException ex) {
                throw new AutorizacaoControllerException(ex);
            }

            // Envia e-mail caso seja compra de contrato
            if (compraContrato) {
                // Manda email de notificação sobre a compra do contrato,
                // para cada um dos contratos comprados
                for (final String adeCodigoEmail : adeCodigos) {
                    EnviaEmailHelper.enviarEmailCompraContrato(EnviaEmailHelper.TIPO_COMPRA_CONTRATO, adeCodigoEmail, null, responsavel);
                }
            } else {
                // Busca as informações do contrato novo
                final String obs = ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.numero.novo.contrato.resultante.renegociacao.arg0", responsavel, autdesNovo.getAdeNumero().toString());

                ades = adeCodigos.iterator();
                // Pecorre a lista de ade_codigos
                while (ades.hasNext()) {
                    // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
                    // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
                    final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.RENEGOCIAR_CONSIGNACAO, ades.next(), obs, null, responsavel);
                    processoEmail.start();
                }
            }

            // Gera o Log de auditoria
            for (final String adeCodigo : adeCodigos) {
                final LogDelegate log = new LogDelegate(responsavel, Log.RELACIONAMENTO_AUTORIZACAO, Log.RENEGOCIAR_CONTRATO, Log.LOG_INFORMACAO);
                log.setAutorizacaoDesconto(adeCodigo);
                log.setRegistroServidor(rseCodigo);
                log.setConvenio(cnvCodigo);
                log.setStatusAutorizacao(sadCodigo);
                log.setAutorizacaoDescontoDestino(adeCodigoNovo);
                log.addChangedField(Columns.ADE_VLR, adeVlr);
                log.addChangedField(Columns.ADE_PRAZO, adePrazo);
                log.addChangedField(Columns.ADE_INC_MARGEM, adeIncMargem);
                log.addChangedField(Columns.ADE_INDICE, adeIndice);
                log.write();
            }
            
            if (compraContrato && renegociarParam.getPortabilidadeCartao()) {
            	final ConsignatariaTransferObject csa = consignatariaController.findConsignataria(csaCodigo, responsavel);
                final String csaEmail = (String) csa.getAttribute(Columns.CSA_EMAIL);
                if (!TextHelper.isNull(csaEmail)) {
                    final ServidorTransferObject servidor = servidorController.findServidor(rseTo.getSerCodigo(), responsavel);
                    final String serCpf = (String) servidor.getAttribute(Columns.SER_CPF);
                	EnviaEmailHelper.notificaCsaPortabilidadeCartao(csaEmail, autdesNovo.getAdeNumero().toString(), serCpf, rseTo.getRseMatricula(), responsavel);
                }
            }

            return adeCodigoNovo;
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    protected BigDecimal validaRenegociacao(List<String> adeCodigos, String rseCodigo, BigDecimal adeVlr,
                                      String corCodigo, AcessoSistema responsavel, Integer adePrazo,
                                      Integer adeCarencia, String adePeriodicidade, String adeIdentificador,
                                      String cnvCodigo, boolean comSerSenha, String adeIndice,
                                      BigDecimal adeVlrTac, BigDecimal adeVlrIof,
                                      BigDecimal adeVlrLiquido, BigDecimal adeVlrMensVinc,
                                      boolean verificaMargem, BigDecimal adeTaxaJuros,
                                      boolean compra, boolean alongamento, boolean serAtivo,
                                      RegistroServidorTO rseTo, Map<String, Object> parametros) throws AutorizacaoControllerException {
        try {
            final Convenio convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
            final String svcCodigo = convenio.getServico().getSvcCodigo();
            final String csaCodigo = convenio.getConsignataria().getCsaCodigo();

            Short tpsMargem = Short.valueOf("0");
            BigDecimal vlrLimiteSemMargem = BigDecimal.ZERO;
            BigDecimal vlrAtual = BigDecimal.ZERO;

            final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);
            final boolean usaTaxaJuros = ParamSist.getBoolParamSist(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, responsavel);
            final boolean apenasPermiteCompraCetMenor = ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_COMPRA_APENAS_CET_MENOR_ANTERIOR, responsavel);
            final boolean cetDeveSerMenorCompra = usaTaxaJuros && apenasPermiteCompraCetMenor && compra;

            if (verificaMargem) {
                final ParamSvcTO paramSvcTo = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                // Incidência da margem
                tpsMargem = paramSvcTo.getTpsIncideMargem();

                // Valor para inclusão além da margem
                vlrLimiteSemMargem = (!TextHelper.isNull(paramSvcTo.getTpsVlrLimiteAdeSemMargem()) ? new BigDecimal(paramSvcTo.getTpsVlrLimiteAdeSemMargem()) : new BigDecimal("0.00"));
            }

            // Filtra os contratos que podem ser negociados
            final ListaConsignacaoRenegociavelNativeQuery lcrQuery = new ListaConsignacaoRenegociavelNativeQuery();
            lcrQuery.adeCodigos = adeCodigos;
            lcrQuery.tipoOperacao = (compra ? "comprar" : (alongamento ? "alongar" : "renegociar"));
            lcrQuery.csaCodigo = csaCodigo;
            lcrQuery.svcCodigo = svcCodigo;
            lcrQuery.responsavel = responsavel;
            lcrQuery.ignoraParamRestTaxaMenor = (compra || !alongamento);

            final List<String> adeRenegociaveis = lcrQuery.executarLista();
            final List<BigDecimal> taxasJuros = new ArrayList<>();

            final Iterator<String> it = adeCodigos.iterator();
            while (it.hasNext()) {
                try {
                    final AutDesconto autdes = AutDescontoHome.findByPrimaryKey(it.next());
                    final String sadCodigo = autdes.getStatusAutorizacaoDesconto().getSadCodigo().trim();
                    final boolean permiteRenegociarContratoSuspensoFolha = verificaContratoSuspensoPodeRenegociar(autdes.getAdeCodigo(), sadCodigo, responsavel);

                    if (!CodedValues.SAD_ESTOQUE.equals(sadCodigo) && !CodedValues.SAD_DEFERIDA.equals(sadCodigo) &&
                        !CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) && !CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) &&
                        !CodedValues.SAD_EMCARENCIA.equals(sadCodigo) && !permiteRenegociarContratoSuspensoFolha) {
                        throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.pode.ser.renegociado.porque.situacao.atual.dele.nao.permite.esta.operacao", responsavel);
                    }

                    // Verifica se o CET/Taxa deve ser menor, caso o contrato não atenda os requisitos mínimos para renegociação/compra
                    boolean cetDeveSerMenorRestricao = false;
                    if (!adeRenegociaveis.contains(autdes.getAdeCodigo())) {
                        if (compra && ParamSist.paramEquals(CodedValues.TPC_PERMITE_COMPRA_SEM_RESTRICAO_TAXA_MENOR, CodedValues.TPC_SIM, responsavel)) {
                            cetDeveSerMenorRestricao = true;
                        } else if (!compra && !alongamento && ParamSist.paramEquals(CodedValues.TPC_PERMITE_RENEG_SEM_RESTRICAO_TAXA_MENOR, CodedValues.TPC_SIM, responsavel)) {
                            cetDeveSerMenorRestricao = true;
                        } else if (!alongamento) {
                            throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.atende.todos.requisitos.necessarios.para.ser.renegociado", responsavel);
                        }
                    }

                    // Soma o valor de todas as ade's renegociadas
                    if (AutorizacaoHelper.valorMargemDisponivelRenegociacao(tpsMargem, autdes.getAdeIncMargem(), responsavel)) {
                        vlrAtual = vlrAtual.add(autdes.getAdeVlr());
                    }

                    if (cetDeveSerMenorCompra || cetDeveSerMenorRestricao) {
                        // guarda todas as taxas de juros para tratamento posterior
                        boolean calcula = !ParamSist.getBoolParamSist(CodedValues.TPC_UTILIZA_CET_GRAVADO_INCLUSAO, responsavel);
                        if (!calcula) {
                            try {
                                final CoeficienteDesconto coeficienteDesconto = CoeficienteDescontoHome.findByAdeCodigo(autdes.getAdeCodigo());

                                if (coeficienteDesconto != null) {
                                    final Coeficiente cft = CoeficienteHome.findByPrimaryKey(coeficienteDesconto.getCoeficiente().getCftCodigo());
                                    if (cft != null) {
                                        final BigDecimal cftVlr = cft.getCftVlr();
                                        if ((cftVlr != null) && (cftVlr.compareTo(BigDecimal.ZERO) != 0)) {
                                            taxasJuros.add(cftVlr);
                                        } else {
                                            calcula = true;
                                        }
                                    }
                                }
                            } catch (final FindException findException) {
                                // caso não exista coeficiente desconto deve calcular a taxa de juros
                                calcula = true;
                            }
                        }

                        if (calcula && (autdes.getAdeVlrLiquido() != null) && (autdes.getAdeVlrLiquido().signum() > 0) && (autdes.getAdePrazo() != null)) {
                            taxasJuros.add(SimulacaoHelper.calcularTaxaJuros(autdes.getAdeVlrLiquido(), autdes.getAdeVlr(), autdes.getAdePrazo(), autdes.getAdeData(), autdes.getAdeAnoMesIni(), OrgaoHome.findByAdeCod(autdes.getAdeCodigo()).getOrgCodigo(), responsavel));
                        }
                    }
                } catch (final FindException ex) {
                    throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.encontrado", responsavel);
                }
            }

            // Verifica se as taxas de juros anteriores são menores do que a taxa de juros atual
            if (!taxasJuros.isEmpty() && (adeVlrLiquido != null)) {
                final String orgCodigo = OrgaoHome.findByRseCod(rseCodigo).getOrgCodigo();
                final Date prazoIni = PeriodoHelper.getInstance().calcularAdeAnoMesIni(orgCodigo, adeCarencia, adePeriodicidade, responsavel);
                final BigDecimal taxaJurosAtual = SimulacaoHelper.calcularTaxaJuros(adeVlrLiquido, adeVlr, adePrazo, new java.util.Date(), prazoIni, orgCodigo, responsavel);

                boolean cetMaior = false;
                for (final BigDecimal taxaJurosAnterior : taxasJuros) {
                    if (taxaJurosAtual.compareTo(taxaJurosAnterior) == 1) {
                        cetMaior = true;
                        break;
                    }
                }

                if (cetMaior) {
                    final BigDecimal menorTaxa = Collections.min(taxasJuros);
                    final String chaveMensagemErro = temCET ? "mensagem.valorCETCalculadoSuperiorAosAnteriores" : "mensagem.valorTaxaCalculadaSuperiorAosAnteriores";
                    throw new AutorizacaoControllerException(chaveMensagemErro, responsavel, NumberHelper.format(menorTaxa.doubleValue(), NumberHelper.getLang(), 2, 8));
                }
            }

            // Verifica o status do convenio
            final String statusConvenio = convenio.getStatusConvenio().getScvCodigo();
            if (CodedValues.SCV_INATIVO.equals(statusConvenio)) {
                // Não pode fazer operação, pois o convenio está bloqueado
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.renegociar.este.contrato.pois.convenio.esta.bloqueado", responsavel);
            } else if (CodedValues.SCV_CANCELADO.equals(statusConvenio)) {
                // Não pode fazer operação, pois o convenio foi cancelado
                throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.renegociar.este.contrato.pois.convenio.foi.cancelado", responsavel);
            }

            if (serAtivo) {
                // Verifica o status do servidor
                if (rseTo.isExcluido()) {
                    // Servidor excluído não pode fazer novas reservas.
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.renegociar.este.contrato.pois.servidor.foi.excluido", responsavel);
                } else if (rseTo.isBloqueado()) {
                    // Servidor bloqueado não pode fazer novas reservas.
                    throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.renegociar.este.contrato.pois.servidor.esta.bloqueado", responsavel);
                }
            }

            // Verifica se na compra o servidor deve ter e-mail cadastrado
            // para recebimento dos anexos de saldo devedor.
            if (compra) {
                verificarEmailServidorCompra(rseTo.getSerCodigo(), responsavel);
            }

            // Verifica se o servidor terá margem suficiente para renegociar.
            if (verificaMargem ) {
                final BigDecimal vlrDesconto = this.calcularValorDescontoParcela(rseCodigo, svcCodigo, adeVlr);
                verificaMargemRenegociacao(rseTo, tpsMargem, vlrAtual, vlrDesconto.subtract(vlrLimiteSemMargem), csaCodigo, adeCodigos, compra, adePrazo, vlrDesconto, svcCodigo, responsavel);
            }

            return vlrAtual;

        } catch (final Exception ex) {
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            }

            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Verifica se é obrigatório que o servidor  possua e-mail cadastrado para
     * realização da compra, caso o seja obrigatório o cadastro de anexo no
     * saldo devedor e estes anexos sejam enviados para o e-mail do servidor.
     * @param adeCodigo
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    private void verificarEmailServidorCompra(String serCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            // Se exige anexo no cadastro de saldo para a compra e ( TPC_EXIGE_ANEXO_DSD_SALDO_DEVEDOR_COMPRA )
            // o servidor deve aprovar o saldo devedor de compra e ( TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA )
            // os anexos são entregues apenas por e-mail, ( TPC_EMAIL_ANEXO_SALDO_DEVEDOR_SERVIDOR )
            // então não deixa realizar a compra se o servidor não possuir e-mail.
            if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_DEVEDOR_COMPRA, CodedValues.TPC_SIM, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel) &&
                    ParamSist.paramEquals(CodedValues.TPC_EMAIL_ANEXO_SALDO_DEVEDOR_SERVIDOR, CodedValues.ANEXO_SALDO_DEVEDOR_SERVIDOR_ENVIA_EMAIL, responsavel)) {
                final Servidor servidor = ServidorHome.findByPrimaryKey(serCodigo);
                final String serEmail = servidor.getSerEmail();
                if (TextHelper.isNull(serEmail)) {
                    throw new AutorizacaoControllerException("mensagem.compra.servidor.erro.email", responsavel);
                }
            }
        } catch (final FindException ex) {
            throw new AutorizacaoControllerException("mensagem.erro.obter.dados.email.servidor.compra", responsavel, ex);
        }
    }

    /**
     *Libera margem de renegociação presa caso o contrato destino tenha sido menor
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void liberaMargemRenegociacaoPrazoExpirado(AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if (ParamSist.paramEquals(CodedValues.TPC_PRENDER_MARGEM_RENEG_CONTRATRO_NOVO_MENOR_ANTIGOS, CodedValues.TPC_SIM, responsavel)) {
                final ListaContratosRenegociacaoLiberaMargemQuery query = new ListaContratosRenegociacaoLiberaMargemQuery();
                final List<TransferObject> lstContratosRenegLiberaMargem = query.executarDTO();

                for (final TransferObject contrato : lstContratosRenegLiberaMargem) {
                    final String rseCodigo = (String) contrato.getAttribute(Columns.RSE_CODIGO);
                    final String adeCodigoRenegDestino = (String) contrato.getAttribute(Columns.ADE_CODIGO);
                    final BigDecimal diferecaPresa = (BigDecimal) contrato.getAttribute("DIFERENCA");
                    final Short adeIncMargem = (Short) contrato.getAttribute(Columns.ADE_INC_MARGEM);
                    final String csaCodigo = (String) contrato.getAttribute(Columns.CSA_CODIGO);
                    final String svcCodigo = (String) contrato.getAttribute(Columns.SVC_CODIGO);

                    final String ocaCodigo = criaOcorrenciaADE(adeCodigoRenegDestino, CodedValues.TOC_LIBERACAO_MARGEM_APOS_PRAZO_RENEGOCIACAO, ApplicationResourcesHelper.getMessage("mensagem.info.ocorrencia.margem.liberada.renegociacao", responsavel), responsavel);

                    final List<HistoricoMargemRse> historicosMargem = atualizaMargem(rseCodigo, adeIncMargem, diferecaPresa.negate(), true, false, false, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                    atualizaHistoricosMargem(historicosMargem, ocaCodigo, adeCodigoRenegDestino, CodedValues.TOC_LIBERACAO_MARGEM_APOS_PRAZO_RENEGOCIACAO);
                }
            }
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
