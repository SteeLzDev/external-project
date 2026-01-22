package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ServicoPermiteTdaHome</p>
 * <p>Description: Classe Home para a entidade ServicoPermiteTda</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ServicoPermiteTdaHome extends AbstractEntityHome {

    public static ServicoPermiteTda findByPrimaryKey(ServicoPermiteTdaId pk) throws FindException {
        ServicoPermiteTda servicoPermiteTda = new ServicoPermiteTda();
        servicoPermiteTda.setId(pk);
        return find(servicoPermiteTda, pk);
    }

    public static String getServicoPermiteTipoDadoAdicional(String svcCodigo, String tdaCodigo) throws FindException {
        try {
            // Se o serviço está ligado ao tipo de dado, retorna o valor que está configurado
            // na tabela de ligação pelo campo SptExibe
            ServicoPermiteTda spt = findByPrimaryKey(new ServicoPermiteTdaId(svcCodigo, tdaCodigo));
            return spt.getSptExibe();
        } catch (FindException ex) {
            // Se o serviço não tem ligação com o tipo de dado, verifica se o tipo de dado
            // tem alguma ligação com qualquer serviço, para determinar se pode ser exibido
            String query = "FROM ServicoPermiteTda spt WHERE spt.id.tdaCodigo = :tdaCodigo";

            Map<String, Object> parameters = new HashMap<String, Object>();
            parameters.put("tdaCodigo", tdaCodigo);

            List<ServicoPermiteTda> spts = findByQuery(query, parameters);
            if (spts == null || spts.isEmpty()) {
                // Se o tipo de dado não tem relação com nenhum serviço, então ele pode ser
                // exibido, porém não sendo obrigatório
                return CodedValues.CAS_SIM;
            } else {
                // Se o tipo de dado tem relação com algum serviço, verifica se é uma relação
                // negativa, ou seja, só está relacionado a serviços que estão configurados como N
                for (ServicoPermiteTda spt : spts) {
                    if (!spt.getSptExibe().equals(CodedValues.CAS_NAO)) {
                        // Se tem algum serviço configurado com valor diferente de N, então o serviço
                        // atual não tem permissão de exibição deste tipo de dado
                        return CodedValues.CAS_NAO;
                    }
                }

                // Só chega até aqui, e todos os serviços estão configurados como N, então
                // o serviço atual, que não tem ligação com o tipo de dado, pode exibí-lo
                return CodedValues.CAS_SIM;
            }
        }
    }

    public static ServicoPermiteTda create(String svcCodigo, String tdaCodigo, String sptExibe) throws CreateException {
        ServicoPermiteTda bean = new ServicoPermiteTda();

        ServicoPermiteTdaId id = new ServicoPermiteTdaId();
        id.setSvcCodigo(svcCodigo);
        id.setTdaCodigo(tdaCodigo);
        bean.setId(id);
        bean.setSptExibe(sptExibe);

        create(bean);
        return bean;
    }
}
