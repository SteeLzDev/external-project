package com.zetra.econsig.service.juros;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.DefinicaoTaxaJurosControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.DefinicaoTaxaJuros;

/**
 * <p>Title: DefinicaoTaxaJurosController</p>
 * <p>Description: Interface para Service Bean de operação de definição de regra de taxa de juros </p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: rodrigo.rosa $
 * $Revision: 25240 $
 * $Date: 2019-04-10 10:02:48 -0300 (qua, 10 abr 2019) $
 */
public interface DefinicaoTaxaJurosController {

    public List<TransferObject> listaDefinicaoRegraTaxaJuros(TransferObject criterio, int offset, int size, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException;

    public void update(DefinicaoTaxaJuros regraTaxaJurosExistente, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException;

    public void excluir(DefinicaoTaxaJuros definicaoTaxaJuros, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException;

    public int lstCountDefinicaoTaxaJuros(TransferObject criterio, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException;

    public DefinicaoTaxaJuros findDefinicaoByCodigo(String dtjCodigo, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException;

    public DefinicaoTaxaJuros create(String csaCodigo, String orgCodigo, String svcCodigo, String funCodigo, Short dtjFaixaEtariaIni, Short dtjFaixaEtariaFim, Short dtjFaixaTempServicoIni, Short dtjFaixaTempServicoFim, BigDecimal dtjFaixaSalarioIni, BigDecimal dtjFaixaSalarioFim, BigDecimal dtjFaixaMargemIni, BigDecimal dtjFaixaMargemFim, BigDecimal dtjFaixaValorTotalIni, BigDecimal dtjFaixaValorTotalFim, BigDecimal dtjFaixaValorContratoIni, BigDecimal dtjFaixaValorContratoFim, Short dtjFaixaPrazoIni, Short dtjFaixaPrazoFim, BigDecimal dtjTaxaJuros, BigDecimal dtjTaxaJurosMinima, Date dtjDataCadastro, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException;

    public void iniciarTabelaVigente(String csaCodigo, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException;

    public void excluirTabelaIniciada(String csaCodigo, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException;

    public void ativarTabelaIniciada(String csaCodigo, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException;

    public void modificaDataFimVigencia(String cftDataFimVigencia, List<TransferObject> lstRegraJurosSuperior, AcessoSistema responsavel) throws DefinicaoTaxaJurosControllerException;

}
