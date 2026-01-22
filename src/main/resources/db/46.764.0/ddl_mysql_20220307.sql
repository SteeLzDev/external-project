/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     07/03/2022 11:48:20                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_ocorrencia_correspondente                          */
/*==============================================================*/
create table tb_ocorrencia_correspondente
(
   OCR_CODIGO           varchar(32) not null,
   USU_CODIGO           varchar(32) not null,
   COR_CODIGO           varchar(32) not null,
   TOC_CODIGO           varchar(32) not null,
   TMO_CODIGO           varchar(32),
   OCR_OBS              text not null,
   OCR_DATA             datetime not null,
   OCR_IP_ACESSO        varchar(45),
   primary key (OCR_CODIGO)
) ENGINE=InnoDB;

alter table tb_ocorrencia_correspondente add constraint FK_R_860 foreign key (COR_CODIGO)
      references tb_correspondente (COR_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_correspondente add constraint FK_R_861 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_correspondente add constraint FK_R_862 foreign key (TOC_CODIGO)
      references tb_tipo_ocorrencia (TOC_CODIGO) on delete restrict on update restrict;

alter table tb_ocorrencia_correspondente add constraint FK_R_863 foreign key (TMO_CODIGO)
      references tb_tipo_motivo_operacao (TMO_CODIGO) on delete restrict on update restrict;

