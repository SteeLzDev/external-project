package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: OcorrenciaOrgaoHome</p>
 * <p>Description: Classe Home da entidade OcorrenciaOrgao.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author: rodrigo.rosa $
 * $Revision: 24514 $
 * $Date: 2020-06-04 13:56:54 -0300 (seg, 04 jun 2018) $
 */
public class OcorrenciaOrgaoHome extends AbstractEntityHome {

    public static OcorrenciaOrgao create(String orgCodigo, String tocCodigo, String usuCodigo, String ipAcesso) throws CreateException {
        OcorrenciaOrgao ocorrencia = new OcorrenciaOrgao();
        Session session = SessionUtil.getSession();

        try {
            ocorrencia.setOorCodigo(DBHelper.getNextId());
            ocorrencia.setOorData(new Date());
            ocorrencia.setOorIpAcesso(ipAcesso);
            ocorrencia.setOrgao(session.getReference(Orgao.class, orgCodigo));
            ocorrencia.setUsuario(session.getReference(Usuario.class, (TextHelper.isNull(usuCodigo) ? CodedValues.USU_CODIGO_SISTEMA : usuCodigo)));

            TipoOcorrencia tipoOcorrencia = TipoOcorrenciaHome.findByPrimaryKey(tocCodigo);
            ocorrencia.setTipoOcorrencia(tipoOcorrencia);
            ocorrencia.setOorObs(tipoOcorrencia.getTocDescricao());
        } catch (MissingPrimaryKeyException | FindException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return create(ocorrencia);
    }

}
