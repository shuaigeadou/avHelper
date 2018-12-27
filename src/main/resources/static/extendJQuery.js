//将josn对象赋值给form
//网址https://www.cnblogs.com/LearningC/p/8118639.html
$.extend({
    setForm :function(frm,jsonValue) {   
    var obj=$(frm);  
    $.each(jsonValue, function (name, ival) {  
        var $oinput = obj.find("input[name=" + name + "]");   
        if ($oinput.attr("type")== "radio" || $oinput.attr("type")== "checkbox"){  
             $oinput.each(function(){  
                 if(Object.prototype.toString.apply(ival) == '[object Array]'){// 是复选框，并且是数组
                      for(var i=0;i<ival.length;i++){  
                          if($(this).val()==ival[i])  
                             $(this).attr("checked", "checked");  
                      }  
                 }else{  
                     if($(this).val()==ival)  
                        $(this).attr("checked", "checked");  
                 }  
             });  
        }else if($oinput.attr("type")== "textarea"){// 多行文本框
            obj.find("[name="+name+"]").html(ival);  
        }else{  
             obj.find("[name="+name+"]").val(ival);   
        }  
    });
},
getFormJson:function(frm) {
        var o = {};
        var a = $(frm).serializeArray();

        $.each(a, function() {
            if (this.name == "password") {
                //this.value = $.md5(this.value) //md5操作 
　　　　　　　　　　this.value=this.value;
            }
            if (o[this.name] !== undefined) {
                if (!o[this.name].push) {
                    o[this.name] = [ o[this.name] ];
                }
                o[this.name].push(this.value || '');
            } else {
                o[this.name] = this.value || '';
            }
        });
        return o;
}
});