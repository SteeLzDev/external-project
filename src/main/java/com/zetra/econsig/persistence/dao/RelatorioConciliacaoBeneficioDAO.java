package com.zetra.econsig.persistence.dao;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Title: RelatorioConciliacaoBeneficioDAO</p>
 * <p>Description: Interface do DAO do relatorio de conciliacao do modulo Beneficio</p>
 * <p>Copyright: Copyright (c) 2002-2018</p>
 * <p>Company: Nostrum Consultoria e Projetos</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public interface RelatorioConciliacaoBeneficioDAO {

    public int getTotalContratoAtivosMensalidadePlanoSaude();

    public int getTotalContratoAtivosMensalidadeOdotologico();

    public List<String> getListaContratoNoSistemaNaoConciliacaoPlanoSaude();

    public List<String> getListaContratoNaoSistemaNoConciliacaoPlanoSaude();

    public List<String> getListaContratoNoSistemaNaoConciliacaoOdontologico();

    public List<String> getListaContratoNaoSistemaNoConciliacaoOdontologico();

    public void adcionaLinhaParaRelatorio(String rseMatricula, String bfcCpf, String cbeNumero, String tipoLancamento, BigDecimal adeVlr, boolean mapeado);

    public void executa(String csaCodigo);
}
