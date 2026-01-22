package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.texto.TextHelper;
import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: OrgaoHome</p>
 * <p>Description: Classe Home para a entidade Orgao</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class OrgaoHome extends AbstractEntityHome {

    public static Orgao findByPrimaryKey(String orgCodigo) throws FindException {
        Orgao orgao = new Orgao();
        orgao.setOrgCodigo(orgCodigo);
        return find(orgao, orgCodigo);
    }

    public static Orgao findByIdn(String orgIdentificador, String estCodigo) throws FindException {
        String query = "FROM Orgao org WHERE org.orgIdentificador = :orgIdentificador AND org.estabelecimento.estCodigo = :estCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orgIdentificador", orgIdentificador);
        parameters.put("estCodigo", estCodigo);

        List<Orgao> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Orgao findByAdeCod(String adeCod) throws FindException {
        String query = "SELECT org FROM AutDesconto ade " +
                       "INNER JOIN ade.registroServidor rs " +
                       "INNER JOIN rs.orgao org " +
                       "WHERE ade.adeCodigo = :adeCodigo ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("adeCodigo", adeCod);

        List<Orgao> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static List<Orgao> listOrgCnvDIRF() throws FindException {
        StringBuilder corpoBuilder = new StringBuilder("select org ");

        corpoBuilder.append("from Orgao org INNER JOIN ");
        corpoBuilder.append("org.convenioSet cnv WHERE cnv.cnvCodVerbaDirf is not NULL ");
        corpoBuilder.append("GROUP BY org.orgCodigo ");

        return findByQuery(corpoBuilder.toString(), null);
    }

    public static Orgao findByRseCod(String rseCod) throws FindException {
        String query = "SELECT org FROM RegistroServidor rs " +
                       "INNER JOIN rs.orgao org " +
                       "WHERE rs.rseCodigo = :rseCodigo ";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rseCodigo", rseCod);

        List<Orgao> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Orgao create(String estCodigo, String orgIdentificador, String orgNome, String orgCnpj) throws CreateException {
        Session session = SessionUtil.getSession();
        Orgao bean = new Orgao();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setOrgCodigo(objectId);
            bean.setEstabelecimento(session.getReference(Estabelecimento.class, estCodigo));
            bean.setOrgIdentificador(orgIdentificador);
            bean.setOrgNome(orgNome);
            bean.setOrgCnpj(orgCnpj);
            bean.setOrgAtivo(CodedValues.STS_ATIVO);
            create(bean, session);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static Orgao create(String estCodigo, String orgIdentificador, String orgNome, String orgNomeAbrev, String orgCnpj, String orgEmail, String orgResponsavel, String orgLogradouro, Integer orgNro, String orgCompl, String orgBairro,
            String orgCidade, String orgUf, String orgCep, String orgTel, String orgFax, Short orgAtivo, String orgResponsavel2, String orgResponsavel3, String orgRespCargo,
            String orgRespCargo2, String orgRespCargo3 , String orgRespTelefone, String orgRespTelefone2, String orgRespTelefone3, Integer orgDiaRepasse, String orgIPAcesso, String orgDDNSAcesso, String orgEmailFolha, String orgFolha) throws CreateException {

        Session session = SessionUtil.getSession();
        Orgao bean = new Orgao();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setOrgCodigo(objectId);
            bean.setOrgAtivo(orgAtivo);
            bean.setOrgCnpj(orgCnpj);
            bean.setOrgNomeAbrev(orgNomeAbrev);
            bean.setEstabelecimento(session.getReference(Estabelecimento.class, estCodigo));
            bean.setOrgIdentificador(orgIdentificador);
            bean.setOrgNome(orgNome);
            bean.setOrgEmail(orgEmail);
            bean.setOrgResponsavel(orgResponsavel);
            bean.setOrgResponsavel2(orgResponsavel2);
            bean.setOrgResponsavel3(orgResponsavel3);
            bean.setOrgRespCargo(orgRespCargo);
            bean.setOrgRespCargo2(orgRespCargo2);
            bean.setOrgRespCargo3(orgRespCargo3);
            bean.setOrgLogradouro(orgLogradouro);
            bean.setOrgRespTelefone(orgRespTelefone);
            bean.setOrgRespTelefone2(orgRespTelefone2);
            bean.setOrgRespTelefone3(orgRespTelefone3);
            bean.setOrgNro(orgNro);
            bean.setOrgCompl(orgCompl);
            bean.setOrgBairro(orgBairro);
            bean.setOrgCidade(orgCidade);
            bean.setOrgUf(orgUf);
            bean.setOrgCep(orgCep);
            bean.setOrgTel(orgTel);
            bean.setOrgFax(orgFax);
            bean.setOrgDiaRepasse(orgDiaRepasse);
            bean.setOrgIpAcesso(orgIPAcesso);
            bean.setOrgDdnsAcesso(orgDDNSAcesso);
            bean.setOrgEmailFolha(orgEmailFolha);
            bean.setOrgFolha(orgFolha);
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static Orgao findByOrgCnpj(String orgCnpj) throws FindException {
        String query = "FROM Orgao org WHERE org.orgCnpj = :orgCnpj";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orgCnpj", orgCnpj);

        List<Orgao> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Orgao findByIdentificador(String orgIdentificador) throws FindException {
        String query = "FROM Orgao org WHERE org.orgIdentificador = :orgIdentificador";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("orgIdentificador", orgIdentificador);

        List<Orgao> result = findByQuery(query, parameters);
        if (!TextHelper.isNull(result) && !result.isEmpty()) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }
}
