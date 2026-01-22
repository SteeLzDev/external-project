/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     26/10/2018 15:32:34                          */
/*==============================================================*/


/*==============================================================*/
/* Table: tb_faturamento_beneficio                              */
/*==============================================================*/
create table tb_faturamento_beneficio
(
   FAT_CODIGO           varchar(32) not null,
   CSA_CODIGO           varchar(32) not null,
   FAT_PERIODO          date not null,
   FAT_DATA             datetime not null,
   primary key (FAT_CODIGO)
) ENGINE=InnoDB;

alter table tb_faturamento_beneficio add constraint FK_R_729 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

