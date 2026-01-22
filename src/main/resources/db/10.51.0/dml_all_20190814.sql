-- DESENV-12184
-- Campo campo_csa_permite_incluir_ade
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES ('campo_csa_permite_incluir_ade', 'Permite incluir novas consignações', '/relatorios/campos_relatorio/campo_csa_permite_incluir_ade.jsp', 'N');

UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1 WHERE REL_CODIGO = 'conf_cad_csa' AND RFI_SEQUENCIA > 1;

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('conf_cad_csa', 'campo_csa_permite_incluir_ade', '1', '0', '0', '0', '0', '0', 2, NULL);

-- Campo campo_csa_possui_ade_ativa
INSERT INTO tb_tipo_filtro_relatorio (TFR_CODIGO, TFR_DESCRICAO, TFR_RECURSO, TFR_EXIBE_EDICAO)
VALUES ('campo_csa_possui_ade_ativa', 'Possui consignações ativas', '/relatorios/campos_relatorio/campo_csa_possui_ade_ativa.jsp', 'N');

UPDATE tb_relatorio_filtro SET RFI_SEQUENCIA = RFI_SEQUENCIA + 1 WHERE REL_CODIGO = 'conf_cad_csa' AND RFI_SEQUENCIA > 2;

INSERT INTO tb_relatorio_filtro (REL_CODIGO, TFR_CODIGO, RFI_EXIBE_SUP, RFI_EXIBE_CSE, RFI_EXIBE_CSA, RFI_EXIBE_COR, RFI_EXIBE_ORG, RFI_EXIBE_SER, RFI_SEQUENCIA, RFI_PARAMETRO)
VALUES ('conf_cad_csa', 'campo_csa_possui_ade_ativa', '1', '0', '0', '0', '0', '0', 3, NULL); 
