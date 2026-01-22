package com.zetra.econsig.values;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zetra.econsig.helper.texto.TextHelper;

/**
 * <p>Title: ComposicaoMargemEnum</p>
 * <p>Description: Enumeração para composição de margem.</p>
 * <p>Copyright: Copyright (c) 2019</p>
 * <p>Company: ZetraSoft</p>
 * $Author: marlon.silva $
 * $Revision: 26978 $
 * $Date: 2019-06-18 12:11:00 -0300 (ter, 18 jun 2019) $
 */

public enum VencimentoEnum {

    PROVENTO_BASE("001", null),
    IRPF("002", Arrays.asList(
            new Desconto(0.00, 1903.98, 0.00),
            new Desconto(1903.99, 2826.65, 7.50),
            new Desconto(2826.66, 3751.05, 15.00),
            new Desconto(3751.06, 4664.68, 22.50),
            new Desconto(4664.69, null, 27.50)
            )),
    INSS("003", Arrays.asList(
            new Desconto(0.00, 1212.00, 7.50),
            new Desconto(1212.01, 2427.35, 9.00),
            new Desconto(2427.36, 3641.03, 12.00),
            new Desconto(3641.04, 7087.22, 14.00),
            new Desconto(7087.22, null, 14.00)
            )),
    FGTS("004", null);

    private String codigo;
    private List<Desconto> descontos;

    private VencimentoEnum(String codigo, List<Desconto> descontos) {
        this.codigo = codigo;
        this.descontos = descontos;
    }

    public String getCodigo() {
        return codigo;
    }

    public BigDecimal getValor(BigDecimal valorProvento) {
        if (descontos != null) {
            Desconto desconto = descontos.stream().filter(d -> valorProvento.compareTo(d.getMinimo()) > 0 && (TextHelper.isNull(d.getMaximo()) || valorProvento.compareTo(d.getMaximo()) <= 0)).findAny().orElse(null);

            if (desconto == null || desconto.getTaxa().equals(BigDecimal.ZERO)) {
                return BigDecimal.ZERO;
            }

            return valorProvento.multiply(desconto.getTaxa()).divide(new BigDecimal(100));
        } else {
            return valorProvento;
        }
    }

    // Reverse-lookup map para buscar uma composição de margem através de um codigo
    private static final Map<String, VencimentoEnum> lookup = new HashMap<String, VencimentoEnum>();

    static {
        for (VencimentoEnum c : VencimentoEnum.values()) {
            lookup.put(c.getCodigo(), c);
        }
    }

    public static VencimentoEnum get(String codigo) {
        return lookup.get(codigo);
    }

}

class Desconto {

    private BigDecimal minimo = BigDecimal.ZERO;
    private BigDecimal maximo = BigDecimal.ZERO;
    private BigDecimal taxa = BigDecimal.ZERO;

    public Desconto(Double minimo, Double maximo, Double taxa) {
        this.minimo = new BigDecimal(minimo);
        this.maximo = !TextHelper.isNull(maximo) ? new BigDecimal(maximo) : null;
        this.taxa = new BigDecimal(taxa);
    }

    public BigDecimal getMinimo() {
        return minimo;
    }

    public BigDecimal getMaximo() {
        return maximo;
    }

    public BigDecimal getTaxa() {
        return taxa;
    }
}
