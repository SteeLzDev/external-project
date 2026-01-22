package com.zetra.econsig.persistence.query.consignataria;

import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaOcorrenciaConsignatariaQuery</p>
 * <p>Description: Listagem de Ocorrências de Consignatária</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaOcorrenciaConsignatariaQuery extends HNativeQuery {

    public boolean count = false;
    public String csaCodigo;
    public String occCodigo;
    public String tocCodigo;
    public List<String> tocCodigos;

    @Override
    public void setCriterios(TransferObject criterio) {
        occCodigo = (String) criterio.getAttribute(Columns.OCC_CODIGO);
        csaCodigo = (String) criterio.getAttribute(Columns.CSA_CODIGO);
        tocCodigo = (String) criterio.getAttribute(Columns.TOC_CODIGO);
        tocCodigos = (List<String>) criterio.getAttribute("tocCodigos");
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        String corpo = "";

        if (!count) {
            corpo =  "select "
                    + "toc.toc_descricao, "
                    + "occ.csa_codigo, "
                    + "occ.occ_codigo, "
                    + "occ.occ_obs, "
                    + "occ.occ_data, "
                    + "occ.occ_ip_acesso, "
                    + "usu.usu_codigo, "
                    + "usu.usu_login, "
                    + "usu.usu_tipo_bloq, "+
                    "uca.csa_codigo as usu_csaCodigo, " +
                    "uce.cse_codigo, " +
                    "uco.cor_codigo, " +
                    "uor.org_codigo, " +
                    "usr.ser_codigo, " +
                    "usp.cse_codigo as sup_cseCodigo, "
                    + "tpe.tpe_descricao ";
        } else {
            corpo = "select count(*) as total ";
        }

        final StringBuilder corpoBuilder = new StringBuilder(corpo);
        corpoBuilder.append("from tb_ocorrencia_consignataria occ ");
        corpoBuilder.append("inner join tb_tipo_ocorrencia toc ON toc.toc_codigo = occ.toc_codigo ");
        corpoBuilder.append("inner join tb_usuario usu ON usu.usu_codigo = occ.usu_codigo ");
        corpoBuilder.append("inner join tb_consignataria csa on csa.csa_codigo = occ.csa_codigo ");
        corpoBuilder.append("left outer join tb_tipo_penalidade tpe ON tpe.tpe_codigo = occ.tpe_codigo ");
        corpoBuilder.append("LEFT JOIN tb_usuario_csa uca ON usu.usu_codigo = uca.usu_codigo ");
        corpoBuilder.append("LEFT JOIN tb_usuario_cse uce ON usu.usu_codigo = uce.usu_codigo ");
        corpoBuilder.append("LEFT JOIN tb_usuario_cor uco ON usu.usu_codigo = uco.usu_codigo ");
        corpoBuilder.append("LEFT JOIN tb_usuario_org uor ON usu.usu_codigo = uor.usu_codigo ");
        corpoBuilder.append("LEFT JOIN tb_usuario_ser usr ON usu.usu_codigo = usr.usu_codigo ");
        corpoBuilder.append("LEFT JOIN tb_usuario_sup usp ON usu.usu_codigo = usp.usu_codigo ");

        if (!TextHelper.isNull(occCodigo)) {
            corpoBuilder.append("where occ.occ_codigo ").append(criaClausulaNomeada("occCodigo", occCodigo));
        } else if (!TextHelper.isNull(csaCodigo)) {
            corpoBuilder.append("where csa.csa_codigo ").append(criaClausulaNomeada("csaCodigo", csaCodigo));
        } else {
            throw new HQueryException("mensagem.erro.informe.consulta.csa.codigo.occ.codigo", (AcessoSistema) null);
        }

        if (!TextHelper.isNull(tocCodigo)) {
            corpoBuilder.append(" and occ.toc_codigo ").append(criaClausulaNomeada("tocCodigo", tocCodigo));
        } else if ((tocCodigos != null) && !tocCodigos.isEmpty()) {
            corpoBuilder.append(" and occ.toc_codigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));
        }

        if (!count) {
            corpoBuilder.append(" order by occ.occ_data desc");
        }

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (!TextHelper.isNull(occCodigo)) {
            defineValorClausulaNomeada("occCodigo", occCodigo, query);
        } else if (!TextHelper.isNull(csaCodigo)) {
            defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        }

        if (!TextHelper.isNull(tocCodigo)) {
            defineValorClausulaNomeada("tocCodigo", tocCodigo, query);
        } else if ((tocCodigos != null) && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigos", tocCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.TOC_DESCRICAO,
                Columns.OCC_CSA_CODIGO,
                Columns.OCC_CODIGO,
                Columns.OCC_OBS,
                Columns.OCC_DATA,
                Columns.OCC_IP_ACESSO,
                Columns.USU_CODIGO,
                Columns.USU_LOGIN,
                Columns.USU_TIPO_BLOQ,
                Columns.UCA_CSA_CODIGO,
                Columns.UCE_CSE_CODIGO,
                Columns.UCO_COR_CODIGO,
                Columns.UOR_ORG_CODIGO,
                Columns.USE_SER_CODIGO,
                Columns.USP_CSE_CODIGO,
                Columns.TPE_DESCRICAO
        };
    }
}
