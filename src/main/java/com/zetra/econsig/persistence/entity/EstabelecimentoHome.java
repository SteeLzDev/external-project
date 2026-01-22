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
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: EstabelecimentoHome</p>
 * <p>Description: Classe Home para a entidade Estabelecimento</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EstabelecimentoHome extends AbstractEntityHome {

    public static Estabelecimento findByPrimaryKey(String estCodigo) throws FindException {
        Estabelecimento estabelecimento = new Estabelecimento();
        estabelecimento.setEstCodigo(estCodigo);
        return find(estabelecimento, estCodigo);
    }

    public static Estabelecimento findByIdn(String estIdentificador) throws FindException {
        String query = "FROM Estabelecimento est WHERE est.estIdentificador = :estIdentificador";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("estIdentificador", estIdentificador);

        List<Estabelecimento> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Estabelecimento findByCnpj(String estCnpj) throws FindException {
        String query = "FROM Estabelecimento est WHERE est.estCnpj = :estCnpj";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("estCnpj", estCnpj);

        List<Estabelecimento> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Estabelecimento findByLast() throws FindException {
        String query = "FROM Estabelecimento est WHERE isnumeric(est.estIdentificador)=1 ORDER BY est.estIdentificador DESC";

        Map<String, Object> parameters = new HashMap<>();

        List<Estabelecimento> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Estabelecimento findByOrgao(String orgCodigo) throws FindException {
        String query = "SELECT est FROM Estabelecimento est INNER JOIN est.orgaoSet org WHERE org.orgCodigo = :orgCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orgCodigo", orgCodigo);

        List<Estabelecimento> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Estabelecimento create(String cseCodigo, String estIdentificador, String estNome, String estCnpj) throws CreateException {
        Session session = SessionUtil.getSession();
        Estabelecimento bean = new Estabelecimento();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setEstCodigo(objectId);
            bean.setConsignante(session.getReference(Consignante.class, cseCodigo));
            bean.setEstIdentificador(estIdentificador);
            bean.setEstNome(estNome);
            bean.setEstAtivo(CodedValues.STS_ATIVO);
            bean.setEstCnpj(estCnpj);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static Estabelecimento create(String cseCodigo, String estIdentificador, String estNome, String estCnpj, String estEmail, String estResponsavel, String estLogradouro, Integer estNro, String estCompl,
            String estBairro, String estCidade, String estUf, String estCep, String estTel, String estFax, Short estAtivo, String estResponsavel2, String estResponsavel3, String estRespCargo,
            String estRespCargo2, String estRespCargo3  , String estRespTelefone, String estRespTelefone2, String estRespTelefone3, String estFolha) throws CreateException {

        Session session = SessionUtil.getSession();
        Estabelecimento bean = new Estabelecimento();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setEstCodigo(objectId);
            bean.setConsignante(session.getReference(Consignante.class, cseCodigo));
            bean.setEstIdentificador(estIdentificador);
            bean.setEstNome(estNome);
            bean.setEstAtivo(estAtivo);
            bean.setEstCnpj(estCnpj);
            bean.setEstEmail(estEmail);
            bean.setEstResponsavel(estResponsavel);
            bean.setEstResponsavel2(estResponsavel2);
            bean.setEstResponsavel3(estResponsavel3);
            bean.setEstRespCargo(estRespCargo);
            bean.setEstRespCargo2(estRespCargo2);
            bean.setEstRespCargo3(estRespCargo3);
            bean.setEstRespTelefone(estRespTelefone);
            bean.setEstRespTelefone2(estRespTelefone2);
            bean.setEstRespTelefone3(estRespTelefone3);
            bean.setEstLogradouro(estLogradouro);
            bean.setEstNro(estNro);
            bean.setEstCompl(estCompl);
            bean.setEstBairro(estBairro);
            bean.setEstCidade(estCidade);
            bean.setEstUf(estUf);
            bean.setEstCep(estCep);
            bean.setEstTel(estTel);
            bean.setEstFax(estFax);
            bean.setEstFolha(estFolha);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static Estabelecimento findByOrgaoIdentificador(String orgIdentificador) throws FindException {
        String query = "SELECT est FROM Estabelecimento est INNER JOIN est.orgaoSet org WHERE org.orgIdentificador = :orgIdentificador";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orgIdentificador", orgIdentificador);

        List<Estabelecimento> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }
}
