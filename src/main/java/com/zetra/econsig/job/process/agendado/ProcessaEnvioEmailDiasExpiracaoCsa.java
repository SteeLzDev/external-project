package com.zetra.econsig.job.process.agendado;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.delegate.ConsignatariaDelegate;
import com.zetra.econsig.dto.entidade.ConsignatariaTransferObject;
import com.zetra.econsig.exception.ConsignatariaControllerException;
import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.email.EnviaEmailHelper;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.persistence.entity.CredenciamentoCsa;
import com.zetra.econsig.service.consignataria.ConsignatariaController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.StatusCredenciamentoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaEnvioEmailDiasExpiracaoCsa</p>
 * <p>Description: Verifica e envia e-mail de alerta às consignatárias cuja data de expiração está próxima.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaEnvioEmailDiasExpiracaoCsa extends ProcessoAgendadoPeriodico {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaEnvioEmailDiasExpiracaoCsa.class);

    public ProcessaEnvioEmailDiasExpiracaoCsa(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        if (ParamSist.paramEquals(CodedValues.TPC_ENVIA_EMAIL_EXPIRACAO_CONSIGNATARIAS, CodedValues.TPC_SIM, getResponsavel())) {
            LOG.debug("Executa Envio de Email de alerta de dias remanescentes para expiração das consignatárias");
            ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            Date hoje = DateHelper.getSystemDate();

            String tpcPrzEnvioEmailExpCsa = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PRAZO_ENVIA_EMAIL_EXPIRACAO_CSA, getResponsavel());
            String[] prazos = null;
            if (!TextHelper.isNull(tpcPrzEnvioEmailExpCsa)) {
                prazos = tpcPrzEnvioEmailExpCsa.replace(" ", "").split(",|;");
            }

            // Se não houver prazo cadastrado, utiliza o padrão
            if (prazos == null || prazos.length == 0) {
                prazos = new String[] { "10", "20", "30" };
            }

            // envia email de alerta para consignatárias a bloquear de acordo com os prazos cadastrados
            for (String prazo : prazos) {
                if (!TextHelper.isNull(prazo)) {
                    try {
                        enviaEmailAlertaBloqueio(Integer.parseInt(prazo.trim()), hoje, csaDelegate);
                    } catch (NumberFormatException ex) {
                        LOG.error("Não foi possível enviar e-mail de alerta para as consignatárias a bloquear para o prazo: " + prazo + ".", ex);
                    }
                }
            }

            if (ParamSist.getBoolParamSist(CodedValues.TPC_HABILITA_MODULO_CREDENCIAMENTO_CSA, getResponsavel()) && !TextHelper.isNull(tpcPrzEnvioEmailExpCsa)) {
                ConsignatariaController consignatariaController = ApplicationContextProvider.getApplicationContext().getBean(ConsignatariaController.class);
                int maiorPrazo = Integer.MIN_VALUE;
                Date dataAtual = DateHelper.getSystemDatetime();
                String statusCredenciamento = StatusCredenciamentoEnum.AGUARDANDO_ENVIO_DOCUMENTACAO_CSA.getCodigo();
                for (String prazo : prazos) {
                    if (Integer.valueOf(prazo) > maiorPrazo) {
                        maiorPrazo = Integer.valueOf(prazo);
                    }
                }
                Date hojeMaisNDias = DateHelper.addDays(hoje, maiorPrazo);
                List<ConsignatariaTransferObject> csaList = csaDelegate.lstConsignatariasAExpirar(hojeMaisNDias, getResponsavel());
                for (ConsignatariaTransferObject csa : csaList) {
                    String csaCodigo = csa.getCsaCodigo();
                    consignatariaController.criarCredenciamentoConsignataria(csaCodigo, statusCredenciamento, dataAtual, null, getResponsavel());
                }
            }
        }

        if (ParamSist.paramEquals(CodedValues.TPC_ALERTA_EMAIL_EXPIRACAO_CONSIGNATARIAS, CodedValues.TPC_SIM, getResponsavel())) {
            LOG.debug("Executa Envio de Email de alerta de dias remanescentes para expiração das consignatárias (parâmetro 810)");
            ConsignatariaDelegate csaDelegate = new ConsignatariaDelegate();
            Date hoje = DateHelper.getSystemDate();

            String tpcPrzEnvioEmailExpCsa = (String) ParamSist.getInstance().getParam(CodedValues.TPC_PRAZO_ENVIA_EMAIL_EXPIRACAO_CSA, getResponsavel());
            String[] prazos = null;
            if (!TextHelper.isNull(tpcPrzEnvioEmailExpCsa)) {
                prazos = tpcPrzEnvioEmailExpCsa.replace(" ", "").split(",|;");
            }

            // Se não houver prazo cadastrado, utiliza o padrão
            if (prazos == null || prazos.length == 0) {
                prazos = new String[] { "10", "20", "30" };
            }

            // envia email de alerta para consignatárias a bloquear de acordo com os prazos cadastrados
            Map<String, List<ConsignatariaTransferObject>> prazosConsignatarias = new HashMap<>();
            for (String prazo : prazos) {

                if (!TextHelper.isNull(prazo)) {
                    try {
                        int prazoInt = Integer.parseInt(prazo.trim());
                        Date hojeMaisNDias = DateHelper.addDays(hoje, prazoInt);
                        List<ConsignatariaTransferObject> csaList = csaDelegate.lstConsignatariasAExpirar(hojeMaisNDias, getResponsavel());
                        if (!csaList.isEmpty()) {
                            prazosConsignatarias.put(prazo, csaList);
                        }

                    } catch (NumberFormatException ex) {
                        LOG.error("Não foi possível enviar e-mail de alerta para as consignatárias a bloquear para o prazo: " + prazo + ".", ex);
                    }
                }
            }

            enviarEmailAlertaBloqueioUnico(prazosConsignatarias);

        }
    }

    private void enviarEmailAlertaBloqueioUnico(Map<String, List<ConsignatariaTransferObject>> prazosConsignatarias) throws ConsignatariaControllerException, ViewHelperException {
        if (!prazosConsignatarias.isEmpty()) {
            EnviaEmailHelper.enviarEmailDiasBloqueioCsaParaCseOrg(prazosConsignatarias, getResponsavel());
        }
    }

    private void enviaEmailAlertaBloqueio(int diasParaBloqueio, Date hoje, ConsignatariaDelegate csaDelegate) throws ConsignatariaControllerException, ViewHelperException {
        Date hojeMaisNDias = DateHelper.addDays(hoje, diasParaBloqueio);
        List<ConsignatariaTransferObject> csaList = csaDelegate.lstConsignatariasAExpirar(hojeMaisNDias, getResponsavel());

        for (ConsignatariaTransferObject csa : csaList) {
            ConsignatariaTransferObject csaTO = csa;
            if (ParamSist.paramEquals(CodedValues.TPC_HABILITA_MODULO_CREDENCIAMENTO_CSA, CodedValues.TPC_SIM, getResponsavel())) {
                CredenciamentoCsa credenciamentoCsa = csaDelegate.findByCsaCodigoCredenciamentoCsa(csa.getCsaCodigo(), getResponsavel());
                if (credenciamentoCsa == null) {
                    EnviaEmailHelper.enviarEmailDiasBloqueioCsa(csaTO, diasParaBloqueio, getResponsavel());
                } else if (credenciamentoCsa.getScrCodigo().equals(StatusCredenciamentoEnum.AGUARDANDO_ENVIO_DOCUMENTACAO_CSA.getCodigo())) {
                    EnviaEmailHelper.enviarEmailDiasBloqueioCsa(csaTO, diasParaBloqueio, getResponsavel());
                }
            } else {
                EnviaEmailHelper.enviarEmailDiasBloqueioCsa(csaTO, diasParaBloqueio, getResponsavel());
            }
        }
    }
}
