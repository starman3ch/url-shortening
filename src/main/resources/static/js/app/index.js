var main = {
    init : function () {
        var _this = this;
        $('#btn-get-shorturl').on('click', function () {
            _this.getShortUrl();
        });
    },
    getShortUrl : function () {
        var data = {
            url: $("#url-string").val()
        };

        $.ajax({
            type: 'POST',
            url: '/shortUrl',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            data: JSON.stringify(data)
        }).done(function (data) {
            var shortUrl = data.shortUrl;
            var template = "<label>SHORTEN URL : {{shortUrl}}</label>";
            var text = Mustache.render(template, data);
            $("#short-url").html(text);
        }).fail(function (error) {
            var template = "<label>" + error.responseJSON.message + "</label>";
            var text = Mustache.render(template, data);
            $("#short-url").html(text);
        });
    }
};

main.init();
