package com.zetra.econsig.helper.email.command;

import java.util.ArrayList;
import java.util.List;

import jakarta.mail.MessagingException;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ModeloEmailEnum;

/**
 * <p>Title: EnviarEmailUploadArquivoCsaCommand</p>
 * <p>Description: Command para envio de email no upload de arquivos de margem e retorno para csa/cor.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailUploadArquivoCsaCommand extends AbstractEnviarEmailCommand {

    private String tipo;
    private String nomeArquivo;
    private boolean enviaEmailCSA;
    private boolean enviaEmailCOR;
    private String observacao;


    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public void setEnviaEmailCSA(boolean enviaEmailCSA) {
        this.enviaEmailCSA = enviaEmailCSA;
    }

    public void setEnviaEmailCOR(boolean enviaEmailCOR) {
        this.enviaEmailCOR = enviaEmailCOR;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    @Override
    public void execute() throws ViewHelperException {
        try {
            ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            List<String> destinatarios = new ArrayList<String>();

            if (enviaEmailCSA) {
                // Seleciona consignatárias ativas que possuem email
                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.CSA_ATIVO, CodedValues.STS_ATIVO);
                List<TransferObject> consignatarias = csaDelegate.lstConsignatariaConvenio(criterio, responsavel);
                for (TransferObject csa : consignatarias) {
                    if (!TextHelper.isNull(csa.getAttribute(Columns.CSA_EMAIL))) {
                        destinatarios.add(csa.getAttribute(Columns.CSA_EMAIL).toString());
                    }
                }
            }

            if (enviaEmailCOR) {
                TransferObject criterio = new CustomTransferObject();
                criterio.setAttribute(Columns.COR_ATIVO, CodedValues.STS_ATIVO);
                List<TransferObject> correspondentes = csaDelegate.lstCorrespondenteConvenio(criterio, responsavel);
                for (TransferObject cor : correspondentes) {
                    if (!TextHelper.isNull(cor.getAttribute(Columns.COR_EMAIL))) {
                        destinatarios.add(cor.getAttribute(Columns.COR_EMAIL).toString());
                    }
                }
            }

            // Se não possui nenhuma consignatária ou correspondente com e-mail cadastrado, não tenta enviar e-mail
            if (destinatarios.isEmpty()) {
                return;
            }

            // 1. Busca o template do e-mail
            ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_UPLOAD_ARQUIVO_CSA, responsavel);

            // 2. Preenche dos dados disponíveis para uso no template
            CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute("tipo_arquivo", tipo);
            dados.setAttribute("nome_arquivo", nomeArquivo);
            if(!TextHelper.isNull(observacao)) {
                observacao = ApplicationResourcesHelper.getMessage("rotulo.email.observacao", responsavel, observacao);
            }

            dados.setAttribute("observacao_upload_label_html_noescape", observacao);

            interpolador.setDados(dados);

            // 3. Interpola o template gerando os textos finais prontos para uso.
            String titulo = interpolador.interpolateTitulo();
            String corpo = interpolador.interpolateTexto();

            MailHelper mailHelper = new MailHelper();
            try {
                if (destinatarios.size() == 1) {
                    mailHelper.send(destinatarios.get(0).replaceAll(";", ","), null, null, titulo, corpo, null);
                } else {
                    // Se há mais de um destinatário, envia para todos via cópia carbono.
                    mailHelper.send(null, null, TextHelper.join(destinatarios, ","), titulo, corpo, null);
                }
            } catch (MessagingException e) {
                throw new ViewHelperException("mensagem.erro.email.recebimento.arquivo", responsavel, e, tipo);
            }
        } catch (ConsignatariaControllerException e) {
            throw new ViewHelperException("mensagem.erro.email.recebimento.arquivo", responsavel, e, tipo);
        }
    }
}
