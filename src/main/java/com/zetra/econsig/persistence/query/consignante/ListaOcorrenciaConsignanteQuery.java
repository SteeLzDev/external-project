package com.zetra.econsig.persistence.query.consignante;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaConsignanteQuery</p>
 * <p>Description: lista ocorrências da entidade consignante.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaConsignanteQuery extends HQuery {

    public boolean count = false;
    public boolean versao = false;
    public String cseCodigo;
    public String oceCodigo;
    public String tocCodigo;
    public Date oceDataIni;
    public Date oceDataFim;

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";
        if (versao) {
            tocCodigo = CodedValues.TOC_INICIALIZANDO_SISTEMA;
            maxResults = 1;
            firstResult = 0;
        }

        if (!count) {
            corpo =  "select "
                + "toc.tocDescricao, "
                + "oce.consignante.cseCodigo, "
                + "oce.oceCodigo, "
                + "oce.oceObs, "
                + "oce.oceData, "
                + "oce.oceIpAcesso, "
                + "usu.usuCodigo, "
                + "usu.usuLogin, "
                + "usu.usuTipoBloq ";

        } else {
            corpo = "select count(*) as total ";
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("from OcorrenciaConsignante oce ");
        corpoBuilder.append("inner join oce.tipoOcorrencia toc ");
        corpoBuilder.append("inner join oce.usuario usu ");
        corpoBuilder.append("where 1=1 ");

        if (!TextHelper.isNull(oceCodigo)) {
            corpoBuilder.append("and oce.oceCodigo ").append(criaClausulaNomeada("oceCodigo", oceCodigo));
        } else if (!TextHelper.isNull(cseCodigo)) {
            corpoBuilder.append("and oce.consignante.cseCodigo ").append(criaClausulaNomeada("cseCodigo", cseCodigo));
        }

        if (!TextHelper.isNull(tocCodigo)) {
            corpoBuilder.append(" and oce.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigo));
        }

        if (!TextHelper.isNull(oceDataIni) && !TextHelper.isNull(oceDataFim)) {
            corpoBuilder.append(" and oce.oceData BETWEEN :oceDataIni AND :oceDataFim ");
        }

        if (!count) {
            if (versao) {
                corpoBuilder.append(" and oce.oceObs ").append(criaClausulaNomeada("oceObs", CodedValues.LIKE_MULTIPLO + ApplicationResourcesHelper.getMessage("release.tag", null)));
                corpoBuilder.append(" order by oce.oceData asc");
            } else {
                corpoBuilder.append(" order by oce.oceData desc");
            }
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(oceCodigo)) {
            defineValorClausulaNomeada("oceCodigo", oceCodigo, query);
        } else if (!TextHelper.isNull(cseCodigo)) {
            defineValorClausulaNomeada("cseCodigo", cseCodigo, query);
        }

        if (!TextHelper.isNull(tocCodigo)) {
            defineValorClausulaNomeada("tocCodigo", tocCodigo, query);
        }

        if (!TextHelper.isNull(oceDataIni) && !TextHelper.isNull(oceDataFim)) {
            defineValorClausulaNomeada("oceDataIni", oceDataIni, query);
            defineValorClausulaNomeada("oceDataFim", oceDataFim, query);
        }

        if (!count && versao) {
            final String oceObs = CodedValues.LIKE_MULTIPLO + ApplicationResourcesHelper.getMessage("release.tag", null);
            defineValorClausulaNomeada("oceObs", oceObs, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TOC_DESCRICAO,
                Columns.OCE_CSE_CODIGO,
                Columns.OCE_CODIGO,
                Columns.OCE_OBS,
                Columns.OCE_DATA,
                Columns.OCE_IP_ACESSO,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_TIPO_BLOQ
        };
    }

}
