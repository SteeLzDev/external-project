/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     07/04/2020 11:10:43                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_ocorrencia_orgao                                   */
/*==============================================================*/
create table tb_ocorrencia_orgao
(
   OOR_CODIGO           varchar(32) not null,
   ORG_CODIGO           varchar(32) not null,
   TOC_CODIGO           varchar(32) not null,
   TMO_CODIGO           varchar(32),
   USU_CODIGO           varchar(32) not null,
   OOR_DATA             datetime not null,
   OOR_OBS              text not null,
   OOR_IP_ACESSO        varchar(45),
   primary key (OOR_CODIGO)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_ocorrencia_orgao add constraint FK_R_797 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_orgao add constraint FK_R_798 foreign key (ORG_CODIGO)
      references tb_orgao (ORG_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_orgao add constraint FK_R_799 foreign key (TOC_CODIGO)
      references tb_tipo_ocorrencia (TOC_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_orgao add constraint FK_R_800 foreign key (TMO_CODIGO)
      references tb_tipo_motivo_operacao (TMO_CODIGO) on delete restrict on update restrict;

