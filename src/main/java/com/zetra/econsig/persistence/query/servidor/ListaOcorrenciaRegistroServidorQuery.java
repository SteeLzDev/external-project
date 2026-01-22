package com.zetra.econsig.persistence.query.servidor;

import org.hibernate.query.Query;

import java.util.List;

import org.hibernate.Session;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaRegistroServidorQuery</p>
 * <p>Description: Listagem de Ocorrências de Servidores</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaRegistroServidorQuery extends HQuery {

    public boolean count = false;
	public String rseCodigo;
	public String tocCodigo;
	public List<String> tocCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo =  "select "
                + "toc.tocDescricao, "
                + "ors.registroServidor.rseCodigo, "
                + "ors.orsCodigo, "
                + "ors.orsObs, "
                + "ors.orsData, "
                + "ors.orsIpAcesso, "
                + "usu.usuCodigo, "
                + "usu.usuLogin, "
                + "usuarioCsa.csaCodigo, "
                + "usuarioCse.cseCodigo, "
                + "usuarioCor.corCodigo, "
                + "usuarioOrg.orgCodigo, "
                + "usuarioSer.serCodigo, "
                + "usuarioSup.cseCodigo, "
                + "usu.usuTipoBloq " ;
        } else {
            corpo = "select count(*) as total ";
        }

        StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("from OcorrenciaRegistroSer ors ");
        corpoBuilder.append("inner join ors.tipoOcorrencia toc ");
        corpoBuilder.append("inner join ors.usuario usu ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCsaSet usuarioCsa ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCseSet usuarioCse ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioCorSet usuarioCor ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioOrgSet usuarioOrg ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSerSet usuarioSer ");
        corpoBuilder.append(" LEFT JOIN usu.usuarioSupSet usuarioSup ");
        corpoBuilder.append(" LEFT JOIN ors.tipoMotivoOperacao tmo ");
        corpoBuilder.append("where 1=1 ");

        if (!TextHelper.isNull(rseCodigo)) {
            corpoBuilder.append("and ors.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        }

        if (!TextHelper.isNull(tocCodigo)) {
            corpoBuilder.append("and ors.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigo", tocCodigo));
        }
        
        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            corpoBuilder.append("and ors.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));
        }

        if (!count) {
            corpoBuilder.append(" order by ors.orsData desc");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(rseCodigo)) {
            defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        }

        if (!TextHelper.isNull(tocCodigo)) {
            defineValorClausulaNomeada("tocCodigo", tocCodigo, query);
        }
        
        if (tocCodigos != null && !tocCodigos.isEmpty()) {
        	defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
    	return new String[] {
    			Columns.TOC_DESCRICAO,
                Columns.ORS_RSE_CODIGO,
                Columns.ORS_CODIGO,
                Columns.ORS_OBS,
                Columns.ORS_DATA,
                Columns.ORS_IP_ACESSO,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.USU_TIPO_BLOQ
    	};
    }
}
