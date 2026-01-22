function openNav(id) {
    document.getElementById("mySidenav-" + id).style.width = "350px";
    document.getElementById("mySidenav-" + id).style.visibility = "visible";
    document.getElementById("main").style.marginLeft = "350px";
}

function closeNav(id) {
    document.getElementById("mySidenav-" + id).style.width = "0";
    document.getElementById("mySidenav-" + id).style.visibility = "hidden";
    document.getElementById("main").style.marginLeft= "50px";
}

function favNav(id) {
    $.post("../v3/favoritarMenu", "itmCodigo=" + id, function(data) {
        try {
            var trimData = $.trim(JSON.stringify(data));
            var obj = JSON.parse(trimData);
            if (obj.success == 'true') {
                $.post("../v3/favoritarMenu", "acao=recarregarMenu", function(data) {
                	$("div#container").replaceWith(data);
                }, "html");
            }
        } catch(err) {
        }
    }, "json");

    
    return false;
}

function favNavV4(id) {
    $.post("../v3/favoritarMenu", "itmCodigo=" + id, function(data) {
        try {
            var trimData = $.trim(JSON.stringify(data));
            var obj = JSON.parse(trimData);
            if (obj.success == 'true') {
                $.post("../v3/favoritarMenu", "acao=recarregarMenu", function(data) {
                	$("div#container").replaceWith(data);
                    $.post("../v3/favoritarMenu", "acao=recarregarDashboard", function(data) {
                    	$("div#containerFavoritos").replaceWith(data);
                    }, "html");
                }, "html");
            }
        } catch(err) {
        }
    }, "json");

    
    return false;
}

function sortMenu(id) {
    $.fn.sortList = function() {
        var mylist = $(this);
        var listitems = $('div', mylist).get();
        listitems.sort(function(a, b) {
            var compA = accentFold($(a).find("a.link-menu").text().toLowerCase());
            var compB = accentFold($(b).find("a.link-menu").text().toLowerCase());
            return (compA < compB) ? -1 : 1;
        });
        $.each(listitems, function(i, itm) {
            mylist.append(itm);
        });
    }
    $("div#links-" + id).sortList();
}

function sortMenuv4(id) {
	$.fn.sortList = function() {
        var mylist = $(this);
        var listitems = $('li', mylist).get();
        listitems.sort(function(a, b) {
            var compA = accentFold($(a).find("a.link-menu").text().toLowerCase());
            var compB = accentFold($(b).find("a.link-menu").text().toLowerCase());
            return (compA < compB) ? -1 : 1;
        });
        $.each(listitems, function(i, itm) {
            mylist.append(itm);
        });
    }
    $("div#" + id + " .submenu").sortList();
}

function accentFold(inStr) {
    return inStr.normalize('NFD').replace(/\p{Diacritic}/gu, '');
}
