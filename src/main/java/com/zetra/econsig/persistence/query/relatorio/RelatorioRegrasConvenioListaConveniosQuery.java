package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: RelatorioRegrasConvenioListaConveniosQuery</p>
 * <p>Description: Query Relatório de Decisões Judiciais</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioRegrasConvenioListaConveniosQuery extends ReportHQuery {

    private final AcessoSistema responsavel;

    public RelatorioRegrasConvenioListaConveniosQuery(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String csaCodigo = responsavel.getCsaCodigo();
        String orgCodigo = responsavel.getOrgCodigo();

        StringBuilder corpo = new StringBuilder();
        corpo.append("select ");
        corpo.append("csa.csaCodigo as CSA_CODIGO, csa.csaNome as CSA_NOME, ");
        corpo.append("max(case when (nse.nseCodigo is not null) then 1 else 0 end) as SALARY_PAY, ");
        // Buscar usuário que possui permissão para (fun_codigo 260 e 261)
        corpo.append("max(case when exists (select 1 from FuncaoPerfilCsa funPer where csa.csaCodigo = funPer.csaCodigo");
        corpo.append(" and funPer.funCodigo in ('").append(CodedValues.FUN_INTEGRA_SOAP_OPERACIONAL).append("', '").append(CodedValues.FUN_INTEGRA_SOAP_COMPRA).append("')");
        corpo.append(" ) then 1 else 0 end) as ACESSA_API_1, ");
        corpo.append("max(case when exists (select 1 from PerfilUsuario upe ");
        corpo.append(" inner join upe.usuario usu ");
        corpo.append(" inner join upe.perfil per ");
        corpo.append(" inner join per.funcaoSet fun ");
        corpo.append(" inner join per.perfilCsaSet perEntidade");
        corpo.append(" where perEntidade.csaCodigo = csa.csaCodigo ");
        corpo.append(" and fun.funCodigo in ('").append(CodedValues.FUN_INTEGRA_SOAP_OPERACIONAL).append("', '").append(CodedValues.FUN_INTEGRA_SOAP_COMPRA).append("')");
        corpo.append(") then 1 else 0 end) as ACESSA_API ");

        corpo.append("from Convenio cnv ");
        corpo.append("inner join cnv.consignataria csa ");
        corpo.append("inner join cnv.orgao org  ");
        corpo.append("inner join cnv.servico svc ");
        corpo.append("left outer join svc.naturezaServico nse WITH nse.nseCodigo = '").append(CodedValues.NSE_SALARYPAY).append("' ");

        corpo.append("where 1 = 1 ");

        if (!TextHelper.isNull(csaCodigo)) {
            corpo.append(" and csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpo.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        corpo.append(" group by csa.csaCodigo, csa.csaNome ");
        corpo.append(" order by csa.csaNome ");

        Query<Object[]> query = instanciarQuery(session, corpo.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                "SALARY_PAY",
                "ACESSA_API_1",
                "ACESSA_API"
        };
    }

}
