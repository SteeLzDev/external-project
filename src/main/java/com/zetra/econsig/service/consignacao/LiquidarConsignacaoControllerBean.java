package com.zetra.econsig.service.consignacao;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.exception.LogControllerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcCseTO;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.parametros.LiquidarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CompraContratoControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.TipoMotivoOperacaoControllerException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.servico.NaturezaRelSvc;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessaEnvioEmailEntidadesAltAde;
import com.zetra.econsig.persistence.entity.AbstractEntityHome;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Beneficiario;
import com.zetra.econsig.persistence.entity.BeneficiarioHome;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.ConsignatariaHome;
import com.zetra.econsig.persistence.entity.ContratoBeneficio;
import com.zetra.econsig.persistence.entity.ContratoBeneficioHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.DecisaoJudicialHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.Servico;
import com.zetra.econsig.persistence.entity.ServicoHome;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.StatusBeneficiario;
import com.zetra.econsig.persistence.entity.StatusContratoBeneficio;
import com.zetra.econsig.persistence.entity.StatusSolicitacao;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.consignacao.PesquisaAutorizacaoSaldoParcelasQuery;
import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentosQuery;
import com.zetra.econsig.persistence.query.parcela.ObtemTotalParcelasPagasQuery;
import com.zetra.econsig.persistence.query.periodo.ObtemDatasUltimoPeriodoExportadoQuery;
import com.zetra.econsig.service.beneficios.BeneficiarioController;
import com.zetra.econsig.service.beneficios.ContratoBeneficioController;
import com.zetra.econsig.service.compra.CompraContratoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.saldodevedor.SaldoDevedorController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoCompraEnum;
import com.zetra.econsig.values.OperacaoEConsigEnum;
import com.zetra.econsig.values.StatusBeneficiarioEnum;
import com.zetra.econsig.values.StatusContratoBeneficioEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoBeneficiarioEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

