package com.zetra.econsig.service.pontuacao;

import java.util.List;

import com.zetra.econsig.exception.ZetraException;
import com.zetra.econsig.helper.seguranca.AcessoSistema;

public interface PontuacaoServidorController {

    public void calcularPontuacao(String rseCodigo, AcessoSistema responsavel) throws ZetraException;

    public void calcularPontuacao(String tipoEntidade, List<String> entCodigos, AcessoSistema responsavel) throws ZetraException;

    public String consultarPontuacaoCsa(String rseCodigo, AcessoSistema responsavel) throws ZetraException;
}
