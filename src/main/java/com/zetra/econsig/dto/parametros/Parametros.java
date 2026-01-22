package com.zetra.econsig.dto.parametros;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import com.zetra.econsig.exception.ParametrosException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: Parametros</p>
 * <p>Description: Classe abstrata para os parâmetros do sistema.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class Parametros implements Serializable {
    protected Map<String, Object> requiredFieldsMap;

    public void checkNotNullSafe() throws ParametrosException {
        Set<String> paramNames = requiredFieldsMap.keySet();

        for (String fieldName : paramNames) {
            if (requiredFieldsMap.get(fieldName) == null) {
                throw new ParametrosException("mensagem.erro.campo.nulo", (AcessoSistema) null, fieldName);
            }
        }
    }
}
