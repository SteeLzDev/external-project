package com.zetra.econsig.persistence.entity;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.comunicacao.GeradorCmnNumero;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ComunicacaoHome</p>
 * <p>Description: Home da Bean Class Comunicacao.</p>
 * <p>Copyright: Copyright (c) 2010</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ComunicacaoHome extends AbstractEntityHome {

    public static Comunicacao findByPrimaryKey(String cmnCodigo) throws FindException {
        final Comunicacao comunicacao = new Comunicacao();
        comunicacao.setCmnCodigo(cmnCodigo);
        return find(comunicacao, cmnCodigo);
    }

    public static Comunicacao create(String usuCodigo, Long cmnNumero, Boolean cmnPendencia, String cmnTexto, String cmnCodigoPai, String cmnIpAcesso, Boolean cmnAlertaEmail, Boolean cmnCopiaEmailSms, String cmnAscCodigo, String adeCodigo) throws CreateException {
        final Session session = SessionUtil.getSession();
        final Comunicacao bean = new Comunicacao();

        try {
            bean.setCmnData(DateHelper.getSystemDatetime());
            bean.setCmnPendencia(cmnPendencia);
            bean.setCmnTexto(cmnTexto);
            bean.setCmnIpAcesso(cmnIpAcesso);
            bean.setCmnAlertaEmail(cmnAlertaEmail);
            bean.setCmnCopiaEmailSms(cmnCopiaEmailSms);

            if (cmnAscCodigo != null) {
                bean.setAssuntoComunicacao(session.getReference(AssuntoComunicacao.class, cmnAscCodigo));
            }

            if (cmnNumero != null) {
                bean.setCmnNumero(cmnNumero);
            } else {
                bean.setCmnNumero(GeradorCmnNumero.getInstance().getNext());
            }

            bean.setCmnCodigo(DBHelper.getNextId());
            bean.setUsuario(session.getReference(Usuario.class, usuCodigo));
            if (!TextHelper.isNull(cmnCodigoPai)) {
                bean.setComunicacaoPai(session.getReference(Comunicacao.class, cmnCodigoPai));
            }

            if (!TextHelper.isNull(adeCodigo)) {
                bean.setAutDesconto(session.getReference(AutDesconto.class, adeCodigo));
            }

            create(bean, session);
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static Collection<Comunicacao> findByCsaCodigo(String csaCodigo) throws FindException {
        final String query = "FROM Comunicacao cmn INNER JOIN cmn.comunicacaoCsaSet cmnCsa WHERE cmnCsa.id.csaCodigo = :csaCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);
    }

    public static Collection<Comunicacao> findByUsuCodigo(String usuCodigo) throws FindException {
        final String query = "FROM Comunicacao cmn WHERE cmn.usuario.usuCodigo = :usuCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }

    public static Collection<Comunicacao> findByCmnCodigoPai(String cmnCodigoPai, boolean pendentes) throws FindException {
        String query = null;

        if (pendentes) {
            query = "FROM Comunicacao cmn WHERE cmn.comunicacaoPai.cmnCodigo = :cmnCodigo and cmn.cmnPendencia = :cmnPendencia";
        } else {
            query = "FROM Comunicacao cmn WHERE cmn.comunicacaoPai.cmnCodigo = :cmnCodigo";
        }

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("cmnCodigo", cmnCodigoPai);
        if (pendentes) {
            parameters.put("cmnPendencia", Boolean.TRUE);
        }

        return findByQuery(query, parameters);
    }

    public static Collection<Comunicacao> findComunicacaoPendenteSerRemetenteCsaDestinatario(String cmnCodigoPai) throws FindException {
        final String query = "FROM Comunicacao cmn " +
                       "INNER JOIN cmn.comunicacaoCsaSet cmnCsa " +
                       "INNER JOIN cmn.comunicacaoSerSet cmnSer " +
        		       "WHERE cmn.comunicacaoPai.cmnCodigo = :cmnCodigoPai " +
                       "AND cmn.cmnPendencia = :cmnPendencia " +
                       "AND cmnCsa.cmcDestinatario = :destinatario " +
                       "AND cmnSer.cmsDestinatario = :remetente";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("cmnCodigoPai", cmnCodigoPai);
        parameters.put("cmnPendencia", Boolean.TRUE);
        parameters.put("destinatario", CodedValues.TPC_SIM);
        parameters.put("remetente", CodedValues.TPC_NAO);

        return findByQuery(query, parameters);
    }

    public static void removeBySerRse(String serCodigo, String rseCodigo) throws FindException, RemoveException {
        final String query = "FROM ComunicacaoSer cms WHERE cms.servidor.serCodigo = :serCodigo and cms.registroServidor.rseCodigo = :rseCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("serCodigo", serCodigo);
        parameters.put("rseCodigo", rseCodigo);

        final List<ComunicacaoSer> resultado = findByQuery(query, parameters);

        if ((resultado != null) && !resultado.isEmpty()) {
            for (final ComunicacaoSer cms : resultado) {
                // Remove ligação comunicação servidor
                remove(cms);
                // Remove ligação comunicação
                remove(findByPrimaryKey(cms.getComunicacao().getCmnCodigo()));
            }
        }
    }

}
