package com.zetra.econsig.dto.parametros;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.texto.DateHelper;
import com.zetra.econsig.helper.texto.LocaleHelper;

/**
 * <p>Title: AlterarMultiplasConsignacoesParametros</p>
 * <p>Description: Parâmetros necessários na operação Alterar Múltiplas Consignações.</p>
 * <p>Copyright: Copyright (c) 2002-2021</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class AlterarMultiplasConsignacoesParametros extends Parametros {
    private List<String> adeCodigos;
    private String tmoCodigo;
    private String ocaObs;
    private BigDecimal vlrTotalNovo;
    private BigDecimal percentualMargem;
    private Short marCodigo;
    private boolean alterarPrazo;
    private boolean restaurarValor;
    private boolean ignorarAltPosterior;
    private boolean alterarIncidencia;
    private boolean restaurarIncidencia;
    private boolean bloquearServidor;
    private boolean desbloquearServidor;
    private boolean ajustaConsignacoesMargem;
    private boolean bloquearRegistroServidor;
    private boolean desbloquearRegistroServidor;
    private String motivoBloqueioRegistroServidor;
    private boolean adequarMargemServidor;
    private List<String> adeCodigosNaoSelecionados;
    private List<Short> marCodigosSelecionados;

    // Atributos para dados da decisão judicial
    private String tjuCodigo;
    private String cidCodigo;
    private String djuNumProcesso;
    private Date djuData;
    private String djuTexto;

    public List<String> getAdeCodigos() {
        return adeCodigos;
    }

    public void setAdeCodigos(List<String> adeCodigos) {
        this.adeCodigos = adeCodigos;
    }

    public String getTmoCodigo() {
        return tmoCodigo;
    }

    public void setTmoCodigo(String tmoCodigo) {
        this.tmoCodigo = tmoCodigo;
    }

    public String getOcaObs() {
        return ocaObs;
    }

    public void setOcaObs(String ocaObs) {
        this.ocaObs = ocaObs;
    }

    public BigDecimal getVlrTotalNovo() {
        return vlrTotalNovo;
    }

    public void setVlrTotalNovo(BigDecimal vlrTotalNovo) {
        this.vlrTotalNovo = vlrTotalNovo;
    }

    public BigDecimal getPercentualMargem() {
        return percentualMargem;
    }

    public void setPercentualMargem(BigDecimal percentualMargem) {
        this.percentualMargem = percentualMargem;
    }

    public Short getMarCodigo() {
        return marCodigo;
    }

    public void setMarCodigo(Short marCodigo) {
        this.marCodigo = marCodigo;
    }

    public boolean isAlterarPrazo() {
        return alterarPrazo;
    }

    public void setAlterarPrazo(boolean alterarPrazo) {
        this.alterarPrazo = alterarPrazo;
    }

    public boolean isRestaurarValor() {
        return restaurarValor;
    }

    public void setRestaurarValor(boolean restaurarValor) {
        this.restaurarValor = restaurarValor;
    }

    public boolean isIgnorarAltPosterior() {
        return ignorarAltPosterior;
    }

    public void setIgnorarAltPosterior(boolean ignorarAltPosterior) {
        this.ignorarAltPosterior = ignorarAltPosterior;
    }

    public boolean isAlterarIncidencia() {
        return alterarIncidencia;
    }

    public void setAlterarIncidencia(boolean alterarIncidencia) {
        this.alterarIncidencia = alterarIncidencia;
    }

    public boolean isRestaurarIncidencia() {
        return restaurarIncidencia;
    }

    public void setRestaurarIncidencia(boolean restaurarIncidencia) {
        this.restaurarIncidencia = restaurarIncidencia;
    }

    public boolean isBloquearServidor() {
        return bloquearServidor;
    }

    public void setBloquearServidor(boolean bloquearServidor) {
        this.bloquearServidor = bloquearServidor;
    }

    public boolean isDesbloquearServidor() {
        return desbloquearServidor;
    }

    public void setDesbloquearServidor(boolean desbloquearServidor) {
        this.desbloquearServidor = desbloquearServidor;
    }

    public boolean isAjustaConsignacoesMargem() {
        return ajustaConsignacoesMargem;
    }

    public void setAjustaConsignacoesMargem(boolean ajustarConsignacoesMargem) {
        ajustaConsignacoesMargem = ajustarConsignacoesMargem;
    }

    public String getTjuCodigo() {
        return tjuCodigo;
    }

    public void setTjuCodigo(String tjuCodigo) {
        this.tjuCodigo = tjuCodigo;
    }

    public String getCidCodigo() {
        return cidCodigo;
    }

    public void setCidCodigo(String cidCodigo) {
        this.cidCodigo = cidCodigo;
    }

    public String getDjuNumProcesso() {
        return djuNumProcesso;
    }

    public void setDjuNumProcesso(String djuNumProcesso) {
        this.djuNumProcesso = djuNumProcesso;
    }

    public Date getDjuData() {
        return djuData;
    }

    public void setDjuData(Date djuData) {
        this.djuData = djuData;
    }

    public String getDjuTexto() {
        return djuTexto;
    }

    public void setDjuTexto(String djuTexto) {
        this.djuTexto = djuTexto;
    }

    public boolean isBloquearRegistroServidor() {
        return bloquearRegistroServidor;
    }

    public void setBloquearRegistroServidor(boolean bloquearRegistroServidor) {
        this.bloquearRegistroServidor = bloquearRegistroServidor;
    }

    public boolean isDesbloquearRegistroServidor() {
        return desbloquearRegistroServidor;
    }

    public void setDesbloquearRegistroServidor(boolean desbloquearRegistroServidor) {
        this.desbloquearRegistroServidor = desbloquearRegistroServidor;
    }

    public String getMotivoBloqueioRegistroServidor() {
        return motivoBloqueioRegistroServidor;
    }

    public void setMotivoBloqueioRegistroServidor(String motivoBloqueioRegistroServidor) {
        this.motivoBloqueioRegistroServidor = motivoBloqueioRegistroServidor;
    }

    public boolean isAdequarMargemServidor() {
        return adequarMargemServidor;
    }

    public void setAdequarMargemServidor(boolean adequarMargemServidor) {
        this.adequarMargemServidor = adequarMargemServidor;
    }

    public List<String> getAdeCodigosNaoSelecionados() {
		return adeCodigosNaoSelecionados;
	}

	public void setAdeCodigosNaoSelecionados(List<String> adeCodigosNaoSelecionados) {
		this.adeCodigosNaoSelecionados = adeCodigosNaoSelecionados;
	}

	@SuppressWarnings("unchecked")
    public void setarCampos(Map<String, Object> mapaCampos) throws ParseException {
        setAdeCodigos((List<String>) mapaCampos.get("adeCodigos"));
        if (mapaCampos.get("alterarIncidencia") != null) {
            setAlterarIncidencia((Boolean) mapaCampos.get("alterarIncidencia"));
        }
        if (mapaCampos.get("alterarPrazo") != null) {
            setAlterarPrazo((Boolean) mapaCampos.get("alterarPrazo"));
        }
        if (mapaCampos.get("bloquearServidor") != null) {
            setBloquearServidor((Boolean) mapaCampos.get("bloquearServidor"));
        }
        setCidCodigo((String) mapaCampos.get("cidCodigo"));
        if (mapaCampos.get("desbloquearServidor") != null) {
            setDesbloquearServidor((Boolean) mapaCampos.get("desbloquearServidor"));
        }
        if (mapaCampos.get("djuData") != null) {
            setDjuData(DateHelper.parse((String) mapaCampos.get("djuData"), LocaleHelper.getDatePattern()) );
        }
        setDjuNumProcesso((String) mapaCampos.get("djuNumProcesso"));
        setDjuTexto((String) mapaCampos.get("djuTexto"));
        setIgnorarAltPosterior((Boolean) mapaCampos.get("ignorarAltPosterior"));
        if (mapaCampos.get("marCodigo") != null) {
            setMarCodigo(((Double) mapaCampos.get("marCodigo")).shortValue());
        }
        setOcaObs((String) mapaCampos.get("ocaObs"));
        if (mapaCampos.get("restaurarIncidencia") != null) {
            setRestaurarIncidencia((Boolean) mapaCampos.get("restaurarIncidencia"));
        }
        if (mapaCampos.get("restaurarValor") != null) {
            setRestaurarValor((Boolean) mapaCampos.get("restaurarValor"));
        }
        setTjuCodigo((String) mapaCampos.get("tjuCodigo"));
        setTmoCodigo((String) mapaCampos.get("tmoCodigo"));
        if (mapaCampos.get("vlrTotalNovo") != null) {
            setVlrTotalNovo(BigDecimal.valueOf((Double) mapaCampos.get("vlrTotalNovo")));
        }
        if (mapaCampos.get("percentualMargem") != null) {
            setPercentualMargem(BigDecimal.valueOf((Double) mapaCampos.get("percentualMargem")));
        }
        if (mapaCampos.get("bloquearRegistroServidor") != null) {
            setBloquearRegistroServidor((Boolean) mapaCampos.get("bloquearRegistroServidor"));
        }
        if (mapaCampos.get("desbloquearRegistroServidor") != null) {
            setDesbloquearRegistroServidor((Boolean) mapaCampos.get("desbloquearRegistroServidor"));
        }
        if (mapaCampos.get("motivoBloqueioRegistroServidor") != null) {
            //Devido ao base64 tratado no web controller que remove os espaços a marcação dos espaços foi necessário para regitrar corretamente o bloqueio
            final String motivoBloqueio = (String) mapaCampos.get("motivoBloqueioRegistroServidor");
            setMotivoBloqueioRegistroServidor(motivoBloqueio.replace("_!_", " "));
        }
        if (mapaCampos.get("adequarMargemServidor") != null) {
            setAdequarMargemServidor((Boolean) mapaCampos.get("adequarMargemServidor"));
        }
        if (mapaCampos.get("marCodigosSelecionados") != null) {
            setMarCodigosSelecionados((List<Short>) mapaCampos.get("marCodigosSelecionados"));
        }
    }

    public List<Short> getMarCodigosSelecionados() {
        return marCodigosSelecionados;
    }

    public void setMarCodigosSelecionados(List<Short> marCodigosSelecionados) {
        this.marCodigosSelecionados = marCodigosSelecionados;
    }
}