/**
 * <p>Title: LiquidarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Liquidação de Contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class LiquidarConsignacaoControllerBean extends AutorizacaoControllerBean implements LiquidarConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(LiquidarConsignacaoControllerBean.class);

    @Autowired
    private CompraContratoController compraContrato;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private CancelarConsignacaoController cancelarConsignacaoController;

    @Autowired
    private ContratoBeneficioController contratoBeneficioController;

    @Autowired
    private BeneficiarioController beneficiarioController;

    @Autowired
    private SaldoDevedorController saldoDevedorController;

    @Autowired
    private ReimplantarConsignacaoController reimplantarConsignacaoController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Override
    public void liquidar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, LiquidarConsignacaoParametros parametros, AcessoSistema responsavel) throws AutorizacaoControllerException {
        final boolean renegociacao = (parametros != null ? parametros.isRenegociacao() : false);
        final boolean podeConfirmarRenegociacao = (parametros != null ? parametros.isPodeConfirmarRenegociacao() : false);
        final boolean verificaBloqueioOperacao = (parametros != null ? parametros.isVerificaBloqueioOperacao() : true);
        final boolean verificaReservaCartaoCredito = (parametros != null ? parametros.isVerificaReservaCartaoCredito() : true);
        final boolean apenasValidacao = (parametros != null ? parametros.isApenasValidacao() : false);
        final java.util.Date ocaPeriodo = (parametros != null ? parametros.getOcaPeriodo() : null);

        if (usuarioPodeModificarAde(adeCodigo, responsavel)) {
            String sadCodigo = null;
            try {
                // Verifica se está habilitado liquidação em duas etapas, caso onde a operação de liquidação só irá alterar o status
                // do contrato para Aguard. Liquidação, e a operação confirmar liquidação irá encerrar o processo
                final boolean habilitaLiqDuasEtapas = ParamSist.paramEquals(CodedValues.TPC_HABILITA_LIQUIDACAO_EM_DUAS_ETAPAS, CodedValues.TPC_SIM, responsavel);
                final boolean habilitaModuloBeneficio = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel);
                final boolean bloqueiaLiquidacaoDiretaSubsidio = ParamSist.paramEquals(CodedValues.TPC_BLOQUEAR_LIQUIDACAO_DIRETA_ADE_SUBSIDIO, CodedValues.TPC_SIM, responsavel);

                final AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

                // Analisando se o contrato atual pode ser considerado um contrato de beneficio.
                final boolean isContratoBeneficio = (autdes.getContratoBeneficio() != null) && (autdes.getTipoLancamento() != null);

                final VerbaConvenio verbaConvenio = VerbaConvenioHome.findByPrimaryKey(autdes.getVerbaConvenio().getVcoCodigo());
                final Convenio convenio = ConvenioHome.findByPrimaryKey(verbaConvenio.getConvenio().getCnvCodigo());
                final Servico servico = ServicoHome.findByPrimaryKey(convenio.getServico().getSvcCodigo());
                final String cnvCodigo = convenio.getCnvCodigo();
                final String svcCodigo = servico.getSvcCodigo();
                final String rseCodigo = autdes.getRegistroServidor().getRseCodigo();
                final String rseStatus = autdes.getRegistroServidor().getSrsCodigo();

                // Busca parâmetros de serviço do cache
                final ParamSvcTO paramSvcCse = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                // Verifica se a CSA/Cor podem liquidar o contrato de um servidor inativo
                boolean tpsImpedirLiquidacao = false;
                if (responsavel.isCsaCor() && CodedValues.SRS_EXCLUIDO.equals(rseStatus)) {
                    tpsImpedirLiquidacao = paramSvcCse.isTpsImpedirLiquidacaoConsignacao();
                }

                // Verifica se pode liquidar contrato suspenso
                final boolean podeLiquidarAdeSuspensa = paramSvcCse.isTpsPermiteLiquidarAdeSuspensa();

                // Verifica se usuário servidor pode liquidar o contrato
                boolean servidorPodeLiquidarContrato = false;
                if (responsavel.isSer()) {
                    servidorPodeLiquidarContrato = paramSvcCse.isTpsServidorLiquidaContrato();
                }

                // Verifica se é permitido a liquidação deste contrato
                sadCodigo = autdes.getStatusAutorizacaoDesconto().getSadCodigo().trim();
                if (!CodedValues.SAD_ESTOQUE.equals(sadCodigo) &&
                        !CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) &&
                        !CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) &&
                        !CodedValues.SAD_DEFERIDA.equals(sadCodigo) &&
                        !CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) &&
                        !CodedValues.SAD_EMCARENCIA.equals(sadCodigo) &&
                        !CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo) &&
                        !(CodedValues.SAD_AGUARD_LIQUIDACAO.equals(sadCodigo) && (renegociacao || habilitaLiqDuasEtapas)) &&
                        !((CodedValues.SAD_SUSPENSA.equals(sadCodigo) || CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo)) && (podeLiquidarAdeSuspensa || servidorPodeLiquidarContrato))) {
                    throw new AutorizacaoControllerException("mensagem.erro.liquidar.consignacao.status.invalido", responsavel, autdes.getStatusAutorizacaoDesconto().getSadDescricao());
                }

                if (tpsImpedirLiquidacao) {
                    throw new AutorizacaoControllerException("mensagem.erro.liquidar.consignacao.csa.nao.tem.permissao", responsavel);
                }

                // Verifica se o servidor possui permissão para liquidar contrato e se o servidor pode liquidar consignação
                if (responsavel.isSer() && !parametros.isLiquidarPortabilidadeCartao()) {
                    if (!responsavel.temPermissao(CodedValues.FUN_LIQ_CONTRATO)) {
                        throw new AutorizacaoControllerException("mensagem.erro.liquidar.consignacao.servidor.nao.tem.permissao", responsavel);
                    } else if (!servidorPodeLiquidarContrato) {
                        throw new AutorizacaoControllerException("mensagem.erro.liquidar.consignacao.servidor.nao.pode.liquidar", responsavel);
                    }
                }


                // Verifica se a liquidação se dá em duas etapas, ou se pode ser feita diretamente.
                final boolean podeConfirmarLiquidacao = usuarioPodeConfirmarLiquidacao(Arrays.asList(autdes), renegociacao, podeConfirmarRenegociacao, responsavel);

                if (verificaBloqueioOperacao) {
                    // Verifica relacionamento de bloqueio de operação
                    verficarRelacionametoBloqueioOperacao(adeCodigo, "liquidar", responsavel);
                }

                // Verifica se o serviço do contrato está relacionado para
                // controle de saldo de parcelas CodedValues.TNT_SALDO_PARCELAS
                verificarControleSaldoParcelas(autdes, responsavel);

                if (verificaReservaCartaoCredito) {
                    // Verifica se é uma reserva de cartão de crédito e se ela pode ser cancelada.
                    if (CodedValues.SAD_DEFERIDA.equals(sadCodigo)) {
                        verificaReservaCartaoCredito(rseCodigo, autdes.getAdeVlr().negate(), cnvCodigo, responsavel);
                    }
                }

                if (!apenasValidacao) {
                    // Salva os anexos antes de liquidar, pois a situação liquidado não permite inclusão de anexos
                    if ((parametros != null) && (parametros.getAnexos() != null) && !parametros.getAnexos().isEmpty()) {
                        final String aadDescricao = ApplicationResourcesHelper.getMessage("mensagem.informacao.upload.suspensao.consignacao", responsavel);
                        final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(convenio.getOrgao().getOrgCodigo(), responsavel);
                        final String[] visibilidadeAnexos = parametros.getVisibilidadeAnexos();
                        if (visibilidadeAnexos != null) {
                            Arrays.sort(visibilidadeAnexos);
                        }
                        final String aadExibeSup = ((visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_SUPORTE) >= 0) ? "S" : "N");
                        final String aadExibeCse = ((visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_CONSIGNANTE) >= 0) ? "S" : "N");
                        final String aadExibeOrg = ((visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_ORGAO) >= 0) ? "S" : "N");
                        final String aadExibeCsa = ((visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_CONSIGNATARIA) >= 0) ? "S" : "N");
                        final String aadExibeCor = ((visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_CORRESPONDENTE) >= 0) ? "S" : "N");
                        final String aadExibeSer = ((visibilidadeAnexos != null) && (Arrays.binarySearch(visibilidadeAnexos, CodedValues.PAP_SERVIDOR) >= 0) ? "S" : "N");
                        for (final File anexo : parametros.getAnexos()) {
                            if (anexo.exists()){
                                editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), aadDescricao, new java.sql.Date(periodoAtual.getTime()),
                                        TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_LIQUIDACAO, aadExibeSup, aadExibeCse, aadExibeOrg, aadExibeCsa, aadExibeCor, aadExibeSer, responsavel);
                            }
                        }
                    }

                    // Executa a modificação do status do contrato
                    final String sadCodigoNovo = podeConfirmarLiquidacao ? CodedValues.SAD_LIQUIDADA : (
                            CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo) ? CodedValues.SAD_AGUARD_LIQUI_COMPRA : CodedValues.SAD_AGUARD_LIQUIDACAO
                    );

                    // Verifica se deve liberar margem
                    boolean liberaMargem = true;
                    if (autdes.getAdePrazo() == null) {
                        liberaMargem = !paramSvcCse.isTpsPrendeMargemLiqAdePrzIndetAteCargaMargem();
                    }

                    String ocaCodigo = null;
                    if (!sadCodigoNovo.equals(sadCodigo)) {
                        ocaCodigo = modificaSituacaoADE(autdes, sadCodigoNovo, responsavel, true, ocaPeriodo, liberaMargem);
                        if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                            // grava motivo da operacao
                            tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                            tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
                        }
                    }

                    // Verifica se o módulo benefício esta ativo para liquidar contratos do tipo natureza plano de saúde e/ou odontologico
                    // E se o contrato atual é um de beneficio.
                    final boolean contratoBeneficioSemRegra = parametroController.isReservaSaudeSemModulo(svcCodigo, responsavel);
                    if (habilitaModuloBeneficio && isContratoBeneficio && !contratoBeneficioSemRegra) {
                        final List<String> tntSubsidio = new ArrayList<>();
                        tntSubsidio.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO);
                        tntSubsidio.addAll(CodedValues.TNT_BENEFICIO_SUBSIDIO_PRO_RATA);

                        final List<String> tntMensalidade = new ArrayList<>();
                        tntMensalidade.addAll(CodedValues.TNT_BENEFICIO_PRO_RATA);
                        tntMensalidade.addAll(CodedValues.TNT_BENEFICIO_MENSALIDADE);

                        // Bloqueia liquidação caso o ade seja do tipo subsídio
                        if (bloqueiaLiquidacaoDiretaSubsidio) {
                            // Verifica se o contrato é de subsídio
                            if (tntSubsidio.contains(autdes.getTipoLancamento().getTipoNatureza().getTntCodigo())) {
                                List<RelacionamentoAutorizacao> relacionamentoAutorizacao = new ArrayList<>();
                                // Procura relacionamentoAutorização do tipo Subsídio
                                relacionamentoAutorizacao = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, tntSubsidio);
                                boolean mensalidadeAtiva = false;
                                for (final RelacionamentoAutorizacao relacionamento : relacionamentoAutorizacao) {
                                    final AutDesconto autDescontoOrigem = relacionamento.getAutDescontoByAdeCodigoOrigem();
                                    final boolean isContratoBeneficioRelacionamento = (autDescontoOrigem.getContratoBeneficio() != null) && (autDescontoOrigem.getTipoLancamento() != null);
                                    final String tntCodigo = autDescontoOrigem.getTipoLancamento().getTipoNatureza().getTntCodigo();
                                    // Verifica se o autDescontoOrigem tem tipoNatureza pro_rata ou mensalidade
                                    if (isContratoBeneficioRelacionamento && tntMensalidade.contains(tntCodigo)) {
                                        if (CodedValues.SAD_CODIGOS_ATIVOS.contains(autDescontoOrigem.getStatusAutorizacaoDesconto().getSadCodigo())) {
                                            mensalidadeAtiva = true;
                                            break;
                                        }
                                    }
                                }

                                if (mensalidadeAtiva) {
                                    throw new AutorizacaoControllerException("mensagem.erro.liquidar.consignacao.subsidio.com.mensalidade.ativa", responsavel);
                                }
                            }
                        }

                        List<RelacionamentoAutorizacao> relacionamentoAutorizacao = new ArrayList<>();
                        if (tntMensalidade.contains(autdes.getTipoLancamento().getTipoNatureza().getTntCodigo())) {
                            final List<String> sadAtivos = new ArrayList<>();
                            sadAtivos.addAll(CodedValues.SAD_CODIGOS_ATIVOS);
                            sadAtivos.add(CodedValues.SAD_AGUARD_LIQUIDACAO);
                            sadAtivos.add(CodedValues.SAD_AGUARD_LIQUI_COMPRA);
                            relacionamentoAutorizacao = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_BENEFICIO_SUBSIDIO, sadAtivos);

                            // Percorre os adeDestino para realizar a liquidação
                            if (!relacionamentoAutorizacao.isEmpty()) {
                                for (final RelacionamentoAutorizacao relacionamento : relacionamentoAutorizacao) {
                                    final AutDesconto autDescontoDestino = relacionamento.getAutDescontoByAdeCodigoDestino();
                                    // Garantido que é um contrato de beneficio.
                                    // Garantido que o ade é de subsidio, evitando assim algum problema de relacionamento na tb_relacinamento autorização
                                    final boolean isContratoBeneficioRelacionamento = (autDescontoDestino.getContratoBeneficio() != null) && (autDescontoDestino.getTipoLancamento() != null);
                                    final String tntCodigo = autDescontoDestino.getTipoLancamento().getTipoNatureza().getTntCodigo();
                                    if (isContratoBeneficioRelacionamento && tntSubsidio.contains(tntCodigo)) {
                                        liquidar(relacionamento.getAutDescontoByAdeCodigoDestino().getAdeCodigo(), tipoMotivoOperacao, parametros, responsavel);
                                    }
                                }
                            }
                        }
                    }

                    if (podeConfirmarLiquidacao) {
                        // Cria ocorrência de liquidação de contrato
                        if (!CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo)) {
                            criaOcorrenciaADE(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO, ApplicationResourcesHelper.getMessage("mensagem.informacao.ptf.liquidacao.contrato", responsavel), null, null, null, ocaPeriodo, null, responsavel);
                            if (habilitaModuloBeneficio && isContratoBeneficio && CodedValues.TNT_BENEFICIO_MENSALIDADE.contains(autdes.getTipoLancamento().getTipoNatureza().getTntCodigo()) && !contratoBeneficioSemRegra) {
                                String tmoCodigo = null;
                                if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                                    tmoCodigo = tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO) != null ? tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO).toString() : null;
                                }

                                contratoBeneficioController.criaOcorrenciaContratoBeneficio(autdes.getContratoBeneficio().getCbeCodigo(),
                                        CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO,
                                        ApplicationResourcesHelper.getMessage("mensagem.beneficio.ocorrencia.contrato.beneficio.exclusao", responsavel),
                                        null, tmoCodigo, responsavel);
                            }
                        }

                        // Se contrato envolvido em processo de compra ...
                        if (CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo)) {
                            final String codigoAdeDestinoCompra = compraContrato.recuperarAdeDestinoCompra(adeCodigo);

                            boolean permiteLivreLiquidacao = false;
                            if (responsavel.isCseSupOrg()) {
                                permiteLivreLiquidacao = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQ_CICLO_VIDA_FIXO_CSE_ORG_SUP, CodedValues.TPC_SIM, responsavel);
                            } else if (responsavel.isCsaCor()) {
                                permiteLivreLiquidacao = ParamSist.paramEquals(CodedValues.TPC_PERMITE_LIQ_CICLO_VIDA_FIXO_CSA_COR, CodedValues.TPC_SIM, responsavel);
                            } else if (responsavel.isSistema()) {
                                permiteLivreLiquidacao = true;
                            }

                            // Se o sistema exige o ciclo de vida fixo no processo de compra, impede
                            // que a liquidação do contrato ocorra antes da informação de pagamento de saldo devedor,
                            // caso o contrato gerado pela compra não seja da própria consignatária ou caso o sistema
                            // não permita liquidar em qualquer etapa do processo de compra.
                            if (ParamSist.getBoolParamSist(CodedValues.TPC_CICLO_VIDA_FIXO_PROCESSO_COMPRA, responsavel)
                                    && !permiteLivreLiquidacao) {
                                // Recupera consignatária do contrato vendido.
                                final Consignataria csaOrigem = ConsignatariaHome.findByAdeCodigo(adeCodigo);
                                // Recupera consignatária do novo contrato.
                                final Consignataria csaDestino = ConsignatariaHome.findByAdeCodigo(codigoAdeDestinoCompra);

                                // Se a consignatária do contrato novo é a própria do contrato sendo liquidadeo, não é necessário haver
                                // pagamento de saldo devedor.
                                if (!csaOrigem.getCsaCodigo().equals(csaDestino.getCsaCodigo())) {
                                    if (!saldoDevedorController.existeSaldoDevedorPago(adeCodigo, responsavel)) {
                                        throw new AutorizacaoControllerException("mensagem.erro.nao.possivel.liquidar.contrato.pagamento.saldo.devedor.nao.informado", responsavel);
                                    }
                                }
                            }

                            // Cria ocorrência especifica de liquidação
                            criaOcorrenciaADE(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.liquidacao.contrado.comprado", responsavel), null, null, null, ocaPeriodo, null, responsavel);
                            if (habilitaModuloBeneficio && isContratoBeneficio && CodedValues.TNT_BENEFICIO_MENSALIDADE.contains(autdes.getTipoLancamento().getTipoNatureza().getTntCodigo()) && !contratoBeneficioSemRegra) {
                                String tmoCodigo = null;
                                if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                                    tmoCodigo = tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO) != null ? tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO).toString() : null;
                                }

                                contratoBeneficioController.criaOcorrenciaContratoBeneficio(autdes.getContratoBeneficio().getCbeCodigo(),
                                        CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO,
                                        ApplicationResourcesHelper.getMessage("mensagem.beneficio.ocorrencia.contrato.beneficio.exclusao", responsavel),
                                        null, tmoCodigo, responsavel);
                            }

                            // Atualiza relacionamento de compra
                            compraContrato.updateRelAutorizacaoCompra(adeCodigo, OperacaoCompraEnum.LIQUIDACAO_CONTRATO, responsavel);

                            if (ParamSist.paramEquals(CodedValues.TPC_SUSPENDE_DESC_CSA_BLOQ_CONTROLE_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                                // OBS: Deve ser feito após a inclusão da ocorrência TOC_TARIF_LIQUIDACAO
                                compraContrato.reativarDescontoAposPendenciaCompra(adeCodigo, true, ocaPeriodo, responsavel);
                            }

                            // Finaliza a compra, caso esta seja a última consignação comprada a ser liquidada
                            final boolean concluido = compraContrato.ultimaAdeFinalizacaoCompra(adeCodigo, codigoAdeDestinoCompra);
                            if (concluido) {
                                compraContrato.finalizarCompra(codigoAdeDestinoCompra, responsavel);
                            }
                        }

                        // Cancela contratos de insere/altera à espera de confirmação ligados a este.
                        final Collection<RelacionamentoAutorizacao> lstRadInsereAltera = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTRATO_GERADO_INSERE_ALTERA);

                        // Cancela contratos de insere/altera à espera de confirmação ligados a este.
                        for (final RelacionamentoAutorizacao radInsereAltera : lstRadInsereAltera) {
                            final String adeCodigoDestInsAlt = radInsereAltera.getAdeCodigoDestino();
                            final AutDesconto adeDestInsAlt = AutDescontoHome.findByPrimaryKey(adeCodigoDestInsAlt);
                            final String sadCodigoDest = adeDestInsAlt.getStatusAutorizacaoDesconto().getSadCodigo().trim();

                            if (!CodedValues.SAD_CANCELADA.equals(sadCodigoDest) && !CodedValues.SAD_LIQUIDADA.equals(sadCodigoDest) &&
                                    !CodedValues.SAD_CONCLUIDO.equals(sadCodigoDest) && !CodedValues.SAD_ENCERRADO.equals(sadCodigoDest) && !CodedValues.SAD_INDEFERIDA.equals(sadCodigoDest)) {
                                cancelarConsignacaoController.cancelar(adeCodigoDestInsAlt, tipoMotivoOperacao, responsavel);
                            }
                        }

                        // Caso o contrato tenha solicitações de saldo pendentes, realiza o cancelamento das solicitações
                        if (saldoDevedorController.temSolicitacaoSaldoDevedor(adeCodigo, true, responsavel)) {
                            saldoDevedorController.atualizaStatusSolicitacaoSaldoDevedor(adeCodigo, StatusSolicitacaoEnum.CANCELADA, responsavel);
                        }

                        /*
                         * Se o sistema permite bloqueio de consignatária pela não liquidação de contrato com saldo pago e com anexo pelo servidor,
                         * finaliza as solicitações na liquidação do contrato
                         */
                        if (ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_BLOQ_CSA_N_LIQ_ADE_SALDO_PAGO_SER, responsavel)) {
                            saldoDevedorController.finalizaSolicitacaoSaldoDevedorLiquidacaoContrato(adeCodigo, responsavel);
                        }

                        if (CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo)) {
                            // Envia email de notificação de liquidação de contrato comprado
                            EnviaEmailHelper.enviarEmailCompraContrato(EnviaEmailHelper.TIPO_LIQUIDACAO_COMPRA_CONTRATO, adeCodigo, null, responsavel);
                        }

                        // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
                        // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email,
                        // se o param. para o envio estiver Sim. A liquidação de compra já possui fluxo de envio de e-mail próprio
                        if (!CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo)) {
                            final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.LIQUIDAR_CONSIGNACAO, adeCodigo, null, tipoMotivoOperacao, responsavel);
                            processoEmail.start();
                        }

                        // DESENV-16877: Um contrato que seja de benefício e que não faça parte das regras deve ser liquidado a consignação e o beneficio e além disso se for titular deve liquidar todos os contratos
                        // de beneficío de seus dependentes e consignações.
                        if (contratoBeneficioSemRegra && !TextHelper.isNull(autdes.getContratoBeneficio().getCbeCodigo())) {
                            final ContratoBeneficio contratoBeneficio = ContratoBeneficioHome.findByPrimaryKey(autdes.getContratoBeneficio().getCbeCodigo());
                            contratoBeneficio.setStatusContratoBeneficio(new StatusContratoBeneficio(StatusContratoBeneficioEnum.CANCELADO.getCodigo()));
                            String tmoCodigo = null;
                            if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                                tmoCodigo = tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO) != null ? tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO).toString() : null;
                            }

                            contratoBeneficioController.criaOcorrenciaContratoBeneficio(autdes.getContratoBeneficio().getCbeCodigo(),
                                    CodedValues.TOC_EXCLUSAO_CONTRATO_BENEFICIO,
                                    ApplicationResourcesHelper.getMessage("mensagem.beneficio.ocorrencia.contrato.beneficio.exclusao", responsavel),
                                    null, tmoCodigo, responsavel);
                            contratoBeneficioController.update(contratoBeneficio, tmoCodigo, responsavel);

                            if (contratoBeneficio.getBeneficiario().getTipoBeneficiario().getTibCodigo().equals(TipoBeneficiarioEnum.TITULAR.tibCodigo)) {
                                final List<String> scbCodigos = new ArrayList<>();
                                scbCodigos.add(StatusContratoBeneficioEnum.ATIVO.getCodigo());

                                final List<String> tibCodigos = new ArrayList<>();
                                tibCodigos.add(TipoBeneficiarioEnum.DEPENDENTE.tibCodigo);

                                final CustomTransferObject criterio = new CustomTransferObject();
                                criterio.setAttribute(Columns.RSE_CODIGO, rseCodigo);
                                criterio.setAttribute(Columns.SCB_CODIGO, scbCodigos);
                                criterio.setAttribute(Columns.NSE_CODIGO, servico.getNaturezaServico().getNseCodigo());
                                criterio.setAttribute(Columns.TIB_CODIGO, tibCodigos);
                                criterio.setAttribute(Columns.CSA_CODIGO, convenio.getConsignataria().getCsaCodigo());
                                criterio.setAttribute(Columns.BEN_CODIGO, contratoBeneficio.getBeneficio().getBenCodigo());
                                criterio.setAttribute("reservaSemRegrasModulo", "true");
                                final List<TransferObject> contratosBeneficiariosDependentes = contratoBeneficioController.listarContratosBeneficioPorRegistroServidorQuery(criterio, responsavel);

                                for(final TransferObject contratoDependente : contratosBeneficiariosDependentes) {
                                    liquidar(contratoDependente.getAttribute(Columns.ADE_CODIGO).toString(), tipoMotivoOperacao, parametros, responsavel);
                                }
                            }
                        }
                    } else if (ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_EMAIL_CSE_LIQUIDAR_CONTRATO, responsavel)) {
                        EnviaEmailHelper.enviarEmailCseLiquidacaoAde(autdes.getAdeCodigo(), responsavel);
                    }

                    // Cria o registro de decisão judicial, caso informado e o sistema permita
                    if ((responsavel.isCseSupOrg() || responsavel.isCsa()) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                        if ((parametros != null) && !TextHelper.isNull(parametros.getTjuCodigo()) && !TextHelper.isNull(parametros.getDjuTexto()) && !TextHelper.isNull(parametros.getDjuData()) && !TextHelper.isNull(ocaCodigo)) {
                            DecisaoJudicialHome.create(ocaCodigo, parametros.getTjuCodigo(), parametros.getCidCodigo(), parametros.getDjuNumProcesso(), parametros.getDjuData(), parametros.getDjuTexto(), null);
                        }
                    }

                    if (CodedValues.FUN_EXECUTAR_DECISAO_JUDICIAL.equals(responsavel.getFunCodigo())) {
                        setDadoAutDesconto(autdes.getAdeCodigo(), CodedValues.TDA_AFETADA_DECISAO_JUDICIAL, CodedValues.TPC_SIM, responsavel);
                    }

                    // DESENV-9242 : Verifica se o beneficiário possui algum contrato benefício (mensalidade) ativo
                    if (habilitaModuloBeneficio && isContratoBeneficio && !contratoBeneficioSemRegra) {
                        if (CodedValues.TNT_BENEFICIO_MENSALIDADE.contains(autdes.getTipoLancamento().getTipoNatureza().getTntCodigo())) {
                            final Beneficiario beneficiario = BeneficiarioHome.findDetachedByAdeCodigo(autdes.getAdeCodigo(), responsavel);
                            final List<AutDesconto> autDescontos = AutDescontoHome.findByBeneficiarioAndInTntCodigoAndNotInSadCodigo(beneficiario.getBfcCodigo(), CodedValues.TNT_BENEFICIO_MENSALIDADE, CodedValues.SAD_CODIGOS_INATIVOS, responsavel);

                            // Se não possuir contratos, trocar status do beneficiário para inativo
                            if((autDescontos == null) || autDescontos.isEmpty()) {
                                beneficiario.setStatusBeneficiario(new StatusBeneficiario(StatusBeneficiarioEnum.INATIVO.sbeCodigo));
                                beneficiarioController.update(beneficiario, responsavel);
                            }
                        }
                    }

                    // DESENV-12089 : Verifica se o contrato tem solicitação de autorização de liquidação
                    // Exite essa verificação quando o parâmetro 454 está habilitado e se estiver executa o método finalizaSolicitacaoSaldoDevedorLiquidacaoContrato
                    // porém tenho que verificar se existe ainda neste momento, caso o parâmetro esteja desabilitado e por algum motivo ainda tenha solicitação.
                    if (!ParamSist.getBoolParamSist(CodedValues.TPC_PERMITE_BLOQ_CSA_N_LIQ_ADE_SALDO_PAGO_SER, responsavel)) {
                        final String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO.getCodigo()};
                        final List<SolicitacaoAutorizacao> solicitacoes = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, StatusSolicitacaoEnum.PENDENTE.getCodigo());

                        if ((solicitacoes != null) && !solicitacoes.isEmpty()) {
                            for (final SolicitacaoAutorizacao solicitacaoAutorizacao : solicitacoes) {
                                solicitacaoAutorizacao.setStatusSolicitacao(new StatusSolicitacao(StatusSolicitacaoEnum.FINALIZADA.getCodigo()));
                                AbstractEntityHome.update(solicitacaoAutorizacao);
                            }
                        }
                    }


                    // DESENV-16740 : cria ocorrência de confirmação de liquidação caso a consignatária exija dupla
                    // confirmação para operações que resultem em liquidações de contratos.
                    if (efetuarLiquidacaoDuasEtapas(responsavel) && exigirDuplaConfirmacaoLiquidacao(responsavel)) {
                        // Se a liquidação é em duas etapas com dupla confirmação obrigatória, verifica se o usuário que
                        // realiza a operação tem permissão para contar como uma confirmação de liquidação
                        if ((CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_CONF_LIQUIDACAO_COMPRA)) ||
                                (!CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo) && responsavel.temPermissao(CodedValues.FUN_CONF_LIQUIDACAO))) {
                            criaOcorrenciaADE(adeCodigo, CodedValues.TOC_CONFIRMACAO_LIQUIDACAO_ADE, ApplicationResourcesHelper.getMessage("mensagem.ocorrencia.autorizacao.confirmacao.liquidacao", responsavel), null, null, null, ocaPeriodo, null, responsavel);
                        }
                    }

                    // Gera o Log de auditoria
                    final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.LIQUIDAR_CONSIGNACAO, Log.LOG_INFORMACAO);
                    logDelegate.setAutorizacaoDesconto(adeCodigo);
                    logDelegate.setStatusAutorizacao(sadCodigoNovo);
                    logDelegate.write();
                }
            } catch (final Exception ex) {
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                LOG.error(ex.getMessage(), ex);
                if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                    // Converte a exceção para AutorizacaoControllerException
                    AutorizacaoControllerException aex = (AutorizacaoControllerException) ex;
                    // Se é liquidação de contrato relacionado para a compra e deu erro
                    // de margem insuficiente, então altera a mensagem de erro para o usuário
                    if ((sadCodigo != null) && CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo) &&
                            (aex.getMessageKey() != null) && "mensagem.margemInsuficiente".equals(aex.getMessageKey())) {
                        aex = new AutorizacaoControllerException("mensagem.erro.liquidacao.margem.insuficiente.processo.compra", responsavel);
                    }
                    throw aex;
                } else if (ex.getClass().equals(CompraContratoControllerException.class) || ex.getClass().equals(ViewHelperException.class)) {
                    throw new AutorizacaoControllerException(ex);
                }

                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Executa a verificação do relacionamento entre serviços para saldo de parcelas.
     * Verifica se o serviço da consignação passada por parâmetro é a origem ou o
     * destino de um relacionamento de saldo de parcelas. Caso sim, efetua o controle
     * necessário para a liquidação do contrato.
     *
     * @param autdes      : contrato que está sendo liquidado
     * @param responsavel : responsável pela operação
     * @throws AutorizacaoControllerException
     */
    private void verificarControleSaldoParcelas(AutDesconto autdes, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            if (NaturezaRelSvc.getInstance().exists(CodedValues.TNT_SALDO_PARCELAS)) {
                // Busca o convênio/serviço do contrato
                final VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(autdes.getVerbaConvenio().getVcoCodigo());
                final Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());
                final String svcCodigo = cnvBean.getServico().getSvcCodigo();
                final String csaCodigo = cnvBean.getConsignataria().getCsaCodigo();
                final String rseCodigo = autdes.getRegistroServidor().getRseCodigo();

                // Verifica se o serviço deste contrato está relacionado a outro
                // serviço para saldo de parcelas
                List<TransferObject> servicos = null;

                // Se o serviço do contrato é o destino em um saldo de parcelas
                ListaRelacionamentosQuery queryRel = new ListaRelacionamentosQuery();
                queryRel.tntCodigo = CodedValues.TNT_SALDO_PARCELAS;
                queryRel.svcCodigoOrigem = null;
                queryRel.svcCodigoDestino = svcCodigo;
                servicos = queryRel.executarDTO();
                if ((servicos != null) && (servicos.size() > 0)) {
                    // Busca nos serviços relacionados o contrato correspondente
                    // a este saldo de parcelas. Ordena pela data, de modo que
                    // o ultimo contrato é o mais novo
                    final List<String> svcs = new ArrayList<>();
                    for (int i = 0; i < servicos.size(); i++) {
                        final CustomTransferObject cto = (CustomTransferObject) servicos.get(i);
                        svcs.add(cto.getAttribute(Columns.RSV_SVC_CODIGO_ORIGEM).toString());
                    }

                    final PesquisaAutorizacaoSaldoParcelasQuery query = new PesquisaAutorizacaoSaldoParcelasQuery();
                    query.adeCodigo = autdes.getAdeCodigo();
                    query.rseCodigo = rseCodigo;
                    query.csaCodigo = csaCodigo;
                    query.svcCodigos = svcs;
                    query.adeIndice = autdes.getAdeIndice();
                    final List<TransferObject> ades = query.executarDTO();

                    // Contador para o numero de contratos abertos (que tem parcelas pagas)
                    int countAbertos = 0;
                    // Contador para o numero de contratos concluídos
                    int countConcluidos = 0;

                    // Código da autorização relacionada que está concluída
                    String adeCodigoConcluido = null;

                    String sadCodigo = null;
                    Integer adePrdPagas = null;

                    for (final TransferObject next : ades) {
                        // Navega na lista de contratos e verifica os status das autorizações
                        sadCodigo = next.getAttribute(Columns.ADE_SAD_CODIGO).toString();
                        adePrdPagas = (Integer) next.getAttribute(Columns.ADE_PRD_PAGAS);

                        if (((adePrdPagas != null) && (adePrdPagas.intValue() > 0)) &&
                                (!CodedValues.SAD_SOLICITADO.equals(sadCodigo) &&
                                        !CodedValues.SAD_AGUARD_CONF.equals(sadCodigo) &&
                                        !CodedValues.SAD_AGUARD_DEFER.equals(sadCodigo) &&
                                        !CodedValues.SAD_INDEFERIDA.equals(sadCodigo) &&
                                        !CodedValues.SAD_DEFERIDA.equals(sadCodigo) &&
                                        !CodedValues.SAD_CANCELADA.equals(sadCodigo) &&
                                        !CodedValues.SAD_LIQUIDADA.equals(sadCodigo) &&
                                        !CodedValues.SAD_ENCERRADO.equals(sadCodigo))) {

                            // A ordenação da pesquisa é pela data, ou seja, os códigos
                            // ficarão setados com os contratos mais novos.
                            if (CodedValues.SAD_CONCLUIDO.equals(sadCodigo)) {
                                countConcluidos++;
                                adeCodigoConcluido = next.getAttribute(Columns.ADE_CODIGO).toString();
                            } else {
                                countAbertos++;
                            }
                        }
                    }

                    if ((countAbertos == 0) && (countConcluidos == 0)) {
                        // Se não tiver contrato relacionado ou o contrato relacionado estiver nas situações
                        // (0, 1, 2, 3, 4, 7, 8) então envia mensagem de erro para o usuário.
                        throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.liquidada.pois.relacionamento.saldo.parcelas.nao.existe.ou.incorreto", responsavel);

                    } else if (countAbertos == 0) {
                        // Só encontrou contratos concluidos (um ou vários). Procede a liquidação
                        // fazendo a liberação da margem de acordo com o contrato concluido.
                        final AutDesconto autdesRel = AutDescontoHome.findByPrimaryKey(adeCodigoConcluido);

                        // Se o contrato estiver concluido, então mudar ade_inc_margem do
                        // saldo de parcelas para 1/2/3 e salvar ade_vlr no campo ade_vlr_sdo_ret
                        // colocando o valor do contrato relacionado no ade_vlr. Proceder a liquidação
                        // fazendo a liberação da margem no valor do contrato relacionado.
                        autdes.setAdeIncMargem(autdesRel.getAdeIncMargem());
                        autdes.setAdeIntFolha(CodedValues.INTEGRA_FOLHA_SIM);
                        autdes.setAdeVlrSdoRet(autdes.getAdeVlr());
                        autdes.setAdeVlr(autdesRel.getAdeVlr());
                        AbstractEntityHome.update(autdes);

                    } else {
                        // Se tiver contratos abertos, mesmo que tenha outros concluidos,
                        // então só liquida o saldo de parcelas sem alterar a margem do servidor,
                        // colocando ade_inc_margem igual a 0.
                        autdes.setAdeIncMargem(CodedValues.INCIDE_MARGEM_NAO);
                        autdes.setAdeIntFolha(CodedValues.INTEGRA_FOLHA_SIM);
                        AbstractEntityHome.update(autdes);
                    }
                }

                // Se o serviço do contrato é a origem em um saldo de parcelas
                servicos = null;
                queryRel = new ListaRelacionamentosQuery();
                queryRel.tntCodigo = CodedValues.TNT_SALDO_PARCELAS;
                queryRel.svcCodigoOrigem = svcCodigo;
                queryRel.svcCodigoDestino = null;
                servicos = queryRel.executarDTO();
                if ((servicos != null) && (servicos.size() > 0)) {
                    // Busca nos serviços relacionados o contrato de saldo de
                    // parcelas correspondente a este contrato
                    final List<String> svcs = new ArrayList<>();
                    for (int i = 0; i < servicos.size(); i++) {
                        final CustomTransferObject cto = (CustomTransferObject) servicos.get(i);
                        svcs.add(cto.getAttribute(Columns.RSV_SVC_CODIGO_DESTINO).toString());
                    }

                    final PesquisaAutorizacaoSaldoParcelasQuery query = new PesquisaAutorizacaoSaldoParcelasQuery();
                    query.adeCodigo = autdes.getAdeCodigo();
                    query.rseCodigo = rseCodigo;
                    query.csaCodigo = csaCodigo;
                    query.svcCodigos = svcs;
                    query.adeIndice = autdes.getAdeIndice();
                    final List<TransferObject> ades = query.executarDTO();

                    for (final TransferObject next : ades) {
                        // Navega na lista de contratos e verifica os status das autorizações
                        final String sadCodigo = next.getAttribute(Columns.ADE_SAD_CODIGO).toString();
                        final String adeCodigoRelacionado = next.getAttribute(Columns.ADE_CODIGO).toString();

                        // Se o saldo de parcelas está numa situação passível de liquidação
                        // então liquida, sem alterar a margem
                        if (CodedValues.SAD_DEFERIDA.equals(sadCodigo) ||
                                CodedValues.SAD_EMANDAMENTO.equals(sadCodigo) ||
                                CodedValues.SAD_SUSPENSA.equals(sadCodigo) ||
                                CodedValues.SAD_SUSPENSA_CSE.equals(sadCodigo) ||
                                CodedValues.SAD_AGUARD_LIQUIDACAO.equals(sadCodigo) ||
                                CodedValues.SAD_AGUARD_LIQUI_COMPRA.equals(sadCodigo) ||
                                CodedValues.SAD_ESTOQUE.equals(sadCodigo) ||
                                CodedValues.SAD_ESTOQUE_NAO_LIBERADO.equals(sadCodigo) ||
                                CodedValues.SAD_ESTOQUE_MENSAL.equals(sadCodigo) ||
                                CodedValues.SAD_EMCARENCIA.equals(sadCodigo)) {

                            // Se o usuário liquidar o contrato antes de liquidar o saldo de parcelas então
                            // libera margem do contrato e liquida o saldo de parcelas, sem alterar margem,
                            // colocando ade_inc_margem igual a 0.
                            final AutDesconto autdesRel = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigoRelacionado);
                            autdesRel.setAdeIncMargem(CodedValues.INCIDE_MARGEM_NAO);
                            autdesRel.setAdeIntFolha(CodedValues.INTEGRA_FOLHA_SIM);
                            AbstractEntityHome.update(autdesRel);
                            liquidar(adeCodigoRelacionado, null, null, responsavel);
                        }
                    }
                }
            }
        } catch (HQueryException | FindException | UpdateException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Método disponível para a desliquidação de um contrato.
     * @param adeCodigo Código do contrato.
     * @param responsavel Responsável pela operação.
     * @throws AutorizacaoControllerException Exceção padrão da classe.
     * @throws TipoMotivoOperacaoControllerException
     */
    @Override
    public void desliquidar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        desliquidar(adeCodigo, true, false, false, tipoMotivoOperacao, responsavel);
    }

    /**
     * Método disponível para a desliquidação de um contrato.
     * @param adeCodigo Código do contrato.
     * @param validarMargem Define se a margem será validada (desliquidação avançada)
     * @param reimplantar Define se reimplantará o contrato (desliquidação avançada)
     * @param tipoMotivoOperacao Objeto motivo de operação
     * @param responsavel Responsável pela operação.
     * @throws AutorizacaoControllerException
     * @throws TipoMotivoOperacaoControllerException
     */
    @Override
    public void desliquidar(String adeCodigo, boolean validarMargem, boolean reimplantar, boolean ignoraCompra, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            desliquidarContrato(adeCodigo, validarMargem, reimplantar, ignoraCompra, tipoMotivoOperacao, responsavel);

            // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
            // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
            final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.DESLIQUIDAR_CONTRATO, adeCodigo, null, tipoMotivoOperacao, responsavel);
            processoEmail.start();

        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            LOG.error(ex.getMessage(), ex);
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else if (ex.getClass().equals(TipoMotivoOperacaoControllerException.class)) {
                throw new AutorizacaoControllerException(ex);
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Método utilizado na desliquidação de uma lista de contratos.
     * @param adeCodigos Lista dos códigos dos contratos.
     * @param ocsObs Observação a ser inserida na ocorrência de informação.
     * @param tmoCodigo Código do tipo de motivo da operação.
     * @param responsavel Responsável pela operação.
     * @throws AutorizacaoControllerException Exceção padrão da classe.
     */
    @Override
    public void desliquidarAoCancelarRenegociacao(List<String> adeCodigos, String ocsObs, String tmoCodigo, boolean permiteCancelarRenegMantendoMargemNegativa, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            for (final String adeCodigo : adeCodigos) {
                desliquidarContrato(adeCodigo, !permiteCancelarRenegMantendoMargemNegativa, false, false, null, permiteCancelarRenegMantendoMargemNegativa, responsavel);

                // Cria ocorrência específica de cancelamento de renegociação
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_CANCELAMENTO_RENEGOCIACAO, ocsObs, tmoCodigo, responsavel);

                // Gera o Log de auditoria
                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.DESLIQUIDAR_CONSIGNACAO, Log.LOG_INFORMACAO);
                logDelegate.setAutorizacaoDesconto(adeCodigo);
                logDelegate.write();

                // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
                // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
                final CustomTransferObject tipoMotivoOperacao = new CustomTransferObject();
                tipoMotivoOperacao.setAttribute(Columns.TMO_CODIGO, tmoCodigo);
                tipoMotivoOperacao.setAttribute(Columns.OCA_OBS, ocsObs);
                final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.DESLIQUIDAR_CONTRATO, adeCodigo, null, tipoMotivoOperacao, responsavel);
                processoEmail.start();
            }
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            LOG.error(ex.getMessage(), ex);
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }


    @Override
    public boolean liquidacaoJaEnviadaParaFolha(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        AutDesconto adeBean;
        try {
            adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

            final VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
            final Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());

            // Pega as ultimas datas de exportação
            final ObtemDatasUltimoPeriodoExportadoQuery queryPeriodoExportacao = new ObtemDatasUltimoPeriodoExportadoQuery();
            queryPeriodoExportacao.orgCodigo = cnvBean.getOrgao().getOrgCodigo();
            final List<TransferObject> periodoExportacaoList = queryPeriodoExportacao.executarDTO();
            final Date dataFimExportacao = ((periodoExportacaoList != null) && (periodoExportacaoList.size() > 0)) ? (Date) periodoExportacaoList.get(0).getAttribute(Columns.HIE_DATA_FIM) : null;

            // Remove ocorrencia de tarifação de liquidação
            final Collection<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO);
            for (final OcorrenciaAutorizacao ocaBean : ocorrencias) {
                // Se a liquidação já foi enviada para a folha então gera uma exceção
                if ((dataFimExportacao != null) && (dataFimExportacao.compareTo(ocaBean.getOcaData()) >= 0)) {
                    return true;
                }
            }
        } catch (final FindException ex) {
            LOG.error(ex.getMessage(), ex);
        } catch (final HQueryException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }

        return false;
    }

    private void desliquidarContrato(String adeCodigo, boolean opcaoValidaMargem, boolean reimplantar, boolean ignoraCompra, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException, TipoMotivoOperacaoControllerException {
        desliquidarContrato(adeCodigo, opcaoValidaMargem, reimplantar, ignoraCompra, tipoMotivoOperacao, false, responsavel);
    }

    /**
     * Método genérico para desliquidação de um contrato.
     * @param adeCodigo Contrato a ser desliquidado.
     * @param responsavel Responsável pela operação.
     * @throws AutorizacaoControllerException Exceção padrão da classe.
     * @throws TipoMotivoOperacaoControllerException
     */
    private void desliquidarContrato(String adeCodigo, boolean opcaoValidaMargem, boolean reimplantar, boolean ignoraCompra, CustomTransferObject tipoMotivoOperacao, boolean permiteCancelarRenegMantendoMargemNegativa, AcessoSistema responsavel) throws AutorizacaoControllerException, TipoMotivoOperacaoControllerException {
        try {
            final AutDesconto adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
            final String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo().trim();

            if (!CodedValues.SAD_LIQUIDADA.equals(sadCodigo)) {
                throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.desliquidada.porque.situacao.atual.dela.nao.permite.esta.operacao", responsavel, adeBean.getStatusAutorizacaoDesconto().getSadDescricao());
            }

            // Bloquear desliquidação de contratos com origem em processo de compra
            if (!ignoraCompra) {
                final Collection<RelacionamentoAutorizacao> relacionamentos = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CONTROLE_COMPRA);

                if (!relacionamentos.isEmpty()) {
                    for (final RelacionamentoAutorizacao rel : relacionamentos) {
                        if (!rel.getStatusCompra().getStcCodigo().equalsIgnoreCase(CodedValues.STC_CANCELADO.toString())) {
                            throw new AutorizacaoControllerException("mensagem.erro.desliquidar.contrato.compra", responsavel);
                        }
                    }
                }
            }

            final RegistroServidor rseBean = RegistroServidorHome.findByPrimaryKey(adeBean.getRegistroServidor().getRseCodigo());
            final VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
            final Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());
            final String csaCodigo = cnvBean.getConsignataria().getCsaCodigo();
            final String svcCodigo = cnvBean.getServico().getSvcCodigo();

            // Pega as ultimas datas de exportação
            final ObtemDatasUltimoPeriodoExportadoQuery queryPeriodoExportacao = new ObtemDatasUltimoPeriodoExportadoQuery();
            queryPeriodoExportacao.orgCodigo = cnvBean.getOrgao().getOrgCodigo();
            final List<TransferObject> periodoExportacaoList = queryPeriodoExportacao.executarDTO();
            final Date dataFimExportacao = ((periodoExportacaoList != null) && (periodoExportacaoList.size() > 0)) ? (Date) periodoExportacaoList.get(0).getAttribute(Columns.HIE_DATA_FIM) : null;

            // verifica se usuário tem permissão de desliquidação avançada e se margem ficará negativa caso este opte por esta operação.
            // o que significa que não verificará se contrato já foi enviado para a folha para a desliquidação
            final boolean desliquidaAvancada = responsavel.temPermissao(CodedValues.FUN_DESLIQUIDACAO_AVANCADA_CONTRATO);
            final boolean validaMargem = !permiteCancelarRenegMantendoMargemNegativa && ((!desliquidaAvancada || opcaoValidaMargem) == true);
            boolean jahEnviadaFolha = false;
            Date dataLiquidacao = null;

            // Remove ocorrencia de tarifação de liquidação
            final List<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO);
            for (final OcorrenciaAutorizacao ocaBean : ocorrencias) {
                dataLiquidacao = ocaBean.getOcaData();

                // Se a liquidação já foi enviada para a folha então gera uma exceção
                if ((dataFimExportacao != null) && (dataFimExportacao.compareTo(dataLiquidacao) >= 0)) {
                    if (!desliquidaAvancada) {
                        throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.desliquidada.porque.informacao.liquidacao.ja.enviada.para.folha", responsavel);
                    } else {
                        jahEnviadaFolha = true;
                    }
                }

                // Remove a ocorrencia
                AbstractEntityHome.remove(ocaBean);
            }

            // Verifica a quantidade de parcelas pagas do contrato
            final ObtemTotalParcelasPagasQuery query = new ObtemTotalParcelasPagasQuery();
            query.adeCodigo = adeCodigo;
            final int adePrdPagas = query.executarContador();

            // Modifica status da consignação, seta o número de parcelas pagas e insere a ocorrencia de alteração de status
            final String sadNovo = (adePrdPagas > 0) ? CodedValues.SAD_EMANDAMENTO : CodedValues.SAD_DEFERIDA;
            adeBean.setAdePrdPagas(adePrdPagas);
            final String ocaCodigo = modificaSituacaoADE(adeBean, sadNovo, responsavel);

            //A desliquilidação de contrato de benefício é feita na reativação do contrato e quando o usuário tem permissão avançada de delisquidação
            //o processo não precisa de tipo motivo operação.
            final boolean isContratoBeneficio = (adeBean.getContratoBeneficio() != null) && (adeBean.getTipoLancamento() != null);

            if (desliquidaAvancada) {
                final boolean exportacaoInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel);

                // se for sistema de exportação inicial faz o reimplante do contrato desliquidado.
                if (exportacaoInicial && reimplantar) {
                    reimplantarConsignacaoController.reimplantar(adeCodigo, null, tipoMotivoOperacao, responsavel);
                }
            }

            if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                // grava motivo da operacao
                tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
            } else if (!permiteCancelarRenegMantendoMargemNegativa && (tipoMotivoOperacao == null) && desliquidaAvancada && (reimplantar || !opcaoValidaMargem || jahEnviadaFolha) && !isContratoBeneficio) {
                throw new AutorizacaoControllerException("mensagem.motivo.operacao.obrigatorio", responsavel);
            }

            // Verifica se deve liberar margem
            boolean atualizaMargem = true;
            if (adeBean.getAdePrazo() == null) {
                try {
                    ParamSvcCseTO paramSvcCse = new ParamSvcCseTO();
                    paramSvcCse.setTpsCodigo(CodedValues.TPS_PRENDE_MARGEM_LIQ_ADE_PRZ_INDET_ATE_CARGA_MARGEM);
                    paramSvcCse.setSvcCodigo(svcCodigo);
                    paramSvcCse.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
                    paramSvcCse = parametroController.findParamSvcCse(paramSvcCse, responsavel);
                    final boolean prendeMargemAteCarga = ((paramSvcCse.getPseVlr() != null) && "1".equals(paramSvcCse.getPseVlr()));
                    // Se a margem de liquidação só é liberada na próxima carga de margem, então ao desliquidar, verifica se
                    // a margem deve ou não ser presa novamente. Verifica pela data da liquidação e o RSE_DATA_CARGA
                    if (prendeMargemAteCarga) {
                        final Date rseDataCarga = rseBean.getRseDataCarga();
                        if ((rseDataCarga != null) && (dataLiquidacao != null) && (rseDataCarga.compareTo(dataLiquidacao) < 0)) {
                            // Se a data carga é menor que a data de liquidação, significa que a margem ainda não foi
                            // liberada, então ao desliquidar, a margem também não será presa novamente
                            atualizaMargem = false;
                        }
                    }
                } catch (final ParametroControllerException e) {
                    LOG.debug("Parâmetro " + CodedValues.TPS_PRENDE_MARGEM_LIQ_ADE_PRZ_INDET_ATE_CARGA_MARGEM + " não informado para o serviço.");
                }
            }

            // Altera a margem do servidor
            try {
                if (atualizaMargem) {
                    final Short adeIncMargem = (adeBean.getAdeIncMargem() != null) ? adeBean.getAdeIncMargem() : CodedValues.INCIDE_MARGEM_SIM;
                    BigDecimal adeVlr = adeBean.getAdeVlr();
                    if (CodedValues.TIPO_VLR_PERCENTUAL.equals(adeBean.getAdeTipoVlr()) && (adeBean.getAdeVlrFolha() != null)) {
                        adeVlr = adeBean.getAdeVlrFolha();
                    }
                    atualizaMargem(rseBean.getRseCodigo(), adeIncMargem, adeVlr, validaMargem, true, true, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                }
            } catch (final AutorizacaoControllerException ex) {
                if ((ex != null) && (ex.getMessageKey() != null) && ("mensagem.servidorBloqueado".equals(ex.getMessageKey()) || "mensagem.servidorExcluido".equals(ex.getMessageKey()))) {
                    throw ex;
                }
                throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.desliquidada.porque.margem.liberada.liquidacao.ja.foi.utilizada", responsavel, ex);
            }

            final boolean habilitaModuloBeneficio = ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_BENEFICIOS_SAUDE, CodedValues.TPC_SIM, responsavel);
            // Desliquidação de contratos de benefício
            if (habilitaModuloBeneficio && isContratoBeneficio) {
                // ADE de mensalidade de benefício
                if(CodedValues.TNT_BENEFICIO_MENSALIDADE.contains(adeBean.getTipoLancamento().getTipoNatureza().getTntCodigo())) {
                    // Altera o status do beneficiário para ativo ao desliquidar uma ADE de mensalidade de benefício
                    final Beneficiario beneficiario = BeneficiarioHome.findDetachedByAdeCodigo(adeBean.getAdeCodigo(), responsavel);
                    // Se beneficiario estiver inativo, ativá-lo
                    if(beneficiario.getStatusBeneficiario().getSbeCodigo().equals(StatusBeneficiarioEnum.INATIVO.sbeCodigo)) {
                        beneficiario.setStatusBeneficiario(new StatusBeneficiario(StatusBeneficiarioEnum.ATIVO.sbeCodigo));
                        beneficiarioController.update(beneficiario, responsavel);
                    }

                    // Desliquida a ADE de subsídio referente à ADE de mensalidade de benefício
                    List<RelacionamentoAutorizacao> relacionamentoAutorizacao = new ArrayList<>();
                    final List<String> sadInativos = new ArrayList<>();
                    sadInativos.add(CodedValues.SAD_LIQUIDADA);
                    sadInativos.add(CodedValues.SAD_CONCLUIDO);
                    sadInativos.add(CodedValues.SAD_CANCELADA);
                    relacionamentoAutorizacao = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_BENEFICIO_SUBSIDIO, sadInativos);
                    // Recupera a ADE de subsídio para desliquidar
                    if (!relacionamentoAutorizacao.isEmpty()) {
                        for (final RelacionamentoAutorizacao relacionamento : relacionamentoAutorizacao) {
                            final AutDesconto adeBeanDestino = relacionamento.getAutDescontoByAdeCodigoDestino();
                            // Certifica que as ADEs são do mesmo contrato de benefício
                            if ((adeBeanDestino.getContratoBeneficio() != null) && adeBeanDestino.getContratoBeneficio().equals(adeBean.getContratoBeneficio())) {
                                desliquidarContrato(adeBeanDestino.getAdeCodigo(), opcaoValidaMargem, reimplantar, ignoraCompra, tipoMotivoOperacao, responsavel);
                            }
                        }
                    }

                    // Altera o status do contrato de benefício para ativo
                    String tmoCodigo = null;
                    if ((ocaCodigo != null) && (tipoMotivoOperacao != null)) {
                        tmoCodigo = tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO) != null ? tipoMotivoOperacao.getAttribute(Columns.TMO_CODIGO).toString() : null;
                    }
                    // Recupera o contrato de benefício
                    final ContratoBeneficio contratoBeneficio = contratoBeneficioController.findByPrimaryKey(adeBean.getContratoBeneficio().getCbeCodigo(), responsavel);
                    contratoBeneficio.setStatusContratoBeneficio(new StatusContratoBeneficio(StatusContratoBeneficioEnum.ATIVO.getCodigo()));
                    final String ocbObs = ApplicationResourcesHelper.getMessage("mensagem.beneficio.ocorrencia.contrato.beneficio.reativacao", responsavel);
                    contratoBeneficioController.update(contratoBeneficio, CodedValues.TOC_ALTERACAO_CONTRATO_BENEFICIO, ocbObs, tmoCodigo, responsavel);
                }
            }

            final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.DESLIQUIDAR_CONSIGNACAO, Log.LOG_INFORMACAO);
            logDelegate.setAutorizacaoDesconto(adeCodigo);

            if (desliquidaAvancada) {
                final StringBuilder logBuild = new StringBuilder(" " + ApplicationResourcesHelper.getMessage("mensagem.informacao.deliquidacao.avancada", responsavel).toUpperCase() + " ");
                logBuild.append("<br>").append(ApplicationResourcesHelper.getMessage("rotulo.avancada.validaMargem", responsavel)).append(" " + ApplicationResourcesHelper.getMessage((opcaoValidaMargem) ? "rotulo.sim" : "rotulo.nao", responsavel));
                logBuild.append("<br>").append(ApplicationResourcesHelper.getMessage("rotulo.avancada.liquidacao.enviada.folha", responsavel)).append(" " + ApplicationResourcesHelper.getMessage((jahEnviadaFolha) ? "rotulo.sim" : "rotulo.nao", responsavel));
                logBuild.append("<br>").append(ApplicationResourcesHelper.getMessage("rotulo.avancada.desliquidacao.reimplanta.ade", responsavel)).append(" " + ApplicationResourcesHelper.getMessage((reimplantar) ? "rotulo.sim" : "rotulo.nao", responsavel));

                logDelegate.add(logBuild.toString());
            }

            logDelegate.write();

        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else if (ex.getClass().equals(TipoMotivoOperacaoControllerException.class)) {
                throw (TipoMotivoOperacaoControllerException) ex;
            } else {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Método para colocar o contrato como aguardando liquidação depois que é feita a solicitação da liquidação pelo papel CSE/SUP/ORG.
     * @param adeCodigos Lista dos códigos dos contratos.
     * @param ocsObs Observação a ser inserida na ocorrência de informação.
     * @param tmoCodigo Código do tipo de motivo da operação.
     * @param responsavel Responsável pela operação.
     * @throws AutorizacaoControllerException Exceção padrão da classe.
     */
    @Override
    public void solicitarLiquidacao(List<String> adeCodigos, String ocsObs, String tmoCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            for (final String adeCodigo : adeCodigos) {
                final AutDesconto adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(adeBean.getVerbaConvenio().getConvenio().getOrgao().getOrgCodigo(), responsavel);

                final String ocaCodigo = modificaSituacaoADE(adeBean, CodedValues.SAD_AGUARD_LIQUIDACAO, responsavel, true, periodoAtual, false);
                final CustomTransferObject tipoMotivoOperacao = new CustomTransferObject();

                if ((ocaCodigo != null) && !TextHelper.isNull(tmoCodigo)) {
                    // grava motivo da operacao
                    tipoMotivoOperacao.setAttribute(Columns.TMO_CODIGO, tmoCodigo);
                    tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                    tipoMotivoOperacao.setAttribute(Columns.OCA_OBS, ocsObs);
                    tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
                }

                // Cria ocorrência
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_SOLICITAR_LIQUIDACAO_CONSIGNACAO, ocsObs, tmoCodigo, responsavel);

                final Object paramQtdDiasBloqCsaNaoConfirmao = ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_BLOQ_SERVIDOR_COM_LEILAO_CANCELADO, AcessoSistema.getAcessoUsuarioSistema());
                final int diasBloqueioNaoConfirmacao = (!TextHelper.isNull(paramQtdDiasBloqCsaNaoConfirmao) && TextHelper.isNum(paramQtdDiasBloqCsaNaoConfirmao)) ? Integer.parseInt(paramQtdDiasBloqCsaNaoConfirmao.toString()) : 0;

                Date soaDataValidade = null;
                if (diasBloqueioNaoConfirmacao > 0) {
                    soaDataValidade = DateHelper.addDays(DateHelper.getSystemDatetime(), diasBloqueioNaoConfirmacao);
                }

                // Cria solicitação de autorização
                final String tisCodigo = TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO.getCodigo();
                final String ssoCodigo = StatusSolicitacaoEnum.PENDENTE.getCodigo();
                SolicitacaoAutorizacaoHome.create(adeCodigo, responsavel.getUsuCodigo(), tisCodigo, ssoCodigo, soaDataValidade);

                // Gera o Log de auditoria
                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.SOLICITACAO_LIQUIDACAO, Log.LOG_INFORMACAO);
                logDelegate.setAutorizacaoDesconto(adeCodigo);
                logDelegate.write();

                // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
                // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
                final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.SOLICITAR_LIQUIDACAO, adeCodigo, null, tipoMotivoOperacao, responsavel);
                processoEmail.start();
            }
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            LOG.error(ex.getMessage(), ex);
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    /**
     * Método para cancelar solicitação da liquidação pelo papel CSE/SUP/ORG.
     * @param adeCodigos Lista dos códigos dos contratos.
     * @param ocsObs Observação a ser inserida na ocorrência de informação.
     * @param tmoCodigo Código do tipo de motivo da operação.
     * @param responsavel Responsável pela operação.
     * @throws AutorizacaoControllerException Exceção padrão da classe.
     */
    @Override
    public void cancelarSolicitacaoLiquidacao(List<String> adeCodigos, String ocsObs, String tmoCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            for (final String adeCodigo : adeCodigos) {
                final AutDesconto adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                final Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(adeBean.getVerbaConvenio().getConvenio().getOrgao().getOrgCodigo(), responsavel);

                final String statusNovo = adeBean.getAdePrdPagas() > 0 ? CodedValues.SAD_EMANDAMENTO : CodedValues.SAD_DEFERIDA;
                final String ocaCodigo = modificaSituacaoADE(adeBean, statusNovo, responsavel, true, periodoAtual, false);
                final CustomTransferObject tipoMotivoOperacao = new CustomTransferObject();

                if ((ocaCodigo != null) && !TextHelper.isNull(tmoCodigo)) {
                    // grava motivo da operacao
                    tipoMotivoOperacao.setAttribute(Columns.TMO_CODIGO, tmoCodigo);
                    tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                    tipoMotivoOperacao.setAttribute(Columns.OCA_OBS, ocsObs);
                    tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
                }

                // Cria ocorrência
                criaOcorrenciaADE(adeCodigo, CodedValues.TOC_CANC_SOLICITAR_LIQUIDACAO_CONSIGNACAO, ocsObs, tmoCodigo, responsavel);

                final String[] tisCodigos = {TipoSolicitacaoEnum.SOLICITACAO_LIQUIDACAO_CONTRATO.getCodigo()};
                final List<SolicitacaoAutorizacao> solicitacoes = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, StatusSolicitacaoEnum.PENDENTE.getCodigo());

                if ((solicitacoes != null) && !solicitacoes.isEmpty()) {
                    for (final SolicitacaoAutorizacao solicitacaoAutorizacao : solicitacoes) {
                        solicitacaoAutorizacao.setStatusSolicitacao(new StatusSolicitacao(StatusSolicitacaoEnum.CANCELADA.getCodigo()));
                        AbstractEntityHome.update(solicitacaoAutorizacao);
                    }
                }

                // Gera o Log de auditoria
                final LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.CANC_SOLICITACAO_LIQUIDACAO, Log.LOG_INFORMACAO);
                logDelegate.setAutorizacaoDesconto(adeCodigo);
                logDelegate.write();

                // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
                // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
                final ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.SOLICITAR_LIQUIDACAO, adeCodigo, null, tipoMotivoOperacao, responsavel);
                processoEmail.start();
            }
        } catch (final Exception ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            LOG.error(ex.getMessage(), ex);
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }


    @Override
    public void desliquidarPosCorte(String adeCodigo, boolean opcaoValidaMargem, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            AutDesconto adeBean = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
            String sadCodigo = adeBean.getStatusAutorizacaoDesconto().getSadCodigo().trim();

            if (!sadCodigo.equals(CodedValues.SAD_LIQUIDADA)) {
                throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.desliquidada.porque.situacao.atual.dela.nao.permite.esta.operacao", responsavel, adeBean.getStatusAutorizacaoDesconto().getSadDescricao());
            }

            final RegistroServidor rseBean = RegistroServidorHome.findByPrimaryKey(adeBean.getRegistroServidor().getRseCodigo());
            final VerbaConvenio vcoBean = VerbaConvenioHome.findByPrimaryKey(adeBean.getVerbaConvenio().getVcoCodigo());
            final Convenio cnvBean = ConvenioHome.findByPrimaryKey(vcoBean.getConvenio().getCnvCodigo());
            final String csaCodigo = cnvBean.getConsignataria().getCsaCodigo();
            final String svcCodigo = cnvBean.getServico().getSvcCodigo();

            Date dataLiquidacao = null;
            List<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, CodedValues.TOC_TARIF_LIQUIDACAO);
            for (OcorrenciaAutorizacao ocaBean : ocorrencias) {
                dataLiquidacao = ocaBean.getOcaData();
            }

            // Verifica a quantidade de parcelas pagas do contrato
            ObtemTotalParcelasPagasQuery query = new ObtemTotalParcelasPagasQuery();
            query.adeCodigo = adeCodigo;
            int adePrdPagas = query.executarContador();

            // Modifica status da consignação, seta o número de parcelas pagas e insere a ocorrencia de alteração de status
            String sadNovo = (adePrdPagas > 0) ? CodedValues.SAD_EMANDAMENTO : CodedValues.SAD_DEFERIDA;
            adeBean.setAdePrdPagas(adePrdPagas);
            String ocaCodigo = modificaSituacaoADE(adeBean, sadNovo, responsavel);

            // se for sistema de exportação inicial faz o reimplante do contrato desliquidado.
            boolean exportacaoInicial = ParamSist.paramEquals(CodedValues.TPC_EXPORTACAO_APENAS_INICIAL, CodedValues.TPC_SIM, responsavel);

            // se for sistema de exportação inicial faz o reimplante do contrato desliquidado.
            if (exportacaoInicial) {
                reimplantarConsignacaoController.reimplantar(adeCodigo, "", tipoMotivoOperacao, responsavel);
            }

            if (ocaCodigo != null && tipoMotivoOperacao != null) {
                // grava motivo da operacao
                tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
            } else if (tipoMotivoOperacao == null) {
                throw new AutorizacaoControllerException("mensagem.motivo.operacao.obrigatorio", responsavel);
            }

            // Verifica se deve liberar margem
            boolean atualizaMargem = true;
            if (adeBean.getAdePrazo() == null) {
                try {
                    ParamSvcCseTO paramSvcCse = new ParamSvcCseTO();
                    paramSvcCse.setTpsCodigo(CodedValues.TPS_PRENDE_MARGEM_LIQ_ADE_PRZ_INDET_ATE_CARGA_MARGEM);
                    paramSvcCse.setSvcCodigo(svcCodigo);
                    paramSvcCse.setCseCodigo(CodedValues.CSE_CODIGO_SISTEMA);
                    paramSvcCse = parametroController.findParamSvcCse(paramSvcCse, responsavel);
                    boolean prendeMargemAteCarga = (paramSvcCse.getPseVlr() != null && paramSvcCse.getPseVlr().equals("1"));
                    // Se a margem de liquidação só é liberada na próxima carga de margem, então ao desliquidar, verifica se
                    // a margem deve ou não ser presa novamente. Verifica pela data da liquidação e o RSE_DATA_CARGA
                    if (prendeMargemAteCarga) {
                        Date rseDataCarga = rseBean.getRseDataCarga();
                        if (rseDataCarga != null && dataLiquidacao != null && rseDataCarga.compareTo(dataLiquidacao) < 0) {
                            // Se a data carga é menor que a data de liquidação, significa que a margem ainda não foi
                            // liberada, então ao desliquidar, a margem também não será presa novamente
                            atualizaMargem = false;
                        }
                    }
                } catch (ParametroControllerException e) {
                    LOG.debug("Parâmetro " + CodedValues.TPS_PRENDE_MARGEM_LIQ_ADE_PRZ_INDET_ATE_CARGA_MARGEM + " não informado para o serviço.");
                }
            }

            // Altera a margem do servidor
            try {
                if (atualizaMargem) {
                    Short adeIncMargem = (adeBean.getAdeIncMargem() != null) ? adeBean.getAdeIncMargem() : CodedValues.INCIDE_MARGEM_SIM;
                    BigDecimal adeVlr = adeBean.getAdeVlr();
                    if (adeBean.getAdeTipoVlr().equals(CodedValues.TIPO_VLR_PERCENTUAL) && adeBean.getAdeVlrFolha() != null) {
                        adeVlr = adeBean.getAdeVlrFolha();
                    }
                    atualizaMargem(rseBean.getRseCodigo(), adeIncMargem, adeVlr, opcaoValidaMargem, true, true, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                }
            } catch (AutorizacaoControllerException ex) {
                if (ex.getMessageKey() != null && (ex.getMessageKey().equals("mensagem.servidorBloqueado") || ex.getMessageKey().equals("mensagem.servidorExcluido"))) {
                    throw ex;
                }
                throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.desliquidada.porque.margem.liberada.liquidacao.ja.foi.utilizada", responsavel, ex);
            }

            LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.DESLIQUIDAR_CONSIGNACAO, Log.LOG_INFORMACAO);
            logDelegate.setAutorizacaoDesconto(adeCodigo);

            String logBuild = " " + ApplicationResourcesHelper.getMessage("mensagem.informacao.deliquidacao.avancada", responsavel).toUpperCase() + " " + "<br>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.validaMargem", responsavel) + " " + ApplicationResourcesHelper.getMessage((opcaoValidaMargem) ? "rotulo.sim" : "rotulo.nao", responsavel) +
                    "<br>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.liquidacao.enviada.folha", responsavel) + " " + ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel) +
                    "<br>" + ApplicationResourcesHelper.getMessage("rotulo.avancada.desliquidacao.reimplanta.ade", responsavel) + " " + ApplicationResourcesHelper.getMessage("rotulo.sim", responsavel);

            logDelegate.add(logBuild);

            logDelegate.write();
        } catch (AutorizacaoControllerException | FindException | TipoMotivoOperacaoControllerException |
                 HQueryException | LogControllerException ex) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex);
        }
    }
}
