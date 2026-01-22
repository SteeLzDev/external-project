package com.zetra.econsig.job.process.agendado;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.notificacao.NotificacaoDispositivoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.TipoNotificacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaNotificacaoNovaPropostaLeilao</p>
 * <p>Description: Processo periódico que confere e envia notificações de novas propostas de leilão aos dispositivos de usuários.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaNotificacaoNovaPropostaLeilao extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaNotificacaoNovaPropostaLeilao.class);

    public ProcessaNotificacaoNovaPropostaLeilao(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_LEILAO_VIA_SIMULACAO_DO_SERVIDOR, CodedValues.TPC_SIM, getResponsavel())) {
            NotificacaoDispositivoController notificacaoDispositivoController = ApplicationContextProvider.getApplicationContext().getBean(NotificacaoDispositivoController.class);
            List<TransferObject> lstNdiAenviar = notificacaoDispositivoController.lstNotificacoes(CodedValues.FUN_INFORMAR_PROPOSTAS_LEILAO, TipoNotificacaoEnum.NOVA_PROPOSTA_LEILAO.getCodigo(), true, getResponsavel());
            UsuarioDelegate usuDelegate = new UsuarioDelegate();

            String textPush = ApplicationResourcesHelper.getMessage("mensagem.notificacao.dispositivo.leilao.nova.proposta", getResponsavel());
            String tituloPush = ApplicationResourcesHelper.getMessage("mensagem.notificacao.dispositivo.leilao.nova.proposta.titulo", getResponsavel());

            for (TransferObject notificacao: lstNdiAenviar) {
                // Envio somente uma notificação de nova proposta por servidor
                String usuCodigoDestinatario = (String) notificacao.getAttribute(Columns.NDI_USU_CODIGO_DESTINATARIO);
                String deviceToken = usuDelegate.findDeviceToken(usuCodigoDestinatario, getResponsavel());

                try {
                    if (!TextHelper.isNull(deviceToken)) {
                        String ndiTexto = (String) notificacao.getAttribute(Columns.NDI_TEXTO);
                        HashMap<String, Object> filters = new ObjectMapper().readValue(ndiTexto, HashMap.class);

                        notificacaoDispositivoController.enviarNotificacao((String) notificacao.getAttribute(Columns.NDI_CODIGO), deviceToken, ndiTexto, textPush, tituloPush, usuCodigoDestinatario, TipoNotificacaoEnum.NOVA_PROPOSTA_LEILAO.getCodigo(), true, filters.get("ade_numero").toString(), getResponsavel());
                        notificacaoDispositivoController.registrarEnvioNotificacoesInativas((String) notificacao.getAttribute(Columns.NDI_CODIGO), (String) notificacao.getAttribute(Columns.NDI_FUN_CODIGO), usuCodigoDestinatario, TipoNotificacaoEnum.NOVA_PROPOSTA_LEILAO.getCodigo(), getResponsavel());
                    }
                } catch (IOException e) {
                    LOG.error("Não foi possível gerar o CollapseId ");
                }
            }
        }
    }
}
