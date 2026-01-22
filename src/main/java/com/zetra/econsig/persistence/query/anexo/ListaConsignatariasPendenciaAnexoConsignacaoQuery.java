package com.zetra.econsig.persistence.query.anexo;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignatariasPendenciaAnexoConsignacaoQuery</p>
 * <p>Description: Listagem de consignatárias com pendência de informação de anexos mínimos na consignação.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignatariasPendenciaAnexoConsignacaoQuery extends HNativeQuery {
    private final AcessoSistema responsavel;
    public boolean count;

    public ListaConsignatariasPendenciaAnexoConsignacaoQuery(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        List<String> sadCodigos = CodedValues.SAD_CODIGOS_ATIVOS;

        String campos = "csa.csa_codigo, "
                + "csa.csa_nome, "
                + "csa.csa_nome_abrev, "
                + "csa.csa_identificador"
                ;

        StringBuilder corpoBuilder = new StringBuilder();

        if (count) {
            corpoBuilder.append("select count(distinct csa.csa_codigo) ");
        } else {
            corpoBuilder.append("select ").append(campos).append(", count(*) ");
        }

        corpoBuilder.append("from tb_aut_desconto ade ");
        corpoBuilder.append("inner join tb_verba_convenio vco on (ade.vco_codigo = vco.vco_codigo) ");
        corpoBuilder.append("inner join tb_convenio cnv on (vco.cnv_codigo = cnv.cnv_codigo) ");
        corpoBuilder.append("inner join tb_consignataria csa on (cnv.csa_codigo = csa.csa_codigo) ");
        corpoBuilder.append("inner join tb_orgao org on (cnv.org_codigo = org.org_codigo) ");
        corpoBuilder.append("inner join tb_estabelecimento est on (org.est_codigo = est.est_codigo) ");
        corpoBuilder.append("left outer join tb_param_svc_consignante pse on (cnv.svc_codigo = pse.svc_codigo and pse.tps_codigo = '").append(CodedValues.TPS_QTD_ANEXOS_MIN_ADE_FEITA_POR_CSA_COR).append("') ");
        corpoBuilder.append("where ade.sad_codigo ").append(criaClausulaNomeada("sadCodigos", sadCodigos));

        String codigoEntidade = responsavel.getCodigoEntidade();
        if (!responsavel.isCseSup()) {
            if (responsavel.isOrg()) {
                if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_ESTABELECIMENTO)) {
                    codigoEntidade = responsavel.getEstCodigo();
                    corpoBuilder.append(" and est.est_codigo ");
                } else {
                    corpoBuilder.append(" and org.org_codigo ");
                }
            } else if (responsavel.isCor()) {
                if (responsavel.temPermissao(CodedValues.FUN_ACE_CONSIG_CONSIGNATARIA)) {
                    codigoEntidade = responsavel.getCsaCodigo();
                    corpoBuilder.append(" and csa.csa_codigo ");
                } else {
                    corpoBuilder.append(" and ade.cor_codigo ");
                }
            } else if (responsavel.isCsa()) {
                corpoBuilder.append(" and csa.csa_codigo ");
            }
            corpoBuilder.append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
        }

        // Default do parâmetro aqui é Zero, ou seja, se não tiver cadastrado, então a CSA não tem pendência
        corpoBuilder.append(" and ( ");
        corpoBuilder.append("  select count(*) from tb_anexo_autorizacao_desconto aad ");
        corpoBuilder.append("   where aad.ade_codigo = ade.ade_codigo ");
        corpoBuilder.append("     and aad.aad_ativo = 1 ");
        corpoBuilder.append(") < to_numeric(coalesce(nullif(pse.pse_vlr_ref, ''), '0')) ");
        corpoBuilder.append(" and pse.pse_vlr_ref is not null");

        if (!count) {
            corpoBuilder.append(" group by ").append(campos);
            corpoBuilder.append(" order by count(*) desc ");
        }

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("sadCodigos", sadCodigos, query);

        if (!responsavel.isCseSup()) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_CODIGO,
                Columns.CSA_NOME,
                Columns.CSA_NOME_ABREV,
                Columns.CSA_IDENTIFICADOR,
                "QTD_CONSIGNACOES"
         };
    }
}
