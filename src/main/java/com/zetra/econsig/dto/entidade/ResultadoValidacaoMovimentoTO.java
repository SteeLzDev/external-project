package com.zetra.econsig.dto.entidade;
import java.io.File;
import java.util.Date;

import com.zetra.econsig.dto.CustomTransferObject;
import com.zetra.econsig.dto.TransferObject;
import com.zetra.econsig.values.Columns;

/**
 * <p>Title: ResultadoValidacaoMovimentoTO</p>
 * <p>Description: Transfer Object do Resultado Validacao Movimento</p>
 * <p>Copyright: Copyright (c) 2003-2006</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */

public class ResultadoValidacaoMovimentoTO extends CustomTransferObject {

    public ResultadoValidacaoMovimentoTO() {
        super();
    }

    public ResultadoValidacaoMovimentoTO(String rvaCodigo) {
        this();
        setAttribute(Columns.RVA_CODIGO, rvaCodigo);
    }

    public ResultadoValidacaoMovimentoTO(TransferObject to) {
        this();
        setAtributos(to.getAtributos());
    }

    // Getter
    public String getRvaCodigo() {
        return (String) getAttribute(Columns.RVA_CODIGO);
    }

    public String getUsuCodigo() {
        return (String) getAttribute(Columns.RVA_USU_CODIGO);
    }

    public String getRvaNomeArquivo() {
        return (String) getAttribute(Columns.RVA_NOME_ARQUIVO);
    }

    public String getRvaNomeArquivoFormatado() {
        final String nomeArq = (String) getAttribute(Columns.RVA_NOME_ARQUIVO);
        final File arq = new File(nomeArq);
        return arq.getName();
    }

    public Date getRvaPeriodo() {
        return (Date) getAttribute(Columns.RVA_PERIODO);
    }

    public String getRvaResultado() {
        return (String) getAttribute(Columns.RVA_RESULTADO);
    }

    public Boolean getRvaAceite() {
        return (Boolean) getAttribute(Columns.RVA_ACEITE);
    }

    public Date getRvaDataProcesso() {
        return (Date) getAttribute(Columns.RVA_DATA_PROCESSO);
    }

    public Date getRvaDataAceite() {
        return (Date) getAttribute(Columns.RVA_DATA_ACEITE);
    }

    // Setter
    public void setUsuCodigo(String usuCodigo) {
        setAttribute(Columns.RVA_USU_CODIGO, usuCodigo);
    }

    public void setRvaNomeArquivo(String rvaNomeArquivo) {
        setAttribute(Columns.RVA_NOME_ARQUIVO, rvaNomeArquivo);
    }

    public void setRvaPeriodo(Date rvaPeriodo) {
        setAttribute(Columns.RVA_PERIODO, rvaPeriodo);
    }

    public void setRvaResultado(String rvaResultado) {
        setAttribute(Columns.RVA_RESULTADO, rvaResultado);
    }

    public void setRvaAceite(Boolean rvaAceite) {
        setAttribute(Columns.RVA_ACEITE, rvaAceite);
    }

    public void setRvaDataProcesso(Date rvaDataProcesso) {
        setAttribute(Columns.RVA_DATA_PROCESSO, rvaDataProcesso);
    }

    public void setRvaDataAceite(Date rvaDataAceite) {
        setAttribute(Columns.RVA_DATA_ACEITE, rvaDataAceite);
    }
}
