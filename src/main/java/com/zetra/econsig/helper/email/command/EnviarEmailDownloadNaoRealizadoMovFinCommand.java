package com.zetra.econsig.helper.email.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.mail.MessagingException;

import com.zetra.econsig.delegate.ConsignanteDelegate;
import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.dto.entidade.ConsignanteTransferObject;
import com.zetra.econsig.exception.ConsignanteControllerException;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ModeloEmailEnum;

/**
 * <p>Title: EnviarEmailDownloadNaoRealizadoMovFinCommand</p>
 * <p>Description: Command para envio de email de alerta download não realizado de movimento financeiro.</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailDownloadNaoRealizadoMovFinCommand extends AbstractEnviarEmailCommand {

    private List<TransferObject> listaArquivoMovFinSemDownload;

    public List<TransferObject> getListaArquivoMovFinSemDownload() {
        return listaArquivoMovFinSemDownload;
    }

    public void setListaArquivoMovFinSemDownload(List<TransferObject> listaArquivoMovFinSemDownload) {
        this.listaArquivoMovFinSemDownload = listaArquivoMovFinSemDownload;
    }

    @Override
    public void execute() throws ViewHelperException {
        String destinatario = null;

        try {
            ConsignanteDelegate cseDelegate = new ConsignanteDelegate();
            ConsignanteTransferObject cse = cseDelegate.findConsignante(CodedValues.CSE_CODIGO_SISTEMA, responsavel);
            destinatario = !TextHelper.isNull(cse.getCseEmailFolha()) ? cse.getCseEmailFolha() : cse.getCseEmail();
        } catch (ConsignanteControllerException e) {
            throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, e);
        }

        String[] emails = destinatario.replace(" ", "").split(",|;");
        List<String> destinatarios = new ArrayList<>(Arrays.asList(emails));

        if (ParamSist.getBoolParamSist(CodedValues.TPC_ENVIA_EMAIL_UPLOAD_ARQ_CSE_PARA_CSA, responsavel)) {
            try {
                // Seleciona consignatárias ativas que possuem email
                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CSA_ATIVO, CodedValues.STS_ATIVO);
                ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
                List<TransferObject> consignatarias = csaDelegate.lstConsignatariaConvenio(criterio, responsavel);
                for (TransferObject csa : consignatarias) {
                    if (!TextHelper.isNull(csa.getAttribute(Columns.CSA_EMAIL))) {
                        destinatarios.add(csa.getAttribute(Columns.CSA_EMAIL).toString());
                    }
                }
            } catch (ConsignatariaControllerException e) {
                throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, e);
            }
        }

        if (destinatarios == null || destinatarios.isEmpty()) {
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        // 1. Busca o template do e-mail
        ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIA_EMAIL_DOWNLOAD_NAO_REALIZADO_MOV_FIN, responsavel);

        // 2. Interpola o template gerando os textos finais prontos para uso.
        String titulo = interpolador.interpolateTitulo();

        String harNomeArquivo = listaArquivoMovFinSemDownload.stream().map(e -> e.getAttribute(Columns.HAR_NOME_ARQUIVO).toString() + ", ").reduce("", String::concat);
        if (harNomeArquivo.endsWith(", ")) {
            harNomeArquivo = harNomeArquivo.substring(0, harNomeArquivo.length() - 2);
        }

        // 3. Preenche dos dados disponíveis para uso no template
        CustomTransferObject dados = new CustomTransferObject();
        dados.setAttribute("arquivo", harNomeArquivo);
        interpolador.setDados(dados);
        String corpo = interpolador.interpolateTexto();

        // Envia o email
        try {
            MailHelper mailHelper = new MailHelper();
            if (destinatarios.size() == 1) {
                mailHelper.send(destinatarios.get(0).trim(), null, null, titulo, corpo, null);
            } else {
                // Se há mais de um destinatário, envia para todos via cópia carbono.
                mailHelper.send(null, null, TextHelper.join(destinatarios, ","), titulo, corpo, null);
            }
        } catch (MessagingException e) {
            throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, e);
        }

    }

}
