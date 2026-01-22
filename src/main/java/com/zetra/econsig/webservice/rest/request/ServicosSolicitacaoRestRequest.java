package com.zetra.econsig.webservice.rest.request;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ServicosSolicitacaoRestRequest {

    public String svc_codigo;
    public String svc_descricao;
    public String svc_identificador;
    public String nse_codigo;
    public boolean indeterminado;
    public List<?> prazos;
    //param serviço 3
    public String incMargem;
    //param serviço 4
    public String tipoValor;
    //param serviço 5
    public String valorPadrao;
    //param serviço 6
    public boolean valorFixo;
    //param serviço 7
    public String maxPrazo;
    //param serviço 7, mas geral na natureza de serviço
    public String maxPrazoNse;
    //param serviço 53
    public boolean prazoFixo;
    //param serviço 48
    public boolean exigeTaxaJuros;
    //param serviço 54
    public boolean exigeInfoBancaria;
    //param serviço 66
    public boolean exigeSeguroPrestamista;
    //param serviço 97
    public boolean possuiCorrecaoValorPresente;
    //param serviço 107
    public boolean cadastraValorTac;
    //param serviço 108
    public boolean cadastraValorIof;
    //param serviço 109
    public boolean cadastraValorLiquidoLiberado;
    //param serviço 110
    public boolean cadastraValorMensalidadeVinc;
    //param serviço 116
    public boolean validaDataNascimentoReserva;
    public boolean senhaServidorObrigatoriaReserva;

    public String exibeCidadeConfirmacaoSolicitacao;
    //param serviço 119
    public BigDecimal maxAdeVlr;
    //param serviço 118
    public BigDecimal minAdeVlr;
    //param serviço 323
    public boolean exigeReconhecimentoFacialServidorSolicitacao;

}
