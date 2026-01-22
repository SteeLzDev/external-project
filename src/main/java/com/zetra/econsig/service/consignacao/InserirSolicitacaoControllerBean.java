package com.zetra.econsig.service.consignacao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.RegistroServidor;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacaoHome;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: InserirSolicitacaoControllerBean </p>
 * <p>Description: Session Bean para a operação Inserir Solicitação.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class InserirSolicitacaoControllerBean extends ReservarMargemControllerBean implements InserirSolicitacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(InserirSolicitacaoControllerBean.class);

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private ServidorController servidorController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Override
    public String solicitarReservaMargem(ReservarMargemParametros reservaMargem, ServidorTransferObject servidor, RegistroServidorTO registroServidor, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            final String rseCodigo = registroServidor.getRseCodigo();
            if (TextHelper.isNull(registroServidor.getOrgCodigo())) {
                final RegistroServidor rse = RegistroServidorHome.findByPrimaryKey(rseCodigo);
                registroServidor.setOrgCodigo(rse.getOrgao().getOrgCodigo());
            }

            final String orgCodigo = registroServidor.getOrgCodigo();
            final BigDecimal adeVlr = reservaMargem.getAdeVlr();
            final BigDecimal vlrLiberado = reservaMargem.getAdeVlrLiquido();
            final Integer adePrazo = reservaMargem.getAdePrazo();

            final String nomeAnexo = reservaMargem.getNomeAnexo();
            final Boolean validaAnexo = reservaMargem.getValidaAnexo();

            List<TransferObject> simulacao = null;

            final boolean temLeilaoSimulacao = ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel);
            final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
            final boolean simulacaoMetodoBrasileiro = ParamSist.paramEquals(CodedValues.TPC_METODO_CALCULO_SIMULACAO, CodedValues.MCS_BRASILEIRO, responsavel);

            /**
             * Se é leilão, ignora os valores passados por parâmetro, recalcula o ranking e repassa os valores do primeiro colocado
             * na simulação para inclusão da solicitação.
             */
            if (temLeilaoSimulacao && reservaMargem.isIniciarLeilaoReverso()) {
                boolean simulacaoPorAdeVlr = reservaMargem.isSimulacaoPorAdeVlr();
                simulacao = simulacaoController.simularConsignacao(svcCodigo, orgCodigo, rseCodigo, simulacaoPorAdeVlr ? adeVlr : null, !simulacaoPorAdeVlr ? vlrLiberado : null, Short.parseShort(adePrazo != null ? adePrazo.toString() : "0"), null, true, reservaMargem.getAdePeriodicidade(), responsavel);

                // Parâmetros de serviços necessários
                ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);

                // Verifica se pode mostrar margem
                final int qtdeConsignatariasSimulacao = paramSvc.getTpsQtdCsaPermitidasSimulador();
                final Short incMargem = paramSvc.getTpsIncideMargem();
                final MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigo, incMargem, responsavel);
                final BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();
                simulacao = simulacaoController.selecionarLinhasSimulacao(simulacao, rseCodigo, rseMargemRest, qtdeConsignatariasSimulacao, false, true, responsavel);

                if (simulacao == null || simulacao.isEmpty()) {
                    throw new AutorizacaoControllerException("mensagem.erro.interno.contate.administrador", responsavel);
                }

                Boolean vlrOk = false;
                String csa_codigo = null, cft_codigo = null, ranking = null;
                BigDecimal tac = null, iof = null, vlr_ade = null, vlrLiberado_param = null;

                for (TransferObject coeficiente : simulacao) {
                    vlrOk = Boolean.parseBoolean(coeficiente.getAttribute("OK").toString());

                    if (!vlrOk) {
                        continue;
                    }

                    csa_codigo = (String) coeficiente.getAttribute(Columns.CSA_CODIGO);
                    svcCodigo = (String) coeficiente.getAttribute(Columns.SVC_CODIGO);
                    cft_codigo = (String) coeficiente.getAttribute(Columns.CFT_CODIGO);
                    vlr_ade = new BigDecimal(coeficiente.getAttribute("VLR_PARCELA").toString());
                    vlrLiberado_param = new BigDecimal(coeficiente.getAttribute("VLR_LIBERADO").toString());
                    ranking = (String) coeficiente.getAttribute("RANKING");

                    if (simulacaoPorTaxaJuros && simulacaoMetodoBrasileiro) {
                        tac = new BigDecimal((coeficiente.getAttribute("TAC_FINANCIADA") != null) ? coeficiente.getAttribute("TAC_FINANCIADA").toString() : "0.00");
                        iof = new BigDecimal((coeficiente.getAttribute("IOF") != null) ? coeficiente.getAttribute("IOF").toString() : "0.00");
                    }

                    break;
                }

                if (!vlrOk) {
                    throw new AutorizacaoControllerException("mensagem.erro.ranking.csa.nao.disponivel", responsavel);
                }

                // Recupera o convênio
                Convenio convenio = ConvenioHome.findByChave(svcCodigo, csa_codigo, orgCodigo);
                reservaMargem.setCnvCodigo(convenio.getCnvCodigo());

                reservaMargem.setAdeVlr(vlr_ade);
                if (simulacaoMetodoBrasileiro) {
                    reservaMargem.setAdeVlrTac(tac);
                    reservaMargem.setAdeVlrIof(iof);
                }
                reservaMargem.setAdeVlrLiquido(vlrLiberado_param);
                reservaMargem.setCftCodigo(cft_codigo);
                reservaMargem.setCdeVlrLiberado(vlrLiberado_param);
                reservaMargem.setCdeRanking(Short.valueOf(ranking));
            }

            // Recupera o convênio
            final String cnvCodigo = reservaMargem.getCnvCodigo();
            final Convenio cnvBean = ConvenioHome.findByPrimaryKey(cnvCodigo);
            final String csaCodigo = cnvBean.getConsignataria().getCsaCodigo();

            // Parâmetros de serviços necessários
            final ParamSvcTO paramSvc = parametroController.getParamSvcCseTO(svcCodigo, responsavel);
            final Short adeIncMargem = paramSvc.getTpsIncideMargem();
            final Short adeIntFolha = paramSvc.getTpsIntegraFolha();
            final String adeTipoVlr = paramSvc.getTpsTipoVlr();
            final boolean deferimentoAutoSolicSer = paramSvc.isTpsDeferimentoAutoSolicitacaoServidor();
            final String adePeriodicidade = reservaMargem.getAdePeriodicidade();

            simulacao = simulacaoController.simularConsignacao(svcCodigo, orgCodigo, rseCodigo, adeVlr, vlrLiberado, Short.parseShort(adePrazo != null ? adePrazo.toString() : "0"), null, true, adePeriodicidade, responsavel);

            // Verifica se pode mostrar margem
            final int qtdeConsignatariasSimulacao = paramSvc.getTpsQtdCsaPermitidasSimulador();
            final Short incMargem = paramSvc.getTpsIncideMargem();
            final MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, svcCodigo, incMargem, responsavel);
            final BigDecimal rseMargemRest = margemDisponivel.getMargemRestante();

            simulacao = simulacaoController.selecionarLinhasSimulacao(simulacao, rseCodigo, rseMargemRest, qtdeConsignatariasSimulacao, false, true, responsavel);

            // Verifica se a consignatária do convênio passado pode inserir uma solicitação
            boolean achouCsaSimulacao = false;
            for (TransferObject coeficiente : simulacao) {
                if ((Boolean) coeficiente.getAttribute("OK") && coeficiente.getAttribute(Columns.CSA_CODIGO).equals(csaCodigo)) {
                    achouCsaSimulacao = true;
                    break;
                }
            }

            // Caso a consignatária informada não possa realizar uma solicitação
            if (!achouCsaSimulacao) {
                throw new AutorizacaoControllerException("mensagem.erro.nao.permitido.inserir.solicitacao.para.consignataria.informada", responsavel);
            }

            String sadCodigo = CodedValues.SAD_SOLICITADO;
            // Se o responsável é servidor e contrato é deferido automaticamente pelo servidor
            if (responsavel.isSer() && deferimentoAutoSolicSer) {
                // Caso o deferimento automático para servidor esteja habilitado para serviço da natureza empréstimo, levanta uma exceção
                CustomTransferObject naturezaSvc = servicoController.findNaturezaServico(svcCodigo, responsavel);
                if (naturezaSvc != null && !TextHelper.isNull(naturezaSvc.getAttribute(Columns.NSE_CODIGO)) && naturezaSvc.getAttribute(Columns.NSE_CODIGO).toString().equals(CodedValues.NSE_EMPRESTIMO) && deferimentoAutoSolicSer) {
                    throw new AutorizacaoControllerException("mensagem.erro.nao.permitido.deferimento.automatico.para.servico.natureza.emprestimo", responsavel);
                }

                sadCodigo = CodedValues.SAD_DEFERIDA;
            }

            // verifica campo cidade para assinatura do contrato
            String paramExibeCampoCidade = paramSvc.getTpsExibeCidadeConfirmacaoSolicitacao();
            if (!TextHelper.isNull(paramExibeCampoCidade) && (paramExibeCampoCidade.equals(CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO) || (paramExibeCampoCidade.equals(CodedValues.EXIBIR_CIDADE_CONFIRMACAO_SOLICITACAO_OBRIGATORIO_LEILAO) && reservaMargem.isIniciarLeilaoReverso()))) {
                if (TextHelper.isNull(reservaMargem.getCidCodigo())) {
                    throw new AutorizacaoControllerException("mensagem.informe.cidade.assinatura.contrato", responsavel);
                }
            }

            String svcCodigoParam = reservaMargem.getSvcCodigoOrigem() != null ? reservaMargem.getSvcCodigoOrigem() : svcCodigo;

            // Validação do anexo
            if (validaAnexo && ParamSist.paramEquals(CodedValues.TPC_PERMITE_ANEXO_INCLUSAO_CONTRATOS, CodedValues.TPC_SIM, responsavel) &&
                    parametroController.isObrigatorioAnexoInclusao(svcCodigoParam, responsavel)
                    && TextHelper.isNull(nomeAnexo) && !responsavel.isSer()) {
                throw new AutorizacaoControllerException("mensagem.erro.nao.pode.inserir.nova.reserva.para.este.servico.pois.anexo.obrigatorio", responsavel);
            }

            // Setar parâmetros para reserva de margem
            reservaMargem.setAdeIncMargem(adeIncMargem);
            reservaMargem.setAdeIntFolha(adeIntFolha);
            reservaMargem.setAdeTipoVlr(adeTipoVlr);
            reservaMargem.setSadCodigo(sadCodigo);

            // Operação de solicitação de portabilidade, por padrão, o novo ADE_VLR será igual ao da consignação anterior. Neste caso
            // esta consignação que abre o leilão não precisa incidir na margem, visto que ela depois será cancelada e substituída pela
            // consignação referente à melhor proposta do leilão criada para o processo de portabilidade
            if (responsavel.getFunCodigo() != null && responsavel.getFunCodigo().equals(CodedValues.FUN_SOLICITAR_PORTABILIDADE) && reservaMargem.isIniciarLeilaoReverso()) {
                final BigDecimal adeVlrAnterior = pesquisarConsignacaoController.obtemTotalValorConsignacaoPorCodigo(reservaMargem.getAdeCodigosRenegociacao(), responsavel);
                if (adeVlr.compareTo(adeVlrAnterior) <= 0) {
                    reservaMargem.setAdeIncMargem(CodedValues.INCIDE_MARGEM_NAO);
                }
            }

            if (ParamSist.paramEquals(CodedValues.TPC_EXIBE_CONF_DADOS_SER_SIMULADOR, null, responsavel) || ParamSist.paramEquals(CodedValues.TPC_EXIBE_CONF_DADOS_SER_SIMULADOR, CodedValues.TPC_SIM, responsavel)) {
                if (servidor != null) {
                    servidorController.updateServidor(servidor, responsavel);
                }

                if (registroServidor != null) {
                    servidorController.updateRegistroServidor(registroServidor, false, false, false, responsavel);
                }
            }

            final String adeCodigo = reservarMargem(reservaMargem, responsavel);

            String tipoOcorrencia = CodedValues.TOC_INFORMACAO;
            if (responsavel.isSer() && !ParamSist.getBoolParamSist(CodedValues.TPC_USA_SENHA_AUTORIZACAO_DESC_SERVIDOR, responsavel)) {
                // Se o sistema não utiliza a senha de autorização, então a ocorrência de inserção de solicitação é do tipo
                // de autorização via senha, já que o usuário logou com sua própria senha de consulta para realizar a simulação.
                tipoOcorrencia = CodedValues.TOC_AUTORIZACAO_VIA_SENHA_SERVIDOR;
            }

            criaOcorrenciaADE(adeCodigo, tipoOcorrencia, ApplicationResourcesHelper.getMessage("mensagem.informacao.insercao.solicitacao.responsavel.arg0", responsavel, responsavel.getUsuNome()), responsavel);

            // DESENV-8846 - Parametro de confirmacao de leitura de CET servidor
            final boolean tpsExigenciaConfirmacaoLeituraServidor = paramSvc.isTpsExigenciaConfirmacaoLeituraServidor();
            if (!reservaMargem.isIniciarLeilaoReverso() && tpsExigenciaConfirmacaoLeituraServidor) {
                String exigenciaConfirmacaoLeitura = reservaMargem.getExigenciaConfirmacaoLeitura();
                if ("true".equals(exigenciaConfirmacaoLeitura)) {
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_CONFIRMACAO_LEITURA_SERVIDOR, ApplicationResourcesHelper.getMessage("mensagem.informacao.simulacao.confirma.leitura", responsavel, responsavel.getUsuNome()), responsavel);
                } else {
                    throw new AutorizacaoControllerException("mensagem.informacao.simulacao.informar.confirmacao.leitura", responsavel);
                }
            }

            // DESENV-17933: Registra relacionamento de solicitação de portabilidade
            if (responsavel.getFunCodigo() != null && responsavel.getFunCodigo().equals(CodedValues.FUN_SOLICITAR_PORTABILIDADE)) {
                if (reservaMargem.getAdeCodigosRenegociacao() != null && !reservaMargem.getAdeCodigosRenegociacao().isEmpty()) {
                    for (String adeCodigoPortabilidade : reservaMargem.getAdeCodigosRenegociacao()) {
                        RelacionamentoAutorizacaoHome.create(adeCodigoPortabilidade, adeCodigo, CodedValues.TNT_SOLICITACAO_PORTABILIDADE, responsavel.getUsuCodigo());
                    }
                } else {
                    throw new AutorizacaoControllerException("mensagem.erro.solicitar.portabilidade.nenhuma.consignacao.encontrada", responsavel);
                }
            }

            // DESENV-10999: para solicitação feitas pelo próprio servidor, envia e-mail (se esta estiver definida) de alerta à consignatária para a qual foi feita a solicitação.
            if (responsavel.isSer()) {
                try {
                    List<String> tpsSvcCsa = new ArrayList<>();
                    tpsSvcCsa.add(CodedValues.TPS_EMAIL_SOLICITACAO_INCLUIDA_OU_CANCELADA_PELO_SERVIDOR);

                    List<TransferObject> lstSvcCsa = parametroController.selectParamSvcCsa(svcCodigo, csaCodigo, tpsSvcCsa, false, responsavel);
                    if (lstSvcCsa != null && !lstSvcCsa.isEmpty()) {
                        ConvenioTransferObject cnvTo = new ConvenioTransferObject();
                        cnvTo.setCsaCodigo(csaCodigo);
                        cnvTo.setOrgCodigo(orgCodigo);
                        cnvTo.setSvcCodigo(svcCodigo);
                        EnviaEmailHelper.enviarEmailCsaSolicitacaoFeitaPorSer((String) lstSvcCsa.get(0).getAttribute(Columns.PSC_VLR), rseCodigo, adeCodigo, rseMargemRest, incMargem, cnvTo, responsavel);
                    }
                } catch (Exception ex) {
                    LOG.error(ex.getMessage(), ex);
                }
            }

            return adeCodigo;

        } catch (NumberFormatException | CreateException | FindException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (ZetraException ex) {
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException(ex);
        }
    }

    @Override
    public boolean temSolicitacaoCreditoEletronicoPendenteDocumentacao(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            String[] tisCodigos = { TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo() };
            String[] ssoCodigos = { StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo(), StatusSolicitacaoEnum.PENDENTE_INFORMACAO_DOCUMENTACAO.getCodigo() };

            List<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigos);
            return (soaList != null && !soaList.isEmpty());
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    @Override
	public boolean temSolicitacaoAutorizacao(String adeCodigo, TipoSolicitacaoEnum tipoSolicitacaoEnum,
			StatusSolicitacaoEnum statusSolicitacaoEnum, AcessoSistema responsavel)
			throws AutorizacaoControllerException {
    	try {
            String[] tisCodigos = { tipoSolicitacaoEnum.getCodigo() };
            String[] ssoCodigos = { statusSolicitacaoEnum.getCodigo() };

            List<SolicitacaoAutorizacao> soaList = SolicitacaoAutorizacaoHome.findByAdeTipoStatus(adeCodigo, tisCodigos, ssoCodigos);
            return (soaList != null && !soaList.isEmpty());
        } catch (FindException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
	}

	@Override
    public void incluirSolicitacaoCreditoEletronico(String adeCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            String tisCodigo = TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo();
            String ssoCodigo = StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo();
            SolicitacaoAutorizacaoHome.create(adeCodigo, responsavel.getUsuCodigo(), tisCodigo, ssoCodigo, null);
        } catch (CreateException ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
