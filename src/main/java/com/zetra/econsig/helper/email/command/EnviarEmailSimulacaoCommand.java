package com.zetra.econsig.helper.email.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.email.MailHelper;
import com.zetra.econsig.helper.email.modelo.ModeloEmailInterpolator;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.ModeloEmailEnum;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.webservice.rest.request.CsaListInfoRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnviarEmailSimulacaoCommand extends AbstractEnviarEmailCommand {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarEmailSimulacaoCommand.class);

    private String serEmail;
    private String nomeUsu;
    private String anexoEmail;
    private String cseNome;
    private List<CsaListInfoRequest> listInfor;
    private AcessoSistema responsavel;

    @Override
    public void execute() throws ViewHelperException {
        try {
            final ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIA_EMAIL_SIMULACAO_CONSIGNACAO, responsavel);
            if (TextHelper.isNull(interpolador)) {
                throw new ViewHelperException("mensagem.erro.email.modelo.nao.encontrado", responsavel);
            }

            if (TextHelper.isNull(serEmail)) {
                throw new ViewHelperException("mensagem.informe.email.destinatario", responsavel);
            }

            final List<TransferObject> listInfoFull = new ArrayList<>();
            final String naoInformado = ApplicationResourcesHelper.getMessage("rotulo.email.simulacao.ranking.nao.informado.contato", responsavel);

            for (final CsaListInfoRequest info : listInfor) {
                final TransferObject obj = new CustomTransferObject();
                obj.setAttribute("ranking", info.getRanking());
                obj.setAttribute("nome", info.getCsaNome() != null ? info.getCsaNome() : naoInformado);
                obj.setAttribute("email", info.getCsaEmail() != null ? info.getCsaEmail() : naoInformado);
                obj.setAttribute("whatsapp", info.getCsaWhatsapp() != null ? info.getCsaWhatsapp() : naoInformado);
                obj.setAttribute("texto", info.getCsaTxt() != null ? info.getCsaTxt() : naoInformado);

                listInfoFull.add(obj);
            }

            final CustomTransferObject dados = new CustomTransferObject();
            dados.setAttribute("infor_csas", listInfoFull);
            dados.setAttribute("cse_nome", cseNome);
            dados.setAttribute("data_hora_atual", DateHelper.getSystemDatetime());
            dados.setAttribute("usu_nome", nomeUsu);
            interpolador.setDados(dados);

            final String titulo = interpolador.interpolateTitulo();
            final String corpo = interpolador.interpolateTexto();

            final MailHelper mailHelper = new MailHelper();
            mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_SIMULACAO_CONSIGNACAO, serEmail, null, null, titulo, corpo, Collections.singletonList(anexoEmail), null, responsavel);

        } catch (final Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erro.falha.enviar.email", responsavel, ex);
        }
    }
}
