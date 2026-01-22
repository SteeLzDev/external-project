-- DESENV-14093
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO) 
VALUES ('campo_prd_realizado', 'Valor realizado das parcelas', '/relatorios/campos_relatorio/campo_prd_realizado.jsp', 'N');

INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO) 
VALUES ('campo_agrupamento', 'Agrupamento', '/relatorios/campos_relatorio/campo_agrupamento.jsp', 'N');


INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('mov_mes', 'campo_prd_realizado', '1', '0', '0', '1', '0', 9, null, '1'); 

UPDATE tb_relatorio_filtro  
SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1
WHERE TFR_CODIGO in ('campo_agendado', 'campo_data_execucao', 'campo_tipo_agendamento', 'campo_periodicidade', 'campo_envio_email', 'campo_formato_relatorio')
AND REL_CODIGO = 'mov_mes';


INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('mov_mes_csa', 'campo_prd_realizado', '0', '1', '0', '0', '0', 10, null, '0');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('mov_mes_csa', 'campo_agrupamento', '0', '1', '0', '0', '0', 11, null, '0');

UPDATE tb_relatorio_filtro  
SET RFI_SEQUENCIA = RFI_SEQUENCIA + 2
WHERE TFR_CODIGO in ('campo_formato_relatorio')
AND REL_CODIGO = 'mov_mes_csa';


INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('mov_mes_cor', 'campo_prd_realizado', '0', '0', '1', '0', '0', 10, null, '0');

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES ('mov_mes_cor', 'campo_agrupamento', '0', '0', '1', '0', '0', 11, null, '0');

UPDATE tb_relatorio_filtro  
SET RFI_SEQUENCIA = RFI_SEQUENCIA + 2
WHERE TFR_CODIGO in ('campo_formato_relatorio')
AND REL_CODIGO = 'mov_mes_cor';
