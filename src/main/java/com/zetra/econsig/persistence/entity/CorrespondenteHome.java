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

/**
 * <p>Title: CorrespondenteHome</p>
 * <p>Description: Classe Home para a entidade Correspondente</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class CorrespondenteHome extends AbstractEntityHome {

    public static Correspondente findByPrimaryKey(String corCodigo) throws FindException {
        Correspondente correspondente = new Correspondente();
        correspondente.setCorCodigo(corCodigo);
        return find(correspondente, corCodigo);
    }

    public static List<Correspondente> findByCsa(String csaCodigo) throws FindException {
        String query = "FROM Correspondente cor WHERE cor.consignataria.csaCodigo = :csaCodigo";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);

        return findByQuery(query, parameters);
    }

    public static Correspondente findByIdn(String corIdentificador, String csaCodigo) throws FindException {
        String query = "FROM Correspondente cor WHERE cor.consignataria.csaCodigo = :csaCodigo AND cor.corIdentificador = :corIdentificador";

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("csaCodigo", csaCodigo);
        parameters.put("corIdentificador", corIdentificador);

        List<Correspondente> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Correspondente create(String csaCodigo, String corNome, String corEmail, String corResponsavel, String corLogradouro, Integer corNro, String corCompl, String corBairro, String corCidade,
            String corUf, String corCep, String corTel, String corFax, String corIdentificador, Short corAtivo, String corResponsavel2, String corResponsavel3, String corRespCargo,
            String corRespCargo2, String corRespCargo3, String corRespTelefone, String corRespTelefone2, String corRespTelefone3, String corCnpj,  String corIdentificadorAntigo,
            String corIPAcesso, String corDDNSAcesso, String corExigeEnderecoAcesso, String ecoCodigo) throws CreateException {

        Session session = SessionUtil.getSession();
        Correspondente bean = new Correspondente();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setCorCodigo(objectId);
            bean.setCorIdentificador(corIdentificador);
            bean.setCorAtivo(corAtivo);
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            bean.setCorNome(corNome);
            bean.setCorEmail(corEmail);
            bean.setCorResponsavel(corResponsavel);
            bean.setCorResponsavel2(corResponsavel2);
            bean.setCorResponsavel3(corResponsavel3);
            bean.setCorRespCargo(corRespCargo);
            bean.setCorRespCargo2(corRespCargo2);
            bean.setCorRespCargo3(corRespCargo3);
            bean.setCorRespTelefone(corRespTelefone);
            bean.setCorRespTelefone2(corRespTelefone2);
            bean.setCorRespTelefone3(corRespTelefone3);
            bean.setCorLogradouro(corLogradouro);
            bean.setCorNro(corNro);
            bean.setCorCompl(corCompl);
            bean.setCorBairro(corBairro);
            bean.setCorCidade(corCidade);
            bean.setCorUf(corUf);
            bean.setCorCep(corCep);
            bean.setCorTel(corTel);
            bean.setCorFax(corFax);
            bean.setCorCnpj(corCnpj);
            bean.setCorIdentificadorAntigo(corIdentificadorAntigo);
            bean.setCorIpAcesso(corIPAcesso);
            bean.setCorDdnsAcesso(corDDNSAcesso);
            bean.setCorExigeEnderecoAcesso(corExigeEnderecoAcesso);
            if (ecoCodigo != null) {
                bean.setEmpresaCorrespondente(session.getReference(EmpresaCorrespondente.class, ecoCodigo));
            }
            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }
}
