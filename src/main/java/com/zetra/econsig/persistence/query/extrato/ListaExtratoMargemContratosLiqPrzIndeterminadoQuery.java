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
 * <p>Title: ListaExtratoMargemContratosLiqPrzIndeterminadoQuery</p>
 * <p>Description: Extrato de Margem: </p>
 * PASSO 8) ADICIONA NA MARGEM USADA OS CONTRATOS DE PRAZO INDETERMINADO LIQUIDADOS
 * ASSOCIADOS A SERVIÇO QUE SÓ LIBERA MARGEM APÓS A CARGA DE MARGEM
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListaExtratoMargemContratosLiqPrzIndeterminadoQuery extends HQuery {

    private final String rseCodigo;

    public ListaExtratoMargemContratosLiqPrzIndeterminadoQuery(String rseCodigo) {
        this.rseCodigo = rseCodigo;
    }

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder sql = new StringBuilder();

        sql.append("SELECT ");
        sql.append("'8' as TIPO, ");
        sql.append("(case when ade.adeTipoVlr = 'P' then coalesce(ade.adeVlrFolha, ade.adeVlr) else ade.adeVlr end), ");
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
        sql.append(" INNER JOIN cnv.consignataria csa ");
        sql.append(" INNER JOIN cnv.servico svc ");
        sql.append(" INNER JOIN svc.paramSvcConsignanteSet pse ");
        sql.append(" INNER JOIN ade.statusAutorizacaoDesconto sad ");
        sql.append(" INNER JOIN ade.ocorrenciaAutorizacaoSet oca ");
        sql.append(" INNER JOIN ade.registroServidor rse ");

        sql.append(" WHERE ade.registroServidor.rseCodigo ").append(criaClausulaNomeada("rseCodigo", rseCodigo));
        sql.append(" AND coalesce(ade.adeIncMargem, 1) <> 0");
        sql.append(" AND ade.adePrazo IS NULL");
        sql.append(" AND ade.statusAutorizacaoDesconto.sadCodigo  = '").append(CodedValues.SAD_LIQUIDADA).append("'");
        sql.append(" AND oca.tipoOcorrencia.tocCodigo = '").append(CodedValues.TOC_TARIF_LIQUIDACAO).append("'");
        sql.append(" AND oca.ocaData > rse.rseDataCarga");
        sql.append(" AND pse.tipoParamSvc.tpsCodigo = '").append(CodedValues.TPS_PRENDE_MARGEM_LIQ_ADE_PRZ_INDET_ATE_CARGA_MARGEM).append("'");
        sql.append(" AND pse.pseVlr = '").append(CodedValues.PSE_BOOLEANO_SIM).append("'");

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
