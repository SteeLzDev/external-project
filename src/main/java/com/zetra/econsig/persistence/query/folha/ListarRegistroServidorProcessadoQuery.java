package com.zetra.econsig.persistence.query.folha;

import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.zetra.econsig.exception.HQueryException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.query.HQuery;
import com.zetra.econsig.values.TipoBlocoProcessamentoEnum;

/**
 * <p>Title: ListarRegistroServidorProcessadoQuery</p>
 * <p>Description: Retorna os registros servidores processados em um determinado período.</p>
 * <p>Copyright: Copyright (c) 2002-2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ListarRegistroServidorProcessadoQuery extends HQuery {

    public Date bprPeriodo;
    public String tipoEntidade;
    public String codigoEntidade;
    public List<String> srsCodigos;
    public List<String> tocCodigos; 

    @Override
    public Query<Object[]> preparar(Session session) throws HQueryException {
        StringBuilder corpoBuilder = new StringBuilder();

        // Lista registros servidores
        corpoBuilder.append("SELECT rse.rseCodigo ");
        corpoBuilder.append("FROM RegistroServidor rse ");
        if (tipoEntidade != null && tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST)) {
            corpoBuilder.append("INNER JOIN rse.orgao org ");
        }
        
        // Excluídos
        corpoBuilder.append("WHERE 1=1 ");
        corpoBuilder.append("AND rse.statusRegistroServidor.srsCodigo ").append(criaClausulaNomeada("srsCodigos", srsCodigos)).append(" "); 

        // Da entidade que realiza o processamento
        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                corpoBuilder.append(" AND rse.orgao.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else {
                corpoBuilder.append(" AND org.estabelecimento.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }

        // Que tiveram ocorrências registradas no processamento do período informado
        corpoBuilder.append(" AND EXISTS (SELECT 1 FROM OcorrenciaRegistroSer ors");
        corpoBuilder.append(" WHERE ors.registroServidor.rseCodigo = rse.rseCodigo");
        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            corpoBuilder.append(" AND ors.tipoOcorrencia.tocCodigo ").append(criaClausulaNomeada("tocCodigos", tocCodigos));    
        }
        corpoBuilder.append(" AND ors.orsData >= (SELECT MIN(bpr.bprDataInclusao) ");
        corpoBuilder.append(" FROM BlocoProcessamento bpr");
        corpoBuilder.append(" WHERE bpr.tipoBlocoProcessamento.tbpCodigo = '").append(TipoBlocoProcessamentoEnum.MARGEM.getCodigo()).append("'");
        corpoBuilder.append(" AND bpr.bprPeriodo ").append(criaClausulaNomeada("bprPeriodo", bprPeriodo));
        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            if (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG)) {
                corpoBuilder.append(" AND bpr.orgao.orgCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            } else {
                corpoBuilder.append(" AND bpr.estabelecimento.estCodigo ").append(criaClausulaNomeada("codigoEntidade", codigoEntidade));
            }
        }
        corpoBuilder.append(") ");
        corpoBuilder.append(")");
        
        Query<Object[]> query = instanciarQuery(session, corpoBuilder.toString());

        defineValorClausulaNomeada("srsCodigos", srsCodigos, query); 
        
        defineValorClausulaNomeada("bprPeriodo", bprPeriodo, query);
        
        if (tocCodigos != null && !tocCodigos.isEmpty()) {
            defineValorClausulaNomeada("tocCodigos", tocCodigos, query);     
        }
        if (tipoEntidade != null && (tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_ORG) || tipoEntidade.equalsIgnoreCase(AcessoSistema.ENTIDADE_EST))) {
            defineValorClausulaNomeada("codigoEntidade", codigoEntidade, query);
        }
        
        return query;
    }
}
