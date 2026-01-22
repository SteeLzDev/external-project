package com.zetra.econsig.job.process.agendado;

import java.util.ArrayList;
import java.util.List;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.job.process.ProcessoAgendadoPeriodico;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.StatusAgendamentoEnum;
import com.zetra.econsig.values.TipoAgendamentoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessaGeraArquivoMargemServicoExterno</p>
 * <p>Description: Classe de processamento excluir ocorrências no histórico de agendamento de acordo com o parâmetro de sistema.</p>
 * <p>Copyright: Copyright (c) 2023</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ProcessaExclusaoHistoricoOcorrenciaAgendamento extends ProcessoAgendadoPeriodico {

    public ProcessaExclusaoHistoricoOcorrenciaAgendamento(String agdCodigo, AcessoSistema responsavel) {
        super(agdCodigo, responsavel);
    }

    @Override
    protected void executa() throws ZetraException {
        final String quantidadeDiasExclusao = (String) ParamSist.getInstance().getParam(CodedValues.TPC_QUANTIDADE_DIAS_MANTER_HIST_OCORRENCIA_AGENDAMENTO, AcessoSistema.getAcessoUsuarioSistema());
        if(!TextHelper.isNull(quantidadeDiasExclusao) && (Integer.valueOf(quantidadeDiasExclusao) > 0)) {
            final AgendamentoController agendamentoController = ApplicationContextProvider.getApplicationContext().getBean(AgendamentoController.class);

            final List<String> sagCodigos = new ArrayList<>();
            sagCodigos.add(StatusAgendamentoEnum.AGUARDANDO_EXECUCAO.getCodigo());
            sagCodigos.add(StatusAgendamentoEnum.EXECUCAO_DIARIA.getCodigo());

            final List<String> tagCodigos = new ArrayList<>();
            tagCodigos.add(TipoAgendamentoEnum.PERIODICO_DIARIO.getCodigo());
            tagCodigos.add(TipoAgendamentoEnum.PERIODICO_5_MIN.getCodigo());


            agendamentoController.excluiHistoricoOcorrenciaAgendamentoExpiradaBySagCodigoByTagCodigo(sagCodigos, tagCodigos, CodedValues.TOC_PROCESSAMENTO_AGENDAMENTO, Integer.parseInt(quantidadeDiasExclusao), getResponsavel());
        }
    }
}