package com.zetra.econsig.persistence.query.parcela;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.AcaoEnum;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaParcelaDescontoQuery</p>
 * <p>Description: Busca os usuários que possuem parcelas rejeitadas e que estão com o envio de
 * email habilitados.</p>
 * <p>Copyright: Copyright (c) 2002-2016</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaParcelasComAcaoEmTipoDescontoQuery extends HQuery {

    private static final String SRS_IRRELEVANTE = "999999999";
    private final List<String> orgCodigo;
    private final List<String> estCodigo;

    public ListaParcelasComAcaoEmTipoDescontoQuery(List<String> orgCodigo, List<String> estCodigo) {
        this.orgCodigo = orgCodigo;
        this.estCodigo = estCodigo;
    }

    @Override
    protected Query<Object[]> preparar(Session session) throws HQueryException {

        StringBuilder sql = new StringBuilder();

        sql.append(" select ade.adeCodigo, tde.tdeCodigo ");
        sql.append(" from ParcelaDescontoPeriodo prd ");
        sql.append(" inner join prd.autDesconto ade ");
        sql.append(" inner join ade.registroServidor rse ");
        sql.append(" inner join prd.tipoDesconto tde ");
        sql.append(" inner join tde.acao aca ");

        if ((orgCodigo != null && !orgCodigo.isEmpty()) || (estCodigo != null && !estCodigo.isEmpty())) {
            sql.append(" inner join ade.verbaConvenio vco ");
            sql.append(" inner join vco.convenio cnv ");
            sql.append(" inner join cnv.orgao org ");

            if (estCodigo != null && !estCodigo.isEmpty()) {
                sql.append(" inner join org.estabelecimento est ");
            }
        }

        sql.append(" where rse.statusRegistroServidor.srsCodigo <> (case ");
        sql.append(" when aca.acaCodigo = '").append(AcaoEnum.BLOQUEAR_SERVIDOR.getCodigo()).append("' then '").append(CodedValues.SRS_BLOQUEADO).append("' ");
        sql.append(" when aca.acaCodigo = '").append(AcaoEnum.EXCLUIR_SERVIDOR.getCodigo()).append("' then '").append(CodedValues.SRS_EXCLUIDO).append("' ");
        sql.append(" when aca.acaCodigo = '").append(AcaoEnum.REGISTRAR_FALECIMENTO_SERVIDOR.getCodigo()).append("' then '").append(CodedValues.SRS_FALECIDO).append("' ");
        sql.append(" when aca.acaCodigo = '").append(AcaoEnum.SUSPENDER_CONTRATO_PARCELA_REJEITADA.getCodigo()).append("' then '").append(SRS_IRRELEVANTE).append("' ");
        sql.append(" else rse.statusRegistroServidor.srsCodigo end) ");

        if (orgCodigo != null && !orgCodigo.isEmpty()) {
            sql.append(" and org.orgCodigo ").append(criaClausulaNomeada("orgCodigo", orgCodigo));
        }
        if (estCodigo != null && !estCodigo.isEmpty()) {
            sql.append(" and est.estCodigo ").append(criaClausulaNomeada("estCodigo", estCodigo));
        }

        Query<Object[]> query = instanciarQuery(session, sql.toString());

        if (orgCodigo != null && !orgCodigo.isEmpty()) {
            defineValorClausulaNomeada("orgCodigo", orgCodigo, query);
        }
        if (estCodigo != null && !estCodigo.isEmpty()) {
            defineValorClausulaNomeada("estCodigo", estCodigo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String [] {
            Columns.ADE_CODIGO,
            Columns.TDE_CODIGO,
        };
    }
}
