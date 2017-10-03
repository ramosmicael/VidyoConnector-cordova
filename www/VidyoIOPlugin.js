var exec = require('cordova/exec');

function plugin() {

}

plugin.prototype.launchVidyoIO = function(args) {
    exec(function(res){}, function(err){}, "VidyoIOPlugin", "launchVidyoIO", args);
}

module.exports = new plugin();
