package com.zetra.econsig.helper.email.command;

import java.math.BigDecimal;

import jakarta.mail.MessagingException;

import com.zetra.econsig.delegate.AutorizacaoDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConvenioTransferObject;
import com.zetra.econsig.dto.entidade.OrgaoTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.margem.MargemDisponivel;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.service.coeficiente.CoeficienteController;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: EnviarEmailCsaNovaSolicitacaoCommand</p>
 *
 * <p>Description: Command para envio de email de notificação à CSA quando um servidor fizer uma solicitação</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author:  $
 * $Revision:  $
 * $Date:  $
 */
public class EnviarEmailCsaNovaSolicitacaoCommand extends AbstractEnviarEmailCommand {

    private String csaMail;
    private String rseCodigo;
    private ConvenioTransferObject cnvTo;
    private String adeCodigo;
    private BigDecimal rseMargemRestOld;
    private Short incMargem;

    public BigDecimal getRseMargemRestOld() {
        return rseMargemRestOld;
    }

    public void setRseMargemRestOld(BigDecimal rseMargemRestOld) {
        this.rseMargemRestOld = rseMargemRestOld;
    }

    public String getRseCodigo() {
        return rseCodigo;
    }

    public void setRseCodigo(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    public String getAdeCodigo() {
        return adeCodigo;
    }

    public void setAdeCodigo(String adeCodigo) {
        this.adeCodigo = adeCodigo;
    }

    public ConvenioTransferObject getCnvTo() {
        return cnvTo;
    }

    public void setCnvTo(ConvenioTransferObject cnvTo) {
        this.cnvTo = cnvTo;
    }

    public String getCsaMail() {
        return csaMail;
    }

    public void setCsaMail(String csaMail) {
        this.csaMail = csaMail;
    }

    public Short getIncMargem() {
        return incMargem;
    }

    public void setIncMargem(Short incMargem) {
        this.incMargem = incMargem;
    }

    @Override
    public void execute() throws ViewHelperException {
        enviaEmail(getInterpolador(ModeloEmailEnum.ENVIA_EMAIL_CSA_NOVA_SOLICITACAO_DO_SERVIDOR));
    }

    protected void enviaEmail(ModeloEmailInterpolator interpolador) throws ViewHelperException {
        try {
            if (TextHelper.isNull(csaMail)) {
                throw new ViewHelperException("mensagem.erro.parametro.email.notificacao.csa.vazio", responsavel);
            }

            ServidorDelegate serDelegate = new ServidorDelegate();
            ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            AutorizacaoDelegate autDelegate = new AutorizacaoDelegate();
            CoeficienteController coeficienteController = ApplicationContextProvider.getApplicationContext().getBean(CoeficienteController.class);

            CustomTransferObject servidor = serDelegate.buscaServidor(rseCodigo, responsavel);
            OrgaoTransferObject orgTo = getOrgao(cnvTo.getOrgCodigo(), responsavel);
            CustomTransferObject csaTo = csaDelegate.findConsignataria(cnvTo.getCsaCodigo(), responsavel);
            TransferObject adeTo = autDelegate.findAutDesconto(adeCodigo, responsavel);
            BigDecimal cftVlr = coeficienteController.getCftVlrByAdeCodigo(adeCodigo, responsavel);
            adeTo.setAttribute(Columns.CFT_VLR, cftVlr);
            servidor.setAttribute(Columns.RSE_MARGEM, rseMargemRestOld);

            MargemDisponivel margemDisponivel = new MargemDisponivel(rseCodigo, null, cnvTo.getSvcCodigo(), incMargem, responsavel);
            servidor.setAttribute(Columns.RSE_MARGEM_REST, margemDisponivel.getMargemRestante());

            CustomTransferObject dados = new CustomTransferObject();
            dados.setAtributos(servidor.getAtributos());
            dados.setAtributos(adeTo.getAtributos());
            dados.setAtributos(csaTo.getAtributos());
            dados.setAtributos(orgTo.getAtributos());

            interpolador.setDados(dados);

            // Interpola o template gerando os textos finais prontos para uso.
            String titulo = interpolador.interpolateTitulo();
            String corpo = interpolador.interpolateTexto();

            sendMail(titulo, corpo);
        } catch (ZetraException ex) {
            throw new ViewHelperException(ex.getMessageKey(), responsavel, ex);
        } catch (Exception ex) {
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }

    protected void sendMail(String titulo, String corpo) throws MessagingException {
        // Envia os emails.
        MailHelper mailHelper = new MailHelper();
        mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_CSA_NOVA_SOLICITACAO_FEITA_POR_SERVIDOR, csaMail, null, null, titulo, corpo, null, null, responsavel);
    }

    /**
     * Busca o template do e-mail
     * @param modelo
     * @return
     * @throws ViewHelperException
     */
    protected ModeloEmailInterpolator getInterpolador(ModeloEmailEnum modelo) throws ViewHelperException {
        ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(modelo, responsavel);
        return interpolador;
    }

}
