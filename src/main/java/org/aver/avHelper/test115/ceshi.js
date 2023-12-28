$.ajax({
    type: "GET",
    url: "https://aps.115.com/natsort/files.php?aid=1&cid=2694413878885023973&o=file_name&asc=1&offset=0&show_dir=1&limit=115&code=&scid=&snap=0&natsort=1&record_open_time=1&count_folders=1&source=&format=json&fc_mix=1&type=&star=&is_share=&suffix=&custom_order=&is_q=",
    dataType: 'JSON',
    success: function(data, textStatus, request) {
        var headers = request.getAllResponseHeaders().toLowerCase();
        console.log(headers);
        //获取头部时间参数date
        var headerdate = request.getResponseHeader('date');
        console.log(headerdate);
    }
});

var req = new XMLHttpRequest();
req.open('https://home.115.com/api/1.0/web/1.0/topic/unread?tid=11925233', document.location, false);
req.send(null);
var headers = req.getAllResponseHeaders().toLowerCase();
console.log(headers);
var iwant = req.getResponseHeader('door');
console.log(iwant);

var xhr = new XMLHttpRequest();

xhr.onreadystatechange = function() {
    if(xhr.readyState === XMLHttpRequest.OPENED) {
        console.log(xhr.getAllRequestHeaders());
    }
}

xhr.open('GET', 'https://aps.115.com/natsort/files.php?aid=1&cid=2694413878885023973&o=file_name&asc=1&offset=0&show_dir=1&limit=115&code=&scid=&snap=0&natsort=1&record_open_time=1&count_folders=1&source=&format=json&fc_mix=1&type=&star=&is_share=&suffix=&custom_order=&is_q=');
xhr.send();