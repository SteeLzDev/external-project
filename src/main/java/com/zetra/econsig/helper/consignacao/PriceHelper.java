package com.zetra.econsig.helper.consignacao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.zetra.econsig.exception.ViewHelperException;
import com.zetra.econsig.helper.financeiro.SimulacaoMetodoMexicano;
import com.zetra.econsig.helper.parametro.ParamSist;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.TextHelper;
import com.zetra.econsig.values.CodedValues;

/**
 * <p>Title: PriceHelper</p>
 * <p>Description: Helper para cálculo da tabela price</p>
 * <p>Copyright: Copyright (c) 2008</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class PriceHelper {

    static BigDecimal CEM = new BigDecimal(100);

    public static TabelaPrice calcula(BigDecimal valorLiquidoLiberado, BigDecimal prestacao, int qtdePrestacoes, BigDecimal taxaJuros, String orgCodigo, AcessoSistema responsavel) throws ViewHelperException {
        // Veja que os juros são calculados de acordo com o valor solicitado, na parcela de número 1 temos: 20.000 x 4% = 800 (valorLiquido x taxaJuros = juros).
        // A amortização é calculada subtraindo o valor da prestação do valor do juros: 2.970,56 - 800 = 2.170,56. (prestacao - juros = amortizacao)
        // No método mexicano, é considerado também o IVA, sendo portanto amortizao = prestacao - juros - iva
        // O saldo devedor da parcela 1 é calculado subtraindo: 20.000 - 2.170,56 = 17.829,44 (valorLiquido - prestacao = valorLiquidoRestante)
        // E assim respectivamente, até a quitação total do financiamento.

        BigDecimal aliquotaIva = BigDecimal.ZERO;
        BigDecimal taxaJurosSemIva = BigDecimal.ZERO;
        BigDecimal taxaJurosAnual = BigDecimal.ZERO;

        Object metodo = ParamSist.getInstance().getParam(CodedValues.TPC_METODO_CALCULO_SIMULACAO, responsavel);
        boolean metodoMexicano = !TextHelper.isNull(metodo) && CodedValues.MCS_MEXICANO.equals(metodo);
        boolean metodoIndiano = !TextHelper.isNull(metodo) && CodedValues.MCS_INDIANO.equals(metodo);
        if (metodoMexicano) {
            Double irr = SimulacaoMetodoMexicano.IRR(valorLiquidoLiberado, prestacao, qtdePrestacoes);
            taxaJuros = !irr.isNaN() ? BigDecimal.valueOf(irr) : BigDecimal.ZERO;
            aliquotaIva = new BigDecimal((String)ParamSist.getInstance().getParam(CodedValues.TPC_ALIQUOTA_ANUAL_IOF, responsavel));
            taxaJurosSemIva = taxaJuros.multiply(CEM.divide(aliquotaIva.add(CEM), 5, RoundingMode.HALF_UP));
        } else if (metodoIndiano) {
            //taxas indianas são cadastros anuais. Então, faz-se a divisão por 12 para se ter a taxa mensal
            taxaJurosAnual = taxaJuros;
            taxaJuros = taxaJuros.divide(new BigDecimal("12"), 6, java.math.RoundingMode.HALF_UP);
        }

        // Monta objeto de retorno
        TabelaPrice tabelaPrice = new TabelaPrice();
        tabelaPrice.setValorLiquidoLiberado(valorLiquidoLiberado);
        tabelaPrice.setPrestacao(prestacao);
        tabelaPrice.setPrazo(qtdePrestacoes);
        //DESENV-9830: Para Índia será exibida a taxa anual original
        tabelaPrice.setTaxaJuros(metodoIndiano ? taxaJurosAnual : taxaJuros);
        tabelaPrice.setTaxaJurosSemIva(taxaJurosSemIva);
        tabelaPrice.setTaxaIva(aliquotaIva);

        List<Parcela> parcelas = new ArrayList<Parcela>();

        BigDecimal juros = BigDecimal.ZERO;
        BigDecimal iva = BigDecimal.ZERO;
        BigDecimal amortizacao = BigDecimal.ZERO;

        int contador = 0;
        while (contador++ < qtdePrestacoes) {
            if (metodoMexicano) {
                juros = valorLiquidoLiberado.multiply(taxaJurosSemIva).divide(CEM);
                iva = juros.multiply(aliquotaIva.divide(CEM));
                amortizacao = prestacao.subtract(juros).subtract(iva);
                if (contador == qtdePrestacoes) {
                    BigDecimal saldoFinal = valorLiquidoLiberado.subtract(amortizacao);
                    amortizacao = amortizacao.add(saldoFinal);
                }
            } else {
                juros = valorLiquidoLiberado.multiply(taxaJuros).divide(CEM);
                amortizacao = prestacao.subtract(juros);
            }

            valorLiquidoLiberado = valorLiquidoLiberado.subtract(amortizacao);
            if (valorLiquidoLiberado.compareTo(BigDecimal.ZERO) < 0) {
                valorLiquidoLiberado = BigDecimal.ZERO;
            }

            if (metodoMexicano) {
                parcelas.add(new Parcela(contador, prestacao, juros, iva, amortizacao, valorLiquidoLiberado));
            } else {
                parcelas.add(new Parcela(contador, prestacao, juros, amortizacao, valorLiquidoLiberado));
            }
        }

        // Ordena parcelas
        Collections.sort(parcelas);
        tabelaPrice.setParcelas(parcelas);

        return tabelaPrice;
    }

    public static class Parcela implements Serializable, Comparable<Parcela> {
        int sequencia;
        BigDecimal juros;
        BigDecimal iva;
        BigDecimal amortizacao;
        BigDecimal saldoDevedor;
        BigDecimal prestacao;

        public Parcela(int sequencia, BigDecimal prestacao, BigDecimal juros, BigDecimal amortizacao, BigDecimal saldoDevedor) {
            this.sequencia = sequencia;
            this.prestacao = prestacao;
            this.juros = juros;
            this.amortizacao = amortizacao;
            this.saldoDevedor = saldoDevedor;
        }

        public Parcela(int sequencia, BigDecimal prestacao, BigDecimal juros, BigDecimal iva, BigDecimal amortizacao, BigDecimal saldoDevedor) {
            this.sequencia = sequencia;
            this.prestacao = prestacao;
            this.juros = juros;
            this.iva = iva;
            this.amortizacao = amortizacao;
            this.saldoDevedor = saldoDevedor;
        }

        public int getSequencia() {
            return sequencia;
        }

        public void setSequencia(int sequencia) {
            this.sequencia = sequencia;
        }

        public BigDecimal getPrestacao() {
            return prestacao;
        }

        public void setPrestacao(BigDecimal prestacao) {
            this.prestacao = prestacao;
        }

        public BigDecimal getJuros() {
            return juros;
        }

        public void setJuros(BigDecimal juros) {
            this.juros = juros;
        }

        public BigDecimal getIva() {
            return iva;
        }

        public void setIva(BigDecimal iva) {
            this.iva = iva;
        }

        public BigDecimal getAmortizacao() {
            return amortizacao;
        }

        public void setAmortizacao(BigDecimal amortizacao) {
            this.amortizacao = amortizacao;
        }

        public BigDecimal getSaldoDevedor() {
            return saldoDevedor;
        }

        public void setSaldoDevedor(BigDecimal saldoDevedor) {
            this.saldoDevedor = saldoDevedor;
        }

        @Override
        public String toString() {
            return "[sequencia: " + sequencia + ", " +
                    "prestacao: " + prestacao.setScale(2, java.math.RoundingMode.HALF_UP).doubleValue() + ", " +
                    "juros: " + juros.setScale(2, java.math.RoundingMode.HALF_UP).doubleValue() + ", " +
                    (!TextHelper.isNull(iva) ? "iva: " + iva.setScale(2, java.math.RoundingMode.HALF_UP).doubleValue() + ", " : "") +
                    "amortizacao: " + amortizacao.setScale(2, java.math.RoundingMode.HALF_UP).doubleValue() + ", " +
                    "saldoDevedor: " + saldoDevedor.setScale(2, java.math.RoundingMode.HALF_UP).doubleValue() + " " +
                    "]";
        }

        @Override
        public int compareTo(Parcela another) {
            if (getSequencia() < another.getSequencia()){
                return -1;
            } else {
                return 1;
            }
        }
    }

    public static class TabelaPrice implements Serializable {

        BigDecimal valorLiquidoLiberado;
        BigDecimal prestacao;
        Integer prazo;
        BigDecimal taxaJuros;
        BigDecimal taxaJurosSemIva;
        BigDecimal taxaIva;

        List<Parcela> parcelas;

        public BigDecimal getValorLiquidoLiberado() {
            return valorLiquidoLiberado;
        }

        public void setValorLiquidoLiberado(BigDecimal valorLiquidoLiberado) {
            this.valorLiquidoLiberado = valorLiquidoLiberado;
        }

        public BigDecimal getPrestacao() {
            return prestacao;
        }

        public void setPrestacao(BigDecimal prestacao) {
            this.prestacao = prestacao;
        }

        public Integer getPrazo() {
            return prazo;
        }

        public void setPrazo(Integer prazo) {
            this.prazo = prazo;
        }

        public BigDecimal getTaxaJuros() {
            return taxaJuros;
        }

        public void setTaxaJuros(BigDecimal taxaJuros) {
            this.taxaJuros = taxaJuros;
        }

        public BigDecimal getTaxaJurosSemIva() {
            return taxaJurosSemIva;
        }

        public void setTaxaJurosSemIva(BigDecimal taxaJurosSemIva) {
            this.taxaJurosSemIva = taxaJurosSemIva;
        }

        public BigDecimal getTaxaIva() {
            return taxaIva;
        }

        public void setTaxaIva(BigDecimal taxaIva) {
            this.taxaIva = taxaIva;
        }

        public List<Parcela> getParcelas() {
            return parcelas;
        }

        public void setParcelas(List<Parcela> parcelas) {
            this.parcelas = parcelas;
        }

        public BigDecimal getTotalPrestacao() {
            return prestacao.multiply(new BigDecimal(prazo));
        }
    }

}
