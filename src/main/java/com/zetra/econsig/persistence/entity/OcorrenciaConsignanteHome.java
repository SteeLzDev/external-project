package com.zetra.econsig.persistence.entity;

import java.util.Date;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: OcorrenciaConsignanteHome</p>
 * <p>Description: Classe Home para a entidade OcorrenciaConsignante</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OcorrenciaConsignanteHome extends AbstractEntityHome {

    public static OcorrenciaConsignante findByPrimaryKey(Integer oceCodigo) throws FindException {
        final OcorrenciaConsignante ocorrencia = new OcorrenciaConsignante();
        ocorrencia.setOceCodigo(oceCodigo);
        return find(ocorrencia, oceCodigo);
    }
    
    public static OcorrenciaConsignante create(String tocCodigo, String msg, String usuCodigo, String ipAcesso) throws CreateException {
        final OcorrenciaConsignante ocorrencia = new OcorrenciaConsignante();
        final Session session = SessionUtil.getSession();

        try {
            ocorrencia.setOceData(new Date());
            ocorrencia.setOceIpAcesso(ipAcesso);
            ocorrencia.setConsignante(session.getReference(Consignante.class, CodedValues.CSE_CODIGO_SISTEMA));
            ocorrencia.setUsuario(session.getReference(Usuario.class, (TextHelper.isNull(usuCodigo) ? CodedValues.USU_CODIGO_SISTEMA : usuCodigo)));

            final TipoOcorrencia tipoOcorrencia = TipoOcorrenciaHome.findByPrimaryKey(tocCodigo);
            ocorrencia.setTipoOcorrencia(tipoOcorrencia);

            if (CodedValues.TOC_INICIALIZANDO_SISTEMA.equals(tocCodigo) || CodedValues.TOC_BACKUP_BASE_DADOS.equals(tocCodigo)) {
                ocorrencia.setOceObs(ApplicationResourcesHelper.getMessage("mensagem.informacao.ocorrencia.consignante.versao", (AcessoSistema) null, tipoOcorrencia.getTocDescricao()));
            } else if (!TextHelper.isNull(msg)) {
			    ocorrencia.setOceObs(ApplicationResourcesHelper.getMessage("mensagem.informacao.ocorrencia.consignante.motivo", (AcessoSistema) null, tipoOcorrencia.getTocDescricao(), msg));
			} else {
			    ocorrencia.setOceObs(tipoOcorrencia.getTocDescricao());
			}
        } catch (final FindException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return create(ocorrencia);
    }
}
