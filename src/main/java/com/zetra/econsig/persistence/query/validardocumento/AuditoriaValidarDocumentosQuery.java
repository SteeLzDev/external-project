package com.zetra.econsig.persistence.query.validardocumento;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;
import com.zetra.econsig.values.StatusSolicitacaoEnum;
import com.zetra.econsig.values.TipoSolicitacaoEnum;

/**
 * <p>Title: AuditoriaValidarDocumentosQuery</p>
 * <p>Description: Auditoria de contratos para validação de documentos</p>
 * <p>Copyright: Copyright (c) 2002-2020</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date
 */
public class AuditoriaValidarDocumentosQuery extends HQuery {

	public Date periodo;
    public boolean usuarios;
    public boolean incluiOrgao;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        List<String> ssoCodigos = new ArrayList<>();
        if(!usuarios) {
            ssoCodigos.add(StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo());
        }
        ssoCodigos.add(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_APROVADA.getCodigo());
        ssoCodigos.add(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_REPROVADA.getCodigo());

    	StringBuilder sql = new StringBuilder();
    	sql.append("SELECT csa.csaNomeAbrev, ");
        if(incluiOrgao) {
            sql.append("org.orgNome, ");
        }
        sql.append("usu.usuLogin, ");
        if(!usuarios) {
            sql.append("sum(CASE WHEN soa.ssoCodigo = '").append(StatusSolicitacaoEnum.PENDENTE_VALIDACAO_DOCUMENTOS.getCodigo()).append("' then 1 else 0 end) as PENDENTE, ");
        }
        sql.append("sum(CASE WHEN soa.ssoCodigo = '").append(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_APROVADA.getCodigo()).append("' then 1 else 0 end) as APROVADO, ");
        sql.append("sum(CASE WHEN soa.ssoCodigo = '").append(StatusSolicitacaoEnum.VALIDACAO_DOCUMENTO_REPROVADA.getCodigo()).append("' then 1 else 0 end) as REPROVADO ");
        sql.append("FROM AutDesconto ade ");
        sql.append("INNER JOIN ade.registroServidor rse  ");
        sql.append("INNER JOIN rse.servidor ser  ");
        sql.append("INNER JOIN ade.verbaConvenio vco  ");
        sql.append("INNER JOIN vco.convenio cnv  ");
        sql.append("INNER JOIN cnv.consignataria csa ");
        sql.append("INNER JOIN cnv.servico svc  ");
        sql.append("INNER JOIN ade.solicitacaoAutorizacaoSet soa  ");
        sql.append("INNER JOIN soa.usuario usu  ");
        sql.append("INNER JOIN soa.origemSolicitacao oso  ");
        sql.append("INNER JOIN cnv.orgao org ");
        sql.append("WHERE soa.tisCodigo ='").append(TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo()).append("' ");
        sql.append("AND NOT EXISTS (SELECT 1 FROM SolicitacaoAutorizacao soa1 WHERE soa1.soaData > soa.soaData AND ade.adeCodigo = soa1.adeCodigo AND soa1.tisCodigo ='").append(TipoSolicitacaoEnum.SOLICITACAO_DEPENDE_AUTORIZACAO.getCodigo()).append("') ");
        sql.append("AND ade.sadCodigo IN ('").append(TextHelper.join(CodedValues.SAD_CODIGOS_ABERTOS_EXPORTACAO, "' , '")).append("') ");
        sql.append("AND soa.ssoCodigo ").append(criaClausulaNomeada("ssoCodigos", ssoCodigos));

        if(periodo !=null) {
            sql.append("AND soa.soaPeriodo ").append(criaClausulaNomeada("periodo", periodo));
        }

        if(!usuarios) {
            sql.append("GROUP BY csa.csaCodigo ");
            if(incluiOrgao) {
                sql.append(", org.orgCodigo ");
            }
            sql.append("ORDER BY csa.csaNomeAbrev ");
        }else {
            sql.append("GROUP BY soa.usuCodigo ");
            sql.append("ORDER BY usu.usuLogin ");
        }

        Query<Object[]> query = instanciarQuery(session, sql.toString());
        defineValorClausulaNomeada("ssoCodigos", ssoCodigos, query);

        if(periodo !=null) {
            defineValorClausulaNomeada("periodo", periodo, query);
        }

        return query;
    }

    @Override
    protected String[] getFields() {
        if(!usuarios) {
            if (incluiOrgao) {
                return new String[] {
                        Columns.CSA_NOME_ABREV,
                        Columns.ORG_NOME,
                        Columns.USU_LOGIN,
                        "PENDENTE",
                        "APROVADO",
                        "REPROVADO"
                };
            }

            return new String[] {
                    Columns.CSA_NOME_ABREV,
                    Columns.USU_LOGIN,
                    "PENDENTE",
                    "APROVADO",
                    "REPROVADO"
            };
        }

        return new String[] {
                Columns.CSA_NOME_ABREV,
                Columns.USU_LOGIN,
                "APROVADO",
                "REPROVADO"
        };
    }
}
