package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: PostoRegistroServidorHome</p>
 * <p>Description: Classe Home para a entidade PostoRegistroServidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PostoRegistroServidorHome extends AbstractEntityHome {

    public static PostoRegistroServidor findByPrimaryKey(String posCodigo) throws FindException {
        PostoRegistroServidor postoRegistroServidor = new PostoRegistroServidor();
        postoRegistroServidor.setPosCodigo(posCodigo);
        return find(postoRegistroServidor, posCodigo);
    }

    public static PostoRegistroServidor create(String posIdentificador, String posDescricao) throws CreateException {
        try {
            PostoRegistroServidor bean = new PostoRegistroServidor();

            bean.setPosCodigo(DBHelper.getNextId());
            bean.setPosIdentificador(posIdentificador);
            bean.setPosDescricao(posDescricao);
            bean.setPosPercTxUso(BigDecimal.ZERO);
            bean.setPosPercTxUsoCond(BigDecimal.ZERO);
            bean.setPosVlrSoldo(BigDecimal.ZERO);
            create(bean);
            return bean;
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
    }

    public static PostoRegistroServidor findByRseCodigo(String rseCodigo) throws FindException {
        String query = "FROM PostoRegistroServidor pos WHERE EXISTS (SELECT 1 FROM RegistroServidor rse WHERE rse.postoRegistroServidor.posCodigo = pos.posCodigo AND rse.rseCodigo = :rseCodigo)";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        List<PostoRegistroServidor> result = findByQuery(query, parameters);
        if (!result.isEmpty()) {
            return result.get(0);
        }
        return null;
    }
}
