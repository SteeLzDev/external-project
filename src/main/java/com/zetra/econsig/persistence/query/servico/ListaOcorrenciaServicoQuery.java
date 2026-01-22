package com.zetra.econsig.persistence.query.servico;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaServicoQuery</p>
 * <p>Description: lista ocorrências da entidade serviço.</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaServicoQuery extends HQuery {

    public boolean count = false;
    public String svcCodigo;
    public List<String> tocCodigos;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo =  "select "
                + "toc.tocDescricao, "
                + "ose.servico.svcCodigo, "
                + "ose.oseCodigo, "
                + "ose.oseObs, "
                + "ose.oseData, "
                + "ose.oseIpAcesso, "
                + "usu.usuCodigo, "
                + "usu.usuLogin, "
                + "usu.usuTipoBloq, "
                + "tmo.tmoDescricao ";

        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("from OcorrenciaServico ose ");
        corpoBuilder.append("inner join ose.tipoOcorrencia toc ");
        corpoBuilder.append("inner join ose.usuario usu ");
        corpoBuilder.append("left join ose.tipoMotivoOperacao tmo ");
        corpoBuilder.append("where ose.servico.svcCodigo ").append(criaClausulaNomeada("svcCodigo", svcCodigo));

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            corpoBuilder.append(" and ose.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));
        }

        if (!count) {
            corpoBuilder.append(" order by ose.oseData desc");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);

        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TOC_DESCRICAO,
                Columns.OSE_SVC_CODIGO,
                Columns.OSE_CODIGO,
                Columns.OSE_OBS,
                Columns.OSE_DATA,
                Columns.OSE_IP_ACESSO,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_TIPO_BLOQ,
                Columns.TMO_DESCRICAO
        };
    }
}
