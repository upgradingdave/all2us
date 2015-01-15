
// refresh recently shortened url list
var recentUrls = function() {
  $.ajax({
    type: "GET",
    url: "/urls",
    success: function(data) {
      $('#recent-url-list').html('');
      for(var i=0; i<data.length; i++) {
        var url = data[i]
        var shorturl = "http://all2.us/" + url.short_url;
        
        var diff = url.url.length - shorturl.length;
        var li = "<li>";
        if(diff > 0)
          li += "(Shorter by "+ diff +" letters!) ";
        li += url.url + " --> " + "<a href=\"/"+ url.short_url + "\">" + 
          shorturl + "</a></li>";
        
        $(li).hide().appendTo('#recent-url-list').fadeIn("fast");
      };
    }
  });
};

// post form data to create a new short url
var createUrl = function(){
      $.ajax({
      type: "POST",
      url: "/urls",
      data: $("#url-form").serialize(),
      success: function(data) {
        $('#url-form input').val('');
        $('#url-label').html("Url to Shorten")
          .removeClass("text-danger");
        recentUrls();
      }, 
      error: function(xhr, status, error) {
        $('#url-label').html(xhr.responseJSON.errors[0])
          .addClass("text-danger");
      }
    });
}

$(document).ready(function(){

  $("#url-form").submit(function(e) {
    e.preventDefault();
    createUrl();
  });

});
