-- DESENV-21137
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 3 WHERE RFI_SEQUENCIA >= 5 AND REL_CODIGO = 'comprometimento';

INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES ('campo_compro_margem_sinal', 'Sinal da Margem', '/relatorios/campos_relatorio/campo_compro_margem_sinal.jsp', 'N');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('comprometimento', 'campo_compro_margem_sinal', '1', '0', '0', '0', '0', 5, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('comprometimento', 'campo_comprometimento_margem', '1', '0', '0', '0', '0', 6, NULL, '1');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('comprometimento', 'campo_percentual_variacao_margem', '1', '0', '0', '0', '0', 7, NULL, '1');

