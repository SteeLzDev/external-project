/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     13/01/2021 10:01:55                          */
/*==============================================================*/

INSERT INTO tb_perfil (PER_CODIGO, PAP_CODIGO, PER_DESCRICAO, PER_VISIVEL)
SELECT 'PERFIL-SERVIDOR', '6', UPPER(TEX_TEXTO), 'S'
FROM tb_texto_sistema
WHERE TEX_CHAVE = 'rotulo.servidor.singular'
;

INSERT INTO tb_funcao_perfil (PER_CODIGO, FUN_CODIGO)
SELECT 'PERFIL-SERVIDOR', FUN_CODIGO
FROM tb_funcao_perfil_ser
GROUP BY FUN_CODIGO;

INSERT INTO tb_perfil_usuario (PER_CODIGO, USU_CODIGO, UPE_ATIVO)
SELECT DISTINCT 'PERFIL-SERVIDOR', USU_CODIGO, 1
FROM tb_usuario_ser
;

drop table if exists tb_funcao_perfil_ser;
