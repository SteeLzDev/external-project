package com.zetra.econsig.persistence.query.beneficios.provedor;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListarProvedorBeneficioCsaEmAreaGeograficaQuery</p>
 * <p>Description: Lista provedores de benefícios de CSA dentro de um raio de geolocalização dado para natureza de serviços também dado.</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author: igor.lucas $
 * $Revision: 28037 $
 * $Date: 2019-10-22 17:13:25 -0300 (ter, 22 out 2019) $
 */
public class ListarProvedorBeneficioCsaEmAreaGeograficaQuery extends HNativeQuery {

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ListarProvedorBeneficioCsaEmAreaGeograficaQuery.class);

    public String orgCodigo;
    public List<String> nseCodigos;
    public String textoBusca;
    public Float latReferencia;
    public Float longReferencia;
    public Float raioAlcance;
    public AcessoSistema responsavel;
    public static final String RAIO_MEDIO_TERRA = "6371";

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {
        if (raioAlcance == null) {
            throw new HQueryException("mensagem.erro.raio.maximo.alcance.geolocalizacao.nao.informado", responsavel);
        }

        final StringBuilder sql = new StringBuilder(2000);

        sql.append("select ").append(Columns.CSA_NOME).append(", ").append(Columns.ENC_LATITUDE).append(", ").append(Columns.ENC_LONGITUDE);
        sql.append(", ").append(Columns.ENC_LOGRADOURO).append(", ").append(Columns.ENC_NUMERO).append(", ").append(Columns.ENC_COMPLEMENTO).append(", ").append(Columns.ENC_BAIRRO).append(", ").append(Columns.ENC_MUNICIPIO);
        sql.append(", ").append(Columns.ENC_UF).append(", ").append(Columns.ENC_CEP);
        sql.append(", (").append(RAIO_MEDIO_TERRA).append(" * ");
        sql.append("acos(cos(radians(").append(latReferencia).append(")) * ");
        sql.append("cos(radians(").append(Columns.ENC_LATITUDE).append(")) * ");
        sql.append("cos(radians(").append(longReferencia).append(") - radians(").append(Columns.ENC_LONGITUDE).append(")) + ");
        sql.append("sin(radians(").append(latReferencia).append(")) * ");
        sql.append("sin(radians(").append(Columns.ENC_LATITUDE).append("))");
        sql.append(")) AS distance, ");
        sql.append("case when ").append(Columns.PCS_VLR).append(" is not NULL then (cast(").append(Columns.PCS_VLR).append(" as decimal) * (1/1000)) else ");
        sql.append(Float.toString((raioAlcance * 1)/1000)).append(" end as raioMax");
        sql.append(" FROM ").append(Columns.TB_CONSIGNATARIA);
        sql.append(" INNER JOIN ").append(Columns.TB_CONVENIO).append(" on ").append(Columns.CNV_CSA_CODIGO).append(" = ").append(Columns.CSA_CODIGO);
        sql.append(" INNER JOIN ").append(Columns.TB_SERVICO).append(" on ").append(Columns.SVC_CODIGO).append(" = ").append(Columns.CNV_SVC_CODIGO);
        sql.append(" INNER JOIN ").append(Columns.TB_BENEFICIO).append(" on ").append(Columns.BEN_CSA_CODIGO).append(" = ").append(Columns.CSA_CODIGO);
        sql.append(" INNER JOIN ").append(Columns.TB_ENDERECO_CONSIGNATARIA).append(" on ").append(Columns.ENC_CSA_CODIGO).append(" = ").append(Columns.CSA_CODIGO);
        sql.append(" LEFT OUTER JOIN ").append(Columns.TB_PARAM_CONSIGNATARIA).append(" on ").append(Columns.PCS_CSA_CODIGO).append(" = ").append(Columns.CSA_CODIGO);
        sql.append(" AND ").append(Columns.PCS_TPA_CODIGO).append(" = '").append(CodedValues.TPA_RAIO_METROS_BUSCA_END_CONSIGNATARIAS).append("'");

        sql.append(" WHERE ").append(Columns.CSA_ATIVO).append(" = '").append(CodedValues.STS_ATIVO).append("'");

        if (!TextHelper.isNull(orgCodigo)) {
            sql.append(" and ").append(Columns.CNV_ORG_CODIGO).append(" ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }

        if ((nseCodigos != null) && !nseCodigos.isEmpty()) {
            sql.append(" and ").append(Columns.SVC_NSE_CODIGO).append(" ").append(criaClausulaNomeada("nseCodigos", nseCodigos));
        }

        if (!TextHelper.isNull(textoBusca)) {
            sql.append(" and (").append("EXISTS (SELECT 1 FROM ").append(Columns.TB_NATUREZA_SERVICO).append(" WHERE ").append(Columns.BEN_NSE_CODIGO).append(" = ").append(Columns.NSE_CODIGO);
            sql.append(" AND ").append(" ").append(criaClausulaNomeada(Columns.NSE_DESCRICAO, "nseDescricao", CodedValues.LIKE_MULTIPLO + textoBusca)).append(")");
            sql.append(" OR EXISTS (SELECT 1 FROM ").append(Columns.TB_PALAVRA_CHAVE_BENEFICIO).append(" INNER JOIN ").append(Columns.TB_PALAVRA_CHAVE).append(" on ");
            sql.append(Columns.PB_PCH_CODIGO).append(" = ").append(Columns.PCH_CODIGO).append(" WHERE ").append(Columns.PB_BEN_CODIGO).append(" = ").append(Columns.BEN_CODIGO);
            sql.append(" AND ").append(criaClausulaNomeada(Columns.PCH_PALAVRA, "pchPalavra", CodedValues.LIKE_MULTIPLO + textoBusca)).append(")");
            sql.append(" OR ").append(" ").append(criaClausulaNomeada(Columns.CSA_NOME, "csaNome", CodedValues.LIKE_MULTIPLO + textoBusca)).append(")");
        }

        sql.append(" GROUP BY ").append(Columns.CSA_NOME).append(", ").append(Columns.ENC_LATITUDE).append(", ");
        sql.append(Columns.ENC_LONGITUDE);
        sql.append(", ").append(Columns.ENC_LOGRADOURO).append(", ").append(Columns.ENC_NUMERO).append(", ").append(Columns.ENC_COMPLEMENTO).append(", ").append(Columns.ENC_BAIRRO).append(", ").append(Columns.ENC_MUNICIPIO);
        sql.append(", ").append(Columns.ENC_UF).append(", ").append(Columns.ENC_CEP);
        sql.append(", ").append("distance").append(", ").append("raioMax");
        sql.append(" HAVING (").append(RAIO_MEDIO_TERRA).append(" * ").append("acos(cos(radians(").append(latReferencia).append(")) * ");
        sql.append("cos(radians(").append(Columns.ENC_LATITUDE).append(")) * ");
        sql.append("cos(radians(").append(longReferencia).append(") - radians(").append(Columns.ENC_LONGITUDE).append(")) + ");
        sql.append("sin(radians(").append(latReferencia).append(")) * ");
        sql.append("sin(radians(").append(Columns.ENC_LATITUDE).append("))");
        sql.append(")) ").append(" <= raioMax");
        sql.append(" ORDER BY distance");
        LOG.debug(sql.toString());

        final Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (!TextHelper.isNull(textoBusca)) {
            defineValorClausulaNomeada("nseDescricao", CodedValues.LIKE_MULTIPLO + textoBusca + CodedValues.LIKE_MULTIPLO, query);
            defineValorClausulaNomeada("pchPalavra", CodedValues.LIKE_MULTIPLO + textoBusca + CodedValues.LIKE_MULTIPLO, query);
            defineValorClausulaNomeada("csaNome", CodedValues.LIKE_MULTIPLO + textoBusca + CodedValues.LIKE_MULTIPLO, query);
        }

        if (!TextHelper.isNull(orgCodigo)) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }

        if ((nseCodigos != null) && !nseCodigos.isEmpty()) {
            defineValorClausulaNomeada("nseCodigos", nseCodigos, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.CSA_NOME,
                Columns.ENC_LATITUDE,
                Columns.ENC_LONGITUDE,
                Columns.ENC_LOGRADOURO,
                Columns.ENC_NUMERO,
                Columns.ENC_COMPLEMENTO,
                Columns.ENC_BAIRRO,
                Columns.ENC_MUNICIPIO,
                Columns.ENC_UF,
                Columns.ENC_CEP,
                "DISTANCIA",
                "RAIO_MAXIMO"
        };
    }

    @Override
    public void setCriterios(TransferObject criterio) {
    }
}
