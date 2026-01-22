package com.zetra.econsig.job.process;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.AgendamentoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.service.agendamento.AgendamentoController;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: ProcessoAgendado</p>
 * <p>Description: Processo que pode ser agendada a sua execução.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class ProcessoAgendado extends Processo {

    private final String agdCodigo;
    private final AcessoSistema responsavel;

    public ProcessoAgendado(String agdCodigo, AcessoSistema responsavel) {
        this.agdCodigo = agdCodigo;
        this.responsavel = responsavel;
    }

    public String getAgdCodigo() {
        return agdCodigo;
    }

    public AcessoSistema getResponsavel() {
        return responsavel;
    }

    protected Map<String, String[]> getParametrosAgendamento(String agdCodigo) throws AgendamentoControllerException {
        AgendamentoController agendamentoController = ApplicationContextProvider.getApplicationContext().getBean(AgendamentoController.class);
        Map<String, List<String>> retorno = agendamentoController.lstParametrosAgendamento(agdCodigo, responsavel);
        Map<String, String[]> parameterMap = new HashMap<>();

        Iterator<Map.Entry<String, List<String>>> iteKey = retorno.entrySet().iterator();
        while (iteKey.hasNext()) {
            Map.Entry<String, List<String>> entry = iteKey.next();
            String chave = entry.getKey();
            List<String> valores = entry.getValue();
            String[] strValores = valores.toArray(new String[0]);
            parameterMap.put(chave, strValores);
        }

        return parameterMap;
    }
}
