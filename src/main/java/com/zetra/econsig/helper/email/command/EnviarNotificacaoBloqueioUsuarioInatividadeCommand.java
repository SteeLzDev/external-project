package com.zetra.econsig.helper.email.command;

import java.util.List;

import com.zetra.econsig.delegate.UsuarioDelegate;
import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
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
 * <p>Title: public class EnviarNotificacaoBloqueioUsuarioInatividadeCommand</p>
 * <p>Description: Envia e-mail aos usuários que devem ser notificados sobre bloqueio por tempo inatividade.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnviarNotificacaoBloqueioUsuarioInatividadeCommand extends AbstractEnviarEmailCommand {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(EnviarNotificacaoBloqueioUsuarioInatividadeCommand.class);

    @Override
    public void execute() throws ViewHelperException {
        try {
            UsuarioDelegate usuDelegate = new UsuarioDelegate();
            List<TransferObject> lista = usuDelegate.enviaNotificacaoUsuariosPorTempoInatividade(getResponsavel());
            boolean envioAposPriAcesso = ParamSist.getInstance().getParam(CodedValues.TPC_ENVIAR_EMAIL_APOS_PRI_ACESSO, responsavel) != null && ParamSist.getInstance().getParam(CodedValues.TPC_ENVIAR_EMAIL_APOS_PRI_ACESSO, responsavel).equals(CodedValues.TPC_SIM);

            // Se a lista for nula, não ha necessidade do envio de e-mail.
            if (lista != null && !lista.isEmpty()) {
                // 1. Busca o template do e-mail
                ModeloEmailInterpolator interpolador = getModeloEmailInterpolator(ModeloEmailEnum.ENVIA_EMAIL_BLOQUEIO_USUARIO_INATIVIDADE, responsavel);
                CustomTransferObject dados = new CustomTransferObject();

                MailHelper mailHelper = new MailHelper();
                String usuEmail = null;
                String usuNome = null;
                String titulo = null;
                String corpo = null;
                String dataLimiteAcesso = null;
                boolean primeiroAcesso = false;

                for (TransferObject to : lista) {
                    usuNome = (String) to.getAttribute(Columns.USU_NOME);
                    usuEmail = (String) to.getAttribute(Columns.USU_EMAIL);
                    primeiroAcesso = to.getAttribute(Columns.USU_DATA_ULT_ACESSO) != null;
                    dataLimiteAcesso = to.getAttribute("dataLimiteAcesso").toString();

                    if (!TextHelper.isNull(usuEmail)) {
                        if (TextHelper.isEmailValid(usuEmail)) {
                            if (envioAposPriAcesso && primeiroAcesso || !envioAposPriAcesso) {
                                // 2. Setando dados no interpolador
                                dados.setAttribute("usu_nome", usuNome);
                                dados.setAttribute("dataLimiteAcesso", dataLimiteAcesso);
                                interpolador.setDados(dados);

                                // 3. Interpola o template gerando os textos finais prontos para uso.
                                titulo = interpolador.interpolateTitulo();
                                corpo = interpolador.interpolateTexto();

                                // Envia os emails.
                                mailHelper.send(TipoNotificacaoEnum.EMAIL_NOTIFICACAO_BLOQUEIO_USUARIO_INATIVIDADE, usuEmail.replaceAll(";", ","), null, null, titulo, corpo, null, null, responsavel);
                            }
                        } else {
                            LOG.warn(ApplicationResourcesHelper.getMessage("mensagem.log.aviso.notificar.usuario.bloqueio.inatividade.email.invalido", responsavel, usuEmail, usuNome));
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            throw new ViewHelperException("mensagem.erroInternoSistema", responsavel, ex);
        }
    }
}
