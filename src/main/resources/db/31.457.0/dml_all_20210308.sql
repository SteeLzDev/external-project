-- DESENV-15412
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1 WHERE REL_CODIGO = 'conf_cad_margem' AND RFI_SEQUENCIA >= 8;

INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES ('campo_percentual_variacao_margem', 'Percentual de Variação da Margem', '/relatorios/campos_relatorio/campo_percentual_variacao_margem.jsp', 'S');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('conf_cad_margem', 'campo_percentual_variacao_margem', '1', '0', '0', '1', '0', 8, NULL, '1');
