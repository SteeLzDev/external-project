package com.zetra.econsig.persistence.query.extrato;

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaExtratoMargemContratosAlteradosQuery</p>
 * <p>Description: Extrato de Margem: </p>
 * PASSO 7) OU SUBTRAI DA MARGEM USADA O VALOR DOS CONTRATOS ALTERADOS NO SISTEMA, QUE POSSUEM OCORRENCIA DE ALTERAÇÃO
 * OBS: SÓ PARA SISTEMAS QUE NÃO CONTROLAM MARGEM (TPC 23 = 'S')
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaExtratoMargemContratosAlteradosQuery extends HQuery {

    private final String rseCodigo;
    private final Date dataFimUltPeriodo;

    public ListaExtratoMargemContratosAlteradosQuery(String rseCodigo, Date dataFimUltPeriodo) {
        this.rseCodigo = rseCodigo;
        this.dataFimUltPeriodo = dataFimUltPeriodo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("'7' as TIPO, ");
        sql.append("ade.adeVlr - ade.adeVlrFolha, ");
        sql.append("ade.adeCodigo, ");
        sql.append("ade.adeData, ");
        sql.append("ade.adeNumero, ");
        sql.append("ade.adeVlr, ");
        sql.append("ade.adeVlrFolha, ");
        sql.append("ade.adeTipoVlr, ");
        sql.append("coalesce(ade.adeIncMargem, 1), ");
        sql.append("sad.sadDescricao, ");
        sql.append("csa.csaIdentificador, ");
        sql.append("csa.csaNome, ");
        sql.append("csa.csaNomeAbrev ");

        if(ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_CONTRATOS_NAO_INCIDEM_MARGEM_EXTRATO_MARGEM, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append(", svc.svcDescricao, ");
            sql.append("cnv.cnvCodVerba ");
        }

        sql.append(" FROM AutDesconto ade ");
        sql.append(" INNER JOIN ade.verbaConvenio vco ");
        sql.append(" INNER JOIN vco.convenio cnv ");
        sql.append(" INNER JOIN cnv.servico svc ");
        sql.append(" INNER JOIN cnv.consignataria csa ");
        sql.append(" INNER JOIN ade.statusAutorizacaoDesconto sad ");

        sql.append(" WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        sql.append(" AND (coalesce(ade.adeIncMargem, 1) <> 0)");
        sql.append(" AND (ade.statusAutorizacaoDesconto.sadCodigo = '").append(CodedValues.SAD_EMANDAMENTO).append("')");
        sql.append(" AND (ade.adeTipoVlr = 'F')");
        sql.append(" AND (ade.adeVlrFolha is not null)");

        // Valor pago pela folha maior que ade_vlr
        sql.append(" AND (ade.adeVlrFolha > ade.adeVlr");
        if (ParamSist.paramEquals(CodedValues.TPC_SUBTRAI_PAGAMENTO_PARCIAL_MARGEM, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema())) {
            // ou valor pago menor e não desconta pagamento parcial
            sql.append(" OR (ade.adeVlrFolha < ade.adeVlr)");
        }
        sql.append(")");

        // Se não zera margem usada (default NAO) adiciona na query cláusula para não retornar nada.
        if (!ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append(" AND (1 = 2)");
        }

        // E existe ocorrência de alteração após o último período
        sql.append(" AND EXISTS (SELECT 1 FROM ade.ocorrenciaAutorizacaoSet oca ");
        sql.append(" where oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_ALTERACAO_CONTRATO).append("'");
        sql.append(" and oca.ocaData > :dataFimPeriodo)");

        // Ordena pela data de inclusão dos contratos que serão listados
        sql.append(" ORDER BY ade.adeData");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);
        defineValorClausulaNomeada("dataFimPeriodo", dataFimUltPeriodo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        if(!ParamSist.getBoolParamSist(CodedValues.TPC_EXIBE_CONTRATOS_NAO_INCIDEM_MARGEM_EXTRATO_MARGEM, AcessoSistema.getAcessoUsuarioSistema())) {
            return new String[] {
                                 "TIPO",
                                 "MARGEM_USADA",
                                 Columns.ADE_CODIGO,
                                 Columns.ADE_DATA,
                                 Columns.ADE_NUMERO,
                                 Columns.ADE_VLR,
                                 Columns.ADE_VLR_FOLHA,
                                 Columns.ADE_TIPO_VLR,
                                 Columns.ADE_INC_MARGEM,
                                 Columns.SAD_DESCRICAO,
                                 Columns.CSA_IDENTIFICADOR,
                                 Columns.CSA_NOME,
                                 Columns.CSA_NOME_ABREV
                         };
        } else {
            return new String[] {
                                 "TIPO",
                                 "MARGEM_USADA",
                                 Columns.ADE_CODIGO,
                                 Columns.ADE_DATA,
                                 Columns.ADE_NUMERO,
                                 Columns.ADE_VLR,
                                 Columns.ADE_VLR_FOLHA,
                                 Columns.ADE_TIPO_VLR,
                                 Columns.ADE_INC_MARGEM,
                                 Columns.SAD_DESCRICAO,
                                 Columns.CSA_IDENTIFICADOR,
                                 Columns.CSA_NOME,
                                 Columns.CSA_NOME_ABREV,
                                 Columns.SVC_DESCRICAO,
                                 Columns.CNV_COD_VERBA
                         };
        }
    }
}
