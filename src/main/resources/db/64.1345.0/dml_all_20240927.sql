-- DESENV-21758
INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO) 
VALUES ('240', 'Renegociação mantendo a data de encerramento igual à data de inclusão da nova consignação');

INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA)
VALUES ('99', 'Alterar por padrão a data de encerramento do contrato anterior para a data do início do novo contrato na renegociação', 'SN', 'N', 'N', 'N');

