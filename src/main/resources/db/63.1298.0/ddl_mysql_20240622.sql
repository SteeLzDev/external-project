/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     17/06/2024 09:45:59                          */
/*==============================================================*/

alter table tb_controle_processamento_lote
   add CPL_CANAL char(1) not null default '2'
;

alter table tb_controle_processamento_lote
   add CPL_DATA datetime
;

alter table tb_controle_processamento_lote
   add CPL_ARQUIVO_CRITICA varchar(255)
;

alter table tb_controle_processamento_lote
   add USU_CODIGO varchar(32)
;

alter table tb_controle_processamento_lote add constraint FK_R_965 foreign key (USU_CODIGO)
      references tb_usuario (USU_CODIGO) on delete restrict on update restrict
;


/*==============================================================*/
/* Table: tb_bloco_processamento_lote                           */
/*==============================================================*/
create table tb_bloco_processamento_lote
(
   CPL_ARQUIVO_ECONSIG  varchar(255) not null,
   BPL_NUM_LINHA        int not null,
   CSA_CODIGO           varchar(32) not null,
   SBP_CODIGO           varchar(32) not null,
   BPL_PERIODO          date not null,
   BPL_DATA_INCLUSAO    datetime not null,
   BPL_DATA_PROCESSAMENTO datetime,
   BPL_LINHA            text not null,
   BPL_CAMPOS           text not null,
   BPL_CRITICA          text,
   primary key (CPL_ARQUIVO_ECONSIG, BPL_NUM_LINHA)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

alter table tb_bloco_processamento_lote add constraint FK_R_966 foreign key (CPL_ARQUIVO_ECONSIG)
      references tb_controle_processamento_lote (CPL_ARQUIVO_ECONSIG) on delete restrict on update restrict;

alter table tb_bloco_processamento_lote add constraint FK_R_967 foreign key (SBP_CODIGO)
      references tb_status_bloco_processamento (SBP_CODIGO) on delete restrict on update restrict;

alter table tb_bloco_processamento_lote add constraint FK_R_968 foreign key (CSA_CODIGO)
      references tb_consignataria (CSA_CODIGO) on delete restrict on update restrict;

