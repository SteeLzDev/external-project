package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.query.MutationQuery;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.exception.RemoveException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;
import com.zetra.econsig.persistence.SessionUtil;

/**
 * <p>Title: EnderecoServidorHome</p>
 * <p>Description: Classe entidade da tabela tb_endereco_servidor.</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class EnderecoServidorHome extends AbstractEntityHome {

    public static EnderecoServidor findByPrimaryKey(String ensCodigo) throws FindException {

        String query = "From EnderecoServidor ens JOIN FETCH ens.tipoEndereco WHERE ens.ensCodigo = :ensCodigo";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("ensCodigo", ensCodigo);

        List<EnderecoServidor> enderecoServidors = findByQuery(query, parameters);
        if (enderecoServidors == null || enderecoServidors.size() == 0) {
            return null;
        } else if (enderecoServidors.size() == 1) {
            return enderecoServidors.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado", (AcessoSistema) null);
        }
    }

    public static EnderecoServidor findByServidor(String serCodigo, String tieCodigo) throws FindException {
        String query = "From EnderecoServidor ens WHERE ens.servidor.serCodigo = :serCodigo and ens.tipoEndereco.tieCodigo = :tieCodigo ";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("serCodigo", serCodigo);
        parameters.put("tieCodigo", tieCodigo);

        List<EnderecoServidor> enderecoServidors = findByQuery(query, parameters);
        if (enderecoServidors == null || enderecoServidors.size() == 0) {
            return null;
        } else if (enderecoServidors.size() == 1) {
            return enderecoServidors.get(0);
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado", (AcessoSistema) null);
        }
    }

    public static List<EnderecoServidor> listEnderecoServidorByCodigo(String serCodigo) throws FindException {
        String query = "From EnderecoServidor ens LEFT JOIN FETCH ens.tipoEndereco tie WHERE ens.servidor.serCodigo = :serCodigo AND ens.ensAtivo = 1";
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("serCodigo", serCodigo);

        List<EnderecoServidor> enderecoServidors = findByQuery(query, parameters);
        if (enderecoServidors == null || enderecoServidors.size() == 0) {
            return null;
        } else if (enderecoServidors.size() > 0) {
            return enderecoServidors;
        } else {
            throw new FindException("mensagem.erro.mais.de.um.resultado.encontrado", (AcessoSistema) null);
        }
    }


    public static EnderecoServidor create(Servidor servidor, TipoEndereco tipoEndereco, String logradouro,
            String numero, String complemento, String bairro, String municipio, String codigoMunicipio,
            String uf, String cep, Short ensAtivo) throws CreateException {
        EnderecoServidor enderecoServidor = new EnderecoServidor();

        try {
            enderecoServidor.setEnsCodigo(DBHelper.getNextId());
            enderecoServidor.setServidor(servidor);
            enderecoServidor.setTipoEndereco(tipoEndereco);
            enderecoServidor.setEnsLogradouro(logradouro);
            enderecoServidor.setEnsNumero(numero);
            enderecoServidor.setEnsComplemento(complemento);
            enderecoServidor.setEnsBairro(bairro);
            enderecoServidor.setEnsMunicipio(municipio);
            enderecoServidor.setEnsCodigoMunicipio(codigoMunicipio);
            enderecoServidor.setEnsUf(uf);
            enderecoServidor.setEnsCep(cep);
            enderecoServidor.setEnsAtivo(ensAtivo);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }

        create(enderecoServidor);

        return enderecoServidor;
    }

    public static void removeBySer(String serCodigo) throws RemoveException {
        Session session = SessionUtil.getSession();
        try {
            StringBuilder hql = new StringBuilder();

            hql.append("DELETE FROM EnderecoServidor ens WHERE ens.servidor.serCodigo = :serCodigo ");

            MutationQuery queryUpdate = session.createMutationQuery(hql.toString());

            queryUpdate.setParameter("serCodigo", serCodigo);

            queryUpdate.executeUpdate();
            session.flush();
        } catch (Exception ex) {
            throw new RemoveException(ex);
        } finally {
            SessionUtil.closeSession(session);
        }
    }
}
