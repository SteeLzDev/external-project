package com.zetra.econsig.persistence.query.coeficiente;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaCoeficienteAtivoQuery</p>
 * <p>Description: Listagem de Coeficientes Ativos.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCoeficienteAtivoQuery extends HQuery {

    public String csaCodigo;
    public String svcCodigo;
    public String orgCodigo;
    public String rseCodigo;
    public short prazo;
    public short dia;
    public boolean validaBloqSerCnvCsa = true;
    private final boolean exibeCETMinMax = ParamSist.paramEquals(CodedValues.TPC_CSA_PODE_CADASTRAR_CET_MININO_E_MAXIMO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        // Se o simulador agrupa os serviços de natureza EMPRESTIMO e a simulação é para um servidor
        final boolean simuladorAgrupadoPorNaturezaServico = ParamSist.paramEquals(CodedValues.TPC_SIMULADOR_AGRUPADO_NATUREZA_SERVICO, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())
                && (rseCodigo != null)
                && (csaCodigo == null);

        /*
         * Se o rseCodigo for passado, então é uma simulação e deve-se fazer o filtro pelo relacionamento de serviço.
         */
        List<String> csaSemContrato = null;
        if (rseCodigo != null) {
            final ListaConsignatariaFiltroSimulacaoQuery query = new ListaConsignatariaFiltroSimulacaoQuery();
            query.rseCodigo = rseCodigo;
            query.svcCodigo = svcCodigo;
            csaSemContrato = query.executarLista();
        }

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select distinct cft.cftCodigo, pzc.consignataria.csaCodigo, cft.cftDia, ");
        corpoBuilder.append("prz.przVlr, cft.cftVlr, cft.cftDataIniVig, cft.cftDataFimVig, ");
        corpoBuilder.append("csa.csaNomeAbrev, csa.csaNome, csa.csaIdentificador, to_string(csa.csaTxtContato), csa.csaCodigo, svc.svcCodigo, svc.svcIdentificador, svc.svcDescricao, ");
        corpoBuilder.append("cast(coalesce(psc.pscVlr, '").append(CodedValues.CSA_NAO_PROMOVIDA).append("') as int), ");
        corpoBuilder.append("cft.cftVlrRef ");
        if(exibeCETMinMax) {
        	corpoBuilder.append(", cft.cftVlrMinimo ");
        }
        corpoBuilder.append(" FROM Servico svc");
        corpoBuilder.append(" INNER JOIN svc.prazoSet prz");
        corpoBuilder.append(" INNER JOIN prz.prazoConsignatariaSet pzc");
        corpoBuilder.append(" INNER JOIN pzc.coeficienteAtivoSet cft");
        corpoBuilder.append(" INNER JOIN pzc.consignataria csa");
        if (orgCodigo != null) {
            corpoBuilder.append(" INNER JOIN csa.convenioSet cnv ");
            corpoBuilder.append(" INNER JOIN cnv.orgao org");
        }

        corpoBuilder.append(" LEFT OUTER JOIN svc.paramSvcConsignatariaSet psc with psc.consignataria.csaCodigo = csa.csaCodigo and psc.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_RELEVANCIA_CSA_RANKING).append("'");

        corpoBuilder.append(" WHERE (svc.svcAtivo = ").append(CodedValues.STS_ATIVO);
        corpoBuilder.append(" OR svc.svcAtivo IS NULL)");
        if (!simuladorAgrupadoPorNaturezaServico) {
            corpoBuilder.append(" AND prz.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
        } else {
            corpoBuilder.append(" AND svc.naturezaServico.nseCodigo = (");
            corpoBuilder.append(" select svcOrigem.naturezaServico.nseCodigo from Servico svcOrigem");
            corpoBuilder.append(" where svcOrigem.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));
            corpoBuilder.append(")");
        }

        if (csaCodigo != null) {
            corpoBuilder.append(" AND pzc.consignataria.csaCodigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        } else {
            if (validaBloqSerCnvCsa) {
                corpoBuilder.append(" AND (csa.csaAtivo = ").append(CodedValues.STS_ATIVO).append(" OR csa.csaAtivo IS NULL)");
            }

            corpoBuilder.append(" AND (prz.przAtivo = ").append(CodedValues.STS_ATIVO);
            corpoBuilder.append(" OR prz.przAtivo IS NULL)");
            corpoBuilder.append(" AND (pzc.przCsaAtivo = ").append(CodedValues.STS_ATIVO);
            corpoBuilder.append(" OR pzc.przCsaAtivo IS NULL)");
        }

        if (orgCodigo != null) {
            corpoBuilder.append(" AND org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
            corpoBuilder.append(" AND cnv.servico.svcCodigo = svc.svcCodigo ");
            corpoBuilder.append(" AND cnv.orgao.orgCodigo = org.orgCodigo ");
            corpoBuilder.append(" AND (org.orgAtivo = ").append(CodedValues.STS_ATIVO);
            corpoBuilder.append(" OR org.orgAtivo IS NULL)");
            corpoBuilder.append(" AND cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        }
        if (prazo > 0) {
            corpoBuilder.append(" AND prz.przVlr ").append(criaClausulaNomeada("przVlr", prazo));
        }
        if (dia > 0) {
            corpoBuilder.append(" AND (cft.cftDia ").append(criaClausulaNomeada("cftDia", dia)).append(" OR cft.cftDia = 0)");
        }
        corpoBuilder.append(" AND cft.cftDataIniVig <= current_date() ");
        corpoBuilder.append(" AND (cft.cftDataFimVig >= current_date() OR cft.cftDataFimVig IS NULL) ");
        corpoBuilder.append(" AND NOT (cft.cftDataIniVig = to_datetime(format_datetime(current_date, '00:00:00')) ");
        corpoBuilder.append(" AND COALESCE(cft.cftDataFimVig, to_datetime(format_datetime(to_date_not_trunc('9999-12-31'), '23:59:59'))) = to_datetime(format_datetime(current_date, '23:59:59'))) ");

        if ((csaSemContrato != null) && !csaSemContrato.isEmpty()) {
            corpoBuilder.append(" AND pzc.consignataria.csaCodigo not in (:csaSemContrato)");
        }

        if (prazo > 0) {
            corpoBuilder.append(" ORDER BY cast(coalesce(psc.pscVlr, '").append(CodedValues.CSA_NAO_PROMOVIDA).append("') as int), cft.cftDia, prz.przVlr");
        } else {
            corpoBuilder.append(" ORDER BY cast(coalesce(psc.pscVlr, '").append(CodedValues.CSA_NAO_PROMOVIDA).append("') as int), prz.przVlr");
        }

        // Define os valores para os parâmetros nomeados
        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        if (csaCodigo != null) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }
        if (orgCodigo != null) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (prazo > 0) {
            defineValorClausulaNomeada("przVlr", prazo, query);
        }
        if (dia > 0) {
            defineValorClausulaNomeada("cftDia", dia, query);
        }
        if ((csaSemContrato != null) && !csaSemContrato.isEmpty()) {
            defineValorClausulaNomeada("csaSemContrato", csaSemContrato, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	final List<String> fields = new ArrayList<>(Arrays.asList(
            Columns.CFT_CODIGO,
            Columns.CFT_PRZ_CSA_CODIGO,
            Columns.CFT_DIA,
            Columns.PRZ_VLR,
            Columns.CFT_VLR,
            Columns.CFT_DATA_INI_VIG,
            Columns.CFT_DATA_FIM_VIG,
            Columns.CSA_NOME_ABREV,
            Columns.CSA_NOME,
            Columns.CSA_IDENTIFICADOR,
            Columns.CSA_TXT_CONTATO,
            Columns.CSA_CODIGO,
            Columns.SVC_CODIGO,
            Columns.SVC_IDENTIFICADOR,
            Columns.SVC_DESCRICAO,
            "RELEVANCIA",
            Columns.CFT_VLR_REF
        ));
        if (exibeCETMinMax) {
            fields.add(Columns.CFT_VLR_MINIMO);
        }
        return fields.toArray(new String[0]);
    }

}
