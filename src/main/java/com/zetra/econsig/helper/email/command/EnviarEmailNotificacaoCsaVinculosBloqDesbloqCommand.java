package com.zetra.econsig.helper.email.command;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.entity.OcorrenciaConsignataria;
import com.zetra.econsig.persistence.entity.OcorrenciaConsignatariaHome;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;

public class EnviarEmailNotificacaoCsaVinculosBloqDesbloqCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailNotificacaoCsaVinculosBloqDesbloqCommand.class);

    private String csaEmail;
    private String csaCodigo;
    private String csaNome;
    private List<String> occCodigos;

    @Override
    public void execute() throws ViewHelperException {
        try {
            // 1. Busca o template do e-mail
            final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIAR_EMAIL_NOTIFICACAO_VINCULOS_BLOQ_DESBLOQ, responsavel);

            if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

            if (TextHelper.isNull(csaEmail)) {
                throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
            }
            List<String> bloqueados = new ArrayList<>();
            List<String> desbloqueados = new ArrayList<>();
            for(String occCodigo : occCodigos) {
                OcorrenciaConsignataria occBean = OcorrenciaConsignatariaHome.findByPrimaryKey(occCodigo);
                String occData = DateHelper.reformat(occBean.getOccData().toString(), "yyyy-MM-dd HH:mm:ss", LocaleHelper.getDateTimePattern());
                String occ = occData + " - " + occBean.getUsuario().getUsuNome() + " - " + occBean.getOccObs();
                if(occBean.getTocCodigo().equals(CodedValues.TOC_BLOQUEIO_VINCULO)) {
                    bloqueados.add(occ);
                } else {
                    desbloqueados.add(occ);
                }
            }
            // 2. Preenche dos dados disponíveis para uso no template
            final CustomTransferObject dados = new CustomTransferObject();
            setDadosTemplateEmail(dados);
            dados.setAttribute("csa_nome", csaNome);
            dados.setAttribute("bloqueados", String.join("<br>", bloqueados));
            dados.setAttribute("desbloqueados", String.join("<br>", desbloqueados));
            interpolador.setDados(dados);

            // 3. Interpola o template gerando os textos finais prontos para uso.
            final String titulo = interpolador.interpolateTitulo();
            final String corpo = interpolador.interpolateTexto();

            // Envia os emails.
            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_VINCULOS_BLOQ_DESBLOQ, csaEmail, null, null, titulo, corpo, null, null, responsavel);
        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.falha.enviar.email", responsavel, ex);
        }
    }

    public String getCsaEmail() {
        return csaEmail;
    }

    public void setCsaEmail(String csaEmail) {
        this.csaEmail = csaEmail;
    }

    public String getCsaCodigo() {
        return csaCodigo;
    }

    public void setCsaCodigo(String csaCodigo) {
        this.csaCodigo = csaCodigo;
    }

    public String getCsaNome() {
        return csaNome;
    }

    public void setCsaNome(String csaNome) {
        this.csaNome = csaNome;
    }

    public List<String> getOccCodigos() {
        return occCodigos;
    }

    public void setOccCodigos(List<String> occCodigos) {
        this.occCodigos = occCodigos;
    }
}
