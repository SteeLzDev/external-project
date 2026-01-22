-- DESENV-16131
INSERT INTO tb_status_registro_servidor (SRS_CODIGO, SRS_DESCRICAO) VALUES ('7', 'Bloqueado Automaticamente por Segurança');

INSERT INTO tb_tipo_motivo_bloqueio (TMB_CODIGO, TMB_DESCRICAO) VALUES ('8', 'Data de expiração vencida');
UPDATE tb_consignataria SET TMB_CODIGO = '8' WHERE TMB_CODIGO = '7';

UPDATE tb_tipo_motivo_bloqueio SET TMB_DESCRICAO = 'Bloqueio automático de segurança' WHERE TMB_CODIGO = '7';
UPDATE tb_consignataria SET TMB_CODIGO = '7' WHERE CSA_ATIVO = '7';
