package com.zetra.econsig.persistence.query.convenio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParamConvenioExpiradoQuery</p>
 * <p>Description: Listagem de convênios associados a consignatárias e serviços
 * que possuem parâmetro de data de expiração cadastrado e menor que a data
 * informada.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParamConvenioExpiradoQuery extends HQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ");
        corpoBuilder.append("csa.csaCodigo, ");
        corpoBuilder.append("csa.csaIdentificador, ");
        corpoBuilder.append("csa.csaNome, ");
        corpoBuilder.append("csa.csaEmail, ");
        corpoBuilder.append("svc.svcCodigo, ");
        corpoBuilder.append("svc.svcIdentificador, ");
        corpoBuilder.append("svc.svcDescricao, ");
        corpoBuilder.append("psc.pscVlr ");

        corpoBuilder.append("from ParamSvcConsignataria psc ");
        corpoBuilder.append("inner join psc.servico svc ");
        corpoBuilder.append("inner join psc.consignataria csa ");

        // Que possuem o parâmetro de data de expiração cadastrado com valor não vazio
        corpoBuilder.append(" where psc.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_DATA_EXPIRACAO_CONVENIO).append("'");
        corpoBuilder.append("   and nullif(trim(psc.pscVlr), '') is not null ");

        // Que possuem algum convênio ativo
        corpoBuilder.append(" and exists (");
        corpoBuilder.append("   select 1 from Convenio cnv");
        corpoBuilder.append("   where cnv.statusConvenio.scvCodigo = '").append(CodedValues.SCV_ATIVO).append("'");
        corpoBuilder.append("     and cnv.consignataria.csaCodigo = csa.csaCodigo");
        corpoBuilder.append("     and cnv.servico.svcCodigo = svc.svcCodigo");
        corpoBuilder.append(")");

        // Que não possui parâmetro de consignatária impedindo o bloqueio
        corpoBuilder.append(" and not exists (");
        corpoBuilder.append("   select 1 from csa.paramConsignatariaSet pcs");
        corpoBuilder.append("   where pcs.tipoParamConsignataria.tpaCodigo = '").append(CodedValues.TPA_NAO_BLOQUEIA_POR_DATA_EXPIRACAO).append("'");
        corpoBuilder.append("     and pcs.pcsVlr = '").append(CodedValues.TPA_SIM).append("'");
        corpoBuilder.append(")");

        corpoBuilder.append(" order by csa.csaNome");

        return instanciarQuery(session, corpoBuilder.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_IDENTIFICADOR,
                Columns.CSA_NOME,
                Columns.CSA_EMAIL,
                Columns.SVC_CODIGO,
                Columns.SVC_IDENTIFICADOR,
                Columns.SVC_DESCRICAO,
                Columns.PSC_VLR
        };
    }
}
