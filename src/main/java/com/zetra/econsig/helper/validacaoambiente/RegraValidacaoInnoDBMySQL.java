package com.zetra.econsig.helper.validacaoambiente;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.ValidacaoAmbienteControllerException;
import com.zetra.econsig.service.ambiente.ValidacaoAmbienteController;
import com.zetra.econsig.values.RegraValidacaoEnum;
import com.zetra.econsig.web.ApplicationContextProvider;

/**
 * <p>Title: RegraValidacaoInnoDBMySQL</p>
 * <p>Description: Regra que verifica se o InnoDB do MySQL está habilitado.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RegraValidacaoInnoDBMySQL implements RegraValidacaoAmbienteInterface {

    private static final String VALOR_VARIAVEL_INNODB = "YES";

    /**
     * Método que executa a validação do InnoDB do MySQL se está habilitado.
     * @return Map com o valor da regra no sistema e tem como chave o resultado da validação.
     * @throws ValidacaoAmbienteControllerException Exceção padrão da validação
     */
    @Override
    public Map<Boolean, String> executar() throws ValidacaoAmbienteControllerException {
        Map<Boolean, String> resultado = new HashMap<>();
        ValidacaoAmbienteController validacaoAmbienteController = ApplicationContextProvider.getApplicationContext().getBean(ValidacaoAmbienteController.class);
        Iterator<TransferObject> itInnoDB = validacaoAmbienteController.obterValorRegraValidacaoAmbiente(RegraValidacaoEnum.VALIDAR_INNODB_MYSQL).iterator();
        while (itInnoDB.hasNext()) {
            TransferObject cto = itInnoDB.next();
            String innoDBHabilitado = (String) cto.getAttribute("value");
            resultado.put(Boolean.valueOf(VALOR_VARIAVEL_INNODB.equalsIgnoreCase(innoDBHabilitado)), innoDBHabilitado);
        }
        return resultado;
    }
}
