package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: NaturezaServicoHome</p>
 * <p>Description: Classe Home para a entidade EnderecoConsignataria</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EnderecoConsignatariaHome extends AbstractEntityHome {

    public static EnderecoConsignataria findByPrimaryKey(String encCodigo) throws FindException {
        EnderecoConsignataria enderecoConsignataria = new EnderecoConsignataria();
        enderecoConsignataria.setEncCodigo(encCodigo);
        return find(enderecoConsignataria, encCodigo);
    }

    public static EnderecoConsignataria findByPrimaryKeyAndCsaCodigo(String encCodigo, String csaCodigo) throws FindException {
        EnderecoConsignataria enderecoConsignataria = new EnderecoConsignataria();
        enderecoConsignataria.setEncCodigo(encCodigo);
        Consignataria csa = new Consignataria();
        csa.setCsaCodigo(csaCodigo);
        enderecoConsignataria.setConsignataria(csa);
        return find(enderecoConsignataria, encCodigo);
    }

    public static EnderecoConsignataria create(String csaCodigo, String tieCodigo, String encLogradouro, String encNumero, String encComplemento, String encBairro,
            String encMunicipio, String encUf, String encCep, BigDecimal encLatitude, BigDecimal encLongitude) throws CreateException {
        Session session = SessionUtil.getSession();
        EnderecoConsignataria bean = new EnderecoConsignataria();

        try {
            bean.setEncCodigo(DBHelper.getNextId());
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            bean.setTipoEndereco(session.getReference(TipoEndereco.class, tieCodigo));
            bean.setEncLogradouro(encLogradouro);
            bean.setEncNumero(encNumero);
            bean.setEncComplemento(encComplemento);
            bean.setEncBairro(encBairro);
            bean.setEncMunicipio(encMunicipio);
            bean.setEncUf(encUf);
            bean.setEncCep(encCep);
            bean.setEncLatitude(encLatitude);
            bean.setEncLongitude(encLongitude);

            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException("mensagem.erro.endereco.consignataria.criar", (AcessoSistema) null, ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static EnderecoConsignataria update(String encCodigo, String csaCodigo, String tieCodigo, String encLogradouro, String encNumero, String encComplemento, String encBairro,
            String encMunicipio, String encUf, String encCep, BigDecimal encLatitude, BigDecimal encLongitude) throws UpdateException {
        Session session = SessionUtil.getSession();
        EnderecoConsignataria bean = new EnderecoConsignataria();

        try {
            bean.setEncCodigo(encCodigo);
            bean.setConsignataria(session.getReference(Consignataria.class, csaCodigo));
            bean.setTipoEndereco(session.getReference(TipoEndereco.class, tieCodigo));
            bean.setEncLogradouro(encLogradouro);
            bean.setEncNumero(encNumero);
            bean.setEncComplemento(encComplemento);
            bean.setEncBairro(encBairro);
            bean.setEncMunicipio(encMunicipio);
            bean.setEncUf(encUf);
            bean.setEncCep(encCep);
            bean.setEncLatitude(encLatitude);
            bean.setEncLongitude(encLongitude);

            update(bean);
        } catch (UpdateException ex) {
            throw new UpdateException("mensagem.erro.endereco.consignataria.atualizar", (AcessoSistema) null, ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}