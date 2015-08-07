/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(window).load(function () {
    // Animate loader off screen
    $(".se-pre-con").fadeOut("slow");
    ;
});

function wait() {
    var elem = document.getElementById("loader");
    elem.style.visibility = "visible";
    var elem2 = document.getElementById("form");
    elem2.style.opacity = "0.6";
    elem2.style.filter = 'alpha(opacity=60)';
}    