package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

public class DashboardFlexConsultaHome extends AbstractEntityHome {

    public static DashboardFlexConsulta findByPrimaryKey(String dfoCOdigo) throws FindException {
        DashboardFlexConsulta dashboardFlexConsulta = new DashboardFlexConsulta();
        dashboardFlexConsulta.setDfoCodigo(dfoCOdigo);
        return find(dashboardFlexConsulta, dfoCOdigo);
    }

    public static DashboardFlexConsulta createDashboardFlexConsulta(DashboardFlexConsulta dashboardFlexConsulta) throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

    public static List<DashboardFlexConsulta> listarDashboardFlexConsulta(List<String> dflCodigos, boolean somentAtivos) throws FindException {
        StringBuilder query = new StringBuilder();

        query.append("FROM DashboardFlexConsulta dfo ");
        query.append(" WHERE 1=1 ");
        
        if (somentAtivos){
            query.append(" AND dfo.dfoAtivo = :ativo");
        }
        
        if (dflCodigos != null && !dflCodigos.isEmpty()) {
            query.append(" AND dfo.dflCodigo IN (:dflCodigos) ");
        }
        
        final Map<String, Object> parameters = new HashMap<>();
        if (somentAtivos) {
            parameters.put("ativo", CodedValues.STS_ATIVO);
        }

        if (dflCodigos != null && !dflCodigos.isEmpty()) {
            parameters.put("dflCodigos", dflCodigos);
        }

        return findByQuery(query.toString(), parameters);
    }
}