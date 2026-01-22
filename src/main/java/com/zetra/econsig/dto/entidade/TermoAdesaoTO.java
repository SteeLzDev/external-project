package com.zetra.econsig.dto.entidade;

import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: TermoAdesaoTO</p>
 * <p>Description: Transfer Object da tabela de termo de adesao.</p>
 * <p>Copyright: Copyright (c) 2007</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class TermoAdesaoTO extends CustomTransferObject {

    public TermoAdesaoTO() {
    }

    public TermoAdesaoTO(String tadCodigo) {
        setAttribute(Columns.TAD_CODIGO, tadCodigo);
    }

    public TermoAdesaoTO(String tadCodigo, String usuCodigo, String funCodigo, String tadTitulo, String tadTexto, Date tadData,
    		Integer tadSequencia, String tadExibeCse, String tadExibeOrg, String tadExibeCsa, String tadExibeCor,
    		String tadExibeSer, String tadExibeSup, String tadHtml, String tadPermiteRecusar, String tadPermiteLerDepois, String tadClasseAcao, String tadExibeAposLeitura) {
        this();
        setAttribute(Columns.TAD_CODIGO, tadCodigo);
        setAttribute(Columns.TAD_USU_CODIGO, usuCodigo);
        setAttribute(Columns.TAD_FUN_CODIGO, funCodigo);
        setAttribute(Columns.TAD_TITULO, tadTitulo);
        setAttribute(Columns.TAD_TEXTO, tadTexto);
        setAttribute(Columns.TAD_DATA, tadData);
        setAttribute(Columns.TAD_SEQUENCIA, tadSequencia);
        setAttribute(Columns.TAD_EXIBE_CSE, tadExibeCse);
        setAttribute(Columns.TAD_EXIBE_ORG, tadExibeOrg);
        setAttribute(Columns.TAD_EXIBE_CSA, tadExibeCsa);
        setAttribute(Columns.TAD_EXIBE_COR, tadExibeCor);
        setAttribute(Columns.TAD_EXIBE_SER, tadExibeSer);
        setAttribute(Columns.TAD_EXIBE_SUP, tadExibeSup);
        setAttribute(Columns.TAD_HTML, tadHtml);
        setAttribute(Columns.TAD_PERMITE_RECUSAR, tadPermiteRecusar);
        setAttribute(Columns.TAD_PERMITE_LER_DEPOIS, tadPermiteLerDepois);
        setAttribute(Columns.TAD_CLASSE_ACAO, tadClasseAcao);
        setAttribute(Columns.TAD_EXIBE_APOS_LEITURA, tadExibeAposLeitura);
    }

    public TermoAdesaoTO(TermoAdesaoTO other) {
        this();
        setAtributos(other.getAtributos());
    }

    // Getter
    public String getTadCodigo() {
        return (String) getAttribute(Columns.TAD_CODIGO);
    }

    public String getUsuCodigo() {
        return (String) getAttribute(Columns.TAD_USU_CODIGO);
    }

    public String getFunCodigo() {
        return (String) getAttribute(Columns.TAD_FUN_CODIGO);
    }

    public String getTadTitulo() {
        return (String) getAttribute(Columns.TAD_TITULO);
    }

    public String getTadTexto() {
        return (String) getAttribute(Columns.TAD_TEXTO);
    }

    public Date getTadData() {
        return (Date) getAttribute(Columns.TAD_DATA);
    }

    public Integer getTadSequencia() {
        return (Integer) getAttribute(Columns.TAD_SEQUENCIA);
    }

    public String getTadExibeCse() {
        return (String) getAttribute(Columns.TAD_EXIBE_CSE);
    }

    public String getTadExibeOrg() {
        return (String) getAttribute(Columns.TAD_EXIBE_ORG);
    }

    public String getTadExibeCsa() {
        return (String) getAttribute(Columns.TAD_EXIBE_CSA);
    }

    public String getTadExibeCor() {
        return (String) getAttribute(Columns.TAD_EXIBE_COR);
    }

    public String getTadExibeSer() {
        return (String) getAttribute(Columns.TAD_EXIBE_SER);
    }

    public String getTadExibeSup() {
        return (String) getAttribute(Columns.TAD_EXIBE_SUP);
    }

    public String getTadHtml() {
        return (String) getAttribute(Columns.TAD_HTML);
    }

    public String getTadPermiteRecusar() {
        return (String) getAttribute(Columns.TAD_PERMITE_RECUSAR);
    }

    public String getTadPermiteLerDepois() {
        return (String) getAttribute(Columns.TAD_PERMITE_LER_DEPOIS);
    }

    public String getTadClasseAcao() {
        return (String) getAttribute(Columns.TAD_CLASSE_ACAO);
    }

    public String getTadExibeAposLeitura() {
        return (String) getAttribute(Columns.TAD_EXIBE_APOS_LEITURA);
    }

    // Setter
    public void setTadCodigo(String tadCodigo) {
        setAttribute(Columns.TAD_CODIGO, tadCodigo);
    }

    public void setUsuCodigo(String usuCodigo) {
        setAttribute(Columns.TAD_USU_CODIGO, usuCodigo);
    }

    public void setFunCodigo(String funCodigo) {
        setAttribute(Columns.TAD_FUN_CODIGO, funCodigo);
    }

    public void setTadTitulo(String tadTitulo) {
        setAttribute(Columns.TAD_TITULO, tadTitulo);
    }

    public void setTadTexto(String tadTexto) {
        setAttribute(Columns.TAD_TEXTO, tadTexto);
    }

    public void setTadData(Date tadData) {
        setAttribute(Columns.TAD_DATA, tadData);
    }

    public void setTadSequencia(Integer tadSequencia) {
        setAttribute(Columns.TAD_SEQUENCIA, tadSequencia);
    }

    public void setTadExibeCse(String tadExibeCse) {
        setAttribute(Columns.TAD_EXIBE_CSE, tadExibeCse);
    }

    public void setTadExibeOrg(String tadExibeOrg) {
        setAttribute(Columns.TAD_EXIBE_ORG, tadExibeOrg);
    }

    public void setTadExibeCsa(String tadExibeCsa) {
        setAttribute(Columns.TAD_EXIBE_CSA, tadExibeCsa);
    }

    public void setTadExibeCor(String tadExibeCor) {
        setAttribute(Columns.TAD_EXIBE_COR, tadExibeCor);
    }

    public void setTadExibeSer(String tadExibeSer) {
        setAttribute(Columns.TAD_EXIBE_SER, tadExibeSer);
    }

    public void setTadExibeSup(String tadExibeSup) {
        setAttribute(Columns.TAD_EXIBE_SUP, tadExibeSup);
    }

    public void setTadHtml(String tadHtml) {
        setAttribute(Columns.TAD_HTML, tadHtml);
    }

    public void setTadPermiteRecusar(String tadPermiteRecusar) {
        setAttribute(Columns.TAD_PERMITE_RECUSAR, tadPermiteRecusar);
    }

    public void setTadPermiteLerDepois(String tadPermiteLerDepois) {
        setAttribute(Columns.TAD_PERMITE_LER_DEPOIS, tadPermiteLerDepois);
    }

    public void setTadClasseAcao(String tadClasseAcao) {
        setAttribute(Columns.TAD_CLASSE_ACAO, tadClasseAcao);
    }

    public void setTadExibeAposLeitura(String tadExibeAposLeitura) {
        setAttribute(Columns.TAD_EXIBE_APOS_LEITURA, tadExibeAposLeitura);
    }
}