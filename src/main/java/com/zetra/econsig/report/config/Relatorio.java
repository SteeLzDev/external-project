package com.zetra.econsig.report.config;

import java.io.Serializable;

import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p> Title: Relatorio</p>
 * <p> Description: Classe com as informções de configuração dos relatórios.</p>
 * <p> Copyright: Copyright (c) 2006 </p>
 * <p> Company: ZetraSoft Ltda. </p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class Relatorio implements Serializable {
    private String funcoes;
    private String classeProcesso;
    private String classeReport;
    private String jasperTemplate;
    private String tipo;
    private String titulo;
    private String modeloDinamico;
    private String subReport;
    private boolean agendado;
    private String classeAgendamento;
    private String tipoAgendamento;
    private String templateSql;
    private boolean ativo;
    private String customizado;
    private String agrupamento;

    public Relatorio(String tipo, String titulo, String funcoes, String classeReport, String classeProcesso, String jasperTemplate, String modeloDinamico, 
                     String subReport, String classeAgendamento, String tipoAgendamento, String templateSql, boolean ativo, boolean agendado, String customizado, String agrupamento) {
        
        this.tipo = tipo;
        this.titulo = titulo;
        this.funcoes = funcoes;
        this.classeProcesso = classeProcesso;
        this.classeReport = classeReport;
        this.jasperTemplate = jasperTemplate;
        this.modeloDinamico = modeloDinamico;
        this.subReport = (!TextHelper.isNull(subReport)) ? subReport.replaceAll(" ", "") : "";
        this.classeAgendamento = classeAgendamento;
        this.agendado = agendado;
        this.tipoAgendamento = (!TextHelper.isNull(tipoAgendamento)) ? tipoAgendamento.replaceAll(" ", "") : "";
        this.templateSql = templateSql;
        this.ativo = ativo;
        this.customizado = customizado;
        this.agrupamento = agrupamento;
    }

    public String getFuncoes() {
        return funcoes;
    }
    public void setFuncoes(String funcoes) {
        this.funcoes = funcoes;
    }

    public String getClasseProcesso() {
        return classeProcesso;
    }
    public void setClasseProcesso(String classeProcesso) {
        this.classeProcesso = classeProcesso;
    }

    public String getClasseReport() {
        return classeReport;
    }
    public void setClasseReport(String classeReport) {
        this.classeReport = classeReport;
    }

    public String getJasperTemplate() {
        return jasperTemplate;
    }
    public void setJasperTemplate(String jasperTemplate) {
        this.jasperTemplate = jasperTemplate;
    }

    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String toString() {
        StringBuilder relatorio = new StringBuilder();
        relatorio.append("[tipo=").append(tipo);
        relatorio.append(", titulo=").append(titulo);
        relatorio.append(", funcoes=").append(funcoes);
        relatorio.append(", classeProcesso=").append(classeProcesso);
        relatorio.append(", classeReport=").append(classeReport);
        relatorio.append(", jasperTemplate=").append(jasperTemplate).append("]");
        return relatorio.toString();
    }

    public String getModeloDinamico() {
        return modeloDinamico;
    }

    public void setModeloDinamico(String modeloDinamico) {
        this.modeloDinamico = modeloDinamico;
    }

    public String[] getArraySubreport() {
        String[] subreports = new String[]{};
        if (!TextHelper.isNull(subReport)) {
            subreports = subReport.split(",");
        }
        return subreports;
    }

    public String getSubReport() {
        return subReport;
    }

    public void setSubReport(String subReport) {
        this.subReport = subReport;
    }

    public boolean isAgendado() {
        return agendado;
    }

    public void setAgendado(boolean agendado) {
        this.agendado = agendado;
    }

    public String getClasseAgendamento() {
        return classeAgendamento;
    }

    public void setClasseAgendamento(String classeAgendamento) {
        this.classeAgendamento = classeAgendamento;
    }

    public String[] getArrayTipoAgendamento() {
        String[] tiposAgendamento = new String[]{};
        if (!TextHelper.isNull(this.tipoAgendamento)) {
            tiposAgendamento = this.tipoAgendamento.split(",");
        }
        return tiposAgendamento;
    }

    public String getTipoAgendamento() {
        return tipoAgendamento;
    }

    public void setTipoAgendamento(String tipoAgendamento) {
        this.tipoAgendamento = tipoAgendamento;
    }
    
    public String getTemplateSql() {
        return templateSql;
    }

    public void setTemplateSql(String templateSql) {
        this.templateSql = templateSql;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public String getCustomizado() {
        return customizado;
    }

    public void setCustomizado(String customizado) {
        this.customizado = customizado;
    }

    public String getAgrupamento() {
        return agrupamento;
    }

    public void setAgrupamento(String agrupamento) {
        this.agrupamento = agrupamento;
    }
}
