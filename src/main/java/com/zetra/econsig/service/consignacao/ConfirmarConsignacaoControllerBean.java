package com.zetra.econsig.service.consignacao;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import com.zetra.econsig.delegate.LogDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.parametros.ConfirmarConsignacaoParametros;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.LimiteTentativaExcedidaException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.consignacao.ControleConfirmacaoAutorizacao;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.log.Log;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.periodo.PeriodoHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessaEnvioEmailEntidadesAltAde;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.AutDescontoHome;
import com.zetra.econsig.persistence.entity.Convenio;
import com.zetra.econsig.persistence.entity.ConvenioHome;
import com.zetra.econsig.persistence.entity.CorrespondenteHome;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacao;
import com.zetra.econsig.persistence.entity.OcorrenciaAutorizacaoHome;
import com.zetra.econsig.persistence.entity.ParamSvcConsignante;
import com.zetra.econsig.persistence.entity.ParamSvcConsignanteHome;
import com.zetra.econsig.persistence.entity.RegistroServidorHome;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacao;
import com.zetra.econsig.persistence.entity.RelacionamentoAutorizacaoHome;
import com.zetra.econsig.persistence.entity.VerbaConvenio;
import com.zetra.econsig.persistence.entity.VerbaConvenioHome;
import com.zetra.econsig.persistence.query.parametro.ListaRelacionamentosQuery;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.parametro.ParametroController;
import com.zetra.econsig.service.servico.ServicoController;
import com.zetra.econsig.service.simulacao.SimulacaoController;
import com.zetra.econsig.service.sistema.TipoMotivoOperacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.OperacaoEConsigEnum;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: ConfirmarConsignacaoControllerBean</p>
 * <p>Description: Session Bean para a operação de Confirmação de Consignação.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
