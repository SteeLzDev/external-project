package com.zetra.econsig.folha.exportacao;

import java.io.Serializable;
import java.util.List;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: ParametrosExportacao</p>
 * <p>Description: Classe auxiliar para guardar os valores da exportação em execução./p>
 * <p>Copyright: Copyright (c) 2013</p>
 * <p>Company: ZetraSoft Ltda.</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public class ParametrosExportacao implements Serializable {
    private static final long serialVersionUID = 2L;

    // exportar ou reexportar
    private AcaoEnum acao;

    // opção para geração dos arquivos
    private String opcao;

    // orgãos a serem exportados
    private List<String> orgCodigos;

    // estabelecimentos a serem  exportados
    private List<String> estCodigos;

    // verbas a serem exportadas
    private List<String> verbas;

    private AcessoSistema responsavel;

    //DESENV-19386 ao executar a rotina de exportação automatica por órgão, esse valor deve-ser passado como true,
    //pois mesmo que o parâmetro de sistema que verifica se a exportação é por órgão esteja habilitada a exportação deverá ser por órgão
    private boolean forcaExpPorOrgao = false;

    public String getAcao() {
        return acao == null ? null : acao.getCodigo();
    }

    public ParametrosExportacao setAcao(String acao) {
        this.acao = AcaoEnum.recuperaAcao(acao);
        return this;
    }

    public String getOpcao() {
        return opcao;
    }

    public ParametrosExportacao setOpcao(String opcao) {
        this.opcao = opcao;
        return this;
    }

    public List<String> getOrgCodigos() {
        return orgCodigos;
    }

    public ParametrosExportacao setOrgCodigos(List<String> orgCodigos) {
        this.orgCodigos = orgCodigos;
        return this;
    }

    public List<String> getEstCodigos() {
        return estCodigos;
    }

    public ParametrosExportacao setEstCodigos(List<String> estCodigos) {
        this.estCodigos = estCodigos;
        return this;
    }

    public List<String> getVerbas() {
        return verbas;
    }

    public ParametrosExportacao setVerbas(List<String> verbas) {
        this.verbas = verbas;
        return this;
    }

    public AcessoSistema getResponsavel() {
        return responsavel;
    }

    public ParametrosExportacao setResponsavel(AcessoSistema responsavel) {
        this.responsavel = responsavel;
        return this;
    }

    public boolean isForcaExpPorOrgao() {
        return forcaExpPorOrgao;
    }

    public void setForcaExpPorOrgao(boolean forcaExpPorOrgao) {
        this.forcaExpPorOrgao = forcaExpPorOrgao;
    }

    public enum AcaoEnum {
        EXPORTAR("exportar"),
        REEXPORTAR("reexportar");

        private final String codigo;

        private AcaoEnum(String codigo) {
            this.codigo = codigo;
        }

        public String getCodigo() {
            return codigo;
        }

        public static AcaoEnum recuperaAcao(String codigo) {
            AcaoEnum acao = null;

            for (AcaoEnum aca : AcaoEnum.values()) {
                if (aca.getCodigo().equals(codigo)) {
                    acao = aca;
                    break;
                }
            }

            if (acao == null) {
                throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.informado.acao.invalido", (AcessoSistema) null));
            }

            return acao;
        }


        public final boolean equals(AcaoEnum other) {
            return (this==other || getCodigo().equals(other.getCodigo()));
        }
    }
}
