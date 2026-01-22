package com.zetra.econsig.service.consignacao;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.assembler.RegistroServidorDtoAssembler;
import com.zetra.econsig.dto.entidade.RegistroServidorTO;
import com.zetra.econsig.dto.parametros.AlongarConsignacaoParametros;
import com.zetra.econsig.dto.parametros.ReservarMargemParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ParametrosException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessaEnvioEmailEntidadesAltAde;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.HistoricoMargemRse;
import com.zetra.econsig.persistence.entity.ParcelaDesconto;
import com.zetra.econsig.persistence.entity.ParcelaDescontoHome;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodo;
import com.zetra.econsig.persistence.entity.ParcelaDescontoPeriodoHome;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentosQuery;
import com.zetra.econsig.persistence.query.periodo.ObtemUltimoPeriodoRetornoQuery;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servidor.ConsultarMargemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoEConsigEnum;
import com.zetra.econsig.values.TpsExigeConfirmacaoRenegociacaoValoresEnum;

/**
 * <p>Title: AlongarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Alongamento de Contrato.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class AlongarConsignacaoControllerBean extends RenegociarConsignacaoControllerBean implements AlongarConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AlongarConsignacaoControllerBean.class);

    @Autowired
    private LiquidarConsignacaoController liqController;

    @Autowired
    private ConsultarMargemController consultarMargemController;

    @Autowired
    private ParametroController parametroController;

    @Override
    public String alongar(AlongarConsignacaoParametros alongarParam, AcessoSistema responsavel) throws AutorizacaoControllerException {
        try {
            boolean temAlongamento = ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel);
            if (!temAlongamento) {
                throw new AutorizacaoControllerException("mensagem.sistemaNaoPermiteAlongamento", responsavel);
            }

            try {
                alongarParam.checkNotNullSafe();
            } catch(ParametrosException pe) {
                throw new AutorizacaoControllerException(pe);
            }

            String adeCodigo = alongarParam.getAdeCodigo();
            String rseCodigo = alongarParam.getRseCodigo();
            BigDecimal adeVlr = alongarParam.getAdeVlr();
            String corCodigo = alongarParam.getCorCodigo();
            Integer adePrazo = alongarParam.getAdePrazo();
            Integer adeCarencia = alongarParam.getAdeCarencia();
            String adeIdentificador = alongarParam.getAdeIdentificador();
            String cnvCodigo = alongarParam.getCnvCodigo();
            boolean comSerSenha = alongarParam.getComSerSenha().booleanValue();
            String adeIndice = alongarParam.getAdeIndice();
            BigDecimal adeVlrTac = alongarParam.getAdeVlrTac();
            BigDecimal adeVlrIof = alongarParam.getAdeVlrIof();
            BigDecimal adeVlrLiquido = alongarParam.getAdeVlrLiquido();
            BigDecimal adeVlrMensVinc = alongarParam.getAdeVlrMensVinc();
            BigDecimal adeTaxaJuros = alongarParam.getAdeTaxaJuros();
            String adePeriodicidade = alongarParam.getAdePeriodicidade();

            String nomeAnexo = alongarParam.getNomeAnexo();
            String idAnexo = alongarParam.getIdAnexo();
            String aadDescricao = alongarParam.getAadDescricao();

            // Find do contrato que será alongado
            AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);

            // Encontra o serviço do contrato que será liquidado
            VerbaConvenio verbaConvenio = VerbaConvenioHome.findByPrimaryKey(autdes.getVerbaConvenio().getVcoCodigo());
            Convenio convenio = ConvenioHome.findByPrimaryKey(verbaConvenio.getConvenio().getCnvCodigo());
            String svcCodigoAde = convenio.getServico().getSvcCodigo();
            String orgCodigo = convenio.getOrgao().getOrgCodigo();

            RegistroServidorTO rseTo = RegistroServidorDtoAssembler.createDto(RegistroServidorHome.findByPrimaryKey(rseCodigo), true);

            List<String> adeCodigos = new ArrayList<>();
            adeCodigos.add(adeCodigo);
            validaRenegociacao(adeCodigos, rseCodigo, adeVlr, corCodigo, responsavel, adePrazo,
                    adeCarencia, adePeriodicidade, adeIdentificador, cnvCodigo, comSerSenha, adeIndice,
                    adeVlrTac, adeVlrIof, adeVlrLiquido, adeVlrMensVinc, false, adeTaxaJuros, false, true, true, rseTo, null);

            String adeTipoVlr = autdes.getAdeTipoVlr();
            Short adeIntFolha = autdes.getAdeIntFolha();
            Short adeIncMargem = autdes.getAdeIncMargem();

            // Localiza o novo convênio, origem do relacionamento de alongamento
            convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
            String svcCodigoAlongamento = convenio.getServico().getSvcCodigo();
            String csaCodigoAlongamento = convenio.getConsignataria().getCsaCodigo();

            // Verifica o critério para inclusão do alongamento
            Object paramCriterio = ParamSist.getInstance().getParam(CodedValues.TPC_CRITERIO_ALONGAMENTO_CONTRATO, responsavel);
            if (TextHelper.isNull(paramCriterio) || paramCriterio.equals(CodedValues.CRITERIO_ALONGAMENTO_MARGEM_NEGATIVA)) {
                verificaMargemNegativa(rseCodigo, svcCodigoAlongamento, responsavel);
            } else if (paramCriterio.equals(CodedValues.CRITERIO_ALONGAMENTO_PARCELA_REJEITADA)) {
                verificaParcelaUltimoPeriodoRejeitada(adeCodigo, orgCodigo, responsavel);
            } else if (paramCriterio.equals(CodedValues.CRITERIO_ALONGAMENTO_PARCELA_REJEITADA_E_MARGEM_NEGATIVA)) {
                verificaMargemNegativa(rseCodigo, svcCodigoAlongamento, responsavel);
                verificaParcelaUltimoPeriodoRejeitada(adeCodigo, orgCodigo, responsavel);
            } else if (paramCriterio.equals(CodedValues.CRITERIO_ALONGAMENTO_PARCELA_REJEITADA_OU_MARGEM_NEGATIVA)) {
                try {
                    verificaMargemNegativa(rseCodigo, svcCodigoAlongamento, responsavel);
                } catch (ZetraException ex) {
                    try {
                        verificaParcelaUltimoPeriodoRejeitada(adeCodigo, orgCodigo, responsavel);
                    } catch (ZetraException ex1) {
                        throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.pode.ser.alongado.pois.nao.possui.parcela.rejeitada.folha.periodo.anterior.e.servidor.possui.margem.positiva", responsavel);
                    }
                }
            } else {
                throw new AutorizacaoControllerException("mensagem.erro.criterio.para.alongamento.contrato.invalido", responsavel);
            }

            // Verifica se existe um relacionamento de alongamento entre os dois serviços
            ListaRelacionamentosQuery queryRel = new ListaRelacionamentosQuery();
            queryRel.tntCodigo = CodedValues.TNT_ALONGAMENTO;
            queryRel.svcCodigoOrigem = svcCodigoAlongamento;
            queryRel.svcCodigoDestino = svcCodigoAde;
            List<TransferObject> relacionamento = queryRel.executarDTO();
            if (relacionamento == null || relacionamento.isEmpty()) {
                throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.pode.ser.alongado.pois.nao.esta.relacionado.alongamento", responsavel);
            }

            // Verifica valor máximo percentual da parcela
            CustomTransferObject paramSvcCse = getParametroSvc(CodedValues.TPS_VLR_PERC_MAXIMO_PARCELA_ALONGAMENTO, svcCodigoAlongamento, BigDecimal.ZERO, false, null);
            BigDecimal perMaxParc = (BigDecimal) paramSvcCse.getAttribute(Columns.PSE_VLR);
            if (perMaxParc != null && autdes.getAdeVlr().multiply(perMaxParc).compareTo(adeVlr) < 0) {
                throw new AutorizacaoControllerException("mensagem.erro.valor.novo.contrato.nao.pode.ser.maior.que.arg0.do.valor.atual", responsavel, NumberHelper.format(perMaxParc.doubleValue(), NumberHelper.getLang()));
            }

            // Verifica a limitação do capital devido
            paramSvcCse = getParametroSvc(CodedValues.TPS_LIMITA_CAPITAL_DEVIDO_ALONGAMENTO, svcCodigoAlongamento, Boolean.FALSE, false, null);
            Boolean limitaCapitalDevido = (Boolean) paramSvcCse.getAttribute(Columns.PSE_VLR);
            if (limitaCapitalDevido != null && limitaCapitalDevido.booleanValue()) {
                int prazoAtual = autdes.getAdePrazo() != null ? autdes.getAdePrazo().intValue() : 1;
                int pagasAtual = autdes.getAdePrdPagas() != null ? autdes.getAdePrdPagas().intValue() : 0;
                BigDecimal capitalDevidoAtual = autdes.getAdeVlr().multiply(new BigDecimal(prazoAtual - pagasAtual));
                BigDecimal capitalDevidoNovo  = adeVlr.multiply(new BigDecimal(adePrazo));
                if (capitalDevidoNovo.compareTo(capitalDevidoAtual) > 0) {
                    throw new AutorizacaoControllerException("mensagem.erro.capital.devido.maximo.para.contrato", responsavel, NumberHelper.format(capitalDevidoAtual.doubleValue(), NumberHelper.getLang()));
                }
            }

            // Se o prazo atual for igual a quantidade já paga somado da quantidade de parcelas em processamento,
            // então a última parcela já está na folha, e possível alteração pode não ser efetivada.
            // Verifica parâmetro que bloqueia a alteração independente do prazo e em caso positivo, bloqueia a
            // alteração e envia mensagem de erro ao usuário.
            Integer adePrazoOld = autdes.getAdePrazo();
            int adePrdPagas = (autdes.getAdePrdPagas() != null ? autdes.getAdePrdPagas().intValue() : 0);

            // Busca as parcelas do periodo
            List<ParcelaDescontoPeriodo> parcelasPeriodo = ParcelaDescontoPeriodoHome.findByAutDescontoStatus(adeCodigo, CodedValues.SPD_EMPROCESSAMENTO);
            // Verifica a quantidade de parcelas em processamento
            int qtdParcelasEmProcessamento = (parcelasPeriodo != null ? parcelasPeriodo.size() : 0);

            validaAlteracaoUltimasParcelas(orgCodigo, adePrazoOld, adePrdPagas, null, qtdParcelasEmProcessamento, responsavel);

            List<HistoricoMargemRse> historicosMargem = null;
            String sadCodigo = null;

            if ((usuarioPodeConfirmarReserva(responsavel) && usuarioPodeDeferir(svcCodigoAlongamento, csaCodigoAlongamento, rseCodigo, comSerSenha, responsavel)) ||
                 podeTpsExigeConfirmarRenegociacao(responsavel, adeVlr, autdes, svcCodigoAde, csaCodigoAlongamento)) {
                // Se usuário pode confirmar e não requer deferimento,
                // então liquida a autorização e insere reserva como Deferida
                liqController.liquidar(adeCodigo, null, null, responsavel);
                sadCodigo = CodedValues.SAD_DEFERIDA;
            } else {
                // Se usuário não pode confirmar ou requer deferimento, coloca
                // autorização em Aguard Liquidação e insere reserva sem incidir na margem
                modificaSituacaoADE(autdes, CodedValues.SAD_AGUARD_LIQUIDACAO, responsavel);
                adeIncMargem = CodedValues.INCIDE_MARGEM_NAO;
                // Verifica se o valor da nova consignação é maior do que o antigo,
                // e se for, prende da margem do servidor a diferença dos valores
                BigDecimal diff = adeVlr.subtract(autdes.getAdeVlr());
                if (diff.signum() == 1) {
                    historicosMargem = atualizaMargem(rseCodigo, autdes.getAdeIncMargem(), diff, true, true, true, null, csaCodigoAlongamento, svcCodigoAlongamento, null, responsavel);
                }
            }

            // Monta o objeto de parâmetro da reserva
            ReservarMargemParametros reservaParam = new ReservarMargemParametros();

            reservaParam.setRseCodigo(rseCodigo);
            reservaParam.setAdeVlr(adeVlr);
            reservaParam.setCorCodigo(corCodigo);
            reservaParam.setAdePrazo(adePrazo);
            reservaParam.setAdeCarencia(parametroController.calcularAdeCarenciaDiaCorteCsa(adeCarencia, convenio.getConsignataria().getCsaCodigo(), orgCodigo, responsavel));
            reservaParam.setAdeIdentificador(adeIdentificador);
            reservaParam.setCnvCodigo(cnvCodigo);
            reservaParam.setSadCodigo(sadCodigo);
            reservaParam.setSerSenha(alongarParam.getSerSenha());
            reservaParam.setComSerSenha(Boolean.valueOf(comSerSenha));
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
            reservaParam.setSerAtivo(Boolean.TRUE);
            reservaParam.setCnvAtivo(Boolean.TRUE);
            reservaParam.setSerCnvAtivo(Boolean.TRUE);
            reservaParam.setSvcAtivo(Boolean.TRUE);
            reservaParam.setCsaAtivo(Boolean.TRUE);
            reservaParam.setOrgAtivo(Boolean.TRUE);
            reservaParam.setEstAtivo(Boolean.TRUE);
            reservaParam.setCseAtivo(Boolean.TRUE);
            reservaParam.setAcao("ALONGAR");
            reservaParam.setAdeTaxaJuros(adeTaxaJuros);
            reservaParam.setAdeBanco(alongarParam.getAdeBanco());
            reservaParam.setAdeAgencia(alongarParam.getAdeAgencia());
            reservaParam.setAdeConta(alongarParam.getAdeConta());
            reservaParam.setAdePeriodicidade(alongarParam.getAdePeriodicidade());

            reservaParam.setNomeAnexo(nomeAnexo);
            reservaParam.setAadDescricao(aadDescricao);
            reservaParam.setIdAnexo(idAnexo);
            // Seta os dados genéricos
            reservaParam.setDadosAutorizacaoMap(alongarParam.getDadosAutorizacaoMap());

            reservaParam.setTelaConfirmacaoDuplicidade(alongarParam.isTelaConfirmacaoDuplicidade());
            reservaParam.setChkConfirmarDuplicidade(alongarParam.isChkConfirmarDuplicidade());
            reservaParam.setMotivoOperacaoCodigoDuplicidade(alongarParam.getMotivoOperacaoCodigoDuplicidade());
            reservaParam.setMotivoOperacaoObsDuplicidade(alongarParam.getMotivoOperacaoObsDuplicidade());


            String adeCodigoNovo = reservarMargem(reservaParam, responsavel);

            RelacionamentoAutorizacaoHome.create(adeCodigo, adeCodigoNovo, CodedValues.TNT_CONTROLE_RENEGOCIACAO, responsavel.getUsuCodigo());

            // Atualiza os históricos de margem com a ocorrencia de inclusão
            // do novo contrato fruto do alongamento
            atualizaHistoricosMargem(historicosMargem, null, adeCodigoNovo, CodedValues.TOC_TARIF_RESERVA);

            // Gera o Log de auditoria
            LogDelegate log = new LogDelegate(responsavel, Log.RELACIONAMENTO_AUTORIZACAO, Log.RENEGOCIAR_CONTRATO, Log.LOG_INFORMACAO);
            log.setAutorizacaoDesconto(adeCodigo);
            log.setAutorizacaoDescontoDestino(adeCodigoNovo);
            log.setRegistroServidor(rseCodigo);
            log.setVerbaConvenio(verbaConvenio.getVcoCodigo());
            log.setStatusAutorizacao(sadCodigo);
            log.addChangedField(Columns.ADE_VLR, adeVlr);
            log.addChangedField(Columns.ADE_PRAZO, adePrazo);
            log.addChangedField(Columns.ADE_INC_MARGEM, adeIncMargem);
            log.addChangedField(Columns.ADE_INDICE, adeIndice);
            log.write();

            // Busca as informações do contrato novo
            AutDesconto autdesNovo = null;
            try {
                autdesNovo = AutDescontoHome.findByPrimaryKey(adeCodigoNovo);
            } catch (FindException ex) {
                throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.encontrado", responsavel);
            }

            String obs = ApplicationResourcesHelper.getMessage("mensagem.informacao.ade.numero.novo.contrato.resultante.alongamento.arg0", responsavel, autdesNovo.getAdeNumero().toString());

            // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
            // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
            ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.ALONGAR_CONTRATO, adeCodigo, obs, null, responsavel);
            processoEmail.start();

            return adeCodigoNovo;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
            if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                throw (AutorizacaoControllerException) ex;
            } else {
                throw new AutorizacaoControllerException("mensagem.erroInternoSistema", responsavel, ex);
            }
        }
    }

    private boolean podeTpsExigeConfirmarRenegociacao(AcessoSistema responsavel, BigDecimal adeVlr, AutDesconto autdes, String svcCodigoAde, String csaCodigoAlongamento) throws ParametroControllerException {
        //DESENV-14162 regra item 6) para definição de que casos exigirá confirmação de renegociação
        boolean podeConfirmar = false;
        List<TransferObject> tpsCsaRenegExigeConfirmacao = parametroController.selectParamSvcCsa(svcCodigoAde, csaCodigoAlongamento, Arrays.asList(CodedValues.TPS_TIPO_RENEGOCIACAO_EXIGE_CONFIRMACAO), false, responsavel);
        if (tpsCsaRenegExigeConfirmacao != null && !tpsCsaRenegExigeConfirmacao.isEmpty()) {
            TpsExigeConfirmacaoRenegociacaoValoresEnum tpsCsaRenegEnum = TpsExigeConfirmacaoRenegociacaoValoresEnum.recuperaTpsExigeConfirmacaoRenegociacao((String) tpsCsaRenegExigeConfirmacao.get(0).getAttribute(Columns.PSC_VLR));
            if ((adeVlr.compareTo(autdes.getAdeVlr()) < 0 && tpsCsaRenegEnum.equals(TpsExigeConfirmacaoRenegociacaoValoresEnum.SOMENTE_PARA_MAIOR)) ||
                    (adeVlr.compareTo(autdes.getAdeVlr()) > 0 && tpsCsaRenegEnum.equals(TpsExigeConfirmacaoRenegociacaoValoresEnum.SOMENTE_PARA_MENOR)) ||
                    (tpsCsaRenegEnum.equals(TpsExigeConfirmacaoRenegociacaoValoresEnum.NENHUMA))) {
                podeConfirmar = true;
            }
        }

        return podeConfirmar;
    }

    private void verificaMargemNegativa(String rseCodigo, String svcCodigo, AcessoSistema responsavel) throws AutorizacaoControllerException, ServidorControllerException {
        // Verifica se o servidor possui margem positiva
        // Se possuir o mesmo não pode alongar contrato
        if (consultarMargemController.servidorTemMargem(rseCodigo, null, svcCodigo, false, responsavel)) {
            throw new AutorizacaoControllerException("mensagem.erro.servidor.possui.margem.positiva.nao.pode.alongar.este.contrato", responsavel);
        }
    }

    private void verificaParcelaUltimoPeriodoRejeitada(String adeCodigo, String orgCodigo, AcessoSistema responsavel) throws DAOException, ParseException, FindException, AutorizacaoControllerException {
        boolean temParcelaRejeitada = false;

        // Obtém o último período de retorno do órgão do servidor
        // pelos históricos de conclusão de retorno gravados pelo sistema
        Date ultPeriodoRetorno = null;
        ObtemUltimoPeriodoRetornoQuery ultPeriodoQuery = new ObtemUltimoPeriodoRetornoQuery();
        ultPeriodoQuery.orgCodigo = orgCodigo;
        List<Date> ultPeriodoList = ultPeriodoQuery.executarLista();
        if (ultPeriodoList != null && !ultPeriodoList.isEmpty()) {
            ultPeriodoRetorno = ultPeriodoList.get(0);
        }

        if (ultPeriodoRetorno != null) {
            // DESENV-16565 : considera parcela paga parcialmente como parcela rejeitada para o critério de alongamento
            boolean consideraPrdPagaParcial = ParamSist.paramEquals(CodedValues.TPC_CONSIDERA_PARCELA_PAGA_PARCIAL_ALONGAMENTO_PRD_REJEITADA, CodedValues.TPC_SIM, responsavel);

            // Localiza a parcela do contrato, baseado neste período. Verifica apenas na tabela de parcelas históricas já integradas
            // pois, obviamente, a tabela de parcelas do período não terá parcelas rejeitadas do último periodo.
            List<ParcelaDesconto> parcelas = ParcelaDescontoHome.findByAutDescontoPeriodo(adeCodigo, new java.sql.Date(ultPeriodoRetorno.getTime()));
            if (parcelas != null && !parcelas.isEmpty()) {
                for (ParcelaDesconto parcela : parcelas) {
                    String spdCodigo = parcela.getStatusParcelaDesconto().getSpdCodigo();
                    if ((spdCodigo.equals(CodedValues.SPD_REJEITADAFOLHA)) ||
                            (consideraPrdPagaParcial && (spdCodigo.equals(CodedValues.SPD_LIQUIDADAFOLHA) || spdCodigo.equals(CodedValues.SPD_LIQUIDADAMANUAL)) && parcela.getPrdVlrPrevisto().compareTo(parcela.getPrdVlrRealizado()) > 0)) {
                        temParcelaRejeitada = true;
                        break;
                    }
                }
            }
        }

        if (!temParcelaRejeitada) {
            throw new AutorizacaoControllerException("mensagem.erro.contrato.nao.pode.ser.alongado.pois.nao.possui.parcela.rejeitada.folha.periodo.anterior", (AcessoSistema) null);
        }
    }
}
