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
 * <p>Title: ListaContratoLiberaOperacaoByOrigemQuery</p>
 * <p>Description: Lista contratos que possuem relacionamento do tipo Libera Operacao.</p>
 * <p>Copyright: Copyright (c) 2009</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaContratoLiberaOperacaoByOrigemQuery extends HNativeQuery {

    public String csaCodigo;
    public String svcCodigo;
    public String rseCodigo;

    @Override
    public void setCriterios(TransferObject criterio) {}

    /**
     * Se houver o relacionamento TNT_CONTRATO_PREEXISTENTE_LIBERA_OPERACAO entre os serviços
     * e se a consignatária tiver convênio destes serviços, então ela só poderá fazer a operação
     * se o servidor <b>NÃO</b> tiver um contrato em aberto do serviço relacionado.
     */
    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        if (TextHelper.isNull(csaCodigo) || TextHelper.isNull(svcCodigo) || TextHelper.isNull(rseCodigo)) {
            throw new HQueryException("mensagem.erroInternoSistema", (AcessoSistema) null);
        }

        String fields = Columns.SVC_DESCRICAO + MySqlDAOFactory.SEPARADOR
        + Columns.ADE_CODIGO;

        StringBuilder corpoBuilder = new StringBuilder();
        corpoBuilder.append("SELECT ").append(fields);
        corpoBuilder.append(" FROM ").append(Columns.TB_REGISTRO_SERVIDOR);
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_RELACIONAMENTO_SERVICO).append(" ON (").append(Columns.RSV_SVC_CODIGO_ORIGEM).append(" = :svcCodigo AND ");
        corpoBuilder.append(Columns.RSV_TNT_CODIGO).append(" = '").append(CodedValues.TNT_CONTRATO_PREEXISTENTE_LIBERA_OPERACAO).append("') ");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_CONVENIO).append(" ON (").append(Columns.CNV_SVC_CODIGO).append(" = ").append(Columns.RSV_SVC_CODIGO_DESTINO).append(" AND ");
        corpoBuilder.append(Columns.CNV_ORG_CODIGO).append(" = ").append(Columns.RSE_ORG_CODIGO).append(" AND ");
        corpoBuilder.append(Columns.CNV_SCV_CODIGO).append(" = '").append(CodedValues.SCV_ATIVO).append("' AND ");
        corpoBuilder.append(Columns.CNV_CSA_CODIGO).append(" = :csaCodigo) ");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_SERVICO).append(" ON (").append(Columns.SVC_CODIGO).append(" = ").append(Columns.CNV_SVC_CODIGO).append(") ");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_VERBA_CONVENIO).append(" ON (").append(Columns.CNV_CODIGO).append(" = ").append(Columns.VCO_CNV_CODIGO).append(") ");
        corpoBuilder.append(" LEFT OUTER JOIN ").append(Columns.TB_AUTORIZACAO_DESCONTO).append(" ON (").append(Columns.ADE_VCO_CODIGO).append(" = ").append(Columns.VCO_CODIGO).append(" AND ");
        corpoBuilder.append(Columns.ADE_RSE_CODIGO).append(" = ").append(Columns.RSE_CODIGO).append(" AND ");
        corpoBuilder.append(Columns.ADE_SAD_CODIGO).append(" in ('").append(CodedValues.SAD_DEFERIDA).append("', '").append(CodedValues.SAD_EMANDAMENTO).append("')) ");
        corpoBuilder.append(" WHERE ").append(Columns.RSE_CODIGO).append(" = :rseCodigo");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);
        defineValorClausulaNomeada("svcCodigo", svcCodigo, query);
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

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
