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
 * <p>Title: OcorrenciaServicoHome</p>
 * <p>Description: Classe Home da entidade OcorrenciaServico.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaServicoHome extends AbstractEntityHome {

    public static OcorrenciaServico findByPrimaryKey(String oseCodigo) throws FindException {
        OcorrenciaServico ocorrenciaServico = new OcorrenciaServico();
        ocorrenciaServico.setOseCodigo(oseCodigo);

        return find(ocorrenciaServico, oseCodigo);
    }

    public static OcorrenciaServico create(String svcCodigo, String tocCodigo, String tmoCodigo, String oseObs, AcessoSistema responsavel) throws CreateException {
        Session session = SessionUtil.getSession();
        OcorrenciaServico ocorrenciaServico = new OcorrenciaServico();

        try {
            ocorrenciaServico.setOseCodigo(DBHelper.getNextId());
            ocorrenciaServico.setTipoOcorrencia((TipoOcorrencia) session.getReference(TipoOcorrencia.class, tocCodigo));
            ocorrenciaServico.setUsuario((Usuario) session.getReference(Usuario.class, responsavel.getUsuCodigo()));
            ocorrenciaServico.setServico((Servico) session.getReference(Servico.class, svcCodigo));
            if (!TextHelper.isNull(tmoCodigo)) {
                ocorrenciaServico.setTipoMotivoOperacao((TipoMotivoOperacao) session.getReference(TipoMotivoOperacao.class, tmoCodigo));
            }
            ocorrenciaServico.setOseData(DateHelper.getSystemDatetime());
            ocorrenciaServico.setOseObs(oseObs);
            ocorrenciaServico.setOseIpAcesso(responsavel.getIpUsuario());

            create(ocorrenciaServico);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return ocorrenciaServico;
    }
}
