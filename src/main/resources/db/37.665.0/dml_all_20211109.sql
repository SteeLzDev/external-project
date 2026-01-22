-- DESENV-17000
INSERT tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO) 
VALUES ('campo_grupo_jira', 'Grupo', '/relatorios/campos_relatorio/campo_grupo_jira.jsp', 'N');

UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1 WHERE REL_CODIGO = 'solicitacao_suporte' AND RFI_SEQUENCIA >= 2;

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP) 
VALUES ('solicitacao_suporte', 'campo_grupo_jira', 1, 0, 0, 0, 0, 2, NULL, 1);
