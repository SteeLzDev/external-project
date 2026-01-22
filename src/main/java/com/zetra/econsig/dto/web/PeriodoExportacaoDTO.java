package com.zetra.econsig.dto.web;

public class PeriodoExportacaoDTO {

    private String orgCodigo;
    private String orgNome;
    private Short diaCorte;
    private String periodo;
    private String dataIni;
    private String dataFim;

    public String getOrgCodigo() {
        return orgCodigo;
    }

    public void setOrgCodigo(String orgCodigo) {
        this.orgCodigo = orgCodigo;
    }

    public String getOrgNome() {
        return orgNome;
    }

    public void setOrgNome(String orgNome) {
        this.orgNome = orgNome;
    }

    public Short getDiaCorte() {
        return diaCorte;
    }

    public void setDiaCorte(Short diaCorte) {
        this.diaCorte = diaCorte;
    }

    public String getDataIni() {
        return dataIni;
    }

    public void setDataIni(String dataIni) {
        this.dataIni = dataIni;
    }

    public String getDataFim() {
        return dataFim;
    }

    public void setDataFim(String dataFim) {
        this.dataFim = dataFim;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dataFim == null) ? 0 : dataFim.hashCode());
        result = prime * result + ((dataIni == null) ? 0 : dataIni.hashCode());
        result = prime * result + ((periodo == null) ? 0 : periodo.hashCode());
        return result;
    }

    /**
     * Compara apenas data ini/fim e o período, e considera os registros iguais
     * caso tenham as mesmas datas, para que possa determinar se os períodos
     * dos órgãos são os mesmos.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PeriodoExportacaoDTO other = (PeriodoExportacaoDTO) obj;
        if (dataFim == null) {
            if (other.dataFim != null) {
                return false;
            }
        } else if (!dataFim.equals(other.dataFim)) {
            return false;
        }
        if (dataIni == null) {
            if (other.dataIni != null) {
                return false;
            }
        } else if (!dataIni.equals(other.dataIni)) {
            return false;
        }
        if (periodo == null) {
            if (other.periodo != null) {
                return false;
            }
        } else if (!periodo.equals(other.periodo)) {
            return false;
        }
        return true;
    }
}
