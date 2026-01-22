package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ConsignatariaPermiteTdaHome</p>
 * <p>Description: Classe Home para a entidade ConsignatariaPermiteTda</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ConsignatariaPermiteTdaHome extends AbstractEntityHome {

    public static ConsignatariaPermiteTda findByPrimaryKey(ConsignatariaPermiteTdaId pk) throws FindException {
        ConsignatariaPermiteTda consignatariaPermiteTda = new ConsignatariaPermiteTda();
        consignatariaPermiteTda.setId(pk);
        return find(consignatariaPermiteTda, pk);
    }

    public static String getConsignatariaPermiteTipoDadoAdicional(String csaCodigo, String tdaCodigo) throws FindException {
        try {
            // Se o serviço está ligado ao tipo de dado, retorna o valor que está configurado
            // na tabela de ligação pelo campo CptExibe
            ConsignatariaPermiteTda cpt = findByPrimaryKey(new ConsignatariaPermiteTdaId(csaCodigo, tdaCodigo));
            return cpt.getCptExibe();
        } catch (FindException ex) {
            // Se o serviço não tem ligação com o tipo de dado, verifica se o tipo de dado
            // tem alguma ligação com qualquer serviço, para determinar se pode ser exibido
            String query = "FROM ConsignatariaPermiteTda cpt WHERE cpt.id.tdaCodigo = :tdaCodigo";

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("tdaCodigo", tdaCodigo);

            List<ConsignatariaPermiteTda> cpts = findByQuery(query, parameters);
            if (cpts == null || cpts.isEmpty()) {
                // Se o tipo de dado não tem relação com nenhum serviço, então ele pode ser
                // exibido, porém não sendo obrigatório
                return CodedValues.CAS_SIM;
            } else {
                // Se o tipo de dado tem relação com algum serviço, verifica se é uma relação
                // negativa, ou seja, só está relacionado a serviços que estão configurados como N
                for (ConsignatariaPermiteTda cpt : cpts) {
                    if (!cpt.getCptExibe().equals(CodedValues.CAS_NAO)) {
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

    public static ConsignatariaPermiteTda create(String csaCodigo, String tdaCodigo, String cptExibe) throws CreateException {
        ConsignatariaPermiteTda bean = new ConsignatariaPermiteTda();

        ConsignatariaPermiteTdaId id = new ConsignatariaPermiteTdaId();
        id.setCsaCodigo(csaCodigo);
        id.setTdaCodigo(tdaCodigo);
        bean.setId(id);
        bean.setCptExibe(cptExibe);

        create(bean);
        return bean;
    }
}
