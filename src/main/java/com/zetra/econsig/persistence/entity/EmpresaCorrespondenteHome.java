package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.exception.MissingPrimaryKeyException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.sistema.DBHelper;

/**
 * <p>Title: EmpresaCorrespondenteHome</p>
 * <p>Description: Classe Home para a entidade EmpresaCorrespondente</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class EmpresaCorrespondenteHome extends AbstractEntityHome {

    public static EmpresaCorrespondente findByPrimaryKey(String ecoCodigo) throws FindException {
        EmpresaCorrespondente empresaCorrespondente = new EmpresaCorrespondente();
        empresaCorrespondente.setEcoCodigo(ecoCodigo);
        return find(empresaCorrespondente, ecoCodigo);
    }

    public static EmpresaCorrespondente findByCNPJ(String ecoCnpj) throws FindException {
        String query = "FROM EmpresaCorrespondente eco WHERE eco.ecoCnpj = :ecoCnpj";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ecoCnpj", ecoCnpj);

        List<EmpresaCorrespondente> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static EmpresaCorrespondente findByIdentificador(String ecoIdentificador) throws FindException {
        String query = "FROM EmpresaCorrespondente eco WHERE eco.ecoIdentificador = :ecoIdentificador";

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("ecoIdentificador", ecoIdentificador);

        List<EmpresaCorrespondente> result = findByQuery(query, parameters);
        if (result != null && result.size() > 0) {
            return result.get(0);
        }
        throw new FindException("mensagem.erro.entidade.nao.encontrada", (AcessoSistema) null);
    }

    public static EmpresaCorrespondente create(Short ecoAtivo, String ecoBairro, String ecoCep, String ecoCidade,
            String ecoCnpj, String ecoCompl, String ecoEmail, String ecoFax, String ecoIdentificador, String ecoLogradouro,
            String ecoNome, Integer ecoNro, String ecoRespCargo, String ecoRespCargo2, String ecoRespCargo3,
            String ecoResponsavel, String ecoResponsavel2, String ecoResponsavel3, String ecoRespTelefone,
            String ecoRespTelefone2, String ecoRespTelefone3, String ecoTel, String ecoUf) throws CreateException {
        EmpresaCorrespondente bean = new EmpresaCorrespondente();

        String objectId = null;
        try {
            objectId = DBHelper.getNextId();
            bean.setEcoCodigo(objectId);

            bean.setEcoAtivo(ecoAtivo);
            bean.setEcoBairro(ecoBairro);
            bean.setEcoCep(ecoCep);
            bean.setEcoCidade(ecoCidade);
            bean.setEcoCnpj(ecoCnpj);
            bean.setEcoCompl(ecoCompl);
            bean.setEcoEmail(ecoEmail);
            bean.setEcoFax(ecoFax);
            bean.setEcoIdentificador(ecoIdentificador);
            bean.setEcoLogradouro(ecoLogradouro);
            bean.setEcoNome(ecoNome);
            bean.setEcoNro(ecoNro);
            bean.setEcoRespCargo(ecoRespCargo);
            bean.setEcoRespCargo2(ecoRespCargo2);
            bean.setEcoRespCargo3(ecoRespCargo3);
            bean.setEcoResponsavel(ecoResponsavel);
            bean.setEcoResponsavel2(ecoResponsavel2);
            bean.setEcoResponsavel3(ecoResponsavel3);
            bean.setEcoRespTelefone(ecoRespTelefone);
            bean.setEcoRespTelefone2(ecoRespTelefone2);
            bean.setEcoRespTelefone3(ecoRespTelefone3);
            bean.setEcoTel(ecoTel);
            bean.setEcoUf(ecoUf);

        } catch (MissingPrimaryKeyException ex) {
            throw new CreateException(ex);
        }
        create(bean);
        return bean;
    }
}
