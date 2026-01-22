package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConvenioRegistroServidorEntidadeQuery</p>
 * <p>Description: Listagem de convênios bloqueados da consignataria ou consignante/suporte.</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConvenioRegistroServidorEntidadeQuery extends HQuery {

    private static final String CSA_CODIGO = "csaCodigo";
    public String csaCodigo;
    public AcessoSistema responsavel;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final String corpo = "select " +
                "svc.svcCodigo, " +
                "svc.svcIdentificador, " +
                "svc.svcDescricao, " +
                "csa.csaCodigo, " +
                "csa.csaIdentificador, " +
                "csa.csaNome, " +
                "csa.csaNomeAbrev, " +
                "cnv.cnvCodigo, " +
                "cnv.cnvCodVerba ";

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from Convenio cnv ");
        corpoBuilder.append(" inner join cnv.consignataria csa ");
        corpoBuilder.append(" inner join cnv.servico svc ");

        corpoBuilder.append(" where exists (select 1 from ParamConvenioRegistroSer param where param.convenio.cnvCodigo = cnv.cnvCodigo ");
        if (!TextHelper.isNull(csaCodigo) && AcessoSistema.ENTIDADE_CSA.equals(responsavel.getTipoEntidade())) {
            corpoBuilder.append(" and param.convenio.csaCodigo ").append(criaClausulaNomeada(CSA_CODIGO, csaCodigo));
            corpoBuilder.append(" and param.pcrVlrCsa is not null ");
        } else if (AcessoSistema.ENTIDADE_CSE.equals(responsavel.getTipoEntidade()) || AcessoSistema.ENTIDADE_SUP.equals(responsavel.getTipoEntidade())) {
            corpoBuilder.append(" and param.pcrVlrCse is not null ");
        }
        corpoBuilder.append(" and param.tpsCodigo = '").append(CodedValues.TPS_NUM_CONTRATOS_POR_CONVENIO).append("' ) ");

        if (!TextHelper.isNull(csaCodigo) && AcessoSistema.ENTIDADE_CSA.equals(responsavel.getTipoEntidade())) {
            corpoBuilder.append(" and csa.csaCodigo ").append(criaClausulaNomeada(CSA_CODIGO, csaCodigo));
        }

        corpoBuilder.append(" order by cnv.cnvCodVerba, svc.svcDescricao, csa.csaNome");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada(CSA_CODIGO, csaCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CNV_CODIGO,
                Columns.CNV_COD_VERBA
        };
    }
}
