package com.zetra.econsig.helper.email;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.zetra.econsig.helper.email.command.EnviarEmailSimulacaoCommand;
import com.zetra.econsig.webservice.rest.request.CsaListInfoRequest;
import org.apache.commons.lang3.StringEscapeUtils;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ParametroDelegate;
import com.zetra.econsig.delegate.SaldoDevedorDelegate;
import com.zetra.econsig.delegate.ServicoDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.delegate.TipoMotivoOperacaoDelegate;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.CorrespondenteTransferObject;
import com.zetra.econsig.dto.entidade.EstabelecimentoTransferObject;
import com.zetra.econsig.dto.entidade.MensagemTO;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.dto.entidade.ParamSvcTO;
import com.zetra.econsig.dto.entidade.SaldoDevedorTransferObject;
import com.zetra.econsig.dto.entidade.ServidorTransferObject;
import com.zetra.econsig.dto.entidade.TipoMotivoOperacaoTransferObject;
import com.zetra.econsig.dto.entidade.UsuarioTransferObject;
import com.zetra.econsig.exception.AutorizacaoControllerException;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.LeilaoSolicitacaoControllerException;
import com.zetra.econsig.exception.MensagemControllerException;
import com.zetra.econsig.exception.ParametroControllerException;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.UsuarioControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.consignacao.BoletoHelper;
import com.zetra.econsig.helper.email.command.EnviaEmailCsaDesbloqueioVerbaRseCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailAlertaCriacaoNovoUsuCseCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailAlertaEnvioArquivosFolhaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailAlertaRetornoServidorCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailAlteracaoOperacaoSensivelCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailAlteracaoPerfilUsuarioCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailBloqDesbloqServidorCsaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailConfirmacaoDesbloqueioCsaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailCsaContratosColocadosEmEstoqueCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailCsaNotificacaoRegraCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailCsaNovaSolicitacaoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailCsaPortabilidadeCartaoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailCsaSerCancelaSolicitacaoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailCsasAlteracaoSerCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailCseBloqueioUsuarioCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailDownloadNaoRealizadoMovFinCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailExpiracaoConsignatariaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailExpiracaoParaConsignatariaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailLimiteAtigindoInclusaoCsaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotifAlterCodVerbaConvCsaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoAutorizacaoIraVencerCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCadastroSenhaServidorCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCadastroServidorCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoConsignacaoDeferidaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCsaCancelamentoCadastroServidorCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCsaCredenciamentoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCsaCredenciamentoConcluidoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCsaErroKYCCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCsaNovoVinculoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCsaVinculosBloqDesbloqCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCseAssTermoCsaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCseBloqueioCsaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCseCredenciamentoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCseCredenciamentoConcluidoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoCseReativacaoPrdRejeitadaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoDesbloqueioCsaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoDocCsaCredenciamentoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoPrazoExpiracaoSenhaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoPreenchimentoTermoCredenciamentoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoReservaMargemCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoSituacaoCsaCredenciamentoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificacaoUsuPermissaoAprovacaoAssTermoCseCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificaoCsaBloqSerCnvVariacaoMargemCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificaoCseBloqSerCnvVariacaoMargemCsaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNotificaoErroCriarArquivoMargemCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNovoBoletoServidorCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailNovoContratoVerbaRescisoriaSerCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailOTPServidorCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailOTPUsuarioCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailRefinanciamentoCsaCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailSenhaNovoUsuarioCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailServidorContratosPendentesReativacaoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailServidorContratosRejeitadosCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailServidorVerbaRescisoriaSaldoInsuficienteCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailSimulacaoCommand;
import com.zetra.econsig.helper.email.command.EnviarEmailUploadArquivoCsaCommand;
import com.zetra.econsig.helper.email.command.EnviarLinkCriarSenhaUsuarioCommand;
import com.zetra.econsig.helper.email.command.EnviarLinkRecurepacaoSenhaServidorCommand;
import com.zetra.econsig.helper.email.command.EnviarNotificacaoBloqueioUsuarioInatividadeCommand;
import com.zetra.econsig.helper.markdown.Markdown4jProcessorExtended;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.ShowFieldHelper;
import com.zetra.econsig.helper.sms.SMSHelper;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.NumberHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.helper.upload.UploadHelper;
import com.zetra.econsig.helper.usuario.UsuarioHelper;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.persistence.entity.AutDesconto;
import com.zetra.econsig.persistence.entity.Comunicacao;
import com.zetra.econsig.persistence.entity.Consignataria;
import com.zetra.econsig.persistence.entity.CredenciamentoCsa;
import com.zetra.econsig.persistence.entity.LeituraMensagemUsuario;
import com.zetra.econsig.persistence.entity.Mensagem;
import com.zetra.econsig.persistence.entity.PropostaLeilaoSolicitacao;
import com.zetra.econsig.persistence.entity.SolicitacaoAutorizacao;
import com.zetra.econsig.persistence.entity.VinculoRegistroServidor;
import com.zetra.econsig.report.jasper.dto.RegrasConvenioParametrosBean;
import com.zetra.econsig.service.comunicacao.ComunicacaoController;
import com.zetra.econsig.service.consignacao.EditarAnexoConsignacaoController;
import com.zetra.econsig.service.consignacao.PesquisarConsignacaoController;
import com.zetra.econsig.service.consignante.ConsignanteController;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.service.leilao.LeilaoSolicitacaoController;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.service.rescisao.VerbaRescisoriaController;
import com.zetra.econsig.service.servidor.ServidorController;
import com.zetra.econsig.service.sistema.SistemaController;
import com.zetra.econsig.service.usuario.UsuarioController;
import com.zetra.econsig.values.CodedNames;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.FieldKeysConstants;
import com.zetra.econsig.values.OperacaoEConsigEnum;
import com.zetra.econsig.values.StatusConsignatariaEnum;
import com.zetra.econsig.values.StatusPropostaEnum;
import com.zetra.econsig.values.TipoArquivoEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;
import com.zetra.econsig.webservice.rest.request.CsaListInfoRequest;

import jakarta.mail.MessagingException;

