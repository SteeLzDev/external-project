package com.zetra.econsig.values;

import com.zetra.econsig.helper.seguranca.AcessoSistema;
import com.zetra.econsig.helper.texto.ApplicationResourcesHelper;

/**
 * <p>Title: PeriodicidadeEmailAuditoriaEnum</p>
 * <p>Description: Definição de tipo de periodicidade de e-mail de auditoria.</p>
 * <p>Copyright: Copyright (c) 2005</p>
 * <p>Company: ZetraSoft</p>
 * $Author$
 * $Revision$
 * $Date$
 */
public enum PeriodicidadeEmailAuditoriaEnum {

    DIARIO (CodedValues.PER_ENV_EMAIL_AUDIT_DIARIO, "Diário"),
    SEMANAL (CodedValues.PER_ENV_EMAIL_AUDIT_SEMANAL, "Semanal"),
    MENSAL (CodedValues.PER_ENV_EMAIL_AUDIT_MENSAL, "Mensal"),
    NAO_HABILITADO (CodedValues.PER_ENV_EMAIL_AUDIT_DESABILITADO, "Não Habilitado");

    private final String chave;
    private final String descricao;
    PeriodicidadeEmailAuditoriaEnum(String chave, String descricao) {
        this.chave = chave;
        this.descricao = descricao;
    }

    /**
     * Recupera uma enumeração de periodicidade de envio de e-mail de auditoria de acordo com o código passado.
     *
     * @param codigo Código da periodicidade de e-mail de auditoria que deve ser recuperado.
     * @return Retorna uma periodicidade de e-mail de auditoria
     *
     * @throws IllegalArgumentException
     */
    public static PeriodicidadeEmailAuditoriaEnum recuperaPeriodicidadeEmailAuditoria(String codigo) {
        PeriodicidadeEmailAuditoriaEnum periodicidadeEmailAuditoria = null;

        for (PeriodicidadeEmailAuditoriaEnum tipo : PeriodicidadeEmailAuditoriaEnum.values()) {
            if (tipo.chave().equals(codigo)) {
                periodicidadeEmailAuditoria = tipo;
                break;
            }
        }

        if (periodicidadeEmailAuditoria == null) {
            throw new IllegalArgumentException(ApplicationResourcesHelper.getMessage("mensagem.erro.codigo.periodicidade.email.auditoria.invalido", (AcessoSistema) null));
        }

        return periodicidadeEmailAuditoria;
    }

    public String chave()   { return chave; }
    public String descricao() { return descricao; }
}
