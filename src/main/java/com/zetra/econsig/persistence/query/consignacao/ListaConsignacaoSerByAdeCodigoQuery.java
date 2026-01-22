package com.zetra.econsig.persistence.query.consignacao;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ListaConsignacaoByRseCodigoQuery</p>
 * <p>Description: Listagem de Consignações de um dado servidor
 * com os demais parâmetros informados</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
  * $Author$
 * $Revision$
 * $Date$
 */
public class ListaConsignacaoSerByAdeCodigoQuery extends HQuery {

    public String adeCodigo;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("select ade.adeCodigo, ade.adeNumero, ade.adeIdentificador, ");
        corpoBuilder.append("ade.adeData, ade.adeVlr, ade.adeVlrFolha, ");
        corpoBuilder.append("ade.adeVlrLiquido, ade.adeTaxaJuros, ade.adePrazo, ");
        corpoBuilder.append("ade.adePrdPagas, ade.adeAnoMesIni, ade.adeAnoMesFim, ");
        corpoBuilder.append("ade.adeTipoVlr, ade.adeIndice, ade.adeCodReg, ");
        corpoBuilder.append("ade.adeIncMargem, ade.adeIntFolha, ade.adeCarencia, ");
        corpoBuilder.append("ade.adeVlrPercentual, ade.adeVlrParcelaFolha, ");
        corpoBuilder.append("ade.adeDataStatus, ade.adePeriodicidade, ");
        corpoBuilder.append("sad.sadDescricao, sad.sadCodigo, ");
        corpoBuilder.append("csa.csaIdentificador, csa.csaNome, csa.csaNomeAbrev, ");
        corpoBuilder.append("usu.usuLogin, usu.usuCodigo, usu.usuTipoBloq, ");
        corpoBuilder.append("cnv.cnvCodVerba, ");
        corpoBuilder.append("svc.svcIdentificador, svc.svcDescricao, rse.rseMatricula, ser.serNome, ser.serCpf ");
        corpoBuilder.append("from AutDesconto ade ");
        corpoBuilder.append("inner join ade.statusAutorizacaoDesconto sad ");
        corpoBuilder.append("inner join ade.usuario usu ");
        corpoBuilder.append("inner join ade.verbaConvenio vco ");
        corpoBuilder.append("inner join ade.registroServidor rse ");
        corpoBuilder.append("inner join rse.servidor ser ");
        corpoBuilder.append("inner join vco.convenio cnv ");
        corpoBuilder.append("inner join cnv.servico svc ");
        corpoBuilder.append("inner join cnv.consignataria csa ");
        corpoBuilder.append("where ade.registroServidor.rseCodigo = ");
        corpoBuilder.append("(SELECT ade.registroServidor.rseCodigo from AutDesconto ade where ade.adeCodigo ").append(criaClausulaNomeada("adeCodigo", adeCodigo) + ")");

        final Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("adeCodigo", adeCodigo, query);

        return query;
    }

    @Override
    protected String[] getFields() {
        return new String[] {
            Columns.ADE_CODIGO,
            Columns.ADE_NUMERO,
            Columns.ADE_IDENTIFICADOR,
            Columns.ADE_DATA,
            Columns.ADE_VLR,
            Columns.ADE_VLR_FOLHA,
            Columns.ADE_VLR_LIQUIDO,
            Columns.ADE_TAXA_JUROS,
            Columns.ADE_PRAZO,
            Columns.ADE_PRD_PAGAS,
            Columns.ADE_ANO_MES_INI,
            Columns.ADE_ANO_MES_FIM,
            Columns.ADE_TIPO_VLR,
            Columns.ADE_INDICE,
            Columns.ADE_COD_REG,
            Columns.ADE_INC_MARGEM,
            Columns.ADE_INT_FOLHA,
            Columns.ADE_CARENCIA,
            Columns.ADE_VLR_PERCENTUAL,
            Columns.ADE_VLR_PARCELA_FOLHA,
            Columns.ADE_DATA_STATUS,
            Columns.ADE_PERIODICIDADE,
            Columns.SAD_DESCRICAO,
            Columns.SAD_CODIGO,
            Columns.CSA_IDENTIFICADOR,
            Columns.CSA_NOME,
            Columns.CSA_NOME_ABREV,
            Columns.USU_LOGIN,
            Columns.USU_CODIGO,
            Columns.USU_TIPO_BLOQ,
            Columns.CNV_COD_VERBA,
            Columns.SVC_IDENTIFICADOR,
            Columns.SVC_DESCRICAO,
            Columns.RSE_MATRICULA,
            Columns.SER_NOME,
            Columns.SER_CPF
        };
    }
}