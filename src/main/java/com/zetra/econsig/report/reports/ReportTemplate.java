package com.zetra.econsig.report.reports;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.hibernate.Session;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.exception.DAOException;
import com.zetra.econsig.helper.relatorio.RelatorioHelper;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.persistence.query.ReportQueryInterface;
import com.zetra.econsig.report.config.Relatorio;
import com.zetra.econsig.report.dao.MyDataSourceFactory;
import com.zetra.econsig.report.dao.ReportDAO;
import com.zetra.econsig.report.dao.hibernate.HibernateDataSourceFactory;

import net.sf.jasperreports.engine.JRDataSource;

/**
 * <p> Title: ReportTemplate</p>
 * <p> Description: Template para os relatórios a serem gerados.e</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public abstract class ReportTemplate {
    protected ReportQueryInterface [] hqueries;
    protected Map<String, Object> parameters = null;
    protected Comparator<Object[]> resultComp;
    protected Relatorio relatorio = null;
    protected AcessoSistema responsavel = null;
    protected MapSqlParameterSource queryParams = null;

    public boolean hasHQuery() {
        return (hqueries != null);
    }
    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }

    public Relatorio getRelatorio() {
        return relatorio;
    }

    public void setRelatorio(Relatorio relatorio) {
        this.relatorio = relatorio;
    }

    public AcessoSistema getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(AcessoSistema responsavel) {
        this.responsavel = responsavel;
    }

    public MapSqlParameterSource getQueryParams() {
        if (queryParams == null) {
            queryParams = new MapSqlParameterSource();
        }
        return queryParams;
    }
    /**
     * Processos que devem ser executados <b>antes</b> da execução
     * da SQL de consulta do relatório
     *
     * @param conn
     */
    public abstract void preSqlProcess(Connection conn);

    /**
     * Processos que devem ser executados <b>depois</b> da execução
     * da SQL de consulta do relatório
     *
     * @param conn
     */
    public abstract void postSqlProcess(Connection conn);

    public abstract String getSql(CustomTransferObject criterio) throws DAOException;

    /**
     * Gera o nome do relatório que será gravado no file system
     * @return
     */
    public String getReportName() {
        String nome = (String) parameters.get(ReportManager.REPORT_FILE_NAME);
        if(nome == null) {
            final String hoje = getHoje();
            nome = (relatorio != null ? relatorio.getTipo() : "NAO_CONFIGURADO" ) + "_" + hoje;
        }

        return nome;
    }

    /**
     * Recupera o Data Source com o dados dos relatório
     * @param conn
     * @param stmt
     * @param criterio
     * @return Jasper Reports Data Source
     * @throws DAOException
     */
    public JRDataSource initReport(ReportDAO factory, CustomTransferObject criterio) throws DAOException {
        preSqlProcess(factory.getConnection());
        return new MyDataSourceFactory().getMyDataSource(getSql(criterio), getQueryParams());
    }

    public JRDataSource initHReport(Session session, CustomTransferObject criterio) throws DAOException {
        for (final ReportQueryInterface hquery : hqueries) {
            hquery.setReportTemplate(this);
        }
        return HibernateDataSourceFactory.getDataSource(session, hqueries, criterio, resultComp, null);
    }

    public JRDataSource initListReport(Session session, CustomTransferObject criterio, List<Object[]> conteudo) throws DAOException {
        return HibernateDataSourceFactory.getDataSource(session, hqueries, criterio, resultComp, conteudo);
    }

    /**
     * Gera o path onde o arquivo do relatório será gravado
     * @return
     */
    public String getPath() {
        return (relatorio != null ? RelatorioHelper.getCaminhoRelatorio(relatorio.getTipo(), null, responsavel) : null);
    }

    /**
     * Recupera a data e tempo que será utilizado no nome do relatório.
     * @return Data corrente no formato ddMMyyHHmmss
     */
    private String getHoje() {
        final SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyHHmmss");
        return formatter.format(DateHelper.getSystemDatetime());
    }

    public String getSqlSubrelatorio(String sreTemplateSql, CustomTransferObject criterio) throws DAOException  {
        return null;
    }
}
