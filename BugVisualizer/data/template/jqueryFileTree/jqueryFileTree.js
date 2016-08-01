// jQuery File Tree Plugin
//
// Version 1.01
//
// Cory S.N. LaViska
// A Beautiful Site (http://abeautifulsite.net/)
// 24 March 2008
//
// Visit http://abeautifulsite.net/notebook.php?article=58 for more information
//
// Usage: $('.fileTreeDemo').fileTree( options, callback )
//
// Options:  root           - root folder to display; default = /
//           script         - location of the serverside AJAX file to use; default = jqueryFileTree.php
//           folderEvent    - event to trigger expand/collapse; default = click
//           expandSpeed    - default = 500 (ms); use -1 for no animation
//           collapseSpeed  - default = 500 (ms); use -1 for no animation
//           expandEasing   - easing function to use on expand (optional)
//           collapseEasing - easing function to use on collapse (optional)
//           multiFolder    - whether or not to limit the browser to one subfolder at a time
//           loadMessage    - Message to display while initial tree loads (can be HTML)
//
// History:
//
// 1.01 - updated to work with foreign characters in directory/file names (12 April 2008)
// 1.00 - released (24 March 2008)
//
// TERMS OF USE
// 
// This plugin is dual-licensed under the GNU General Public License and the MIT License and
// is copyright 2008 A Beautiful Site, LLC. 
//

function isSubDirectory(str1, str2){
	var index = str1.indexOf(str2);
	if(index != 0) {return false;}
	var sub = str1.slice(str2.length);
	if (sub.length == 0) return false; 
	if(sub.indexOf('/') < 0 || sub.indexOf('/') == sub.length - 1){
		return true;
	}
	return false;
}

function isFile(x){
	if (x[x.length - 1] != '/'){return true;}
	return false;
}

function getExtStr(x){
	return x.slice(x.lastIndexOf('.') + 1);
}

function getName(x){
	if(isFile(x)){
		return x.slice(x.lastIndexOf('/') + 1);
	}
	else{
		var temp = x.slice(0, x.length - 1);
		if(temp.indexOf('/') < 0){
			return temp;
		}
		else{
			return temp.slice(temp.lastIndexOf('/') + 1);
		}
	}
}

function addIntoDirectory(str1, str2){
	var seghead = str2.slice(0, `<ul class="jqueryFileTree" style="display: none;">`.length);
	var segtail = str2.slice(`<ul class="jqueryFileTree" style="display: none;">`.length);
	var body;
	if(isFile(str1)){
		body = `<li class="file ext_` + getExtStr(str1) + `"><a href="#" rel="` +
		str1 + `">` + getName(str1) + `</a></li>`;
	}else{
		body = `<li class="directory collapsed"><a href="#" rel="` + str1 +
		`">` + getName(str1) + `</a></li>`;
	}
	return seghead + body + segtail;
}

var dic = new Array();
for (var i = 0 ; i < protoDic.length ; ++i){
	for(var j = 0 ; j < protoDic.length ; ++j){
		if(isSubDirectory(protoDic[i], protoDic[j])){
			if(dic[protoDic[j]] == undefined){
				dic[protoDic[j]] = `<ul class="jqueryFileTree" style="display: none;"></ul>`;
			}
			dic[protoDic[j]] = addIntoDirectory(protoDic[i], dic[protoDic[j]]);
		}
	}
}


if(jQuery) (function($){
	$.extend($.fn, {
		fileTree: function(o, h) {
			// Defaults
			if( !o ) var o = {};
			if( o.root == undefined ) o.root = '/';
			if( o.folderEvent == undefined ) o.folderEvent = 'click';
			if( o.expandSpeed == undefined ) o.expandSpeed= 500;
			if( o.collapseSpeed == undefined ) o.collapseSpeed= 500;
			if( o.expandEasing == undefined ) o.expandEasing = null;
			if( o.collapseEasing == undefined ) o.collapseEasing = null;
			if( o.multiFolder == undefined ) o.multiFolder = true;
			if( o.loadMessage == undefined ) o.loadMessage = 'Loading...';
			
			$(this).each( function() {
				
				function showTree(c, t) {
					$(c).addClass('wait');
					$(".jqueryFileTree.start").remove();
					$(c).find('.start').html('');
					$(c).removeClass('wait').append(dic[t]);
					if( o.root == t ) $(c).find('UL:hidden').show(); else $(c).find('UL:hidden').slideDown({ duration: o.expandSpeed, easing: o.expandEasing });
					bindTree(c);
				}
				
				function bindTree(t) {
					$(t).find('LI A').bind(o.folderEvent, function() {
						if( $(this).parent().hasClass('directory') ) {
							if( $(this).parent().hasClass('collapsed') ) {
								// Expand
								if( !o.multiFolder ) {
									$(this).parent().parent().find('UL').slideUp({ duration: o.collapseSpeed, easing: o.collapseEasing });
									$(this).parent().parent().find('LI.directory').removeClass('expanded').addClass('collapsed');
								}
								$(this).parent().find('UL').remove(); // cleanup
								showTree( $(this).parent(), $(this).attr('rel').match( /.*\// ) );
								$(this).parent().removeClass('collapsed').addClass('expanded');
							} else {
								// Collapse
								$(this).parent().find('UL').slideUp({ duration: o.collapseSpeed, easing: o.collapseEasing });
								$(this).parent().removeClass('expanded').addClass('collapsed');
							}
						} else {
							h($(this).attr('rel'));
						}
						return false;
					});
					// Prevent A from triggering the # on non-click events
					if( o.folderEvent.toLowerCase != 'click' ) $(t).find('LI A').bind('click', function() { return false; });
				}
				// Loading message
				$(this).html('<ul class="jqueryFileTree start"><li class="wait">' + o.loadMessage + '<li></ul>');
				// Get the initial file list
				showTree( $(this), o.root );
			});
		}
	});
	
})(jQuery);