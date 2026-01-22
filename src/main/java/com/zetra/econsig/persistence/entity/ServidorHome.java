package com.zetra.econsig.persistence.entity;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ServidorHome</p>
 * <p>Description: Classe Home para a entidade Servidor</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ServidorHome extends AbstractEntityHome {

    public static Servidor findByPrimaryKey(String serCodigo) throws FindException {
        final Servidor servidor = new Servidor();
        servidor.setSerCodigo(serCodigo);
        return find(servidor, serCodigo);
    }

    public static Servidor findByRseCodigo(String rseCodigo) throws FindException {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT ser FROM RegistroServidor rse ");
        query.append("INNER JOIN rse.servidor ser ");
        query.append("WHERE rse.rseCodigo = :rseCodigo ");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCodigo);

        final List<Servidor> result = findByQuery(query.toString(), parameters);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<Servidor> findByCPF(String serCpf) throws FindException {
        final String query = "FROM Servidor ser WHERE ser.serCpf = :serCpf";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("serCpf", serCpf);

        return findByQuery(query, parameters);
    }

    public static List<Servidor> findByCPFFetchRegistroServidor(String serCpf) throws FindException {
        final String query = "FROM Servidor ser LEFT JOIN FETCH ser.registroServidorSet rse WHERE ser.serCpf = :serCpf";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("serCpf", serCpf);

        return findByQuery(query, parameters);
    }

    public static Servidor findByCPFNome(String serCpf, String serNome) throws FindException {
        final String query = "FROM Servidor ser WHERE ser.serCpf = :serCpf AND ser.serNome = :serNome";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("serCpf", serCpf);
        parameters.put("serNome", serNome);

        final List<Servidor> result = findByQuery(query, parameters);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<Servidor> findByDataNascNome(Date serDataNasc, String serNome) throws FindException {
        final String query = "FROM Servidor ser LEFT JOIN FETCH ser.registroServidorSet rse WHERE ser.serDataNasc = :serDataNasc AND ser.serNome = :serNome ";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("serDataNasc", serDataNasc);
        parameters.put("serNome", serNome);

        return findByQuery(query, parameters);
    }

    public static Servidor findByUsuCodigo(String usuCodigo) throws FindException {
        final String query = "FROM Servidor ser INNER JOIN ser.usuarioSerSet usu WHERE usu.id.usuCodigo= :usuCodigo";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("usuCodigo", usuCodigo);

        final List<Servidor> result = findByQuery(query, parameters);
        if ((result != null) && !result.isEmpty()) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<Servidor> findByEmail(String serEmail) throws FindException {
        final String query = "FROM Servidor ser WHERE ser.serEmail = :serEmail";

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("serEmail", serEmail);

        return findByQuery(query, parameters);
    }

    public static Servidor create(String serCpf, Date serDataNasc, String serNomeMae, String serNomePai, String serNome, String serPrimeiroNome,
            String serNomeMeio, String serUltimoNome, String serTitulacao, String serSexo, String serEstCivil, Short serQtdFilhos, String serNacionalidade, String serNroIdt,
            String serCartProf, String serPis, String serEnd, String serBairro, String serCidade, String serCompl, String serNro, String serCep, String serUf, String serTel,
            String serEmail, String serEmissorIdt, String serUfIdt, Date serDataIdt, String serCidNasc, String serUfNasc, String serNomeConjuge, String serDeficienteVisual, java.util.Date serDataAlteracao,
            String serCelular, String serAcessaHostaHost, String nesCodigo, String thaCodigo, String sseCodigo) throws CreateException {

        final Session session = SessionUtil.getSession();
        final Servidor bean = new Servidor();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setSerCodigo(objectId);
            bean.setSerCpf(serCpf);
            bean.setSerDataNasc(serDataNasc);
            bean.setSerNomeMae(serNomeMae);
            bean.setSerNomePai(serNomePai);
            bean.setSerNome(serNome);
            bean.setSerPrimeiroNome(serPrimeiroNome);
            bean.setSerNomeMeio(serNomeMeio);
            bean.setSerUltimoNome(serUltimoNome);
            bean.setSerTitulacao(serTitulacao);
            bean.setSerSexo(serSexo);
            bean.setSerEstCivil(serEstCivil);
            bean.setSerQtdFilhos(serQtdFilhos);
            bean.setSerNacionalidade(serNacionalidade);
            bean.setSerNroIdt(serNroIdt);
            bean.setSerCartProf(serCartProf);
            bean.setSerPis(serPis);
            bean.setSerEnd(serEnd);
            bean.setSerBairro(serBairro);
            bean.setSerCidade(serCidade);
            bean.setSerCompl(serCompl);
            bean.setSerNro(serNro);
            bean.setSerCep(serCep);
            bean.setSerUf(serUf);
            bean.setSerTel(serTel);
            bean.setSerEmail(serEmail);
            bean.setSerEmissorIdt(serEmissorIdt);
            bean.setSerUfIdt(serUfIdt);
            bean.setSerDataIdt(serDataIdt);
            bean.setSerCidNasc(serCidNasc);
            bean.setSerUfNasc(serUfNasc);
            bean.setSerNomeConjuge(serNomeConjuge);
            bean.setSerDeficienteVisual(serDeficienteVisual);
            bean.setSerDataAlteracao(serDataAlteracao);
            bean.setSerCelular(serCelular);
            bean.setSerAcessaHostAHost(serAcessaHostaHost);

            bean.setNivelEscolaridade(!TextHelper.isNull(nesCodigo) ? (NivelEscolaridade) session.getReference(NivelEscolaridade.class, nesCodigo) : null);
            bean.setTipoHabitacao(!TextHelper.isNull(thaCodigo) ? (TipoHabitacao) session.getReference(TipoHabitacao.class, thaCodigo) : null);
            bean.setStatusServidor(!TextHelper.isNull(sseCodigo) ? (StatusServidor) session.getReference(StatusServidor.class, sseCodigo) : null);

            // DESENV-12892
            // Bloquear a edição de e-mail caso este tenha sido enviado pela folha
            if (!TextHelper.isNull(serEmail) && ParamSist.paramEquals(CodedValues.TPC_BLOQUEIA_EDICAO_EMAIL_SERVIDOR_CAD_FOLHA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
                bean.setSerPermiteAlterarEmail(CodedValues.TPC_NAO);
            } else {
                bean.setSerPermiteAlterarEmail(CodedValues.TPC_SIM);
            }

            create(bean, session);
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
        return bean;
    }

    public static Servidor create(String serCpf, Date serDataNasc, String serNomeMae, String serNomePai, String serNome) throws CreateException {
        final Servidor bean = new Servidor();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setSerCodigo(objectId);
            bean.setSerCpf(serCpf);
            bean.setSerDataNasc(serDataNasc);
            bean.setSerNomeMae(serNomeMae);
            bean.setSerNomePai(serNomePai);
            bean.setSerNome(serNome);
        } catch (final MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
        create(bean);
        return bean;
    }

    public static Servidor findByRseMatricula(String rseMatricula) throws FindException {
        final StringBuilder query = new StringBuilder();
        query.append("SELECT ser FROM RegistroServidor rse ");
        query.append("INNER JOIN rse.servidor ser ");
        query.append("WHERE rse.rseMatricula = :rseMatricula ");

        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseMatricula", rseMatricula);

        final List<Servidor> result = findByQuery(query.toString(), parameters);
        if ((result != null) && (result.size() > 0)) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }
}
