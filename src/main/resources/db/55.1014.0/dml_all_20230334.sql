-- DESENV-16740
INSERT INTO tb_tipo_ocorrencia (TOC_CODIGO, TOC_DESCRICAO)
VALUES ('218', 'Confirmação de liquidação de contrato');

INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA)
VALUES ('77', 'Habilita operação de liquidação em duas etapas para a consignatária', 'SN', 'N', 'N', 'N');

INSERT INTO tb_tipo_param_consignataria (TPA_CODIGO, TPA_DESCRICAO, TPA_DOMINIO, TPA_CSE_ALTERA, TPA_SUP_ALTERA, TPA_CSA_ALTERA)
VALUES ('78', 'Exige dupla confirmação de liquidação de contratos', 'SN', 'N', 'N', 'N');

