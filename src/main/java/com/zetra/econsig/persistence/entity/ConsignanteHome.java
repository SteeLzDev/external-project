package com.zetra.econsig.persistence.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: ConsignanteHome</p>
 * <p>Description: Classe Home para a entidade Consignante</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsignanteHome extends AbstractEntityHome {

    public static Consignante findByPrimaryKey(String cseCodigo) throws FindException {
        Consignante consignante = new Consignante();
        consignante.setCseCodigo(cseCodigo);
        return find(consignante, cseCodigo);
    }

    public static Consignante findByIdn(String cseIdentificador) throws FindException {
        String query = "FROM Consignante cse WHERE cse.cseIdentificador = :cseIdentificador";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("cseIdentificador", cseIdentificador);

        List<Consignante> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static Consignante create(String cseIdentificador, String cseNome, String cseCnpj, String cseEmail, String cseResponsavel, String cseLogradouro, Integer cseNro, String cseCompl, String cseBairro,
            String cseCidade, String cseUf, String cseCep, String cseTel, String cseFax, Short cseAtivo, String cseResponsavel2, String cseResponsavel3, String cseRespCargo, String cseRespCargo2,
            String cseRespCargo3, String cseRespTelefone, String cseRespTelefone2, String cseRespTelefone3, String cseIdentificadorInterno, Date cseDataCobranca, String cseEmailFolha, String cseProjetoInadimplencia) throws CreateException {
        Consignante bean = new Consignante();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setCseCodigo(objectId);
            bean.setCseIdentificador(cseIdentificador);
            bean.setCseNome(cseNome);
            bean.setCseCnpj(cseCnpj);
            bean.setCseEmail(cseEmail);
            bean.setCseEmailFolha(cseEmailFolha);
            bean.setCseResponsavel(cseResponsavel);
            bean.setCseResponsavel2(cseResponsavel2);
            bean.setCseResponsavel3(cseResponsavel3);
            bean.setCseRespCargo(cseRespCargo);
            bean.setCseRespCargo2(cseRespCargo2);
            bean.setCseRespCargo3(cseRespCargo3);
            bean.setCseRespTelefone(cseRespTelefone);
            bean.setCseRespTelefone2(cseRespTelefone2);
            bean.setCseRespTelefone3(cseRespTelefone3);
            bean.setCseLogradouro(cseLogradouro);
            bean.setCseNro(cseNro);
            bean.setCseCompl(cseCompl);
            bean.setCseBairro(cseBairro);
            bean.setCseCidade(cseCidade);
            bean.setCseUf(cseUf);
            bean.setCseCep(cseCep);
            bean.setCseTel(cseTel);
            bean.setCseFax(cseFax);
            bean.setCseAtivo(cseAtivo);
            bean.setCseIdentificadorInterno(cseIdentificadorInterno);
            bean.setCseDataCobranca(cseDataCobranca);
            bean.setCseProjetoInadimplencia(cseProjetoInadimplencia);
        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
        create(bean);
        return bean;
    }


}
