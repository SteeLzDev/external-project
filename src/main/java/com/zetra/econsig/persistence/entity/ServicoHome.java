package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: ServicoHome</p>
 * <p>Description: Classe Home para a entidade Servico</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ServicoHome extends AbstractEntityHome {

    public static Servico findByPrimaryKey(String svcCodigo) throws FindException {
        final Servico servico = new Servico();
        servico.setSvcCodigo(svcCodigo);
        return find(servico, svcCodigo);
    }

    public static Servico findByIdn(String svcIdentificador) throws FindException {
        final String query = "FROM Servico svc WHERE svc.svcIdentificador = :svcIdentificador";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("svcIdentificador", svcIdentificador);

        final List<Servico> result = findByQuery(query, parameters);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<Servico> findByTgsCodigo(String tgsCodigo) throws FindException {
        final String query = "FROM Servico svc WHERE svc.tipoGrupoSvc.tgsCodigo = :tgsCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("tgsCodigo", tgsCodigo);

        return findByQuery(query, parameters);
    }

    public static Servico findByAdeCodigo(String adeCodigo) throws FindException {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT svc FROM AutDesconto ade ");
        query.append("INNER JOIN ade.verbaConvenio vco ");
        query.append("INNER JOIN vco.convenio cnv ");
        query.append("INNER JOIN cnv.servico svc ");
        query.append("WHERE ade.adeCodigo = :adeCodigo ");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCodigo);

        final List<Servico> result = findByQuery(query.toString(), parameters);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<Servico> findByNseCodigo(String nseCodigo) throws FindException {
        final String query = "FROM Servico svc WHERE svc.naturezaServico.nseCodigo = :nseCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("nseCodigo", nseCodigo);

        return findByQuery(query, parameters);
    }

    public static Servico create(String svcIdentificador, String svcDescricao, Short svcAtivo, String tgsCodigo, String svcPrioridade, String svcObs, String nseCodigo) throws CreateException {

        final Session session = SessionUtil.getSession();
        final Servico bean = new Servico();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setSvcCodigo(objectId);
            bean.setSvcAtivo(svcAtivo);
            bean.setSvcIdentificador(svcIdentificador);
            bean.setSvcDescricao(svcDescricao);
            bean.setSvcObs(svcObs);
            if (!TextHelper.isNull(tgsCodigo)) {
                bean.setTipoGrupoSvc(session.getReference(TipoGrupoSvc.class, tgsCodigo));
            }
            bean.setSvcPrioridade(svcPrioridade);
            bean.setNaturezaServico(session.getReference(NaturezaServico.class, nseCodigo));
            create(bean, session);

        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static Servico findByAdeNumero(String adeNumero) throws FindException {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT svc FROM AutDesconto ade ");
        query.append("INNER JOIN ade.verbaConvenio vco ");
        query.append("INNER JOIN vco.convenio cnv ");
        query.append("INNER JOIN cnv.servico svc ");
        query.append("WHERE ade.adeNumero = :adeNumero ");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeNumero", adeNumero);

        final List<Servico> result = findByQuery(query.toString(), parameters);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }
}