/**
 * <p>Title: EnviaEmailHelper</p>
 * <p>Description: Helper Class para Operação de Envio de Email Com os dados de Uma Consignação</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviaEmailHelper {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviaEmailHelper.class);

    // Tipos de Envio de Email para Acompanhamento de Compra de Contrato (Renegociação)
    public static final int TIPO_COMPRA_CONTRATO             = 1;
    public static final int TIPO_CADASTRO_SALDO_DEVEDOR      = 2;
    public static final int TIPO_INF_PGT_SALDO_DEVEDOR       = 3;
    public static final int TIPO_LIQUIDACAO_COMPRA_CONTRATO  = 4;
    public static final int TIPO_SOL_RECALCULO_SALDO_DEVEDOR = 5;
    public static final int TIPO_REJ_PGT_SALDO_DEVEDOR       = 6;
    public static final int TIPO_APROVACAO_SALDO_DEVEDOR     = 7;
    public static final int TIPO_REJEICAO_SALDO_DEVEDOR      = 8;

    private static String DESTINATARIO_BLOQ_SISTEMA;
    private static String EMAIL_VALIDACAO_INTEGRACAO_CSA;
    private static String EMAIL_OPERACAO_BENEFICIO;

    private static Properties env = new Properties();

    static {
        try {
            env.load(EnviaEmailHelper.class.getClassLoader().getResourceAsStream("mail.properties"));
            EMAIL_VALIDACAO_INTEGRACAO_CSA = env.getProperty("email.validacao.integracao.csa");
            DESTINATARIO_BLOQ_SISTEMA = env.getProperty("email.dest.bloqueio.sistema");
            EMAIL_OPERACAO_BENEFICIO = env.getProperty("email.dest.operacao.beneficio");
        } catch (final IOException ex) {
            LOG.error("Erro ao carregar arquivo mail.properties: " + ex.getMessage(), ex);
        }
    }

    /**
     * Envia email de acompanhamento de compra de contratos (Renegociação). O parâmetro
     * adeCodigo deve conter o código do contrato comprado, aquele que está no estado
     * de Aguard. Liq. de Compra. O tipo de email pode ser TIPO_COMPRA_CONTRATO,
     * TIPO_CADASTRO_SALDO_DEVEDOR, TIPO_INF_PGT_SALDO_DEVEDOR, TIPO_RESOL_SALDO_DEVEDOR ou
     * TIPO_LIQUIDACAO_COMPRA_CONTRATO.
     *
     * @param tipo int
     * @param adeCodigo String
     * @param observacao String
     * @param responsavel AcessoSistema
     * @return String com mensagem de erro
     */
    public static final String enviarEmailCompraContrato(int tipo, String adeCodigo, String observacao, AcessoSistema responsavel) {
        try {
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final SaldoDevedorDelegate sdvDelegate = new SaldoDevedorDelegate();

            // Busca o adeCodigo do contrato novo, que substitui o contrato comprado
            final String adeCodigoNovo = adeDelegate.getAdeRelacionamentoCompra(adeCodigo, responsavel);
            if (adeCodigoNovo == null) {
                return "";
            }

            // Busca as informações do contrato comprado e do contrato novo
            final TransferObject adeComprada = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);
            final TransferObject adeNova = adeDelegate.buscaAutorizacao(adeCodigoNovo, responsavel);
            if ((adeComprada == null) || (adeNova == null)) {
                return "";
            }

            // Recupera os e-mails das consignatárias.
            final ConfirguracoesEmailCsaCompraContrato enderecosCsaOrigem = recuperarEnderecosEmailCompraContrato(tipo, (String) adeComprada.getAttribute(Columns.CSA_CODIGO), (String) adeComprada.getAttribute(Columns.SVC_CODIGO), responsavel);
            final ConfirguracoesEmailCsaCompraContrato enderecosCsaDestino = recuperarEnderecosEmailCompraContrato(tipo, (String) adeNova.getAttribute(Columns.CSA_CODIGO), (String) adeNova.getAttribute(Columns.SVC_CODIGO), responsavel);

            // Recupera os e-mails dos correspondentes.
            final String emailCorOrigem  = (String) adeComprada.getAttribute(Columns.COR_EMAIL);
            final String emailCorDestino = (String) adeNova.getAttribute(Columns.COR_EMAIL);

            // Monta a lista dos destinatários da mensagem.
            List<String> emailDestinatarios;
            switch (tipo) {
                case TIPO_CADASTRO_SALDO_DEVEDOR:
                case TIPO_LIQUIDACAO_COMPRA_CONTRATO:
                case TIPO_REJ_PGT_SALDO_DEVEDOR:
                    // E-mail deve ser enviado para o responsável pelo novo contrato
                    emailDestinatarios = EnviaEmailHelper.montarListaEnderecosDestinatarios(enderecosCsaDestino.destinatariosEmail, enderecosCsaDestino.emailCsaEvento, emailCorDestino);
                    break;
                case TIPO_COMPRA_CONTRATO:
                case TIPO_INF_PGT_SALDO_DEVEDOR:
                case TIPO_SOL_RECALCULO_SALDO_DEVEDOR:
                    // E-mail deve ser enviado para o proprietário do contrato original
                    emailDestinatarios = EnviaEmailHelper.montarListaEnderecosDestinatarios(enderecosCsaOrigem.destinatariosEmail, enderecosCsaOrigem.emailCsaEvento, emailCorOrigem);
                    break;
                case TIPO_APROVACAO_SALDO_DEVEDOR:
                case TIPO_REJEICAO_SALDO_DEVEDOR:
                    // E-mail deve ser enviado para ambas as consignatárias
                    emailDestinatarios = new ArrayList<>(EnviaEmailHelper.montarListaEnderecosDestinatarios(enderecosCsaDestino.destinatariosEmail, enderecosCsaDestino.emailCsaEvento, emailCorDestino));
                    emailDestinatarios.addAll(EnviaEmailHelper.montarListaEnderecosDestinatarios(enderecosCsaOrigem.destinatariosEmail, enderecosCsaOrigem.emailCsaEvento, emailCorOrigem));
                    break;
                default:
                    LOG.error("Operação não identificada em EnviaEmailHelper.enviarEmailCompraContrato");
                    return "";
            }

            // Dados da consignação comprada
            final String adeNumero = adeComprada.getAttribute(Columns.ADE_NUMERO).toString();

            // Descrição das consignatárias envolvidas
            final String csaVendedora = !TextHelper.isNull(adeComprada.getAttribute(Columns.CSA_NOME_ABREV)) ? adeComprada.getAttribute(Columns.CSA_NOME_ABREV).toString() : adeComprada.getAttribute(Columns.CSA_NOME).toString();
            final String csaCompradora = !TextHelper.isNull(adeNova.getAttribute(Columns.CSA_NOME_ABREV)) ? adeNova.getAttribute(Columns.CSA_NOME_ABREV).toString() : adeNova.getAttribute(Columns.CSA_NOME).toString();

            // Obtém o nome do consignante
            final String cseNome = getCseNome(responsavel);

            // Dados do saldo devedor
            String textoSaldoDevedor = "";
            String textoInfDepositoSdv = "";

            // Busca os valores do saldo devedor cadastrados
            final SaldoDevedorTransferObject saldoDevedorTO = sdvDelegate.getSaldoDevedor(adeCodigo, responsavel);
            if (saldoDevedorTO != null) {
                textoSaldoDevedor = gerarTextoDetalheSaldoDevedor(saldoDevedorTO, responsavel);
                textoInfDepositoSdv = gerarTextoInfDepositoSaldoDevedor(saldoDevedorTO);
            }

            // Texto Comum para todas as mensagems
            final StringBuilder textoGeral = new StringBuilder();
            textoGeral.append(ApplicationResourcesHelper.getMessage("mensagem.email.abaixo.seguem.dados.consignacao", responsavel));
            textoGeral.append(gerarTextoDetalheContratoParaEmail(adeComprada, cseNome, responsavel));
            textoGeral.append("<br/>\n<br/>\n");

            // Define o titulo do E-mail a partir da operação realizada
            final StringBuilder tituloOperacao = new StringBuilder();
            switch (tipo) {
                case TIPO_COMPRA_CONTRATO:
                    tituloOperacao.append(ApplicationResourcesHelper.getMessage("mensagem.email.compra.consignacao.renegociada.consignataria", responsavel, adeNumero, csaCompradora));
                    break;
                case TIPO_CADASTRO_SALDO_DEVEDOR:
                    tituloOperacao.append(ApplicationResourcesHelper.getMessage("mensagem.email.compra.consignataria.informou.saldo.devedor", responsavel, csaVendedora, adeNumero));
                    break;
                case TIPO_APROVACAO_SALDO_DEVEDOR:
                    tituloOperacao.append(ApplicationResourcesHelper.getMessage("mensagem.email.compra.servidor.aprovou.saldo.devedor", responsavel, adeNumero));
                    break;
                case TIPO_REJEICAO_SALDO_DEVEDOR:
                    tituloOperacao.append(ApplicationResourcesHelper.getMessage("mensagem.email.compra.servidor.rejeitou.saldo.devedor", responsavel, adeNumero));
                    break;
                case TIPO_INF_PGT_SALDO_DEVEDOR:
                    tituloOperacao.append(ApplicationResourcesHelper.getMessage("mensagem.email.compra.consignataria.informou.pagamento", responsavel, csaCompradora, adeNumero));
                    break;
                case TIPO_SOL_RECALCULO_SALDO_DEVEDOR:
                    tituloOperacao.append(ApplicationResourcesHelper.getMessage("mensagem.email.compra.consignataria.solicitou.novo.calculo", responsavel, csaCompradora, adeNumero));
                    break;
                case TIPO_REJ_PGT_SALDO_DEVEDOR:
                    tituloOperacao.append(ApplicationResourcesHelper.getMessage("mensagem.email.compra.consignataria.rejeitou.pagamento.saldo.devedor", responsavel, csaVendedora, adeNumero));
                    break;
                case TIPO_LIQUIDACAO_COMPRA_CONTRATO:
                    tituloOperacao.append(ApplicationResourcesHelper.getMessage("mensagem.email.compra.consignataria.liquidou.consignacao", responsavel, csaVendedora, adeNumero));
                    break;
                default:
                    break;
            }

            final String titulo = gerarTituloEmail(null, StringEscapeUtils.unescapeHtml4(tituloOperacao.toString()), responsavel);

            // Define o texto do E-mail
            String texto = titulo + "<br/>\n<br/>\n" + textoGeral.toString() + textoSaldoDevedor + textoInfDepositoSdv;

            if (!TextHelper.isNull(observacao)) {
                texto += "<br/>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.compra.observacao", responsavel, observacao);
            }

            // Inclui informações sobre a compra no cabeçalho do e-mail
            final Map<String, String> mailHeaders = new HashMap<>();
            mailHeaders.put("eConsig.nomeConsignante", cseNome);
            mailHeaders.put("eConsig.numeroADECompra", adeNova.getAttribute(Columns.ADE_NUMERO) != null ? adeNova.getAttribute(Columns.ADE_NUMERO).toString() : "");
            mailHeaders.put("eConsig.nomeCSAOrigem", csaVendedora);
            mailHeaders.put("eConsig.nomeCSADestino", csaCompradora);
            mailHeaders.put("eConsig.emailPrincipalCSAOrigem", enderecosCsaOrigem.emailCsaPrincipal != null ? enderecosCsaOrigem.emailCsaPrincipal : "");
            mailHeaders.put("eConsig.emailPrincipalCSADestino", enderecosCsaDestino.emailCsaPrincipal != null ? enderecosCsaDestino.emailCsaPrincipal : "");
            mailHeaders.put("eConsig.emailEventoCompraCSAOrigem", enderecosCsaOrigem.emailCsaEvento != null ? enderecosCsaOrigem.emailCsaEvento : "");
            mailHeaders.put("eConsig.emailEventoCompraCSADestino", enderecosCsaDestino.emailCsaEvento != null ? enderecosCsaDestino.emailCsaEvento : "");

            // Manda o email para a consignatária destinatária
            final MailHelper mailHelper = new MailHelper();
            if (emailDestinatarios.size() > 0) {
                for (final String element : emailDestinatarios) {
                    mailHelper.send(element, null, null, titulo, texto, null, mailHeaders);
                }
            }

            String serEmail = adeDelegate.getValorDadoAutDesconto(adeCodigoNovo, CodedValues.TDA_SDV_EMAIL_SERVIDOR, responsavel);
            if (TextHelper.isNull(serEmail)) {
                // Se não tiver e-mail específico para esta compra, veja se no cadastro há um e-mail
                serEmail = (String) adeComprada.getAttribute(Columns.SER_EMAIL);
            }

            if (!TextHelper.isNull(serEmail)) {
                if (tipo == TIPO_COMPRA_CONTRATO) {
                    // Se é compra de contrato, envia uma cópia da mensagem.
                    mailHelper.send(serEmail, null, null, titulo, texto, null, mailHeaders);
                } else if ((tipo == TIPO_CADASTRO_SALDO_DEVEDOR) && ParamSist.paramEquals(CodedValues.TPC_HABILITA_APROVACAO_SALDO_SERVIDOR_COMPRA, CodedValues.TPC_SIM, responsavel)) {
                    // Se é cadastro de saldo e o sistema tem etapa de aprovação de saldo, envia uma cópia da mensagem para o servidor

                    // Redefine o texto do E-mail, sem informação de depósito do saldo
                    texto = titulo + "<br/>\n<br/>\n" + textoGeral.toString() + textoSaldoDevedor;
                    if (!TextHelper.isNull(observacao)) {
                        texto += "<br/>\n"+ ApplicationResourcesHelper.getMessage("rotulo.email.compra.observacao", responsavel, observacao);
                    }

                    // Verifica se são exigidos o cadastro de anexos no saldo, para envio ao servidor
                    List<String> anexos = null;
                    if (ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_DEVEDOR_COMPRA, CodedValues.TPC_SIM, responsavel) &&
                            (ParamSist.paramEquals(CodedValues.TPC_EMAIL_ANEXO_SALDO_DEVEDOR_SERVIDOR, CodedValues.ANEXO_SALDO_DEVEDOR_SERVIDOR_ENVIA_EMAIL, responsavel) ||
                                    ParamSist.paramEquals(CodedValues.TPC_EMAIL_ANEXO_SALDO_DEVEDOR_SERVIDOR, CodedValues.ANEXO_SALDO_DEVEDOR_SERVIDOR_EMAIL_E_TELA, responsavel))) {

                        final List<String> tarCodigos = new ArrayList<>();
                        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD.getCodigo());
                        final CustomTransferObject cto = new CustomTransferObject();
                        cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
                        cto.setAttribute(Columns.AAD_TAR_CODIGO, tarCodigos);
                        cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);
                        final EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);
                        final List<TransferObject> aadList = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(cto, -1, -1, responsavel);
                        if ((aadList != null) && (aadList.size() > 0)) {
                            texto += "<br/>\n<br/>\n"
                                    + ApplicationResourcesHelper.getMessage("rotulo.email.compra.arquivos.anexos", responsavel)
                                    + "<br/>\n<br/>\n";

                            anexos = new ArrayList<>();
                            for (final TransferObject aad : aadList) {
                                final String aadNome = aad.getAttribute(Columns.AAD_NOME).toString();
                                final String aadDescricao = aad.getAttribute(Columns.AAD_DESCRICAO).toString();
                                final String caminho = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "anexo" + File.separatorChar + DateHelper.format((Date)adeComprada.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + adeCodigo;
                                final File arquivoAnexo = new File(caminho + File.separatorChar + aadNome);
                                if ((arquivoAnexo != null) && arquivoAnexo.exists()) {
                                    anexos.add(arquivoAnexo.getAbsolutePath());
                                    texto += "<b>" + aadDescricao + "</b>: " + aadNome + "<br>\n";
                                }
                            }
                        }
                    }

                    mailHelper.send(serEmail, null, null, titulo, texto, anexos, mailHeaders);
                }
            }

            return "";
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return ex.getMessage();
        }
    }

    /**
     * Envia por e-mail a informação de saldo devedor para o gestor/órgão quando a solicitação
     * for cadastrada para exclusão de servidor
     * @param adeCodigo
     * @param responsavel
     * @return
     */
    public static final String enviarEmailSaldoDevedorExclusaoServidor(SaldoDevedorTransferObject saldoDevedorTO, AcessoSistema responsavel) {
        try {
            // Busca o contrato informado
            final String adeCodigo = saldoDevedorTO.getAdeCodigo();
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);

            if (ade != null) {
                // Obtém os dados da consignação
                final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                final String cseNome = getCseNome(responsavel);

                // Define o titulo do E-mail
                final String titulo = gerarTituloEmail(null, ApplicationResourcesHelper.getMessage("mensagem.email.saldo.devedor.consignacao.informado", responsavel, adeNumero), responsavel);

                // Texto com os dados do contrato
                final StringBuilder textoGeral = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.email.abaixo.seguem.dados.consignacao", responsavel)).append(gerarTextoDetalheContratoParaEmail(ade, cseNome, responsavel)).append("<br/>\n<br/>\n");

                // Busca os valores do saldo devedor cadastrados
                if (saldoDevedorTO != null) {
                    textoGeral.append(gerarTextoDetalheSaldoDevedor(saldoDevedorTO, responsavel)).append("<br/>\n<br/>\n");
                }

                // Define o texto do E-mail
                final String texto = titulo + "<br/>\n<br/>\n" + textoGeral.toString();

                final List<SolicitacaoAutorizacao> soaList = new SaldoDevedorDelegate().lstSolicitacaoSaldoExclusaoPendente(adeCodigo, responsavel);
                if ((soaList != null) && !soaList.isEmpty()) {
                    for (final SolicitacaoAutorizacao solicitacaoAutorizacao : soaList) {
                        final String usuCodigo = solicitacaoAutorizacao.getUsuario().getUsuCodigo();
                        final UsuarioTransferObject usuario = new UsuarioDelegate().findUsuario(usuCodigo, responsavel);
                        // Envia as mensagens
                        if (!TextHelper.isNull(usuario.getUsuEmail())) {
                            final MailHelper mailHelper = new MailHelper();
                            mailHelper.send(usuario.getUsuEmail(), null, null, titulo, texto, null);
                        }
                    }
                }
            }
            return "";
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return ex.getMessage();
        }
    }


    /**
     * envia email para entidades relacionadas ao contrato quando papel do usuário responsável estiver configurado tal
     * @param operacao - operação realizada sobre o contrato
     * @param adeCodigo - código da ADE
     * @param observacao - observação a ser incluída no email
     * @param responsavel
     * @throws ZetraException
     */
    public static final void enviarEmailAlteracaoAdePapDestinatarios(OperacaoEConsigEnum operacao, String adeCodigo, String observacao, TransferObject motivoOperacao, AcessoSistema responsavel) throws ZetraException, MessagingException {
        // recupera se o sistema está configurado para enviar email às entidades quando operações sobre consignação são realizadas
        final boolean enviaEmailAlertaAlteracaoAde = ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_ENTIDADES_QNDO_ALTERA_ADE, CodedValues.TPC_SIM, responsavel);
        // recupera se o sistema está configurado para enviar SMS ao servidor quando operações sobre consignação são realizadas
        final boolean enviaSMSAlertaAlteracaoAde = ParamSist.paramEquals(CodedValues.TPC_ENVIA_SMS_SERVIDOR_QNDO_ALTERA_ADE, CodedValues.TPC_SIM, responsavel);

        if (enviaEmailAlertaAlteracaoAde) {
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            final ServidorDelegate serDelegate = new ServidorDelegate();

            // Se não há configuração de envio de e-mail, então retorna
            final String funCodigo = responsavel.getFunCodigo();
            final String papCodigo = responsavel.getPapCodigo();
            final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
            final List<String> lstDests = sistemaController.listarPapeisEnvioEmailOperacoes(funCodigo, papCodigo, responsavel);
            if ((lstDests == null) || lstDests.isEmpty()) {
                return;
            }

            // Busca as informações do contrato comprado e do contrato novo
            final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);
            if (ade == null) {
                return;
            }

            final String csaCodigo = (String) ade.getAttribute(Columns.CSA_CODIGO);
            final String svcCodigo = (String) ade.getAttribute(Columns.SVC_CODIGO);

            final List<String> emailDestinos = new ArrayList<>();
            final List<String> celularDestinos = new ArrayList<>();

            for (final String destinatario: lstDests) {
                switch (destinatario) {
                    case CodedValues.PAP_CONSIGNATARIA:
                        final String demEmail = csaDelegate.getEmailCsaNotificacaoOperacao(funCodigo, papCodigo, csaCodigo, svcCodigo, responsavel);
                        if (!TextHelper.isNull(demEmail)) {
                            emailDestinos.add(demEmail);
                        }
                        break;
                    case CodedValues.PAP_CORRESPONDENTE:
                        if (!TextHelper.isNull(ade.getAttribute(Columns.COR_EMAIL))) {
                            emailDestinos.add(ade.getAttribute(Columns.COR_EMAIL).toString());
                        }
                        break;
                    case CodedValues.PAP_ORGAO:
                        if (!TextHelper.isNull(ade.getAttribute(Columns.ORG_EMAIL))) {
                            emailDestinos.add(ade.getAttribute(Columns.ORG_EMAIL).toString());
                        }
                        break;
                    case CodedValues.PAP_SERVIDOR:
                    	final String serEmail = serDelegate.getEmailSerNotificacaoOperacao(funCodigo, papCodigo, ade.getAttribute(Columns.SER_CODIGO).toString(), responsavel);
                        if (!TextHelper.isNull(serEmail)) {
                            emailDestinos.add(serEmail);
                        }
                        if (!TextHelper.isNull(ade.getAttribute(Columns.SER_CELULAR))) {
                            celularDestinos.add(ade.getAttribute(Columns.SER_CELULAR).toString());
                        }
                        break;
                    case CodedValues.PAP_CONSIGNANTE:
                        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                        final String deeEmail = cseDelegate.getEmailCseNotificacaoOperacao(funCodigo, papCodigo, CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                        if (!TextHelper.isNull(deeEmail)) {
                            emailDestinos.add(deeEmail);
                        }
                        break;
                    case CodedValues.PAP_SUPORTE:
                        emailDestinos.add((String) ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel));
                        break;
                }
            }

            // Se não tem e-mail nem celular não faz nada
            if (emailDestinos.isEmpty() && (!enviaSMSAlertaAlteracaoAde || celularDestinos.isEmpty())) {
                return;
            }

            // Dados necessários para envio do e-mail
            final Long adeNumero = (Long) ade.getAttribute(Columns.ADE_NUMERO);
            final String cseNome = getCseNome(responsavel);
            String csaNome = (String) ade.getAttribute(Columns.CSA_NOME_ABREV);
            if (TextHelper.isNull(csaNome)) {
                csaNome = (String) ade.getAttribute(Columns.CSA_NOME);
            }
            if (csaNome.length() > 50) {
                csaNome = csaNome.substring(0, 47) + "...";
            }

            if (!emailDestinos.isEmpty()) {
                // Define o titulo do E-mail
                final String titulo = gerarTituloEmail(null, ApplicationResourcesHelper.getMessage("mensagem.email.operacao.realizada.consignacao", responsavel, operacao.getOperacao(), adeNumero.toString()), responsavel);

                // Se for usuário de órgão, inclui o nome do órgão na mensagem
                String usuario = responsavel.getUsuLogin();
                if (responsavel.isOrg()) {
                    usuario += ApplicationResourcesHelper.getMessage("mensagem.email.pertencente.a", responsavel, responsavel.getNomeEntidade());
                }

                // Texto Comum para todas as mensagems
                final StringBuilder textoGeral = new StringBuilder();

                textoGeral.append(ApplicationResourcesHelper.getMessage("mensagem.email.cse.consignacao.cabecalho", responsavel, usuario, adeNumero.toString(), operacao.getOperacao(), DateHelper.toDateTimeString(DateHelper.getSystemDatetime())));
                textoGeral.append(gerarTextoDetalheContratoParaEmail(ade, cseNome, responsavel));
                textoGeral.append("<br/>\n<br/>\n");

                if (motivoOperacao != null) {
                    final String tmoCodigo = (String) motivoOperacao.getAttribute(Columns.TMO_CODIGO);
                    final String ocaObs = (String) motivoOperacao.getAttribute(Columns.OCA_OBS);
                    if (!TextHelper.isNull(tmoCodigo)) {
                        final TipoMotivoOperacaoDelegate tmoDelegate = new TipoMotivoOperacaoDelegate();
                        final TipoMotivoOperacaoTransferObject tmo = tmoDelegate.findMotivoOperacao(tmoCodigo, responsavel);
                        textoGeral.append(ApplicationResourcesHelper.getMessage("rotulo.email.motivo", responsavel, tmo.getTmoDescricao())).append(!TextHelper.isNull(ocaObs) ? " - " + ocaObs : "").append("<br/>\n");
                    }
                }
                if (!TextHelper.isNull(observacao)) {
                    textoGeral.append(ApplicationResourcesHelper.getMessage("rotulo.email.observacao", responsavel, observacao));
                }

                // Envia os emails.
                final MailHelper mailHelper = new MailHelper();
                for (final String email: emailDestinos) {
                    if (!TextHelper.isNull(email)) {
                        mailHelper.send(email, null, null, titulo, textoGeral.toString(), null, null);
                    }
                }
            }

            if (enviaSMSAlertaAlteracaoAde && !celularDestinos.isEmpty()) {
                try {
                    // Credenciais para envio do SMS
                    final String accountSid = ParamSist.getInstance().getParam(CodedValues.TPC_SID_CONTA_SMS, responsavel).toString();
                    final String authToken = ParamSist.getInstance().getParam(CodedValues.TPC_TOKEN_AUTENTICACAO_SMS, responsavel).toString();
                    final String fromNumber = ParamSist.getInstance().getParam(CodedValues.TPC_NUMERO_REMETENTE_SMS, responsavel).toString();

                    if (TextHelper.isNull(accountSid) || TextHelper.isNull(authToken) || TextHelper.isNull(fromNumber)) {
                        LOG.warn("Necessário habilitar os parâmetros de sistema " + CodedValues.TPC_SID_CONTA_SMS + ", " + CodedValues.TPC_TOKEN_AUTENTICACAO_SMS + ", " + CodedValues.TPC_NUMERO_REMETENTE_SMS + " para envio de SMS.");

                    } else {
                        for (String celularDestinatario : celularDestinos) {
                            celularDestinatario = LocaleHelper.formataCelular(celularDestinatario);
                            if (!TextHelper.isNull(celularDestinatario)) {
                                final String corpo = ApplicationResourcesHelper.getMessage("mensagem.sms.operacao.realizada.consignacao", responsavel, operacao.getOperacao(), adeNumero.toString(), csaNome);
                                new SMSHelper(accountSid, authToken, fromNumber).send(celularDestinatario, TextHelper.removeAccent(corpo));
                            }
                        }
                    }
                } catch (final ZetraException e) {
                    throw new ViewHelperException("mensagem.erro.sms.enviar", responsavel, e);
                }
            }
        }
    }

    public static final void enviarEmailAlteracaoPerfil(String perCodigo, String tipoEntidade, String codigoEntidade, AcessoSistema responsavel) throws ZetraException {
        // Recupera se o sistema está configurado para enviar email às entidades quando operações sobre perfis de usuário são realizadas
        if (ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_ENTIDADES_QNDO_ALTERA_PERFIL, CodedValues.TPC_SIM, responsavel)) {
            final String funCodigo = responsavel.getFunCodigo();
            final String papCodigoOperador = responsavel.getPapCodigo();
            final String papCodigoDestinatario = UsuarioHelper.getPapCodigo(tipoEntidade);

            // Verifica se o papel ao qual o perfil pertence está entre a lista de destinatarios para o papel do operador
            final SistemaController sistemaController = ApplicationContextProvider.getApplicationContext().getBean(SistemaController.class);
            final List<String> lstDests = sistemaController.listarPapeisEnvioEmailOperacoes(funCodigo, papCodigoOperador, responsavel);
            if ((lstDests == null) || lstDests.isEmpty() || !lstDests.contains(papCodigoDestinatario)) {
                return;
            }

            final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();

            final List<String> emailDestinos = new ArrayList<>();
            switch (papCodigoDestinatario) {
                case CodedValues.PAP_CONSIGNATARIA:
                    final String demEmail = csaDelegate.getEmailCsaNotificacaoOperacao(funCodigo, papCodigoOperador, codigoEntidade, null, responsavel);
                    if (!TextHelper.isNull(demEmail)) {
                        emailDestinos.add(demEmail);
                    }
                    break;
                case CodedValues.PAP_CORRESPONDENTE:
                    final String corEmail = csaDelegate.findCorrespondente(codigoEntidade, responsavel).getCorEmail();
                    if (!TextHelper.isNull(corEmail)) {
                        emailDestinos.add(corEmail);
                    }
                    break;
                case CodedValues.PAP_ORGAO:
                    final String orgEmail = cseDelegate.findOrgao(codigoEntidade, responsavel).getOrgEmail();
                    if (!TextHelper.isNull(orgEmail)) {
                        emailDestinos.add(orgEmail.toString());
                    }
                    break;
                case CodedValues.PAP_CONSIGNANTE:
                    final String cseEmail = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel).getCseEmail();
                    if (!TextHelper.isNull(cseEmail)) {
                        emailDestinos.add(cseEmail);
                    }
                    break;
                case CodedValues.PAP_SUPORTE:
                    final String supEmail = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel);
                    if (!TextHelper.isNull(supEmail)) {
                        emailDestinos.add(supEmail);
                    }
                    break;
            }

            // Se não tem e-mail para envio não faz nada
            if (emailDestinos.isEmpty()) {
                return;
            }

            final UsuarioDelegate usuDelegate = new UsuarioDelegate();
            final String perDescricao = usuDelegate.findPerfil(perCodigo, responsavel).getPerDescricao();

            for (final String emailDestino : emailDestinos) {
                final EnviarEmailAlteracaoPerfilUsuarioCommand command = new EnviarEmailAlteracaoPerfilUsuarioCommand(emailDestino, perDescricao, responsavel);
                command.execute();
            }
        }
    }

    /**
     * Envia e-mail para a consignatária quando um servidor solicita o saldo
     * devedor de uma de suas consignações.
     * @param adeCodigo
     * @param isQuitacao
     * @param responsavel
     * @return
     */
    public static final String enviarEmailSolicitacaoSaldo(String adeCodigo, boolean isQuitacao, Date dataValidade, AcessoSistema responsavel) {
        try {
            // Busca o contrato
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);

            if (ade != null) {
                // Determina os destinatários da mensagem, de acordo com os parâmetros de SVC/CSA.
                final List<String> tpsCodigos = new ArrayList<>();
                tpsCodigos.add(CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR);
                tpsCodigos.add(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA);

                String destinatariosEmail = CodedValues.RECEBE_EMAIL_APENAS_CONSIGNATARIA;
                final String emailCorrespondente = (String) ade.getAttribute(Columns.COR_EMAIL);
                String emailConsignataria = null;

                // Busca os parâmetros de serviço da consignatária.
                final List<TransferObject> parametros = new ParametroDelegate().selectParamSvcCsa((String) ade.getAttribute(Columns.SVC_CODIGO), (String) ade.getAttribute(Columns.CSA_CODIGO), tpsCodigos, false, responsavel);
                if (parametros != null) {
                    for (final TransferObject paramSvcCsa : parametros) {
                        final String codigoParametro = (String) paramSvcCsa.getAttribute(Columns.TPS_CODIGO);
                        final String valorParametro = (String) paramSvcCsa.getAttribute(Columns.PSC_VLR);
                        if (CodedValues.TPS_EMAIL_INF_SOLICITACAO_SALDO_DEVEDOR.equals(codigoParametro)) {
                            emailConsignataria = valorParametro;
                        } else if (CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA.equals(codigoParametro)) {
                            destinatariosEmail = valorParametro;
                        }
                    }
                }

                // Monta a lista de destinatários da mensagem.
                final List<String> destinatarios = montarListaEnderecosDestinatarios(destinatariosEmail, emailConsignataria, emailCorrespondente);

                // Manda o email para os destinatários.
                if (destinatarios.size() > 0) {
                    // Dados da consignação que teve o saldo devedor solicitado
                    final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                    final String cseNome = getCseNome(responsavel);

                    // Telefone pra contato - se existir.
                    final String telefone = adeDelegate.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_TEL_SERVIDOR, responsavel);

                    // Texto Comum para todas as mensagems
                    final StringBuilder textoGeral = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.email.abaixo.seguem.dados.consignacao", responsavel)).append(gerarTextoDetalheContratoParaEmail(ade, cseNome, responsavel)).append(TextHelper.isNull(telefone) ? "" : "<br/>\n"+ ApplicationResourcesHelper.getMessage("rotulo.email.telefone", responsavel, telefone));
                            if(dataValidade != null) {
                                textoGeral.append("<br/>\n").append(ApplicationResourcesHelper.getMessage("mensagem.email.data.validade.solicitacao.saldo.devedor", responsavel, DateHelper.format(dataValidade, LocaleHelper.getDateTimePattern())));
                            }
                            textoGeral.append("<br/>\n<br/>\n");

                    // Define o titulo do E-mail
                    final String titulo = gerarTituloEmail(null, isQuitacao ? ApplicationResourcesHelper.getMessage("mensagem.email.saldo.devedor.consignacao.solicitado.liquidacao", responsavel, adeNumero) : ApplicationResourcesHelper.getMessage("mensagem.email.saldo.devedor.consignacao.solicitado", responsavel, adeNumero), responsavel);

                    // Define o texto do E-mail
                    final String texto = titulo + "<br/>\n<br/>\n" + textoGeral.toString();

                    // Envia as mensagens.
                    final MailHelper mailHelper = new MailHelper();
                    for (final String element : destinatarios) {
                        mailHelper.send(element, null, null, titulo, texto, null);
                    }
                }
            }

            return "";
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return ex.getMessage();
        }
    }

    /**
     * Envia por e-mail os anexos do saldo devedor para o servidor quando a consignatária
     * cadastra um saldo devedor originado de uma solicitação do prórprio servidor,
     * e o parâmetro de sistema determina que o e-mail seja enviado.
     * @param saldoDevedorTO
     * @param responsavel
     * @return
     */
    public static final String enviarEmailAnexosSaldoSolicServidor(SaldoDevedorTransferObject saldoDevedorTO, AcessoSistema responsavel) {
        try {
            // Se exige anexos no cadastro de saldo para o servidor e o parâmetro de sistema
            // diz que os anexos do saldo devedor são entregues por e-mail ou e-mail/tela,
            // então verifica se o servidor possui e-mail para envio do saldo devedor.
            if ((ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel) ||
                    ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) &&
                    (ParamSist.paramEquals(CodedValues.TPC_EMAIL_ANEXO_SALDO_DEVEDOR_SERVIDOR, CodedValues.ANEXO_SALDO_DEVEDOR_SERVIDOR_ENVIA_EMAIL, responsavel) ||
                            ParamSist.paramEquals(CodedValues.TPC_EMAIL_ANEXO_SALDO_DEVEDOR_SERVIDOR, CodedValues.ANEXO_SALDO_DEVEDOR_SERVIDOR_EMAIL_E_TELA, responsavel))) {

                // Busca o contrato informado
                final String adeCodigo = saldoDevedorTO.getAdeCodigo();
                final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
                final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);
                if (ade != null) {
                    // Verifica se o servidor possui e-mail cadastrado
                    final String serEmail = (String) ade.getAttribute(Columns.SER_EMAIL);
                    if (!TextHelper.isNull(serEmail)) {

                        // Obtém os dados da consignação
                        final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                        final String cseNome = getCseNome(responsavel);

                        // Define o titulo do E-mail
                        final String titulo = gerarTituloEmail(null, ApplicationResourcesHelper.getMessage("mensagem.email.saldo.devedor.consignacao.informado", responsavel, adeNumero), responsavel);

                        // Texto com os dados do contrato
                        final StringBuilder textoGeral = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.email.abaixo.seguem.dados.consignacao", responsavel)).append(gerarTextoDetalheContratoParaEmail(ade, cseNome, responsavel)).append("<br/>\n<br/>\n");

                        // Busca os valores do saldo devedor cadastrados
                        if (saldoDevedorTO != null) {
                            textoGeral.append(gerarTextoDetalheSaldoDevedor(saldoDevedorTO, responsavel)).append("<br/>\n<br/>\n");
                        }

                        // Se o sistema exige anexos no cadastro de saldo, verifica se já foram informados
                        List<String> anexos = null;
                        final List<String> tarCodigos = new ArrayList<>();
                        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_DSD.getCodigo());
                        tarCodigos.add(TipoArquivoEnum.ARQUIVO_ANEXO_AUTORIZACAO_BOLETO.getCodigo());
                        final CustomTransferObject cto = new CustomTransferObject();
                        cto.setAttribute(Columns.AAD_ADE_CODIGO, adeCodigo);
                        cto.setAttribute(Columns.AAD_TAR_CODIGO, tarCodigos);
                        cto.setAttribute(Columns.AAD_ATIVO, CodedValues.STS_ATIVO);
                        final EditarAnexoConsignacaoController editarAnexoConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(EditarAnexoConsignacaoController.class);
                        final List<TransferObject> aadList = editarAnexoConsignacaoController.lstAnexoAutorizacaoDesconto(cto, -1, -1, responsavel);
                        if ((aadList != null) && (aadList.size() > 0)) {
                            textoGeral.append(ApplicationResourcesHelper.getMessage("rotulo.email.compra.arquivos.anexos", responsavel)).append("<br/>\n<br/>\n");

                            anexos = new ArrayList<>();
                            for (final TransferObject aad : aadList) {
                                final String aadNome = aad.getAttribute(Columns.AAD_NOME).toString();
                                final String aadDescricao = aad.getAttribute(Columns.AAD_DESCRICAO).toString();
                                final String caminho = ParamSist.getDiretorioRaizArquivos() + File.separatorChar + "anexo" + File.separatorChar + DateHelper.format((Date)ade.getAttribute(Columns.ADE_DATA), "yyyyMMdd") + File.separatorChar + adeCodigo;
                                final File arquivoAnexo = new File(caminho + File.separatorChar + aadNome);
                                if ((arquivoAnexo != null) && arquivoAnexo.exists()) {
                                    anexos.add(arquivoAnexo.getAbsolutePath());
                                    textoGeral.append("<b>").append(aadDescricao).append("</b>: ").append(aadNome).append("<br>\n");
                                }
                            }
                        }

                        // Define o texto do E-mail
                        final String texto = titulo + "<br/>\n<br/>\n" + textoGeral.toString();

                        // Envia as mensagens.
                        final MailHelper mailHelper = new MailHelper();
                        mailHelper.send(serEmail, null, null, titulo, texto, anexos);
                    }
                }
            }

            return "";
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return ex.getMessage();
        }
    }

    /**
     * Envia e-mail o servidor quando a consignatária
     * cadastra um saldo devedor originado de uma solicitação do prórprio servidor,
     * e o parâmetro de sistema determina que o e-mail seja enviado.
     * @param saldoDevedorTO
     * @param responsavel
     * @return
     */
    public static final String enviarEmailSaldoSolicServidor(SaldoDevedorTransferObject saldoDevedorTO, AcessoSistema responsavel) {
        try {
            boolean emailAnexo = false;

            //faz a mesma verificação do enviarEmailAnexosSaldoSolicServidor() para evitar duplicação de emails
            if ((ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_DSD_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel) ||
                    ParamSist.paramEquals(CodedValues.TPC_EXIGE_ANEXO_BOLETO_SALDO_SERVIDOR, CodedValues.TPC_SIM, responsavel)) &&
                    (ParamSist.paramEquals(CodedValues.TPC_EMAIL_ANEXO_SALDO_DEVEDOR_SERVIDOR, CodedValues.ANEXO_SALDO_DEVEDOR_SERVIDOR_ENVIA_EMAIL, responsavel) ||
                            ParamSist.paramEquals(CodedValues.TPC_EMAIL_ANEXO_SALDO_DEVEDOR_SERVIDOR, CodedValues.ANEXO_SALDO_DEVEDOR_SERVIDOR_EMAIL_E_TELA, responsavel))) {
                emailAnexo = true;
            }

            // Busca o contrato informado
            final String adeCodigo = saldoDevedorTO.getAdeCodigo();
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);
            if (ade != null) {
                // Verifica se o servidor possui e-mail cadastrado
                final String serEmail = (String) ade.getAttribute(Columns.SER_EMAIL);
                if (!TextHelper.isNull(serEmail) && !emailAnexo) {

                    // Obtém os dados da consignação
                    final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                    final String cseNome = getCseNome(responsavel);

                    // Define o titulo do E-mail
                    final String titulo = gerarTituloEmail(null, ApplicationResourcesHelper.getMessage("mensagem.email.saldo.devedor.consignacao.informado", responsavel, adeNumero), responsavel);

                    // Texto com os dados do contrato
                    final StringBuilder textoGeral = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.email.abaixo.seguem.dados.consignacao", responsavel)).append(gerarTextoDetalheContratoParaEmail(ade, cseNome, responsavel)).append("<br/>\n<br/>\n");

                    // Busca os valores do saldo devedor cadastrados
                    if (saldoDevedorTO != null) {
                        textoGeral.append(gerarTextoDetalheSaldoDevedor(saldoDevedorTO, responsavel)).append("<br/>\n<br/>\n");
                    }

                    // Define o texto do E-mail
                    final String texto = titulo + "<br/>\n<br/>\n" + textoGeral.toString();

                    // Envia as mensagens.
                    final MailHelper mailHelper = new MailHelper();
                    mailHelper.send(serEmail, null, null, titulo, texto, null);
                }
            }

            return "";
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return ex.getMessage();
        }
    }


    /**
     * Envia e-mail para o servidor quando este for bloqueado ou desbloqueado por serviço
     * @param saldoDevedorTO
     * @param responsavel
     * @return
     */
    public static final void enviarEmailBloqDesbloqServidorServico(String rseCodigo, List<TransferObject> bloqueado, List<TransferObject> desbloqueado, List<TransferObject> alterado, AcessoSistema responsavel) {
        try {
            // se não teve alteração, retorna
            if(bloqueado.isEmpty() && desbloqueado.isEmpty() && alterado.isEmpty()){
                return;
            }

            // Busca o servidor
            final ServidorDelegate serDelegate = new ServidorDelegate();

            final TransferObject servidor = serDelegate.buscaServidor(rseCodigo, responsavel);
            if (servidor != null) {
                // Verifica se o servidor possui e-mail cadastrado
                final String serEmail = (String) servidor.getAttribute(Columns.SER_EMAIL);
                if (!TextHelper.isNull(serEmail)) {

                    // Define o titulo do E-mail
                    final String titulo = gerarTituloEmail(null, ApplicationResourcesHelper.getMessage("mensagem.email.bloqueio.servidor.servico.titulo", responsavel), responsavel);

                    final StringBuilder textoGeral = new StringBuilder();
                    // Texto com os dados do bloqueio
                    if (bloqueado.size()>0){
                        textoGeral.append(ApplicationResourcesHelper.getMessage("mensagem.email.bloqueio.servidor.servico", responsavel));
                        textoGeral.append("<br>\n");

                        final Iterator<TransferObject> it = bloqueado.iterator();

                        while (it.hasNext()) {
                            final TransferObject to = it.next();
                            textoGeral.append(to.getAttribute(Columns.SVC_DESCRICAO));
                            if (it.hasNext()){
                                textoGeral.append(", ");
                            }
                        }
                        textoGeral.append(".<br>\n");
                    }
                    // Texto com os dados do desbloqueio
                    if (desbloqueado.size()>0){
                        textoGeral.append("<br>\n");
                        final Iterator<TransferObject> it = desbloqueado.iterator();
                        final StringBuilder msg = new StringBuilder();
                        while (it.hasNext()) {
                            final TransferObject to = it.next();
                            msg.append(to.getAttribute(Columns.SVC_DESCRICAO));
                            if (it.hasNext()){
                                msg.append(", ");
                            }
                        }

                        textoGeral.append(ApplicationResourcesHelper.getMessage("mensagem.email.desbloqueio.servidor.servico", responsavel, msg.toString(), JspHelper.getNomeSistema(responsavel)));
                        textoGeral.append("<br>\n");
                    }
                    // Texto com os dados dos alterados
                    if (alterado.size()>0){
                        textoGeral.append("<br>\n");
                        final Iterator<TransferObject> it = alterado.iterator();
                        while (it.hasNext()) {
                            final TransferObject to = it.next();
                            textoGeral.append(ApplicationResourcesHelper.getMessage("mensagem.email.alteracao.servidor.servico", responsavel, to.getAttribute(Columns.SVC_DESCRICAO).toString(), to.getAttribute(Columns.PSR_VLR).toString()));
                            if (it.hasNext()){
                                textoGeral.append("<br>\n");
                            }
                        }
                        textoGeral.append("<br>\n");
                    }

                    // Define o texto do E-mail
                    String texto = titulo + "<br>\n<br>\n" + textoGeral.toString();
                    texto = TextHelper.forHtmlContentComTags(texto);

                    // Envia as mensagens.
                    final MailHelper mailHelper = new MailHelper();
                    mailHelper.send(serEmail, null, null, titulo, texto, null);
                }
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail para o servidor quando este for bloqueado ou desbloqueado por natureza de serviço
     * @param saldoDevedorTO
     * @param responsavel
     * @return
     */
    public static final void enviarEmailBloqDesbloqServidorNaturezaServico(String rseCodigo, List<TransferObject> bloqueado, List<TransferObject> desbloqueado, List<TransferObject> alterado, AcessoSistema responsavel) {
        try {
            // se não teve alteração, retorna
            if(bloqueado.isEmpty() && desbloqueado.isEmpty() && alterado.isEmpty()){
                return;
            }

            // Busca o servidor
            final ServidorDelegate serDelegate = new ServidorDelegate();

            final TransferObject servidor = serDelegate.buscaServidor(rseCodigo, responsavel);
            if (servidor != null) {
                // Verifica se o servidor possui e-mail cadastrado
                final String serEmail = (String) servidor.getAttribute(Columns.SER_EMAIL);
                if (!TextHelper.isNull(serEmail)) {

                    // Define o titulo do E-mail
                    final String titulo = gerarTituloEmail(null, ApplicationResourcesHelper.getMessage("mensagem.email.bloqueio.servidor.natureza.servico.titulo", responsavel), responsavel);

                    final StringBuilder textoGeral = new StringBuilder();
                    // Texto com os dados do bloqueio
                    if (bloqueado.size()>0){
                        textoGeral.append(ApplicationResourcesHelper.getMessage("mensagem.email.bloqueio.servidor.natureza.servico", responsavel));
                        textoGeral.append("<br>\n");

                        final Iterator<TransferObject> it = bloqueado.iterator();

                        while (it.hasNext()) {
                            final TransferObject to = it.next();
                            textoGeral.append(to.getAttribute(Columns.NSE_DESCRICAO));
                            if (it.hasNext()){
                                textoGeral.append(", ");
                            }
                        }
                        textoGeral.append(".<br>\n");
                    }
                    // Texto com os dados do desbloqueio
                    if (desbloqueado.size()>0){
                        textoGeral.append("<br>\n");
                        final Iterator<TransferObject> it = desbloqueado.iterator();
                        final StringBuilder msg = new StringBuilder();
                        while (it.hasNext()) {
                            final TransferObject to = it.next();
                            msg.append(to.getAttribute(Columns.NSE_DESCRICAO));
                            if (it.hasNext()){
                                msg.append(", ");
                            }
                        }

                        textoGeral.append(ApplicationResourcesHelper.getMessage("mensagem.email.desbloqueio.servidor.natureza.servico", responsavel, msg.toString(), JspHelper.getNomeSistema(responsavel)));
                        textoGeral.append("<br>\n");
                    }
                    // Texto com os dados dos alterados
                    if (alterado.size()>0){
                        textoGeral.append("<br>\n");
                        final Iterator<TransferObject> it = alterado.iterator();
                        while (it.hasNext()) {
                            final TransferObject to = it.next();
                            textoGeral.append(ApplicationResourcesHelper.getMessage("mensagem.email.alteracao.servidor.natureza.servico", responsavel, to.getAttribute(Columns.NSE_DESCRICAO).toString(), to.getAttribute(Columns.PNR_VLR).toString()));
                            if (it.hasNext()){
                                textoGeral.append("<br>\n");
                            }
                        }
                        textoGeral.append("<br>\n");
                    }

                    // Define o texto do E-mail
                    String texto = titulo + "<br>\n<br>\n" + textoGeral.toString();
                    texto = TextHelper.forHtmlContentComTags(texto);

                    // Envia as mensagens.
                    final MailHelper mailHelper = new MailHelper();
                    mailHelper.send(serEmail, null, null, titulo, texto, null);
                }
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail para o servidor quando este for bloqueado ou desbloqueado por verba
     * @param saldoDevedorTO
     * @param responsavel
     * @return
     */
    public static final void enviarEmailBloqDesbloqServidorVerba(String rseCodigo, List<TransferObject> bloqueado, List<TransferObject> desbloqueado, List<TransferObject> alterado, AcessoSistema responsavel) {
        try {
            // se não teve alteração, retorna
            if(bloqueado.isEmpty() && desbloqueado.isEmpty() && alterado.isEmpty()){
                return;
            }

            // Busca o servidor
            final ServidorDelegate serDelegate = new ServidorDelegate();

            final TransferObject servidor = serDelegate.buscaServidor(rseCodigo, responsavel);
            if (servidor != null) {
                // Verifica se o servidor possui e-mail cadastrado
                final String serEmail = (String) servidor.getAttribute(Columns.SER_EMAIL);
                if (!TextHelper.isNull(serEmail)) {

                    // Define o titulo do E-mail
                    final String titulo = gerarTituloEmail(null, ApplicationResourcesHelper.getMessage("mensagem.email.bloqueio.servidor.codigo.verba.titulo", responsavel), responsavel);

                    final StringBuilder textoGeral = new StringBuilder();
                    // Texto com os dados do bloqueio
                    if (bloqueado.size()>0){
                        textoGeral.append(ApplicationResourcesHelper.getMessage("mensagem.email.bloqueio.servidor.codigo.verba", responsavel));
                        textoGeral.append("<br>\n");

                        final Iterator<TransferObject> it = bloqueado.iterator();

                        while (it.hasNext()) {
                            final TransferObject to = it.next();
                            textoGeral.append(to.getAttribute(Columns.CNV_COD_VERBA));
                            if (it.hasNext()){
                                textoGeral.append(", ");
                            }
                        }
                        textoGeral.append(".<br>\n");
                    }
                    // Texto com os dados do desbloqueio
                    if (desbloqueado.size()>0){
                        textoGeral.append("<br>\n");
                        final Iterator<TransferObject> it = desbloqueado.iterator();
                        final StringBuilder msg = new StringBuilder();
                        while (it.hasNext()) {
                            final TransferObject to = it.next();
                            msg.append(to.getAttribute(Columns.CNV_COD_VERBA));
                            if (it.hasNext()){
                                msg.append(", ");
                            }
                        }

                        textoGeral.append(ApplicationResourcesHelper.getMessage("mensagem.email.desbloqueio.servidor.codigo.verba", responsavel, msg.toString(), JspHelper.getNomeSistema(responsavel)));
                        textoGeral.append("<br>\n");
                    }
                    // Texto com os dados dos alterados
                    if (alterado.size()>0){
                        textoGeral.append("<br>\n");
                        final Iterator<TransferObject> it = alterado.iterator();
                        while (it.hasNext()) {
                            final TransferObject to = it.next();
                            textoGeral.append(ApplicationResourcesHelper.getMessage("mensagem.email.alteracao.servidor.codigo.verba",
                                    responsavel, to.getAttribute(Columns.CNV_COD_VERBA).toString(),
                                    to.getAttribute(Columns.PCR_VLR).toString()));
                            if (it.hasNext()){
                                textoGeral.append("<br>\n");
                            }
                        }
                        textoGeral.append("<br>\n");
                    }

                    // Define o texto do E-mail
                    String texto = titulo + "<br>\n<br>\n" + textoGeral.toString();
                    texto = TextHelper.forHtmlContentComTags(texto);

                    // Envia as mensagens.
                    final MailHelper mailHelper = new MailHelper();
                    mailHelper.send(serEmail, null, null, titulo, texto, null);
                }
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail para o servidor quando este for bloqueado ou desbloqueado para fazer reserva em determinada consignatária
     * @param saldoDevedorTO
     * @param responsavel
     * @return
     */
    public static final void enviarEmailBloqDesbloqServidorCsa(String rseCodigo, List<TransferObject> bloqueado, List<TransferObject> desbloqueado, List<TransferObject> alterado, AcessoSistema responsavel) {
        try {
            // se não teve alteração, retorna
            if(bloqueado.isEmpty() && desbloqueado.isEmpty() && alterado.isEmpty()){
                return;
            }

            final EnviarEmailBloqDesbloqServidorCsaCommand command = new EnviarEmailBloqDesbloqServidorCsaCommand(rseCodigo, bloqueado, desbloqueado, alterado, responsavel);
            command.execute();

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia email para o servidor, caso o módulo de recuperação de senha do servidor esteja habilitado e o email do servidor tenha sido alterado.
     * @param destinatario
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailCadastroSenhaSer(String destinatario, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(destinatario)) {
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        final String nomeSistema = JspHelper.getNomeSistema(responsavel);
        final String titulo =  ApplicationResourcesHelper.getMessage("rotulo.email.recuperacao.senha", responsavel, nomeSistema);

        // Recupera o nome do consignante
        final String cseNome = getCseNome(responsavel);
        //Uso o parâmetro "TPC_LINK_ACESSO_SISTEMA" para recuperar o link de acesso ao sistema porque este método é chamado no
        //SessionBean "ServidorControllerBean" que não tem acesso ao "Request".
        String url = !TextHelper.isNull(ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel)) ? (String) ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel) : "";
        url += (url.endsWith("/") ? "" : "/") + "v3/recuperarSenhaServidor?acao=iniciarServidor";
        url = "<a href='" + url +"' >" + url + "</a>";

        final String mensagemCorpo = ApplicationResourcesHelper.getMessage("mensagem.cadastro.senha.email", responsavel, url);

        final StringBuilder corpo = new StringBuilder("<b>").append(nomeSistema).append(" - ").append(cseNome).append("</b><br/>\n<br/>\n");
        corpo.append(mensagemCorpo);
        corpo.append("<br/>\n<br/>\n");

        // Envia o email
        try {
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(destinatario, null, null, titulo, corpo.toString(), null);
        } catch (final MessagingException e) {
            throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, e);

        }
    }

    /**
     * Envia por e-mail uma mensagem cadastrada no sistema.
     * @param mensagem Bean da mensagem a ser enviada.
     * @param destinatarios Endereços de e-mail dos destinatários.
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailMensagem(Mensagem mensagem, List<String> destinatarios, AcessoSistema responsavel) throws ViewHelperException {
        if (mensagem == null) {
            throw new ViewHelperException("mensagem.erro.email.mensagem", responsavel);
        }

        if ((destinatarios == null) || (destinatarios.size() == 0) || TextHelper.isNull(destinatarios.get(0))) {
            throw new ViewHelperException("mensagem.erro.email.nenhuma.csa.cadastrada", responsavel);
        }

        if (TextHelper.isNull(mensagem.getMenTexto())) {
            throw new ViewHelperException("mensagem.informe.email.mensagem", responsavel);
        }

        final String nomeSistema = JspHelper.getNomeSistema(responsavel);
        final String titulo = nomeSistema + (!TextHelper.isNull(mensagem.getMenTitulo()) ? ": " + mensagem.getMenTitulo() : "");

        // Recupera o nome do consignante
        final String cseNome = getCseNome(responsavel);

        String corpo = "<b>" + nomeSistema + " - " + cseNome + "<b><br/>\n<br/>\n";
        try {
            if("N".equals(mensagem.getMenHtml())) {
                corpo += new Markdown4jProcessorExtended().process(TextHelper.forHtmlContent(mensagem.getMenTexto())).toString();
            } else {
                corpo += mensagem.getMenTexto();
            }
        } catch (final IOException e) {
            throw new ViewHelperException("mensagem.erro.interpretar.texto.email", responsavel, e);
        }


        // Envia o email
        try {
            final MailHelper mailHelper = new MailHelper();
            if (destinatarios.size() == 1) {
                mailHelper.send(destinatarios.get(0), null, null, titulo, corpo, null);
            } else {
                // Se há mais de um destinatário, envia para todos via cópia carbono.
                mailHelper.send(null, null, TextHelper.join(destinatarios, ","), titulo, corpo, null);
            }
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.indisponivel", responsavel, e);
        }
    }

    /**
     * Envia confirmação de leitura de mensagem para o consignante.
     * @param leituraMensagem
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailConfLeituraMensagem(LeituraMensagemUsuario leituraMensagem, AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (leituraMensagem == null) {
                throw new ViewHelperException("mensagem.erro.email.enviar", responsavel);
            }

            // Recupera o nome do consignante
            final String cseNome = getCseNome(responsavel);
            final String cseEmail = getCseEmail(responsavel);

            if (TextHelper.isNull(cseEmail)) {
                return;
            }

            final String[] emails = cseEmail.replace(" ", "").split(",|;");
            final List<String> destinatarios = Arrays.asList(emails);

            final MensagemController mensagemController = ApplicationContextProvider.getApplicationContext().getBean(MensagemController.class);
            MensagemTO msgTO = new MensagemTO(leituraMensagem.getMenCodigo());
            msgTO = mensagemController.findMensagem(msgTO, responsavel);

            final UsuarioDelegate usuDelegate = new UsuarioDelegate();
            final TransferObject usuario = usuDelegate.obtemUsuarioTipo(leituraMensagem.getUsuCodigo(), null, responsavel);

            final String entidade = usuario.getAttribute("ENTIDADE").toString();
            final String usuLogin = usuario.getAttribute(Columns.USU_LOGIN).toString();
            final String lmuData = DateHelper.format(leituraMensagem.getLmuData(), LocaleHelper.getDateTimePattern());

            final String nomeSistema = JspHelper.getNomeSistema(responsavel);
            final String titulo = nomeSistema + (!TextHelper.isNull(msgTO.getMenTitulo()) ? ": " + msgTO.getMenTitulo() : "");

            // incluindo o nome da entidade (seja CSA/COR/ORG), do usuário, login, data e hora da confirmação.

            String corpo = "<b>" + nomeSistema + " - " + cseNome + "</b><br/>\n<br/>\n";
            corpo += ApplicationResourcesHelper.getMessage("rotulo.usuario.entidade", responsavel) + ": " + entidade + "<br/>\n";
            corpo += ApplicationResourcesHelper.getMessage("rotulo.usuario.login", responsavel) + ": " + usuLogin + "<br/>\n";
            corpo += ApplicationResourcesHelper.getMessage("rotulo.mensagem.data.confirmacao.leitura", responsavel) + ": " + lmuData;
            corpo += "<br/>\n";

            // Envia o email
            final MailHelper mailHelper = new MailHelper();
            if (destinatarios.size() == 1) {
                mailHelper.send(destinatarios.get(0), null, null, titulo, corpo, null);
            } else {
                // Se há mais de um destinatário, envia para todos via cópia carbono.
                mailHelper.send(null, null, TextHelper.join(destinatarios, ","), titulo, corpo, null);
            }
        } catch (MessagingException | MensagemControllerException | UsuarioControllerException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * Envia e-mail para o servidor informando sobre alteração de senha.
     * @param destinatario : E-mail do destinatário
     * @param matricula : Matrícula do servidor
     * @param novaSenha : Nova senha do servidor
     * @param reiniciacao : Informa se foi uma reiniciação ou alteração de senha
     * @param senhaAutorizacaoServidor : Indica que é senha de autorização (senha 2)
     * @param responsavel : Responsável
     * @throws ViewHelperException
     */
    public static final void enviarEmailAlteracaoSenhaServidor(String destinatario, String matricula, String novaSenha, boolean reiniciacao, boolean senhaAutorizacaoServidor, boolean senhaApp, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(destinatario)) {
            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
        }

        final String nomeSistema = JspHelper.getNomeSistema(responsavel);
        final String cseNome = getCseNome(responsavel);
        String titulo = nomeSistema + " - " + cseNome + ": ";
        final StringBuilder corpo = new StringBuilder();

        if (!senhaAutorizacaoServidor) {
            titulo += reiniciacao ? ApplicationResourcesHelper.getMessage("rotulo.email.reinicializacao.senha", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.email.alteracao.senha", responsavel);

            // Determina quais dados do servidor devem ir no corpo do e-mail.
            String paramDadosServidorEmail = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DADOS_SERVIDOR_EMAIL_ALTERACAO_SENHA, responsavel);
            if (TextHelper.isNull(paramDadosServidorEmail)) {
                paramDadosServidorEmail = CodedValues.DADOS_SERVIDOR_EMAIL_MATRICULA + CodedValues.DADOS_SERVIDOR_EMAIL_SEPARADOR + CodedValues.DADOS_SERVIDOR_EMAIL_SENHA;
            }
            final List<String> dadosServidorEmail = Arrays.asList(TextHelper.dropBlankSpace(paramDadosServidorEmail).toUpperCase().split(CodedValues.DADOS_SERVIDOR_EMAIL_SEPARADOR));

            if (reiniciacao) {
                corpo.append(ApplicationResourcesHelper.getMessage("mensagem.email.senha.servidor.reiniciada", responsavel));
            } else if (senhaApp) {
                corpo.append(ApplicationResourcesHelper.getMessage("mensagem.email.senha.app.servidor.alterada", responsavel));
            } else {
                corpo.append(ApplicationResourcesHelper.getMessage("mensagem.email.senha.servidor.alterada", responsavel));
            }

            corpo.append("<br/>\n<br/>");

            if (dadosServidorEmail.contains(CodedValues.DADOS_SERVIDOR_EMAIL_MATRICULA)) {
                corpo.append(ApplicationResourcesHelper.getMessage("rotulo.email.matricula.servidor", responsavel, matricula));
                corpo.append("<br/>\n");
            }

            //DESENV-13471: Se alteração de senha vinda de requisição de mobile, nunca enviar senha no corpo do e-mail.
            if (!senhaApp && dadosServidorEmail.contains(CodedValues.DADOS_SERVIDOR_EMAIL_SENHA)) {
                corpo.append(ApplicationResourcesHelper.getMessage("rotulo.email.nova.senha", responsavel, novaSenha));
                corpo.append("<br/>\n");
            }

        } else {
            titulo += ApplicationResourcesHelper.getMessage("rotulo.email.geracao.senha.servidor.autorizacao", responsavel);
            corpo.append(ApplicationResourcesHelper.getMessage("mensagem.senha.servidor.autorizacao.sucesso.corpo.email", responsavel)).append("<br/>\n<br/>").append(ApplicationResourcesHelper.getMessage("rotulo.email.senha.servidor.autorizacao", responsavel, novaSenha)).append("<br/>\n");
        }

        final String texto = titulo + "<br/>\n<br/>\n" + corpo.toString();

        // Envia o e-mail.
        try {
            new MailHelper().send(destinatario, null, null, titulo, texto, null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * Envia link por e-mail para o servidor para recuperação de senha.
     * @param destinatario E-mail do destinatário.
     * @param matricula Matrícula do servidor.
     * @param novaSenha Nova senha do servidor.
     * @param reiniciacao Informa se foi uma reiniciação ou alteração de senha.
     * @param responsavel Responsável.
     * @throws ViewHelperException
     */
    public static final void enviarEmailLinkRecuperarSenha(String destinatario, String matricula, String link, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(destinatario)) {
            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
        }

        final String nomeSistema = JspHelper.getNomeSistema(responsavel);
        final String cseNome = getCseNome(responsavel);
        final String titulo =  ApplicationResourcesHelper.getMessage("rotulo.email.consignante.recuperacao.senha", responsavel, nomeSistema, cseNome);

        // Determina quais dados do servidor devem ir no corpo do e-mail.
        String paramDadosServidorEmail = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DADOS_SERVIDOR_EMAIL_ALTERACAO_SENHA, responsavel);
        if (TextHelper.isNull(paramDadosServidorEmail)) {
            paramDadosServidorEmail = CodedValues.DADOS_SERVIDOR_EMAIL_MATRICULA + CodedValues.DADOS_SERVIDOR_EMAIL_SEPARADOR + CodedValues.DADOS_SERVIDOR_EMAIL_SENHA;
        }
        Arrays.asList(TextHelper.dropBlankSpace(paramDadosServidorEmail).toUpperCase().split(CodedValues.DADOS_SERVIDOR_EMAIL_SEPARADOR));

        link = "<a href='" + link +"' >" + link + "</a>";

        final String corpo = ApplicationResourcesHelper.getMessage("mensagem.recuperacao.senha.email", responsavel, link);

        final String texto = titulo + "<br/>\n<br/>\n" + corpo;

        // Envia o e-mail.
        try {
            new MailHelper().send(destinatario, null, null, titulo, texto, null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

   /**
    * E-mail enviado a um novo usuário do sistema eConsig para definir senha de acesso
    * @param destinatario E-mail do destinatário.
    * @param usuNome - Nome do novo usuário criado
    * @param entNome - Nome da entidade à qual pertence o usuário
    * @param link link a que direciona à página de entrada da nova senha
    * @param responsavel
    * @throws ViewHelperException
    */
    public static final void enviarEmailLinkDefinirSenhaNovoUsuario(String destinatario, String usuLogin, String usuNome, String link, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final EnviarLinkCriarSenhaUsuarioCommand command = new EnviarLinkCriarSenhaUsuarioCommand();
            command.setSerEmail(destinatario);
            command.setLink(link);
            command.setUsuLogin(usuLogin);
            command.setSerNome(usuNome);
            command.setResponsavel(responsavel);
            command.execute();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia link por e-mail para o usuário informando sobre alteração de senha no auto-desbloqueio.
     * @param destinatario E-mail do destinatário.
     * @param matricula Matrícula do servidor, no caso de desbloqueio de servidor.
     * @param link url de recuperação de senha
     * @param responsavel Responsável.
     * @throws ViewHelperException
     */
    public static final void enviarEmailLinkRecuperarSenhaAutoDesbloqueio(String destinatario, String matricula, String link, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(destinatario)) {
            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
        }

        final String nomeSistema = JspHelper.getNomeSistema(responsavel);
        final String cseNome = getCseNome(responsavel);
        final String titulo =  ApplicationResourcesHelper.getMessage("rotulo.email.consignante.auto.desbloqueio", responsavel, nomeSistema, cseNome);

        if (responsavel.isSer()) {
            // Determina quais dados do servidor devem ir no corpo do e-mail.
            String paramDadosServidorEmail = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DADOS_SERVIDOR_EMAIL_ALTERACAO_SENHA, responsavel);
            if (TextHelper.isNull(paramDadosServidorEmail)) {
                paramDadosServidorEmail = CodedValues.DADOS_SERVIDOR_EMAIL_MATRICULA + CodedValues.DADOS_SERVIDOR_EMAIL_SEPARADOR + CodedValues.DADOS_SERVIDOR_EMAIL_SENHA;
            }
            Arrays.asList(TextHelper.dropBlankSpace(paramDadosServidorEmail).toUpperCase().split(CodedValues.DADOS_SERVIDOR_EMAIL_SEPARADOR));
        }

        link = "<a href='" + link +"' >" + link + "</a>";

        final String corpo = ApplicationResourcesHelper.getMessage("mensagem.auto.desbloqueio.email", responsavel, link);

        final String texto = titulo + "<br/>\n<br/>\n" + corpo;

        // Envia o e-mail.
        try {
            new MailHelper().send(destinatario, null, null, titulo, texto, null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * Envia e-mail para o servidor informando o código de autorização para solicitação.
     * @param destinatario E-mail do destinatário.
     * @param matricula Matrícula do servidor.
     * @param adeCodigo Código da autorização desconto.
     * @param codAutorizacao Código de autorização da solicitação.
     * @param responsavel Responsável.
     * @throws ViewHelperException
     */
    public static final void enviarEmailCodigoAutorizacaoServidor(String destinatario, String matricula, String adeCodigo, String codAutorizacao, AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (TextHelper.isNull(destinatario)) {
                throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
            }

            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);

            final String cseNome = getCseNome(responsavel);
            final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();

            final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("rotulo.email.codigo.autorizacao.solicitacao", responsavel, adeNumero), responsavel);

            final StringBuilder corpo = new StringBuilder();
            corpo.append(ApplicationResourcesHelper.getMessage("mensagem.email.seguem.dados.solicitacao.codigo.autorizacao", responsavel) + ": <br/>\n<br/>");
            corpo.append(gerarTextoDetalheContratoParaEmail(ade, cseNome, responsavel));
            corpo.append("<br/>\n<b>" + ApplicationResourcesHelper.getMessage("rotulo.email.codigo.autorizacao", responsavel, codAutorizacao) + "</b>");
            corpo.append("<br/>\n<br/>\n");

            final String texto = titulo + "<br/>\n<br/>\n" + corpo.toString();

            // Envia o e-mail.
            new MailHelper().send(destinatario, null, null, titulo, texto, null);
        } catch (MessagingException | AutorizacaoControllerException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }


    /**
     * Envia e-mail para o servidor informando o código para verificação do e-mail.
     * @param destinatario E-mail do destinatário.
     * @param matricula Matrícula do servidor.
     * @param adeCodigo Código da autorização desconto.
     * @param codAutorizacao Código de autorização da solicitação.
     * @param responsavel Responsável.
     * @throws ViewHelperException
     */
    public static final void enviarEmailCodigoVerificacaoEmailServidor(String destinatario, String codVerificacao, AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (TextHelper.isNull(destinatario)) {
                throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
            }

            final String cseNome = getCseNome(responsavel);
            final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("rotulo.email.codigo.verificacao.email", responsavel), responsavel);

            final StringBuilder corpo = new StringBuilder();
            corpo.append(ApplicationResourcesHelper.getMessage("mensagem.email.codigo.verificacao.alteracao.email", responsavel) + ": <br/>\n<br/>");
            corpo.append("<br/>\n<b>" + ApplicationResourcesHelper.getMessage("rotulo.email.codigo.autorizacao", responsavel, codVerificacao) + "</b>");
            corpo.append("<br/>\n<br/>\n");

            final String texto = titulo + "<br/>\n<br/>\n" + corpo.toString();

            // Envia o e-mail.
            new MailHelper().send(destinatario, null, null, titulo, texto, null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }


    /**
     * Envia e-mail para as consignatárias que são bloqueadas automaticamente pelo módulo avançado
     * de compra ou pela solicitação de saldo devedor, em detrimento a alguma penalidade sofrida
     * pelo não cumprimento de algum prazo estipulado pelo Consignante.
     * @param adesResponsaveisBloqueio Lista de ADEs responsáveis pelo bloqueio.
     * @param motivoBloqueio Motivo de bloqueio.
     * @param responsavel
     */
    public final static void enviarEmailBloqueioConsignatarias(List<TransferObject> adesResponsaveisBloqueio, int motivoBloqueio, AcessoSistema responsavel) throws ViewHelperException {
        if ((adesResponsaveisBloqueio == null) || (adesResponsaveisBloqueio.size() == 0)) {
            return;
        }

        // Relaciona os endereços de email dos destinatários com os respectivos contratos (agrupados por consignatária) responsáveis pelo bloqueio.
        final Map<String, Map<String, List<TransferObject>>> destinatariosContratos = new HashMap<>();

        for (final TransferObject adeResponsavelBloqueio : adesResponsaveisBloqueio) {
            // Determina quais endereços de e-mail devem ser comunicados a respeito do bloqueio provocado pela ADE.
            final List<String> enderecosAviso = EnviaEmailHelper.montarListaEnderecosDestinatarios((String) adeResponsavelBloqueio.getAttribute("DESTINATARIOS_EMAILS"), (String) adeResponsavelBloqueio.getAttribute("EMAIL_AVISO_CSA"), (String) adeResponsavelBloqueio.getAttribute(Columns.COR_EMAIL));

            final String csaCodigo = (String) adeResponsavelBloqueio.getAttribute(Columns.CSA_CODIGO);

            // Se a lista de endereços não for vazia.
            if (enderecosAviso.size() > 0) {
                // Agrupa as ADEs por CSA para o destinatário.
                for (final String enderecoAviso : enderecosAviso) {
                    final Map<String, List<TransferObject>> adesBloqueioDestinatario = destinatariosContratos.containsKey(enderecoAviso) ? destinatariosContratos.get(enderecoAviso) : new HashMap<>();
                    final List<TransferObject> adesDaConsignataria = adesBloqueioDestinatario.containsKey(csaCodigo) ? adesBloqueioDestinatario.get(csaCodigo) : new ArrayList<>();
                    adesDaConsignataria.add(adeResponsavelBloqueio);
                    adesBloqueioDestinatario.put(csaCodigo, adesDaConsignataria);
                    destinatariosContratos.put(enderecoAviso, adesBloqueioDestinatario);
                }
            }
        }

        // Se não há e-mail a ser enviado, termina.
        if (destinatariosContratos.size() < 1) {
            return;
        }

        // Para cada destinatário
        for (final String emailDestinatario : destinatariosContratos.keySet()) {
            final Map<String, List<TransferObject>> consignatariaAdes = destinatariosContratos.get(emailDestinatario);

            // Para cada consignatária
            for (final String csaCodigo : consignatariaAdes.keySet()) {
                final List<TransferObject> ades = consignatariaAdes.get(csaCodigo);

                // O nome da consignatária é igual em todas ADEs. Pega o nome da primeira ADE da lista.
                final String csaNome = (String) ades.get(0).getAttribute(Columns.CSA_NOME);

                EnviaEmailHelper.enviarEmailBloqueioConsignataria(emailDestinatario, motivoBloqueio, csaNome, ades, responsavel);
            }
        }
    }

    private final static void enviarEmailBloqueioConsignataria(String emailDestinatario, int motivoBloqueio, String csaNome, List<TransferObject> adesResponsaveisBloqueio, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(emailDestinatario)) {
            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
        }

        // Recupera o nome do consignante
        final String cseNome = getCseNome(responsavel);

        // Define o título do E-mail
        final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("rotulo.email.bloqueio.consignataria", responsavel), responsavel);

        final StringBuilder corpo = new StringBuilder();
        switch (motivoBloqueio) {
            case CodedValues.BLOQUEIO_INF_SALDO_DEVEDOR_COMPRA:
                corpo.append(ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.bloqueada.nao.informou.saldo.devedor", responsavel, csaNome));
                break;
            case CodedValues.BLOQUEIO_INF_PGT_SALDO_COMPRA:
                corpo.append(ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.bloqueada.nao.informou.pagamento.saldo.devedor", responsavel, csaNome));
                break;
            case CodedValues.BLOQUEIO_LIQUIDACAO_COMPRA:
                corpo.append(ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.bloqueada.nao.liquidou.contrato", responsavel, csaNome));
                break;
            case CodedValues.BLOQUEIO_SOLIC_SALDO_DEVEDOR:
                corpo.append(ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.bloqueada.nao.atendeu.solicitacao", responsavel, csaNome));
                break;
            default:
                break;
        }
        corpo.append("<br/>\n<br/>\n");

        if ((adesResponsaveisBloqueio != null) && (adesResponsaveisBloqueio.size() > 0)) {
            final StringBuilder textoConsignacoes = new StringBuilder("<b>").append(ApplicationResourcesHelper.getMessage("mensagem.email.abaixo.seguem.dados.consignacao.plural", responsavel)).append("</b>");

            for (final TransferObject ade : adesResponsaveisBloqueio) {
                textoConsignacoes.append(gerarTextoDetalheContratoParaEmail(ade, cseNome, responsavel)).append("<br/>\n");
            }

            corpo.append(textoConsignacoes.toString());
        }

        final String texto = titulo + "<br/>\n<br/>\n" + corpo.toString();

        // Envia o e-mail.
        try {
            new MailHelper().send(emailDestinatario, null, null, titulo, texto, null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * Envia e-mail de aviso da alteração da tabela de taxas limite,
     * informando que as demais consignatárias devem alterar sua tabela
     * de taxas para ficarem em conformidade com o sistema.
     * @param emailDestinatario
     * @param entName
     * @param tipoEntidade
     * @param dataFimVig
     * @param novaTabelaLimiteTaxa
     * @param tabelaTaxaAtual
     * @param responsavel
     * @throws ViewHelperException
     */
    public final static void enviarEmailAlteracaoTabelaTaxaLimite(String emailDestinatario, String entName, String tipoEntidade, java.util.Date dataFimVig, List<TransferObject> novaTabelaLimiteTaxa, List<TransferObject> tabelaTaxaAtual, AcessoSistema responsavel) throws ViewHelperException {
        if ((novaTabelaLimiteTaxa != null) && (novaTabelaLimiteTaxa.size() > 0) &&
                !TextHelper.isNull(emailDestinatario)) {

            // Define o título da mensagem
            final String nomeSistema = JspHelper.getNomeSistema(responsavel);
            final String cseNome = getCseNome(responsavel);
            final String titulo = ApplicationResourcesHelper.getMessage("rotulo.email.alteracao.tabela.taxas.limite", responsavel, nomeSistema, cseNome);

            // Monta o corpo da mensagem com a tabela de taxas
            final StringBuilder corpoMensagem = new StringBuilder();
            if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
                corpoMensagem.append("<br>"+ ApplicationResourcesHelper.getMessage("rotulo.email.consignataria", responsavel, entName));
            } else if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade)) {
                corpoMensagem.append("<br>"+ ApplicationResourcesHelper.getMessage("rotulo.email.consignante", responsavel, entName));
            }
            if (dataFimVig != null) {
                // Se foi passada uma data limite, então significa que esta consignatária teve a tabela expirada
                corpoMensagem.append(ApplicationResourcesHelper.getMessage("mensagem.email.tabela.taxas.limite.alterada.atualize.dados", responsavel, DateHelper.toDateTimeString(dataFimVig)));
            } else {
                // Senão, é apenas uma mensagem de aviso sobre a alteração
                corpoMensagem.append(ApplicationResourcesHelper.getMessage("mensagem.email.tabela.taxas.limite.alterada", responsavel));
            }

            corpoMensagem.append("<br><br><b>" + ApplicationResourcesHelper.getMessage("mensagem.email.nova.tabela.taxas", responsavel) + ":</b><br>");
            corpoMensagem.append("<table border=\"1\">");
            corpoMensagem.append("<tr><td><b>" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo", responsavel) + "</b></td><td><b>" + ApplicationResourcesHelper.getMessage("rotulo.email.valor.maximo.permitido", responsavel) + "</b></td></tr>");

            Iterator<TransferObject> itCoeficientes = novaTabelaLimiteTaxa.iterator();
            while (itCoeficientes.hasNext()) {
                final TransferObject valor = itCoeficientes.next();
                final BigDecimal cftVlr = new BigDecimal(valor.getAttribute(Columns.CFT_VLR).toString());
                final String prazo = valor.getAttribute(Columns.PRZ_VLR).toString();
                if (cftVlr.signum() > 0) {
                    corpoMensagem.append("<tr><td>" + prazo + "</td><td>" + NumberHelper.format(cftVlr.doubleValue() - 0.01, NumberHelper.getLang()) + "</td></tr>");
                } else {
                    corpoMensagem.append("<tr><td>" + prazo + "</td><td>" + ApplicationResourcesHelper.getMessage("rotulo.email.ilimitado", responsavel) + "</td></tr>");
                }
            }
            corpoMensagem.append("</table>");

            if (tabelaTaxaAtual != null) {
                corpoMensagem.append("<br><br><b>" + ApplicationResourcesHelper.getMessage("mensagem.email.prazos.acima.limite.permitido.sao", responsavel) + ":</b><br>");
                corpoMensagem.append("<table border=\"1\">");
                corpoMensagem.append("<tr><td><b>" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo", responsavel) + "</b></td><td><b>" + ApplicationResourcesHelper.getMessage("rotulo.email.valor.atual", responsavel) + "</b></td></tr>");

                itCoeficientes = tabelaTaxaAtual.iterator();
                while (itCoeficientes.hasNext()) {
                    final TransferObject valor = itCoeficientes.next();
                    final BigDecimal cftVlr = new BigDecimal(valor.getAttribute(Columns.CFT_VLR).toString());
                    final String prazo = valor.getAttribute(Columns.PRZ_VLR).toString();
                    corpoMensagem.append("<tr><td>" + prazo + "</td><td>" + NumberHelper.format(cftVlr.doubleValue(), NumberHelper.getLang()) + "</td></tr>");
                }
                corpoMensagem.append("</table>");
            }

            // Envia o e-mail.
            try {
                new MailHelper().send(emailDestinatario, null, null, titulo, corpoMensagem.toString(), null);
            } catch (final MessagingException e) {
                throw new ViewHelperException ("mensagem.erro.email.alteracao.tabela.taxas.limite", responsavel, e);
            }
        }
    }

    /**
     * Envia email com o resultado da validação no ambiente do eConsig das regras cadastradas.
     * @param destinatario Destinatpario do email.
     * @param regrasInvalidas Lista de regras que nao foram cumpridas.
     * @param bloqueio Flag que indica se o ambiente foi bloqueado.
     * @param responsavel Responsável pela operação.
     * @throws ViewHelperException Exceção padrão da classe.
     */
    public static void enviarEmailValidacaoAmbiente(List<String> regrasInvalidas, boolean bloqueio, AcessoSistema responsavel) throws ViewHelperException  {
        final String nomeSistema = JspHelper.getNomeSistema(responsavel);
        final String cseNome = getCseNome(responsavel);

        final String titulo = ApplicationResourcesHelper.getMessage("rotulo.email.rotina.automatica.verificacao.ambiente.execucao", responsavel, nomeSistema, cseNome);
        final StringBuilder corpo = new StringBuilder(ApplicationResourcesHelper.getMessage("mensagem.email.ocorreram.erros.regras.ambiente", responsavel));
        for (final String regra : regrasInvalidas) {
            corpo.append("- <b>" + regra + "</b><br/>\n");
        }
        if (bloqueio) {
            corpo.append("<br/>\n<br/>\n "+ ApplicationResourcesHelper.getMessage("mensagem.email.sistema.bloqueado.para.ajustes", responsavel));
        }
        final String texto = titulo + "<br/>\n<br/>\n" + corpo;

        // Envia o e-mail
        try {
            new MailHelper().send(DESTINATARIO_BLOQ_SISTEMA, null, null, titulo, texto, null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * envia e-mail de alerta aos destinatários de uma nova comunicação gerada.
     * @param usuCodigo código do usuário rementente
     * @param endDestinatario - e-mail de destino
     * @param cmnCodigo - código da comunicação para a qual o e-mail será criado
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailComunicacao(String usuCodigo, String endDestinatario, TransferObject comunicacao, AcessoSistema responsavel) throws ViewHelperException {
        String nomeRemetente = "";
        TransferObject dadosRemetente = null;
        UsuarioTransferObject usuEntity = null;
        final StringBuilder emailTexto = new StringBuilder();


        try {
            final UsuarioDelegate usuDelegate = new UsuarioDelegate();
            Throwable findException = null;

            try {
                dadosRemetente = usuDelegate.findUsuarioSer(usuCodigo, responsavel);
            } catch (final Exception ex) {
                findException = ex;
            }

            if (dadosRemetente != null) {
                nomeRemetente = (String) dadosRemetente.getAttribute(Columns.USU_NOME);
                emailTexto.append(ApplicationResourcesHelper.getMessage("rotulo.email.o.usuario.servidor", responsavel, nomeRemetente));
            } else {
                try {
                    usuEntity = usuDelegate.findUsuario(usuCodigo, responsavel);
                } catch (final Exception ex) {
                    findException = ex;
                }

                if (usuEntity != null) {
                    nomeRemetente = usuEntity.getUsuNome();

                    final CustomTransferObject usuEntityTO = usuDelegate.findTipoUsuario(usuEntity.getUsuLogin(), responsavel);

                    if ((usuEntityTO != null) && (usuEntityTO.getAttribute(Columns.UCA_CSA_CODIGO) != null)) {
                        final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                        final ConsignatariaTransferObject csaTO = csaDelegate.findConsignataria((String) usuEntityTO.getAttribute(Columns.UCA_CSA_CODIGO), responsavel);
                        emailTexto.append(ApplicationResourcesHelper.getMessage("rotulo.email.a.consignataria", responsavel, csaTO.getCsaNome()));

                    } else if ((usuEntityTO != null) && (usuEntityTO.getAttribute(Columns.UCE_CSE_CODIGO) != null)) {
                        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                        final ConsignanteTransferObject cseTO = cseDelegate.findConsignante((String) usuEntityTO.getAttribute(Columns.UCE_CSE_CODIGO), responsavel);
                        emailTexto.append(ApplicationResourcesHelper.getMessage("rotulo.email.o.gestor", responsavel, cseTO.getCseNome()));

                    } else if ((usuEntityTO != null) && (usuEntityTO.getAttribute(Columns.UOR_ORG_CODIGO) != null)) {
                        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
                        final OrgaoTransferObject orgTO = cseDelegate.findOrgao((String) usuEntityTO.getAttribute(Columns.UOR_ORG_CODIGO), responsavel);
                        emailTexto.append(ApplicationResourcesHelper.getMessage("rotulo.email.o.orgao", responsavel, orgTO.getOrgNome()));

                    } else {
                        throw new ViewHelperException ("mensagem.erro.email.remetente.nao.encontrado", responsavel);
                    }
                } else {
                    throw new ViewHelperException ("mensagem.erro.email.remetente.nao.encontrado", responsavel, findException);
                }
            }

            if ((dadosRemetente != null) || (usuEntity != null)) {
                if (comunicacao.getAttribute(Columns.CMN_CODIGO_PAI) == null) {
                    emailTexto.append(" " + ApplicationResourcesHelper.getMessage("rotulo.email.gerou.nova.comunicacao", responsavel));
                } else {
                    final ComunicacaoController comunicacaoController = ApplicationContextProvider.getApplicationContext().getBean(ComunicacaoController.class);
                    final Comunicacao cmnOriginal = comunicacaoController.findComunicacaoByPK((String) comunicacao.getAttribute(Columns.CMN_CODIGO_PAI), responsavel);
                    emailTexto.append(" " + ApplicationResourcesHelper.getMessage("rotulo.email.gerou.comunicacao.resposta", responsavel) + "<br><br>\n");
                    emailTexto.append("<P ALIGN=\"CENTER\">").append("\"").append(cmnOriginal.getCmnTexto()).append("\"").append("</P>\n");
                    emailTexto.append("<br><br>\n");
                    emailTexto.append(ApplicationResourcesHelper.getMessage("rotulo.email.enviada.em", responsavel, DateHelper.toDateTimeString(cmnOriginal.getCmnData()))).append("\n");
                }

                final String nomeSistema = JspHelper.getNomeSistema(responsavel);
                final String cseNome = getCseNome(responsavel);

                final String titulo = ApplicationResourcesHelper.getMessage("rotulo.email.alerta.nova.comunicacao", responsavel, nomeSistema, cseNome);

                String mensagem = (String) comunicacao.getAttribute(Columns.CMN_TEXTO);
                mensagem = mensagem.replaceAll("\\r\\n|\\r|\\n", "<br>");

                emailTexto.append("<br><br>\n");
                emailTexto.append("<b>").append(mensagem).append("</b>");

                emailTexto.append("<br><br>\n");
                emailTexto.append(ApplicationResourcesHelper.getMessage("mensagem.email.confira.sistema.maiores.detalhes", responsavel, nomeSistema));

                final MailHelper mailHelper = new MailHelper();
                try {
                    mailHelper.send(endDestinatario, null, null, titulo, emailTexto.toString(), null);
                } catch (final MessagingException e) {
                    throw new ViewHelperException ("mensagem.erro.email.alerta.nova.comunicacao", responsavel, e);
                }

            } else {
                throw new ViewHelperException ("mensagem.erro.email.remetente.nao.encontrado", responsavel);
            }
        } catch (final ZetraException e) {
            throw new ViewHelperException ("mensagem.erro.email.alerta.nova.comunicacao", responsavel, e);
        }
    }

    /**
     * envia e-mail do relatorio
     * @param usuCodigo código do usuário rementente
     * @param endDestinatario - e-mail de destino
     * @param cmnCodigo - código da comunicação para a qual o e-mail será criado
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailRelatorioIntegracao(String csaCodigo, String endDestinatario,
            String pathArquivo, List<String> arquivos, String nomeArquivo, AcessoSistema responsavel) throws ViewHelperException {

        final StringBuilder emailTexto = new StringBuilder();
        final String nomeSistema = JspHelper.getNomeSistema(responsavel);
        final String cseNome = getCseNome(responsavel);
        final String titulo = ApplicationResourcesHelper.getMessage("rotulo.email.relatorio.integracao", responsavel, nomeSistema, cseNome);
        final List<String> anexo = new ArrayList<>();
        File arquivoAnexo = null;
        try {
            emailTexto.append("<b>").append(nomeSistema).append(" - ").append(cseNome).append("</b><br/>\n<br/>\n");
            emailTexto.append(ApplicationResourcesHelper.getMessage("mensagem.email.relatorio.integracao", responsavel));
            emailTexto.append("<br/>\n<br/>\n");

            if ((arquivos != null) && !arquivos.isEmpty()) {
                final String zip = pathArquivo + nomeArquivo + ".zip";
                FileHelper.zip(arquivos, zip);
                arquivoAnexo = new File(zip);

                if ((arquivoAnexo != null) && arquivoAnexo.exists()) {
                    anexo.add(arquivoAnexo.getAbsolutePath());
                    final MailHelper mailHelper = new MailHelper();
                    mailHelper.send(endDestinatario, null, null, titulo, emailTexto.toString(), anexo);
                }

            }

        } catch (MessagingException | IOException e) {
            throw new ViewHelperException ("mensagem.erro.email.relatorio.integracao", responsavel, e);
        } finally {
            if ((arquivoAnexo != null) && arquivoAnexo.exists()) {
                arquivoAnexo.delete();
            }
        }
    }

    /**
     * Envia email para consignatária de alerta de proximidade de corte.
     * @param destinatario
     * @param diaCorte
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailCsaAlertaProximidadeCorte(String destinatario, Date diaCorte, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(destinatario)) {
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        final String[] emails = destinatario.replace(" ", "").split(",|;");
        final List<String> destinatarios = Arrays.asList(emails);

        final String nomeSistema = JspHelper.getNomeSistema(responsavel);
        final String cseNome = getCseNome(responsavel);

        final String titulo =  ApplicationResourcesHelper.getMessage("rotulo.email.proximidade.corte.titulo", responsavel, cseNome);
        final String mensagemCorpo = ApplicationResourcesHelper.getMessage("rotulo.email.proximidade.corte", responsavel, cseNome, DateHelper.format(diaCorte, LocaleHelper.getDatePattern()));

        String corpo = "<b>" + nomeSistema + " - " + cseNome + "</b><br/>\n<br/>\n";
        corpo += mensagemCorpo;
        corpo += "<br/>\n<br/>\n";

        // Envia o email
        try {
            final MailHelper mailHelper = new MailHelper();
            if (destinatarios.size() == 1) {
                mailHelper.send(destinatarios.get(0), null, null, titulo, corpo, null);
            } else {
                // Se há mais de um destinatário, envia para todos via cópia carbono.
                mailHelper.send(null, null, TextHelper.join(destinatarios, ","), titulo, corpo, null);
            }
        } catch (final MessagingException e) {
            throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, e);

        }
    }

    /**
     * envia e-mail de alerta à consignatária de dias restantes para o bloqueio desta no sistema em questão
     * @param consignataria - consignatária alvo do email
     * @param diasParaBloqueio - dias restantes para bloqueio da consignatária no sistema
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailDiasBloqueioCsa(ConsignatariaTransferObject consignataria, int diasParaBloqueio, AcessoSistema responsavel) throws ViewHelperException {

        // Recupera o nome do consignante
        final String cseNome = getCseNome(responsavel);
        final String csaEmailExpiracao = consignataria.getCsaEmailExpiracao();
        final String emailCsa = !TextHelper.isNull(csaEmailExpiracao) ? csaEmailExpiracao : consignataria.getCsaEmail();

        if (TextHelper.isNull(emailCsa)) {
            LOG.warn("Erro ao enviar e-mail de alerta de dias para expiração de consignatária: e-mail da consignatária não cadastrado.");
            return;
        }

        final EnviarEmailExpiracaoParaConsignatariaCommand command = new EnviarEmailExpiracaoParaConsignatariaCommand();
        command.setCsaNome(consignataria.getCsaNome());
        command.setCseNome(cseNome);
        command.setEmail(emailCsa);
        command.setPrazoConsignatarias(diasParaBloqueio);
        command.setResponsavel(responsavel);
        command.execute();
    }

    /**
     * envia e-mail de alerta à consignatária de dias restantes para o bloqueio desta no sistema em questão
     * @param consignataria - consignatária alvo do email
     * @param diasParaBloqueio - dias restantes para bloqueio da consignatária no sistema
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailDiasBloqueioCsaParaCseOrg(Map<String, List<ConsignatariaTransferObject>> prazoConsignatarias, AcessoSistema responsavel) throws ViewHelperException {

        // Recupera o nome do consignante
        final String cseNome = getCseNome(responsavel);
        String email = getCseEmail(responsavel);

        if (TextHelper.isNull(email)) {
            // Pegar todos os endereços cadastrados em ORG_EMAIL
            email = getAllOrgEmail(responsavel);
        }

        if (TextHelper.isNull(email)) {
            LOG.error("Erro ao enviar e-mail de alerta de dias para expiração de consignatária: e-mail do consignante ou órgãos não cadastrado.");
            return;
        }

        final EnviarEmailExpiracaoConsignatariaCommand command = new EnviarEmailExpiracaoConsignatariaCommand();
        command.setCseNome(cseNome);
        command.setEmail(email);
        command.setPrazoConsignatarias(prazoConsignatarias);
        command.setResponsavel(responsavel);
        command.execute();

    }

    /**
     * envia e-mail de alerta ao gestor de que há mensagens pendentes para uma comunicação
     * @param comunicacao - comunicação que possui mensagens pendentes
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailAlertaCmnMsgPendentes(TransferObject comunicacao, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final ConsignanteTransferObject cse = new ConsignanteDelegate().findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            final ConsignatariaTransferObject csa = new ConsignatariaDelegate().findConsignataria((String) comunicacao.getAttribute(Columns.CSA_CODIGO), responsavel);

            final ServidorDelegate serDelegate = new ServidorDelegate();
            final TransferObject serTO = serDelegate.buscaUsuarioServidor((String) comunicacao.getAttribute(Columns.CMN_USU_CODIGO), responsavel);

            final StringBuilder emailTexto = new StringBuilder();

            final String nomeSistema = JspHelper.getNomeSistema(responsavel);

            emailTexto.append(ApplicationResourcesHelper.getMessage("rotulo.email.a.comunicacao", responsavel, comunicacao.getAttribute(Columns.CMN_NUMERO).toString()) + "<br><br>\n");
            emailTexto.append("<P ALIGN=\"CENTER\">").append("\"").append((String) comunicacao.getAttribute(Columns.CMN_TEXTO)).append("\"").append("</P>\n");
            emailTexto.append("<br><br>\n");
            emailTexto.append(ApplicationResourcesHelper.getMessage("mensagem.email.enviada.por.servidor.destinada.consignataria", responsavel
                    ,DateHelper.toDateTimeString((Date) comunicacao.getAttribute(Columns.CMN_DATA))
                    ,serTO.getAttribute(Columns.SER_NOME).toString()
                    ,serTO.getAttribute(Columns.RSE_MATRICULA).toString()
                    ,csa.getCsaIdentificador()
                    ,csa.getCsaNome()));
            emailTexto.append("<br><br>\n");
            emailTexto.append(ApplicationResourcesHelper.getMessage("mensagem.email.confira.sistema.maiores.detalhes", responsavel, nomeSistema));

            final String emailGestor = cse.getCseEmail();

            if (TextHelper.isNull(emailGestor)) {
                throw new ViewHelperException ("mensagem.erro.email.alerta.mensagem.pendente.comunicacao", responsavel);
            }

            final String titulo = ApplicationResourcesHelper.getMessage("rotulo.email.alerta.mensagem.pendente.comunicacao", responsavel, nomeSistema);

            final MailHelper mailHelper = new MailHelper();
            try {
                mailHelper.send(emailGestor, null, null, titulo, emailTexto.toString(), null);
            } catch (final MessagingException e) {
                throw new ViewHelperException ("mensagem.erro.email.alerta.mensagem.pendente.comunicacao", responsavel, e);
            }
        } catch (ConsignanteControllerException | ConsignatariaControllerException | ServidorControllerException e) {
            throw new ViewHelperException ("mensagem.erro.email.alerta.numero.maximo.mensagens.comunicacao", responsavel, e);
        }
    }

    /**
     * Envia e-mail com o arquivo do relatório em anexo para os destinatários informados.
     *
     * @param relatorio Caminho completo do arquivo do relatório contendo inclusive o nome do arquivo
     * @param tituloRelatorio Título do relatório
     * @param destinatarios E-mails destinatários.
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailRelatorio(String relatorio, String tituloRelatorio, String[] destinatarios, AcessoSistema responsavel) throws ViewHelperException {
        try {
            // Se não houver destinatários, não envia e-mail
            if ((destinatarios == null) || (destinatarios.length == 0)) {
                return;
            }

            // Recupera o nome do consignante
            final String cseNome = getCseNome(responsavel);

            // Define o titulo do e-mail
            final String titulo = gerarTituloEmail(cseNome, tituloRelatorio, responsavel);

            final String corpo = ApplicationResourcesHelper.getMessage("mensagem.email.relatorio.anexo", responsavel, tituloRelatorio);

            final List<String> anexos = new ArrayList<>();
            final File arquivoAnexo = new File(relatorio);
            if ((arquivoAnexo != null) && arquivoAnexo.exists()) {
                anexos.add(arquivoAnexo.getAbsolutePath());

                // Envia o e-mail
                final MailHelper mailHelper = new MailHelper();
                if (destinatarios.length == 1) {
                    mailHelper.send(destinatarios[0], null, null, titulo, corpo, anexos);
                } else {
                    // Se houver mais de um destinatário, envia para todos via cópia carbono.
                    mailHelper.send(null, null, TextHelper.join(destinatarios, ","), titulo, corpo, anexos);
                }
            }

        } catch (final MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Envia e-mail informando a geração do Relatório de Auditoria.
     *
     * @param destinatarios Endereços de e-mail dos destinatários.
     * @param responsavel Responsável pela operação.
     * @throws ViewHelperException
     * @throws MensagemControllerException
     */
    public static final void enviarEmailRelatorioAuditoria(String relatorio, String codigoEntidade, String tipoEntidade, List<String> destinatarios, AcessoSistema responsavel) throws ViewHelperException, MensagemControllerException {
        if ((destinatarios == null) || destinatarios.isEmpty() || TextHelper.isNull(destinatarios.get(0))) {
            throw new MensagemControllerException("mensagem.erro.email.nenhum.destinatario.cadastrado.valido", responsavel);
        }

        // Recupera o nome do consignante
        final String cseNome = getCseNome(responsavel);
        final String titulo = ApplicationResourcesHelper.getMessage("rotulo.email.auditoria", responsavel, cseNome);

        // Recupera a entidade para qual foi gerado o relatório
        String entidade = "";
        if (!TextHelper.isNull(tipoEntidade) && !TextHelper.isNull(codigoEntidade)) {
            try {
                if (AcessoSistema.ENTIDADE_CSE.equals(tipoEntidade)) {
                    final ConsignanteTransferObject cse = new ConsignanteDelegate().findConsignante(codigoEntidade, responsavel);
                    entidade = cse.getCseIdentificador() + " - " + cse.getCseNome();
                } else if (AcessoSistema.ENTIDADE_EST.equals(tipoEntidade)) {
                    final EstabelecimentoTransferObject est = new ConsignanteDelegate().findEstabelecimento(codigoEntidade, responsavel);
                    entidade =  est.getEstIdentificador() + " - " + (!TextHelper.isNull(est.getEstNomeAbrev()) ? est.getEstNomeAbrev() : est.getEstNome());
                } else if (AcessoSistema.ENTIDADE_ORG.equals(tipoEntidade)) {
                    final OrgaoTransferObject org = new ConsignanteDelegate().findOrgao(codigoEntidade, responsavel);
                    entidade = org.getOrgIdentificador() + " - " + (!TextHelper.isNull(org.getOrgNomeAbrev()) ? org.getOrgNomeAbrev() : org.getOrgNome());
                } else if (AcessoSistema.ENTIDADE_CSA.equals(tipoEntidade)) {
                    final ConsignatariaTransferObject csa = new ConsignatariaDelegate().findConsignataria(codigoEntidade, responsavel);
                    entidade =  csa.getCsaIdentificador() + " - " + (!TextHelper.isNull(csa.getCsaNomeAbreviado()) ? csa.getCsaNomeAbreviado() : csa.getCsaNome());
                } else if (AcessoSistema.ENTIDADE_COR.equals(tipoEntidade)) {
                    final CorrespondenteTransferObject cor = new ConsignatariaDelegate().findCorrespondente(codigoEntidade, responsavel);
                    entidade = cor.getCorIdentificador() + " - " + cor.getCorNome();
                }
            } catch (ConsignanteControllerException | ConsignatariaControllerException e) {
                LOG.error("Não foi possível localizar a entidade. ", e);
            }
        }

        String corpo = "";
        if (!TextHelper.isNull(entidade)) {
            corpo += "<b>" + entidade + "</b><br/>\n<br/>\n";
        }

        corpo += ApplicationResourcesHelper.getMessage("mensagem.email.relatorio.auditoria", responsavel, relatorio.substring(relatorio.lastIndexOf(File.separatorChar) + 1, relatorio.length()));

        final List<String> anexos = new ArrayList<>();
        anexos.add(relatorio);

        // Envia o email
        try {
            final MailHelper mailHelper = new MailHelper();
            if (destinatarios.size() == 1) {
                mailHelper.send(destinatarios.get(0), null, null, titulo, corpo, anexos);
            } else {
                // Se há mais de um destinatário, envia para todos via cópia carbono.
                mailHelper.send(null, null, TextHelper.join(destinatarios, ","), titulo, corpo, anexos);
            }
        } catch (final MessagingException e) {
            throw new MensagemControllerException ("mensagem.erro.email.indisponivel", responsavel, e);
        }
    }

    /**
     * Envia e-mail sobre o bloqueio do sistema, com a mensagem informada por parâmetro
     * @param motivo      : mensagem de motivo de bloqueio
     * @param responsavel : responsável pelo bloqueio
     * @throws ViewHelperException
     */
    public static void enviarEmailBloqueioSistema(String motivo, AcessoSistema responsavel) throws ViewHelperException  {
        final String nomeSistema = JspHelper.getNomeSistema(responsavel);
        final String cseNome = getCseNome(responsavel);
        final String usuNome = responsavel.getUsuNome();
        final String ipAcesso = responsavel.getIpUsuario();
        final String data = DateHelper.toDateTimeString(DateHelper.getSystemDatetime());

        final String titulo = ApplicationResourcesHelper.getMessage("rotulo.email.bloqueio.sistema", responsavel, nomeSistema, cseNome);

        final StringBuilder texto = new StringBuilder();
        texto.append(ApplicationResourcesHelper.getMessage("mensagem.email.sistema.bloqueado", responsavel, cseNome, usuNome, data, motivo, ipAcesso));

        // Envia o e-mail
        try {
            new MailHelper().send(DESTINATARIO_BLOQ_SISTEMA, null, null, titulo, texto.toString(), null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * envia e-mail relatorio com lista de relatórios de integração com códigos de verba inválidos por consignatária
     * @param emailTexto - listagem de relatórios inválidos por consignatária
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailValidacaoIntegracaoCsa(String textoArqInvalidos, String textoArqIgnorados, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(EMAIL_VALIDACAO_INTEGRACAO_CSA)) {
            throw new ViewHelperException("mensagem.informe.email.endereco.validacao.relatorio.integracao.consignataria", responsavel);
        }
        if (TextHelper.isNull(textoArqInvalidos) && TextHelper.isNull(textoArqIgnorados)) {
            // Nada a ser enviado por e-mail
            return;
        }

        final String nomeSistema = JspHelper.getNomeSistema(responsavel);
        final String cseNome = getCseNome(responsavel);
        final String titulo = ApplicationResourcesHelper.getMessage("rotulo.email.alerta.relatorio.integracao.consignataria", responsavel, nomeSistema);

        final StringBuilder corpoEmail = new StringBuilder();
        corpoEmail.append(nomeSistema).append(" - ").append(cseNome);
        corpoEmail.append("<br><br>");

        if (!TextHelper.isNull(textoArqInvalidos)) {
            corpoEmail.append(ApplicationResourcesHelper.getMessage("mensagem.email.consignatarias.relatorios.integracao.codigo.verba.invalido", responsavel));
            corpoEmail.append(textoArqInvalidos.toString());
        }
        if (!TextHelper.isNull(textoArqInvalidos) && !TextHelper.isNull(textoArqIgnorados)) {
            corpoEmail.append("<br><br>");
        }
        if (!TextHelper.isNull(textoArqIgnorados)) {
            corpoEmail.append(ApplicationResourcesHelper.getMessage("mensagem.email.consignatarias.relatorios.integracao.leiaute.customizado", responsavel));
            corpoEmail.append(textoArqIgnorados.toString());
        }
        corpoEmail.append("<br><br>");

        // Envia o e-mail
        try {
            LOG.debug(corpoEmail.toString());
            new MailHelper().send(EMAIL_VALIDACAO_INTEGRACAO_CSA, null, null, titulo, corpoEmail.toString(), null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * envia e-mail de informação ao gestor de que um arquivo de margem/retorno foi recebido
     * @param tipo
     * @param nomeArquivo
     * @param enviaEmailCSE
     * @param enviaEmailORG
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailRecebimentoArquivo(String tipo, String nomeArquivo, boolean enviaEmailCSE, boolean enviaEmailORG, String orgCodigo, String obs, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            final List<String> destinatarios = new ArrayList<>();

            if (enviaEmailCSE) {
                final ConsignanteTransferObject cse = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
                if (!TextHelper.isNull(cse.getCseEmailFolha())) {
                    destinatarios.add(cse.getCseEmailFolha());
                }
            }

            if (enviaEmailORG) {
                if (responsavel.isOrg() || !TextHelper.isNull(orgCodigo)) {
                    final String codigo = !TextHelper.isNull(orgCodigo) ? orgCodigo : responsavel.getOrgCodigo();
                    final OrgaoTransferObject org = cseDelegate.findOrgao(codigo, responsavel);
                    if (!TextHelper.isNull(org.getOrgEmailFolha())) {
                        destinatarios.add(org.getOrgEmailFolha());
                    }
                } else {
                    // Se não é usuário de órgão, nem arquivo direcionado a um órgão, mas ainda assim deve
                    // enviar e-mail para órgão, então lista todos e envia o e-mail
                    final TransferObject criterio = new CustomTransferObject();
                    criterio.setAttribute(Columns.ORG_ATIVO, CodedValues.STS_ATIVO);
                    final List<TransferObject> orgaos = cseDelegate.lstOrgaos(criterio, responsavel);
                    for (final TransferObject org : orgaos) {
                        if (!TextHelper.isNull(org.getAttribute(Columns.ORG_EMAIL_FOLHA))) {
                            destinatarios.add(org.getAttribute(Columns.ORG_EMAIL_FOLHA).toString());
                        }
                    }
                }
            }

            // Se não possui e-mail cadastrado, não tenta enviar e-mail
            if (destinatarios.isEmpty()) {
                return;
            }

            String corpo = ApplicationResourcesHelper.getMessage("mensagem.email.notificacao.upload.arquivo.corpo", responsavel);

            if (corpo.contains("<CABECALHO>")) {
                final String mensagemCorpo = ApplicationResourcesHelper.getMessage("mensagem.email.recebimento.arquivo", responsavel, tipo);
                corpo = corpo.replace("<CABECALHO>", mensagemCorpo);
            }

            final String nomeSistema = JspHelper.getNomeSistema(responsavel);

            if(!TextHelper.isNull(obs)) {
                corpo += "<br/>\n";
                corpo += ApplicationResourcesHelper.getMessage("rotulo.email.observacao", responsavel, obs);
            }

            if (corpo.contains("<DETALHE_ARQUIVOS>")) {

                final StringBuilder emailTexto = new StringBuilder();

                if (!TextHelper.isNull(nomeArquivo)) {
                    emailTexto.append(nomeArquivo);
                }

                emailTexto.append("<br/>\n<br/>\n");
                emailTexto.append(ApplicationResourcesHelper.getMessage("rotulo.email.enviado.usuario", responsavel, responsavel.getUsuLogin()));

                emailTexto.append("<br/>\n<br/>\n");
                emailTexto.append(ApplicationResourcesHelper.getMessage("rotulo.email.enviado.data", responsavel, DateHelper.format(Calendar.getInstance().getTime(), LocaleHelper.getDateTimePattern())));

                emailTexto.append("<br/>\n<br/>\n");

                corpo = corpo.replace("<DETALHE_ARQUIVOS>", emailTexto.toString());

            }


            if (corpo.contains("<RODAPE>")) {
                final String rodape = ApplicationResourcesHelper.getMessage("mensagem.email.recebimento.arquivo.rodape", responsavel);
                corpo = corpo.replace("<RODAPE>", rodape);
            }


            String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.notificacao.upload.arquivo.titulo", responsavel);

            if (titulo.contains("<NOME_SISTEMA>")) {
                titulo = titulo.replace("<NOME_SISTEMA>", nomeSistema);
            }

            if (titulo.contains("<NOME_CONSIGNANTE>")) {
                titulo = titulo.replace("<NOME_CONSIGNANTE>", getCseNome(responsavel));
            }

            if (titulo.contains("<TITULO>")) {
                titulo = titulo.replace("<TITULO>", ApplicationResourcesHelper.getMessage("rotulo.email.recebimento.arquivo", responsavel, nomeSistema, tipo.toString()));
            }

            final MailHelper mailHelper = new MailHelper();
            try {
                mailHelper.send(TextHelper.join(destinatarios, ",").replace(';', ','), null, null, titulo, corpo, null);
            } catch (final MessagingException e) {
                throw new ViewHelperException("mensagem.erro.email.recebimento.arquivo", responsavel, e, tipo.toString());
            }
        } catch (final Exception e) {
            throw new ViewHelperException("mensagem.erro.email.recebimento.arquivo", responsavel, e, tipo.toString());
        }
    }

    /**
     * Envia e-mail para consignatárias e correspondente informando upload de arquivo.
     * @param tipo
     * @param nomeArquivo
     * @param enviaEmailCSA
     * @param enviaEmailCOR
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailUploadArquivoCsa(String tipo, String nomeArquivo, boolean enviaEmailCSA, boolean enviaEmailCOR, String obs, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailUploadArquivoCsaCommand command = new EnviarEmailUploadArquivoCsaCommand();
        command.setTipo(tipo);
        command.setNomeArquivo(nomeArquivo);
        command.setEnviaEmailCSA(enviaEmailCSA);
        command.setEnviaEmailCOR(enviaEmailCOR);
        command.setResponsavel(responsavel);
        command.setObservacao(obs);
        command.execute();
    }

    /**
     * Envia e-mail à consignatária identificada pelo parâmetro "csaCodigo" informando que a consignação,
     * representada pelo parâmetro "adeCodigo" está pendente de aprovação/deferimento pela consignatária.
     * @param csaCodigo
     * @param adeCodigo
     * @param responsavel
     * @throws ZetraException
     * @throws MessagingException
     */
    public static final void enviarEmailCsaPendenciaDeferimento(String csaCodigo, String adeCodigo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            // Busca as informações do contrato que está pendente de deferimento
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);
            if (ade == null) {
                return;
            }

            // Recupera o endereço de e-mail da consignatária que deve realizar o deferimento
            final ConsignatariaTransferObject consignataria = new ConsignatariaDelegate().findConsignataria(csaCodigo, responsavel);
            final String emailDestino = consignataria.getCsaEmail();

            // Se não tem e-mail não faz nada
            if (TextHelper.isNull(emailDestino)) {
                return;
            }

            // Dados necessários para envio do e-mail
            final String cseNome = getCseNome(responsavel);
            final String rseMatricula = ade.getAttribute(Columns.RSE_MATRICULA) != null ? ade.getAttribute(Columns.RSE_MATRICULA).toString() : "";
            final String serNome =  ade.getAttribute(Columns.SER_NOME) != null ? ade.getAttribute(Columns.SER_NOME).toString() : "";
            final String serCpf = ade.getAttribute(Columns.SER_CPF) != null ? ade.getAttribute(Columns.SER_CPF).toString() : "";
            final String adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? NumberHelper.format(((BigDecimal) ade.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang(), true) : "";
            final String adeData = ade.getAttribute(Columns.ADE_DATA) != null ? DateHelper.toDateTimeString((Date) ade.getAttribute(Columns.ADE_DATA)) : "";

            // Define o titulo do E-mail
            final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("rotulo.email.operacao.consignacao.pendente.aprovacao", responsavel), responsavel);

            // Texto Comum para todas as mensagems
            final StringBuilder textoGeral = new StringBuilder();
            textoGeral.append(ApplicationResourcesHelper.getMessage("mensagem.email.consignacao.pendente.aprovacao", responsavel));
            textoGeral.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.servidor", responsavel, rseMatricula, serNome));
            textoGeral.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.cpf", responsavel, serCpf));
            textoGeral.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.data.inclusao", responsavel, adeData));
            textoGeral.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.valor.prestacao", responsavel, adeVlr));
            textoGeral.append("<br/>\n<br/>\n");

            // Envia os emails.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(emailDestino, null, null, titulo, textoGeral.toString(), null, null);
        } catch (AutorizacaoControllerException | ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        } catch (final MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.email.alerta.entidade.responsavel.deferimento.contrato", responsavel, ex);
        }
    }

    /**
     * Envia e-mail ao endereço padrão de consignante do sistema para alertar de inclusão de novo contrato.
     * @param adeNumero número do novo contrato incluído.
     * @param operacaoAde operação que disparou o email
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailCseInclusaoAde(String adeCodigo, String operacaoAde, AcessoSistema responsavel) throws ViewHelperException {
        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        try {
            final ConsignanteTransferObject cseTO = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);

            // Define o titulo do E-mail
            final String titulo = gerarTituloEmail(cseTO.getCseNome(), ApplicationResourcesHelper.getMessage("mensagem.email.cse.titulo.consignacao.criada", responsavel), responsavel);

            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject adeTO = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);
            final StringBuilder corpoEmail = new StringBuilder(ApplicationResourcesHelper.getMessage("mensagem.email.cse.consignacao.cabecalho", responsavel,
                    responsavel.getUsuLogin(), //0
                    adeTO.getAttribute(Columns.ADE_NUMERO).toString(), //1
                    operacaoAde, //2
                    DateHelper.toDateTimeString(DateHelper.getSystemDatetime()) //3
                    ));
            corpoEmail.append(gerarTextoDetalheContratoParaEmail(adeTO, cseTO.getCseNome(), responsavel));

            // se o órgão do servidor possuir e-mail, este prevalece sobre o do gestor.
            final String orgEmail = (String) adeTO.getAttribute(Columns.ORG_EMAIL_VALIDAR_SERVIDOR);

            final String cseMail = cseTO.getCseEmailValidarServidor();

            if (TextHelper.isNull(cseMail) && TextHelper.isNull(orgEmail)) {
                throw new ViewHelperException("mensagem.email.validar.servidor.cse.nao.cadastrado", responsavel);
            }

            // Envia os emails.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(!TextHelper.isNull(orgEmail) ? orgEmail : cseMail, null, null, titulo, corpoEmail.toString(), null, null);
        } catch (final MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.email.cse.falha.envio.email", responsavel, ex);
        } catch (ConsignanteControllerException | AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Envia e-mail ao endereço padrão de consignante do sistema para alertar de liquidação de contrato.
     * @param adeNumero número do contrato liquidado.
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailCseLiquidacaoAde(String adeCodigo, AcessoSistema responsavel) throws ViewHelperException {
        final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
        final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        try {
            if (ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_EMAIL_CSE_LIQUIDAR_CONTRATO, responsavel)){
                final ConsignanteTransferObject cseTO = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);

                // Busca as informações do contrato
                final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);

                final String titulo = gerarTituloEmail(cseTO.getCseNome(), ApplicationResourcesHelper.getMessage("mensagem.email.cse.titulo.consignacao.liquidada", responsavel), responsavel);
                final StringBuilder corpoEmail = new StringBuilder(ApplicationResourcesHelper.getMessage("mensagem.email.cse.consignacao.cabecalho", responsavel,
                        responsavel.getUsuLogin(), //0
                        ade.getAttribute(Columns.ADE_NUMERO).toString(), //1
                        OperacaoEConsigEnum.LIQUIDAR_CONSIGNACAO.getOperacao(), //2
                        DateHelper.toDateTimeString(DateHelper.getSystemDatetime()) //3
                        ));
                corpoEmail.append(gerarTextoDetalheContratoParaEmail(ade, cseTO.getCseNome(), responsavel));

                // se o órgão do servidor possuir e-mail, este prevalece sobre o do gestor.
                final String orgEmail = (String) ade.getAttribute(Columns.ORG_EMAIL);

                final String cseMail = cseTO.getCseEmail();

                if (TextHelper.isNull(cseMail) && TextHelper.isNull(orgEmail)) {
                    throw new ViewHelperException("mensagem.email.cse.nao.cadastrado", responsavel);
                }

                // Envia o email
                final MailHelper mailHelper = new MailHelper();
                mailHelper.send(!TextHelper.isNull(orgEmail) ? orgEmail : cseMail, null, null, titulo, corpoEmail.toString(), null, null);
            }
        } catch (final MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.email.cse.falha.envio.email", responsavel, ex);
        } catch (ConsignanteControllerException | AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /**
     * Envia por e-mail a informação de saldo devedor para o gestor/órgão quando a solicitação
     * for cadastrada para exclusão de servidor
     * @param adeCodigo
     * @param responsavel
     * @return
     */
    public static final void enviarEmailDataCorteAlterada(String diaAtual, Date periodoAtual, AcessoSistema responsavel) {
        try {
            final List<Consignataria> consignatarias = new ConsignatariaDelegate().findConsignatariaComEmailCadastrado(responsavel);
            if ((consignatarias != null) && !consignatarias.isEmpty()) {
                // Recupera o nome do consignante
                final String cseNome = getCseNome(responsavel);

                // Define o titulo do E-mail
                final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("mensagem.email.data.corte.alterada", responsavel), responsavel);

                final DateFormat df = new SimpleDateFormat(LocaleHelper.getDatePattern());

                // Texto com os dados do contrato
                final String texto = ApplicationResourcesHelper.getMessage("rotulo.consignante.singular.arg0", responsavel, cseNome) + "<br>"
                             + ApplicationResourcesHelper.getMessage("mensagem.email.data.corte.alterada.corpo", responsavel, df.format(periodoAtual), diaAtual)
                        ;

                for (final Consignataria consignataria : consignatarias) {
                    if (!TextHelper.isNull(consignataria.getCsaEmail())) {
                        final MailHelper mailHelper = new MailHelper();
                        mailHelper.send(consignataria.getCsaEmail(), null, null, titulo, texto, null);
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia email com OTP para o servidor.
     * @param destinatario
     * @param otp
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailOTPServidor(String nome, String destinatario, String otp, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(destinatario)) {
            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
        }

        try {
            final String nomeSistema = JspHelper.getNomeSistema(responsavel);
            final String cseNome = getCseNome(responsavel);

            final String tituloEmail = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("rotulo.otp", responsavel), responsavel);

            final String primeiroNome = nome.split("\\s+")[0];

            final EnviarEmailOTPServidorCommand command = new EnviarEmailOTPServidorCommand();

            command.setNomeSistema(nomeSistema);
            command.setCseNome(cseNome);
            command.setTituloEmail(tituloEmail);
            command.setResponsavel(responsavel);
            command.setNome(nome);
            command.setPrimeiroNome(primeiroNome);
            command.setEmail(destinatario);
            command.setOtp(otp);

            command.execute();

        } catch (final ViewHelperException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * Envia email com OTP para o usuário.
     * @param destinatario
     * @param otp
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailOTP(String nome, String destinatario, String otp, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(destinatario)) {
            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
        }

        try {
            final String nomeSistema = JspHelper.getNomeSistema(responsavel);
            final String cseNome = getCseNome(responsavel);

            final String tituloEmail = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("rotulo.otp", responsavel), responsavel);

            final String primeiroNome = nome.split("\\s+")[0];


            final EnviarEmailOTPUsuarioCommand command = new EnviarEmailOTPUsuarioCommand();

            command.setNomeSistema(nomeSistema);
            command.setCseNome(cseNome);
            command.setTituloEmail(tituloEmail);
            command.setResponsavel(responsavel);
            command.setNome(nome);
            command.setPrimeiroNome(primeiroNome);
            command.setEmail(destinatario);
            command.setOtp(otp);

            command.execute();

        } catch (final ViewHelperException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * envia e-mail ao servidor alertando-o de que a documentação anexa da solicitação foi aprovada
     * @param destinatario
     * @param adeCodigo
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailDocAprovadaServidor(String destinatario, String adeCodigo, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(destinatario)) {
            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
        }

        try {
            final String cseNome = getCseNome(responsavel);
            final AutorizacaoDelegate autDelegate = new AutorizacaoDelegate();
            final TransferObject ade = autDelegate.buscaAutorizacao(adeCodigo, responsavel);

            final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("mensagem.email.documentacao.aprovada.titulo", responsavel), responsavel);

            final StringBuilder corpo = new StringBuilder();
            corpo.append("<br/>\n" + ApplicationResourcesHelper.getMessage("mensagem.email.documentacao.aprovada", responsavel, ade.getAttribute(Columns.ADE_NUMERO).toString()));
            corpo.append("<br/>\n<br/>\n");

            final String texto = titulo + "<br/>\n<br/>\n" + corpo.toString();

            //Envia para a consignatária o mesmo email
            if (!TextHelper.isNull(ade.getAttribute(Columns.CSA_EMAIL))) {
                destinatario += ","+ade.getAttribute(Columns.CSA_EMAIL).toString();
            }

            // Envia o e-mail.
            new MailHelper().send(destinatario, null, null, titulo, texto, null);
        } catch (AutorizacaoControllerException | MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * Envia email para consignatária sobre alteração no servidor.
     *
     * @param dadosServidor
     * @param cpf
     * @param matricula
     * @param novoStatus
     * @param obsAlteracao
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailCsasAlteracaoSer(TransferObject dadosServidor, String cpf, String matricula, String novoStatus, String obsAlteracao, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailCsasAlteracaoSerCommand command = new EnviarEmailCsasAlteracaoSerCommand();
        command.setDadosServidor(dadosServidor);
        command.setCpf(cpf);
        command.setMatricula(matricula);
        command.setNovoStatus(novoStatus);
        command.setObsAlteracao(obsAlteracao);
        command.setResponsavel(responsavel);
        command.execute();
    }

    /**
     * Envia e-mail para  as consignatárias cuja proposta foi rejeitada.
     * @param adeCodigo
     * @param plsVencedora
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailCsaPropostaLeilaoRejeitada(String adeCodigo, PropostaLeilaoSolicitacao plsVencedora, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final LeilaoSolicitacaoController leilaoSolicitacaoController = ApplicationContextProvider.getApplicationContext().getBean(LeilaoSolicitacaoController.class);
            final List<TransferObject> lstRejeitadas = leilaoSolicitacaoController.lstPropostaLeilaoSolicitacao(adeCodigo, null, StatusPropostaEnum.REJEITADA.getCodigo(), false, responsavel);

            final List<String> csaEmails = new ArrayList<>();
            final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            for (final TransferObject rejeitada: lstRejeitadas) {
                final String csaCodigo = (String) rejeitada.getAttribute(Columns.CSA_CODIGO);

                final ConsignatariaTransferObject csaTO = csaDelegate.findConsignataria(csaCodigo, responsavel);
                if (!TextHelper.isNull(csaTO.getCsaEmail())) {
                    csaEmails.add(csaTO.getCsaEmail());
                }
            }

            TransferObject adeTO = null;

            if (!csaEmails.isEmpty()) {
                final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
                adeTO = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);
            } else {
                return;
            }

            final String cseNome = getCseNome(responsavel);
            final Long adeNumero = (Long) adeTO.getAttribute(Columns.ADE_NUMERO);
            final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.titulo.leilao.encerrado", responsavel, adeNumero.toString()), responsavel);

            final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
            final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

            final String adeTipoTaxa = (String) adeTO.getAttribute(Columns.ADE_TIPO_TAXA);

            final List<String> parametros = new ArrayList<>();
            parametros.add(CodedValues.TPS_ADD_VALOR_IOF_VAL_TAXA_JUROS);
            parametros.add(CodedValues.TPS_ADD_VALOR_TAC_VAL_TAXA_JUROS);
            parametros.add(CodedValues.TPS_VLR_LIQ_TAXA_JUROS);

            final ParametroDelegate parDelegate = new ParametroDelegate();
            final ParamSvcTO paramSvcCse = parDelegate.selectParamSvcCse((String) adeTO.getAttribute(Columns.SVC_CODIGO), parametros, responsavel);

            // Define rótulo para o campo de Taxa de Juros/CET/Coeficiente
            String rotuloTaxa = "";
            if (adeTipoTaxa != null) {
                if (CodedValues.TIPO_TAXA_CET.equals(adeTipoTaxa)) {
                    //CET Real: Cálculo com valor liberado sem adicionar IOF e TAC
                    rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel);
                } else if (CodedValues.TIPO_TAXA_JUROS.equals(adeTipoTaxa)) {
                    // Taxa de Juros Real: Cálculo com valor liberado somando IOF e TAC
                    rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel);
                }
            } else if (temCET) {
                rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel);
            } else if (paramSvcCse.isTpsVlrLiqTaxaJuros() || simulacaoPorTaxaJuros) {
                rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel);
            }

            final StringBuilder corpoEmail = new StringBuilder(ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.cabecalho.leilao.encerrado", responsavel, adeTO.getAttribute(Columns.ADE_NUMERO).toString(), (String) adeTO.getAttribute(Columns.SVC_DESCRICAO))).append("<BR>");

            corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("mensagem.email.servidor.proposta.aprovada.leilao", responsavel));
            corpoEmail.append("</b> : <BR><BR>");
            corpoEmail.append("<b>").append(rotuloTaxa).append("</b> : ").append(plsVencedora.getPlsTaxaJuros().setScale(2, java.math.RoundingMode.HALF_UP));
            corpoEmail.append("<BR>");
            corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.liberado", responsavel)).append("</b> : ");
            corpoEmail.append(LocaleHelper.getCurrencyFormat().format(plsVencedora.getPlsValorLiberado().setScale(2, java.math.RoundingMode.HALF_UP))).append("<BR>");
            corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela", responsavel)).append("</b> : ");
            corpoEmail.append(LocaleHelper.getCurrencyFormat().format(plsVencedora.getPlsValorParcela().setScale(2, java.math.RoundingMode.HALF_UP)));
            corpoEmail.append("<BR>");
            corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.prazo.singular", responsavel)).append("</b> : ");
            corpoEmail.append(plsVencedora.getPlsPrazo()).append(" ").append(CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adeTO.getAttribute(Columns.ADE_PERIODICIDADE)) ? ApplicationResourcesHelper.getMessage("rotulo.consignacao.mes.plural", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.quinzena.plural", responsavel)).append("<BR>");

            if (ParamSist.getBoolParamSist(CodedValues.TPC_ENVIAR_DADOS_SERVIDOR_CSA_REJEITADA_PROPOSTA_LEILAO, responsavel)) {
                final String serNome = (String) adeTO.getAttribute(Columns.SER_NOME);
                final String serTel = (String) adeTO.getAttribute(Columns.SER_TEL);
                final String serCelular = (String) adeTO.getAttribute(Columns.SER_CELULAR);

                corpoEmail.append("<BR>");
                corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.leilao.proposta.rejeitada.servidor", responsavel)).append("</b> : ");
                corpoEmail.append(serNome).append("<BR>");

                if (!TextHelper.isNull(serTel) || !TextHelper.isNull(serCelular)) {
                    corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.leilao.proposta.rejeitada.telefone", responsavel)).append("</b> : ");

                    if (!TextHelper.isNull(serTel) && !TextHelper.isNull(serCelular)) {
                        corpoEmail.append(TextHelper.formatarTelefone(serTel)).append(" / ").append(TextHelper.formatarTelefone(serCelular)).append("<BR>");
                    } else {
                        corpoEmail.append(!TextHelper.isNull(serTel) ? TextHelper.formatarTelefone(serTel) : TextHelper.formatarTelefone(serCelular)).append("<BR>");
                    }
                }
            }

            // Envia o e-mail.
            for (final String email : csaEmails) {
                new MailHelper().send(email, null, null, titulo, corpoEmail.toString(), null);
            }
        } catch (LeilaoSolicitacaoControllerException | ConsignatariaControllerException | AutorizacaoControllerException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(ex.getMessageKey(), responsavel, ex);
        } catch (final MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /********************************************* MÉTODOS AUXILIARES PARA ENVIO DE E-MAIL *****************************************************/

    /**
     * Recupera as configurações de e-mail de uma consignatária para eventos relacionados ao processo de compra de contrato.
     * @param tipoEvento
     * @param csaCodigo
     * @param svcCodigo
     * @param responsavel
     * @return
     * @throws Exception
     */
    private static ConfirguracoesEmailCsaCompraContrato recuperarEnderecosEmailCompraContrato(int tipoEvento, String csaCodigo, String svcCodigo, AcessoSistema responsavel) throws Exception {
        // Em uma só consulta traz a configuração de e-mail para o evento de compra
        // e a configuração dos destinatários da mensagem.
        final List<String> tpsCodigos = new ArrayList<>();
        tpsCodigos.add(CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA);

        String parametroEvento = null;
        switch (tipoEvento) {
            case TIPO_COMPRA_CONTRATO:
            case TIPO_SOL_RECALCULO_SALDO_DEVEDOR:
                parametroEvento = CodedValues.TPS_EMAIL_INF_CONTRATOS_COMPRADOS;
                break;
            case TIPO_CADASTRO_SALDO_DEVEDOR:
            case TIPO_REJ_PGT_SALDO_DEVEDOR:
                parametroEvento = CodedValues.TPS_EMAIL_INF_SALDO_DEVEDOR;
                break;
            case TIPO_APROVACAO_SALDO_DEVEDOR:
            case TIPO_REJEICAO_SALDO_DEVEDOR:
                parametroEvento = CodedValues.TPS_EMAIL_INF_APROVACAO_SALDO_DEVEDOR;
                break;
            case TIPO_INF_PGT_SALDO_DEVEDOR:
                parametroEvento = CodedValues.TPS_EMAIL_INF_PGT_SALDO_DEVEDOR;
                break;
            case TIPO_LIQUIDACAO_COMPRA_CONTRATO:
                parametroEvento = CodedValues.TPS_EMAIL_INF_LIQ_CONTRATO_COMPRADO;
                break;
            default:
                break;
        }
        tpsCodigos.add(parametroEvento);

        final ConfirguracoesEmailCsaCompraContrato enderecos = new ConfirguracoesEmailCsaCompraContrato();

        // Recupera o endereço de e-mail do cadastro da consignatária.
        final ConsignatariaTransferObject consignataria = new ConsignatariaDelegate().findConsignataria(csaCodigo, responsavel);
        enderecos.emailCsaPrincipal = consignataria.getCsaEmail();

        // Busca os parâmetros de serviço da consignatária
        final List<TransferObject> parametros = new ParametroDelegate().selectParamSvcCsa(svcCodigo, csaCodigo, tpsCodigos, false, responsavel);
        if ((parametros != null) && (parametros.size() > 0)) {
            String codigoParametro, valorParametro;
            for (final TransferObject parametro : parametros) {
                codigoParametro = (parametro != null) && !TextHelper.isNull(parametro.getAttribute(Columns.TPS_CODIGO)) ? parametro.getAttribute(Columns.TPS_CODIGO).toString() : null;
                valorParametro = (parametro != null) && !TextHelper.isNull(parametro.getAttribute(Columns.PSC_VLR)) ? parametro.getAttribute(Columns.PSC_VLR).toString() : null;

                if (CodedValues.TPS_DESTINATARIOS_EMAILS_CONTROLE_COMPRA.equals(codigoParametro)) {
                    enderecos.destinatariosEmail = valorParametro;
                } else if ((codigoParametro != null) && (parametroEvento != null) && codigoParametro.equals(parametroEvento)) {
                    enderecos.emailCsaEvento = valorParametro;
                }
            }
        }

        return enderecos;
    }

    /**
     * Monta a lista de endereços de destinatários.
     * @param destinatariosEmail Valor do parâmetro que informa quem deve receber e-mails (CSA, COR ou ambos)
     * @param emailCsa
     * @param emailCorrespondente
     * @return
     */
    private static List<String> montarListaEnderecosDestinatarios(String destinatariosEmail, String emailCsa, String emailCorrespondente) {
        final List<String> listaEnderecos = new ArrayList<>();

        if (TextHelper.isNull(destinatariosEmail) || CodedValues.RECEBE_EMAIL_APENAS_CONSIGNATARIA.equals(destinatariosEmail)) {
            if (!TextHelper.isNull(emailCsa)) {
                listaEnderecos.add(emailCsa);
            }
        } else if (CodedValues.RECEBE_EMAIL_APENAS_CORRESPONDENTE.equals(destinatariosEmail)) {
            if (!TextHelper.isNull(emailCorrespondente)) {
                listaEnderecos.add(emailCorrespondente);
            } else // Se o correspondente não possui e-mail, envia para a consignatária.
            if (!TextHelper.isNull(emailCsa)) {
                listaEnderecos.add(emailCsa);
            }
        } else if (CodedValues.RECEBE_EMAIL_CSA_E_COR.equals(destinatariosEmail)) {
            if (!TextHelper.isNull(emailCorrespondente)) {
                listaEnderecos.add(emailCorrespondente);
            }
            if (!TextHelper.isNull(emailCsa)) {
                listaEnderecos.add(emailCsa);
            }
        }

        return listaEnderecos;
    }

    /**
     * Classe privada que reúne configurações de e-mail relacionadas ao
     * processo de compra de contrato para uma consignatária.
     */
    private static class ConfirguracoesEmailCsaCompraContrato {
        private String destinatariosEmail = null;
        private String emailCsaPrincipal = null;
        private String emailCsaEvento = null;
    }

    /**
     * Obtém o nome do consignante para envio de e-mail
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    private static final String getCseNome(AcessoSistema responsavel) throws ViewHelperException {
        try {
            final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            final ConsignanteTransferObject cse = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            return cse.getCseNome();
        } catch (final ConsignanteControllerException e) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    /**
     * Obtém o email do consignante
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    private static final String getCseEmail(AcessoSistema responsavel) throws ViewHelperException {
        try {
            final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            final ConsignanteTransferObject cse = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            return cse.getCseEmail();
        } catch (final ConsignanteControllerException e) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    /**
     * Obtém o email do consignante
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    private static final String getAllOrgEmail(AcessoSistema responsavel) throws ViewHelperException {
        try {
            final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            final List<TransferObject> lstOrgaos = cseDelegate.lstOrgaos(null, responsavel);

            final StringBuilder builder = new StringBuilder();

            for (final Iterator<TransferObject> iterator = lstOrgaos.iterator(); iterator.hasNext();) {
                final TransferObject orgao = iterator.next();

                final String email = (String)orgao.getAttribute(Columns.ORG_EMAIL);

                if (!TextHelper.isNull(email)) {
                    builder.append(email);
                    if (iterator.hasNext()) {
                        builder.append(",");
                    }
                }

            }

            return builder.toString();

        } catch (final ConsignanteControllerException e) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, e);
        }
    }

    /**
     * Método auxuliar para geração do texto do e-mail com o detalhe de um
     * contrato. Recebe um TransferObject com os dados do contrato.
     * @param ade
     * @param cseNome
     * @param responsavel
     * @return
     */
    private static String gerarTextoDetalheContratoParaEmail(TransferObject ade1, String cseNome, AcessoSistema responsavel) {

        final AutorizacaoDelegate del = new AutorizacaoDelegate();
        TransferObject ade;
        // Como não vinham dados suficientes em nenhuma das situações que este método é chamado, foi escolhido realizar uma
        // busca pelos dados do contrato.
        try {
            ade = del.buscaAutorizacao((String) ade1.getAttribute(Columns.ADE_CODIGO), responsavel);
        } catch (final AutorizacaoControllerException e) {
            // Usa os dados informados (Provavelmente nunca entrará aqui)
            ade = ade1;
        }

        final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
        final String adeIdentificador = ade.getAttribute(Columns.ADE_IDENTIFICADOR) != null ? ade.getAttribute(Columns.ADE_IDENTIFICADOR).toString() : "";
        final String adePrazo = ade.getAttribute(Columns.ADE_PRAZO) != null ? ade.getAttribute(Columns.ADE_PRAZO).toString() : ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel);
        final String adePrdPagas = ade.getAttribute(Columns.ADE_PRD_PAGAS) != null ? ade.getAttribute(Columns.ADE_PRD_PAGAS).toString() : "0";
        final String adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? NumberHelper.format(((BigDecimal) ade.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang(), true) : "";
        final String adeData = ade.getAttribute(Columns.ADE_DATA) != null ? DateHelper.toDateTimeString((Date) ade.getAttribute(Columns.ADE_DATA)) : "";
        final String sadDescricao = ade.getAttribute(Columns.SAD_DESCRICAO) != null ? ade.getAttribute(Columns.SAD_DESCRICAO).toString() : "";
        final String rseMatricula = ade.getAttribute(Columns.RSE_MATRICULA) != null ? ade.getAttribute(Columns.RSE_MATRICULA).toString() : "";
        final String serNome =  ade.getAttribute(Columns.SER_NOME) != null ? ade.getAttribute(Columns.SER_NOME).toString() : "";
        final String serCpf = ade.getAttribute(Columns.SER_CPF) != null ? ade.getAttribute(Columns.SER_CPF).toString() : "";
        final String csaIdentificador = ade.getAttribute(Columns.CSA_IDENTIFICADOR) != null ? ade.getAttribute(Columns.CSA_IDENTIFICADOR).toString() : "";
        final String csaNome = ade.getAttribute(Columns.CSA_NOME_ABREV) != null ? ade.getAttribute(Columns.CSA_NOME_ABREV).toString() : ade.getAttribute(Columns.CSA_NOME).toString();
        final String corIdentificador = ade.getAttribute(Columns.COR_IDENTIFICADOR) != null ? ade.getAttribute(Columns.COR_IDENTIFICADOR).toString() : "";
        final String corNome = ade.getAttribute(Columns.COR_NOME) != null ? ade.getAttribute(Columns.COR_NOME).toString() : "";
        final StringBuilder servico = new StringBuilder().append((ade.getAttribute(Columns.CNV_COD_VERBA) != null) && !"".equals(ade.getAttribute(Columns.CNV_COD_VERBA).toString()) ? ade.getAttribute(Columns.CNV_COD_VERBA).toString() : ade.getAttribute(Columns.SVC_IDENTIFICADOR).toString());
        servico.append((ade.getAttribute(Columns.ADE_INDICE) != null) && !"".equals(ade.getAttribute(Columns.ADE_INDICE).toString()) ? ade.getAttribute(Columns.ADE_INDICE).toString() : "");
        servico.append(" - ").append(ade.getAttribute(Columns.SVC_DESCRICAO).toString());

        final StringBuilder texto = new StringBuilder();
        texto.append("<br>\n<b>" + ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel) + ":</b> " + cseNome);
        texto.append("<br>\n<b>" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel) + ":</b> " + csaIdentificador + " - " + csaNome);
        if (!TextHelper.isNull(corIdentificador) && !TextHelper.isNull(corNome)) {
            texto.append("<br>\n<b>" + ApplicationResourcesHelper.getMessage("rotulo.correspondente.singular", responsavel) + ":</b> " + corIdentificador + " - " + corNome);
        }
        texto.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.servidor", responsavel, rseMatricula, serNome));
        texto.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.cpf", responsavel, serCpf));
        texto.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.data.inclusao", responsavel, adeData));
        texto.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.numero.ade", responsavel, adeNumero));
        texto.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.consignacao.identificador", responsavel, adeIdentificador));
        texto.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.servico", responsavel, servico.toString()));
        texto.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.valor.prestacao", responsavel, adeVlr));
        texto.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.numero.prestacao", responsavel, adePrazo));
        texto.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.parcelas.pagas", responsavel, adePrdPagas));
        texto.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.situacao.ade", responsavel, sadDescricao));

        return texto.toString();
    }

    /**
     * Método auxuliar para geração do texto do e-mail com o detalhe do saldo
     * devedor de um contrato. Recebe um TransferObject com os dados do saldo.
     * @param saldoDevedorTO
     * @param responsavel
     * @return
     */
    private static String gerarTextoDetalheSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO, AcessoSistema responsavel) {
        try {
            final String adeCodigo = saldoDevedorTO.getAdeCodigo();
            final String valorSaldoDevedor = NumberHelper.format(saldoDevedorTO.getSdvValor().doubleValue(), NumberHelper.getLang());
            final boolean exigeMultiplosSaldos = ParamSist.paramEquals(CodedValues.TPC_EXIGE_MULTIPLOS_SALDOS_DEVEDORES, CodedValues.TPC_SIM, responsavel);

            final StringBuilder textoSaldoDevedor = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.email.abaixo.seguem.dados.consignacao", responsavel));
            if (exigeMultiplosSaldos) {
                final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
                final String dataSaldoDevedor1 = adeDelegate.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_DATA_VCTO1, responsavel);
                String valorSaldoDevedor1 = adeDelegate.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_VALOR_VCTO1, responsavel);
                final String dataSaldoDevedor2 = adeDelegate.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_DATA_VCTO2, responsavel);
                String valorSaldoDevedor2 = adeDelegate.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_VALOR_VCTO2, responsavel);
                final String dataSaldoDevedor3 = adeDelegate.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_DATA_VCTO3, responsavel);
                String valorSaldoDevedor3 = adeDelegate.getValorDadoAutDesconto(adeCodigo, CodedValues.TDA_SDV_VALOR_VCTO3, responsavel);

                valorSaldoDevedor1 = NumberHelper.reformat(valorSaldoDevedor1, "en", NumberHelper.getLang());
                valorSaldoDevedor2 = NumberHelper.reformat(valorSaldoDevedor2, "en", NumberHelper.getLang());
                valorSaldoDevedor3 = NumberHelper.reformat(valorSaldoDevedor3, "en", NumberHelper.getLang());

                textoSaldoDevedor.append("<br/>\n").append(ApplicationResourcesHelper.getMessage("rotulo.email.valor.saldo.devedor.vencimento.em", responsavel, dataSaldoDevedor1, valorSaldoDevedor1));
                textoSaldoDevedor.append("<br/>\n").append(ApplicationResourcesHelper.getMessage("rotulo.email.valor.saldo.devedor.vencimento.em", responsavel, dataSaldoDevedor2, valorSaldoDevedor2));
                textoSaldoDevedor.append("<br/>\n").append(ApplicationResourcesHelper.getMessage("rotulo.email.valor.saldo.devedor.vencimento.em", responsavel, dataSaldoDevedor3, valorSaldoDevedor3));
            } else {
                textoSaldoDevedor.append("<br/>\n").append(ApplicationResourcesHelper.getMessage("rotulo.email.valor.saldo.devedor", responsavel, valorSaldoDevedor));
            }
            if (!TextHelper.isNull(saldoDevedorTO.getObs())) {
                textoSaldoDevedor.append("<br/>\n").append(ApplicationResourcesHelper.getMessage("rotulo.email.observacao.abreviado", responsavel, saldoDevedorTO.getObs()));
            }

            return textoSaldoDevedor.toString();
        } catch (AutorizacaoControllerException | ParseException ex) {
            LOG.error(ex.getMessage(), ex);
            return "";
        }
    }

    /**
     * Método auxuliar para geração do texto do e-mail com a informação para
     * depósito do saldo devedor de um contrato.
     * @param saldoDevedorTO
     * @return
     */
    private static String gerarTextoInfDepositoSaldoDevedor(SaldoDevedorTransferObject saldoDevedorTO) {

        return (saldoDevedorTO.getBcoCodigo() != null ? "<br/>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.banco.deposito", (AcessoSistema) null,  saldoDevedorTO.getBcoCodigo().toString()) : "")
                + (!TextHelper.isNull(saldoDevedorTO.getSdvAgencia()) ? "<br/>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.agencia.deposito", (AcessoSistema) null, saldoDevedorTO.getSdvAgencia()) : "")
                + (!TextHelper.isNull(saldoDevedorTO.getSdvConta()) ? "<br/>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.conta.deposito", (AcessoSistema) null, saldoDevedorTO.getSdvConta()) : "")
                + (!TextHelper.isNull(saldoDevedorTO.getSdvNomeFavorecido()) ? "<br/>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.nome.favorecido.deposito", (AcessoSistema) null, saldoDevedorTO.getSdvNomeFavorecido()) : "")
                + (!TextHelper.isNull(saldoDevedorTO.getSdvCnpj()) ? "<br/>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.cnpj.deposito", (AcessoSistema) null, saldoDevedorTO.getSdvCnpj()) : "")
                + "<br/>\n<br/>\n";
    }

    /**
     * Define o título padrão dos e-mails, com o nome do sistema,
     * opcionalmente com o nome do consignante, e um titulo customizado.
     * @param cseNome
     * @param tituloCustomizado
     * @param responsavel
     * @return
     */
    private static String gerarTituloEmail(String cseNome, String tituloCustomizado, AcessoSistema responsavel) {
        return JspHelper.getNomeSistema(responsavel) + (!TextHelper.isNull(cseNome) ? " - " + cseNome : "") + ": " + tituloCustomizado;
    }


    /**
     * Envia boleto por email para o servidor.
     * @param boleto
     * @param responsavel
     * @throws Exception
     */
    public static void enviaBoleto(TransferObject boleto, AcessoSistema responsavel) throws Exception {
        if (!TextHelper.isNull(boleto.getAttribute(Columns.SER_EMAIL))) {
            final String corpo = geraHtmlBoleto(boleto, responsavel);
            final MailHelper mh = new MailHelper();
            final String cseNome = getCseNome(responsavel);
            final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("rotulo.boleto.consignacao.titulo", responsavel), responsavel);
            mh.send((String) boleto.getAttribute(Columns.SER_EMAIL), null, null, titulo, corpo, null);
        }
    }

  //TODO: mover para BoletoHelper este método
    /**
     * encapsula geração de HTML do boleto
     * @param boleto
     * @param includeCSS - define se mantém ou remove código CSS do template final (relevante para parser HTML do iText)
     * @param responsavel
     * @return
     * @throws Exception
     */
    public static String geraHtmlBoleto(TransferObject boleto, boolean includeCSS, AcessoSistema responsavel) throws Exception{
        final ParametroDelegate parDelegate = new ParametroDelegate();
        final List<String> parametros = new ArrayList<>();
        parametros.add(CodedValues.TPS_BUSCA_BOLETO_EXTERNO);
        parametros.add(CodedValues.TPS_DIAS_DESBL_SOLICITACAO_NAO_CONF);
        parametros.add(CodedValues.TPS_EXIGE_CODIGO_AUTORIZACAO_CONF_SOLIC);
        final String svcCodigo = (String) boleto.getAttribute(Columns.SVC_CODIGO);
        final ParamSvcTO paramSvcCse = parDelegate.selectParamSvcCse(svcCodigo, parametros, responsavel);
        final boolean boletoExterno = paramSvcCse.isTpsBuscaBoletoExterno();
        final boolean exigeCodAutSolicitacao = paramSvcCse.isTpsExigeCodAutorizacaoSolic();

        final int numDias = (paramSvcCse.getTpsDiasDesblSolicitacaoNaoConf() != null) && !"".equals(paramSvcCse.getTpsDiasDesblSolicitacaoNaoConf()) ?
                Integer.parseInt(paramSvcCse.getTpsDiasDesblSolicitacaoNaoConf()) : 2;
        Date dataSim = (Date) boleto.getAttribute(Columns.ADE_DATA);
        if (dataSim == null) {
          dataSim = DateHelper.getSystemDatetime();
        }
        final Date dataValidade = DateHelper.addDays(dataSim, numDias - 1);

        final StringBuilder out = new StringBuilder();

        if (includeCSS) {
            out.append("<HTML>");
            out.append("<HEAD>");
            out.append("<TITLE>").append(ApplicationResourcesHelper.getMessage("rotulo.boleto.consignacao.titulo", responsavel)).append("</TITLE>");
            out.append("<META HTTP-EQUIV=\"Content-Type\" CONTENT=\"text/html; charset=iso-8859-1\">");
            out.append("<link rel=\"stylesheet\" href=\"../css/style.css\" type=\"text/css\">");

            out.append("<STYLE>");
            out.append("table {  padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px}");
            out.append("table.Tabelaresultado {  padding-top: 0px; padding-right: 0px; padding-bottom: 0px; padding-left: 0px}");
            out.append("table.Tabelaresultado1 { padding-top: 1pt; padding-right: 1pt; padding-bottom: 1pt; padding-left: 1pt ; border: #000000 solid; border-width: 0px 1px}");
            out.append("td.dados {");
            out.append("border: 1px solid #666666;");
            out.append("padding: 1px;");
            out.append("}");
            out.append("BODY {");
            out.append("BACKGROUND-COLOR: #FFFFFF;");
            out.append("background-image: none;");
            out.append("}");
            out.append("BODY.PRINT {");
            out.append("BACKGROUND-COLOR: #FFFFFF;");
            out.append("background-image: none;");
            out.append("}");
            out.append("@media print {    /* for good browsers */");
            out.append(".no-print, .no-print * {");
            out.append("display: none !important;");
            out.append("}");
            out.append("BODY {");
            out.append("BACKGROUND-COLOR: #FFFFFF;");
            out.append("background-image: none;");
            out.append("}");
            out.append("}");
            out.append("</STYLE>");
        }
        out.append("</HEAD>");
        out.append("<BODY>");

        final String boletoMsg = CodedNames.TEMPLATE_BOLETO_AUT_DESCONTO;

        if (boletoExterno) {
            String absolutePath = ParamSist.getDiretorioRaizArquivos();
            absolutePath += File.separatorChar + "boleto" + File.separatorChar + svcCodigo + File.separatorChar + boletoMsg;

            File arqBoleto = new File(absolutePath);
            if (!arqBoleto.exists()) {
                absolutePath = ParamSist.getDiretorioRaizArquivos();
                absolutePath += File.separatorChar + "boleto" + File.separatorChar + boletoMsg;
                arqBoleto = new File(absolutePath);
                if (!arqBoleto.exists()) {
                    LOG.error(ApplicationResourcesHelper.getMessage("mensagem.log.boleto.nao.encontrado", responsavel, absolutePath));
                    throw new ZetraException("mensagem.erro.interno.boleto.nao.encontrado", responsavel);
                }
            }

            boleto.setAttribute(Columns.ORG_CODIGO, responsavel.getOrgCodigo());

            String msg = FileHelper.readAll(absolutePath);
            msg = BoletoHelper.gerarTextoBoleto(msg, (CustomTransferObject) boleto, responsavel);
            out.append(msg);
            out.append("</BODY></HTML>");
            return out.toString();
        }

        final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
        final String codigoAutorizacaoSolic = adeDelegate.getValorDadoAutDesconto((String) boleto.getAttribute(Columns.ADE_CODIGO), CodedValues.TDA_CODIGO_AUTORIZACAO_SOLICITACAO, responsavel);

        out.append("<TABLE WIDTH=\"80%\" BORDER=\"0\" ALIGN=\"CENTER\" CELLPADDING=\"0\" CELLSPACING=\"0\">");
        if (exigeCodAutSolicitacao && responsavel.isSer() && !TextHelper.isNull(codigoAutorizacaoSolic)) {
            out.append("<TR>");
            out.append("<TD>");
            out.append("<br><font class=\"codigoAutorizacao\"><b>" + ApplicationResourcesHelper.getMessage("rotulo.consignacao.codigo.autorizacao", responsavel) + "</b>: " + codigoAutorizacaoSolic + "</font>");
            out.append("</TD>");
            out.append("</TR>");
        }
        out.append("<TR>");
        out.append("<TD>");
        out.append(" <TABLE WIDTH=\"100%\" BORDER=\"1\" CELLPADDING=\"0\" CELLSPACING=\"0\" >");
        out.append("       <TR>");
        out.append("         <TD colspan=\"5\" ALIGN=\"CENTER\"><FONT SIZE=\"4\"><I>");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.boleto.consignacao.titulo", responsavel));
        out.append("</I></FONT></TD>");
        out.append("       </TR>");
        out.append("       <TR>");
        out.append("         <TD colspan=\"5\" class=\"TituloColuna\"><I>I - ");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.boleto.dados.pessoais", responsavel)).append("</I></TD>");
        out.append("       </TR>");
        out.append("       <TR>");
        out.append("         <TD colspan=\"3\" valign=\"top\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.nome", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_NOME))).append("&nbsp;</TD>");
        out.append("        <TD colspan=\"2\" valign=\"top\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_CPF))).append("&nbsp;</TD>");
        if(!TextHelper.isNull(boleto.getAttribute(Columns.SER_SEXO))) {
            out.append("        <TD valign=\"top\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
            out.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo", responsavel)).append("</span><BR>");
            out.append("M".equalsIgnoreCase(boleto.getAttribute(Columns.SER_SEXO).toString())
                    ? ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.masculino", responsavel)
                            : ApplicationResourcesHelper.getMessage("rotulo.servidor.sexo.feminino", responsavel)).append("&nbsp;</TD>");
        }
        out.append("      </TR>");
        out.append("    <TR>");
        out.append("      <TD valign=\"top\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.dataNasc", responsavel)).append("</span><br>");
        out.append(DateHelper.toDateString((Date) boleto.getAttribute(Columns.SER_DATA_NASC))).append("&nbsp;</TD>");
        out.append("        <TD valign=\"top\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.estadoCivil", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_EST_CIVIL)) + "&nbsp;</TD>");
        out.append("        <TD colspan=\"3\" valign=\"top\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.identidade.uf.orgao.data", responsavel));
        out.append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_NRO_IDT))).append("&nbsp; </TD>");
        out.append("      </TR>");
        out.append("      <TR>");
        out.append("        <TD colspan=\"2\" valign=\"top\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.nomePai", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_NOME_PAI))).append("&nbsp; </TD>");
        out.append("        <TD colspan=\"3\" valign=\"top\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.nomeMae", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_NOME_MAE))).append("&nbsp;</TD>");
        out.append("      </TR>");
        out.append("      <TR>");
        out.append("       <TD colspan=\"2\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.boleto.rua.avenida.praca", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_END)) + "&nbsp;</TD>");
        out.append("        <TD CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.endereco.numero", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_NRO)) + "&nbsp;</TD>");
        out.append("        <TD CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.endereco.complemento.abreviado", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_COMPL))).append("&nbsp;</TD>");
        out.append("        <TD CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.endereco.bairro", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_BAIRRO))).append("&nbsp;</TD>");
        out.append("      </TR>");
        out.append("     <TR>");
        out.append("        <TD valign=\"top\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.endereco.cidade", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_CIDADE)) + "&nbsp;</TD>");
        out.append("        <TD valign=\"top\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.endereco.estado", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_UF))).append("&nbsp;</TD>");
        out.append("        <TD valign=\"top\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.endereco.cep", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_CEP))).append("&nbsp;</TD>");
        out.append("        <TD colspan=\"2\" valign=\"top\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.telefone", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SER_TEL))).append("&nbsp;</TD>");
        out.append("      </TR>");
        out.append("      <TR>");
        out.append("        <TD colspan=\"5\" class=\"TituloColuna\"><I>II - ");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.boleto.dados.funcionais", responsavel)).append("</I></TD>");
        out.append("      </TR>");
        out.append("      <TR valign=\"top\">");
        out.append("        <TD width=\"19%\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.RSE_MATRICULA)) + "&nbsp;</TD>");
        out.append("        <TD width=\"19%\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.categoria", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.RSE_TIPO)) + "&nbsp;</TD>");
        out.append("        <TD width=\"19%\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.servidor.dataAdmissao", responsavel)).append("</span><br>");
        out.append(forHtmlContent(DateHelper.toDateString((Date) boleto.getAttribute(Columns.RSE_DATA_ADMISSAO)))).append("&nbsp;</TD>");
        out.append("       <TD colspan=\"2\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.singular", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.ORG_IDENTIFICADOR))).append(" - ");
        out.append(forHtmlContent(boleto.getAttribute(Columns.ORG_NOME))).append("&nbsp;</TD>");
        out.append("      </TR>");
        out.append("      <TR valign=\"top\">");
        out.append("        <TD colspan=\"2\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.endereco.comercial", responsavel));
        out.append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.ORG_LOGRADOURO)) + "&nbsp;</TD>");
        out.append("        <TD CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.numero", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.ORG_NRO))).append("&nbsp;</TD>");
        out.append("       <TD CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.complemento", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.ORG_COMPL))).append("&nbsp;</TD>");
        out.append("        <TD CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.bairro", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.ORG_BAIRRO))).append("&nbsp;</TD>");
        out.append("      </TR>");
        out.append("      <TR valign=\"top\">");
        out.append("        <TD colspan=\"2\" CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.cidade", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.ORG_CIDADE))).append("&nbsp;</TD>");
        out.append("       <TD CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.uf", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.ORG_UF))).append("&nbsp;</TD>");
        out.append("       <TD CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.cep", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.ORG_CEP))).append("&nbsp;</TD>");
        out.append("        <TD CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.orgao.telefone.ramal", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.ORG_TEL))).append("&nbsp;</TD>");
        out.append("      </TR>");
        out.append("      <TR>");
        out.append("        <TD colspan=\"5\" class=\"TituloColuna\"><I>III -");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.boleto.caracteristicas.operacao", responsavel)).append("</I></TD>");
        out.append("     </TR>");
        out.append("     <TR valign=\"top\">");
        out.append("       <TD colspan=\"4\" CLASS=\"FonteReduzida\">");
        out.append("         <span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.CSA_NOME)));
        out.append("       </TD>");
        out.append("       <TD CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.usuario.responsavel", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.USU_LOGIN))).append("&nbsp;</TD>");
        out.append("     </TR>");
        out.append("     <TR valign=\"top\">");
        out.append("       <TD CLASS=\"FonteReduzida\">");
        out.append("         <span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.codigo.verba.singular", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.CNV_COD_VERBA))).append("&nbsp;</TD>");
        out.append("        <TD colspan=\"2\" CLASS=\"FonteReduzida\">");
        out.append("          <span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.natureza.operacao", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.SVC_DESCRICAO))).append("&nbsp;</TD>");
        out.append("       <TD CLASS=\"FonteReduzida\">");
        out.append("         <span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.ADE_NUMERO))).append("&nbsp;</TD>");
        out.append("       <TD CLASS=\"FonteReduzida\"><span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.ranking", responsavel)).append("</span><BR>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.CDE_RANKING))).append("&ordf;</TD>");
        out.append("     </TR>");
        out.append("      <TR valign=\"top\">");
        out.append("        <TD CLASS=\"FonteReduzida\">");
        out.append("           <span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.liberado", responsavel)).append("</span><br>");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel))
            .append(NumberHelper.format(((BigDecimal) boleto.getAttribute(Columns.CDE_VLR_LIBERADO)).doubleValue(), NumberHelper.getLang(), true)).append("&nbsp;</TD>");
        out.append("        <TD CLASS=\"FonteReduzida\">");
        out.append("         <span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela", responsavel)).append("</span><br>");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel))
            .append(NumberHelper.format(((BigDecimal) boleto.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang(), true)).append("&nbsp;</TD>");
        out.append("       <TD CLASS=\"FonteReduzida\">");
        out.append("          <span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo", responsavel)).append("</span><br>");
        out.append(forHtmlContent(boleto.getAttribute(Columns.ADE_PRAZO))).append("&nbsp;</TD>");
        out.append("       <TD CLASS=\"FonteReduzida\">");
        out.append("         <span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.inicial", responsavel)).append("</span><br>");
        out.append(forHtmlContent(DateHelper.toPeriodString((java.util.Date) boleto.getAttribute(Columns.ADE_ANO_MES_INI)))).append("&nbsp;</TD>");
        out.append("       <TD CLASS=\"FonteReduzida\">");
        out.append("         <span class=\"RotuloReduzido\">");
        out.append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.data.final", responsavel)).append("</span><br>");
        out.append(forHtmlContent(DateHelper.toPeriodString((java.util.Date) boleto.getAttribute(Columns.ADE_ANO_MES_FIM)))).append("&nbsp;</TD>");
        out.append("     </TR>");
        out.append("   </TABLE>");
        out.append(" </TD>");
        out.append("</TR>");
        out.append("<TR>");
        out.append("<TD CLASS=\"FonteReduzida\"><p align=\"justify\"><br>");

        final StringBuilder absolutePath = new StringBuilder().append(ParamSist.getDiretorioRaizArquivos());
        absolutePath.append(File.separatorChar).append("boleto").append(File.separatorChar).append(boletoMsg);
        final String msg = FileHelper.readAll(absolutePath.toString())
                .replaceAll("<SERVICO>", boleto.getAttribute(Columns.SVC_DESCRICAO).toString().toUpperCase())
                .replaceAll("<CONSIGNATARIA>", boleto.getAttribute(Columns.CSA_NOME).toString().toUpperCase());
        out.append(msg);

        out.append("</p></TD>");
        out.append("</TR>");
        out.append(" <TR>");
        out.append(" <TD>&nbsp;</TD>");
        out.append("</TR>");
        out.append("<TR>");
        out.append("  <TD>");
        out.append("  <TABLE WIDTH=\"100%\" BORDER=\"0\">");
        out.append("     <TR>");
        out.append("       <TD colspan=\"2\" VALIGN=\"TOP\" CLASS=\"FonteReduzida\">_________________________, _____ ")
                .append(ApplicationResourcesHelper.getMessage("rotulo.data.de", responsavel))
                .append(" ________________ ")
                .append(ApplicationResourcesHelper.getMessage("rotulo.data.de", responsavel)).append(" 20____</TD>");
        out.append("      </TR>");
        out.append("      <TR>");
        out.append("        <TD height=\"30\" VALIGN=\"TOP\" CLASS=\"FonteReduzida\">&nbsp;</TD>");
        out.append("        <TD>&nbsp;</TD>");
        out.append("      </TR>");
        out.append("      <TR>");
        out.append("        <TD align=\"center\" VALIGN=\"TOP\" CLASS=\"FonteReduzida\"><p>______________________________________________<BR>")
                .append(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel).toUpperCase())
                .append("</p></TD>");
        out.append("        <TD align=\"center\" valign=\"top\"><p>______________________________________________<BR>")
                .append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel).toUpperCase())
                .append("</p></TD>");
        out.append("      </TR>");
        out.append("  </TABLE></TD>");
        out.append("</TR>");
        out.append("<TR>");
        out.append("  <TD>");
        out.append("  <HR>");
        out.append("  </TD>");
        out.append("</TR>");
        out.append("<TR>");
        out.append("  <TD CLASS=\"FonteReduzida\">");
        out.append("  <TABLE WIDTH=\"100%\" BORDER=\"0\" cellpadding=\"0\" cellspacing=\"0\">");
        out.append("      <TR>");
        out.append("        <TD CLASS=\"FonteReduzida\" WIDTH=\"40%\">")
                .append(ApplicationResourcesHelper.getMessage("rotulo.boleto.valido.ate", responsavel))
                .append(DateHelper.toDateString(dataValidade)).append("</TD>");
        out.append("        <TD WIDTH=\"20%\" align=\"center\" CLASS=\"FonteReduzida\">")
                .append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.numero", responsavel)).append(": ")
                .append(forHtmlContent(boleto.getAttribute(Columns.ADE_NUMERO))).append("</TD>");
        out.append("        <TD CLASS=\"FonteReduzida\" WIDTH=\"40%\" ALIGN=\"RIGHT\">")
                .append(forHtmlContent(boleto.getAttribute(Columns.USU_LOGIN))).append(" - ")
                .append(DateHelper.toDateTimeString(dataSim)).append("</TD>");
        out.append("      </TR>");
        out.append("  </TABLE></TD>");
        out.append("</TR>");
        out.append("</TABLE>");
        out.append("<BR>");
        out.append("</BODY></HTML>");

        return out.toString();

    }

    //TODO: mover para BoletoHelper este método
    public static String geraHtmlBoleto(TransferObject boleto, AcessoSistema responsavel) throws Exception{
        return geraHtmlBoleto(boleto, true, responsavel);
    }

    private static String forHtmlContent(Object st){
        if (st == null) {
            return "";
        }
        return TextHelper.forHtmlContent(st);
    }

    /**
     * Envia por e-mail a informação de cadastro de servidor
     * @param adeCodigo
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static final void enviarEmailNotificacaoCadastroServidor(String rseCodigo, AcessoSistema responsavel) throws ViewHelperException{
        final EnviarEmailNotificacaoCadastroServidorCommand command = new EnviarEmailNotificacaoCadastroServidorCommand();
        command.setRseCodigo(rseCodigo);
        command.setResponsavel(responsavel);
        command.execute();
    }

    public static void enviarEmailCsaSolicitacaoFeitaPorSer(String csaEmail, String rseCodigo, String adeCodigo, BigDecimal rseMargemRestAntes, Short adeIncMargem, ConvenioTransferObject cnvTo, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailCsaNovaSolicitacaoCommand command = new EnviarEmailCsaNovaSolicitacaoCommand();
        command.setRseCodigo(rseCodigo);
        command.setAdeCodigo(adeCodigo);
        command.setCnvTo(cnvTo);
        command.setCsaMail(csaEmail);
        command.setRseMargemRestOld(rseMargemRestAntes);
        command.setIncMargem(adeIncMargem);
        command.setResponsavel(responsavel);
        command.execute();
    }

    public static void enviarEmailCsaSolicitacaoCanceladaPorSer(String csaEmail, String rseCodigo, String adeCodigo, BigDecimal rseMargemRestAntes, Short adeIncMargem, ConvenioTransferObject cnvTo, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailCsaSerCancelaSolicitacaoCommand command = new EnviarEmailCsaSerCancelaSolicitacaoCommand();
        command.setRseCodigo(rseCodigo);
        command.setAdeCodigo(adeCodigo);
        command.setCnvTo(cnvTo);
        command.setCsaMail(csaEmail);
        command.setRseMargemRestOld(rseMargemRestAntes);
        command.setIncMargem(adeIncMargem);
        command.setResponsavel(responsavel);
        command.execute();
    }

    /**
     * Envia e-mail para as consignatárias informando sobre a inclusão de um novo leilão
     * @param adeCodigo
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static void enviarEmailNotificacaoNovoLeilao(SolicitacaoAutorizacao solicitacao, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject ade = adeDelegate.buscaAutorizacao(solicitacao.getAutDesconto().getAdeCodigo(), responsavel);

            final LeilaoSolicitacaoController leilaoSolicitacaoController = ApplicationContextProvider.getApplicationContext().getBean(LeilaoSolicitacaoController.class);
            final List<TransferObject> lstPls = leilaoSolicitacaoController.lstPropostaLeilaoSolicitacao(ade.getAttribute(Columns.ADE_CODIGO).toString(), null, null, false, responsavel);

            // Nesse momento, somente 1 proposta pode estar ativa
            if ((lstPls !=null) && (lstPls.isEmpty() || (lstPls.size() > 1))) {
                throw new LeilaoSolicitacaoControllerException("mensagem.erro.leilao.envio.notificacao.consignataria", responsavel);
            }

            final TransferObject proposta = lstPls.get(0);

            final List<String> lstEmailsDest = leilaoSolicitacaoController.lstEmailConsignatariasNotificacaoLeilao(solicitacao.getAutDesconto().getAdeCodigo(), responsavel);
            String titulo = null;

            if ((lstEmailsDest != null) && !lstEmailsDest.isEmpty()) {
                final String cseNome = getCseNome(responsavel);
                final String dataAbertura = DateHelper.format(solicitacao.getSoaData(), LocaleHelper.getDateTimePattern());
                final String dataValidade = !TextHelper.isNull(solicitacao.getSoaDataValidade()) ? DateHelper.format(solicitacao.getSoaDataValidade(), LocaleHelper.getDateTimePattern()) : ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.solicitacao.data.fechamento.nao.definida", responsavel);
                final String adeValorLiq = NumberHelper.format(((BigDecimal) proposta.getAttribute(Columns.PLS_VALOR_LIBERADO)).doubleValue(), NumberHelper.getLang());
                final String adeValor = NumberHelper.format(((BigDecimal) proposta.getAttribute(Columns.PLS_VALOR_PARCELA)).doubleValue(), NumberHelper.getLang());

                final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                final String serNome = ade.getAttribute(Columns.SER_NOME).toString();
                final String rseMatricula = ade.getAttribute(Columns.RSE_MATRICULA).toString();
                final String serCpf = ade.getAttribute(Columns.SER_CPF).toString();
                String posto = null;

                if (ShowFieldHelper.showField(FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR, responsavel)) {
                    posto = ade.getAttribute(Columns.POS_DESCRICAO) != null ? ade.getAttribute(Columns.POS_DESCRICAO).toString() : "";
                }

                // Valor Taxa Juros
                 final String adeTaxaJurosReal = NumberHelper.format(((BigDecimal) proposta.getAttribute(Columns.PLS_TAXA_JUROS)).doubleValue(), NumberHelper.getLang()) + " %";

                // Define rótulo para o campo de Taxa de Juros/CET/Coeficiente
                final String rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.proposta.leilao.solicitacao.proposta.taxa.informada", responsavel);

                titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.titulo.alerta.novo.leilao", responsavel, dataAbertura, dataValidade), responsavel);
                final StringBuilder corpoEmail = new StringBuilder();
                corpoEmail.append(ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.novo.leilao.aberto", responsavel)).append("<br><br>");

                corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.proposta.leilao.solicitacao.numero", responsavel)).append("</b> : ");
                corpoEmail.append(adeNumero).append("<br>");
                corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.solicitacao.valor.liberado", responsavel)).append("</b> : ");
                corpoEmail.append(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)).append(" ").append(adeValorLiq).append("<br>");
                corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.solicitacao.valor.prestacao", responsavel)).append("</b> : ");
                corpoEmail.append(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)).append(" ").append(adeValor).append("<br>");
                corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo", responsavel)).append("</b> : ");
                corpoEmail.append(proposta.getAttribute(Columns.PLS_PRAZO)).append("<br>");
                corpoEmail.append("<b>").append(rotuloTaxa).append("</b> : ");
                corpoEmail.append(adeTaxaJurosReal).append("<br>");
                corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.solicitacao.data.abertura", responsavel)).append("</b> : ");
                corpoEmail.append(dataAbertura).append("<br>");
                corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.solicitacao.data.fechamento", responsavel)).append("</b> : ");
                corpoEmail.append(dataValidade).append("<br><br>");
                corpoEmail.append(ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.dados.servidor", responsavel)).append("<br><br>");

                corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.nome", responsavel)).append("</b> : ");
                corpoEmail.append(serNome).append("<br>");
                corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel)).append("</b> : ");
                corpoEmail.append(serCpf).append("<br>");
                corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.matricula.singular", responsavel)).append("</b> : ");
                corpoEmail.append(rseMatricula).append("<br>");

                if (posto != null) {
                    corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.posto", responsavel)).append("</b> : ");
                    corpoEmail.append(posto).append("<br>");
                }

                // Envia o e-mail.
                for (final String email : lstEmailsDest) {
                    new MailHelper().send(TipoNotificacaoEnum.EMAIL_NOVA_PROPOSTA_LEILAO, email, null, null, titulo, corpoEmail.toString(), null, null, responsavel);
                }
            }
        } catch (MessagingException | ZetraException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail ao servidor para alertar de inclusão de novo contrato oriundo de proposta de leilão aceita.
     * @param adeCodigo do novo contrato incluído.
     * @param codigoAutorizacao código de autorização da solicitação para confirmação do contrato
     * @param senhaAutorizacao senha para ser consumida na confirmação do contrato
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailSerPropostaLeilaoAprovada(String adeCodigo, String codigoAutorizacao, String senhaAutorizacao, PropostaLeilaoSolicitacao plsVencedora, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject adeTO = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);
            final String serEmail = (String) adeTO.getAttribute(Columns.SER_EMAIL);

            if (!TextHelper.isNull(serEmail)) {
                final String qtdDiasConcretizarLeilao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_PARA_SER_CONCRETIZAR_LEILAO, responsavel);

                final StringBuilder textoSenha = new StringBuilder();
                if (!TextHelper.isNull(codigoAutorizacao)) {
                    textoSenha.append("<br/>\n<b>").append(ApplicationResourcesHelper.getMessage("rotulo.email.codigo.autorizacao", responsavel, codigoAutorizacao)).append("</b>");
                }
                if (!TextHelper.isNull(senhaAutorizacao)) {
                    textoSenha.append("<br/>\n<b>").append(ApplicationResourcesHelper.getMessage("rotulo.email.senha.servidor.autorizacao", responsavel, senhaAutorizacao)).append("</b>");
                }
                final String cseNome = getCseNome(responsavel);
                // Define o titulo do email
                final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("mensagem.email.servidor.proposta.aprovada.leilao", responsavel), responsavel);
                // Define o corpo do email
                final StringBuilder corpoEmail = new StringBuilder().append(ApplicationResourcesHelper.getMessage("mensagem.email.abaixo.seguem.dados.consignacao", responsavel)).append(gerarTextoDetalheContratoParaEmail(adeTO, cseNome, responsavel)).append(textoSenha.append("<br/>\n<br/>\n").append(ApplicationResourcesHelper.getMessage("mensagem.email.servidor.proposta.aprovada.leilao.instrucoes", responsavel, qtdDiasConcretizarLeilao)).append("<br/>\n<br/>\n").toString());

                final boolean enviaEmailCsaVencedoraLeilao = ParamSist.getBoolParamSist(CodedValues.TPC_ENVIAR_EMAIL_CONSIGNATARIA_VENCEDORA_DO_LEILAO, responsavel);
                if (!enviaEmailCsaVencedoraLeilao) {
                    corpoEmail.append(ApplicationResourcesHelper.getMessage("mensagem.email.servidor.proposta.aprovada.leilao.acesse.web", responsavel, qtdDiasConcretizarLeilao)).append("<br/>\n<br/>\n");
                }

                corpoEmail.append(plsVencedora.getPlsTxtContatoCsa()).append("<br/>\n<br/>\n");

                if (ParamSist.getBoolParamSist(CodedValues.TPC_ENVIAR_INFOS_CSA_NO_EMAIL_SER_PROPOSTA_LEILAO_APROVADA, responsavel)) {
                    final String csaNome = plsVencedora.getConsignataria().getCsaNome();
                    final String cet = plsVencedora.getPlsTaxaJuros() != null ? NumberHelper.format(plsVencedora.getPlsTaxaJuros().doubleValue(), NumberHelper.getLang()) : null;
                    final String csaEmailContato = plsVencedora.getConsignataria().getCsaEmailContato();
                    final String csaWhatsapp = plsVencedora.getConsignataria().getCsaWhatsapp();
                    final String csaTxtContato = plsVencedora.getConsignataria().getCsaTxtContato();

                    if (!TextHelper.isNull(csaNome)) {
                        corpoEmail.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)).append(": ").append(csaNome);
                    }
                    if (!TextHelper.isNull(cet)) {
                        corpoEmail.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.cet.singular", responsavel)).append(": ").append(cet);
                    }
                    if (!TextHelper.isNull(csaEmailContato)) {
                        corpoEmail.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.email", responsavel)).append(": ").append(csaEmailContato);
                    }
                    if (!TextHelper.isNull(csaWhatsapp)) {
                        corpoEmail.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.whatsapp", responsavel)).append(": ").append(csaWhatsapp);
                    }
                    if (!TextHelper.isNull(csaTxtContato)) {
                        corpoEmail.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.telefone", responsavel)).append(": ").append(csaTxtContato);
                    }


                    try {
                        final LeilaoSolicitacaoController leilaoSolicitacaoController = ApplicationContextProvider.getApplicationContext().getBean(LeilaoSolicitacaoController.class);
                        final ConsignatariaController consingnatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                        final List<TransferObject> lstRejeitadas = leilaoSolicitacaoController.lstPropostaLeilaoSolicitacao(plsVencedora.getAdeCodigo(), null, StatusPropostaEnum.REJEITADA.getCodigo(), false, responsavel);

                        if ((lstRejeitadas != null) && !lstRejeitadas.isEmpty()) {
                            corpoEmail.append("<br/>\n<br/>\n").append(ApplicationResourcesHelper.getMessage("mensagem.email.abaixo.seguem.propostas.leilao.rejeitadas", responsavel));

                            for (final TransferObject propostaRejeitada: lstRejeitadas) {
                                final String csaCodigoPropostaRejeitada = (String) propostaRejeitada.getAttribute(Columns.CSA_CODIGO);
                                final String csaNomePropostaRejeitada = (String) propostaRejeitada.getAttribute(Columns.CSA_NOME);
                                final String cetPropostaRejeitada = propostaRejeitada.getAttribute(Columns.PLS_TAXA_JUROS) != null ? NumberHelper.format(((BigDecimal) propostaRejeitada.getAttribute(Columns.PLS_TAXA_JUROS)).doubleValue(), NumberHelper.getLang()) : null;

                                final ConsignatariaTransferObject csaPropostaRejeitada = consingnatariaController.findConsignataria(csaCodigoPropostaRejeitada, responsavel);
                                final String csaEmailContatoPropostaRejeitada = csaPropostaRejeitada.getCsaEmailContato();
                                final String csaWhatsappPropostaRejeitada = csaPropostaRejeitada.getCsaWhatsapp();
                                final String csaTxtContatoPropostaRejeitada = csaPropostaRejeitada.getCsaTxtContato();

                                if (!TextHelper.isNull(csaNomePropostaRejeitada)) {
                                    corpoEmail.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)).append(": ").append(csaNomePropostaRejeitada);
                                }
                                if (!TextHelper.isNull(cetPropostaRejeitada)) {
                                    corpoEmail.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.cet.singular", responsavel)).append(": ").append(cetPropostaRejeitada);
                                }
                                if (!TextHelper.isNull(csaEmailContatoPropostaRejeitada)) {
                                    corpoEmail.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.email", responsavel)).append(": ").append(csaEmailContatoPropostaRejeitada);
                                }
                                if (!TextHelper.isNull(csaWhatsappPropostaRejeitada)) {
                                    corpoEmail.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.whatsapp", responsavel)).append(": ").append(csaWhatsappPropostaRejeitada);
                                }
                                if (!TextHelper.isNull(csaTxtContatoPropostaRejeitada)) {
                                    corpoEmail.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.telefone", responsavel)).append(": ").append(csaTxtContatoPropostaRejeitada);
                                }
                                corpoEmail.append("<br/>\n<br/>\n");
                            }
                        }
                    } catch (final ZetraException ex) {
                        LOG.error(ex.getMessage(), ex);
                    }
                }

                // Envia o email.
                final MailHelper mailHelper = new MailHelper();
                mailHelper.send(serEmail, null, null, titulo, corpoEmail.toString().toString(), null, null);
            }
        } catch (MessagingException | AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail de notificação que não foi possível realizar oferta automática em um leilão pois o limite mínimo foi alcançado.
     *
     * @param adeCodigo
     * @param melhorTaxa
     * @param taxaLimite
     * @param decremento
     * @param dataFechamento
     * @param email
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailCsaOfertaAutLimiteMinSuperado(String adeCodigo, BigDecimal melhorTaxa, BigDecimal taxaLimite, BigDecimal decremento, Date dataFechamento, String email, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);
            final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();

            final String taxaLimiteStr = NumberHelper.format(taxaLimite.doubleValue(), NumberHelper.getLang());
            final String decrementoStr = NumberHelper.format(decremento.doubleValue(), NumberHelper.getLang());
            final String melhorTaxaStr = NumberHelper.format(melhorTaxa.doubleValue(), NumberHelper.getLang());

            final String cseNome = getCseNome(responsavel);
            final String rseMatricula = ade.getAttribute(Columns.RSE_MATRICULA) != null ? ade.getAttribute(Columns.RSE_MATRICULA).toString() : "";
            final String serNome =  ade.getAttribute(Columns.SER_NOME) != null ? ade.getAttribute(Columns.SER_NOME).toString() : "";
            final String serCpf = ade.getAttribute(Columns.SER_CPF) != null ? ade.getAttribute(Columns.SER_CPF).toString() : "";

            // Define o titulo do email
            final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("mensagem.email.oferta.automatica.leilao.taxa.min.superada", responsavel), responsavel);

            // Define o corpo do email
            final String corpoEmail  = ApplicationResourcesHelper.getMessage("mensagem.email.oferta.automatica.leilao.taxa.min.superada.detalhe", responsavel, taxaLimiteStr, decrementoStr, adeNumero, melhorTaxaStr) + "<br>\n"
                    + "<br>\n" + ApplicationResourcesHelper.getMessage("mensagem.email.seguem.dados.leilao.solicitacao", responsavel)
                    + "<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.servidor", responsavel, rseMatricula, serNome)
                    + "<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.cpf", responsavel, serCpf)
                    + "<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.data.fechamento.leilao", responsavel, DateHelper.format(dataFechamento, LocaleHelper.getDateTimePattern()))
                    + "<br/>\n<br/>\n";

            // Envia o email.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(email, null, null, titulo, corpoEmail.toString(), null, null);

        } catch (AutorizacaoControllerException | MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }


    /**
     * Envia e-mail para as consignatárias informando sobre a inclusão de um novo leilão dentro dos filtros criados
     * @param adeCodigo
     * @param responsavel
     * @return
     * @throws ViewHelperException
     */
    public static void enviarEmailNotificacaoNovoLeilaoByFiltro(SolicitacaoAutorizacao solicitacao, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final LeilaoSolicitacaoController leilaoSolicitacaoController = ApplicationContextProvider.getApplicationContext().getBean(LeilaoSolicitacaoController.class);
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();

            final List<TransferObject> filtros = leilaoSolicitacaoController.listarFiltrosByAdeCodigo(solicitacao.getAutDesconto().getAdeCodigo(), responsavel);

            final TransferObject ade = adeDelegate.buscaAutorizacao(solicitacao.getAutDesconto().getAdeCodigo(), responsavel);

            final List<TransferObject> lstPls = leilaoSolicitacaoController.lstPropostaLeilaoSolicitacao(ade.getAttribute(Columns.ADE_CODIGO).toString(), null, null, false, responsavel);

            // Nesse momento, somente 1 proposta pode estar ativa
            if ((lstPls !=null) && (lstPls.isEmpty() || (lstPls.size() > 1))) {
                throw new LeilaoSolicitacaoControllerException("mensagem.erro.leilao.envio.notificacao.filtro.consignataria", responsavel);
            }

            final TransferObject proposta = lstPls.get(0);

            if ((filtros != null) && !filtros.isEmpty()) {
                for (final TransferObject filtro : filtros) {
                    try {
                        final String cseNome = getCseNome(responsavel);
                        final String dataAbertura = DateHelper.format(solicitacao.getSoaData(), LocaleHelper.getDateTimePattern());
                        final String dataValidade = !TextHelper.isNull(solicitacao.getSoaDataValidade()) ? DateHelper.format(solicitacao.getSoaDataValidade(), LocaleHelper.getDateTimePattern()) : ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.solicitacao.data.fechamento.nao.definida", responsavel);
                        final String adeValorLiq = NumberHelper.format(((BigDecimal) proposta.getAttribute(Columns.PLS_VALOR_LIBERADO)).doubleValue(), NumberHelper.getLang());
                        final String adeValor = NumberHelper.format(((BigDecimal) proposta.getAttribute(Columns.PLS_VALOR_PARCELA)).doubleValue(), NumberHelper.getLang());

                        final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                        final String serNome = ade.getAttribute(Columns.SER_NOME).toString();
                        final String rseMatricula = ade.getAttribute(Columns.RSE_MATRICULA).toString();
                        final String serCpf = ade.getAttribute(Columns.SER_CPF).toString();
                        String posto = null;

                        if (ShowFieldHelper.showField(FieldKeysConstants.FILTRO_PESQUISA_POSTO_SERVIDOR, responsavel)) {
                            posto = ade.getAttribute(Columns.POS_DESCRICAO) != null ? ade.getAttribute(Columns.POS_DESCRICAO).toString() : "";
                        }

                        // Valor Taxa Juros
                        final String adeTaxaJurosReal = NumberHelper.format(((BigDecimal) proposta.getAttribute(Columns.PLS_TAXA_JUROS)).doubleValue(), NumberHelper.getLang()) + " %";

                        // Define rótulo para o campo de Taxa de Juros/CET/Coeficiente
                        final String rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.proposta.leilao.solicitacao.proposta.taxa.informada", responsavel);

                        final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.titulo.alerta.novo.leilao.filtro", responsavel, dataAbertura, dataValidade), responsavel);
                        final StringBuilder corpoEmail = new StringBuilder();
                        corpoEmail.append(ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.novo.leilao.aberto.filtro", responsavel)).append("<br><br>");

                        corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.filtro.leilao.singular", responsavel)).append("</b> : ");
                        corpoEmail.append(filtro.getAttribute(Columns.FLS_DESCRICAO).toString()).append("<br>");

                        corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.proposta.leilao.solicitacao.numero", responsavel)).append("</b> : ");
                        corpoEmail.append(adeNumero).append("<br>");
                        corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.solicitacao.valor.liberado", responsavel)).append("</b> : ");
                        corpoEmail.append(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)).append(" ").append(adeValorLiq).append("<br>");
                        corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.solicitacao.valor.prestacao", responsavel)).append("</b> : ");
                        corpoEmail.append(ApplicationResourcesHelper.getMessage("rotulo.moeda", responsavel)).append(" ").append(adeValor).append("<br>");
                        corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.prazo", responsavel)).append("</b> : ");
                        corpoEmail.append(proposta.getAttribute(Columns.PLS_PRAZO)).append("<br>");
                        corpoEmail.append("<b>").append(rotuloTaxa).append("</b> : ");
                        corpoEmail.append(adeTaxaJurosReal).append("<br>");
                        corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.solicitacao.data.abertura", responsavel)).append("</b> : ");
                        corpoEmail.append(dataAbertura).append("<br>");
                        corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.acompanhamento.leilao.solicitacao.data.fechamento", responsavel)).append("</b> : ");
                        corpoEmail.append(dataValidade).append("<br><br>");
                        corpoEmail.append(ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.dados.servidor", responsavel)).append("<br><br>");

                        corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.nome", responsavel)).append("</b> : ");
                        corpoEmail.append(serNome).append("<br>");
                        corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel)).append("</b> : ");
                        corpoEmail.append(serCpf).append("<br>");
                        corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.matricula.singular", responsavel)).append("</b> : ");
                        corpoEmail.append(rseMatricula).append("<br>");

                        if (posto != null) {
                            corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.posto", responsavel)).append("</b> : ");
                            corpoEmail.append(posto).append("<br>");
                        }

                        final String email = filtro.getAttribute(Columns.FLS_EMAIL_NOTIFICACAO) != null ? filtro.getAttribute(Columns.FLS_EMAIL_NOTIFICACAO).toString() : null;

                        if (!TextHelper.isNull(email)) {
                            // Envia o e-mail.
                            new MailHelper().send(email, null, null, titulo, corpoEmail.toString(), null);
                        }
                    } catch (final Exception ex) {
                        // caso algum filtro dê algum erro continua o processo enviando o email dos outros filtros
                        LOG.error(ex.getMessage(), ex);
                        continue;
                    }
                }
            }

        } catch (LeilaoSolicitacaoControllerException | AutorizacaoControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia email para consignatária que possui taxa superior ao limite cadastrado.
     *
     * @param lstTaxas
     * @param limiteTaxaJuros
     * @param svcCodigo
     * @param dataFimVig
     * @param responsavel
     * @throws ZetraException
     * @throws MessagingException
     */
    public static final void enviarEmailTaxaSuperiorLimite(List<TransferObject> lstTaxas, CustomTransferObject limiteTaxaJuros, List<TransferObject> lstRegraJurosSuperior, String svcCodigo, Date dataFimVig, boolean aplicarRegraCETTaxaJuros,AcessoSistema responsavel) throws ZetraException, MessagingException {

        // Retorna caso não exista taxa superior ao limite cadastrado
        if(((lstTaxas == null) || lstTaxas.isEmpty()) && ((lstRegraJurosSuperior == null) || lstRegraJurosSuperior.isEmpty())){
            return;
        }

        // Agrupa taxas superiores ao limite informado por consignatária
        final Map<String, List<TransferObject>> consignatariasPorTaxa = new HashMap<>();
        for (final TransferObject taxa : lstTaxas) {
            final String csaCodigo = (String) taxa.getAttribute(Columns.CSA_CODIGO);

            List<TransferObject> taxasPorConsignataria = consignatariasPorTaxa.get(csaCodigo);
            if (taxasPorConsignataria == null) {
                taxasPorConsignataria = new ArrayList<>();
                consignatariasPorTaxa.put(csaCodigo, taxasPorConsignataria);
            }

            taxasPorConsignataria.add(taxa);
        }

        //Agrupo regras de juros superiores ao limite informado por consignatária
        final Map<String, List<TransferObject>> consignatariasRegraTaxaJuros = new HashMap<>();
        if(aplicarRegraCETTaxaJuros) {
            for (final TransferObject regraJurosSuperior : lstRegraJurosSuperior) {
                final String csaCodigo = (String) regraJurosSuperior.getAttribute(Columns.CSA_CODIGO);

                List<TransferObject> regraTaxaJurosPorConsignataria = consignatariasRegraTaxaJuros.get(csaCodigo);
                if (regraTaxaJurosPorConsignataria == null) {
                    regraTaxaJurosPorConsignataria = new ArrayList<>();
                    consignatariasRegraTaxaJuros.put(csaCodigo, regraTaxaJurosPorConsignataria);
                }

                regraTaxaJurosPorConsignataria.add(regraJurosSuperior);
            }
        }


        final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

        final String nomeSistema = JspHelper.getNomeSistema(responsavel);
        // Dados necessários para envio do e-mail
        final String cseNome = getCseNome(responsavel);
        // Define o titulo do E-mail
        final String titulo = ApplicationResourcesHelper.getMessage("rotulo.email.alteracao.tabela.taxas.limite", responsavel, nomeSistema, cseNome);

        final ServicoDelegate svcDelegate = new ServicoDelegate();
        final TransferObject servico = svcDelegate.findServico(svcCodigo);
        final String svcDescricao = (servico != null) && !TextHelper.isNull(servico.getAttribute(Columns.SVC_DESCRICAO)) ? servico.getAttribute(Columns.SVC_DESCRICAO).toString() : "";


        //Consignatarias que receberao o email
        final List<String> consignatarias = new ArrayList<>(consignatariasPorTaxa.keySet());
        for(final String csaCodigo : consignatariasRegraTaxaJuros.keySet()) {
            if(!consignatarias.contains(csaCodigo)) {
                consignatarias.add(csaCodigo);
            }
        }

        for (final String csaCodigo : consignatarias) {
            // Recupera o endereço de e-mail do cadastro da consignatária.
            final ConsignatariaTransferObject consignataria = new ConsignatariaDelegate().findConsignataria(csaCodigo, responsavel);
            final String emailDestino = consignataria.getCsaEmail();
            final String csaNome = !TextHelper.isNull(consignataria.getCsaNomeAbreviado()) ? consignataria.getCsaNomeAbreviado() : consignataria.getCsaNome();

            // Se não tem e-mail não faz nada
            if (TextHelper.isNull(emailDestino)) {
                continue;
            }

            // Texto Comum para todas as mensagems
            final StringBuilder corpoMensagem = new StringBuilder();
            corpoMensagem.append(ApplicationResourcesHelper.getMessage("mensagem.email.tabela.taxas.limite.alterada.atualize.dados", responsavel, DateHelper.toDateTimeString(dataFimVig)));

            corpoMensagem.append("<br>");
            corpoMensagem.append("<br><b>").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)).append(":</b> ").append(csaNome);
            corpoMensagem.append("<br><b>").append(ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel)).append(":</b> ").append(svcDescricao);

            corpoMensagem.append("<br><br><b>" + ApplicationResourcesHelper.getMessage("mensagem.email.novo.limite.cadastrado", responsavel) + ":</b><br>");
            corpoMensagem.append("<table border=\"1\">");
            corpoMensagem.append("<tr><td><b>" + ApplicationResourcesHelper.getMessage("rotulo.prazo.singular", responsavel) + "</b></td><td><b>" + ApplicationResourcesHelper.getMessage("rotulo.email.valor.maximo.permitido", responsavel) + "</b></td></tr>");

            final BigDecimal cftVlrLimite = new BigDecimal(limiteTaxaJuros.getAttribute(Columns.LTJ_JUROS_MAX).toString());
            final String prazoLimite = limiteTaxaJuros.getAttribute(Columns.LTJ_PRAZO_REF).toString();
            if (cftVlrLimite.signum() > 0) {
                corpoMensagem.append("<tr><td>" + prazoLimite + "</td><td>" + NumberHelper.format(cftVlrLimite.doubleValue(), NumberHelper.getLang()) + "</td></tr>");
            } else {
                corpoMensagem.append("<tr><td>" + prazoLimite + "</td><td>" + ApplicationResourcesHelper.getMessage("rotulo.email.ilimitado", responsavel) + "</td></tr>");
            }
            corpoMensagem.append("</table>");

            corpoMensagem.append("<br><br><b>" + ApplicationResourcesHelper.getMessage("mensagem.email.prazos.acima.limite.permitido.sao", responsavel) + ":</b><br>");

            corpoMensagem.append("<br/>\n<br/>\n");
            corpoMensagem.append("<table border=\"1\">");
            corpoMensagem.append("<tr>");
            corpoMensagem.append("<td><b>").append(ApplicationResourcesHelper.getMessage("rotulo.prazo.singular", responsavel)).append("</b></td>");
            if (temCET) {
                corpoMensagem.append("<td><b>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel)).append("</b></td>");
            } else {
                corpoMensagem.append("<td><b>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel)).append("</b></td>");
            }
            corpoMensagem.append("</tr>");

            final List<TransferObject> taxas = consignatariasPorTaxa.get(csaCodigo);
            if(!TextHelper.isNull(taxas)) {
                for (final TransferObject taxa : taxas) {
                    final BigDecimal cftVlr = new BigDecimal(taxa.getAttribute(Columns.CFT_VLR).toString());
                    final String prazo = taxa.getAttribute(Columns.PRZ_VLR).toString();
                    corpoMensagem.append("<tr>");
                    corpoMensagem.append("<td>").append(prazo + "</td>");
                    corpoMensagem.append("<td>").append(NumberHelper.format(cftVlr.doubleValue() - 0.01, NumberHelper.getLang()) + "</td>");
                    corpoMensagem.append("</tr>");
                }
            }
            corpoMensagem.append("</table>");
            corpoMensagem.append("<br/>\n<br/>\n");

            //Cria tabela que sera enviada no email com as regras de taxa de juros acima do limite
            if(aplicarRegraCETTaxaJuros) {
                corpoMensagem.append("<br><br><b>" + ApplicationResourcesHelper.getMessage("mensagem.email.prazos.regra.taxa.juros.acima.limite.permitido.sao", responsavel) + ":</b><br>");

                corpoMensagem.append("<br/>\n<br/>\n");
                corpoMensagem.append("<table border=\"1\">");
                corpoMensagem.append("<tr>");
                corpoMensagem.append("<td><b>").append(ApplicationResourcesHelper.getMessage("rotulo.prazo.inicial", responsavel)).append("</b></td>");
                corpoMensagem.append("<td><b>").append(ApplicationResourcesHelper.getMessage("rotulo.prazo.final", responsavel)).append("</b></td>");
                corpoMensagem.append("<td><b>").append(ApplicationResourcesHelper.getMessage("rotulo.funcao.singular", responsavel)).append("</b></td>");
                corpoMensagem.append("<td><b>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel)).append("</b></td>");

                corpoMensagem.append("</tr>");

                final List<TransferObject> regrasTaxaJuros = consignatariasRegraTaxaJuros.get(csaCodigo);
                if(!TextHelper.isNull(regrasTaxaJuros)) {
                    for (final TransferObject regraTaxaJuros : regrasTaxaJuros) {
                        final BigDecimal dtjTaxaJuros = new BigDecimal(regraTaxaJuros.getAttribute(Columns.DTJ_TAXA_JUROS).toString());
                        final String funcao = !TextHelper.isNull(regraTaxaJuros.getAttribute(Columns.FUN_CODIGO)) ? (String) regraTaxaJuros.getAttribute(Columns.FUN_CODIGO) : "-";
                        final String dtjFaixaPrazoIni = regraTaxaJuros.getAttribute(Columns.DTJ_FAIXA_PRAZO_INI).toString();
                        final String dtjFaixaPrazoFim = regraTaxaJuros.getAttribute(Columns.DTJ_FAIXA_PRAZO_FIM).toString();
                        corpoMensagem.append("<tr>");
                        corpoMensagem.append("<td>").append(dtjFaixaPrazoIni + "</td>");
                        corpoMensagem.append("<td>").append(dtjFaixaPrazoFim + "</td>");
                        corpoMensagem.append("<td>").append(funcao + "</td>");
                        corpoMensagem.append("<td>").append(NumberHelper.format(dtjTaxaJuros.doubleValue() - 0.01, NumberHelper.getLang()) + "</td>");
                        corpoMensagem.append("</tr>");
                    }
                }
                corpoMensagem.append("</table>");
                corpoMensagem.append("<br/>\n<br/>\n");
            }

            // Envia os emails.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(emailDestino, null, null, titulo, corpoMensagem.toString(), null, null);
        }
    }


    /**
     * envia e-mail de alerta ao responsável pelo processamento da folha de que data de upload de arquivo de retorno no sistema está próximo
     * ou já passou
     * @param destinatario - e-mail do destinatário
     * @param dataPrevistaRetorno - data prevista da importação de retorno
     * @param alertaAntesDiaCorte - define se é um e-mail de alerta antes ou após a data prevista
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailAlertaEnvioArquivosFolha(String destinatario, Date dataPrevistaRetorno, boolean alertaAntesDiaCorte, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailAlertaEnvioArquivosFolhaCommand command = new EnviarEmailAlertaEnvioArquivosFolhaCommand();
        command.setDestinatario(destinatario);
        command.setDataPrevistaRetorno(dataPrevistaRetorno);
        command.setAlertaAntesDiaCorte(alertaAntesDiaCorte);
        command.setResponsavel(responsavel);
        command.execute();
    }

    /**
     * Envia e-mail para o servidor informando que a documentação enviada para aprovação de crédito eletrônico
     * foi reprovada. O corpo do e-mail deve conter os dados da solicitação e o motivo de reprovação informado
     * pela consignatária. O motivo informado pela consignatária deve especificar os documentos que foram reprovados.
     * @param destinatario E-mail do destinatário.
     * @param matricula Matrícula do servidor.
     * @param adeCodigo Código da autorização desconto.
     * @param motivoReprovacao Motivo de reprovação da documentação.
     * @param responsavel Responsável.
     * @throws ViewHelperException
     */
    public static final void enviarEmailReprovacaoDocumentacaoServidor(String destinatario, String adeCodigo, String motivoReprovacao, AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (TextHelper.isNull(destinatario)) {
                throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
            }

            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);

            final String cseNome = getCseNome(responsavel);
            final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();

            final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("rotulo.email.credito.eletronico.documentacao.reprovada", responsavel, adeNumero), responsavel);

            final StringBuilder corpo = new StringBuilder();
            corpo.append(ApplicationResourcesHelper.getMessage("mensagem.email.seguem.dados.solicitacao.credito.eletronico.documentacao.reprovada", responsavel) + ": <br/>\n<br/>");
            corpo.append(gerarTextoDetalheContratoParaEmail(ade, cseNome, responsavel));
            corpo.append("<br/>\n<b>" + ApplicationResourcesHelper.getMessage("rotulo.email.credito.eletronico.documentacao.reprovada.motivo", responsavel, motivoReprovacao) + "</b>");
            corpo.append("<br/>\n<br/>\n");

            final String texto = titulo + "<br/>\n<br/>\n" + corpo.toString();

            // Envia o e-mail.
            new MailHelper().send(destinatario, null, null, titulo, texto, null);
        } catch (MessagingException | AutorizacaoControllerException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * Envia e-mail com resumo de uma consignação
     * @param emailDestinatario
     * @param autdes
     * @param responsavel
     * @throws ViewHelperException
     */
    public final static void enviarEmailResumoConsignacao(String emailDestinatario, CustomTransferObject autdes, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(emailDestinatario)) {
            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
        }

        // Recupera os dados necessários para montar o texto do e-mail
        final String cseNome = getCseNome(responsavel);
        final String adeNumero = autdes.getAttribute(Columns.ADE_NUMERO).toString();

        // Define o título do E-mail
        final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("rotulo.email.resumo.consignacao", responsavel, adeNumero, responsavel.getNomeEntidade()), responsavel);

        final String corpo = "<b>"+ ApplicationResourcesHelper.getMessage("mensagem.email.abaixo.seguem.dados.consignacao", responsavel) + "</b>"
                     + gerarTextoDetalheContratoParaEmail(autdes, cseNome, responsavel) + "<br/>\n";

        final String texto = titulo + "<br/>\n<br/>\n" + corpo;

        // Envia o e-mail.
        try {
            new MailHelper().send(emailDestinatario, null, null, titulo, texto, null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * Envia e-mail com aviso de comunicações não lidas
     * @param emailDestinatario
     * @param autdes
     * @param responsavel
     * @throws ViewHelperException
     */
    public final static void enviarEmailComunicacaoNoaLida(String emailDestinatario, String destinatario, Long qtdComunicacoesNaoLidas, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(emailDestinatario)) {
            throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
        }

        // Recupera os dados necessários para montar o texto do e-mail
        final String cseNome = getCseNome(responsavel);

        // Define o título do E-mail
        final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("rotulo.email.aviso.comunicacoes.nao.lidas", responsavel), responsavel);

        String corpo = null;
        if (qtdComunicacoesNaoLidas == 1) {
            corpo = ApplicationResourcesHelper.getMessage("mensagem.email.comunicacao.nao.lidas", responsavel, destinatario);
        } else {
            corpo = ApplicationResourcesHelper.getMessage("mensagem.email.comunicacoes.nao.lidas", responsavel, destinatario, String.valueOf(qtdComunicacoesNaoLidas));
        }

        final String texto = titulo + "<br/>\n<br/>\n" + corpo;

        // Envia o e-mail.
        try {
            new MailHelper().send(emailDestinatario, null, null, titulo, texto, null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     *
     * @param diasParam
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailAlertaRetornoServidor(List<Integer> diasParam, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailAlertaRetornoServidorCommand command = new EnviarEmailAlertaRetornoServidorCommand();
        command.setDiasParam(diasParam);
        command.setResponsavel(responsavel);
        command.execute();
    }

    /**
     * Envia email para usuários caso haja contratos rejeitados pela folha
     * @param email
     * @param corpoEmail
     * @throws ViewHelperException
     */
    public static final void enviaEmailServidorContratosRejeitados (String email, String corpoEmail) throws ViewHelperException {
        final EnviarEmailServidorContratosRejeitadosCommand command = new EnviarEmailServidorContratosRejeitadosCommand();
        command.setEmail(email);
        command.setCorpoEmail(corpoEmail);
        command.execute();
    }

    /**
     * Envia por e-mail a informação do cancelamento do cadastro do servidor para as CSA's que possuem contratos com os mesmos.
     * @param rseCodigo
     * @param responsavel
     * @return
     */
    public static final void enviaEmailNotificacaoCsaCancelamentoCadastroServidor(TransferObject ade, AcessoSistema responsavel) {
        try {
            final EnviarEmailNotificacaoCsaCancelamentoCadastroServidorCommand command = new EnviarEmailNotificacaoCsaCancelamentoCadastroServidorCommand();
            command.setAde(ade);
            command.setResponsavel(responsavel);
            command.execute();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Monta o corpo do email e envia.
     * @param totalContratoAtivosMensalidadePlanoSaude
     * @param totalContratoAtivosMensalidadeOdotologico
     * @param listaContratoNoSistemaNaoConciliacaoPlanoSaude
     * @param listaContratoNaoSistemaNoConciliacaoPlanoSaude
     * @param listaContratoNoSistemaNaoConciliacaoOdontologico
     * @param listaContratoNaoSistemaNoConciliacaoOdontologico
     */
    public static final void enviaEmailConciliacaoBeneficio(String csaCodigo, int totalContratoAtivosMensalidadePlanoSaude, int totalContratoAtivosMensalidadeOdotologico,
            List<String> listaContratoNoSistemaNaoConciliacaoPlanoSaude, List<String> listaContratoNaoSistemaNoConciliacaoPlanoSaude,
            List<String> listaContratoNoSistemaNaoConciliacaoOdontologico, List<String> listaContratoNaoSistemaNoConciliacaoOdontologico, AcessoSistema responsavel) {
        try {
            // Define o título do E-mail
            String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.titulo.relatorio.conciliacao.beneficio", responsavel);

            if(titulo.contains("<NOME_SISTEMA>")) {
                titulo = titulo.replace("<NOME_SISTEMA>", JspHelper.getNomeSistema(responsavel)+" - ");
            }
            if(titulo.contains("<NOME_CONSIGNANTE>")) {
                titulo = titulo.replace("<NOME_CONSIGNANTE>", getCseNome(responsavel)+ " - ");
            }
            if(titulo.contains("<TITULO>")) {
                titulo = titulo.replace("<TITULO>", ApplicationResourcesHelper.getMessage("mensagem.email.assunto.relatorio.conciliacao.beneficio", responsavel));
            }

            //Texto com os detalhes do servidor.
            final ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            final ConsignatariaTransferObject csaTO = csaDelegate.findConsignataria(csaCodigo, responsavel);
            String textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.operadora.relatorio.conciliacao.beneficio", responsavel, csaTO.getCsaNome());
            textoGeral = "<b>" + textoGeral +"</b><br><br>\n" + ApplicationResourcesHelper.getMessage("mensagem.email.corpo.relatorio.conciliacao.beneficio", responsavel);

            if (textoGeral.contains("<TOTAL_MENSALIDADE_PLANO_SAUDE>")) {
                textoGeral = textoGeral.replace("<TOTAL_MENSALIDADE_PLANO_SAUDE>", String.valueOf(totalContratoAtivosMensalidadePlanoSaude));
            }

            if (textoGeral.contains("<TOTAL_MENSALIDADE_ODONTOLOGICO>")) {
                textoGeral = textoGeral.replace("<TOTAL_MENSALIDADE_ODONTOLOGICO>", String.valueOf(totalContratoAtivosMensalidadeOdotologico));
            }

            if (textoGeral.contains("<TOTAL_EXISTE_SISTEMA_NAO_EXISTE_ARQUIVO_SAUDE>")) {
                textoGeral = textoGeral.replace("<TOTAL_EXISTE_SISTEMA_NAO_EXISTE_ARQUIVO_SAUDE>", String.valueOf(listaContratoNoSistemaNaoConciliacaoPlanoSaude.size()));
            }
            StringBuilder tmp = geraTextoListaConciliacaoBeneficio(listaContratoNoSistemaNaoConciliacaoPlanoSaude, responsavel);
            textoGeral = textoGeral.replace("<LINHA_EXISTE_SISTEMA_NAO_EXISTE_ARQUIVO_SAUDE>", tmp.toString());

            if (textoGeral.contains("<TOTAL_NAO_EXISTE_SISTEMA_EXISTE_ARQUIVO_SAUDE>")) {
                textoGeral = textoGeral.replace("<TOTAL_NAO_EXISTE_SISTEMA_EXISTE_ARQUIVO_SAUDE>", String.valueOf(listaContratoNaoSistemaNoConciliacaoPlanoSaude.size()));
            }
            tmp = geraTextoListaConciliacaoBeneficio(listaContratoNaoSistemaNoConciliacaoPlanoSaude, responsavel);
            textoGeral = textoGeral.replace("<LINHA_NAO_EXISTE_SISTEMA_EXISTE_ARQUIVO_SAUDE>", tmp.toString());

            if (textoGeral.contains("<TOTAL_EXISTE_SISTEMA_NAO_EXISTE_ARQUIVO_ODONTOLOGICO>")) {
                textoGeral = textoGeral.replace("<TOTAL_EXISTE_SISTEMA_NAO_EXISTE_ARQUIVO_ODONTOLOGICO>", String.valueOf(listaContratoNoSistemaNaoConciliacaoOdontologico.size()));
            }
            tmp = geraTextoListaConciliacaoBeneficio(listaContratoNoSistemaNaoConciliacaoOdontologico, responsavel);
            textoGeral = textoGeral.replace("<LINHA_EXISTE_SISTEMA_NAO_EXISTE_ARQUIVO_ODONTOLOGICO>", tmp.toString());

            if (textoGeral.contains("<TOTAL_NAO_EXISTE_SISTEMA_EXISTE_ARQUIVO_ODONTOLOGICO>")) {
                textoGeral = textoGeral.replace("<TOTAL_NAO_EXISTE_SISTEMA_EXISTE_ARQUIVO_ODONTOLOGICO>", String.valueOf(listaContratoNaoSistemaNoConciliacaoOdontologico.size()));
            }
            tmp = geraTextoListaConciliacaoBeneficio(listaContratoNaoSistemaNoConciliacaoOdontologico, responsavel);
            textoGeral = textoGeral.replace("<LINHA_NAO_EXISTE_SISTEMA_EXISTE_ARQUIVO_ODONTOLOGICO>", tmp.toString());

            tmp = new StringBuilder();
            textoGeral = textoGeral.concat("<br>\n");

            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(EMAIL_OPERACAO_BENEFICIO, null, null, titulo, textoGeral, null);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * geraTextoListaConciliacaoBeneficio - Helper para gerar as linhas.
     * @param entrada
     * @return
     */
    private static StringBuilder geraTextoListaConciliacaoBeneficio(List<String> entrada, AcessoSistema responsavel) {
        final StringBuilder tmp = new StringBuilder();
        tmp.append("<br>\n");
        if ((entrada.size() > 0) && (entrada.size() < 101)) {
            for (final String linha : entrada) {
                tmp.append(linha);
                tmp.append("<br>\n");
            }
        } else if (entrada.size() > 101){
            tmp.append(ApplicationResourcesHelper.getMessage("mensagem.email.gentileza.consultar.relatorio.conciliacao.beneficio", responsavel));
        }

        return tmp;
    }

    /**
     * Envia no email os anexos do relatorio de Beneficiario e Concessoes de Beneficios
     * @param arquivosRelatorio
     * @param responsavel
     */
    public static final void enviaEmailRelatorioBeneficiariosEConcessoesDeBeneficios(List<String> arquivosRelatorio, AcessoSistema responsavel) {
        try {
            // Define o título do E-mail
            String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.titulo.relatorio.beneficiarios.e.concessoes.de.beneficios", responsavel);

            if(titulo.contains("<NOME_SISTEMA>")) {
                titulo = titulo.replace("<NOME_SISTEMA>", JspHelper.getNomeSistema(responsavel)+" - ");
            }
            if(titulo.contains("<NOME_CONSIGNANTE>")) {
                titulo = titulo.replace("<NOME_CONSIGNANTE>", getCseNome(responsavel)+ " - ");
            }
            if(titulo.contains("<TITULO>")) {
                titulo = titulo.replace("<TITULO>", ApplicationResourcesHelper.getMessage("mensagem.email.assunto.relatorio.beneficiarios.e.concessoes.de.beneficios", responsavel));
            }

            // Texto do copo do email
            final String textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.corpo.relatorio.beneficiarios.e.concessoes.de.beneficios", responsavel);

            // Enviando o email.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(EMAIL_OPERACAO_BENEFICIO, null, null, titulo, textoGeral, arquivosRelatorio);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    public static final void enviaEmailConclusaoRelatorioBeneficioDirf(Date periodo, String orgNome, boolean inconsistencia, AcessoSistema responsavel) {
        try {
            // Define o título do E-mail
            String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.titulo.relatorio.beneficios.consolidados.dirf", responsavel, orgNome);

            if(titulo.contains("<NOME_SISTEMA>")) {
                titulo = titulo.replace("<NOME_SISTEMA>", JspHelper.getNomeSistema(responsavel)+" - ");
            }
            if(titulo.contains("<NOME_CONSIGNANTE>")) {
                titulo = titulo.replace("<NOME_CONSIGNANTE>", getCseNome(responsavel)+ " - ");
            }
            if(titulo.contains("<TITULO>")) {
                titulo = titulo.replace("<TITULO>", ApplicationResourcesHelper.getMessage("mensagem.email.assunto.relatorio.beneficios.consolidados.dirf", responsavel));
            }

            // Texto do copo do email
            String textoGeral = null;
            if (!inconsistencia) {
                textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.corpo.relatorio.beneficios.consolidados.dirf", responsavel, DateHelper.format(periodo, "MM/yyyy"), orgNome);
            } else {
                textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.corpo.relatorio.beneficios.consolidados.dirf.com.inconsistencia", responsavel, DateHelper.format(periodo, "MM/yyyy"), orgNome);
            }

            // Enviando o email.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(EMAIL_OPERACAO_BENEFICIO, null, null, titulo, textoGeral, null);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail para a consignatária informando que os convênios com o serviço informado no parâmetro "svcDescricao"
     * foram bloqueados pois expiraram na data informada em "pscVlr".
     * @param csaNome
     * @param csaEmail
     * @param svcIdentificador
     * @param svcDescricao
     * @param pscVlr
     * @param responsavel
     */
    public static final void enviarEmailBloqueioConvenioExpirado(String csaNome, String csaEmail, String svcIdentificador, String svcDescricao, String pscVlr, AcessoSistema responsavel) {
        try {
            if (!TextHelper.isNull(csaEmail)) {
                final String titulo = gerarTituloEmail(null, ApplicationResourcesHelper.getMessage("mensagem.email.convenio.expirado.bloqueado.assunto", responsavel, svcDescricao), responsavel);
                final String texto = ApplicationResourcesHelper.getMessage("mensagem.email.convenio.expirado.bloqueado.corpo", responsavel, csaNome, svcDescricao, pscVlr) + "<br>\n"
                        + "<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.consignante.singular", responsavel) + ": " + getCseNome(responsavel)
                        + "<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.servico.singular", responsavel) + ": " + svcDescricao + " - " + svcIdentificador
                        ;

                new MailHelper().send(csaEmail, null, null, titulo, texto, null);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail para o servidor informando que foi solicitado o cancelamento do contrato de benefício
     * @param serEmail
     * @param bfcNome
     * @param benDescricao
     * @param responsavel
     */
    public static final void enviarEmailContratoBeneficioCancelamento(String serEmail, String bfcNome, String benDescricao, String bfcCpf, String csaNome, AcessoSistema responsavel) {
        try {
            final String titulo = gerarTituloEmail(null, ApplicationResourcesHelper.getMessage("mensagem.email.contrato.beneficio.cancelado.titulo", responsavel, benDescricao) , responsavel);
            final String texto = ApplicationResourcesHelper.getMessage("mensagem.email.contrato.beneficio.cancelado.corpo", responsavel, csaNome, bfcNome, bfcCpf, benDescricao) + "<br>\n";

            new MailHelper().send(serEmail, null, null, titulo, texto, null);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia no email os anexos do relatorio de Beneficiario e Concessoes de Beneficios
     * @param arquivosExportados
     * @param responsavel
     */
    public static final void enviaEmailExportacaoArquivosDeIntegracaoOperadora(List<String> arquivosExportados, AcessoSistema responsavel) {
        try {
            // Define o título do E-mail
            String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.titulo.exportacao.arquivo.operadora.beneficio", responsavel);

            if(titulo.contains("<NOME_SISTEMA>")) {
                titulo = titulo.replace("<NOME_SISTEMA>", JspHelper.getNomeSistema(responsavel)+" - ");
            }
            if(titulo.contains("<NOME_CONSIGNANTE>")) {
                titulo = titulo.replace("<NOME_CONSIGNANTE>", getCseNome(responsavel)+ " - ");
            }
            if(titulo.contains("<TITULO>")) {
                titulo = titulo.replace("<TITULO>", ApplicationResourcesHelper.getMessage("mensagem.email.assunto.exportacao.arquivo.operadora.beneficio", responsavel));
            }

            // Texto do copo do email
            final String textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.corpo.exportacao.arquivo.operadora.beneficio", responsavel);

            // Enviando o email.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(EMAIL_OPERACAO_BENEFICIO, null, null, titulo, textoGeral, arquivosExportados);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia no email retortando importação com sucesso ou não.
     * @param arquivosExportados
     * @param responsavel
     */
    public static final void enviaEmailImportacaoArquivosOperadora(String nomeArquivoRetorno, String nomeArquivoCritica, String csaNome, int totalLinhasCriticas, AcessoSistema responsavel) {
        try {
            // Define o título do E-mail
            String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.titulo.importacao.arquivo.operadora.beneficio", responsavel);

            if (titulo.contains("<NOME_SISTEMA>")) {
                titulo = titulo.replace("<NOME_SISTEMA>", JspHelper.getNomeSistema(responsavel) + " - ");
            }
            if (titulo.contains("<NOME_CONSIGNANTE>")) {
                titulo = titulo.replace("<NOME_CONSIGNANTE>", getCseNome(responsavel) + " - ");
            }
            if (titulo.contains("<TITULO>")) {
                if (totalLinhasCriticas != 0) {
                    titulo = titulo.replace("<TITULO>", ApplicationResourcesHelper.getMessage("mensagem.email.assunto.importacao.arquivo.operadora.beneficio.ressalva", responsavel));
                } else {
                    titulo = titulo.replace("<TITULO>", ApplicationResourcesHelper.getMessage("mensagem.email.assunto.importacao.arquivo.operadora.beneficio.sucesso", responsavel));

                }
            }

            titulo = titulo.replace("<CSA_NOME>", csaNome);

            // Texto do copo do email
            String textoGeral;
            if (totalLinhasCriticas != 0) {
                textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.corpo.importacao.arquivo.operadora.beneficio.ressalva", responsavel);
                textoGeral = textoGeral.replace("<NOME_ARQUIVO_CRITICA>", nomeArquivoCritica);
            } else {
                textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.corpo.importacao.arquivo.operadora.beneficio.sucesso", responsavel);
            }

            textoGeral = textoGeral.replace("<NOME_ARQUIVO>", nomeArquivoRetorno);

            // Enviando o email.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(EMAIL_OPERACAO_BENEFICIO, null, null, titulo, textoGeral, null);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Método que aciona a classe "Command" para que seja enviado o email de
     * notificação.
     */
    public static final void enviaNotificacaoUsuariosPorTempoInatividade() {

        final EnviarNotificacaoBloqueioUsuarioInatividadeCommand enviaNotificacaoUsuarioInatividade = new EnviarNotificacaoBloqueioUsuarioInatividadeCommand();
        try {
            enviaNotificacaoUsuarioInatividade.execute();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }

    }


    /**
     * Envia email para servidor/funcionário informando que sua consignação foi deferida.
     * @param csaNome
     * @param emailDestinatario
     * @param adeNumero
     * @param responsavel
     */
    public static final void enviaEmailNotificacaoConsignacaoDeferida(String csaNome, String emailDestinatario, String adeNumero, String corpo, AcessoSistema responsavel) {
        final EnviarEmailNotificacaoConsignacaoDeferidaCommand enviarEmailNotificacaoConsignacaoDeferida = new EnviarEmailNotificacaoConsignacaoDeferidaCommand();
        enviarEmailNotificacaoConsignacaoDeferida.setEmailDestinatario(emailDestinatario);
        enviarEmailNotificacaoConsignacaoDeferida.setCsaNome(csaNome);
        enviarEmailNotificacaoConsignacaoDeferida.setAdeNumero(adeNumero);
        enviarEmailNotificacaoConsignacaoDeferida.setCorpo(corpo);
        enviarEmailNotificacaoConsignacaoDeferida.setResponsavel(responsavel);

        try {
            enviarEmailNotificacaoConsignacaoDeferida.execute();
        } catch (final ViewHelperException ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia email de notificação referente à alteração do código da verba do convênio da CSA
     * @param email
     * @param corpoEmail
     * @throws ViewHelperException
     */
    public static final void enviaNotifAlterCodVerbaConvCSA (AcessoSistema responsavel, String email, List<TransferObject> conveniosAnteriores
            , List<TransferObject> conveniosAtuais, String svcCodigo, String svcDescricao) throws ViewHelperException {

        if (TextHelper.isNull(email)) {
            LOG.info("Não existe usuário a ser notificado.");
            return;
        }
        final String servico = svcDescricao;
        final StringBuilder listaAtivado = new StringBuilder();
        final StringBuilder listaDesativado = new StringBuilder();
        final StringBuilder listaCodVerbaAlterado = new StringBuilder();

        List<TransferObject> conveniosA = null;
        List<TransferObject> conveniosD = null;
        if ((conveniosAnteriores != null) && (conveniosAtuais!=null)){
            conveniosA = conveniosAnteriores;
            conveniosD = conveniosAtuais;

            CustomTransferObject ca = null;
            final Iterator<TransferObject> it = conveniosA.iterator();

            CustomTransferObject cd = null;
            final Iterator<TransferObject> itD = conveniosD.iterator();

            while (it.hasNext() && itD.hasNext()) {
                ca = (CustomTransferObject)it.next();
                cd = (CustomTransferObject)itD.next();
                //verifica se o estabelecimento/orgao anterior é igual ao estabelecimento/orgao atual
                final String estabAnt = ca.getAttribute(Columns.EST_IDENTIFICADOR).toString()+" - "+ca.getAttribute(Columns.ORG_NOME).toString()+" - "+ca.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                final String estabAtu = cd.getAttribute(Columns.EST_IDENTIFICADOR).toString()+" - "+cd.getAttribute(Columns.ORG_NOME).toString()+" - "+cd.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                if (estabAnt.equals(estabAtu)) {
                    //Monta lista de convênios ativados e desativados
                    final String scvCodigoAnt = ca.getAttribute("STATUS").toString();
                    final String scvCodigoNov = cd.getAttribute("STATUS").toString();
                    final String listaOrgao = ca.getAttribute(Columns.EST_IDENTIFICADOR).toString()+" - "+ca.getAttribute(Columns.ORG_NOME).toString()+" - "+ca.getAttribute(Columns.ORG_IDENTIFICADOR).toString();
                    if (!scvCodigoAnt.equals(scvCodigoNov)) {
                        if ("1".equals(scvCodigoNov)) {
                            listaAtivado.append("<br>" + listaOrgao + ": "+ ApplicationResourcesHelper.getMessage("rotulo.corpo.email.codVerba.servico.listaAtivado", responsavel) +"; ");
                        }else {
                            listaDesativado.append("<br>" + listaOrgao +": "+ ApplicationResourcesHelper.getMessage("rotulo.corpo.email.codVerba.servico.listaDesativado", responsavel) + "; ");
                        }
                    }
                    //monta lista de codigo de verba alterado
                    final String cnvCodVerbaAnt = ca.getAttribute(Columns.CNV_COD_VERBA) != null ? ca.getAttribute(Columns.CNV_COD_VERBA).toString() : "";
                    final String cnvCodVerbaRefAnt = ca.getAttribute(Columns.CNV_COD_VERBA_REF) != null ? ca.getAttribute(Columns.CNV_COD_VERBA_REF).toString() : "";
                    final String cnvCodVerbaDep = cd.getAttribute(Columns.CNV_COD_VERBA) != null ? cd.getAttribute(Columns.CNV_COD_VERBA).toString() : "";
                    final String cnvCodVerbaRefDep = cd.getAttribute(Columns.CNV_COD_VERBA_REF) != null ? cd.getAttribute(Columns.CNV_COD_VERBA_REF).toString() : "";
                    if (!cnvCodVerbaAnt.equals(cnvCodVerbaDep) || !cnvCodVerbaRefAnt.equals(cnvCodVerbaRefDep)) {
                        listaCodVerbaAlterado.append("<br>" + listaOrgao + ": " + ApplicationResourcesHelper.getMessage("rotulo.corpo.email.codVerba.servico.convenioAlterado.de", responsavel)
                          + " " + cnvCodVerbaAnt + " " + cnvCodVerbaRefAnt + " " + ApplicationResourcesHelper.getMessage("rotulo.corpo.email.codVerba.servico.convenioAlterado.para", responsavel)
                          + " " + cnvCodVerbaDep + " " + cnvCodVerbaRefDep +"; ");
                    }
                }
            }
            final EnviarEmailNotifAlterCodVerbaConvCsaCommand command = new EnviarEmailNotifAlterCodVerbaConvCsaCommand();
            command.setEmail(email);
            command.setServico(servico);
            command.setListaAtivado(listaAtivado);
            command.setListaDesativado(listaDesativado);
            command.setListaCodVerbaAlterado(listaCodVerbaAlterado);
            command.setResponsavel(responsavel);
            command.execute();
        }
    }

    /**
     * Envia e-mail de alerta de download não realizado de movimento financeiro.
     * ou já passou
     * @param listaArquivoMovFinSemDownload - listagem de arquivos de movimento financeiro que não foram baixados
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailDownloadNaoRealizadoMovFin(List<TransferObject> listaArquivoMovFinSemDownload, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailDownloadNaoRealizadoMovFinCommand command = new EnviarEmailDownloadNaoRealizadoMovFinCommand();
        command.setListaArquivoMovFinSemDownload(listaArquivoMovFinSemDownload);
        command.setResponsavel(responsavel);
        command.execute();
    }

    /**
     * Envia e-mail de notificação de envio ao sistema de novo boleto para o servidor
     * @param serEmail
     * @param remetente
     * @param servidor
     * @param conteudoArquivoPdf
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailNovoBoletoServidor(String serEmail, String remetente, TransferObject servidor, byte[] conteudoArquivoPdf, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailNovoBoletoServidorCommand command = new EnviarEmailNovoBoletoServidorCommand();
        command.setSerEmail(serEmail);
        command.setRemetente(remetente);
        command.setServidor(servidor);
        command.setConteudoArquivoPdf(conteudoArquivoPdf);
        command.setResponsavel(responsavel);
        command.execute();
    }

    /**
     * E-mail enviado a um usuário do sistema eConsig para validação do email
     * @param destinatario E-mail do destinatário.
     * @param usuNome - Nome do novo usuário criado
     * @param entNome - Nome da entidade à qual pertence o usuário
     * @param link link a que direciona à página de confirmação de validação
     * @param responsavel
     * @throws ViewHelperException
     */
     public static final void enviarEmailLinkValidarEmailusuario(String destinatario, String usuNome, String link, AcessoSistema responsavel) throws ViewHelperException {
         if (TextHelper.isNull(destinatario)) {
             throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
         }

         final String nomeSistema = JspHelper.getNomeSistema(responsavel);
         final String cseNome = getCseNome(responsavel);
         final String titulo =  ApplicationResourcesHelper.getMessage("rotulo.validaemail.titulo.assunto", responsavel, nomeSistema, cseNome);

         link = "<a href='" + link + "'>" + link + "</a>";

         final String corpo = ApplicationResourcesHelper.getMessage("mensagem.validaemail.link.confirmacao", responsavel, usuNome, link);

         final String texto = titulo + "<br/>\n<br/>\n" + corpo;

         // Envia o e-mail.
         try {
             new MailHelper().send(destinatario, null, null, titulo, texto, null);
         } catch (final MessagingException e) {
             throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
         }
     }


     public static final void enviarEmailErroReativacaoAutomatica(TransferObject ade, String emailDestinatario, String nomeDestinatario, String dataReativacaoAut, String mensagemErro, AcessoSistema responsavel) {
         try {
             if (TextHelper.isNull(emailDestinatario)) {
                 throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
             }

             if (ade != null) {
                 // Obtém os dados da consignação
                 final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                 final String cseNome = getCseNome(responsavel);

                 // Define o titulo do E-mail
                 final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("mensagem.email.erro.reativao.automatica.consignacao.resumo", responsavel, adeNumero), responsavel);

                 // Texto com os dados do contrato
                 final String texto = ApplicationResourcesHelper.getMessage("mensagem.email.erro.reativao.automatica.consignacao.detalhe", responsavel, nomeDestinatario, dataReativacaoAut, mensagemErro)
                         + "<br/>\n<br/>\n"
                         + ApplicationResourcesHelper.getMessage("mensagem.email.abaixo.seguem.dados.consignacao", responsavel)
                         + gerarTextoDetalheContratoParaEmail(ade, cseNome, responsavel)
                         + "<br/>\n<br/>\n"
                         ;

                 final MailHelper mailHelper = new MailHelper();
                 mailHelper.send(emailDestinatario, null, null, titulo, texto, null);
             }
         } catch (final Exception ex) {
             LOG.error(ex.getMessage(), ex);
         }
     }

     /**
      * DESENV-13123 - Envia email das ades autorizadas pelo servidor para o cse
      * @param nomeServidor
      * @param estabelecimento
      * @param orgao
      * @param cpf
      * @param tabela
      * @param responsavel
      * @throws ViewHelperException
      */
     public static final void enviarEmailAutorizarConsignacao(String nomeServidor, String estabelecimento, String orgao, String cpf, String tabela, AcessoSistema responsavel) throws ViewHelperException {
         try {
             final ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
             final ConsignanteTransferObject cse = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
             final String destinatario = cse.getCseEmailFolha();

             if (TextHelper.isNull(destinatario)) {
                 throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
             }

             final String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.autorizar.titulo", responsavel, nomeServidor);

             final String corpo = ApplicationResourcesHelper.getMessage("mensagem.email.autorizar.corpo", responsavel, estabelecimento, orgao, nomeServidor, cpf, tabela);

             // Envia o e-mail.
             new MailHelper().send(destinatario, null, null, titulo, corpo, null);
         } catch (final MessagingException e) {
             throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
         } catch (final ConsignanteControllerException ex) {
             LOG.error(ex.getMessage(), ex);
             throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
         }
     }

     /**
      * E-mail recuperação de senha usuário servidor
      * @param destinatario E-mail do destinatário.
      * @param serNome - Nome do Servidor
      * @param link - Link de Recuperação de Senha
      * @param responsavel
      * @throws ViewHelperException
      */

     public static final void enviarLinkRecuperacaoSenhaServidor(String serEmail, String serPrimeiroNome, String link, AcessoSistema responsavel) throws ViewHelperException {
         final EnviarLinkRecurepacaoSenhaServidorCommand command = new EnviarLinkRecurepacaoSenhaServidorCommand();
         command.setSerEmail(serEmail);
         command.setSerPrimeirNome(serPrimeiroNome);
         command.setLink(link);
         command.setResponsavel(responsavel);
         command.execute();
     }

     public static final void enviarCadastroSenhaServidor(String serEmail, String serPrimeiroNome, List<String> anexos, AcessoSistema responsavel) throws ViewHelperException {
         final EnviarEmailNotificacaoCadastroSenhaServidorCommand command = new EnviarEmailNotificacaoCadastroSenhaServidorCommand();
         command.setSerEmail(serEmail);
         command.setSerPrimeirNome(serPrimeiroNome);
         command.setResponsavel(responsavel);
         command.setAnexos(anexos);
         command.execute();
     }

     public static final void enviarEmailConfirmacaoOperacaoSensivel(EmailAlteracaoOperacaoSensivel email, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailAlteracaoOperacaoSensivelCommand command = new EnviarEmailAlteracaoOperacaoSensivelCommand();
        command.setEmail(email);
        command.setConfirmacaoOperacaoSensivel(true);
        command.setResponsavel(responsavel);
        command.execute();
    }

    public static final void enviarEmailReprovacaoOperacaoSensivel(EmailAlteracaoOperacaoSensivel email, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailAlteracaoOperacaoSensivelCommand command = new EnviarEmailAlteracaoOperacaoSensivelCommand();
        command.setEmail(email);
        command.setConfirmacaoOperacaoSensivel(false);
        command.setResponsavel(responsavel);
        command.execute();
    }

    /**
     * Envia por e-mail a informação de que houve erro na tentativa de executar o KYC pelo servidor.
     * @param rseCodigo
     * @param responsavel
     * @return
     */
    public static final void enviaEmailNotificacaoCsaErroKYC(String csaCodigo, String panNumber, String status, AcessoSistema responsavel) {
        try {
            final EnviarEmailNotificacaoCsaErroKYCCommand command = new EnviarEmailNotificacaoCsaErroKYCCommand();
            command.setCsaCodigo(csaCodigo);
            command.setPanNumber(panNumber);
            command.setStatus(status);
            command.setResponsavel(responsavel);
            command.execute();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail à consignatária identificada pelo parâmetro "csaCodigo" informando que uma nova consignação
     * foi criada para pagamento do saldo devedor da ADE representada pelo parâmetro "adeCodigo" utilizando a
     * verba rescisória do colaborador.
     * @param csaCodigo
     * @param adeCodigo
     * @param responsavel
     * @throws ZetraException
     * @throws MessagingException
     */
    public static final void enviarEmailCsaNovoContratoVerbaRescisoria(String csaCodigo, String adeCodigo, AcessoSistema responsavel) {
        try {
            // Busca as informações do novo contrato criado para abater o saldo devedor na verba rescisória
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);
            if (ade == null) {
                return;
            }

            // Recupera o endereço de e-mail da consignatária que deve realizar o deferimento
            final ConsignatariaTransferObject consignataria = new ConsignatariaDelegate().findConsignataria(csaCodigo, responsavel);
            final String emailDestino = consignataria.getCsaEmail();

            // Se não tem e-mail não faz nada
            if (TextHelper.isNull(emailDestino)) {
                return;
            }

            // Dados necessários para envio do e-mail
            final String rseMatricula = ade.getAttribute(Columns.RSE_MATRICULA) != null ? ade.getAttribute(Columns.RSE_MATRICULA).toString() : "";
            final String serNome =  ade.getAttribute(Columns.SER_NOME) != null ? ade.getAttribute(Columns.SER_NOME).toString() : "";
            final String serCpf = ade.getAttribute(Columns.SER_CPF) != null ? ade.getAttribute(Columns.SER_CPF).toString() : "";
            final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO) != null ? ade.getAttribute(Columns.ADE_NUMERO).toString() : "";
            final String adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? NumberHelper.format(((BigDecimal) ade.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang(), true) : "";
            final String adeData = ade.getAttribute(Columns.ADE_DATA) != null ? DateHelper.toDateTimeString((Date) ade.getAttribute(Columns.ADE_DATA)) : "";

            // Define o titulo do E-mail
            String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.titulo.novo.contrato.verba.rescisoria", responsavel);
            if(titulo.contains("<NOME_SISTEMA>")) {
                titulo = titulo.replace("<NOME_SISTEMA>", JspHelper.getNomeSistema(responsavel)+" - ");
            }
            if(titulo.contains("<NOME_CONSIGNANTE>")) {
                titulo = titulo.replace("<NOME_CONSIGNANTE>", getCseNome(responsavel)+ " - ");
            }
            if(titulo.contains("<TITULO>")) {
                titulo = titulo.replace("<TITULO>", ApplicationResourcesHelper.getMessage("mensagem.email.assunto.novo.contrato.verba.rescisoria", responsavel));
            }

            // Texto do corpo do e-mail
            String textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.corpo.novo.contrato.verba.rescisoria", responsavel);

            if (textoGeral.contains("<DETALHE_CONTRATO>")) {
                final StringBuilder detalhe = new StringBuilder();
                detalhe.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.servidor", responsavel, rseMatricula, serNome));
                detalhe.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.cpf", responsavel, serCpf));
                detalhe.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.numero.ade", responsavel, adeNumero));
                detalhe.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.data.inclusao", responsavel, adeData));
                detalhe.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.valor.prestacao", responsavel, adeVlr));
                detalhe.append("<br/>\n<br/>\n");
                textoGeral = textoGeral.replace("<DETALHE_CONTRATO>", detalhe.toString());
            }

            // Envia o email.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(emailDestino, null, null, titulo, textoGeral.toString(), null, null);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail ao servidor identificado pelo parâmetro "rseCodigo" informando que uma nova consignação
     * foi criada para pagamento do saldo devedor da ADE representada pelo parâmetro "adeCodigo" utilizando a
     * verba rescisória deste servidor
     * @param csaCodigo
     * @param adeCodigo
     * @param responsavel
     * @throws ZetraException
     * @throws MessagingException
     */
    public static final void enviarEmailSerNovoContratoVerbaRescisoria(String rseCodigo, String adeCodigo, AcessoSistema responsavel) {
        try {
            // Busca as informações do novo contrato criado para abater o saldo devedor na verba rescisória
            final PesquisarConsignacaoController pesquisarConsignacaoController = ApplicationContextProvider.getApplicationContext().getBean(PesquisarConsignacaoController.class);
            final TransferObject ade = pesquisarConsignacaoController.buscaAutorizacao(adeCodigo, responsavel);
            if (ade == null) {
                LOG.info("Não encontrado contrato para notificação");
                return;
            }

            // Recupera o endereço de e-mail da consignatária que deve realizar o deferimento
            final ServidorController servidorContorller = ApplicationContextProvider.getApplicationContext().getBean(ServidorController.class);
            final ServidorTransferObject servidor = servidorContorller.findServidorByRseCodigo(rseCodigo, responsavel);
            final String emailDestino = servidor.getSerEmail();

            // Se não tem e-mail não faz nada
            if (TextHelper.isNull(emailDestino)) {
                LOG.info("Não existe email cadastrado do registro servidor para envio: " + rseCodigo);
                return;
            }

            // Dados necessários para envio do e-mail
            final String rseMatricula = ade.getAttribute(Columns.RSE_MATRICULA) != null ? ade.getAttribute(Columns.RSE_MATRICULA).toString() : "";
            final String serNome =  ade.getAttribute(Columns.SER_NOME) != null ? ade.getAttribute(Columns.SER_NOME).toString() : "";
            final String serCpf = ade.getAttribute(Columns.SER_CPF) != null ? ade.getAttribute(Columns.SER_CPF).toString() : "";
            final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO) != null ? ade.getAttribute(Columns.ADE_NUMERO).toString() : "";
            final BigDecimal adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? (BigDecimal) ade.getAttribute(Columns.ADE_VLR) : BigDecimal.ZERO;
            final Date adeData = ade.getAttribute(Columns.ADE_DATA) != null ? (Date) ade.getAttribute(Columns.ADE_DATA) : null;

            final EnviarEmailNovoContratoVerbaRescisoriaSerCommand command = new EnviarEmailNovoContratoVerbaRescisoriaSerCommand();
            command.setEmail(emailDestino);
            command.setRseMatricula(rseMatricula);
            command.setSerNome(serNome);
            command.setSerCpf(serCpf);
            command.setAdeNumero(adeNumero);
            command.setAdeData(adeData);
            command.setAdeVlr(adeVlr);
            command.execute();

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail ao colaborador envolvido em um processo de rescisão de contrato de trabalho informando os dados
     * das consignações que possuem saldo devedor.
     * @param saldoDevedorTO
     * @param responsavel
     * @return
     */
    public static final void enviarEmailServidorDemitidoSaldoDevedor(String serEmail, List<TransferObject> contratosComSaldoDevedor, AcessoSistema responsavel) {
        try {
            if ((contratosComSaldoDevedor == null) || contratosComSaldoDevedor.isEmpty()) {
                return;
            }
            // Define o titulo do E-mail
            String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.titulo.colaborador.rescisao.saldo.devedor", responsavel);
            if(titulo.contains("<NOME_SISTEMA>")) {
                titulo = titulo.replace("<NOME_SISTEMA>", JspHelper.getNomeSistema(responsavel)+" - ");
            }
            if(titulo.contains("<NOME_CONSIGNANTE>")) {
                titulo = titulo.replace("<NOME_CONSIGNANTE>", getCseNome(responsavel)+ " - ");
            }
            if(titulo.contains("<TITULO>")) {
                titulo = titulo.replace("<TITULO>", ApplicationResourcesHelper.getMessage("mensagem.email.assunto.colaborador.rescisao.saldo.devedor", responsavel));
            }

            // Texto do corpo do e-mail
            String textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.corpo.colaborador.rescisao.saldo.devedor", responsavel);

            if (textoGeral.contains("<DETALHE_CONTRATO>")) {
                final StringBuilder detalhe = new StringBuilder();
                for (final TransferObject ade : contratosComSaldoDevedor) {
                    // Dados necessários para envio do e-mail
                    final String csaNome =  ade.getAttribute(Columns.CSA_NOME) != null ? ade.getAttribute(Columns.CSA_NOME).toString() : "";
                    final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO) != null ? ade.getAttribute(Columns.ADE_NUMERO).toString() : "";
                    final String adeVlr = ade.getAttribute(Columns.ADE_VLR) != null ? NumberHelper.format(((BigDecimal) ade.getAttribute(Columns.ADE_VLR)).doubleValue(), NumberHelper.getLang(), true) : "";
                    final String adeData = ade.getAttribute(Columns.ADE_DATA) != null ? DateHelper.toDateTimeString((Date) ade.getAttribute(Columns.ADE_DATA)) : "";

                    detalhe.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.numero.ade", responsavel, adeNumero));
                    detalhe.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.data.inclusao", responsavel, adeData));
                    detalhe.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.valor.prestacao", responsavel, adeVlr));
                    detalhe.append("<br>\n" + ApplicationResourcesHelper.getMessage("rotulo.email.consignataria", responsavel, csaNome));
                    detalhe.append("<br/>\n<br/>\n");
                }
                textoGeral = textoGeral.replace("<DETALHE_CONTRATO>", detalhe.toString());
            }

            // Envia o email.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(serEmail, null, null, titulo, textoGeral.toString(), null, null);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail e anexos para o caso de uso "mensagem de e-mail para a consignatária de destino para portabilidade"
     * @param mensagem
     * @param destinatario
     * @param arquivosAnexoEmail
     * @param responsavel
     */
    public static final void enviarEmailMensagemCsaCorPortabilidade(String mensagem, String destinatario, List<String> arquivosAnexoEmail, AcessoSistema responsavel) {
        try {
            // Define o título do E-mail
            String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.titulo.consignataria.destino.portabilidade", responsavel);

            if(titulo.contains("<NOME_SISTEMA>")) {
                titulo = titulo.replace("<NOME_SISTEMA>", JspHelper.getNomeSistema(responsavel)+" - ");
            }
            if(titulo.contains("<NOME_CONSIGNANTE>")) {
                titulo = titulo.replace("<NOME_CONSIGNANTE>", getCseNome(responsavel)+ " - ");
            }
            if(titulo.contains("<TITULO>")) {
                titulo = titulo.replace("<TITULO>", ApplicationResourcesHelper.getMessage("mensagem.email.assunto.comunicacao.portabilidade.contrato", responsavel));
            }

            // Enviando o email.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(destinatario, null, null, titulo, mensagem, arquivosAnexoEmail);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail o servidor quando a consignatária
     * deseja fazer um refinanciamento de parcelas ou redução.
     * @param saldoDevedorTO
     * @param responsavel
     * @return
     */
    public static final String enviarEmailRefinanciamentoParcela(String adeCodigo, String textoProposta, AcessoSistema responsavel) {
        try {

            // Busca o contrato informado
            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);
            if (ade != null) {
                // Verifica se o servidor possui e-mail cadastrado
                final String serEmail = ade.getAttribute(Columns.SER_EMAIL).toString();

                // Obtém os dados da consignação
                final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
                final String cseNome = getCseNome(responsavel);

                // Define o titulo do E-mail
                final String titulo = gerarTituloEmail(null, ApplicationResourcesHelper.getMessage("mensagem.email.saldo.devedor.refinanciamento.parcelas.titulo", responsavel, adeNumero), responsavel);

                // Busca os valores do saldo devedor cadastrados
                textoProposta += gerarTextoDetalheContratoParaEmail(ade,cseNome, responsavel) + "<br/>\n<br/>\n";

                // Define o texto do E-mail
                final String texto = "<br/>\n<br/>\n" + textoProposta;

                // Envia as mensagens.
                final MailHelper mailHelper = new MailHelper();
                mailHelper.send(serEmail, null, null, titulo, texto, null);
            }

            return "";
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return ex.getMessage();
        }
    }

    public static final String enviarEmailSuporteDuvidaChatbot(String nome, String email, String duvida, AcessoSistema responsavel) {
        try {
            final String emailSuporte = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel);
            if (!TextHelper.isNull(emailSuporte)) {
                // Define o titulo do E-mail
                final String titulo = gerarTituloEmail(null, ApplicationResourcesHelper.getMessage("mensagem.atendimento.chatbot.email.duvida.titulo", responsavel), responsavel);

                // Define o texto do E-mail
                //Texto contendo o comentário sobre o motivo do faq não ter sido útil.
                String textoGeral = "";
                if (!TextHelper.isNull(email)) {
                    textoGeral = ApplicationResourcesHelper.getMessage("", responsavel, nome, email, duvida);
                } else {
                    textoGeral = ApplicationResourcesHelper.getMessage("mensagem.atendimento.chatbot.email.duvida.texto.sem.email", responsavel, nome, duvida);
                }
                final String texto = titulo + "<br/>\n<br/>\n" + textoGeral;

                // Envia as mensagens.
                final MailHelper mailHelper = new MailHelper();
                mailHelper.send(emailSuporte, null, null, titulo, texto, null);
            }

            return "";
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return ex.getMessage();
        }
    }

    /**
     * Envia e-mail para o servidor informando o código para autorizar desconto parcial.
     * @param destinatario E-mail do destinatário.
     * @param codAutorizacao Código de autorização da solicitação.
     * @param responsavel Responsável.
     * @throws ViewHelperException
     */
    public static final void enviarEmailCodigoAutorizarDescontoParcialSer(String destinatario, String codVerificacao, AcessoSistema responsavel) throws ViewHelperException {
        try {
            if (TextHelper.isNull(destinatario)) {
                throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
            }

            final String cseNome = getCseNome(responsavel);
            final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("rotulo.email.codigo.verificacao.email", responsavel), responsavel);

            final StringBuilder corpo = new StringBuilder();
            corpo.append(ApplicationResourcesHelper.getMessage("mensagem.email.codigo.autorizar.desconto.parcial.email", responsavel) + ": <br/>\n<br/>");
            corpo.append("<br/>\n<b>" + ApplicationResourcesHelper.getMessage("rotulo.email.codigo.autorizacao", responsavel, codVerificacao) + "</b>");
            corpo.append("<br/>\n<br/>\n");

            final String texto = titulo + "<br/>\n<br/>\n" + corpo.toString();

            // Envia o e-mail.
            new MailHelper().send(destinatario, null, null, titulo, texto, null);
        } catch (final MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    public static final String enviarEmailAvaliacaoFaqNaoUtil(String nome, String email, String duvida, String entidade, String consignante,  AcessoSistema responsavel) {
        try {

            final String emailSuporte = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel);
            if (!TextHelper.isNull(emailSuporte)) {
                // Define o titulo do E-mail
            	//Comantário referente a avaliação do faq não útil feita pelo usuário.
                final String titulo = gerarTituloEmail(null, ApplicationResourcesHelper.getMessage("rotulo.email.avaliacao.faq.nao.util", responsavel), responsavel);

                // Define o texto do E-mail
                String textoGeral = "";
                if (!TextHelper.isNull(email)) {
                    textoGeral = ApplicationResourcesHelper.getMessage("mensagem.avaliacao.faq.email.duvida.texto", responsavel, nome, email, duvida);
                } else {
                    textoGeral = ApplicationResourcesHelper.getMessage("mensagem.avaliacao.faq.email.duvida.texto.sem.email", responsavel, nome, duvida);
                }
                final String texto = titulo + "<br/>\n<br/>\n" + textoGeral;

                // Envia as mensagens.
                final MailHelper mailHelper = new MailHelper();
                mailHelper.send(emailSuporte, null, null, titulo, texto, null);
            }

            return "";
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return ex.getMessage();
        }
    }

    /**
     * Envia e-mail à equipe de segurança, com destinatário cadastrado em parâmetro de sistema, informando os usuários e/ou entidades
     * que foram bloqueados automaticamente por motivo de segurança por ultrapassarem os limites de operações de liberação de margem.
     * @param servidoresAfetados
     * @param usuariosBloqueados
     * @param servidoresBloqueados
     * @param csaCodigo
     */
    public static final void enviarEmailBloqueioAutomaticoSegurancaLiberacaoMargem(List<TransferObject> servidoresAfetados, List<TransferObject> usuariosBloqueados, List<TransferObject> servidoresBloqueados, String csaCodigo, AcessoSistema responsavel) {
        try {
            if ((servidoresAfetados != null) && !servidoresAfetados.isEmpty()) {
                // Recupera o endereço de e-mail para destinatário da mensagem
                final String destinatario = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_NOTIFICACAO_SEGURANCA, responsavel);
                // Se não tem e-mail configurado para receber notificações, interrompe o envio
                if (TextHelper.isNull(destinatario)) {
                    return;
                }

                // Define o titulo do E-mail
                String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.titulo.notificacao.bloqueio.automatico.seguranca", responsavel);
                if(titulo.contains("<NOME_SISTEMA>")) {
                    titulo = titulo.replace("<NOME_SISTEMA>", JspHelper.getNomeSistema(responsavel) + " - ");
                }
                if(titulo.contains("<NOME_CONSIGNANTE>")) {
                    titulo = titulo.replace("<NOME_CONSIGNANTE>", getCseNome(responsavel) + " - ");
                }
                if(titulo.contains("<TITULO>")) {
                    titulo = titulo.replace("<TITULO>", ApplicationResourcesHelper.getMessage("mensagem.email.assunto.notificacao.bloqueio.automatico.seguranca", responsavel));
                }

                // Texto do corpo do e-mail
                String textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.corpo.notificacao.bloqueio.automatico.seguranca", responsavel);

                if (textoGeral.contains("<DETALHE_OPERACOES>")) {
                    final String detalheOperacoes = gerarTextoDetalheOperacoesLiberacaoMargem(servidoresAfetados, csaCodigo, responsavel);
                    textoGeral = textoGeral.replace("<DETALHE_OPERACOES>", detalheOperacoes.toString());
                }

                if (textoGeral.contains("<DETALHE_BLOQUEIO>")) {
                    final String detalheBloqueio = gerarTextoDetalheBloqueioAutomaticoSeguranca(usuariosBloqueados, servidoresBloqueados, responsavel);
                    textoGeral = textoGeral.replace("<DETALHE_BLOQUEIO>", detalheBloqueio);
                }
                // Envia o email.
                final MailHelper mailHelper = new MailHelper();
                mailHelper.send(destinatario, null, null, titulo, textoGeral.toString(), null, null);
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia e-mail à equipe de segurança, com destinatário cadastrado em parâmetro de sistema, informando os usuários e/ou entidades
     * que atingiram os limites de operações de liberação de margem para notificação.
     * @param servidoresAfetados
     * @param csaCodigo
     */
    public static final void enviarEmailNotificacaoLimiteOperacoesLiberacaoMargem(List<TransferObject> servidoresAfetados, String csaCodigo, AcessoSistema responsavel) {
        try {
            if ((servidoresAfetados != null) && !servidoresAfetados.isEmpty()) {
                // Recupera o endereço de e-mail para destinatário da mensagem
                final String destinatario = (String) ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_NOTIFICACAO_SEGURANCA, responsavel);
                // Se não tem e-mail configurado para receber notificações, interrompe o envio
                if (TextHelper.isNull(destinatario)) {
                    return;
                }

                // Define o titulo do E-mail
                String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.titulo.notificacao.bloqueio.automatico.seguranca", responsavel);
                if(titulo.contains("<NOME_SISTEMA>")) {
                    titulo = titulo.replace("<NOME_SISTEMA>", JspHelper.getNomeSistema(responsavel) + " - ");
                }
                if(titulo.contains("<NOME_CONSIGNANTE>")) {
                    titulo = titulo.replace("<NOME_CONSIGNANTE>", getCseNome(responsavel) + " - ");
                }
                if(titulo.contains("<TITULO>")) {
                    titulo = titulo.replace("<TITULO>", ApplicationResourcesHelper.getMessage("mensagem.email.assunto.notificacao.limite.operacoes.liberacao.margem", responsavel));
                }

                // Texto do corpo do e-mail
                String textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.corpo.notificacao.limite.operacao.liberacao.margem", responsavel);

                if (textoGeral.contains("<DETALHE_OPERACOES>")) {
                    final String detalheOperacoes = gerarTextoDetalheOperacoesLiberacaoMargem(servidoresAfetados, csaCodigo, responsavel);
                    textoGeral = textoGeral.replace("<DETALHE_OPERACOES>", detalheOperacoes.toString());
                }

                // Envia o email.
                final MailHelper mailHelper = new MailHelper();
                mailHelper.send(destinatario, null, null, titulo, textoGeral.toString(), null, null);
            }

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Método auxiliar para geração do texto do e-mail com o detalhe dos usuários e entidades bloqueados.
     * @param usuCodigosBloqueados
     * @param rseCodigosBloqueados
     * @param responsavel
     * @return String com o texto informativo dos usuários/entidades bloqueados
     */
    private static String gerarTextoDetalheBloqueioAutomaticoSeguranca(List<TransferObject> usuariosBloqueados, List<TransferObject> servidoresBloqueados, AcessoSistema responsavel) {
        final StringBuilder detalhe = new StringBuilder();
        if ((usuariosBloqueados != null) && !usuariosBloqueados.isEmpty()) {
            detalhe.append("<br><b>\n<b>" + ApplicationResourcesHelper.getMessage("mensagem.email.detalhe.usuarios.bloqueio.automatico.seguranca.liberacao.margem", responsavel) + "</b>");
            detalhe.append("<br>\n");
            detalhe.append("<table border=\"1\">");
            detalhe.append("<tr>");
            detalhe.append("<td><b>" + ApplicationResourcesHelper.getMessage("rotulo.usuario.nome", responsavel) + "</b></td>");
            detalhe.append("<td><b>" + ApplicationResourcesHelper.getMessage("rotulo.usuario.login", responsavel) + "</b></td>");
            detalhe.append("</tr>");
            for (final TransferObject to : usuariosBloqueados) {
                final String usuNome = (String) to.getAttribute(Columns.USU_NOME);
                final String usuLogin = (String) to.getAttribute(Columns.USU_LOGIN);
                detalhe.append("<tr>");
                detalhe.append("<td>" + usuNome + "</td>");
                detalhe.append("<td>" + usuLogin + "</td>");
                detalhe.append("</tr>");
            }
            detalhe.append("</table>");
        }
        if ((servidoresBloqueados != null) && !servidoresBloqueados.isEmpty()) {
            detalhe.append("<br>\n<br>\n<b>" + ApplicationResourcesHelper.getMessage("mensagem.email.detalhe.servidores.bloqueio.automatico.seguranca.liberacao.margem", responsavel) + "</b>");
            detalhe.append("<br>\n");
            detalhe.append("<table border=\"1\">");
            detalhe.append("<tr>");
            detalhe.append("<td><b>" + ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula", responsavel) + "</b></td>");
            detalhe.append("<td><b>" + ApplicationResourcesHelper.getMessage("rotulo.servidor.nome", responsavel) + "</b></td>");
            detalhe.append("<td><b>" + ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel) + "</b></td>");
            detalhe.append("</tr>");
            for (final TransferObject to : servidoresBloqueados) {
                final String rseMatricula = (String) to.getAttribute(Columns.RSE_MATRICULA);
                final String serNome = (String) to.getAttribute(Columns.SER_NOME);
                final String serCpf = (String) to.getAttribute(Columns.SER_CPF);
                detalhe.append("<tr>");
                detalhe.append("<td>" + rseMatricula + "</td>");
                detalhe.append("<td>" + serNome + "</td>");
                detalhe.append("<td>" + serCpf + "</td>");
                detalhe.append("</tr>");
            }
            detalhe.append("</table>");
        }
        return detalhe.toString();
    }

    /**
     * Método auxiliar para geração do texto do e-mail com o detalhe das operações de liberação de margem que geraram notificação de limite
     * de operações e/ou bloqueio de usuários e entidades.
     * @param rseCodigosAfetados
     * @param responsavel
     * @param csaCodigo
     * @return String com o texto informativo das operações de liberação de margem
     */
    private static String gerarTextoDetalheOperacoesLiberacaoMargem(List<TransferObject> rseCodigosAfetados, String csaCodigo, AcessoSistema responsavel) {
        final StringBuilder detalhe = new StringBuilder();
        try {
            detalhe.append("<br>\n<br>\n<b>" + ApplicationResourcesHelper.getMessage("mensagem.email.detalhe.notificacao.operacoes.liberacao.margem", responsavel) + "</b>");
            detalhe.append("<br>\n");
            detalhe.append("<table border=\"1\">");
            detalhe.append("<tr>");
            detalhe.append("<td><b>" + ApplicationResourcesHelper.getMessage("rotulo.servidor.matricula", responsavel) + "</b></td>");
            detalhe.append("<td><b>" + ApplicationResourcesHelper.getMessage("rotulo.servidor.nome", responsavel) + "</b></td>");
            detalhe.append("<td><b>" + ApplicationResourcesHelper.getMessage("rotulo.servidor.cpf", responsavel) + "</b></td>");
            detalhe.append("<td><b>" + ApplicationResourcesHelper.getMessage("rotulo.usuario.singular", responsavel) + "</b></td>");
            detalhe.append("<td><b>" + ApplicationResourcesHelper.getMessage("rotulo.usuario.login", responsavel) + "</b></td>");
            detalhe.append("<td><b>" + ApplicationResourcesHelper.getMessage("rotulo.usuario.entidade", responsavel) + "</b></td>");
            detalhe.append("</tr>");
            for (final TransferObject to : rseCodigosAfetados) {
                final String rseMatricula = (String) to.getAttribute(Columns.RSE_MATRICULA);
                final String serNome = (String) to.getAttribute(Columns.SER_NOME);
                final String serCpf = (String) to.getAttribute(Columns.SER_CPF);
                final String usuNome = (String) to.getAttribute(Columns.USU_NOME);
                final String usuLogin = (String) to.getAttribute(Columns.USU_LOGIN);
                final String tipoEntidade = (String) to.getAttribute("TIPO_ENTIDADE");
                final String nomeEntidade = (String) to.getAttribute("NOME_ENTIDADE");
                detalhe.append("<tr>");
                detalhe.append("<td>" + rseMatricula + "</td>");
                detalhe.append("<td>" + serNome + "</td>");
                detalhe.append("<td>" + serCpf + "</td>");
                detalhe.append("<td>" + usuNome + "</td>");
                detalhe.append("<td>" + usuLogin + "</td>");
                detalhe.append("<td>" + tipoEntidade + " - " + nomeEntidade + "</td>");
                detalhe.append("</tr>");
            }
            detalhe.append("</table>");
            if (!TextHelper.isNull(csaCodigo)) {
                final ConsignatariaTransferObject consignataria = new ConsignatariaDelegate().findConsignataria(csaCodigo, responsavel);
                final String csaNome = consignataria.getCsaIdentificador() + " - " + consignataria.getCsaNome();
                final String csaNomeResponsavel = consignataria.getCsaResponsavel();
                final String csaTelefoneResponsavel = consignataria.getCsaRespTelefone();
                final String csaEmail = consignataria.getCsaEmail();
                final String csaAtivo = String.valueOf(consignataria.getCsaAtivo());
                String csaStatus = "";
                if (StatusConsignatariaEnum.ATIVO.getCodigo().equals(csaAtivo)) {
                    csaStatus = ApplicationResourcesHelper.getMessage("rotulo.consignataria.ativa", responsavel);
                } else if (StatusConsignatariaEnum.BLOQUEADO.getCodigo().equals(csaAtivo)) {
                    csaStatus = ApplicationResourcesHelper.getMessage("rotulo.consignataria.bloqueada", responsavel);
                } else if (StatusConsignatariaEnum.BLOQUEADO_POR_SEGURANCA.getCodigo().equals(csaAtivo)) {
                    csaStatus = ApplicationResourcesHelper.getMessage("rotulo.consignataria.bloqueada.seguranca", responsavel);
                }
                detalhe.append("<br>\n<br>\n<b>" + ApplicationResourcesHelper.getMessage("mensagem.email.detalhe.csa.bloqueio.automatico.seguranca.liberacao.margem", responsavel) + "</b>");
                detalhe.append("<br>\n<b>" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.nome", responsavel) + ":</b> " + csaNome);
                detalhe.append("<br>\n<b>" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.contato", responsavel) + ":</b> " + csaNomeResponsavel);
                detalhe.append("<br>\n<b>" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.telefone", responsavel) + ":</b> " + csaTelefoneResponsavel);
                detalhe.append("<br>\n<b>" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.email", responsavel) + ":</b> " + csaEmail);
                detalhe.append("<br>\n<b>" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.status", responsavel) + ":</b> " + csaStatus);
            }
        } catch (final ConsignatariaControllerException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return detalhe.toString();
    }

    /**
     * Envia e-mail às consignatárias afetadas pelo caso de uso de ajuste de consignações à margem e seus contratos foram automaticamente alterados
     * pelo sistema.
     * @param consignações modificadas
     * @param parametros da decisão judicial
     */
    public static final void enviarEmailNotificacaoAlteracaoContratoAjustadosMargem(List<TransferObject> autDes, CustomTransferObject decisaoJudicial, AcessoSistema responsavel) {
        try {
            if ((autDes != null) && !autDes.isEmpty()) {
                String destinatario = null;
                // Recupera o endereço de e-mail para destinatário da mensagem, se nulo/vazio, enviar para o usu_email de todos os usuários desbloqueados da CSA que tenham permissão de editar consignatária (FUN_CODIGO = 85)
                for (final TransferObject ade : autDes) {
                    final String csaCodigo = ade.getAttribute(Columns.CSA_CODIGO).toString();
                    if(!csaCodigo.equals(responsavel.getCodigoEntidade())) {
                        final ConsignatariaController consingnatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                        final ConsignatariaTransferObject consignataria = consingnatariaController.findConsignataria(csaCodigo, responsavel);
                        destinatario = consignataria.getCsaEmail();

                        List<TransferObject> usuarioCsa = new ArrayList<>();
                        final UsuarioController usuarioController = ApplicationContextProvider.getApplicationContext().getBean(UsuarioController.class);
                        if(TextHelper.isNull(destinatario)) {
                            final CustomTransferObject filtro = new CustomTransferObject();
                            filtro.setAttribute(Columns.USU_STU_CODIGO, CodedValues.STU_ATIVO);
                            usuarioCsa = usuarioController.getUsuarios(AcessoSistema.ENTIDADE_CSA, csaCodigo, filtro, -1, -1, responsavel);
                        }

                        // Define o titulo do E-mail
                        String titulo = ApplicationResourcesHelper.getMessage("mensagem.email.titulo.ajustar.consignacao.a.margem", responsavel);
                        if(titulo.contains("<NOME_SISTEMA>")) {
                            titulo = titulo.replace("<NOME_SISTEMA>", JspHelper.getNomeSistema(responsavel) + " - ");
                        }

                        String textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.corpo.ajustar.consignacao.a.margem.sem.decisao", responsavel, ade.getAttribute(Columns.ADE_NUMERO).toString());
                        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_CAD_DECISAO_JUDICIAL_OP_AVANCADA, CodedValues.TPC_SIM, responsavel)) {
                            // Texto do corpo do e-mail
                            final String numeroProcesso = (String) decisaoJudicial.getAttribute("numeroProcesso");
                            final String tipoJustica = (String) decisaoJudicial.getAttribute("tipoJustica");
                            final String uf = (String) decisaoJudicial.getAttribute("uf");
                            final String comarca = (String) decisaoJudicial.getAttribute("comarca");
                            final String dataDecisao = (String) decisaoJudicial.getAttribute("dataDecisao");
                            final String textoDecisao = (String) decisaoJudicial.getAttribute("textoDecisao");

                            textoGeral = ApplicationResourcesHelper.getMessage("mensagem.email.corpo.ajustar.consignacao.a.margem",responsavel, ade.getAttribute(Columns.ADE_NUMERO).toString(),numeroProcesso,tipoJustica,uf,comarca,dataDecisao,textoDecisao);
                        }


                        if((usuarioCsa != null) && !usuarioCsa.isEmpty()) {
                            for (final TransferObject usuario : usuarioCsa) {
                                final String emailUsu = (String) usuario.getAttribute(Columns.USU_EMAIL);
                                if (!TextHelper.isNull(emailUsu) && usuarioController.usuarioTemPermissao(usuario.getAttribute(Columns.USU_CODIGO).toString(), CodedValues.FUN_EDT_CONSIGNATARIA, AcessoSistema.ENTIDADE_CSA, responsavel)) {
                                    final MailHelper mailHelper = new MailHelper();
                                    mailHelper.send(emailUsu, null, null, titulo, textoGeral.toString(), null, null);
                                }
                            }
                        } else {
                            final MailHelper mailHelper = new MailHelper();
                            mailHelper.send(destinatario, null, null, titulo, textoGeral.toString(), null, null);
                        }
                    }
                }
            }
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia email alerta logo após criação de um novo usuário de CSE.
     * @param usuCodigo
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailAlertaCriaNovoUsuCse(String usuCodigo, AcessoSistema responsavel)
            throws ViewHelperException {

        try {

            final String convenio = JspHelper.getNomeSistema(responsavel);

            final EnviarEmailAlertaCriacaoNovoUsuCseCommand command = new EnviarEmailAlertaCriacaoNovoUsuCseCommand();

            command.setConvenio(convenio);
            command.setUsuCodigo(usuCodigo);

            command.execute();

        } catch (final ViewHelperException e) {
            throw new ViewHelperException("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * Envia email informando o servidor que existem contratos suspensos pendentes de reativação.
     * @param usuCodigo
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailServidorContratosPendentesReativacao(String serNome, String serEmail, AcessoSistema responsavel)
            throws ViewHelperException {

        try {
            final EnviarEmailServidorContratosPendentesReativacaoCommand command = new EnviarEmailServidorContratosPendentesReativacaoCommand();
            command.setNome(serNome);
            command.setEmail(serEmail);
            command.execute();

        } catch (final ViewHelperException e) {
            throw new ViewHelperException("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * Envia email informando o servidor que existem contratos suspensos pendentes de reativação.
     * @param usuCodigo
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailConsignatariaContratoColocadoEmEstoque(AutDesconto autDesconto, AcessoSistema responsavel)
            throws ViewHelperException {

        try {

            final String matricula = autDesconto.getRegistroServidor().getRseMatricula();
            final String csaEmail = autDesconto.getVerbaConvenio().getConvenio().getConsignataria().getCsaEmail();
            final String serNome = autDesconto.getRegistroServidor().getServidor().getSerNome();
            final String adeVlr = NumberHelper.formata(autDesconto.getAdeVlr().doubleValue(), ApplicationResourcesHelper.getMessage("rotulo.moeda.pattern",responsavel));
            final String adePrazo = TextHelper.isNull(autDesconto.getAdePrazo()) ? ApplicationResourcesHelper.getMessage("rotulo.indeterminado", responsavel) : autDesconto.getAdePrazo().toString();
            final String adeNumero = autDesconto.getAdeNumero().toString();
            final String csaNomeAbrev = autDesconto.getVerbaConvenio().getConvenio().getConsignataria().getCsaNomeAbrev();
            final String serCpf = autDesconto.getRegistroServidor().getServidor().getSerCpf();

            if(TextHelper.isNull(csaEmail)) {
                LOG.debug(ApplicationResourcesHelper.getMessage("mensagem.erro.email.nao.enviado.csa.contrato.colocado.em.estoque", responsavel));
                return;
            }

            final EnviarEmailCsaContratosColocadosEmEstoqueCommand command = new EnviarEmailCsaContratosColocadosEmEstoqueCommand();
            command.setMatricula(matricula);
            command.setCsaEmail(csaEmail);
            command.setSerNome(serNome);
            command.setAdeVlr(adeVlr);
            command.setAdePrazo(adePrazo);
            command.setAdeNumero(adeNumero);
            command.setCsaNomeAbrev(csaNomeAbrev);
            command.setSerCpf(serCpf);
            command.execute();

        } catch (final ViewHelperException e) {
            throw new ViewHelperException("mensagem.erro.email.enviar", responsavel, e);
        }
    }

    /**
     * Envia email informando ao consignante a reativação do contrato suspenso por parcela rejeitada.
     * @param adeCodigo
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailNotificacaoCseReativacaoPrdRejeitada(String adeCodigo, AcessoSistema responsavel) throws ViewHelperException {
        try {
            // Recupera o nome do consignante
            final String cseNome = getCseNome(responsavel);
            final String email = getCseEmail(responsavel);

            final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
            final TransferObject ade = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);

            if (TextHelper.isNull(email)) {
                throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
            }

            final String adeNumero = ade.getAttribute(Columns.ADE_NUMERO).toString();
            final String rseMatricula = ade.getAttribute(Columns.RSE_MATRICULA) != null ? ade.getAttribute(Columns.RSE_MATRICULA).toString() : "";
            final String serNome =  ade.getAttribute(Columns.SER_NOME) != null ? ade.getAttribute(Columns.SER_NOME).toString() : "";
            final String serCpf = ade.getAttribute(Columns.SER_CPF) != null ? ade.getAttribute(Columns.SER_CPF).toString() : "";

            final EnviarEmailNotificacaoCseReativacaoPrdRejeitadaCommand command = new EnviarEmailNotificacaoCseReativacaoPrdRejeitadaCommand();
            command.setCseNome(cseNome);
            command.setEmail(email);
            command.setNome(serNome);
            command.setMatricula(rseMatricula);
            command.setSerCpf(serCpf);
            command.setAdeNumero(adeNumero);
            command.setResponsavel(responsavel);
            command.execute();

        } catch (final AutorizacaoControllerException ex) {
            throw new ViewHelperException("mensagem.erro.email.enviar", responsavel, ex);
        }
    }

    /**
     * Envia email sobre informação para a consignatária iniciar o processo de credenciamento da consignataria
     * @param adeCodigo
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailNotificacaoCsaModuloCredenciamento(ConsignatariaTransferObject consignataria, AcessoSistema responsavel) throws ViewHelperException {
            String arqModelo = ParamSist.getDiretorioRaizArquivos();
            arqModelo += File.separator + "credenciamento" + File.separator + "modelo" + File.separator + "lista_documentos";

            boolean arquivoExiste = false;

            for(final String extensao : UploadHelper.EXTENSOES_PERMITIDAS_DOCUMENTOS_CREDENCIAMENTO) {
                final File arquivo = new File(arqModelo+extensao);
                if(arquivo.exists()) {
                    arqModelo += extensao;
                    arquivoExiste = true;
                    break;
                }
            }

            final EnviarEmailNotificacaoCsaCredenciamentoCommand command = new EnviarEmailNotificacaoCsaCredenciamentoCommand();
            command.setEmail(consignataria.getCsaEmail());
            if(arquivoExiste) {
                command.setAnexo(arqModelo);
            } else {
                LOG.warn("Credenciamento: Arquivo com a lista de documentos não existe, mesmo assim iremos enviar o email");
            }
            command.execute();
	}

    /**
     * Envia email informando o desbloqueio da CSA.
     * @param csa
     * @param email
     * @param convenio
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailNotificacaoDesbloqueioCsa(String csaNome, String email, AcessoSistema responsavel) throws ViewHelperException {
            if (TextHelper.isNull(email)) {
                throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
            }

            final EnviarEmailNotificacaoDesbloqueioCsaCommand command = new EnviarEmailNotificacaoDesbloqueioCsaCommand();
            command.setCsaNome(csaNome);
            command.setEmail(email);
            command.execute();
    }

    /**
     * Envia email informando necessidade de aprovação para o desbloqueio da CSA.
     * @param nome
     * @param csa
     * @param email
     * @param convenio
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailSuporteCsaPendente(String nome, String csaNome, String csaEmailsDesbloqueio, AcessoSistema responsavel) throws ViewHelperException {
        if(TextHelper.isNull(csaEmailsDesbloqueio)) {
            LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.info.notificacao.nao.enviada.desbloqueio.csa", responsavel));
            return;
        }
        final EnviarEmailConfirmacaoDesbloqueioCsaCommand command = new EnviarEmailConfirmacaoDesbloqueioCsaCommand();
        command.setEmail(csaEmailsDesbloqueio);
        command.setNome(nome);
        command.setCsaNome(csaNome);
        command.execute();
    }

    /**
     * Envia email sobre informação para a consignante que a csa fez os envios da documentação
     * @param cseEmail
     * @param csaNomeAbrev
     * @param anexos
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailNotificacaoCseModuloCredenciamento(String cseEmail, String csaNomeAbrev, List<String> anexos, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(cseEmail)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.email.destinatario.invalido", responsavel));
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        final EnviarEmailNotificacaoCseCredenciamentoCommand command = new EnviarEmailNotificacaoCseCredenciamentoCommand();
        command.setEmail(cseEmail);
        command.setCsaNomeAbrev(csaNomeAbrev);
        command.setAnexos(anexos);
        command.execute();
    }

    /**
     * Envia email sobre aprovação da documentação do credenciamento para a consignatária
     * @param CredenciamentoCsa
     * @param situacao
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificarCsaAprovacaoDocumentacaoCsa(CredenciamentoCsa credenciamentocsa, String situacao, AcessoSistema responsavel) throws ViewHelperException {
        final String csaEmail = credenciamentocsa.getConsignataria().getCsaEmail();
        if (TextHelper.isNull(csaEmail)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.email.destinatario.invalido", responsavel));
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        final EnviarEmailNotificacaoDocCsaCredenciamentoCommand command = new EnviarEmailNotificacaoDocCsaCredenciamentoCommand();
        command.setEmail(csaEmail);
        command.setCsaNomeAbrev(credenciamentocsa.getConsignataria().getCsaNomeAbrev());
        command.setSituacao(situacao);
        command.execute();
    }

    /**
     * Envia email sobre atualização do credenciamento para a consignatária
     * @param CredenciamentoCsa
     * @param situacao
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificarCsaSituacaoCredenciamento(CredenciamentoCsa credenciamentocsa, String situacao, AcessoSistema responsavel) throws ViewHelperException {
        final String csaEmail = credenciamentocsa.getConsignataria().getCsaEmail();
        if (TextHelper.isNull(csaEmail)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.email.destinatario.invalido", responsavel));
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        final EnviarEmailNotificacaoSituacaoCsaCredenciamentoCommand command = new EnviarEmailNotificacaoSituacaoCsaCredenciamentoCommand();
        command.setEmail(csaEmail);
        command.setCsaNomeAbrev(credenciamentocsa.getConsignataria().getCsaNomeAbrev());
        command.setSituacao(situacao);
        command.execute();
    }

    /**
     * Envia email sobre a necessidade do preenchidmento do termo aditivo para a consignatária
     * @param CredenciamentoCsa
     * @param anexoTermoAditivo
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificarCsaPreenchimentoTermoAditivoCredenciamento(CredenciamentoCsa credenciamentocsa, String anexoTermoPreenchido, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailNotificacaoPreenchimentoTermoCredenciamentoCommand command = new EnviarEmailNotificacaoPreenchimentoTermoCredenciamentoCommand();
        command.setEmail(credenciamentocsa.getConsignataria().getCsaEmail());
        command.setCsaNomeAbrev(credenciamentocsa.getConsignataria().getCsaNomeAbrev());
        command.setAnexoTermoPreenchido(anexoTermoPreenchido);
        command.execute();
    }

    /**
     * Envia email informando que o termo aditivo foi assinado pela csa
     * @param cseEmail
     * @param csaNome
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificarCseAssTermoAditivo(String cseEmail, String csaNome, String anexoTermoAssinado, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(cseEmail)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.email.destinatario.invalido", responsavel));
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        final EnviarEmailNotificacaoCseAssTermoCsaCommand command = new EnviarEmailNotificacaoCseAssTermoCsaCommand();
        command.setEmail(cseEmail);
        command.setCsaNomeAbrev(csaNome);
        command.setAnexo(anexoTermoAssinado);
        command.execute();
    }

    /**
     * Envia email informando que o termo aditivo foi assinado pela cse para os usuário com função de aprovacao
     * @param cseEmail
     * @param csaNome
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificarUsuAprovacaoAssTermoCseAditivo(String usuEmail,String usuNome, String anexoTermoAssinado, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(usuEmail)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.email.destinatario.invalido", responsavel));
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        final EnviarEmailNotificacaoUsuPermissaoAprovacaoAssTermoCseCommand command = new EnviarEmailNotificacaoUsuPermissaoAprovacaoAssTermoCseCommand();
        command.setEmail(usuEmail);
        command.setUsuNome(usuNome);
        command.setAnexo(anexoTermoAssinado);
        command.execute();
    }

    /**
     * Envia email informando que o credenciamento foi concluído
     * @param cseEmail
     * @param csaNome
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificarCredenciamentoConcluido(String cseEmail, String csaNome, String csaEmail, String anexoTermoAssinado, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(cseEmail) || TextHelper.isNull(csaEmail)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.email.destinatario.invalido", responsavel));
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        final EnviarEmailNotificacaoCseCredenciamentoConcluidoCommand command = new EnviarEmailNotificacaoCseCredenciamentoConcluidoCommand();
        command.setEmail(cseEmail);
        command.setCsaNomeAbrev(csaNome);
        command.setAnexo(anexoTermoAssinado);
        command.execute();

        final EnviarEmailNotificacaoCsaCredenciamentoConcluidoCommand commandCsa = new EnviarEmailNotificacaoCsaCredenciamentoConcluidoCommand();
        commandCsa.setEmail(csaEmail);
        commandCsa.setCsaNomeAbrev(csaNome);
        commandCsa.setAnexo(anexoTermoAssinado);
        commandCsa.execute();
    }

    /**
     * Envia e-mail para o servidor com os contratos que não foram possíveis ser concluídos com o valor da verba rescisória
     * @param rseCodigo
     * @param responsavel
     * @throws ZetraException
     * @throws MessagingException
     */
    public static final void enviarEmailSerVerbaRescisoriaSaldoInsuficiente(String rseCodigo, AcessoSistema responsavel) {
        try {

            final VerbaRescisoriaController verbaRescisoriaController = ApplicationContextProvider.getApplicationContext().getBean(VerbaRescisoriaController.class);

            // Busca os contratos com a ocorrência de insuficiente de saldo da verba rescisória
            final List<AutDesconto> lstAutDescontoVerbaRescisoria = verbaRescisoriaController.listarConsignacoesReterVerbaRescisoriaSaldoInsuficiente(rseCodigo, responsavel);

            final String emailDestino = lstAutDescontoVerbaRescisoria.get(0).getRegistroServidor().getServidor().getSerEmail();

            if (TextHelper.isNull(emailDestino)) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.servidor.email.invalido", responsavel));
                throw new ViewHelperException("mensagem.erro.servidor.email.invalido", responsavel);
            }

            final StringBuilder detalhe = new StringBuilder();

            for (final AutDesconto ade : lstAutDescontoVerbaRescisoria) {
                detalhe.append("<br>" + ApplicationResourcesHelper.getMessage("rotulo.email.numero.ade", responsavel, String.valueOf(ade.getAdeNumero())));
                detalhe.append("<br>" + ApplicationResourcesHelper.getMessage("rotulo.email.data.inclusao", responsavel, DateHelper.toDateTimeString(ade.getAdeData())));
                detalhe.append("<br>" + ApplicationResourcesHelper.getMessage("rotulo.email.valor.prestacao", responsavel, NumberHelper.format(ade.getAdeVlr().doubleValue(), NumberHelper.getLang(), true)));
                detalhe.append("<br>" + ApplicationResourcesHelper.getMessage("rotulo.email.valor.restante", responsavel));
                detalhe.append("<br><b>" + ApplicationResourcesHelper.getMessage("rotulo.consignataria.singular", responsavel)).append(":</b> ").append(ade.getVerbaConvenio().getConvenio().getConsignataria().getCsaNome());
            }

            final EnviarEmailServidorVerbaRescisoriaSaldoInsuficienteCommand enviaEmailSer = new EnviarEmailServidorVerbaRescisoriaSaldoInsuficienteCommand();
            enviaEmailSer.setSerNome(TextHelper.capitailizeFirstLetter(lstAutDescontoVerbaRescisoria.get(0).getRegistroServidor().getServidor().getSerNome()));
            enviaEmailSer.setDetalheConsignacao(detalhe.toString());
            enviaEmailSer.setEmail(emailDestino);

            enviaEmailSer.execute();
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
        }
    }

    /**
     * Envia email informando todos vinculos que foram criados e ela deve escolher como proceder
     * @param lista de vinculos
     * @Param lista de consignatarias
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificarCsaNovosVinculosRse(List<VinculoRegistroServidor> vinculosRegistroServidor, List<TransferObject> listaCsaEnviarNotificacao, AcessoSistema responsavel) throws ViewHelperException {

        for (final TransferObject csaEnviarNotificacao : listaCsaEnviarNotificacao) {
            final String csaNome = (String) csaEnviarNotificacao.getAttribute(Columns.CSA_NOME);
            final String csaNomeAbrev = (String) csaEnviarNotificacao.getAttribute(Columns.CSA_NOME_ABREV);
            final String csaIdentificador = (String) csaEnviarNotificacao.getAttribute(Columns.CSA_IDENTIFICADOR);
            final String tpaEmail = (String) csaEnviarNotificacao.getAttribute("VALOR_PARAM_95");
            final String valorParam81 = (String) csaEnviarNotificacao.getAttribute("VALOR_PARAM");

            if(TextHelper.isNull(tpaEmail)) {
                LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.email.destinatario.invalido", responsavel));
                continue;
            }
            final StringBuilder vinculos = new StringBuilder();

           for(var i = 0; i < vinculosRegistroServidor.size(); i++) {
                final VinculoRegistroServidor vinculoRegistroServidor = vinculosRegistroServidor.get(i);
                vinculos.append(vinculoRegistroServidor.getVrsIdentificador() + "-" + vinculoRegistroServidor.getVrsDescricao() + " " +
                                ApplicationResourcesHelper.getMessage("mensagem.email.notificar.novos.vinculos.texto.dia", responsavel) + " " +
                                DateHelper.toDateTimeString(vinculoRegistroServidor.getVrsDataCriacao()));
                if(i < (vinculosRegistroServidor.size()-1)) {
                    vinculos.append(", ");
                }
            }

        final EnviarEmailNotificacaoCsaNovoVinculoCommand command = new EnviarEmailNotificacaoCsaNovoVinculoCommand();
        command.setCsaEmail(tpaEmail.replace(";", ","));
        command.setCsaIdentificador(csaIdentificador);
        command.setCsaNome(csaNome);
        command.setCsaNomeAbrev(csaNomeAbrev);
        command.setVinculos(vinculos.toString());
        command.setSituacaoVinculo(CodedValues.TPC_NAO.equals(valorParam81) ? ApplicationResourcesHelper.getMessage("rotulo.acoes.bloquear", responsavel).toLowerCase() : ApplicationResourcesHelper.getMessage("rotulo.acoes.desbloquear", responsavel).toLowerCase());
        command.execute();
        }
    }

    /**
     * Envia email informando ao suporte que houve um erro ao criar o arquivo de margem de serviço externo
     * @Param emailSuporte
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificaSuporteErroCriarArqMargemServicoExterno(String emailSuporte, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailNotificaoErroCriarArquivoMargemCommand command = new EnviarEmailNotificaoErroCriarArquivoMargemCommand();
        command.setEmail(emailSuporte);
        command.execute();
    }

    /**
     * Envia email informando ao consignante que foi gerado um arquivo com a lista de servidores bloqueados para determinada csa, pois o limite de variação foi superado
     * @Param emailSuporte
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificaCseArqRelacaoBloqRseCnvVariacaoCsa(String emailConsignante, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailNotificaoCseBloqSerCnvVariacaoMargemCsaCommand command = new EnviarEmailNotificaoCseBloqSerCnvVariacaoMargemCsaCommand();
        command.setEmail(emailConsignante);
        command.execute();
    }

    /**
     * Envia email informando à consignatária que uma determinada de servdiores foram bloqueados para cada verba por ultrapassarem o limite estabelecido de variação de margem
     * @Param emailSuporte
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificaCsaQuantidadeServidorVerbaBloq(String csaEmail, String csaNome, String quantidadePorVerba, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailNotificaoCsaBloqSerCnvVariacaoMargemCommand command = new EnviarEmailNotificaoCsaBloqSerCnvVariacaoMargemCommand();
        command.setEmail(csaEmail);
        command.setCsaNome(csaNome);
        command.setQuantidadePorVerba(quantidadePorVerba);
        command.execute();
    }

    public static final void enviarEmailOfertaRefinanciamentoCsa(String csaEmail, String csaNome, String percentual, String adeNumeros) throws ViewHelperException {
        final EnviarEmailRefinanciamentoCsaCommand command = new EnviarEmailRefinanciamentoCsaCommand();
        command.setEmail(csaEmail);
        command.setCsaNome(csaNome);
        command.setAdeNumeros(adeNumeros);
        command.setPercentual(percentual);
        command.execute();
    }

    /**
     * Envia email para notificar servidor após reserva de margem sem necessidade de senha ou código único
     * @Param serEmail
     * @param serNome
     * @param csaNome
     * @throws ViewHelperException
     */
    public static final void notificarSerReservaMargem(String serEmail, String serNome, String csaNome) throws ViewHelperException {
        final EnviarEmailNotificacaoReservaMargemCommand command = new EnviarEmailNotificacaoReservaMargemCommand();
        command.setEmail(serEmail);
        command.setSerNome(serNome);
        command.setCsaNome(csaNome);
        command.execute();
    }

    /**
     * Envia email para notificar servidor que a autorização irá vencer
     * @Param serEmail
     * @param serNome
     * @param csaNome
     * @param dataVencimento
     * @throws ViewHelperException
     */
    public static final void notificarSerAutorizacaoIraVencer(String serEmail, String serNome, String csaNome, String dataVencimento) throws ViewHelperException {
        final EnviarEmailNotificacaoAutorizacaoIraVencerCommand command = new EnviarEmailNotificacaoAutorizacaoIraVencerCommand();
        command.setEmail(serEmail);
        command.setSerNome(serNome);
        command.setCsaNome(csaNome);
        command.setDataVencimento(dataVencimento);
        command.execute();
    }

    /**
     * Envia email para notificar csa que a verba do registro servidor foi desbloqueada
     * @Param csaEmail
     * @param csaNome
     * @param cseNome
     * @param count
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificaCsaDesbloqueioVerbaRse(String csaEmail, String cseNome, String csaNome, int count, AcessoSistema responsavel) throws ViewHelperException {
        final EnviaEmailCsaDesbloqueioVerbaRseCommand command = new EnviaEmailCsaDesbloqueioVerbaRseCommand();
        command.setCount(count);
        command.setCsaEmail(csaEmail);
        command.setCsaNome(csaNome);
        command.setResponsavel(responsavel);
        command.execute();
    }

    /**
     * Envia email para notificar csa os vínculos bloqueados e desbloqueados
     * @Param csaEmail
     * @param csaCodigo
     * @param csaNome
     * @param occCodigos
     * @throws ViewHelperException
     */
    public static final void enviarEmailCsaVinculosBloqDesbloq(String csaEmail, String csaCodigo, String csaNome, List<String> occCodigos) throws ViewHelperException {
        final EnviarEmailNotificacaoCsaVinculosBloqDesbloqCommand command = new EnviarEmailNotificacaoCsaVinculosBloqDesbloqCommand();
        command.setCsaEmail(csaEmail);
        command.setCsaCodigo(csaCodigo);
        command.setCsaNome(csaNome);
        command.setOccCodigos(occCodigos);
        command.execute();
    }

    /**
     * Envia email ao gestor informando o bloqueio de uma consignatária, motivo e usuário.
     * @param cseEmail
     * @param csaNomeAbrev
     * @param anexos
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailNotificacaoCseBloqueioConsignataria(String cseEmail, String obsBloqueio, String csaNome, String csaNomeAbrev, String usuario, AcessoSistema responsavel) throws ViewHelperException {
        if (TextHelper.isNull(cseEmail)) {
            LOG.error(ApplicationResourcesHelper.getMessage("mensagem.erro.email.destinatario.invalido", responsavel));
            return;
        }

        final EnviarEmailNotificacaoCseBloqueioCsaCommand command = new EnviarEmailNotificacaoCseBloqueioCsaCommand();
        command.setEmail(cseEmail);
        command.setMotivoBloqueio(obsBloqueio);
        command.setCsaNome(csaNome);
        command.setCsaNomeAbrev(csaNomeAbrev);
        command.setUsuario(usuario);
        command.setDataBloqueio(DateHelper.toDateString(DateHelper.getSystemDate()));
        command.execute();
    }

    /** Envia email para notificar csa após portabilidade de reserva de cartão
     * @Param csaEmail
     * @param adeNumero
     * @param serCpf
     * @param serMatricula
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificaCsaPortabilidadeCartao(String csaEmail, String adeNumero, String serCpf, String serMatricula, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailCsaPortabilidadeCartaoCommand command = new EnviarEmailCsaPortabilidadeCartaoCommand();
        command.setCsaEmail(csaEmail);
        command.setAdeNumero(adeNumero);
        command.setSerCpf(serCpf);
        command.setSerMatricula(serMatricula);
        command.setResponsavel(responsavel);
        command.execute();
    }

    /** Envia email para notificar servidor após simulacao de reserva
     * @Param serEmail
     * @Param anexoEmail
     * @Param nomeUSu
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificaSerSimulacaoConsignacao(List<CsaListInfoRequest> inforCsas, String serEmail, String nomeUsu, String cseNome, String anexoEmail, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailSimulacaoCommand command = new EnviarEmailSimulacaoCommand();
        command.setResponsavel(responsavel);
        command.setSerEmail(serEmail);
        command.setNomeUsu(nomeUsu);
        command.setAnexoEmail(anexoEmail);
        command.setListInfor(inforCsas);
        command.setCseNome(cseNome);
        command.execute();

	}

    /**
     * Envia e-mail para a consignatária vencedora do leilão com o telefone do servidor.
     * @param adeCodigo
     * @param plsVencedora
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void enviarEmailCsaVencedoraLeilao(String adeCodigo, PropostaLeilaoSolicitacao plsVencedora, AcessoSistema responsavel) throws ViewHelperException {
        try {
            final String csaEmail = plsVencedora.getConsignataria().getCsaEmail();

            TransferObject adeTO = null;

            if (!TextHelper.isNull(csaEmail)) {
                final AutorizacaoDelegate adeDelegate = new AutorizacaoDelegate();
                adeTO = adeDelegate.buscaAutorizacao(adeCodigo, responsavel);
            } else {
                return;
            }

            final String cseNome = getCseNome(responsavel);
            final String adeNumero = adeTO.getAttribute(Columns.ADE_NUMERO).toString();
            final String serNome = (String) adeTO.getAttribute(Columns.SER_NOME);
            final String serTel = (String) adeTO.getAttribute(Columns.SER_TEL);
            final String serCelular = (String) adeTO.getAttribute(Columns.SER_CELULAR);
            final String titulo = gerarTituloEmail(cseNome, ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.titulo.leilao.encerrado", responsavel, adeNumero), responsavel);

            final boolean simulacaoPorTaxaJuros = ParamSist.paramEquals(CodedValues.TPC_SIMULACAO_POR_TAXA_JUROS, CodedValues.TPC_SIM, responsavel);
            final boolean temCET = ParamSist.paramEquals(CodedValues.TPC_TEM_CET, CodedValues.TPC_SIM, responsavel);

            final String adeTipoTaxa = (String) adeTO.getAttribute(Columns.ADE_TIPO_TAXA);

            final List<String> parametros = new ArrayList<>();
            parametros.add(CodedValues.TPS_ADD_VALOR_IOF_VAL_TAXA_JUROS);
            parametros.add(CodedValues.TPS_ADD_VALOR_TAC_VAL_TAXA_JUROS);
            parametros.add(CodedValues.TPS_VLR_LIQ_TAXA_JUROS);

            final ParametroDelegate parDelegate = new ParametroDelegate();
            final ParamSvcTO paramSvcCse = parDelegate.selectParamSvcCse((String) adeTO.getAttribute(Columns.SVC_CODIGO), parametros, responsavel);

            // Define rótulo para o campo de Taxa de Juros/CET/Coeficiente
            String rotuloTaxa = "";
            if (adeTipoTaxa != null) {
                if (CodedValues.TIPO_TAXA_CET.equals(adeTipoTaxa)) {
                    //CET Real: Cálculo com valor liberado sem adicionar IOF e TAC
                    rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel);
                } else if (CodedValues.TIPO_TAXA_JUROS.equals(adeTipoTaxa)) {
                    // Taxa de Juros Real: Cálculo com valor liberado somando IOF e TAC
                    rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel);
                }
            } else if (temCET) {
                rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.cet", responsavel);
            } else if (paramSvcCse.isTpsVlrLiqTaxaJuros() || simulacaoPorTaxaJuros) {
                rotuloTaxa = ApplicationResourcesHelper.getMessage("rotulo.consignacao.taxa.juros", responsavel);
            }

            final StringBuilder corpoEmail = new StringBuilder(ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.cabecalho.leilao.encerrado", responsavel, adeNumero, (String) adeTO.getAttribute(Columns.SVC_DESCRICAO))).append("<BR>");

            corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("mensagem.email.consignataria.vencedora.leilao", responsavel, adeNumero));
            corpoEmail.append("</b> : <BR><BR>");
            corpoEmail.append("<b>").append(rotuloTaxa).append("</b> : ").append(plsVencedora.getPlsTaxaJuros().setScale(2, java.math.RoundingMode.HALF_UP));
            corpoEmail.append("<BR>");
            corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.liberado", responsavel)).append("</b> : ");
            corpoEmail.append(LocaleHelper.getCurrencyFormat().format(plsVencedora.getPlsValorLiberado().setScale(2, java.math.RoundingMode.HALF_UP))).append("<BR>");
            corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.consignacao.valor.parcela", responsavel)).append("</b> : ");
            corpoEmail.append(LocaleHelper.getCurrencyFormat().format(plsVencedora.getPlsValorParcela().setScale(2, java.math.RoundingMode.HALF_UP)));
            corpoEmail.append("<BR>");
            corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.prazo.singular", responsavel)).append("</b> : ");
            corpoEmail.append(plsVencedora.getPlsPrazo()).append(" ").append(CodedValues.PERIODICIDADE_FOLHA_MENSAL.equals(adeTO.getAttribute(Columns.ADE_PERIODICIDADE)) ? ApplicationResourcesHelper.getMessage("rotulo.consignacao.mes.plural", responsavel) : ApplicationResourcesHelper.getMessage("rotulo.consignacao.quinzena.plural", responsavel)).append("<BR>");
            corpoEmail.append("<BR><BR>");
            corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.singular", responsavel)).append("</b> : ").append(serNome);
            corpoEmail.append("<BR>");
            if (!TextHelper.isNull(serTel) || !TextHelper.isNull(serCelular)) {
                corpoEmail.append("<b>").append(ApplicationResourcesHelper.getMessage("rotulo.servidor.telefone", responsavel)).append("</b> : ");
                if (!TextHelper.isNull(serTel) && !TextHelper.isNull(serCelular)) {
                    corpoEmail.append(serTel).append(" / ").append(serCelular);
                } else if (!TextHelper.isNull(serTel)) {
                    corpoEmail.append(serTel);
                } else {
                    corpoEmail.append(serCelular);
                }
            }

            // Envia o e-mail.
            new MailHelper().send(csaEmail, null, null, titulo, corpoEmail.toString(), null);
        } catch (AutorizacaoControllerException | ParametroControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException(ex.getMessageKey(), responsavel, ex);
        } catch (final MessagingException ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    /** Envia email para notificar gestor que determinada consignatária atingiu o limite diário de consignações incluídas
     * @Param serEmail
     * @Param anexoEmail
     * @Param nomeUSu
     * @param responsavel
     * @throws ViewHelperException
     */
    public static final void notificaCseLimiteConsignacaoCsa(String csaCodigo, int contadorAtual, AcessoSistema responsavel) throws ViewHelperException {
        final ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
        final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
        ConsignatariaTransferObject consignataria;
        ConsignanteTransferObject consignante;
        try {
            consignataria = consignatariaController.findConsignataria(csaCodigo, responsavel);
            consignante = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
        } catch (ConsignatariaControllerException | ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return;
        }

        final EnviarEmailLimiteAtigindoInclusaoCsaCommand command = new EnviarEmailLimiteAtigindoInclusaoCsaCommand();
        command.setResponsavel(responsavel);
        command.setContadorAtual(contadorAtual);
        command.setCsaNome(consignataria.getCsaNome());
        command.setCsaNomeAbrev(consignataria.getCsaNomeAbreviado());
        command.setCseEmail(consignante.getCseEmail());
        command.setCseNome(consignante.getCseNome());
        command.execute();
    }

    /**
     * Envia email para notificar usuário sobre o prazo de expiração de senha
     * @Param usuNome
     * @Param usuEmail
     * @param qtdeDiasExpiracaoSenha
     * @throws ViewHelperException
     */
    public static final void notificarUsuPrazoExpiracaoSenha(String usuNome, String usuEmail, Integer qtdeDiasExpiracaoSenha, AcessoSistema responsavel) throws ViewHelperException {
        final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
        ConsignanteTransferObject consignante;
        try {
            consignante = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
        } catch (final ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return;
        }

        final EnviarEmailNotificacaoPrazoExpiracaoSenhaCommand command = new EnviarEmailNotificacaoPrazoExpiracaoSenhaCommand();
        command.setResponsavel(responsavel);
        command.setCseNome(consignante.getCseNome());
        command.setUsuEmail(usuEmail);
        command.setUsuNome(usuNome);
        command.setQtdeDiasExpiracaoSenha(qtdeDiasExpiracaoSenha);
        command.execute();
    }

    /**
     * Envia email para alertar consignante sobre bloqueio de usuário
     * @Param usuario
     * @throws ViewHelperException
     */
    public static final void enviarEmailCseBloqueioUsuario(UsuarioTransferObject usuario, AcessoSistema responsavel) throws ViewHelperException {
        final ConsignanteController consignanteController = ApplicationContextProvider.getApplicationContext().getBean(ConsignanteController.class);
        ConsignanteTransferObject consignante;
        try {
            consignante = consignanteController.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
        } catch (final ConsignanteControllerException ex) {
            LOG.error(ex.getMessage(), ex);
            return;
        }

        final EnviarEmailCseBloqueioUsuarioCommand command = new EnviarEmailCseBloqueioUsuarioCommand();
        command.setResponsavel(responsavel);
        command.setCseEmail(consignante.getCseEmail());
        command.setUsuNome(usuario.getUsuNome());
        command.setUsuCpf(usuario.getUsuCPF());
        command.setUsuLogin(usuario.getUsuLogin());
        command.setUsuEmail(usuario.getUsuEmail());
        command.execute();
    }

    /**
     * Envia email para o usuário com a senha criada para ele.
     * @Param usuario
     * @throws ViewHelperException
     */
    public static final void enviarEmailSenhaNovoUsuario(UsuarioTransferObject usuario, String senha, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailSenhaNovoUsuarioCommand command = new EnviarEmailSenhaNovoUsuarioCommand();
        command.setResponsavel(responsavel);
        command.setUsuEmail(usuario.getUsuEmail());
        command.setUsuNome(usuario.getUsuNome());
        command.setUsuSenha(senha);
        command.execute();
    }

    /**
     * Envia e-mail para a consignatária com as alterações realizadas nas regras do convênio.
     * @param dadosAlterados 
     * @param csaNome 
     * @param csaNotificacaoRegra 
     * @param responsavel 
     * @throws ViewHelperException 
     */
	public static void enviarEmailCsaAlteracaoRegrasConvenio(List<RegrasConvenioParametrosBean> dadosAlterados, String csaNome, String csaNotificacaoRegra, AcessoSistema responsavel) throws ViewHelperException {
        final EnviarEmailCsaNotificacaoRegraCommand command = new EnviarEmailCsaNotificacaoRegraCommand();
        command.setResponsavel(responsavel);
        command.setCsaNome(csaNome);
        command.setCsaNotificacaoRegra(csaNotificacaoRegra);
        command.setDadosAlterados(dadosAlterados);
        command.execute();		
	}
}
