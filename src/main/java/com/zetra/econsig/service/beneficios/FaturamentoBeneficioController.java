package com.zetra.econsig.service.beneficios;

import java.util.List;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.exception.FaturamentoBeneficioControllerException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.persistence.entity.FaturamentoBeneficioNf;
import com.zetra.econsig.persistence.entity.TipoLancamento;

/**
 * <p>Title: FaturamentoBeneficioController</p>
 * <p>Description: Controller para faturamento de beneficios</p>
 * <p>Copyright: Copyright (c) 2012</p>
 * <p>Company: ZetraSoft</p>
 * $Author: tadeu.cruz $
 * $Revision: 25571 $
 * $Date: 2018-10-10 13:59:39 -0300 (Qua, 10 out 2018) $
 */

public interface FaturamentoBeneficioController {

    public List<TransferObject> findFaturamento(CustomTransferObject criterio, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException;

    public String gerarArquivoFaturamentoPrincipal(String fatCodigo, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException;

    public String validarPreviaFaturamento(String fatCodigo, List<String> arquivosPrevia, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException;

    public List<TransferObject> findArquivosFaturamento(CustomTransferObject criterio, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException;

    public int countArquivosFaturamento(CustomTransferObject criterio, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException;

    public TransferObject salvarArquivoFaturamento(TransferObject af, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException;

    public List<TipoLancamento> listarTipoLancamento(AcessoSistema responsavel) throws FaturamentoBeneficioControllerException;

    public void excluirArquivoFaturamento(Integer afbCodigo, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException;

    public List<FaturamentoBeneficioNf> listarFaturamentoBeneficioNfPorIdFaturamentoBeneficio(String fatCodigo, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException;

    public FaturamentoBeneficioNf salvarFaturamentoBeneficioNf(FaturamentoBeneficioNf faturamentoBeneficioNf, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException;

    public FaturamentoBeneficioNf findFaturamentoBeneficioNf(String fnfCodigo, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException;

    FaturamentoBeneficioNf excluirFaturamentoBeneficioNf(String fnfCodigo, AcessoSistema responsavel) throws FaturamentoBeneficioControllerException;
}
