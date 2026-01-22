package com.zetra.econsig.helper.validacaoambiente;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ValidacaoAmbienteControllerException;
import com.zetra.econsig.service.ambiente.ValidacaoAmbienteController;
import com.zetra.econsig.values.RegraValidacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: RegraValidacaoTriggerStatusAutDesconto</p>
 * <p>Description: Regra que verifica se existe trigger para alteração de status de aut_desconto.</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraValidacaoTriggerStatusAutDesconto implements RegraValidacaoAmbienteInterface {
    public static final String TRIGGER_HISTORICO_STATUS_ADE = "trg_atualiza_ade_data_status";

    /**
     * Método que executa a validação do nível de transação do MySQL se está em Read-Commited.
     * @return Map com o valor da regra no sistema e tem como chave o resultado da validação.
     * @throws ValidacaoAmbienteControllerException Exceção padrão da validação
     */
    @Override
    public Map<Boolean, String> executar() throws ValidacaoAmbienteControllerException {
        ValidacaoAmbienteController validacaoAmbienteController = ApplicationContextProvider.getApplicationContext().getBean(ValidacaoAmbienteController.class);
        List<TransferObject> trigger = validacaoAmbienteController.obterValorRegraValidacaoAmbiente(RegraValidacaoEnum.VALIDAR_TRIGGER_STATUS_ADE);
        boolean triggerExists = (trigger != null && !trigger.isEmpty());

        Map<Boolean, String> resultado = new HashMap<>();
        resultado.put(Boolean.valueOf(triggerExists), "");
        return resultado;
    }
}
