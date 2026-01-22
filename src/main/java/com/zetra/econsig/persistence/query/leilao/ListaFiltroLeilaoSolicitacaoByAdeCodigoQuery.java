package com.zetra.econsig.persistence.query.leilao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaSolicitacaoLeilaoSemPropostaQuery</p>
 * <p>Description: Lista solicitação de leilões que só possuem a proposta inicial.</p>
 * <p>Copyright: Copyright (c) 2002-2017</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaFiltroLeilaoSolicitacaoByAdeCodigoQuery extends HNativeQuery {

    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append(" SELECT fls.fls_descricao, fls.fls_email_notificacao ");

        corpoBuilder.append(" FROM tb_filtro_leilao_solicitacao fls ");
        corpoBuilder.append(" INNER JOIN tb_usuario usu ON usu.usu_codigo = fls.usu_codigo ");
        corpoBuilder.append(" INNER JOIN tb_usuario_csa usuCsa ON usuCsa.usu_codigo = usu.usu_codigo ");
        corpoBuilder.append(" INNER JOIN tb_consignataria csa ON csa.csa_codigo = usuCsa.csa_codigo ");
        corpoBuilder.append(" WHERE fls.fls_codigo IN ( ");
        corpoBuilder.append(" SELECT fls.fls_codigo ");
        corpoBuilder.append(" FROM tb_aut_desconto ade ");
        corpoBuilder.append(" INNER JOIN tb_solicitacao_autorizacao soa ON soa.ade_codigo = ade.ade_codigo ");
        corpoBuilder.append(" INNER JOIN tb_proposta_leilao_solicitacao pls ON pls.ade_codigo = ade.ade_codigo ");
        corpoBuilder.append(" INNER JOIN tb_registro_servidor rse ON rse.rse_codigo = ade.rse_codigo ");
        corpoBuilder.append(" INNER JOIN tb_servidor ser ON ser.ser_codigo = rse.ser_codigo ");
        corpoBuilder.append(" WHERE ");
        corpoBuilder.append(" ade.ade_codigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo));
        corpoBuilder.append(" AND ((fls.fls_tipo_pesquisa = '1' AND pls.csa_codigo = csa.csa_codigo) OR (fls.fls_tipo_pesquisa = '0' AND pls.csa_codigo <> csa.csa_codigo)) ");
        corpoBuilder.append(" AND ((rse.pos_codigo = fls.pos_codigo) OR fls.pos_codigo IS NULL) ");
        corpoBuilder.append(" AND ((ade.cid_codigo = fls.cid_codigo) OR fls.cid_codigo IS NULL) ");
        corpoBuilder.append(" AND ((ade.ade_data BETWEEN concat(fls.fls_data_abertura_ini, ' 00:00:00') AND concat(fls.fls_data_abertura_fim, ' 23:59:59')) OR fls.fls_data_abertura_ini IS NULL AND fls.fls_data_abertura_fim IS NULL) ");
        corpoBuilder.append(" AND ((soa.soa_data_validade <= add_hour(current_timestamp, fls.fls_horas_encerramento)) OR fls.fls_horas_encerramento IS NULL) ");
        corpoBuilder.append(" AND ((rse.rse_pontuacao >= fls.fls_pontuacao_min) OR fls.fls_pontuacao_min IS NULL) ");
        corpoBuilder.append(" AND ((rse.rse_matricula LIKE concat(concat('%',fls.fls_matricula),'%')) OR fls.fls_matricula IS NULL) ");
        corpoBuilder.append(" AND ((ser.ser_cpf = fls.fls_cpf) OR nullif(trim(fls.fls_cpf), '') IS NULL) ");

        // margem livre
        corpoBuilder.append(" AND (fls.fls_margem_livre_max IS NULL OR ");
        corpoBuilder.append(" (CASE ");
        corpoBuilder.append(" WHEN (ade.ade_inc_margem = '"+CodedValues.INCIDE_MARGEM_SIM+"') THEN ");
        corpoBuilder.append("   (CASE ");
        corpoBuilder.append("       WHEN (fls.fls_margem_livre_max = 10) THEN (((rse.rse_margem_rest / rse.rse_margem) * 100.00) <= fls.fls_margem_livre_max) ");
        corpoBuilder.append("       WHEN (fls.fls_margem_livre_max = 100) THEN (((rse.rse_margem_rest / rse.rse_margem) * 100.00) > fls.fls_margem_livre_max) ");
        corpoBuilder.append("       ELSE (((rse.rse_margem_rest / rse.rse_margem) * 100.00) > fls.fls_margem_livre_max-10 AND ((rse.rse_margem_rest / rse.rse_margem) * 100.00) <= fls.fls_margem_livre_max) ");
        corpoBuilder.append("   END) ");
        corpoBuilder.append(" WHEN (ade.ade_inc_margem = '"+CodedValues.INCIDE_MARGEM_SIM_2+"') THEN ");
        corpoBuilder.append("   (CASE ");
        corpoBuilder.append("       WHEN (fls.fls_margem_livre_max = 10) THEN (((rse.rse_margem_rest_2 / rse.rse_margem_2) * 100.00) <= fls.fls_margem_livre_max) ");
        corpoBuilder.append("       WHEN (fls.fls_margem_livre_max = 100) THEN (((rse.rse_margem_rest_2 / rse.rse_margem_2) * 100.00) > fls.fls_margem_livre_max) ");
        corpoBuilder.append("       ELSE (((rse.rse_margem_rest_2 / rse.rse_margem_2) * 100.00) > fls.fls_margem_livre_max-10 AND ((rse.rse_margem_rest_2 / rse.rse_margem_2) * 100.00) <= fls.fls_margem_livre_max) ");
        corpoBuilder.append("   END) ");
        corpoBuilder.append(" WHEN (ade.ade_inc_margem = '"+CodedValues.INCIDE_MARGEM_SIM_3+"') THEN ");
        corpoBuilder.append("   (CASE ");
        corpoBuilder.append("       WHEN (fls.fls_margem_livre_max = 10) THEN (((rse.rse_margem_rest_3 / rse.rse_margem_3) * 100.00) <= fls.fls_margem_livre_max) ");
        corpoBuilder.append("       WHEN (fls.fls_margem_livre_max = 100) THEN (((rse.rse_margem_rest_3 / rse.rse_margem_3) * 100.00) > fls.fls_margem_livre_max) ");
        corpoBuilder.append("       ELSE (((rse.rse_margem_rest_3 / rse.rse_margem_3) * 100.00) > fls.fls_margem_livre_max-10 AND ((rse.rse_margem_rest_3 / rse.rse_margem_3) * 100.00) <= fls.fls_margem_livre_max) ");
        corpoBuilder.append("   END) ");
        corpoBuilder.append(" ELSE ");
        corpoBuilder.append("   EXISTS (SELECT 1 ");
        corpoBuilder.append("           FROM tb_margem_registro_servidor mrs ");
        corpoBuilder.append("           WHERE mrs.rse_codigo = rse.rse_codigo ");
        corpoBuilder.append("           AND mrs.mar_codigo = ade.ade_inc_margem ");
        corpoBuilder.append("           AND CASE ");
        corpoBuilder.append("               WHEN fls.fls_margem_livre_max = 10 THEN ((mrs.mrs_margem_rest / mrs.mrs_margem) * 100.00) <= fls.fls_margem_livre_max ");
        corpoBuilder.append("               WHEN fls.fls_margem_livre_max = 100 THEN ((mrs.mrs_margem_rest / mrs.mrs_margem) * 100.00) > fls.fls_margem_livre_max-10 ");
        corpoBuilder.append("               ELSE ((mrs.mrs_margem_rest / mrs.mrs_margem) * 100.00) > fls.fls_margem_livre_max-10 AND ((mrs.mrs_margem_rest / mrs.mrs_margem) * 100.00) <= fls.fls_margem_livre_max ");
        corpoBuilder.append("               END ");
        corpoBuilder.append("           ) = 1 ");
        corpoBuilder.append(" END) ");
        corpoBuilder.append(" ) ");

        // fecha subquery
        corpoBuilder.append(" ) ");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.FLS_DESCRICAO,
                Columns.FLS_EMAIL_NOTIFICACAO
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
