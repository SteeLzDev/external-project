package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;

/**
 * <p>Title: BloqueioUsuFunSvcHome</p>
 * <p>Description: Classe Home para a entidade BloqueioUsuFunSvc</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class BloqueioUsuFunSvcHome extends AbstractEntityHome {

    public static BloqueioUsuFunSvc findByPrimaryKey(BloqueioUsuFunSvcId pk) throws FindException {
        BloqueioUsuFunSvc blkUsuFunSvc = new BloqueioUsuFunSvc();
        blkUsuFunSvc.setId(pk);
        return find(blkUsuFunSvc, pk);
    }

    public static List<BloqueioUsuFunSvc> findByUsuCodigo(String usuCodigo) throws FindException {
        String query = "FROM BloqueioUsuFunSvc blk WHERE blk.usuario.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static BloqueioUsuFunSvc create(String funCodigo, String usuCodigo, String svcCodigo) throws CreateException {
        BloqueioUsuFunSvc bean = new BloqueioUsuFunSvc();

        BloqueioUsuFunSvcId id = new BloqueioUsuFunSvcId(funCodigo, usuCodigo, svcCodigo);
        bean.setId(id);
        create(bean);
        return bean;
    }

}
