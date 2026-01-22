package com.zetra.econsig.persistence.entity;

import java.math.BigDecimal;

import org.hibernate.Session;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.UpdateException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: NaturezaServicoHome</p>
 * <p>Description: Classe Home para a entidade EnderecoCorrespondente</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author: junio.goncalves $
 * $Revision: 30241 $
 * $Date: 2020-08-28 11:35:31 -0300 (Sex, 28 ago 2020) $
 */
public class EnderecoCorrespondenteHome extends AbstractEntityHome {

    public static EnderecoCorrespondente findByPrimaryKey(String ecrCodigo) throws FindException {
        EnderecoCorrespondente enderecoCorrespondente = new EnderecoCorrespondente();
        enderecoCorrespondente.setEcrCodigo(ecrCodigo);
        return find(enderecoCorrespondente, ecrCodigo);
    }

    public static EnderecoCorrespondente findByPrimaryKeyAndCorCodigo(String ecrCodigo, String csaCodigo, String corCodigo) throws FindException {
        EnderecoCorrespondente enderecoCorrespondente = new EnderecoCorrespondente();
        enderecoCorrespondente.setEcrCodigo(ecrCodigo);
        Correspondente cor = new Correspondente();
        cor.setCorCodigo(corCodigo);
        enderecoCorrespondente.setCorrespondente(cor);
        if (!TextHelper.isNull(csaCodigo)) {
            Consignataria csa = new Consignataria();
            csa.setCsaCodigo(csaCodigo);
            cor.setConsignataria(csa);
        }
        return find(enderecoCorrespondente, ecrCodigo);
    }

    public static EnderecoCorrespondente create(String corCodigo, String tieCodigo, String ecrLogradouro, String ecrNumero, String ecrComplemento, String ecrBairro,
            String ecrMunicipio, String ecrUf, String ecrCep, BigDecimal ecrLatitude, BigDecimal ecrLongitude) throws CreateException {
        Session session = SessionUtil.getSession();
        EnderecoCorrespondente bean = new EnderecoCorrespondente();

        try {
            bean.setEcrCodigo(DBHelper.getNextId());
            bean.setCorrespondente(session.getReference(Correspondente.class, corCodigo));
            bean.setTipoEndereco(session.getReference(TipoEndereco.class, tieCodigo));
            bean.setEcrLogradouro(ecrLogradouro);
            bean.setEcrNumero(ecrNumero);
            bean.setEcrComplemento(ecrComplemento);
            bean.setEcrBairro(ecrBairro);
            bean.setEcrMunicipio(ecrMunicipio);
            bean.setEcrUf(ecrUf);
            bean.setEcrCep(ecrCep);
            bean.setEcrLatitude(ecrLatitude);
            bean.setEcrLongitude(ecrLongitude);

            create(bean, session);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException("mensagem.erro.endereco.correspondente.criar", (AcessoSistema) null, ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

    public static EnderecoCorrespondente update(String ecrCodigo, String corCodigo, String tieCodigo, String ecrLogradouro, String ecrNumero, String ecrComplemento, String ecrBairro,
            String ecrMunicipio, String ecrUf, String ecrCep, BigDecimal ecrLatitude, BigDecimal ecrLongitude) throws UpdateException {
        Session session = SessionUtil.getSession();
        EnderecoCorrespondente bean = new EnderecoCorrespondente();

        try {
            bean.setEcrCodigo(ecrCodigo);
            bean.setCorrespondente(session.getReference(Correspondente.class, corCodigo));
            bean.setTipoEndereco(session.getReference(TipoEndereco.class, tieCodigo));
            bean.setEcrLogradouro(ecrLogradouro);
            bean.setEcrNumero(ecrNumero);
            bean.setEcrComplemento(ecrComplemento);
            bean.setEcrBairro(ecrBairro);
            bean.setEcrMunicipio(ecrMunicipio);
            bean.setEcrUf(ecrUf);
            bean.setEcrCep(ecrCep);
            bean.setEcrLatitude(ecrLatitude);
            bean.setEcrLongitude(ecrLongitude);

            update(bean);
        } catch (UpdateException ex) {
            throw new UpdateException("mensagem.erro.endereco.correspondente.atualizar", (AcessoSistema) null, ex);
        } finally {
            SessionUtil.closeSession(session);
        }

        return bean;
    }

}