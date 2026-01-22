package com.zetra.econsig.persistence.query.beneficios;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.NaturezaConsignatariaEnum;

/**
 * <p>Title: ListaCalculoBeneficioQuery</p>
 * <p>Description: Listagem de cálculo de beneficios</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaCalculoBeneficioQuery extends HQuery {

    public String ncaCodigo = NaturezaConsignatariaEnum.OPERADORA_BENEFICIOS.getCodigo();
    public String benCodigo = null;
    public String orgCodigo = null;
    public String tibCodigo = null;
    public String grpCodigo = null;
    public String mdeCodigo = null;
    public String statusRegra = null;
    public Date data = null;
    public Boolean count = false;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpo = new StringBuilder();

        if (!count) {
            corpo.append("SELECT clb.clbVigenciaIni as clb_vigencia_ini, " +
                    "clb.clbVigenciaFim as clb_vigencia_fim, " +
                    "clb.clbCodigo as clb_codigo, " +
                    "org.orgNome as org_nome, " +
                    "ben.benDescricao as ben_descricao, " +
                    "tib.tibDescricao as tib_descricao, " +
                    "grp.grpDescricao as grp_descricao, " +
                    "mde.mdeDescricao as mde_descricao, " +
                    "clb.clbFaixaSalarialIni as clb_faixa_salarial_ini, " +
                    "clb.clbFaixaSalarialFim as clb_faixa_salarial_fim, " +
                    "clb.clbFaixaEtariaIni as clb_faixa_etaria_ini, " +
                    "clb.clbFaixaEtariaFim as clb_faixa_etaria_fim, " +
                    "clb.clbValorMensalidade as clb_valor_mensalidade, " +
                    "clb.clbValorSubsidio as clb_valor_subsidio ");
        } else {
            corpo.append("SELECT count(*) as total ");
        }
        corpo.append("from CalculoBeneficio clb " +
            		"inner join clb.beneficio ben " +
            		"left join clb.tipoBeneficiario tib " +
            		"inner join ben.consignataria csa " +
                    "left join clb.orgao org " +
                    "left join clb.grauParentesco grp " +
                    "left join clb.motivoDependencia mde ");

        StringBuilder corpoBuilder = new StringBuilder(corpo);

        corpoBuilder.append(" where 1 = 1 ");

        if (benCodigo != null && !benCodigo.isEmpty()) {
            corpoBuilder.append("and ben.benCodigo = :benCodigo ");
        }

        if (orgCodigo != null && !orgCodigo.isEmpty()) {
            corpoBuilder.append("and org.orgCodigo = :orgCodigo ");
        }

        if (tibCodigo != null && !tibCodigo.isEmpty()) {
            corpoBuilder.append("and tib.tibCodigo = :tibCodigo ");
        }

        if (grpCodigo != null && !grpCodigo.isEmpty()) {
            corpoBuilder.append("and grp.grpCodigo = :grpCodigo ");
        }

        if (mdeCodigo != null && !mdeCodigo.isEmpty()) {
            corpoBuilder.append("and mde.mdeCodigo = :mdeCodigo ");
        }

        if (statusRegra != null) {
            switch (statusRegra) {

                case "1":

                  corpoBuilder.append("and clb.clbVigenciaIni is null ");
                  corpoBuilder.append("and clb.clbVigenciaFim is null ");

                break;

                case "2":

                    corpoBuilder.append("and clb.clbVigenciaIni is not null ");
                    corpoBuilder.append("and clb.clbVigenciaFim is null ");

                break;

                case "3":

                    corpoBuilder.append("and clb.clbVigenciaIni is not null ");
                    corpoBuilder.append("and clb.clbVigenciaFim is not null ");

                break;

            }
        }

        if (data != null) {
            corpoBuilder.append("and :data between to_date(clb.clbVigenciaIni) and to_date(clb.clbVigenciaFim) ");
        }

        corpoBuilder.append(" ORDER BY org.orgNome, tib.tibCodigo, ben.benDescricao, clb.clbFaixaSalarialIni, clb.clbFaixaEtariaIni ");
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        if (benCodigo != null && !benCodigo.isEmpty()) {
            defineValorClausulaNomeada("benCodigo", benCodigo, query);
        }

        if (orgCodigo != null && !orgCodigo.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if (tibCodigo != null && !tibCodigo.isEmpty()) {
            defineValorClausulaNomeada("tibCodigo", tibCodigo, query);
        }

        if (grpCodigo != null && !grpCodigo.isEmpty()) {
            defineValorClausulaNomeada("grpCodigo", grpCodigo, query);
        }

        if (mdeCodigo != null && !mdeCodigo.isEmpty()) {
            defineValorClausulaNomeada("mdeCodigo", mdeCodigo, query);
        }

        if (data != null) {
            defineValorClausulaNomeada("data", data, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CLB_VIGENCIA_INI,
                Columns.CLB_VIGENCIA_FIM,
                Columns.CLB_CODIGO,
                Columns.ORG_NOME,
                Columns.BEN_DESCRICAO,
                Columns.TIB_DESCRICAO,
                Columns.GRP_DESCRICAO,
                Columns.MDE_DESCRICAO,
                Columns.CLB_FAIXA_SALARIAL_INI,
                Columns.CLB_FAIXA_SALARIAL_FIM,
                Columns.CLB_FAIXA_ETARIA_INI,
                Columns.CLB_FAIXA_ETARIA_FIM,
                Columns.CLB_VALOR_MENSALIDADE,
                Columns.CLB_VALOR_SUBSIDIO        };
    }
}
