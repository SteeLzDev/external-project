package com.zetra.econsig.job.process.agendado;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.comunicacao.ComunicacaoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaEnvioEmailComunicacaoNaoLida</p>
 * <p>Description: Processo para envio de notificação de comunicação não lida</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEnvioEmailComunicacaoNaoLida extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnvioEmailComunicacaoNaoLida.class);

    public ProcessaEnvioEmailComunicacaoNaoLida(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        LOG.debug("Executa envio de email de notificação de comunicação não lida.");

        String diasNotificacaoCseOrg = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_NOTIFICACAO_COMUNICACAO_NAO_LIDA_CSE_ORG, getResponsavel());
        String diasNotificacaoCsaCor = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_NOTIFICACAO_COMUNICACAO_NAO_LIDA_CSA_COR, getResponsavel());
        String diasNotificacaoSer    = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DIAS_NOTIFICACAO_COMUNICACAO_NAO_LIDA_SER, getResponsavel());

        if (!TextHelper.isNull(diasNotificacaoCseOrg) || !TextHelper.isNull(diasNotificacaoCsaCor) || !TextHelper.isNull(diasNotificacaoSer)) {
            ComunicacaoController comunicacaoController = ApplicationContextProvider.getApplicationContext().getBean(ComunicacaoController.class);

            if (!TextHelper.isNull(diasNotificacaoCseOrg)) {
                // Busca comunicação não lida que o destinatário é CSE ou ORG
                for (String diasAposEnvio : diasNotificacaoCseOrg.split(",")) {
                    if (TextHelper.isNum(diasAposEnvio.trim())) {
                        // Consignante
                        List<TransferObject> consignantes = comunicacaoController.listarComunicacaoNaoLida(Integer.valueOf(diasAposEnvio.trim()), CodedValues.PAP_CONSIGNANTE, getResponsavel());
                        if (consignantes != null && !consignantes.isEmpty()) {
                            for (TransferObject cse : consignantes) {
                                String cseNome = (String) cse.getAttribute(Columns.CSE_NOME);
                                String cseEmail = (String) cse.getAttribute(Columns.CSE_EMAIL);
                                Long qtdComunicacoesNaoLidas = (Long) cse.getAttribute("QTD_CMN_NAO_LIDA");

                                if (qtdComunicacoesNaoLidas > 0) {
                                    if (!TextHelper.isNull(cseEmail)) {
                                        // Envia E-mail
                                        LOG.info("O consignante \"" + cseNome + "\" possui \"" + qtdComunicacoesNaoLidas + "\" comunicações não lidas e será notificada no e-mail \"" + cseEmail + "\".");
                                        EnviaEmailHelper.enviarEmailComunicacaoNoaLida(cseEmail, cseNome, qtdComunicacoesNaoLidas, getResponsavel());
                                    } else {
                                        LOG.warn("O consignante \"" + cseNome + "\" possui \"" + qtdComunicacoesNaoLidas + "\" comunicações não lidas porém não possui e-mail cadastrado para notificação.");
                                    }
                                }
                            }
                        }
                        // Órgão
                        List<TransferObject> orgaos = comunicacaoController.listarComunicacaoNaoLida(Integer.valueOf(diasAposEnvio.trim()), CodedValues.PAP_ORGAO, getResponsavel());
                        if (orgaos != null && !orgaos.isEmpty()) {
                            for (TransferObject org : orgaos) {
                                String orgNome = (String) org.getAttribute(Columns.ORG_NOME);
                                String orgEmail = (String) org.getAttribute(Columns.ORG_EMAIL);
                                Long qtdComunicacoesNaoLidas = (Long) org.getAttribute("QTD_CMN_NAO_LIDA");

                                if (qtdComunicacoesNaoLidas > 0) {
                                    if (!TextHelper.isNull(orgEmail)) {
                                        // Envia E-mail
                                        LOG.info("O órgão \"" + orgNome + "\" possui \"" + qtdComunicacoesNaoLidas + "\" comunicações não lidas e será notificada no e-mail \"" + orgEmail + "\".");
                                        EnviaEmailHelper.enviarEmailComunicacaoNoaLida(orgEmail, orgNome, qtdComunicacoesNaoLidas, getResponsavel());
                                    } else {
                                        LOG.warn("O órgão \"" + orgNome + "\" possui \"" + qtdComunicacoesNaoLidas + "\" comunicações não lidas porém não possui e-mail cadastrado para notificação.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!TextHelper.isNull(diasNotificacaoCsaCor)) {
                // Busca comunicação não lida que o destinatário é CSA ou COR
                for (String diasAposEnvio : diasNotificacaoCsaCor.split(",")) {
                    if (TextHelper.isNum(diasAposEnvio.trim())) {
                        List<TransferObject> consignatarias = comunicacaoController.listarComunicacaoNaoLida(Integer.valueOf(diasAposEnvio.trim()), CodedValues.PAP_CONSIGNATARIA, getResponsavel());
                        if (consignatarias != null && !consignatarias.isEmpty()) {
                            for (TransferObject csa : consignatarias) {
                                String csaNome = (String) csa.getAttribute(Columns.CSA_NOME);
                                String csaEmail = (String) csa.getAttribute(Columns.CSA_EMAIL);
                                Long qtdComunicacoesNaoLidas = (Long) csa.getAttribute("QTD_CMN_NAO_LIDA");

                                if (qtdComunicacoesNaoLidas > 0) {
                                    if (!TextHelper.isNull(csaEmail)) {
                                        // Envia E-mail
                                        LOG.info("A consignatária \"" + csaNome + "\" possui \"" + qtdComunicacoesNaoLidas + "\" comunicações não lidas e será notificada no e-mail \"" + csaEmail + "\".");
                                        EnviaEmailHelper.enviarEmailComunicacaoNoaLida(csaEmail, csaNome, qtdComunicacoesNaoLidas, getResponsavel());
                                    } else {
                                        LOG.warn("A consignatária \"" + csaNome + "\" possui \"" + qtdComunicacoesNaoLidas + "\" comunicações não lidas porém não possui e-mail cadastrado para notificação.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (!TextHelper.isNull(diasNotificacaoSer)) {
                // Busca comunicação não lida que o destinatário é SER
                for (String diasAposEnvio : diasNotificacaoSer.split(",")) {
                    if (TextHelper.isNum(diasAposEnvio.trim())) {
                        List<TransferObject> servidores = comunicacaoController.listarComunicacaoNaoLida(Integer.valueOf(diasAposEnvio.trim()), CodedValues.PAP_SERVIDOR, getResponsavel());
                        if (servidores != null && !servidores.isEmpty()) {
                            for (TransferObject ser : servidores) {
                                String serNome = (String) ser.getAttribute(Columns.SER_NOME);
                                String serEmail = (String) ser.getAttribute(Columns.SER_EMAIL);
                                Long qtdComunicacoesNaoLidas = (Long) ser.getAttribute("QTD_CMN_NAO_LIDA");

                                if (qtdComunicacoesNaoLidas > 0) {
                                    if (!TextHelper.isNull(serEmail)) {
                                        // Envia E-mail
                                        LOG.info("O servidor \"" + serNome + "\" possui \"" + qtdComunicacoesNaoLidas + "\" comunicações não lidas e será notificada no e-mail \"" + serEmail + "\".");
                                        EnviaEmailHelper.enviarEmailComunicacaoNoaLida(serEmail, serNome, qtdComunicacoesNaoLidas, getResponsavel());
                                    } else {
                                        LOG.warn("O servidor \"" + serNome + "\" possui \"" + qtdComunicacoesNaoLidas + "\" comunicações não lidas porém não possui e-mail cadastrado para notificação.");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            LOG.debug("Notificação de comunicação não lida está desabilitado.");
        }
    }
}
