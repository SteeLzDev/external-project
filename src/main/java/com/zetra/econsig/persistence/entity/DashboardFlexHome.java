package com.zetra.econsig.persistence.entity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.exception.CreateException;
import com.zetra.econsig.exception.FindException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.values.CodedValues;

import br.com.nostrum.simpletl.util.TextHelper;

public class DashboardFlexHome extends AbstractEntityHome {

    public static DashboardFlex findByPrimaryKey(String dflCodigo) throws FindException {
        DashboardFlex dashboardFlex = new DashboardFlex();
        dashboardFlex.setDflCodigo(dflCodigo);
        return find(dashboardFlex, dflCodigo);
    }

    public static DashboardFlex createDashboardFlex(DashboardFlex dashboardFlex) throws CreateException {
        throw new CreateException("mensagem.erro.metodo.nao.implementado", (AcessoSistema) null);
    }

    public static List<DashboardFlex> listarDashboardFlex(boolean somentAtivos, String papCodigo, List<String> funCodigos) throws FindException {
        StringBuilder query = new StringBuilder();

        query.append("FROM DashboardFlex dfl ");
        query.append(" WHERE 1=1 ");
        if (somentAtivos){
            query.append(" AND dfl.dflAtivo = :ativo");
        } 

        if (!TextHelper.isNull(papCodigo)) {
            query.append(" AND dfl.papCodigo = :papCodigo");
        }

        if (funCodigos != null && !funCodigos.isEmpty()) {
            query.append(" AND dfl.funCodigo IN :funCodigos");
        }
        
        final Map<String, Object> parameters = new HashMap<>();
        if (somentAtivos) {
            parameters.put("ativo", CodedValues.STS_ATIVO);
        }

        if (!TextHelper.isNull(papCodigo)) {
            parameters.put("papCodigo", papCodigo);
        }

        if (funCodigos != null && !funCodigos.isEmpty()) {
            parameters.put("funCodigos", funCodigos);
        }

        return findByQuery(query.toString(), parameters);
    }
}