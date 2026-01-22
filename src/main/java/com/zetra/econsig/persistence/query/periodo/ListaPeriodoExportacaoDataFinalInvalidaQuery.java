package com.zetra.econsig.persistence.query.periodo;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListaPeriodoExportacaoDataFinalInvalidaQuery</p>
 * <p>Description: Retorna os registros de período exportação que possuem data fim maior
 * que a data atual ou menor do que a data limite, configurada através do parâmetro de sistema
 * TPC_QTDE_DIAS_LIMITE_MIN_PEX_DATA_FIM. Isto indica que o período de exportação é inválido
 * e não pode ser utilizado pelas rotinas de exportação de movimento e importação de retorno.</p>
 * <p>Copyright: Copyright (c) 2011</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaPeriodoExportacaoDataFinalInvalidaQuery extends HQuery {

    private final List<String> orgCodigos;
    private final List<String> estCodigos;

    public ListaPeriodoExportacaoDataFinalInvalidaQuery(List<String> orgCodigos, List<String> estCodigos) {
        this.orgCodigos = orgCodigos;
        this.estCodigos = estCodigos;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        // Valor default = 30
        int qtdDias = 30;

        Object param = ParamSist.getInstance().getParam(CodedValues.TPC_QTDE_DIAS_LIMITE_MIN_PEX_DATA_FIM, AcessoSistema.getAcessoUsuarioSistema());
        if (param != null && !param.equals("")) {
            try {
                qtdDias = Integer.parseInt(param.toString());
            } catch (NumberFormatException e) {
                qtdDias = 30;
            }
        }

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("select org.orgNome, pex.pexDataFim, add_day(current_date(), -1 * " + qtdDias + ")");
        corpoBuilder.append(" from PeriodoExportacao pex");
        corpoBuilder.append(" inner join pex.orgao org");

        corpoBuilder.append(" where 1=1");

        if (ParamSist.paramEquals(CodedValues.TPC_PERMITE_EXPORTAR_MOVIMENTO_DATA_FIM_FUTURA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            // Se permite exportar período com data fim futura, então não valida se a data fim é maior que a data atual
            corpoBuilder.append(" and (");
        } else {
            corpoBuilder.append(" and (pex.pexDataFim >= current_date() or ");
        }
        corpoBuilder.append(" pex.pexDataFim <= add_day(current_date(), -1 * " + qtdDias + "))");

        if (estCodigos != null && estCodigos.size() > 0) {
            corpoBuilder.append(" and org.estabelecimento.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigos));
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            corpoBuilder.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigos));
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (estCodigos != null && estCodigos.size() > 0) {
            defineValorClausulaNomeada("estCodigo", estCodigos, query);
        }
        if (orgCodigos != null && orgCodigos.size() > 0) {
            defineValorClausulaNomeada("orgCodigo", orgCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "ORGAO",
                "DATA_FIM",
                "DATA_FIM_LIMITE"
        };
    }
}
