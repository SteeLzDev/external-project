package com.zetra.econsig.persistence.entity;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * <p>Title: OcorrenciaContratoBeneficioHome</p>
 * <p>Description: Classe Home da entidade OcorrenciaCttBeneficio.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class OcorrenciaContratoBeneficioHome extends AbstractEntityHome {

    public static OcorrenciaCttBeneficio findByPrimaryKey(String ocbCodigo) throws FindException {
        OcorrenciaCttBeneficio ocorrenciaCttBeneficio = new OcorrenciaCttBeneficio();
        ocorrenciaCttBeneficio.setOcbCodigo(ocbCodigo);
        return find(ocorrenciaCttBeneficio, ocbCodigo);
    }

    public static OcorrenciaCttBeneficio findByCbeTocTmo(String cbeCodigo, String tocCodigo, String tmoCodigo) throws FindException {
        String query = "SELECT ocb FROM OcorrenciaCttBeneficio ocb "
                     + " WHERE ocb.contratoBeneficio.cbeCodigo = :cbeCodigo ";
        if (tocCodigo != null) {
            query = query + " AND ocb.tipoOcorrencia.tocCodigo = :tocCodigo";
        }
        if (tmoCodigo != null) {
            query = query + " AND ocb.tipoMotivoOperacao.tmoCodigo = :tmoCodigo";
        }
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("cbeCodigo", cbeCodigo);
        if (tocCodigo != null) {
            parameters.put("tocCodigo", tocCodigo);
        }
        if (tmoCodigo != null) {
            parameters.put("tmoCodigo", tmoCodigo);
        }

        List<OcorrenciaCttBeneficio> OcorrenciaCttBeneficio = findByQuery(query, parameters);

        if (OcorrenciaCttBeneficio == null || OcorrenciaCttBeneficio.size() == 0) {
            return null;
        } else {
            return OcorrenciaCttBeneficio.get(0);
        }
    }

    public static OcorrenciaCttBeneficio create(TipoOcorrencia tipoOcorrencia, Usuario usuario,
            ContratoBeneficio contratoBeneficio, TipoMotivoOperacao tipoMotivoOperacao, Date data,
            String observacao, String ipAcesso) throws CreateException {
        OcorrenciaCttBeneficio OcorrenciaCttBeneficio = new OcorrenciaCttBeneficio();

        try {
            OcorrenciaCttBeneficio.setOcbCodigo(DBHelper.getNextId());
            OcorrenciaCttBeneficio.setTipoOcorrencia(tipoOcorrencia);
            OcorrenciaCttBeneficio.setUsuario(usuario);
            OcorrenciaCttBeneficio.setContratoBeneficio(contratoBeneficio);
            OcorrenciaCttBeneficio.setTipoMotivoOperacao(tipoMotivoOperacao);

            if (data == null) {
                data = Calendar.getInstance().getTime();
            }
            OcorrenciaCttBeneficio.setOcbData(data);

            OcorrenciaCttBeneficio.setOcbObs(observacao);
            OcorrenciaCttBeneficio.setOcbIpAcesso(ipAcesso);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }

        create(OcorrenciaCttBeneficio);

        return OcorrenciaCttBeneficio;
    }

    public static OcorrenciaCttBeneficio create(String tocCodigo, String cbeCodigo, String tmoCodigo, String ocbObs, AcessoSistema responsavel) throws CreateException {
        Session session = SessionUtil.getSession();
        OcorrenciaCttBeneficio OcorrenciaCttBeneficio = new OcorrenciaCttBeneficio();

        try {
            OcorrenciaCttBeneficio.setOcbCodigo(DBHelper.getNextId());
            OcorrenciaCttBeneficio.setTipoOcorrencia((TipoOcorrencia) session.getReference(TipoOcorrencia.class, tocCodigo));
            OcorrenciaCttBeneficio.setUsuario((Usuario) session.getReference(Usuario.class, responsavel.getUsuCodigo()));
            OcorrenciaCttBeneficio.setContratoBeneficio((ContratoBeneficio) session.getReference(ContratoBeneficio.class, cbeCodigo));
            if (!TextHelper.isNull(tmoCodigo)) {
                OcorrenciaCttBeneficio.setTipoMotivoOperacao((TipoMotivoOperacao) session.getReference(TipoMotivoOperacao.class, tmoCodigo));
            }
            OcorrenciaCttBeneficio.setOcbData(DateHelper.getSystemDatetime());
            OcorrenciaCttBeneficio.setOcbObs(ocbObs);
            OcorrenciaCttBeneficio.setOcbIpAcesso(responsavel.getIpUsuario());

            create(OcorrenciaCttBeneficio);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return OcorrenciaCttBeneficio;
    }
}
