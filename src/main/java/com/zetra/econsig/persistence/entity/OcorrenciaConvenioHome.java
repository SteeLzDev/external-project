package com.zetra.econsig.persistence.entity;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: OcorrenciaConvenioHome</p>
 * <p>Description: Classe Home da entidade OcorrenciaConvenio.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaConvenioHome extends AbstractEntityHome {

    public static OcorrenciaConvenio findByPrimaryKey(String ocoCodigo) throws FindException {
        OcorrenciaConvenio ocorrenciaConvenio = new OcorrenciaConvenio();
        ocorrenciaConvenio.setOcoCodigo(ocoCodigo);

        return find(ocorrenciaConvenio, ocoCodigo);
    }

    public static OcorrenciaConvenio create(String cnvCodigo, String tocCodigo, String tmoCodigo, String ocoObs, AcessoSistema responsavel) throws CreateException {
        Session session = SessionUtil.getSession();
        OcorrenciaConvenio ocorrenciaConvenio = new OcorrenciaConvenio();

        try {
            ocorrenciaConvenio.setOcoCodigo(DBHelper.getNextId());
            ocorrenciaConvenio.setTipoOcorrencia((TipoOcorrencia) session.getReference(TipoOcorrencia.class, tocCodigo));
            ocorrenciaConvenio.setUsuario((Usuario) session.getReference(Usuario.class, responsavel.getUsuCodigo()));
            ocorrenciaConvenio.setConvenio((Convenio) session.getReference(Convenio.class, cnvCodigo));
            if (!TextHelper.isNull(tmoCodigo)) {
                ocorrenciaConvenio.setTipoMotivoOperacao((TipoMotivoOperacao) session.getReference(TipoMotivoOperacao.class, tmoCodigo));
            }
            ocorrenciaConvenio.setOcoData(DateHelper.getSystemDatetime());
            ocorrenciaConvenio.setOcoObs(ocoObs);
            ocorrenciaConvenio.setOcoIpAcesso(responsavel.getIpUsuario());

            create(ocorrenciaConvenio);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return ocorrenciaConvenio;
    }
}
