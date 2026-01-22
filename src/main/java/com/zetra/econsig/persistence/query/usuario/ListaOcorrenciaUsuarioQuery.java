package com.zetra.econsig.persistence.query.usuario;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaUsuarioQuery</p>
 * <p>Description: Busca as ocorrências de um usuário</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaUsuarioQuery extends HQuery {

    public boolean count = false;
    public String ousUsuCodigo;
    public List<String> tocCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo =  "select ous.ousCodigo, "
                + "toc.tocCodigo, "
                + "toc.tocDescricao, "
                + "ous.usuarioByUsuCodigo.usuCodigo, "
                + "ous.usuarioByOusUsuCodigo.usuCodigo, "
                + "ous.ousData, "
                + "ous.ousObs, "
                + "ous.ousIpAcesso, "
                + "tmo.tmoCodigo, "
                + "tmo.tmoDescricao, "
                + "usu.usuCodigo as USU_CODIGO, "
                + "usu.usuLogin AS USU_LOGIN, "
                + "usu1.usuCodigo AS USU_CODIGO_MOD, " +
                "usuarioCsa.csaCodigo, " +
                "usuarioCse.cseCodigo, " +
                "usuarioCor.corCodigo, " +
                "usuarioOrg.orgCodigo, " +
                "usuarioSer.serCodigo, " +
                "usuarioSup.cseCodigo, " +
                "case when usu1.usuLogin = usu1.usuCodigo and usu1.usuTipoBloq is not null "
                + " then concat(usu1.usuTipoBloq,'(*)') "
                + " else usu1.usuLogin end AS USU_LOGIN_MOD ";
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append(" from OcorrenciaUsuario ous ");
        corpoBuilder.append(" inner join ous.usuarioByUsuCodigo usu ");
        corpoBuilder.append(" inner join ous.usuarioByOusUsuCodigo usu1 ");
        corpoBuilder.append(" inner join ous.tipoOcorrencia toc ");
        corpoBuilder.append(" left outer join ous.tipoMotivoOperacao tmo ");
        corpoBuilder.append("LEFT JOIN usu1.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append("LEFT JOIN usu1.usuarioCseSet usuarioCse ");
        corpoBuilder.append("LEFT JOIN usu1.usuarioCorSet usuarioCor ");
        corpoBuilder.append("LEFT JOIN usu1.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append("LEFT JOIN usu1.usuarioSerSet usuarioSer ");
        corpoBuilder.append("LEFT JOIN usu1.usuarioSupSet usuarioSup ");
        corpoBuilder.append(" where 1=1 ");

        if (!TextHelper.isNull(ousUsuCodigo)) {
            corpoBuilder.append(" and ous.usuarioByUsuCodigo.usuCodigo ").append(criaClausulaNomeada("ousUsuCodigo", ousUsuCodigo));
        }

        if (tocCodigos != null && tocCodigos.size() > 0) {
            corpoBuilder.append(" and ous.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));
        }

        if (!count) {
            corpoBuilder.append(" order by ous.ousData desc");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(ousUsuCodigo)) {
            defineValorClausulaNomeada("ousUsuCodigo", ousUsuCodigo, query);
        }

        if (tocCodigos != null && tocCodigos.size() > 0) {
            defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.OUS_CODIGO,
                Columns.TOC_CODIGO,
                Columns.TOC_DESCRICAO,
                Columns.OUS_USU_CODIGO,
                Columns.OUS_OUS_USU_CODIGO,
                Columns.OUS_DATA,
                Columns.OUS_OBS,
                Columns.OUS_IP_ACESSO,
                Columns.TMO_CODIGO,
                Columns.TMO_DESCRICAO,
                "USU_CODIGO",
                "USU_LOGIN",
                "USU_CODIGO_MOD",
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                "USU_LOGIN_MOD"
        };
    }
}
