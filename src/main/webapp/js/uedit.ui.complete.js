"use strict";
var UeditUserInterfaceComplete = (function(){
    this.ueditor = null;
    var buttonStrip = null;
    
    var buttonTooltips = {
        align_left: mensagem('rotulo.markdown.alinhamento.esq'),
        align_right: mensagem('rotulo.markdown.alinhamento.dir'),
        align_center: mensagem('rotulo.markdown.alinhamento.centro'),
       // align_justified: "Align text evenly",
        bold: mensagem('rotulo.markdown.negrito'),
        italic: mensagem('rotulo.markdown.italico'),
        deleted: mensagem('rotulo.markdown.sobrescrito'),
        ulist: mensagem('rotulo.markdown.lista'),
        numlist: mensagem('rotulo.markdown.lista.numerada'),
        colorRed: mensagem('rotulo.markdown.fonte.vermelha'),
        colorYellow: mensagem('rotulo.markdown.fonte.amarela'),
        colorGreen: mensagem('rotulo.markdown.fonte.verde'),
        colorBlue: mensagem('rotulo.markdown.fonte.azul'),
        estiloTitulo: mensagem('rotulo.markdown.estilo.titulo'),
        estiloSubtitulo: mensagem('rotulo.markdown.estilo.subtitulo'),
        estiloTexto: mensagem('rotulo.markdown.estilo.texto'),

        //code: "Insert/format code",
        //link: "Link",
        video: "Insert video",
        image: mensagem('rotulo.markdown.imagem'),
        undo: mensagem('rotulo.markdown.desfazer'),
        redo: mensagem('rotulo.markdown.refazer'),
        help: mensagem('rotulo.markdown.ajuda')
    };

    var enabledButtons = [
        'align_left', 'align_right', 'align_center', 'bold', 'italic', 'deleted', 'help',
        'ulist', 'numlist', 'colorRed', 'colorYellow', 'colorGreen', 'colorBlue', 'image', 'video', 'undo', 'redo',
        'estiloTitulo', 'estiloSubtitulo', 'estiloTexto'
    ];
    
    function log(record){
        var now = new Date();
        
        if (typeof UEDIT_DEBUG === "undefined") {
            return;
        }

        typeof console !== "undefined" && console.log(
            "uedit_complete -> [" + now.getHours() + ":" 
                + now.getMinutes() + ":" 
                + now.getSeconds() + "] " 
            + record
        );
    }
    
    function addClass(element, _class){
        if (!element.className) {
            element.className = _class;
        }
        
        var setClass = element.className + " ";
        
        if (element.className.indexOf(_class) < 0) {
            setClass += _class;
        }
        
        element.className = setClass;
    }
    
    function removeClass(element, _class){
        if (!element.className) {
            return;
        } 
        
        element.className = element.className.replace(new RegExp('\\b'+ _class +'\\b'),'');
    }
    
    this.setRedoState = function(state){
        log('Setting redo button state to ' + state);
        
        var redoButton = this.controls.button_redo;
        
        if (state){
            removeClass(redoButton, 'uedit_complete_button_redo_light');
        } else {
            addClass(redoButton, 'uedit_complete_button_redo_light');
        }
    }
    
    this.setUndoState = function(state){
        log('Setting undo button state to ' + state);
        
        var undoButton = this.controls.button_undo;
        
        if (state){
            removeClass(undoButton, 'uedit_complete_button_undo_light');
        } else {
            addClass(undoButton, 'uedit_complete_button_undo_light');
        }
    }
    
    this.init = function(textarea, buttonStrip){
        this.ueditor = ueditCreate(textarea);
        this.ueditor.setRedoStateCallback = this.setRedoState.bind(this);
        this.ueditor.setUndoStateCallback = this.setUndoState.bind(this); 
        
        buttonStrip.className = 'uedit_complete_button_strip';
        
        this.controls = new Object();

        for (var i = 0; i < enabledButtons.length; i++){
            var button = document.createElement('li');
            button.onmousedown = this.ueditor.cacheSelection;
            button.className = 'uedit_complete_button uedit_complete_button_' + enabledButtons[i];
            
            var buttonLink = document.createElement('a');
            buttonLink.title = buttonTooltips[enabledButtons[i]];
            
            buttonLink.onclick = this.ueditor['button_'+enabledButtons[i]].bind(this.ueditor);
            
            button.appendChild(buttonLink);
            buttonStrip.appendChild(button);
            
            this.controls['button_' + enabledButtons[i]] = button;
        }
        
        this.controls.button_undo && addClass(this.controls.button_undo, 'uedit_complete_button_light');
        this.controls.button_redo && addClass(this.controls.button_redo, 'uedit_complete_button_light');
        
        this.buttonStrip = buttonStrip;
    }
    
});

var ueditInterface = function(textarea, buttonStrip){
    var editorInterface = new Object();
    UeditUserInterfaceComplete.apply(editorInterface);
    
    editorInterface.init(textarea, buttonStrip);
    return editorInterface;
}
