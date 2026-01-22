package com.zetra.econsig.helper.validacaoambiente;

import java.util.Map;

import com.zetra.econsig.exception.ValidacaoAmbienteControllerException;

/**
 * <p>Title: RegraValidacaoAmbienteInterface</p>
 * <p>Description: Interface para padronizar as regras de validação do ambiente do eConsig.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public interface RegraValidacaoAmbienteInterface {

    /**
     * Método que executa a validação.
     * @return Map com o valor da regra no sistema e tem como chave o resultado da validação.
     * @throws ValidacaoAmbienteControllerException Exceção padrão da validação.
     */
    public Map<Boolean, String> executar() throws ValidacaoAmbienteControllerException;

}
