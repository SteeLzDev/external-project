package com.zetra.econsig.job.process.agendado;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.entidade.MensagemTO;
import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.service.mensagem.MensagemController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaAniversarioConvenio</p>
 * <p>Description: processo assíncrono que dispara criação de mensagens automáticas para consignantes para data de aniversário do convênio</p>
 * <p>Copyright: Copyright (c) 2014</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaAniversarioConvenio extends ProcessoAgendadoPeriodico {

    /** Log object for this class. */
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ProcessaAniversarioConvenio.class);

    private static final int QTDE_MAX_DIAS_APOS_DATA_ANIVERSARIO = 30;

    public ProcessaAniversarioConvenio(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        String dataImplantacao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_DATA_IMPLANTACAO_SISTEMA, getResponsavel());

        if (!TextHelper.isNull(dataImplantacao)) {

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date dataAtual = new Date();
            Date diaMesImplantacao = null;
            Date diaMesAtual = null;
            Date dataImplantacaoDate = null;
            boolean gravaMensagem = false;
            Calendar dataAniversarioAnoAtual = Calendar.getInstance();

            try {
                dataImplantacaoDate = dateFormat.parse(dataImplantacao);
                diaMesImplantacao = DateHelper.clearYearHourTime(dataImplantacaoDate);
                diaMesAtual = DateHelper.clearYearHourTime(dataAtual);
                dataAniversarioAnoAtual.setTime(dataImplantacaoDate);

                /**
                 * Se o mês já passou verifica se enviou mensagem do ano anterior. Isso serve para tratar erros de troca de ano
                 */
                if(diaMesAtual.compareTo(diaMesImplantacao) >= 0) {
                    dataAniversarioAnoAtual.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR));
                } else {
                    dataAniversarioAnoAtual.set(Calendar.YEAR, Calendar.getInstance().get(Calendar.YEAR)-1);
                }

            } catch (ParseException e) {
                throw new AgendamentoControllerException("mensagem.erro.data.invalida.americano", getResponsavel());
            }

            /*
             * Se a data de implantacao for pelo menos do ano anterior e
             * não tiver passado 30 dias que fez aniversário do convênio e
             * se não executou com sucesso
             */
            if(dataAniversarioAnoAtual.getTime().compareTo(dataAtual) == 0) {
                gravaMensagem = true;
            } else if (DateHelper.yearDiff(dataImplantacaoDate, dataAtual) >= 1 &&
                       DateHelper.dayDiff(dataAtual, dataAniversarioAnoAtual.getTime()) <= QTDE_MAX_DIAS_APOS_DATA_ANIVERSARIO &&
                       !jaExecutouComSucesso(dataAniversarioAnoAtual.getTime(), dataAtual)) {
                gravaMensagem = true;
            }

            if(gravaMensagem){
                LOG.debug("Criação de mensagem para data de aniversário do convênio.");

                MensagemTO mensagemTO = new MensagemTO();
                mensagemTO.setMenTitulo(ApplicationResourcesHelper.getMessage("rotulo.aniversario.convenio.titulo", getResponsavel()));
                mensagemTO.setMenTexto(ApplicationResourcesHelper.getMessage("mensagem.aniversario.convenio", getResponsavel()));
                mensagemTO.setMenData(new Date());
                mensagemTO.setMenExibeCse(CodedValues.TPC_SIM);
                mensagemTO.setMenExibeCor(CodedValues.TPC_NAO);
                mensagemTO.setMenExibeCsa(CodedValues.TPC_NAO);
                mensagemTO.setMenExibeOrg(CodedValues.TPC_NAO);
                mensagemTO.setMenExibeSer(CodedValues.TPC_NAO);
                mensagemTO.setMenExibeSup(CodedValues.TPC_NAO);
                mensagemTO.setMenExigeLeitura(CodedValues.TPC_SIM);
                mensagemTO.setMenHtml(CodedValues.TPC_SIM);
                mensagemTO.setUsuCodigo(getResponsavel().getUsuCodigo());

                MensagemController mensagemController = ApplicationContextProvider.getApplicationContext().getBean(MensagemController.class);
                mensagemController.createMensagem(mensagemTO, getResponsavel());
            }
        }
    }

    public boolean jaExecutouComSucesso(Date dataAnterior, Date dataAtual) throws ZetraException {
        AgendamentoController agendamentoController = ApplicationContextProvider.getApplicationContext().getBean(AgendamentoController.class);
        List<String> ocorrencias = agendamentoController.lstOcorrenciaSucessoPorIntervalo(getAgdCodigo(), dataAnterior, dataAtual, getResponsavel());
        return ocorrencias != null && !ocorrencias.isEmpty() ? true : false;
    }
}
