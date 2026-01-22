/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     13/06/2025 11:45:00                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_destinatario_email_csa_svc                         */
/*==============================================================*/
CREATE TABLE `tb_destinatario_email_csa_svc` (
  `CSA_CODIGO` varchar(32) NOT NULL,
  `SVC_CODIGO` varchar(32) NOT NULL,
  PRIMARY KEY (`CSA_CODIGO`,`SVC_CODIGO`),
  KEY `IDX_CSA_DCS_1` (`CSA_CODIGO`),
  KEY `IDX_SVC_DCS_2` (`SVC_CODIGO`),
  CONSTRAINT `FK_CSA_DCS_1` FOREIGN KEY (`CSA_CODIGO`) REFERENCES `tb_consignataria` (`csa_codigo`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `FK_SVC_DCS_2` FOREIGN KEY (`SVC_CODIGO`) REFERENCES `tb_servico` (`svc_codigo`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_termo_adesao
   add TAD_CLASSE_ACAO varchar(100);

alter table tb_termo_adesao
   add TAD_EXIBE_APOS_LEITURA char(1) null default 'N';

alter table tb_termo_adesao
   add TAD_ENVIA_API_CONSENTIMENTO char(1) null default 'N';

alter table tb_coeficiente
  add CFT_VLR_MINIMO decimal(13,8) null;

alter table tb_coeficiente_ativo
  add CFT_VLR_MINIMO decimal(13,8) null;

