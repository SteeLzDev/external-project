package com.zetra.econsig.helper.financeiro;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.SimulacaoControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

public interface SimuladorCustomizado {

    List<TransferObject> simularConsignacao(String csaCodigo, String svcCodigo, String orgCodigo, String rseCodigo, BigDecimal vlrParcela, BigDecimal vlrLiberado, short przVlr, Date adeAnoMesIni, boolean validaBloqSerCnvCsa, boolean utilizaLimiteTaxa, String adePeridiocidade, AcessoSistema responsavel) throws SimulacaoControllerException;
}
