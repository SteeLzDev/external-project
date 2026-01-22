package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

/**
 * <p>Title: ModeloTermoTagHome</p>
 * <p>Description: Classe Home para a entidade ModeloTermoTag</p>
 * <p>Copyright: Copyright (c) 2025</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ModeloTermoTagHome extends AbstractEntityHome {

    public static ModeloTermoTag findByPrimaryKey(String mttCodigo) throws FindException {
        final ModeloTermoTag modeloTermoTag = new ModeloTermoTag();
        modeloTermoTag.setMttCodigo(mttCodigo);
        return find(modeloTermoTag, mttCodigo);
    }

    public static MotivoDependencia create() throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

    public static List<ModeloTermoTag> findByMtaCodigo(String mtaCodigo) throws FindException {
        final String query = "FROM ModeloTermoTag mtt WHERE mtt.mtaCodigo = :mtaCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("mtaCodigo", mtaCodigo);

        return findByQuery(query, parameters);
    }
}
