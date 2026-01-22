package com.zetra.econsig.persistence.query.folha;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: ListarLinhasBlocosProcessamentoQuery</p>
 * <p>Description: Lista as linhas que tiveram blocos mapeados em seus convênios</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarLinhasBlocosProcessamentoQuery extends HQuery  {

    public String tipoEntidade;
    public String codigoEntidade;
    public String csaCodigo;
    public List<String> tbpCodigos;
    public List<String> sbpCodigos;

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        final boolean utilizaVerbaRef = ParamSist.paramEquals(CodedValues.TPC_UTILIZA_CNV_COD_VERBA_REF, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());
        final boolean temProcessamentoFerias = ParamSist.paramEquals(CodedValues.TPC_TEM_PROCESSAMENTO_FERIAS, CodedValues.TPC_SIM, AcessoSistema.getAcessoUsuarioSistema());

        final StringBuilder corpoBuilder = new StringBuilder();

        corpoBuilder.append("SELECT bpr.bprLinha ");
        corpoBuilder.append("FROM BlocoProcessamento bpr ");
        corpoBuilder.append("WHERE 1=1 ");

        if (tbpCodigos != null && !tbpCodigos.isEmpty()) {
            corpoBuilder.append(" AND bpr.tipoBlocoProcessamento.tbpCodigo ").append(criaClausulaNomeada("tbpCodigos", tbpCodigos));
        }
        if (sbpCodigos != null && !sbpCodigos.isEmpty()) {
            corpoBuilder.append(" AND bpr.statusBlocoProcessamento.sbpCodigo ").append(criaClausulaNomeada("sbpCodigos", sbpCodigos));
        }

        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                corpoBuilder.append(" AND bpr.orgao.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else {
                corpoBuilder.append(" AND bpr.estabelecimento.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }

        corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM bpr.convenio cnv WHERE cnv.consignataria.csaCodigo = :csaCodigo)");
        corpoBuilder.append("  OR (bpr.convenio.cnvCodigo IS NULL");
        corpoBuilder.append(" AND NOT EXISTS (SELECT 1 FROM Convenio cnv WHERE bpr.cnvCodVerba = cnv.cnvCodVerba AND cnv.consignataria.csaCodigo <> :csaCodigo)");
        corpoBuilder.append(" AND (EXISTS (SELECT 1 FROM Convenio cnv WHERE bpr.cnvCodVerba = cnv.cnvCodVerba AND cnv.consignataria.csaCodigo = :csaCodigo)");
        if (utilizaVerbaRef) {
            corpoBuilder.append(" OR EXISTS (SELECT 1 FROM Convenio cnv WHERE bpr.cnvCodVerba = cnv.cnvCodVerbaRef AND cnv.consignataria.csaCodigo = :csaCodigo)");
        }
        if (temProcessamentoFerias) {
            corpoBuilder.append(" OR EXISTS (SELECT 1 FROM Convenio cnv WHERE bpr.cnvCodVerba = cnv.cnvCodVerbaFerias AND cnv.consignataria.csaCodigo = :csaCodigo)");
        }
        corpoBuilder.append("))) ");

        corpoBuilder.append(" ORDER BY bpr.bprOrdemExecucao");

        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());
        defineValorClausulaNomeada("csaCodigo", csaCodigo, query);

        if (tbpCodigos != null && !tbpCodigos.isEmpty()) {
            defineValorClausulaNomeada("tbpCodigos", tbpCodigos, query);
        }
        if (sbpCodigos != null && !sbpCodigos.isEmpty()) {
            defineValorClausulaNomeada("sbpCodigos", sbpCodigos, query);
        }
        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }

        return query;
    }
}
