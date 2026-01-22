/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     07/01/2021 13:21:32                          */
/*==============================================================*/

CREATE TEMPORARY TABLE tb_arquivo_movimento_validacao_bkp 
  SELECT * FROM tb_arquivo_movimento_validacao;

DROP TABLE tb_arquivo_movimento_validacao;
CREATE TABLE tb_arquivo_movimento_validacao (
  amv_operacao varchar(1) NOT NULL,
  org_identificador varchar(40) DEFAULT NULL,
  est_identificador varchar(40) DEFAULT NULL,
  csa_identificador varchar(40) DEFAULT NULL,
  svc_identificador varchar(40) DEFAULT NULL,
  cnv_cod_verba varchar(32) DEFAULT NULL,
  cnv_codigo varchar(32) DEFAULT NULL,
  ser_nome varchar(255) DEFAULT NULL,
  ser_cpf varchar(19) DEFAULT NULL,
  rse_matricula varchar(20) DEFAULT NULL,
  rse_matricula_inst varchar(20) DEFAULT NULL,
  rse_codigo varchar(32) DEFAULT NULL,
  amv_periodo date DEFAULT NULL,
  amv_competencia date DEFAULT NULL,
  amv_data date DEFAULT NULL,
  pex_periodo date DEFAULT NULL,
  pex_periodo_ant date DEFAULT NULL,
  ade_indice varchar(32) DEFAULT NULL,
  ade_numero bigint unsigned DEFAULT NULL,
  ade_prazo int DEFAULT NULL,
  ade_vlr decimal(13,2) DEFAULT NULL,
  ade_tipo_vlr char(1) DEFAULT NULL,
  ade_vlr_folha decimal(13,2) DEFAULT NULL,
  ade_data datetime DEFAULT NULL,
  ade_data_ref datetime DEFAULT NULL,
  ade_ano_mes_ini date DEFAULT NULL,
  ade_ano_mes_fim date DEFAULT NULL,
  ade_ano_mes_ini_folha date DEFAULT NULL,
  ade_ano_mes_fim_folha date DEFAULT NULL,
  ade_ano_mes_ini_ref date DEFAULT NULL,
  ade_ano_mes_fim_ref date DEFAULT NULL,
  ade_cod_reg char(1) DEFAULT NULL,
  KEY ix01 (rse_matricula,cnv_cod_verba),
  KEY ix02 (cnv_codigo),
  KEY IX03 (rse_codigo)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO tb_arquivo_movimento_validacao
  SELECT * FROM tb_arquivo_movimento_validacao_bkp;
