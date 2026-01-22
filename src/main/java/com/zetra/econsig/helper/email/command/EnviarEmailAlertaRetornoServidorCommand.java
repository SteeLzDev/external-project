package com.zetra.econsig.helper.email.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.mail.MessagingException;

import com.zetra.econsig.delegate.ServidorDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ServidorControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

/**
 * <p>Title: EnviarEmailAlertaRetornoServidorCommand</p>
 * <p>Description: Command para envio de email de notificação de retorno do servidor/funcionário.</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailAlertaRetornoServidorCommand extends AbstractEnviarEmailCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailAlertaRetornoServidorCommand.class);

    private List<Integer> diasParam;

    public List<Integer> getDiasParam() {
        return diasParam;
    }

    public void setDiasParam(List<Integer> diasParam) {
        this.diasParam = diasParam;
    }

    @Override
    public void execute() throws ViewHelperException {
        class EmailToSend {
            private String email;
            private final List<CustomTransferObject> servidores;
            private String csaNome;

            public EmailToSend() {
                servidores = new ArrayList<>();
            }

            public String getEmail() {
                return email;
            }

            public void setEmail(String email) {
                this.email = email;
            }

            public List<CustomTransferObject> getServidores() {
                return servidores;
            }

            public void addServidor(CustomTransferObject servidor) {
                servidores.add(servidor);
            }

            public String getCsaNome() {
                return csaNome;
            }

            public void setCsaNome(String csaNome) {
                this.csaNome = csaNome;
            }
        }

        Map<String, EmailToSend> mapaEmailsParaEnviar = new HashMap<>();
        try {
            List<TransferObject> codServidores = new ServidorDelegate().listarCodigoServidorConsignacaoAtivaRetorno(diasParam, responsavel);
            for (TransferObject transfer : codServidores) {
                String csaEmail = (String) transfer.getAttribute(Columns.CSA_EMAIL);
                String csaCodigo = (String) transfer.getAttribute(Columns.CSA_CODIGO);
                String csaNome = (String) transfer.getAttribute(Columns.CSA_NOME);
                EmailToSend mapEntry = mapaEmailsParaEnviar.get(csaCodigo);
                if (mapEntry == null) {
                    mapEntry = new EmailToSend();
                    mapEntry.setEmail(csaEmail);
                    mapEntry.setCsaNome(csaNome);
                }
                mapaEmailsParaEnviar.put(csaCodigo, mapEntry);
                CustomTransferObject servidor = new CustomTransferObject();
                servidor.setAtributos(transfer.getAtributos());
                mapEntry.addServidor(servidor);
            }
        } catch (ServidorControllerException e1) {
            e1.printStackTrace();
        }

        for (java.util.Map.Entry<String, EmailToSend> entryMap : mapaEmailsParaEnviar.entrySet()) {
            EmailToSend emailParaEnvio = entryMap.getValue();
            String destinatario = emailParaEnvio.getEmail();
            if (TextHelper.isNull(destinatario)) {
                LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.erro.email.destinatario.csa.invalido", responsavel, emailParaEnvio.getCsaNome()));
                continue;
            }

            // Envia o email
            if (!emailParaEnvio.getServidores().isEmpty()) {
                String[] emails = destinatario.replace(" ", "").split(",|;");
                List<String> destinatarios = Arrays.asList(emails);

                // 1. Busca o template do e-mail
                ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_ALERTA_RETORNO_SERVIDOR, responsavel);

                // 2. Preenche dos dados disponíveis para uso no template
                CustomTransferObject dados = new CustomTransferObject();
                dados.setAttribute("csa_nome", emailParaEnvio.getCsaNome());
                dados.setAttribute("servidores", emailParaEnvio.getServidores());

                // Adiciona os parâmetros abaixo:
                // URL do sistema (parâmetro de sistema 256);
                dados.setAttribute("url_sistema", ParamSist.getInstance().getParam(CodedValues.TPC_LINK_ACESSO_SISTEMA, responsavel));
                // E-mail do suporte (parâmetro de sistema 207);
                dados.setAttribute("email_suporte", ParamSist.getInstance().getParam(CodedValues.TPC_EMAIL_SUPORTE_ZETRASOFT, responsavel));
                // Telefone do suporte (parâmetro de sistema 258).
                dados.setAttribute("telefone_suporte", ParamSist.getInstance().getParam(CodedValues.TPC_TELEFONE_SUPORTE_ZETRASOFT, responsavel));
                interpolador.setDados(dados);

                // 3. Interpola o template gerando os textos finais prontos para uso.
                String titulo = interpolador.interpolateTitulo();
                String corpo = interpolador.interpolateTexto();

                try {
                    MailHelper mailHelper = new MailHelper();
                    if (destinatarios.size() == 1) {
                        mailHelper.send(TipoNotificacaoEnum.EMAIL_RETORNO_SERVIDOR, destinatarios.get(0), null, null, titulo, corpo, null, null, responsavel);
                    } else {
                        // Se há mais de um destinatário, envia para todos via cópia carbono.
                        mailHelper.send(TipoNotificacaoEnum.EMAIL_RETORNO_SERVIDOR, TextHelper.join(destinatarios, ","), null, null, titulo, corpo, null, null, responsavel);
                    }
                } catch (MessagingException e) {
                    throw new ViewHelperException("mensagem.erro.email.indisponivel", responsavel, e);
                }
            }
        }
    }
}
