package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: DestinatarioEmailHome</p>
 * <p>Description: Classe Home para a entidade DestinatarioEmail</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class DestinatarioEmailHome extends AbstractEntityHome {

    public static List<DestinatarioEmail> listByFunCodigoPapOperador(String funCodigo, String papOperador) throws FindException {
        String query = "FROM DestinatarioEmail des WHERE des.id.papCodigoOperador = :papCodigoOperador and des.id.funCodigo = :funCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("papCodigoOperador", papOperador);
        parameters.put("funCodigo", funCodigo);

        return findByQuery(query, parameters);

    }

    public static boolean verificaSeOperadorPodeEnviarEmail(String funCodigo, String papOperador, String papDestinatario) throws FindException {
        String query = "FROM DestinatarioEmail des WHERE des.id.papCodigoOperador = :papCodigoOperador and des.id.funCodigo = :funCodigo and des.papCodigoDestinatario = :papCodigoDestinatario";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("papCodigoOperador", papOperador);
        parameters.put("funCodigo", funCodigo);
        parameters.put("papCodigoDestinatario", papDestinatario);

        List<DestinatarioEmail> list = findByQuery(query, parameters);
        return !list.isEmpty();
    }
}
