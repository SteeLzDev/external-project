package com.zetra.econsig.persistence.query.prazo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaLimiteTaxaPorPrazoCsaQuery</p>
 * <p>Description: Listagem dos limites de taxa por prazo cadastrado
 * para a consignatária, em um dado convênio.</p>
 * <p>Copyright: Copyright (c) 2002-2014</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaLimiteTaxaPorPrazoCsaQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;
    public String orgCodigo;
    public short prazo;
    private boolean exibeCETMinMax = ParamSist.paramEquals(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select csa.csaCodigo, csa.csaIdentificador, csa.csaNome, csa.csaNomeAbrev, csa.csaTxtContato, ");
        corpoBuilder.append("svc.svcCodigo, svc.svcIdentificador, svc.svcDescricao, ");
        corpoBuilder.append("prz.przVlr, max(ltj.ltjJurosMax), ");
        corpoBuilder.append("max(ltj.ltjPrazoRef), max(ltj.ltjJurosMax) ");
        if(exibeCETMinMax) {
        	corpoBuilder.append(", min(ltj.ltjJurosMax) ");        	
        }
        corpoBuilder.append(" FROM Convenio cnv");
        corpoBuilder.append(" INNER JOIN cnv.servico svc");
        corpoBuilder.append(" INNER JOIN svc.prazoSet prz");
        corpoBuilder.append(" INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append(" INNER JOIN pzc.consignataria csa");
        corpoBuilder.append(" INNER JOIN svc.limiteTaxaJurosSet ltj");

        corpoBuilder.append(" WHERE svc.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        corpoBuilder.append(" AND (svc.svcAtivo = ").append(CodedValues.STS_ATIVO).append(" OR svc.svcAtivo IS NULL)");
        corpoBuilder.append(" AND (csa.csaAtivo = ").append(CodedValues.STS_ATIVO).append(" OR csa.csaAtivo IS NULL)");
        corpoBuilder.append(" AND (prz.przAtivo = ").append(CodedValues.STS_ATIVO).append(" OR prz.przAtivo IS NULL)");
        corpoBuilder.append(" AND (pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO).append(" OR pzc.przCsaAtivo IS NULL)");

        corpoBuilder.append(" AND prz.przVlr <= ltj.ltjPrazoRef");
        corpoBuilder.append(" AND NOT EXISTS (");
        corpoBuilder.append("    select 1 from LimiteTaxaJuros ltj2 ");
        corpoBuilder.append("    where ltj2.servico.svcCodigo = svc.svcCodigo ");
        corpoBuilder.append("      and prz.przVlr <= ltj2.ltjPrazoRef ");
        corpoBuilder.append("      and ltj2.ltjPrazoRef < ltj.ltjPrazoRef");
        corpoBuilder.append(" )");

        if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append(" AND csa.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        }

        if (!TextHelper.isNull(orgCodigo)) {
            corpoBuilder.append(" AND cnv.orgao.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if (prazo > 0) {
            corpoBuilder.append(" AND prz.przVlr ").append(criaClausulaNomeada("przVlr", prazo));
        }

        corpoBuilder.append(" GROUP BY csa.csaCodigo, csa.csaIdentificador, csa.csaNome, csa.csaNomeAbrev, svc.svcCodigo, svc.svcDescricao, prz.przVlr ");
        corpoBuilder.append(" ORDER BY prz.przVlr");

        // Define os valores para os parâmetros nomeados
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (prazo > 0) {
            defineValorClausulaNomeada("przVlr", prazo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	List<String> fields = new ArrayList<>(Arrays.asList(
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_TXT_CONTATO,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.PRZ_VLR,
                Columns.CFT_VLR, // Retorna como CFT_VLR por compatibilidade com o método de simulação
                Columns.LTJ_PRAZO_REF,
                Columns.LTJ_JUROS_MAX
        ));
        if (exibeCETMinMax) {
            fields.add(Columns.CFT_VLR_MINIMO);
        }
        return fields.toArray(new String[0]);
    }
}
