package com.zetra.econsig.helper.email.command;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jakarta.mail.MessagingException;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.web.JspHelper;
import com.zetra.econsig.values.ModeloEmailEnum;

/**
 * <p>Title: EnviarEmailExpiracaoConsignatariaCommand</p>
 * <p>Description: Command para envio de email de expiracão de consignatárias para cse/org</p>
 * <p>Copyright: Copyright (c) 2006</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarEmailExpiracaoConsignatariaCommand extends AbstractEnviarEmailCommand {

    AcessoSistema responsavel;
    Map<String, List<ConsignatariaTransferObject>> prazoConsignatarias;
    String cseNome;
    String email;

    @Override
    public void execute() throws ViewHelperException {

        String nomeSistema = JspHelper.getNomeSistema(responsavel);

        ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_EXPIRACAO_CSA, responsavel);
        CustomTransferObject dados = new CustomTransferObject();

        dados.setAttribute("nome_sistema", nomeSistema);
        dados.setAttribute("cse_nome", cseNome);

        interpolador.setDados(dados);

        String titulo = interpolador.interpolateTitulo();

        StringBuilder texto = new StringBuilder();
        texto.append(interpolador.interpolateTexto());

        CustomTransferObject dadosCsa = new CustomTransferObject();
        interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_EXPIRACAO_CSA_TEXTO, responsavel);

        Set<Entry<String, List<ConsignatariaTransferObject>>> entrySet = prazoConsignatarias.entrySet();
        for (Entry<String, List<ConsignatariaTransferObject>> entry : entrySet) {

            dadosCsa.setAttribute("dias", entry.getKey());
            interpolador.setDados(dadosCsa);
            texto.append(interpolador.interpolateTitulo());

            for (ConsignatariaTransferObject consignataria : entry.getValue()) {
                dadosCsa.setAttribute("csa_nome", consignataria.getCsaNome());
                interpolador.setDados(dadosCsa);
                texto.append(interpolador.interpolateTexto());
            }

        }

        MailHelper mailHelper = new MailHelper();
        try {
            mailHelper.send(email, null, null, titulo, texto.toString(), null);
        } catch (MessagingException e) {
            throw new ViewHelperException ("mensagem.erro.email.alerta.dias.expiracao.consignataria", responsavel, e);
        }


    }

    public void setPrazoConsignatarias(Map<String, List<ConsignatariaTransferObject>> prazoConsignatarias) {
        this.prazoConsignatarias = prazoConsignatarias;
    }

    @Override
    public void setResponsavel(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCseNome(String cseNome) {
        this.cseNome = cseNome;
    }


}
