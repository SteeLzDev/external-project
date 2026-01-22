-- DESENV-14619
UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 2 WHERE REL_CODIGO = 'mov_mes' AND RFI_SEQUENCIA >= 6;

UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 2 WHERE REL_CODIGO = 'consignacoes' AND RFI_SEQUENCIA >= 6;

-- SUB ORGÃO

INSERT INTO tb_tipo_filtro_relatorio
(TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES('campo_sub_orgao', 'Sub-órgão', '/relatorios/campos_relatorio/campo_sub_orgao.jsp', 'S');

INSERT INTO tb_relatorio_filtro
(REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('mov_mes', 'campo_sub_orgao', '1', '0', '0', '1', '0', 6, NULL, '1');

INSERT INTO tb_relatorio_filtro
(REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('consignacoes', 'campo_sub_orgao', '1', '0', '0', '1', '0', 6, NULL, '1');

-- UNIDADE

INSERT INTO tb_tipo_filtro_relatorio
(TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES('campo_unidade', 'Unidade', '/relatorios/campos_relatorio/campo_unidade.jsp', 'S');

INSERT INTO tb_relatorio_filtro
(REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('mov_mes', 'campo_unidade', '1', '0', '0', '1', '0', 7, NULL, '1');

INSERT INTO tb_relatorio_filtro
(REL_CODIGO, TFR_CODIGO, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO, RFI_EXIBE_SUP)
VALUES('consignacoes', 'campo_unidade', '1', '0', '0', '1', '0', 7, NULL, '1');