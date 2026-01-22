package com.zetra.econsig.persistence.query.parametro;

import org.hibernate.query.Query;
import org.hibernate.Session;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.dao.mysql.MySqlDAOFactory;
import com.zetra.econsig.persistence.query.HNativeQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaContratoLiberaOperacaoByDestinoQuery</p>
 * <p>Description: Lista contratos que possuem relacionamento do tipo Libera Operacao.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaContratoLiberaOperacaoByDestinoQuery extends HNativeQuery {

    public String adeCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {}

    /**
     * Se houver o relacionamento TNT_CONTRATO_PREEXISTENTE_LIBERA_OPERACAO entre os serviços
     * e a consignatária tiver convênio destes serviços, então ela só poderá fazer a operação
     * se o servidor <b>NÃO</b> tiver um contrato em aberto do serviço relacionado.
     */
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        if (TextHelper.isNull(adeCodigo)) {
            throw new HQueryException("mensagem.erroInternoSistema", (AcessoSistema) null);
        }

        String fields = Columns.SVC_DESCRICAO + MySqlDAOFactory.SEPARADOR
        + Columns.ADE_CODIGO;

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ").append(fields);
        corpoBuilder.append(" FROM ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" adeDestino ");
        corpoBuilder.append("INNER JOIN tb_verba_convenio vcoDestino ON (adeDestino.vco_codigo = vcoDestino.vco_codigo) ");
        corpoBuilder.append("INNER JOIN tb_convenio cnvDestino ON (vcoDestino.cnv_codigo = cnvDestino.cnv_codigo) ");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_RELACIONAMENTO_SERVICO).append(" ON (").append(Columns.RSV_SVC_CODIGO_DESTINO).append(" = cnvDestino.").append(Columns.getColumnName(Columns.SVC_CODIGO)).append(" AND ");
        corpoBuilder.append(Columns.RSV_TNT_CODIGO).append(" = '").append(CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_OPERACAO).append("') ");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.RSV_SVC_CODIGO_ORIGEM).append(" AND ");
        corpoBuilder.append(Columns.CNV_ORG_CODIGO).append(" = cnvDestino.").append(Columns.getColumnName(Columns.RSE_ORG_CODIGO)).append(" AND ");
        corpoBuilder.append(Columns.CNV_SCV_CODIGO).append(" = '").append(CodedValues.SCV_ATIVO).append("' AND ");
        corpoBuilder.append(Columns.CNV_CSA_CODIGO).append(" = cnvDestino.").append(Columns.getColumnName(Columns.CSA_CODIGO)).append(") ");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_SERVICO).append(" ON (").append(Columns.SVC_CODIGO).append(" = ").append(Columns.CNV_SVC_CODIGO).append(") ");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (").append(Columns.CNV_CODIGO).append(" = ").append(Columns.VCO_CNV_CODIGO).append(") ");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ON (").append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(" AND ");
        corpoBuilder.append(Columns.ADE_RSE_CODIGO).append(" = adeDestino.").append(Columns.getColumnName(Columns.RSE_CODIGO)).append(" AND ");
        corpoBuilder.append(Columns.ADE_SAD_CODIGO).append(" in (:sadCodigo)) ");
        corpoBuilder.append(" WHERE adeDestino.").append(Columns.getColumnName(Columns.ADE_CODIGO)).append(" = :adeCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);
        defineValorClausulaNomeada("sadCodigo", CodedValues.SAD_CODIGOS_ATIVOS, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
                Columns.SVC_DESCRICAO,
                Columns.ADE_CODIGO
        };
    }
}
