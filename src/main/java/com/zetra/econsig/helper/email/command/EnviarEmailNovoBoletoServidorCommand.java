package com.zetra.econsig.helper.email.command;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jakarta.mail.MessagingException;

import org.apache.commons.io.FileUtils;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.arquivo.FileHelper;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.SynchronizerToken;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailNovoBoletoServidorCommand</p>
 * <p>Description: Command para envio de email de notificação de novo boleto para o servidor.</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailNovoBoletoServidorCommand extends AbstractEnviarEmailCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNovoBoletoServidorCommand.class);

    private String serEmail;
    private String remetente;
    private TransferObject servidor;
    private byte[] conteudoArquivoPdf;

    public void setSerEmail(String serEmail) {
        this.serEmail = serEmail;
    }

    public void setRemetente(String remetente) {
        this.remetente = remetente;
    }

    public void setServidor(TransferObject servidor) {
        this.servidor = servidor;
    }

    public void setConteudoArquivoPdf(byte[] conteudoArquivoPdf) {
        this.conteudoArquivoPdf = conteudoArquivoPdf;
    }

    @Override
    public void execute() throws ViewHelperException {
        if (TextHelper.isNull(serEmail)) {
            throw new ViewHelperException("mensagem.erro.email.destinatario.invalido", responsavel);
        }

        // 1. Gera o anexo, caso seja informado
        List<String> anexos = null;
        if (conteudoArquivoPdf != null) {
            // 1.1. Salva o arquivo em pasta temporária
            String dirArquivo = ParamSist.getDiretorioRaizArquivos()
                              + File.separator + "temp"
                              + File.separator + "anexo";

            String nomeArquivo = dirArquivo + File.separator + SynchronizerToken.generateToken() + ".pdf";

            try {
                FileUtils.forceMkdir(new File(dirArquivo));
                FileHelper.saveByteArrayToFile(conteudoArquivoPdf, nomeArquivo);
            } catch (IOException ex) {
                throw new ViewHelperException("mensagem.erro.email.anexo.indisponivel", responsavel, ex);
            }

            // 1.2. Adiciona o arquivo como recurso para anexo no e-mail
            anexos = new ArrayList<>();
            anexos.add(nomeArquivo);
        }

        // 2. Busca o template do e-mail
        ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIA_EMAIL_NOVO_BOLETO_SERVIDOR, responsavel);

        // 3. Interpola o template gerando os textos finais prontos para uso.
        String titulo = interpolador.interpolateTitulo();

        // 4. Preenche dos dados disponíveis para uso no template
        CustomTransferObject dados = new CustomTransferObject(servidor);
        dados.setAttribute("remetente", remetente);
        dados.setAttribute("arquivo", (anexos != null && !anexos.isEmpty() ? new File(anexos.get(0)).getName() : null));

        // Adiciona os parâmetros abaixo:
        // URL do sistema (parâmetro de sistema 256);
        dados.setAttribute("url_sistema", ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel));
        // E-mail do suporte (parâmetro de sistema 207);
        dados.setAttribute("email_suporte", ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel));
        // Telefone do suporte (parâmetro de sistema 258).
        dados.setAttribute("telefone_suporte", ParamSist.getInstance().getParam(CodedValues.TPC_TELEFONE_SUPORTE_ZETRASOFT, responsavel));

        interpolador.setDados(dados);
        String corpo = interpolador.interpolateTexto();

        // 5. Envia o email
        try {
            MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.NOVO_BOLETO_SERVIDOR, serEmail, null, null, titulo, corpo, anexos, null, responsavel);
        } catch (MessagingException ex) {
            throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, ex);
        } finally {
            try {
                // 6. Remove os anexos
                if (anexos != null && !anexos.isEmpty()) {
                    for (String anexo : anexos) {
                        FileHelper.delete(anexo);
                    }
                }
            } catch (IOException ex) {
                LOG.error(ex.getMessage(), ex);
            }
        }
    }
}
