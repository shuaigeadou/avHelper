$(function() {
	var javBusSite;
	
	/* 帮助提示 */
	$(".popoverHelp").popover();

	/* 时间控件 */
	$('.datetimepicker').datetimepicker({
		format : 'yyyy-mm-dd',
		language : "zh-CN",
		autoclose : true,
		startView : 'year',
		minView : 'month',
		todayBtn : true
	});

	/* 下拉框 */
	$('ul.dropdown-menu li a').on('click',function() {
		var button = $(this).parents('.btn-group').find('button:eq(0)');
		button.text($(this).text());
		button.val($(this).parents('li').index());
	});

	/* 获取配置显示到页面 */
	$.ajax({
		type : 'POST',
		contentType : "application/json",
		url : "/ajax/getConfig",
		cache : false,
		timeout : 3000,
		async : false,
		success : function(config) {
			if($.trim(config.tempMediaDir) != ''){
				$('.tempDir').val(config.tempMediaDir);
			}
			if(config.serverMediaDir != null && config.serverMediaDir.length > 0){
				var parentDiv = $('#mediaDirs');
				var tempLate = $("#mediaDirsTemplate");
				$(config.serverMediaDir).each(function(index, ele){
					var temp = tempLate.clone(true).removeAttr("id").show();
					temp.find('.name').text(ele.name);
					temp.find('.dirOrUrl').val(ele.url);
					parentDiv.append(temp);
				});
			}
			configDeleteDir();
			
			if(config.categoryUrl != null && config.categoryUrl.length > 0){
				var parentDiv = $('#catogaryUrl');
				var tempLate = $("#catogaryUrlTepmlate");
				$(config.categoryUrl).each(function(index, ele){
					var temp = tempLate.clone(true).removeAttr("id").show();
					temp.find('.name').text(ele.name);
					temp.find('.dirOrUrl').val(ele.url);
					parentDiv.append(temp);
				});
			}
			
			$.setForm('#config', config);
			if(config.genreExclude != null && config.genreExclude.length > 0){
				var parentDiv = $('#excludeType');
				var tempLate = $("#h4Template");
				$(config.genreExclude).each(function(index, ele){
					var temp = tempLate.clone(true).removeAttr("id").show();
					temp.find('span').text(ele);
					parentDiv.append(temp);
				});
			}
			if(config.downloadFanart){
				$('.isDownloadArt button:eq(0)').val(0);
				$('.isDownloadArt button:eq(0)').text('是');
			}else{
				$('.isDownloadArt button:eq(0)').val(1);
				$('.isDownloadArt button:eq(0)').text('否');
			}
		},
		error : function(XMLHttpRequest, textStatus, errorThrown) {
			alert(XMLHttpRequest.responseJSON.message);
        }
	});
	$.get("/ajax/getJavBusSite",function(data,status){
		javBusSite = data;
	});
	
	/* 锁定编辑临时目录 */
	$('.lockTempDir').on('click', function() {
		if($('.tempDir').attr("disabled") == "disabled"){
			return;
		}
		
		if($.trim($('.tempDir').val()) == ''){
			alert('临时目录不能为空!');
			return;
		}
		
		$.ajax({
			type : 'POST',
			contentType : "application/json",
			url : "/ajax/lockTempDir",
			data : $.trim($('.tempDir').val()),
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				$('.tempDir').attr("disabled", "disabled");
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});
	$('.editTempDir').on('click', function() {
		$('.tempDir').removeAttr("disabled");
	});

	/* 添加目录或添加地址 */
	$('.addMediaDir,.addCategoryUrl').on('click',function() {
		var newName = $.trim($('.newName').val());
		var newUrl = $.trim($('.newUrl').val());
		if (newName == '' || newUrl == '') {
			alert("名称和路径都不能为空！");
			return;
		}
		var tempLate;
		var parentDiv;
		var url;
		if ($(this).attr("class").indexOf('addMediaDir') != -1) {
			tempLate = $("#mediaDirsTemplate").clone(true).removeAttr("id").show();
			parentDiv = $('#mediaDirs');
			url = '/ajax/addMedirDir';
		} else if ($(this).attr("class").indexOf('addCategoryUrl') != -1) {
			tempLate = $("#catogaryUrlTepmlate").clone(true).removeAttr("id").show();
			parentDiv = $('#catogaryUrl');
			url = '/ajax/addUrl';
		}
		
		//保存
		$.ajax({
			type : 'POST',
			contentType : "application/json",
			url : url,
			data : JSON.stringify({name : newName, url : newUrl}),
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				tempLate.find('.name').text(newName);
				tempLate.find('.dirOrUrl').val(newUrl);
				parentDiv.append(tempLate);
				$('.newName').val('');
				$('.newUrl').val('');
				configDeleteDir();
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});

	/* 删除目录 */
	$('.deleteDir').on('click', function(){
		var parent = $(this).parents('.clearfix');
		var dirName = parent.find('ul li a').text();
		$.ajax({
			type : 'POST',
			contentType : "text/plain",
			url : '/ajax/deleteMedirDir',
			data : dirName,
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				parent.remove();
				configDeleteDir();
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});
	
	/* 删除url */
	$('.deleteUrl').on('click', function(){
		var parent = $(this).parents('.clearfix');
		var urlName = parent.find('ul li a').text();
		$.ajax({
			type : 'POST',
			contentType : "text/plain",
			url : '/ajax/deleteUrl',
			data : urlName,
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				parent.remove();
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});
	
	/* 锁定编辑选项 */
	//初始锁定
	$('#config input').attr("disabled", "disabled");
	$('#config .isDownloadArt .dropdown-toggle').hide();
	$('.lockConfig').on('click', function() {
		if($('#config input:eq(0)').attr("disabled") == "disabled"){
			return;
		}
		
		var data = $("#config").serializeJSON();
		data.downloadFanart = $('.isDownloadArt button:eq(0)').val() == 1 ? false : true;
		//保存
		$.ajax({
			type : 'POST',
			contentType : "application/json",
			url : '/ajax/lockConfig',
			data : JSON.stringify(data),
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				$('#config input').attr("disabled", "disabled");
				$('#config .isDownloadArt .dropdown-toggle').hide();
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});
	$('.editConfig').on('click', function() {
		$('#config input').removeAttr("disabled");
		$('#config .isDownloadArt .dropdown-toggle').show();
	});

	/* 添加删除类别 */
	$('.addType').on('click', function() {
		if ($('.newType').attr('disabled') == 'disabled') {
			return;
		}
		var type = $.trim($('.newType').val());
		if (type == '') {
			alert('类别不能为空！');
			return;
		}
		
		//保存
		$.ajax({
			type : 'POST',
			contentType : "text/plain",
			url : '/ajax/addType',
			data : type,
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				var h4 = $('#h4Template').clone(true).removeAttr('id').show();
				h4.find('span').text(type);
				$("#excludeType").append(h4);
				$('.newType').val('');
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});
	$('.deleteType').on('click', function() {
		if ($('.newType').attr('disabled') == 'disabled') {
			return;
		}
		var ele = this;
		//保存
		$.ajax({
			type : 'POST',
			contentType : "text/plain",
			url : '/ajax/deleteType',
			data : $(ele).text(),
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				$(ele).parent().remove();
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});

	/** 设置下拉目录 */
	function configDeleteDir(){
		var selectTemplate = $('#selectTemplate li');
		var deleteDir1 = $('.deleteDir1 .dropdown-menu').empty();
		var deleteDir2 = $('.deleteDir2 .dropdown-menu').empty();
		var temp;
		$('#mediaDirs ul li a').each(function(index, ele){
			temp = selectTemplate.clone(true);
			temp.find('a').text($(ele).text());
			deleteDir1.append(temp.clone(true));
			deleteDir2.append(temp);
		});
	}
	
	/** 生成改名配置*/
	$('.generateConfig').on('click', function() {
		var usedRules;
		if($('.usedRules button:eq(0)').text() == '使用规则生成新文件名'){
			usedRules = 'true';
		}else{
			usedRules = 'false';
		}
		$.ajax({
			type : 'POST',
			contentType : "text/plain",
			url : '/ajax/generateConfig',
			data : usedRules,
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				addToTable(data, 'newName');
				alert('完成');
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});
	
	/*新文件名修改时同时修改网址*/
	$('.newFileName input').on('change', function(){
		var name = $(this).val();
		name = name.substr(0, name.lastIndexOf('.'));
		var aLink = $(this).parents('tr').find('.movieWebSite a');
		aLink.attr('href', javBusSite + name);
		aLink.text(javBusSite + name);
	});
	
	/*下载电影信息*/
	$('.getMovieMessage').on('click', function(){
		//synchronizeNewName();
		$.ajax({
			type : 'POST',
			contentType : "application/json",
			url : '/ajax/getMovieMessage',
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				addToTable(data, 'title');
				alert('完成');
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});
	
	/*填充表格，根据指定字段判断状态是否成功*/
	function addToTable(data, field){
		var tbody = $('#movieList tbody:eq(0)').empty();
		addToTable2(data, field);
	}
	function addToTable2(data, field){
		var tbody = $('#movieList tbody:eq(0)');
		var movieTemp = $('#movieTemp');
		$(data).each(function(index, ele){
			var temp = movieTemp.clone(true).removeAttr('id').show();
			temp.find('.no').text(index + 1);
			temp.find('.oriFileName').text(ele.originalName);
			temp.find('.newFileName input').val(ele.newName);
			temp.find('.movieWebSite').append(
				$('<a>').attr('href', ele.webSite).attr('target','_blank')
					.text(ele.webSite));
			if(ele[field] == null || ele[field] == ''){
				temp.addClass('danger');
				temp.find('.msgStatus').text('失败');
			}else{
				temp.addClass('success');
				temp.find('.msgStatus').text('成功');
			}
			tbody.append(temp);
			
			if(ele.childrenMovies != null && ele.childrenMovies.length > 0){
				addToTable2(ele.childrenMovies, 'newName');
			}
		});
	}
	
	
	/*修改文件名*/
	$('.reName').on('click', function(){
		//synchronizeNewName();
		$.ajax({
			type : 'POST',
			contentType : "application/json",
			url : '/ajax/reName',
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				alert('完成');
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});
	
	/*将前台新文件名同步到后台*/
	function synchronizeNewName(){
		var movieArray = [];
		var movie;
		$('#movieList tbody:eq(0) tr').each(function(index, ele){
			movie = {};
			movie.originalName = $.trim($(ele).find('.oriFileName').text());
			movie.newName = $.trim($(ele).find('.newFileName input').val());
			movieArray.push(movie);
		});
		$.ajax({
			type : 'POST',
			contentType : "application/json",
			url : '/ajax/synchronizeNewName',
			data : JSON.stringify(movieArray),
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	}
	
	/*移动电影到本目录*/
	$('.moveToThis').on('click', function(){
		var dirName = $(this).parents('.clearfix').find('ul.nav-pills a').text();
		$.ajax({
			type : 'POST',
			contentType : "text/plain",
			url : '/ajax/moveToThis',
			data : dirName,
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				alert('完成');
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});
	
	/*移动到临时目录*/
	$('.moveToTemp').on('click', function(){
		var dirName = $(this).parents('.clearfix').find('ul.nav-pills a').text();
		$.ajax({
			type : 'POST',
			contentType : "text/plain",
			url : '/ajax/moveToTemp',
			data : dirName,
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				alert('完成');
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});
	
	/*删除目录*/
	$('.compareDir').on('click', function(){
		var parent = $(this).parents('.form-group');
		var deleteDirName = parent.find('.deleteDir1 button:eq(0)').text();
		var retainDirName = parent.find('.deleteDir2 button:eq(0)').text();
		
		$.ajax({
			type : 'POST',
			contentType : "application/x-www-form-urlencoded",
			url : '/ajax/compareDir',
			data : 'deleteDirName='+deleteDirName+'&retainDirName='+retainDirName,
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				alert('完成');
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});
	
	/*生成伪装视频到临时目录*/
	$('.generatePseudoVideo').on('click', function(){
		var urlName = $(this).parents('.form-group').find('ul.nav-pills a').text();
		
		$.ajax({
			type : 'POST',
			contentType : "text/plain",
			url : '/ajax/generatePseudoVideo',
			data : urlName,
			cache : false,
			timeout : 3000,
			async : false,
			success : function(data) {
				alert('完成');
			},
			error : function(XMLHttpRequest, textStatus, errorThrown) {
				alert(XMLHttpRequest.responseJSON.message);
            }
		});
	});
	
});
