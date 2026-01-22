package com.zetra.econsig.persistence.query.relatorio;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;
import com.zetra.econsig.persistence.query.ReportHNativeQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: RelatorioGerencialGeralListaCsaSituacaoObsQuery</p>
 * <p>Description: Retorna as consignatarias e suas situações.</p>
 * <p>Copyright: Copyright (c) 2024</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class RelatorioGerencialGeralListaCsaSituacaoObsQuery extends ReportHNativeQuery {

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpo = new StringBuilder();
        corpo.append("SELECT coalesce(csa.csa_nome_abrev, csa.csa_nome) AS CONSIGNATARIA, ");
        corpo.append("CASE WHEN csa.csa_ativo = ").append(CodedValues.STS_ATIVO).append(" ");
        corpo.append("THEN '").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.filtro.desbloqueado", (AcessoSistema) null)).append("' ");
        corpo.append("ELSE '").append(ApplicationResourcesHelper.getMessage("rotulo.consignataria.filtro.bloqueado", (AcessoSistema) null)).append("' END AS STATUS, ");
        corpo.append("CASE WHEN csa.csa_ativo != ").append(CodedValues.STS_ATIVO).append(" ");
        corpo.append("THEN ultima_ocorrencia.occ_obs ");
        corpo.append("ELSE '' END AS OBSERVACAO ");
        corpo.append("FROM tb_consignataria csa ");
        corpo.append("LEFT JOIN ( ");
        corpo.append("SELECT occ.csa_codigo, to_string(occ.occ_obs) as occ_obs ");
        corpo.append("FROM tb_ocorrencia_consignataria occ ");
        corpo.append("WHERE occ.toc_codigo= '").append(CodedValues.TOC_BLOQUEIA_CONSIGNATARIA).append("' ");
        corpo.append("AND NOT EXISTS ( ");
        corpo.append("SELECT 1 FROM tb_ocorrencia_consignataria occ1 ");
        corpo.append("WHERE occ.csa_codigo = occ1.csa_codigo ");
        corpo.append("AND occ1.toc_codigo='").append(CodedValues.TOC_BLOQUEIA_CONSIGNATARIA).append("' ");
        corpo.append("AND occ1.occ_data > occ.occ_data ");
        corpo.append(") ");
        corpo.append("GROUP BY occ.csa_codigo, to_string(occ.occ_obs) ");
        corpo.append(") ultima_ocorrencia ON (ultima_ocorrencia.csa_codigo = csa.csa_codigo) ");
        corpo.append("ORDER BY STATUS, CONSIGNATARIA ");

        return instanciarQuery(session, corpo.toString());
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                "CONSIGNATARIA",
                "STATUS",
                "OBSERVACAO"
        };
    }
}