@Service
@Transactional
public class ConfirmarConsignacaoControllerBean extends DeferirConsignacaoControllerBean implements ConfirmarConsignacaoController {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ConfirmarConsignacaoControllerBean.class);

    @Autowired
    private LeilaoSolicitacaoController leilaoSolicitacaoController;

    @Autowired
    private PesquisarConsignacaoController pesquisarConsignacaoController;

    @Autowired
    private SimulacaoController simulacaoController;

    @Autowired
    private ParametroController parametroController;

    @Autowired
    private ServicoController servicoController;

    @Autowired
    private EditarAnexoConsignacaoController editarAnexoConsignacaoController;

    @Autowired
    private TipoMotivoOperacaoController tipoMotivoOperacaoController;

    @Override
    public void confirmar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        confirmar(adeCodigo, null, null, null, null, null, null, null, null, null, false, tipoMotivoOperacao, responsavel);
    }


    @Override
    public void confirmar(String adeCodigo, CustomTransferObject tipoMotivoOperacao, ConfirmarConsignacaoParametros confirmAdeParams, AcessoSistema responsavel) throws AutorizacaoControllerException {
        confirmar(adeCodigo, null, null, null, null, null, null, null, null, null, false, null, null, tipoMotivoOperacao, confirmAdeParams, responsavel);
    }


    /**
     * Confirma uma consignação. O status deve estar em 'Aguard. Confirmação' ou 'Solicitação'.
     * @param adeCodigo : código da autorização
     * @param adeVlr : o novo valor da autorização
     * @param adeIdentificador : o novo identificador da autorização
     * @param adeBanco : cod. banco para depósito
     * @param adeAgencia : cod. agência para depósito
     * @param adeConta : cod. conta para depósito
     * @param corCodigo : código do correspondente
     * @param adePrazo: novo valor do prazo
     * @param senhaUtilizada : senha do servidor informada na confirmação
     * @param codAutorizacao : código de autorização informado na confirmação
     * @param comSerSenha : informa se foi utilizada a senha do servidor
     * @param tipoMotivoOperacao : dados do motivo de operação
     * @param responsavel : responsável
     * @throws AutorizacaoControllerException
     */
    @Override
    public void confirmar(String adeCodigo, BigDecimal adeVlr, String adeIdentificador, String adeBanco, String adeAgencia, String adeConta, String corCodigo, Integer adePrazo,
            String senhaUtilizada, String codAutorizacao, boolean comSerSenha, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        confirmar(adeCodigo, adeVlr, adeIdentificador, adeBanco, adeAgencia, adeConta, corCodigo, adePrazo, senhaUtilizada, codAutorizacao, comSerSenha, null, null, tipoMotivoOperacao, responsavel);
    }

    /**
     * Confirma uma consignação. O status deve estar em 'Aguard. Confirmação' ou 'Solicitação'.
     * @param adeCodigo : código da autorização
     * @param adeVlr : o novo valor da autorização
     * @param adeIdentificador : o novo identificador da autorização
     * @param adeBanco : cod. banco para depósito
     * @param adeAgencia : cod. agência para depósito
     * @param adeConta : cod. conta para depósito
     * @param corCodigo : código do correspondente
     * @param adePrazo : novo valor do prazo
     * @param senhaUtilizada : senha do servidor informada na confirmação
     * @param codAutorizacao : código de autorização informado na confirmação
     * @param comSerSenha : informa se foi utilizada a senha do servidor
     * @param tdaModalidadeOp : modalidade de operação
     * @param tdaMatriculaCsa : matrícula do servidor na consignatária
     * @param tipoMotivoOperacao : dados do motivo de operação
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void confirmar(String adeCodigo, BigDecimal adeVlr, String adeIdentificador, String adeBanco, String adeAgencia, String adeConta, String corCodigo, Integer adePrazo,
            String senhaUtilizada, String codAutorizacao, boolean comSerSenha, String tdaModalidadeOp, String tdaMatriculaCsa, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws AutorizacaoControllerException {
        confirmar(adeCodigo, adeVlr, adeIdentificador, adeBanco, adeAgencia, adeConta, corCodigo, adePrazo, senhaUtilizada, codAutorizacao, comSerSenha, tdaModalidadeOp, tdaMatriculaCsa, tipoMotivoOperacao, null, responsavel);
    }

    /**
     * Confirma uma consignação. O status deve estar em 'Aguard. Confirmação' ou 'Solicitação'.
     * @param adeCodigo : código da autorização
     * @param adeVlr : o novo valor da autorização
     * @param adeIdentificador : o novo identificador da autorização
     * @param adeBanco : cod. banco para depósito
     * @param adeAgencia : cod. agência para depósito
     * @param adeConta : cod. conta para depósito
     * @param corCodigo : código do correspondente
     * @param adePrazo : novo valor do prazo
     * @param senhaUtilizada : senha do servidor informada na confirmação
     * @param codAutorizacao : código de autorização informado na confirmação
     * @param comSerSenha : informa se foi utilizada a senha do servidor
     * @param tdaModalidadeOp : modalidade de operação
     * @param tdaMatriculaCsa : matrícula do servidor na consignatária
     * @param tipoMotivoOperacao : dados do motivo de operação
     * @param confirmAdeParams : parâmetros adicionais para confirmação da consignação
     * @param responsavel
     * @throws AutorizacaoControllerException
     */
    @Override
    public void confirmar(String adeCodigo, BigDecimal adeVlr, String adeIdentificador, String adeBanco, String adeAgencia, String adeConta, String corCodigo, Integer adePrazo,
            String senhaUtilizada, String codAutorizacao, boolean comSerSenha, String tdaModalidadeOp, String tdaMatriculaCsa, CustomTransferObject tipoMotivoOperacao, ConfirmarConsignacaoParametros confirmAdeParams, AcessoSistema responsavel) throws AutorizacaoControllerException {
        if (usuarioPodeModificarAde(adeCodigo, responsavel)) {
            try {
                AutDesconto autdes = AutDescontoHome.findByPrimaryKeyForUpdate(adeCodigo);
                String sadCodigo = autdes.getStatusAutorizacaoDesconto().getSadCodigo().trim();
                String rseCodigo = autdes.getRegistroServidor().getRseCodigo();
                String ocaCodigo = null;
                String srsCodigo = RegistroServidorHome.findByPrimaryKey(rseCodigo).getStatusRegistroServidor().getSrsCodigo();
                Date ocaPeriodo = (responsavel.isCseSup() || responsavel.isCsaCor()) && confirmAdeParams != null && confirmAdeParams.getOcaPeriodo() != null ? confirmAdeParams.getOcaPeriodo() : null;

                // Não permite confirmar contratos de servidores com situação "Pendente"
                if (srsCodigo.equals(CodedValues.SRS_PENDENTE)) {
                    throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.confirmada.servidor.pendente", responsavel);
                }

                if (!sadCodigo.equals(CodedValues.SAD_AGUARD_CONF) && !sadCodigo.equals(CodedValues.SAD_SOLICITADO)) {
                    throw new AutorizacaoControllerException("mensagem.erro.autorizacao.nao.pode.ser.confirmada.situacao.atual.nao.permite.operacao", responsavel, autdes.getStatusAutorizacaoDesconto().getSadDescricao());
                } else if (sadCodigo.equals(CodedValues.SAD_AGUARD_CONF)) {
                    //DESENV-14162 Confirmação de reserva não se aplica a contratos destinos de renegociação/portabilidade. Deve ser chamada a Confirmação de Renegociação.
                    List<TransferObject> relRenegociacao = pesquisarConsignacaoController.pesquisarConsignacaoRelacionamento(null, adeCodigo, null, null, CodedValues.TNT_CONTROLE_RENEGOCIACAO, null, responsavel);
                    List<TransferObject> relCompra = pesquisarConsignacaoController.pesquisarConsignacaoRelacionamento(null, adeCodigo, null, null, CodedValues.TNT_CONTROLE_COMPRA, null, responsavel);
                    if ((relRenegociacao != null && !relRenegociacao.isEmpty()) || (relCompra != null && !relCompra.isEmpty())) {
                        if (!usuarioPodeConfirmarRenegociacao(responsavel)) {
                            throw new ZetraException("mensagem.erro.usuario.atual.nao.possui.permissao.para.confirmar.renegociacao", responsavel);
                        }
                    } else if (!usuarioPodeConfirmarReserva(responsavel)) {
                        throw new AutorizacaoControllerException("mensagem.erro.usuario.atual.nao.possui.permissao.para.confirmar.reserva", responsavel);
                    }
                }

                VerbaConvenio verbaConvenio = VerbaConvenioHome.findByPrimaryKey(autdes.getVerbaConvenio().getVcoCodigo());
                String cnvCodigo = verbaConvenio.getConvenio().getCnvCodigo();
                Convenio convenio = ConvenioHome.findByPrimaryKey(cnvCodigo);
                String csaCodigo = convenio.getConsignataria().getCsaCodigo();
                String svcCodigo = convenio.getServico().getSvcCodigo();

                // Verifica se as entidades estão ativas para fazer novas reservas
                validarEntidades(cnvCodigo, corCodigo, responsavel);

                if (sadCodigo.equals(CodedValues.SAD_SOLICITADO)) {
                    // Verifica se exige código de autorização para solicitação
                    verificaCodAutSolicitacao(adeCodigo, svcCodigo, codAutorizacao, responsavel);
                    // Verifica a solicitação de proposta se ainda está pendente
                    verificaSolicitacaoPropostasLeilao(adeCodigo, responsavel);
                    // DESENV-7612: se solicitação é de crédito eletrônico e exige assinatura digital de documentação anexa,
                    // verifica se esta já foi anexada, aprovada e assinada.
                    statusSoaCredEletronicoPermiteConfirmacao(adeCodigo, responsavel);
                }

                // Verifica se já foi informada senha de autorização, caso esta defira contratos
                boolean jaInformouSenha = false;
                if (ParamSist.paramEquals(CodedValues.TPC_SER_SENHA_DEFERE_RESERVA, CodedValues.TPC_SIM, responsavel)) {
                    // Busca ocorrências de TOC_AUTORIZACAO_VIA_SENHA_SERVIDOR
                    List<OcorrenciaAutorizacao> ocorrencias = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, CodedValues.TOC_AUTORIZACAO_VIA_SENHA_SERVIDOR);
                    jaInformouSenha = (ocorrencias != null && !ocorrencias.isEmpty());
                }

                if (sadCodigo.equals(CodedValues.SAD_SOLICITADO) && !usuarioPodeConfirmarReserva(responsavel)) {
                    // Confirmando uma solicitação e usuário não pode confirmar reserva, então muda status para SAD_AGUARD_CONF
                    ocaCodigo = modificaSituacaoADE(autdes, CodedValues.SAD_AGUARD_CONF, responsavel, true, ocaPeriodo, true);
                } else if (usuarioPodeDeferir(svcCodigo, csaCodigo, rseCodigo, jaInformouSenha, responsavel)) {
                    // Convênio permite deferimento, ou a senha do servidor foi informada e esta defere contratos
                    ocaCodigo = deferir(autdes, senhaUtilizada, true, true, null, ocaPeriodo, responsavel);


                    // se há ocorrência de cancelamento deste contrato e não deferimento, então confere se contrato que seria confirmar é destino
                    // de relacionamento de insere/altera. Se sim, não continua com a confirmação, pois este contrato foi cancelado e
                    // o contrato origem foi alterado segundo as regras de insere/altera
                    List<OcorrenciaAutorizacao> ocaLst = OcorrenciaAutorizacaoHome.findByAdeTocCodigo(adeCodigo, CodedValues.TOC_TARIF_CANCELAMENTO_RESERVA);
                    if (ocaLst != null && !ocaLst.isEmpty()) {
                        List<RelacionamentoAutorizacao> lstRel = RelacionamentoAutorizacaoHome.findByDestino(adeCodigo, CodedValues.TNT_CONTRATO_GERADO_INSERE_ALTERA);
                        for (RelacionamentoAutorizacao rel: lstRel) {
                            if (rel.getAdeCodigoDestino().equals(adeCodigo)) {
                                return;
                            }
                        }
                    }
                } else {
                    // Se não permite deferimento, então muda status para SAD_AGUARD_DEFER
                    ocaCodigo = modificaSituacaoADE(autdes, CodedValues.SAD_AGUARD_DEFER, responsavel, true, ocaPeriodo, true);

                    // Se contrato na situação SAD_AGUARD_DEFER incluir ocorrência
                    // com prazo para deferimento automático
                    alertarPrazoDeferimentoAut(autdes, svcCodigo, null, responsavel);
                }

                boolean pulaInformacaoValorPrazo = false;
                //verifica se deve gravar os campos de valor e prazo digitados pelo usuário.
                ParamSvcConsignante paramSvcCsePula = null;

                try {
                    paramSvcCsePula = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_PULA_INFORMACAO_VALOR_PRAZO_FLUXO_RESERVA, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
                } catch (FindException ex) {
                    // Parâmetro não encontrado, então assume o valor padrão para incide margem
                    paramSvcCsePula = null;
                }

                if (!TextHelper.isNull(paramSvcCsePula)) {
                    CustomTransferObject naturezaSvc = servicoController.findNaturezaServico(paramSvcCsePula.getServico().getSvcCodigo(), responsavel);
                    pulaInformacaoValorPrazo = (paramSvcCsePula.getPseVlr() != null && paramSvcCsePula.getPseVlr().equals("1") && naturezaSvc != null && !TextHelper.isNull(naturezaSvc.getAttribute(Columns.NSE_CODIGO)) && !naturezaSvc.getAttribute(Columns.NSE_CODIGO).toString().equals(CodedValues.NSE_EMPRESTIMO));
                }

                // Altera o adeIdentificador
                if (adeIdentificador != null) {
                    autdes.setAdeIdentificador(adeIdentificador);
                }

                // Altera os dados bancários
                if (adeBanco != null && adeAgencia != null && adeConta != null) {
                    autdes.setAdeBanco(adeBanco);
                    autdes.setAdeAgencia(adeAgencia);
                    autdes.setAdeConta(adeConta);
                }

                // Atualiza o Correspondente
                if (corCodigo != null) {
                    autdes.setCorrespondente(CorrespondenteHome.findByPrimaryKey(corCodigo));
                }

                // Atualiza o valor do Prazo
                if (adePrazo != null) {
                    autdes.setAdePrazo(adePrazo);
                }else if (adePrazo == null && pulaInformacaoValorPrazo && responsavel.isCsaCor()){
                    throw new AutorizacaoControllerException("mensagem.erro.pula.etapa.obrigatorios", responsavel);
                }

                boolean validaMargem = true;

                // Antes de confirmar a reserva verifica o relacionamento entre servicos
                boolean temAlongamento = ParamSist.paramEquals(CodedValues.TPC_TEM_ALONGAMENTO_CONTRATO, CodedValues.TPC_SIM, responsavel);
                if (temAlongamento) {
                    // Se o serviço do contrato é origem de um relacionamento de alongamento, então não valida a margem do servidor
                    ListaRelacionamentosQuery queryRel = new ListaRelacionamentosQuery();
                    queryRel.tntCodigo = CodedValues.TNT_ALONGAMENTO;
                    queryRel.svcCodigoOrigem = svcCodigo;
                    queryRel.svcCodigoDestino = null;
                    List<TransferObject> relacionamentoAlongamento = queryRel.executarDTO();
                    if (relacionamentoAlongamento != null && relacionamentoAlongamento.size() > 0) {
                        validaMargem = false;
                    }
                }

                // Altera o valor da autorização e libera da margem do servidor o vlr alterado
                if (adeVlr != null) {
                    BigDecimal diff = autdes.getAdeVlr().subtract(adeVlr);
                    if ((diff.signum() == 1) || (pulaInformacaoValorPrazo)) {
                        // Altera valor da autorização
                        autdes.setAdeVlr(adeVlr);
                        // Altera a margem do servidor
                        atualizaMargem(rseCodigo, autdes.getAdeIncMargem(), diff.negate(), validaMargem, true, true, ocaCodigo, csaCodigo, svcCodigo, null, responsavel);
                    } else if ((diff.signum() == -1) && (!pulaInformacaoValorPrazo)) {
                        throw new AutorizacaoControllerException("mensagem.erro.novo.valor.parcela.deve.ser.menor.atual", responsavel);
                    }
                }

                AutDescontoHome.update(autdes);

                String tpaModalidadeOperacao = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INF_MODALIDADE_OPERACAO_OBRIGATORIO, responsavel);
                boolean exigeModalidadeOperacao = (!TextHelper.isNull(tpaModalidadeOperacao) && tpaModalidadeOperacao.equals("S")) ? true : false;
                if (responsavel.isCsaCor() && exigeModalidadeOperacao) {
                    if (TextHelper.isNull(tdaModalidadeOp)) {
                        throw new AutorizacaoControllerException("mensagem.erro.modalidade.operacao.obrigatorio", responsavel);
                    }
                    setDadoAutDesconto(autdes.getAdeCodigo(), CodedValues.TDA_MODALIDADE_OPERACAO, tdaModalidadeOp, responsavel);
                }

                String tpaMatriculaSerCsa = parametroController.getParamCsa(csaCodigo, CodedValues.TPA_INFORMAR_MATRICULA_NA_CSA_OBRIGATORIO, responsavel);
                boolean exigeMatriculaSerCsa = (!TextHelper.isNull(tpaMatriculaSerCsa) && tpaMatriculaSerCsa.equals("S")) ? true : false;
                if (responsavel.isCsaCor() && exigeMatriculaSerCsa) {
                    if (TextHelper.isNull(tdaMatriculaCsa)) {
                        throw new AutorizacaoControllerException("mensagem.erro.matricula.csa.obrigatoria", responsavel);
                    }
                    setDadoAutDesconto(autdes.getAdeCodigo(), CodedValues.TDA_MATRICULA_SER_NA_CSA, tdaMatriculaCsa, responsavel);
                }

                if (ocaCodigo != null && tipoMotivoOperacao != null) {
                    // grava motivo da operacao
                    tipoMotivoOperacao.setAttribute(Columns.OCA_CODIGO, ocaCodigo);
                    tipoMotivoOperacaoController.gravarMotivoOperacaoConsignacao(tipoMotivoOperacao, responsavel);
                }

                // Inclui ocorrência mostrando autorização pela senha do servidor.
                if (comSerSenha) {
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_AUTORIZACAO_VIA_SENHA_SERVIDOR, ApplicationResourcesHelper.getMessage("mensagem.informacao.autorizacao.pela.senha.do.servidor", responsavel), responsavel);
                }

                confirmarCorrecaoSaldo(adeCodigo, adeVlr, adeIdentificador, corCodigo, adePrazo, tipoMotivoOperacao, responsavel);

                //DESENV-15344 : Anexos de confirmar reserva.
                if (confirmAdeParams != null && confirmAdeParams.getAnexos() != null && !confirmAdeParams.getAnexos().isEmpty()) {
                    String aadDescricao = ApplicationResourcesHelper.getMessage("mensagem.informacao.upload.confirmacao.consignacao", responsavel);
                    Date periodoAtual = PeriodoHelper.getInstance().getPeriodoAtual(autdes.getVerbaConvenio().getConvenio().getOrgao().getOrgCodigo(), responsavel);

                    for (File anexo : confirmAdeParams.getAnexos()) {
                        if (anexo.exists()){
                            editarAnexoConsignacaoController.createAnexoAutorizacaoDesconto(adeCodigo, anexo.getName(), (!TextHelper.isNull(confirmAdeParams.getAnexoObs())) ? confirmAdeParams.getAnexoObs() : aadDescricao, new java.sql.Date(periodoAtual.getTime()),
                                    TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_SUSPENSAO, "S", "S", (responsavel.isCseSup()) ? "N" : "S", (responsavel.isCsaCor() || responsavel.isSer()) ? "S" : "N",
                                    (responsavel.isCor() || (responsavel.isSer() && !TextHelper.isNull(corCodigo))) ? "S" : "N", "N", responsavel);
                        }
                    }
                }

                // Gera o Log de auditoria
                LogDelegate logDelegate = new LogDelegate(responsavel, Log.AUTORIZACAO, Log.CONFIRMAR_CONSIGNACAO, Log.LOG_INFORMACAO);
                logDelegate.setAutorizacaoDesconto(adeCodigo);
                logDelegate.write();

                // DESENV-8933: se responsável pela operação tiver seu papel configurado para disparar e-mail de alerta de alteração na ade
                // para entidades relacionadas a este, também configuradas na tabela tb_destinatario_email
                ProcessaEnvioEmailEntidadesAltAde processoEmail = new ProcessaEnvioEmailEntidadesAltAde(OperacaoEConsigEnum.CONFIRMAR_SOLICITACAO, adeCodigo, null, tipoMotivoOperacao, responsavel);
                processoEmail.start();

                // se parâmetro 463 estiver com valor 'S' envia email para usuário cse do sistema na inclusão de contrato *aguardando deferimento*
                // geralmente estará setado como 'S' para o eConsig Light
                if (ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_CSE_NOVO_CONTRATO, CodedValues.TPC_SIM, responsavel) && autdes.getStatusAutorizacaoDesconto().getSadCodigo().equals(CodedValues.SAD_AGUARD_DEFER)) {
                    EnviaEmailHelper.enviarEmailCseInclusaoAde(autdes.getAdeCodigo(), OperacaoEConsigEnum.CONFIRMAR_RESERVA.getOperacao(), responsavel);
                }

            } catch (LimiteTentativaExcedidaException ex) {
                LOG.error(ex.getMessage(), ex);
                throw new AutorizacaoControllerException(ex);
            } catch (Exception ex) {
                LOG.error(ex.getMessage(), ex);
                TransactionInterceptor.currentTransactionStatus().setRollbackOnly();
                if (ex.getClass().equals(AutorizacaoControllerException.class)) {
                    throw (AutorizacaoControllerException) ex;
                } else {
                    throw new AutorizacaoControllerException(ex);
                }
            }
        }

    }

    private void verificaCodAutSolicitacao(String adeCodigo, String svcCodigo, String codAutorizacao, AcessoSistema responsavel) throws ZetraException {
        try {
            ParamSvcConsignante paramSvcCse = ParamSvcConsignanteHome.findByTipoCseServico(CodedValues.TPS_EXIGE_CODIGO_AUTORIZACAO_CONF_SOLIC, CodedValues.CSE_CODIGO_SISTEMA, svcCodigo);
            boolean exigeCodAutSolicitacao = (paramSvcCse != null && paramSvcCse.getPseVlr() != null && paramSvcCse.getPseVlr().equals("1"));

            if (exigeCodAutSolicitacao) {
                String adeCodAutSolic = getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_CODIGO_AUTORIZACAO_SOLICITACAO, false, responsavel);
                if (adeCodAutSolic == null || adeCodAutSolic.isEmpty()) {
                    throw new ZetraException("mensagem.erro.codigo.autorizacao.para.confirmacao.solicitacao.invalido", responsavel);
                }
                if (!adeCodAutSolic.equals(codAutorizacao)) {
                    ControleConfirmacaoAutorizacao.getInstance().bloqueia(adeCodigo, responsavel);
                    throw new ZetraException("mensagem.erro.codigo.autorizacao.para.confirmacao.solicitacao.invalido", responsavel);
                }
                // Inclui ocorrência informando autorização pelo código de autorização.
                if (!TextHelper.isNull(codAutorizacao)) {
                    criaOcorrenciaADE(adeCodigo, CodedValues.TOC_INFORMACAO, ApplicationResourcesHelper.getMessage("mensagem.informacao.autorizacao.pelo.codigo.autorizacao", responsavel), responsavel);
                }
            }
        } catch (FindException e) {
        }
    }

    private void verificaSolicitacaoPropostasLeilao(String adeCodigo, AcessoSistema responsavel) throws ZetraException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) {
            if (leilaoSolicitacaoController.temSolicitacaoLeilao(adeCodigo, true, responsavel)) {
                throw new ZetraException("mensagem.erro.solicitacao.nao.pode.ser.confirmada.leilao.pendente", responsavel);
            }
        }
    }

    /**
     * verifica se não há solicitações com status de crédito eletrônico que impeçam a confirmação deste ade. Os status são
     * "Pendente de Validação de Documentos", "Pendente de Informação da Documentação" ou "Pendente de Assinatura da Documentação"
     * (DESENV-7612)
     * @param adeCodigo
     * @param responsavel
     * @throws ZetraException
     */
    private void statusSoaCredEletronicoPermiteConfirmacao(String adeCodigo, AcessoSistema responsavel) throws ZetraException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_ASSINATURA_DIGITAL_CONSIGNACAO, CodedValues.TPC_SIM, responsavel)) {
            List<String> tisCodigos = new ArrayList<>();
            tisCodigos.add(TipoSolicitacaoEnum.SOLICITACAO_CONSIGNACAO_CREDITO_ELETRONICO.getCodigo());
            List<String> ssoCodigos = new ArrayList<>();
            ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo());
            ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_ASSINATURA_DOCUMENTACAO.getCodigo());
            ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_INFORMACAO_DOCUMENTACAO.getCodigo());
            ssoCodigos.add(StatusSolicitacaoEnum.DOCUMENTACAO_ENVIADA_PARA_ASSINATURA.getCodigo());

            List<TransferObject> lstRgsSoa = simulacaoController.lstRegistrosSolicitacaoAutorizacao(adeCodigo, tisCodigos, ssoCodigos, responsavel);
            if (lstRgsSoa != null && !lstRgsSoa.isEmpty()) {
                throw new ZetraException("mensagem.erro.solicitacao.nao.pode.ser.confirmada.doc.pendente.assinatura.digital", responsavel);
            }
        }
    }

    private void confirmarCorrecaoSaldo(String adeCodigo, BigDecimal adeVlr, String adeIdentificador, String corCodigo, Integer adePrazo, CustomTransferObject tipoMotivoOperacao, AcessoSistema responsavel) throws FindException, AutorizacaoControllerException {
        // Confirma os contratos de correção de saldo relacionados a este contrato
        List<RelacionamentoAutorizacao> radCorrecaoSaldo = RelacionamentoAutorizacaoHome.findByOrigem(adeCodigo, CodedValues.TNT_CORRECAO_SALDO);
        Iterator<RelacionamentoAutorizacao> it = radCorrecaoSaldo.iterator();

        while (it.hasNext()) {
            BigDecimal adeCorrecaoVlr = null;
            String adeCorrecaoId = null;

            RelacionamentoAutorizacao radBean = it.next();
            String adeCorrecaoCodigo = radBean.getAutDescontoByAdeCodigoDestino().getAdeCodigo();
            AutDesconto adeCorrecao = AutDescontoHome.findByPrimaryKey(adeCorrecaoCodigo);

            // se a confirmação é chamada para o contrato de origem sem passagem do valor e do identifador
            // os mesmos não são passados para a confirmação dos seus contratos de correção de saldo
            if (adeVlr != null) {
                adeCorrecaoVlr = adeCorrecao.getAdeVlr();
            }

            if (adeIdentificador != null) {
                adeCorrecaoId = adeCorrecao.getAdeIdentificador();
            }
            confirmar(adeCorrecaoCodigo, adeCorrecaoVlr, adeCorrecaoId, null, null, null, corCodigo, adePrazo, null, null, false, tipoMotivoOperacao, responsavel);
        }
    }
}
