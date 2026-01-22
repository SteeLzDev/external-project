package com.zetra.econsig.persistence.query.extrato;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaExtratoMargemVlrFolhaMenorQuery</p>
 * <p>Description: Extrato de Margem: </p>
 * PASSO 3) ADICIONA NA MARGEM USADA, O VALOR PAGO A MENOS PELA FOLHA
 * OBS: SÓ PARA SISTEMAS QUE NÃO CONTROLAM MARGEM (TPC 23 = 'S') E
 * QUE SUBTRAEM DA MARGEM OS PAGAMENTOS PARCIAIS (TPC 323 != 'N')
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaExtratoMargemVlrFolhaMenorQuery extends HQuery {

    private final String rseCodigo;

    public ListaExtratoMargemVlrFolhaMenorQuery(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("'3' as TIPO, ");
        sql.append("coalesce(ade.adeVlrParcelaFolha, ade.adeVlr) - ade.adeVlrFolha, ");
        sql.append("ade.adeCodigo, ");
        sql.append("ade.adeData, ");
        sql.append("ade.adeNumero, ");
        sql.append("coalesce(ade.adeVlrParcelaFolha, ade.adeVlr), ");
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
        sql.append(" AND (coalesce(ade.adePaga, 'N') = 'S')");
        sql.append(" AND (ade.adeVlrFolha is not null)");
        sql.append(" AND (ade.adeVlrFolha < coalesce(ade.adeVlrParcelaFolha, ade.adeVlr))");

        // Se não zera margem usada (default NAO) ou não subtrai pagamento parcial da margem (default SIM)
        // então adiciona na query cláusula para não retornar nada.
        if (!ParamSist.paramEquals(CodedValues.TPC_ZERA_MARGEM_USADA, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema()) ||
                ParamSist.paramEquals(CodedValues.TPC_SUBTRAI_PAGAMENTO_PARCIAL_MARGEM, CodedValues.TPC_NAO, AcessoSistema.getAcessoUsuarioSistema())) {
            sql.append(" AND (1 = 2)");
        }

        // Ordena pela data de inclusão dos contratos que serão listados
        sql.append(" ORDER BY ade.adeData");

        final Query<Object[]> query = instanciarQuery(session, sql.toString());
        defineValorClausulaNomeada("rseCodigo", rseCodigo, query);

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
