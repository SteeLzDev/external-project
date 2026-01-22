package com.zetra.econsig.persistence.entity;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.OperacaoValidacaoTotpEnum;

/**
 * <p>Title: UsuarioHome</p>
 * <p>Description: Classe Home para a entidade Usuario</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class UsuarioHome extends AbstractEntityHome {

    public static Usuario findByLogin(String usuLogin) throws FindException {
        String query = "FROM Usuario usu WHERE usu.usuLogin = :usuLogin";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuLogin", usuLogin);

        List<Usuario> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<Usuario> findByEmail(String usuEmail) throws FindException {
        String query = "FROM Usuario usu WHERE usu.usuEmail = :usuEmail ORDER BY usu.stuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuEmail", usuEmail);

        return findByQuery(query, parameters);
    }

    public static List<Usuario> findByEmailAndToken(String usuEmail, String usuToken) throws FindException {
        String query = "FROM Usuario usu WHERE usu.usuEmail = :usuEmail and usu.usuOtpCodigo = :usuToken";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuEmail", usuEmail);
        parameters.put("usuToken", usuToken);

        return findByQuery(query, parameters);
    }

    public static Usuario findUsuCse(String usuCodigo) throws FindException {
        String query = "FROM Usuario usu WHERE EXISTS (SELECT 1 FROM UsuarioCse usuCse WHERE usu.usuCodigo = usuCse.usuario.usuCodigo) and usu.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        List<Usuario> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Usuario findUsuCsa(String usuCodigo) throws FindException {
        String query = "FROM Usuario usu WHERE EXISTS (SELECT 1 FROM UsuarioCsa usuCsa WHERE usu.usuCodigo = usuCsa.usuario.usuCodigo) and usu.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        List<Usuario> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Usuario findUsuCor(String usuCodigo) throws FindException {
        String query = "FROM Usuario usu WHERE EXISTS (SELECT 1 FROM UsuarioCor usuCor WHERE usu.usuCodigo = usuCor.usuario.usuCodigo) and usu.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        List<Usuario> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Usuario findUsuOrg(String usuCodigo) throws FindException {
        String query = "FROM Usuario usu WHERE EXISTS (SELECT 1 FROM UsuarioOrg usuOrg WHERE usu.usuCodigo = usuOrg.usuario.usuCodigo) and usu.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        List<Usuario> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Usuario findUsuSer(String usuCodigo) throws FindException {
        String query = "FROM Usuario usu WHERE EXISTS (SELECT 1 FROM UsuarioSer usuSer WHERE usu.usuCodigo = usuSer.usuario.usuCodigo) and usu.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        List<Usuario> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Usuario findUsuSup(String usuCodigo) throws FindException {
        String query = "FROM Usuario usu WHERE EXISTS (SELECT 1 FROM UsuarioSup usuSup WHERE usu.usuCodigo = usuSup.usuario.usuCodigo) and usu.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        List<Usuario> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static void updateUsuNovaSenha(String usuNovaSenha, String usuCodigo) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            String hql = "UPDATE Usuario set usuNovaSenha = :usuNovaSenha WHERE usuCodigo = :usuCodigo";
            MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("usuNovaSenha", usuNovaSenha);
            queryUpdate.setParameter("usuCodigo", usuCodigo);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void updateUsuDataUltimoAcesso(java.util.Date usuDataUltAcesso, String usuCodigo) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            String hql = "UPDATE Usuario set usuDataUltAcesso = :usuDataUltAcesso WHERE usuCodigo = :usuCodigo";
            MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("usuDataUltAcesso", usuDataUltAcesso);
            queryUpdate.setParameter("usuCodigo", usuCodigo);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static void ativaUsuNovaSenha(java.util.Date usuDataExpSenha, String usuCodigo) throws UpdateException {
        Session session = SessionUtil.getSession();
        try {
            String hql = "UPDATE Usuario set usuSenha = usuNovaSenha, usuDataExpSenha = :usuDataExpSenha, usuNovaSenha = null WHERE usuCodigo = :usuCodigo";
            MutationQuery queryUpdate = session.createMutationQuery(hql);
            queryUpdate.setParameter("usuDataExpSenha", usuDataExpSenha);
            queryUpdate.setParameter("usuCodigo", usuCodigo);
            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new UpdateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }

    public static Usuario findByPrimaryKey(String usuCodigo) throws FindException {
        Usuario usuario = new Usuario();
        usuario.setUsuCodigo(usuCodigo);
        return find(usuario, usuCodigo);
    }

    public static Usuario create(String stuCodigo, String usuLogin, String usuSenha, String usuSenha2, String usuNome, String usuEmail, String usuTel, String usuDicaSenha, String usuTipoBloq, Date usuDataExpSenha,
            Date usuDataExpSenha2, String usuIpAcesso, String usuDDNSAcesso, String usuCPF, String usuCentralizador, String usuVisivel, String usuExigeCertificado, String usuMatriculaInst, String usuChaveRecuperarSenha,
            Date usuDataFimVig, String usuDeficienteVisual, String usuChaveValidacaoTotp, String usuPermiteValidacaoTotp, Integer usuQtdConsultasMargem, String usuAutenticaSso) throws CreateException {

        Session session = SessionUtil.getSession();
        Usuario bean = new Usuario();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setUsuCodigo(objectId);
            bean.setUsuDataCad(DateHelper.getSystemDatetime());
            bean.setStatusLogin(session.getReference(StatusLogin.class, stuCodigo));
            bean.setUsuLogin(usuLogin);
            bean.setUsuSenha(usuSenha);
            bean.setUsuSenha2(usuSenha2);
            bean.setUsuNome(usuNome);
            bean.setUsuEmail(usuEmail);
            bean.setUsuTel(usuTel);
            bean.setUsuDicaSenha(usuDicaSenha);
            bean.setUsuTipoBloq(usuTipoBloq);
            bean.setUsuDataExpSenha(usuDataExpSenha);
            bean.setUsuDataExpSenha2(usuDataExpSenha2);
            bean.setUsuIpAcesso(usuIpAcesso);
            bean.setUsuDdnsAcesso(usuDDNSAcesso);
            bean.setUsuCpf(usuCPF);
            bean.setUsuCentralizador(usuCentralizador);
            bean.setUsuVisivel(usuVisivel);
            bean.setUsuExigeCertificado(usuExigeCertificado);
            bean.setUsuDataUltAcesso(DateHelper.getSystemDatetime());
            bean.setUsuMatriculaInst(usuMatriculaInst);
            bean.setUsuChaveRecuperarSenha(usuChaveRecuperarSenha);
            bean.setUsuDataFimVig(usuDataFimVig);
            bean.setUsuDeficienteVisual(usuDeficienteVisual);
            bean.setUsuChaveValidacaoTotp(usuChaveValidacaoTotp);
            bean.setUsuPermiteValidacaoTotp(usuPermiteValidacaoTotp);
            bean.setUsuQtdConsultasMargem(usuQtdConsultasMargem);
            bean.setUsuAutorizaEmailMarketing(CodedValues.TPC_SIM);
            bean.setUsuOperacoesValidacaoTotp(OperacaoValidacaoTotpEnum.AUTORIZACAO_OPERACAO_SENSIVEL.getCodigo());
            if (!TextHelper.isNull(usuAutenticaSso)) {
                bean.setUsuAutenticaSso(usuAutenticaSso);
            }
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static Usuario createUsuarioSenhaExpirada(String usuLogin, String usuSenha, String usuNome, String usuEmail, String usuTel, String usuCPF) throws CreateException {
        Session session = SessionUtil.getSession();
        Usuario bean = new Usuario();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setUsuCodigo(objectId);
            bean.setUsuDataCad(DateHelper.getSystemDatetime());
            bean.setUsuDataExpSenha(DateHelper.getSystemDatetime());
            bean.setUsuLogin(usuLogin);
            bean.setUsuNome(usuNome);
            bean.setUsuEmail(usuEmail);
            bean.setUsuTel(usuTel);
            bean.setUsuCpf(usuCPF);
            bean.setStatusLogin(session.getReference(StatusLogin.class, CodedValues.STU_ATIVO));
            bean.setUsuSenha(usuSenha);
            bean.setUsuAutorizaEmailMarketing(CodedValues.TPC_SIM);
            bean.setUsuOperacoesValidacaoTotp(OperacaoValidacaoTotpEnum.AUTORIZACAO_OPERACAO_SENSIVEL.getCodigo());

            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static Usuario findByServidor(String rseCodigo) throws FindException, RemoveException {

        StringBuilder sql = new StringBuilder();

        String usuLoginOrg = "";
        String usuLoginOrg2 = "";
        if (ParamSist.paramEquals(CodedValues.TPC_LOGIN_USU_SERVIDOR_COM_EST_ORG_ID, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            usuLoginOrg =" '-',org.orgIdentificador, ";
            usuLoginOrg2 = " '-',org2.orgIdentificador, ";
        }

        sql.append(" SELECT usu");
        sql.append(" FROM Usuario usu");
        sql.append(" INNER JOIN usu.usuarioSerSet usr");
        sql.append(" INNER JOIN usr.servidor ser");
        sql.append(" INNER JOIN ser.registroServidorSet rse");
        sql.append(" INNER JOIN rse.orgao org");
        sql.append(" INNER JOIN org.estabelecimento est");
        sql.append(" WHERE rse.rseCodigo = :rseCodigo");
        sql.append(" AND usu.usuLogin = concat(est.estIdentificador,").append(usuLoginOrg).append("'-', rse.rseMatricula)");
        sql.append(" AND NOT EXISTS (");
        sql.append(" SELECT 1 FROM RegistroServidor rse2");
        sql.append(" INNER JOIN rse2.orgao org2");
        sql.append(" INNER JOIN org2.estabelecimento est2");
        sql.append(" WHERE rse2.rseCodigo <> rse.rseCodigo");
        sql.append(" AND rse2.statusRegistroServidor.srsCodigo in ( :srsCodigos )");
        sql.append(" AND usu.usuLogin = concat(est2.estIdentificador,").append(usuLoginOrg2).append("'-', rse2.rseMatricula)");
        sql.append(" )");

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);
        parameters.put("srsCodigos", CodedValues.SRS_ATIVOS);

        List<Usuario> resultado = findByQuery(sql.toString(), parameters);

        return (resultado != null && !resultado.isEmpty()) ? resultado.get(0) : null;
    }

    public static List<Usuario> listaUsuServidores(String usuCodigo) throws FindException{
        String query = " SELECT usrs.usuario";
        query += " FROM Usuario usu";
        query += " INNER JOIN usu.usuarioSerSet usr";
        query += " INNER JOIN usr.servidor.usuarioSerSet usrs";
        query += " WHERE usu.usuCodigo = :usuCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        return findByQuery(query, parameters);
    }
}
